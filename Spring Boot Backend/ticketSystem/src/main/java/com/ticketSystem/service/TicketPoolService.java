package com.ticketSystem.service;

import com.ticketSystem.model.Event;
import com.ticketSystem.model.SystemConfiguration;
import com.ticketSystem.model.Transaction;
import com.ticketSystem.repository.EventRepository;
import com.ticketSystem.repository.SystemConfigurationRepository;
import com.ticketSystem.repository.TransactionRepository;
import com.ticketSystem.model.Ticket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Service responsible for managing ticket sales and distribution in a concurrent ticket system.
 * This service handles the complex interactions between ticket vendors and customers,
 * ensuring thread-safe ticket addition, purchase, and tracking. It supports multiple
 * events and provides real-time updates via WebSocket messaging.
 * Key Features:
 * - Concurrent ticket addition by multiple vendors
 * - Thread-safe ticket purchasing
 * - WebSocket updates for ticket system events
 * - Configurable release and retrieval rates
 * - Logging of system activities
 */
@Service
public class TicketPoolService {

    // State Management Flags
    private volatile boolean running = false;
    private volatile boolean stopped = false;
    private boolean allTicketsSold = false;

    // State Counters
    private int ticketsAdded = 0;
    private int ticketsSold = 0;
    private int currentTickets = 0;

    // Thread-safe collections for managing ticket pool and queues
    private final List<Ticket> ticketPool = Collections.synchronizedList(new ArrayList<>());
    private final ReentrantLock lock = new ReentrantLock();
    private Semaphore ticketsAvailable = new Semaphore(0);
    private final BlockingQueue<Integer> customerQueue = new LinkedBlockingQueue<>();
    private final List<Thread> vendorThreads = new ArrayList<>();
    private final List<Thread> customerThreads = new ArrayList<>();

    // Autowired dependencies for database interactions and messaging
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SystemConfigurationRepository configurationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private LogService logService;

    /**
     * Starts a vendor thread for adding tickets to the ticket pool asynchronously.
     * This method runs continuously, adding tickets to the pool at a configurable rate.
     * It respects the total ticket limit and handles thread interruption gracefully.
     * @param vendorId Unique identifier for the vendor
     * @param eventName Name of the event for which tickets are being added
     * @param config System configuration defining ticket release parameters
     */
    @Async
    public void startVendorThread(int vendorId, String eventName, SystemConfiguration config) {

        logService.logSystemStatus(String.format("Starting vendor thread for vendor %d with event %s", vendorId, eventName));
        Random random = new Random();
        while (!Thread.currentThread().isInterrupted()) {
            try {

                // Simulate dynamic ticket addition rate defined in the system configuration
                int ticketsToAdd = random.nextInt(1, config.getReleaseRate()) + 1;
                Optional<Event> event = eventRepository.findByEventName(eventName);

                if (event.isPresent()) {
                    addTickets(eventName, vendorId, ticketsToAdd, event.get().getEventPrice(), config);
                    Thread.sleep(40000 / config.getReleaseRate()); // Adjust timing to match the system configuration
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logService.logVendorThreadInterruption(vendorId);
            }
        }
    }
    private void logCurrentTicketsInPool() {
        logService.logSystemStatus(String.format("Current tickets in pool: %d", currentTickets));
    }

    /**
     * Adds tickets to the ticket pool with thread-safe synchronization.
     * Handles ticket pool additions, respecting total ticket limits, and
     * sends real-time updates via WebSocket. Logs ticket addition details.
     * @param eventName Name of the event for the tickets
     * @param vendorId ID of the vendor adding tickets
     * @param ticketsToAdd Number of tickets to be added
     * @param price Price of each ticket
     * @param config System configuration for ticket management
     */
    private void addTickets(String eventName, int vendorId, int ticketsToAdd, double price, SystemConfiguration config) {
        lock.lock(); // Acquire lock to ensure thread safety
        try {

            // Check if tickets pool has reached the maximum allowed number
            if (ticketsAdded >= config.getTotalTickets()) {
                logService.logSystemStatus(String.format("Total tickets reached. Vendor %d cannot add more tickets.", vendorId));
                Thread.currentThread().interrupt();
                return;
            }

            int remainingCapacity = config.getTotalTickets() - ticketsAdded;
            ticketsToAdd = Math.min(ticketsToAdd, remainingCapacity);

            if (ticketsToAdd > 0) {
                for (int i = 0; i < ticketsToAdd; i++) {
                    Ticket ticket = new Ticket(eventName, price, vendorId);
                    ticketPool.add(ticket);
                    ticketsAvailable.release(); // Signal ticket availability
                }

                ticketsAdded += ticketsToAdd;
                currentTickets += ticketsToAdd;

                // Notify clients about ticket updates using WebSocket
                messagingTemplate.convertAndSend("/topic/ticket-updates",
                        Map.of("action", "add", "vendor", vendorId, "tickets", ticketsToAdd, "event", eventName)
                );

                logService.logVendorTicketAddition(vendorId, eventName, ticketsToAdd, price, currentTickets);
                // Log current tickets in pool
                logCurrentTicketsInPool();
            }
        } finally {
            lock.unlock();  // Always release the lock
        }
    }

    /**
     * Handles ticket purchases for customers in a thread-safe manner.
     * Manages ticket retrieval from the pool, transaction creation, and
     * system state updates. Supports partial purchases if requested tickets
     * exceed available tickets. Stops ticket handling when all tickets are sold.
     * @param customerId Unique identifier for the customer
     * @param config System configuration defining ticket retrieval parameters
     */
    @Transactional
    public void purchaseTickets(int customerId, SystemConfiguration config) {
        try {
            customerQueue.put(customerId);

            if (stopped) {
                logService.logSystemStatus(String.format("System is stopped. Customer %d cannot purchase tickets.", customerId));
                return;
            }

            ticketsAvailable.acquire();  // Wait until tickets are available
            lock.lock();
            try {
                if (stopped || ticketPool.isEmpty()) {
                    ticketsAvailable.release();
                    return;
                }

                Random random = new Random();
                int ticketsToBuy = random.nextInt(1, config.getRetrievalRate()) + 1;
                int availableTickets = ticketPool.size();

                if (ticketsToBuy > availableTickets) {
                    logService.logSystemStatus(String.format("Customer %d requested %d tickets, but only %d available. Adjusting purchase.",
                            customerId, ticketsToBuy, availableTickets));
                    ticketsToBuy = availableTickets;
                }

                double totalPrice = 0.0;
                List<String> eventNames = new ArrayList<>();
                List<Transaction> transactions = new ArrayList<>();

                for (int i = 0; i < ticketsToBuy; i++) {
                    Ticket ticket = ticketPool.remove(0);
                    ticketsSold++;
                    currentTickets--;
                    totalPrice += ticket.getPrice();
                    eventNames.add(ticket.getEventName());

                    Transaction transaction = new Transaction();
                    transaction.setEventName(ticket.getEventName());
                    transaction.setTicketPrice(ticket.getPrice());
                    transaction.setVendorId(ticket.getVendorId());
                    transaction.setCustomerId(customerId);
                    transaction.setTicketCount(1);

                    transactions.add(transaction);
                }

                transactionRepository.saveAll(transactions);

                // Send WebSocket update
                messagingTemplate.convertAndSend("/topic/ticket-updates",
                        Map.of("action", "purchase", "customer", customerId, "tickets", ticketsToBuy, "events", eventNames)
                );

                // Log ticket purchase
                logService.logTicketPurchase(customerId, ticketsToBuy, eventNames, totalPrice, currentTickets);

                // Log current tickets in pool after purchase
                logCurrentTicketsInPool();

                if (ticketsSold >= config.getTotalTickets() && ticketPool.isEmpty()) {
                    stopTicketHandling();
                    allTicketsSold = true; // Set flag when all tickets are sold

                }
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logService.logCustomerTicketPurchaseInterruption(customerId);
        }
    }

    /**
     * Initializes the ticket handling system.
     * Starts vendor and customer threads for ticket addition and purchase.
     * Validates system configuration and event availability before starting.
     * Resets system state and prepares for ticket sales.
     */
    public void startTicketHandling() {
        // Check if all tickets have been sold previously
        if (allTicketsSold) {
            logService.logSystemStatus("All tickets have been sold. System must be reset before restarting.");
        }
        if (running) {
            logService.logSystemStatus("System is already running.");
            return;
        }

        SystemConfiguration config = configurationRepository.findFirstByOrderByIdDesc();
        List<Event> events = eventRepository.findAll();

        if (events.isEmpty()) {
            logService.logSystemStatus("No events configured. Cannot start ticket system.");
        }

        if (config == null) {
            logService.logSystemStatus("No system configuration found. Cannot start ticket system.");
        }

        running = true;
        stopped = false;
        allTicketsSold = false; // Reset this flag when starting

        logService.logSystemStart();

        for (int i = 0; i < events.size(); i++) {
            final int vendorId = i + 1;
            final Event event = events.get(i);
            Thread vendorThread = new Thread(() -> startVendorThread(vendorId, event.getEventName(), config));
            vendorThread.start();
            vendorThreads.add(vendorThread);
        }

        // Start customer threads
        for (int i = 1; i <= 20; i++) {
            final int customerId = i;
            Thread customerThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        purchaseTickets(customerId, config);
                        Thread.sleep(40000 / config.getRetrievalRate());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            customerThread.start();
            customerThreads.add(customerThread);
        }
    }

    /**
     * Retrieves the current number of tickets for each event in the ticket pool.
     * @return A map with event names as keys and ticket counts as values
     */
    public Map<String, Integer> getCurrentTicketsByEvent() {
        lock.lock();
        try {
            // Use a mutable map to count tickets
            Map<String, Integer> ticketCounts = new HashMap<>();

            // Iterate through the ticket pool and count tickets for each event
            for (Ticket ticket : ticketPool) {
                ticketCounts.put(
                        ticket.getEventName(),
                        ticketCounts.getOrDefault(ticket.getEventName(), 0) + 1
                );
            }

            return ticketCounts;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Resets the ticket handling system to its initial state.
     * Stops current ticket handling, clears all internal state,
     * and sends a WebSocket update about the system reset.
     */
    public void resetTicketHandling() {
        // Stop the current ticket handling if it's running
        if (running) {
            stopTicketHandling();
        }

        // Clear all internal state
        lock.lock();
        try {
            // Reset counters
            ticketsAdded = 0;
            ticketsSold = 0;
            currentTickets = 0;

            // Clear ticket pool
            ticketPool.clear();
            ticketsAvailable = new Semaphore(0);
            customerQueue.clear();

            // Clear thread lists
            vendorThreads.clear();
            customerThreads.clear();

            // Reset running and stopped flags
            running = false;
            stopped = false;
            allTicketsSold = false;

            logService.logSystemReset();

        } finally {
            lock.unlock();
        }

        // Send a WebSocket update about the system reset
        messagingTemplate.convertAndSend("/topic/ticket-updates",
                Map.of("action", "reset", "message", "Ticket system has been reset")
        );
    }

    /**
     * @return Total number of tickets added to the system
     */
    public int getTicketsAdded() {
        return ticketsAdded;
    }

    /**
     * @return Current number of tickets available in the ticket pool
     */
    public int getCurrentTickets() {
        return currentTickets;
    }

    /**
     * @return Total number of tickets sold
     */
    public int getTicketsSold() {
        return ticketsSold;
    }

    /**
     * @return Whether the ticket system is currently running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * @return Whether all tickets in the system have been sold
     */
    public boolean isAllTicketsSold() {
        return allTicketsSold;
    }

    /**
     * Stops the ticket handling system.
     * Interrupts all vendor and customer threads, logs system stop details,
     * and updates system state flags.
     */
    public void stopTicketHandling() {
        if (!running) {
            logService.logSystemStatus("System is not running.");
            return;
        }
        running = false;
        stopped = true;

        // Interrupt all vendor and customer threads
        vendorThreads.forEach(Thread::interrupt);
        customerThreads.forEach(Thread::interrupt);
        logService.logSystemStatus("All tickets have been sold.");

        logService.logSystemStop(ticketsAdded, ticketsSold);
    }
}