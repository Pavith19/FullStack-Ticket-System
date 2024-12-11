import React from 'react';
import { Box, Container, Typography } from '@mui/material';

const Footer = () => {
  return (

    // Use the Box component to create the footer section
    <Box
      component="footer"
      sx={{
        backgroundColor: '#ffffff', 
        color: '#333',
        padding: '30px 0',
        textAlign: 'center',
        borderTop: '1px solid #e0e0e0',
        marginTop: 4,
      }}
    >
      <Container> {/* Container to center the content and limit the width */}
        <Typography 
          variant="h6" 
          sx={{ 
            fontWeight: 500, 
            color: '#1a73e8', // A nice blue color to match your site's theme
            marginBottom: 1 
          }}
        >
          Tickets.LK
        </Typography>

        {/* Body text for copyright notice */}
        <Typography 
          variant="body1" 
          sx={{ 
            fontSize: '0.95rem', 
            color: '#333',
            fontWeight: 400,
          }}
        >
          © {new Date().getFullYear()} All Rights Reserved
        </Typography>
        <Typography 
          variant="body2" 
          sx={{ 
            fontSize: '0.95rem', 
            color: '#666',
            marginTop: 0.6 
          }}
        >
          Developed by Pavith Bambaravanage
        </Typography>
      </Container>
    </Box>
  );
};

export default Footer;