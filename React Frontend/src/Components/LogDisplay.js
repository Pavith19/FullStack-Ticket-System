import React, { useState } from 'react';
import { 
  Typography, 
  Box, 
  List, 
  ListItem, 
  ListItemText, 
  Paper, 
  Select, 
  MenuItem,
  FormControl,
  InputLabel
} from '@mui/material';

const LogDisplay = ({ logs = [] }) => {

  // State to manage the selected log level filter (default is 'all')
  const [logLevel, setLogLevel] = useState('all');
  
  // Filter logs based on the selected log level
  const filteredLogs = logLevel === 'all' 
    ? logs 
    : logs.filter(log => log.level === logLevel);
  
  // Function to get the color for each log level
  const getLogColor = (level) => {
    switch(level) {
      case 'ERROR': return 'error.main';
      case 'WARN': return 'warning.main';
      case 'INFO': return 'info.main';
      default: return 'text.primary';
    }
  };

  return (
    <Box>
      {/* Header section with the title and the log level filter */}
      <Box sx={{ 
        display: 'flex', 
        justifyContent: 'space-between', 
        alignItems: 'center', 
        mb: 2 
      }}>
        <Typography variant="h6">
          System Logs
        </Typography>
        
         {/* Log Level filter dropdown */}
        <FormControl size="small" sx={{ minWidth: 120 }}>
          <InputLabel>Log Level</InputLabel>
          <Select
            value={logLevel}
            label="Log Level"
            onChange={(e) => setLogLevel(e.target.value)}
          >
             {/* Log level options */}
            <MenuItem value="all">All Logs</MenuItem>
            <MenuItem value="ERROR">Errors</MenuItem>
            <MenuItem value="WARN">Warnings</MenuItem>
            <MenuItem value="INFO">Info</MenuItem>
          </Select>
        </FormControl>
      </Box>
      
      {/* Paper component to contain the logs and make them scrollable */}
      <Paper sx={{ maxHeight: 500, overflow: 'auto' }}>
        <List dense>
          {/* Display a message if no logs are available */}
          {filteredLogs.length === 0 ? (
            <ListItem>
              <ListItemText primary="No logs available" />
            </ListItem>
          ) : (
            filteredLogs.map((log, index) => (
              <ListItem key={index}>
                <ListItemText
                  primary={log.message}
                  secondary={`${log.timestamp} - ${log.level}`}
                  primaryTypographyProps={{ 
                    color: getLogColor(log.level) 
                  }}
                />
              </ListItem>
            ))
          )}
        </List>
      </Paper>
    </Box>
  );
};

export default LogDisplay;