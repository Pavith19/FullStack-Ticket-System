import React, { useState, useEffect } from 'react';
import {
  Container,
  Grid,
  Typography,
  Paper,
  createTheme,
  ThemeProvider
} from '@mui/material';
import { Toaster } from 'react-hot-toast';

import ConfigurationForm from './ConfigurationForm';
import ControlPanel from './ControlPanel';
import TicketDisplay from './TicketDisplay';
import LogDisplay from './LogDisplay';
import EventBoard from './EventBoard';
import Footer from './Footer';

import {
  systemConfigService,
  ticketAvailabilityService,
  WebSocketService
} from './systemConfigService';

// Custom theme definition
const theme = createTheme({
  palette: {
    background: {
      default: '#f4f6f9',
      paper: '#ffffff'
    },
    primary: {
      main: '#3f51b5',
    },
    secondary: {
      main: '#f50057',
    }
  },
  typography: {
    fontFamily: 'Roboto, Arial, sans-serif',
    h3: {
      fontWeight: 600,
      color: '#3f51b5'
    }
  },
  components: {
    MuiPaper: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
          transition: 'transform 0.3s ease-in-out',
          '&:hover': {
            transform: 'translateY(-5px)'// Hover effect for Paper component
          }
        }
      }
    }
  }
});

function Main() {
  const [systemConfig, setSystemConfig] = useState(null); // State for system configuration
  const [ticketAvailability, setTicketAvailability] = useState(null); // State for ticket availability
  const [logs, setLogs] = useState([]); // State to track logs

  // Function to reload the system configuration
  const reloadSystemConfig = async () => {
    try {
      const updatedConfig = await systemConfigService.getSystemStatus();
      setSystemConfig(updatedConfig); // Update the system config state
      return updatedConfig;
    } catch (error) {
      console.error('Failed to reload system configuration', error);
      throw error;
    }
  };

  // Effect hook to fetch initial system status and handle WebSocket connection
  useEffect(() => {
    // Fetch initial system status
    const fetchSystemStatus = async () => {
      try {
        const status = await systemConfigService.getSystemStatus();
        setSystemConfig(status);
      } catch (error) {
        console.log('No existing configuration found');
      }
    };

    fetchSystemStatus(); // Initial fetch on mount

    // WebSocket connection for real-time updates
    const stompClient = WebSocketService.connect((message) => {

      // Handle incoming log messages
      if (message.type) {
        const newLog = {
          message: message.message,
          level: message.type.includes('ERROR') ? 'ERROR' :
            message.type.includes('WARN') ? 'WARN' : 'INFO',
          timestamp: new Date().toLocaleString() // Add timestamp to logs

        };
        setLogs(prevLogs => [...prevLogs, newLog]); // Update logs state
      }

      // Handle system configuration updates via WebSocket
      if (message.action === 'system-configuration-update' || message.action === 'event-update') {
        systemConfigService.getSystemStatus()
          .then(updatedConfig => {
            setSystemConfig(updatedConfig);
          })
          .catch(error => {
            console.error('Failed to fetch updated system configuration', error);
          });
      }

      // Handle ticket availability updates
      if (message.action === 'ticket-updates' || message.details) {
        ticketAvailabilityService.getAvailability()
          .then(setTicketAvailability) // Update ticket availability
          .catch(console.error);
      }
    });

    // Cleanup WebSocket on component unmount
    return () => {
      if (stompClient) {
        stompClient.disconnect();
      }
    };
  }, []); // Empty dependency array means this effect runs once on mount
  
  // Callback when configuration is completed
  const handleConfigurationComplete = (config) => {
    console.log('Configuration Result:', config);
    setSystemConfig(config); // Update system configuration with new values

  };

  return (
    <ThemeProvider theme={theme}>
      <Container
        maxWidth="xl"
        sx={{
          background: "linear-gradient(135deg, #6ec1e4, #3f94cb, #1a73e8, #4285f4)", // Multi-shade blue gradient
          minHeight: "100vh",
          py: 4,
          color: "#333", // Text color to ensure readability
        }}
      >
        <Toaster position="top-right" />
        <Typography
          variant="h3"
          align="center"
          sx={{
            my: 3,
            fontSize: "4rem",
            fontWeight: 2000,  
            background: "linear-gradient(to right, #336699, #003366, #336699)",  // Dark blue gradient that goes left to right and right to left
            backgroundClip: "text",  // Apply the gradient to the text
            color: "transparent",  // Make the text transparent to show the gradient
            textShadow: "2px 2px 5px rgba(0, 0, 0, 0.3)",
          }}
        >
          Tickets.LK
        </Typography>


        <Grid container spacing={3}>

          {/* Configuration Form */}
          <Grid item xs={12} md={8}>
            <Grid container spacing={3}>
              <Grid item xs={12}>
                <Paper elevation={3} sx={{ p: 2 }}>
                  <ConfigurationForm
                    onConfigurationComplete={handleConfigurationComplete} // Callback when configuration is complete
                  />
                </Paper>
              </Grid>
              
              {/* Control Panel */}
              <Grid item xs={12}>
                <Paper elevation={3} sx={{ p: 2 }}>
                  <ControlPanel
                    systemConfig={systemConfig}
                    onSystemStateChange={() => { }}
                  />
                </Paper>
              </Grid>
              
               {/* Ticket Display */}
              <Grid item xs={12}>
                <Paper elevation={3} sx={{ p: 2 }}>
                  <TicketDisplay ticketData={ticketAvailability} />
                </Paper>
              </Grid>
            </Grid>
          </Grid>

          <Grid item xs={12} md={4}>
            <Grid container spacing={2}>

              {/* Log Display */}
              <Grid item xs={12}>
                <Paper elevation={3} sx={{ p: 2 }}>
                  <LogDisplay logs={logs} />
                </Paper>
              </Grid>

              {/* Event Board */}
              <Grid item xs={20}>
                <Paper elevation={3} sx={{ p: 2 }}>
                  <EventBoard
                    systemConfig={systemConfig} // Pass current system configuration
                    onReload={reloadSystemConfig} // Callback to reload system configuration
                  />
                </Paper>
              </Grid>
            </Grid>
          </Grid>
        </Grid>
        {/* Footer component for the bottom of the page */}
        <Footer />
      </Container>
    </ThemeProvider>
  );
}

export default Main;