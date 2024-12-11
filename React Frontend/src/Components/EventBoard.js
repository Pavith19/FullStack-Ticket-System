import React, { useState, useCallback } from 'react';
import { 
  Typography, 
  Box, 
  Grid, 
  Chip,
  Card,
  CardContent,
  CardHeader,
  IconButton,
  Tooltip
} from '@mui/material';
import { 
  Event as EventIcon, 
  AttachMoney as AttachMoneyIcon,
  Refresh as RefreshIcon
} from '@mui/icons-material';

const EventBoard = ({ systemConfig, onReload }) => {
  const [isLoading, setIsLoading] = useState(false);

  // Function to transform events if they are not in the correct format
  const transformEvents = (config) => {
    if (!config || !config.events) return [];
    
    // If events is an array of strings, convert to objects with default price
    if (config.events.length > 0 && typeof config.events[0] === 'string') {
      return config.events.map(eventName => ({
        name: eventName,
        price: 0.00  // Default price if not provided
      }));
    }
    
    return config.events; // Return the event objects if already in correct format
  };
  
  // Transform the events based on the systemConfig
  const events = transformEvents(systemConfig);
  
  // Reload handler that triggers the onReload function passed as prop
  const handleReload = useCallback(() => {
    if (onReload) {
      setIsLoading(true);
      onReload().finally(() => {
        setIsLoading(false);
      });
    }
  }, [onReload]);
  
  // If no events are available, display a message indicating no events are configured
  if (!events || events.length === 0) {
    return (
      <Card 
        variant="outlined"
        sx={{ 
          height: '100%', 
          display: 'flex', 
          flexDirection: 'column',
          borderRadius: 3,
          textAlign: 'center'
        }}
      >
        <CardHeader
          action={
            <Tooltip title="Reload Events">
              <IconButton 
                onClick={handleReload} 
                color="primary"
                disabled={isLoading}
              >
                <RefreshIcon />
              </IconButton>
            </Tooltip>
          }
          title={
            <Typography 
              variant="h6" 
              sx={{ 
                color: 'primary.main', 
                display: 'flex', 
                alignItems: 'center',
                justifyContent: 'center',
                fontWeight: 'bold'
              }}
            >
              <EventIcon sx={{ mr: 1 }} /> 
              Event Catalog
            </Typography>
          }
        />
        
        <Box sx={{ 
          display: 'flex', 
          flexDirection: 'column', 
          alignItems: 'center', 
          justifyContent: 'center', 
          flexGrow: 1,
          p: 3 
        }}>
          <Typography variant="h6" color="textSecondary">
            No events configured
          </Typography>
        </Box>
      </Card>
    );
  }
  
   // If events are available, display them in a grid layout
  return (
    <Card 
      variant="outlined"
      sx={{ 
        height: '100%', 
        display: 'flex', 
        flexDirection: 'column',
        borderRadius: 3
      }}
    >
      <CardHeader
        action={
          <Tooltip title="Reload Events">
            <IconButton 
              onClick={handleReload} 
              color="primary"
              disabled={isLoading}
            >
              <RefreshIcon />
            </IconButton>
          </Tooltip>
        }
        title={
          <Typography 
            variant="h6" 
            sx={{ 
              color: 'primary.main', 
              display: 'flex', 
              alignItems: 'center',
              fontWeight: 'bold'
            }}
          >
            <EventIcon sx={{ mr: 1 }} /> 
            Event Catalog
          </Typography>
        }
        sx={{ pb: 0 }} // Remove bottom padding from CardHeader
      />
      
      <CardContent sx={{ flexGrow: 1, pt: 2 }}>
        <Grid container spacing={2}>
          {events.map((event, index) => (
            <Grid item xs={12} sm={6} key={index}>
              <Card 
                variant="outlined"
                sx={{
                  height: '100%',
                  transition: 'all 0.3s ease',
                  '&:hover': {
                    transform: 'scale(1.03)',
                    boxShadow: 3,
                    borderColor: 'primary.main'
                  }
                }}
              >
                <CardContent>
                  <Typography 
                    variant="h6" 
                    sx={{ 
                      mb: 2, 
                      color: 'primary.main',
                      fontWeight: 'bold',
                      textAlign: 'center'
                    }}
                  >
                    {event.name || 'Unnamed Event'} {/* Display event name, or fallback */}
                  </Typography>
                  
                  <Box sx={{ display: 'flex', justifyContent: 'center' }}>
                    <Chip 
                      icon={<AttachMoneyIcon />} 
                      label={`Price: $${event.price !== undefined ? event.price.toFixed(2) : '0.00'}`} 
                      color="primary" 
                      variant="outlined"
                      sx={{ 
                        fontSize: '0.9rem',
                        fontWeight: 'bold',
                        p: 1,
                        width: 'fit-content'
                      }}
                    />
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      </CardContent>
    </Card>
  );
};

export default EventBoard;