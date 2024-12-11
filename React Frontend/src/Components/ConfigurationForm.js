import React, { useState } from 'react';
import { 
  TextField, 
  Button, 
  Container, 
  Typography, 
  Grid, 
  Paper,
  Box
} from '@mui/material';
import toast, { Toaster } from 'react-hot-toast';
import { systemConfigService } from './systemConfigService';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import SaveIcon from '@mui/icons-material/Save';

// State variables to store configuration form values
const ConfigurationForm = ({ onConfigurationComplete }) => {
  const [maxCapacity, setMaxCapacity] = useState('');
  const [totalTickets, setTotalTickets] = useState('');
  const [releaseRate, setReleaseRate] = useState('');
  const [retrievalRate, setRetrievalRate] = useState('');
  const [events, setEvents] = useState([{ name: '', price: '' }]);
  const [validationErrors, setValidationErrors] = useState({});
  
   // Helper function to validate if a value is a positive number
  const validatePositiveNumber = (value) => {
    return value !== '' && !isNaN(value) && parseFloat(value) > 0;
  };
  
   // Function to validate the entire configuration form
  const validateConfiguration = () => {
    const errors = {};

    // Validate max capacity
    if (!validatePositiveNumber(maxCapacity)) {
      errors.maxCapacity = 'Maximum capacity must be a positive number';
    }

    // Validate total tickets
    if (!validatePositiveNumber(totalTickets)) {
      errors.totalTickets = 'Total tickets must be a positive number';
    } else if (parseFloat(totalTickets) > parseFloat(maxCapacity)) {
      errors.totalTickets = 'Total tickets cannot exceed maximum capacity';
    }

    // Validate release and retrieval rates
    if (!validatePositiveNumber(releaseRate)) {
      errors.releaseRate = 'Release rate must be a positive number';
    } else if (parseFloat(releaseRate) > parseFloat(totalTickets)) {
      errors.releaseRate = 'Release rate cannot exceed total tickets';
    }

    if (!validatePositiveNumber(retrievalRate)) {
      errors.retrievalRate = 'Retrieval rate must be a positive number';
    } else if (parseFloat(retrievalRate) > parseFloat(totalTickets)) {
      errors.retrievalRate = 'Retrieval rate cannot exceed total tickets';
    }

    // Validate events
    const eventErrors = events.map((event, index) => {
      const eventError = {};
      
      if (!event.name.trim()) {
        eventError.name = 'Event name is required';
      }

      if (!validatePositiveNumber(event.price)) {
        eventError.price = 'Price must be a positive number';
      }

      return Object.keys(eventError).length > 0 ? eventError : null;
    });

    // Check for duplicate event names
    const eventNames = events.map(event => event.name.trim().toLowerCase());
    const duplicateEvents = eventNames.filter((name, index) => 
      eventNames.indexOf(name) !== index
    );

    if (duplicateEvents.length > 0) {
      errors.events = 'Event names must be unique';
    }

    setValidationErrors({
      ...errors,
      events: eventErrors
    });

    return Object.keys(errors).length === 0 && !eventErrors.some(Boolean);
  };
  
  // Function to add a new event
  const handleAddEvent = () => {

    // Check for duplicate event names before adding
    const existingNames = events.map(event => event.name.trim().toLowerCase());
    
    // Prompt user not to add duplicate events
    if (existingNames.includes('')) {
      toast.error('Please fill in existing event names before adding a new event', {
        style: {
          background: '#f44336',
          color: 'white',
        }
      });
      return;
    }

    // Check if the last event has a name (prevent adding empty events)
    const lastEvent = events[events.length - 1];
    if (!lastEvent.name.trim() || !validatePositiveNumber(lastEvent.price)) {
      toast.error('Please complete the previous event details before adding a new event', {
        style: {
          background: '#f44336',
          color: 'white',
        }
      });
      return;
    }

    setEvents([...events, { name: '', price: '' }]);
  };

  const updateEvent = (index, field, value) => {
    const updatedEvents = [...events];
    updatedEvents[index][field] = value;
    
    // Check for duplicate event names when updating
    if (field === 'name') {
      const duplicateNames = updatedEvents
        .map(event => event.name.trim().toLowerCase())
        .filter((name, idx) => name !== '' && 
          idx !== index && 
          name === value.trim().toLowerCase());
      
      if (duplicateNames.length > 0) {
        toast.error('Event names must be unique', {
          style: {
            background: '#f44336',
            color: 'white',
          }
        });
        return;
      }
    }

    setEvents(updatedEvents);
    
    // Clear specific event validation error when user starts typing
    if (validationErrors.events) {
      const newEventErrors = [...validationErrors.events];
      if (newEventErrors[index]) {
        delete newEventErrors[index][field];
        if (Object.keys(newEventErrors[index]).length === 0) {
          newEventErrors[index] = null;
        }
        setValidationErrors(prevErrors => ({
          ...prevErrors,
          events: newEventErrors
        }));
      }
    }
  };
  
  // Function to remove an event from the list
  const removeEvent = (index) => {
    const updatedEvents = events.filter((_, i) => i !== index);
    setEvents(updatedEvents);

    // Update event errors
    if (validationErrors.events) {
      const newEventErrors = validationErrors.events.filter((_, i) => i !== index);
      setValidationErrors(prevErrors => ({
        ...prevErrors,
        events: newEventErrors
      }));
    }
  };
  
  // Function to handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validate the form before submission
    if (!validateConfiguration()) {
      toast.error('Please correct the errors before submitting');
      return;
    }
    
    // Create the configuration object
    const config = {
      maxCapacity: parseInt(maxCapacity),
      totalTickets: parseInt(totalTickets),
      releaseRate: parseInt(releaseRate),
      retrievalRate: parseInt(retrievalRate),
      events: events.map(event => ({
        name: event.name,
        price: parseFloat(event.price)  // Ensure price is included
      }))
    };
  
    try {

      // Call the system config service to save the configuration
      const result = await systemConfigService.configure(config);
      toast.success('System Configured Successfully!');
      onConfigurationComplete(result);  // Call the callback passed as prop
    } catch (error) {
      toast.error(error.message || 'Configuration Failed');
    }
  };

  return (
    <Container maxWidth="md">
      <Toaster position="top-right" /> {/* Toast notifications */}
      <Paper 
        elevation={3} 
        sx={{ 
          p: 3, 
          mt: 2, 
          background: 'linear-gradient(145deg, #f0f4f8 0%, #e6eaf3 100%)',
          borderRadius: 2 
        }}
      >
        <Typography 
          variant="h5" 
          gutterBottom 
          sx={{ 
            textAlign: 'center', 
            color: '#3f51b5', 
            fontWeight: 600 
          }}
        >
          Ticket System Configuration
        </Typography>
        <form onSubmit={handleSubmit}>
          <Grid container spacing={2}>

             {/* Input for Maximum Capacity */}
            <Grid item xs={6}>
              <TextField
                fullWidth
                label="Maximum Capacity"
                type="number"
                value={maxCapacity}
                onChange={(e) => {
                  setMaxCapacity(e.target.value);
                  if (validationErrors.maxCapacity) {
                    setValidationErrors(prev => ({...prev, maxCapacity: undefined}));
                  }
                }}
                required
                variant="outlined"
                color="primary"
                error={!!validationErrors.maxCapacity}
                helperText={validationErrors.maxCapacity}
                inputProps={{
                  min: 1
                }}
              />
            
            {/* Input for Total Tickets */}
            </Grid>
            <Grid item xs={6}>
              <TextField
                fullWidth
                label="Total Tickets"
                type="number"
                value={totalTickets}
                onChange={(e) => {
                  setTotalTickets(e.target.value);
                  if (validationErrors.totalTickets) {
                    setValidationErrors(prev => ({...prev, totalTickets: undefined}));
                  }
                }}
                required
                variant="outlined"
                color="primary"
                error={!!validationErrors.totalTickets}
                helperText={validationErrors.totalTickets}
                inputProps={{
                  min: 1
                }}
              />
            </Grid>

            {/* Input for Release Rate */}
            <Grid item xs={6}>
              <TextField
                fullWidth
                label="Release Rate"
                type="number"
                value={releaseRate}
                onChange={(e) => {
                  setReleaseRate(e.target.value);
                  if (validationErrors.releaseRate) {
                    setValidationErrors(prev => ({...prev, releaseRate: undefined}));
                  }
                }}
                required
                variant="outlined"
                color="primary"
                error={!!validationErrors.releaseRate}
                helperText={validationErrors.releaseRate}
                inputProps={{
                  min: 1
                }}
              />
            </Grid>

             {/* Input for Retrieval Rate */}
            <Grid item xs={6}>
              <TextField
                fullWidth
                label="Retrieval Rate"
                type="number"
                value={retrievalRate}
                onChange={(e) => {
                  setRetrievalRate(e.target.value);
                  if (validationErrors.retrievalRate) {
                    setValidationErrors(prev => ({...prev, retrievalRate: undefined}));
                  }
                }}
                required
                variant="outlined"
                color="primary"
                error={!!validationErrors.retrievalRate}
                helperText={validationErrors.retrievalRate}
                inputProps={{
                  min: 1
                }}
              />
            </Grid>
          </Grid>

          <Typography variant="h6" sx={{ mt: 2, color: '#3f51b5' }}>Events</Typography>
          {events.map((event, index) => (
            <Grid container spacing={2} key={index} sx={{ mt: 1 }} alignItems="center">
              <Grid item xs={5}>
                <TextField
                  fullWidth
                  label="Event Name"
                  value={event.name}
                  onChange={(e) => updateEvent(index, 'name', e.target.value)}
                  required
                  variant="outlined"
                  error={validationErrors.events?.[index]?.name !== undefined}
                  helperText={validationErrors.events?.[index]?.name}
                />
              </Grid>
              <Grid item xs={5}>
                <TextField
                  fullWidth
                  label="Event Price"
                  type="number"
                  value={event.price}
                  onChange={(e) => updateEvent(index, 'price', e.target.value)}
                  required
                  variant="outlined"
                  error={validationErrors.events?.[index]?.price !== undefined}
                  helperText={validationErrors.events?.[index]?.price}
                  inputProps={{
                    min: 0.01,
                    step: 0.01
                  }}
                />
              </Grid>

              {/* Remove Event Button */}
              <Grid item xs={2}>
                <Button 
                  variant="outlined" 
                  color="error" 
                  onClick={() => removeEvent(index)}
                  disabled={events.length <= 1}
                  startIcon={<DeleteOutlineIcon />}
                  sx={{ height: '100%' }}
                >
                  Remove
                </Button>
              </Grid>
            </Grid>
          ))}

          <Box sx={{ 
            display: 'flex', 
            justifyContent: 'space-between', 
            mt: 2 
          }}>
            <Button 
              variant="outlined" 
              color="primary" 
              onClick={handleAddEvent}
              startIcon={<AddCircleOutlineIcon />}
            >
              Add Event
            </Button>

            {/* Submit Button */}
            <Button 
              type="submit" 
              variant="contained" 
              color="primary" 
              startIcon={<SaveIcon />}
            >
              Configure System
            </Button>
          </Box>
        </form>
      </Paper>
    </Container>
  );
};

export default ConfigurationForm;