import React from 'react';
import { 
  Typography, 
  Box,
  Card,
  CardContent,
  Grid,
  Divider
} from '@mui/material';
import ConfirmationNumberOutlinedIcon from '@mui/icons-material/ConfirmationNumberOutlined';
import LocalActivityOutlinedIcon from '@mui/icons-material/LocalActivityOutlined';
import EventSeatOutlinedIcon from '@mui/icons-material/EventSeatOutlined';

const TicketDisplay = ({ ticketData }) => {

  // If no ticketData is provided, display a message indicating the system is not started
  if (!ticketData) {
    return (
      <Box sx={{ 
        display: 'flex', 
        flexDirection: 'column', 
        alignItems: 'center', 
        justifyContent: 'center', 
        height: '100%', 
        p: 3 
      }}>
        <Typography variant="h6" color="textSecondary">
          Start the ticket system to view ticket availability
        </Typography>
      </Box>
    );
  }
  
  // Return the card displaying ticket data when available
  return (
    <Card variant="outlined" sx={{ 
      borderRadius: 3,
      boxShadow: 3,
      transition: 'transform 0.3s',
      '&:hover': { 
        transform: 'scale(1.02)' 
      }
    }}>
      <CardContent>
        {/* Title of the section */}
        <Typography 
          variant="h5" 
          gutterBottom 
          sx={{ 
            color: 'primary.main', 
            fontWeight: 'bold',
            mb: 3,
            textAlign: 'center'
          }}
        >
          Ticket Availability Overview
        </Typography>
        
        {/* Display ticket summary in a grid layout */}
        <Grid container spacing={2} sx={{ mb: 3 }}>
          <Grid item xs={4}>
            <Box sx={{ 
              display: 'flex', 
              flexDirection: 'column',
              alignItems: 'center', 
              justifyContent: 'center',
              bgcolor: 'primary.light',
              color: 'primary.contrastText',
              p: 3,
              borderRadius: 2,
              height: '100%'
            }}>
              <ConfirmationNumberOutlinedIcon sx={{ fontSize: 48, mb: 1 }} />
              <Typography variant="h6">Total Tickets</Typography>
              <Typography variant="h4" sx={{ fontWeight: 'bold' }}>
                {ticketData.ticketsAdded}
              </Typography>
            </Box>
          </Grid>

          {/* Current Tickets Section */}
          <Grid item xs={4}>
            <Box sx={{ 
              display: 'flex', 
              flexDirection: 'column',
              alignItems: 'center', 
              justifyContent: 'center',
              bgcolor: 'success.light',
              color: 'success.contrastText',
              p: 3,
              borderRadius: 2,
              height: '100%'
            }}>
              <LocalActivityOutlinedIcon sx={{ fontSize: 48, mb: 1 }} />
              <Typography variant="h6">Current Tickets</Typography>
              <Typography variant="h4" sx={{ fontWeight: 'bold' }}>
                {ticketData.currentTickets}
              </Typography>
            </Box>
          </Grid>

           {/* Sold Tickets Section */}
          <Grid item xs={4}>
            <Box sx={{ 
              display: 'flex', 
              flexDirection: 'column',
              alignItems: 'center', 
              justifyContent: 'center',
              bgcolor: 'secondary.light',
              color: 'secondary.contrastText',
              p: 3,
              borderRadius: 2,
              height: '100%'
            }}>
              <EventSeatOutlinedIcon sx={{ fontSize: 48, mb: 1 }} />
              <Typography variant="h6">Sold Tickets</Typography>
              <Typography variant="h4" sx={{ fontWeight: 'bold' }}>
                {ticketData.ticketsSold}
              </Typography>
            </Box>
          </Grid>
        </Grid>
        
        {/* Divider between sections */}
        <Divider sx={{ my: 3 }} />
        
        {/* Title for event breakdown section */}
        <Typography 
          variant="h6" 
          sx={{ 
            color: 'text.primary', 
            fontWeight: 'bold',
            mb: 2,
            textAlign: 'center'
          }}
        >
          Event Ticket Breakdown
        </Typography>
        
        {/* Display breakdown of ticket availability by event */}
        <Grid container spacing={2}>
          {Object.entries(ticketData.availability || {}).map(([event, count]) => (
            <Grid item xs={12} sm={6} key={event}>
              <Box sx={{ 
                display: 'flex', 
                justifyContent: 'space-between',
                alignItems: 'center',
                bgcolor: 'background.paper',
                p: 2,
                borderRadius: 2,
                boxShadow: 2,
                transition: 'transform 0.2s',
                '&:hover': {
                  transform: 'scale(1.02)'
                }
              }}>
                <Typography variant="h6" color="text.primary">{event}</Typography>
                <Typography 
                  variant="h5" 
                  color="primary" 
                  sx={{ fontWeight: 'bold' }}
                >
                  {count} Tickets
                </Typography>
              </Box>
            </Grid>
          ))}
        </Grid>
      </CardContent>
    </Card>
  );
};

export default TicketDisplay;