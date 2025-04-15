import React, { useState } from 'react';
import { Paper, Button, Box, Typography, TextField } from '@mui/material';

const DataIngestionForm = ({ connectionDetails }) => {
  const [file, setFile] = useState(null);
  const [tableName, setTableName] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    // Implementation coming in next step
  };

  return (
    <Paper sx={{ p: 3 }}>
      <Typography variant="h6" gutterBottom>
        Data Ingestion
      </Typography>
      <Box component="form" onSubmit={handleSubmit} sx={{ mt: 2 }}>
        <TextField
          fullWidth
          label="Table Name"
          margin="normal"
          value={tableName}
          onChange={(e) => setTableName(e.target.value)}
        />
        <input
          accept=".csv"
          type="file"
          onChange={(e) => setFile(e.target.files[0])}
          style={{ display: 'none' }}
          id="csv-file-input"
        />
        <label htmlFor="csv-file-input">
          <Button
            variant="contained"
            component="span"
            fullWidth
            sx={{ mt: 2 }}
          >
            Select CSV File
          </Button>
        </label>
        {file && (
          <Typography sx={{ mt: 2 }}>
            Selected file: {file.name}
          </Typography>
        )}
        <Button 
          type="submit" 
          variant="contained" 
          fullWidth 
          sx={{ mt: 3 }}
          disabled={!file || !tableName}
        >
          Upload and Process
        </Button>
      </Box>
    </Paper>
  );
};

export default DataIngestionForm;
