import React, { useState } from 'react';
import { Container, CssBaseline, ThemeProvider, createTheme, Paper, Tabs, Tab, Box } from '@mui/material';


const theme = createTheme();

function TabPanel({ children, value, index }) {
  return (
    <div hidden={value !== index} style={{ marginTop: '20px' }}>
      {value === index && <Box>{children}</Box>}
    </div>
  );
}

function App() {
  const [tabValue, setTabValue] = useState(0);
  const [connectionDetails, setConnectionDetails] = useState({
    host: 'localhost',
    port: 8123,
    database: 'file_ingestor',
    user: 'default',
    token: ''
  });
  const [isConnected, setIsConnected] = useState(false);

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <h1>ClickHouse File Ingestor</h1>
        <Paper sx={{ mb: 2 }}>
          <Tabs value={tabValue} onChange={handleTabChange} centered>
            <Tab label="Connection Settings" />
            <Tab label="Data Ingestion" disabled={!isConnected} />
          </Tabs>
        </Paper>

        <TabPanel value={tabValue} index={0}>
          <ConnectionForm 
            connectionDetails={connectionDetails}
            setConnectionDetails={setConnectionDetails}
            setIsConnected={setIsConnected}
            onSuccess={() => setTabValue(1)}
          />
        </TabPanel>
        <TabPanel value={tabValue} index={1}>
          <DataIngestionForm connectionDetails={connectionDetails} />
        </TabPanel>
      </Container>
    </ThemeProvider>
  );
}

export default App;
