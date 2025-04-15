import React from 'react';
import { Container, CssBaseline, ThemeProvider, createTheme } from '@mui/material';
import DataIngestionForm from './components/DataIngestionForm';

const theme = createTheme();

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <h1>ClickHouse File Ingestor</h1>
        <DataIngestionForm />
      </Container>
    </ThemeProvider>
  );
}

export default App;
