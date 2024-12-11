import React, { useState } from 'react';
import { 
  Button, 
  Typography, 
  Grid, 
  Box, 
  Alert,
  Card,
  CardContent,
  Divider
} from '@mui/material';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import StopIcon from '@mui/icons-material/Stop';
import RefreshIcon from '@mui/icons-material/Refresh';
import SettingsIcon from '@mui/icons-material/Settings';
import PeopleIcon from '@mui/icons-material/People';
import ConfirmationNumberIcon from '@mui/icons-material/ConfirmationNumber';
import SpeedIcon from '@mui/icons-material/Speed';
import toast from 'react-hot-toast';

import { ticketControlService, ticketAvailabilityService } from './systemConfigService';

const ControlPanel = ({ systemConfig, onSystemStateChange }) => {
  const [isRunning, setIsRunning] = useState(false); // State to track if the system is running
  
  // Function to handle starting the system
  const handleStartSystem = async () => {
    try {
      const ticketAvailability = await ticketAvailabilityService.getAvailability();
      
      // If no tickets are available, show error
      if (ticketAvailability.availableTickets === 0) {
        toast.error('All tickets have been sold. Please reset the system first.');
        return;
      }
      
      // Check if the system is properly configured
      if (!systemConfig) {
        toast.error('Please configure the system first');
        return;
      }
      
      // Start the ticket control system
      await ticketControlService.startSystem();
      setIsRunning(true); // Set system state to running
      toast.success('Ticket System Started'); // Notify parent component that system has started
      onSystemStateChange('start');
    } catch (error) {
      toast.error(error.message || 'Failed to start system'); // Show error if system can't be started
    }
  };
  
  // Function to handle stopping the system
  const handleStopSystem = async () => {
    try {
      await ticketControlService.stopSystem(); // Stop the ticket control system
      setIsRunning(false);
      toast.success('Ticket System Stopped');
      onSystemStateChange('stop');
    } catch (error) {
      toast.error(error.message || 'Failed to stop system');
    }
  };
  
  // Function to handle resetting the system
  const handleResetSystem = async () => {
    try {
      await ticketControlService.resetSystem(); // Reset the ticket control system
      setIsRunning(false);
      toast.success('Ticket System Reset');
      onSystemStateChange('reset');
    } catch (error) {
      toast.error(error.message || 'Failed to reset system');
    }
  };

  return (
    <Card 
      variant="outlined" 
      sx={{ 
        borderRadius: 3, 
        boxShadow: 3,
        transition: 'transform 0.3s',
        '&:hover': {
          transform: 'scale(1.02)'
        }
      }}
    >
      <CardContent>
        {/* System Control Header */}
        <Typography 
          variant="h6" 
          gutterBottom 
          sx={{ 
            color: 'primary.main', 
            fontWeight: 'bold',
            mb: 2 
          }}
        >
          System Control
        </Typography>
        
         {/* Show warning if system is not configured */}
        {!systemConfig && (
          <Alert 
            severity="warning" 
            sx={{ 
              mb: 2, 
              '& .MuiAlert-icon': { 
                color: 'warning.main' 
              } 
            }}
          >
            Please configure the system before starting
          </Alert>
        )}
        
        {/* Start, Stop, Reset buttons */}
        <Grid container spacing={2} sx={{ mb: 2 }}>
          <Grid item xs={4}>
            <Button
              fullWidth
              variant="contained"
              color="success"
              startIcon={<PlayArrowIcon />}
              onClick={handleStartSystem}  // Handle start button click
              disabled={!systemConfig || isRunning}
              sx={{
                borderRadius: 2,
                textTransform: 'none',
                py: 1.5,
                '&:hover': {
                  backgroundColor: 'success.dark'
                }
              }}
            >
              Start
            </Button>
          </Grid>
          <Grid item xs={4}>
            <Button
              fullWidth
              variant="contained"
              color="error"
              startIcon={<StopIcon />}
              onClick={handleStopSystem}
              disabled={!isRunning}
              sx={{
                borderRadius: 2,
                textTransform: 'none',
                py: 1.5,
                '&:hover': {
                  backgroundColor: 'error.dark'
                }
              }}
            >
              Stop
            </Button>
          </Grid>
          <Grid item xs={4}>
            <Button
              fullWidth
              variant="contained"
              color="secondary"
              startIcon={<RefreshIcon />}
              onClick={handleResetSystem} // Handle reset button click
              sx={{
                borderRadius: 2,
                textTransform: 'none',
                py: 1.5,
                '&:hover': {
                  backgroundColor: 'secondary.dark'
                }
              }}
            >
              Reset
            </Button>
          </Grid>
        </Grid>
       
       {/* Divider between system control buttons and system configuration */}
        <Divider sx={{ my: 2 }} />

        {/* Display system configuration if available */}
        {systemConfig && (
          <Box>

             {/* Configuration Section Header */}
            <Typography 
              variant="subtitle1" 
              sx={{ 
                display: 'flex', 
                alignItems: 'center',
                color: 'text.secondary', 
                fontWeight: 'bold',
                mb: 2 
              }}
            >
              <SettingsIcon sx={{ mr: 1, color: 'primary.main' }} /> 
              System Configuration
            </Typography>

            {/* Display configuration details like max capacity, total tickets, rates */}
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6} md={3}>
                <Box 
                  sx={{ 
                    display: 'flex', 
                    alignItems: 'center', 
                    bgcolor: 'grey.100', 
                    p: 2, 
                    borderRadius: 2 
                  }}
                >
                  <PeopleIcon sx={{ mr: 2, color: 'primary.main' }} />
                  <Box>
                    <Typography variant="body2" color="text.secondary">Max Capacity</Typography>
                    <Typography variant="h6" color="primary">
                      {systemConfig.maxCapacity}
                    </Typography>
                  </Box>
                </Box>
              </Grid>

               {/* Total Tickets */}
              <Grid item xs={12} sm={6} md={3}>
                <Box 
                  sx={{ 
                    display: 'flex', 
                    alignItems: 'center', 
                    bgcolor: 'grey.100', 
                    p: 2, 
                    borderRadius: 2 
                  }}
                >
                  <ConfirmationNumberIcon sx={{ mr: 2, color: 'secondary.main' }} />
                  <Box>
                    <Typography variant="body2" color="text.secondary">Total Tickets</Typography>
                    <Typography variant="h6" color="secondary.main">
                      {systemConfig.totalTickets}
                    </Typography>
                  </Box>
                </Box>
              </Grid>

               {/* Release Rate */}
              <Grid item xs={12} sm={6} md={3}>
                <Box 
                  sx={{ 
                    display: 'flex', 
                    alignItems: 'center', 
                    bgcolor: 'grey.100', 
                    p: 2, 
                    borderRadius: 2 
                  }}
                >
                  <SpeedIcon sx={{ mr: 2, color: 'info.main' }} />
                  <Box>
                    <Typography variant="body2" color="text.secondary">Release Rate</Typography>
                    <Typography variant="h6" color="info.main">
                      {systemConfig.releaseRate}
                    </Typography>
                  </Box>
                </Box>
              </Grid>

               {/* Retrieval Rate */}
              <Grid item xs={12} sm={6} md={3}>
                <Box 
                  sx={{ 
                    display: 'flex', 
                    alignItems: 'center', 
                    bgcolor: 'grey.100', 
                    p: 2, 
                    borderRadius: 2 
                  }}
                >
                  <SpeedIcon sx={{ mr: 2, color: 'warning.main' }} />
                  <Box>
                    <Typography variant="body2" color="text.secondary">Retrieval Rate</Typography>
                    <Typography variant="h6" color="warning.main">
                      {systemConfig.retrievalRate}
                    </Typography>
                  </Box>
                </Box>
              </Grid>
            </Grid>
          </Box>
        )}
      </CardContent>
    </Card>
  );
};

export default ControlPanel;