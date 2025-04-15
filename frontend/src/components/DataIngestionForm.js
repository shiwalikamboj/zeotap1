import React, { useState } from 'react';
import { Box, Button, TextField, Typography, Input, Alert, Snackbar } from '@mui/material';
import DataPreview from './DataPreview';

function DataIngestionForm() {
    const [formData, setFormData] = useState({
        host: 'localhost',
        port: '8123',
        database: 'default',
        user: 'default',
        token: '',
        file: null,
        tableName: '',
        selectedColumns: []
    });
    const [notification, setNotification] = useState({
        open: false,
        message: '',
        severity: 'success'
    });
    const [showPreview, setShowPreview] = useState(false);
    const handleTestConnection = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch(`http://localhost:8080/api/transfer/test-connection?host=${formData.host}&port=${formData.port}&database=${formData.database}&user=${formData.user}&token=${formData.token}`);
            const data = await response.json();
            setNotification({
                open: true,
                message: data.message,
                severity: data.status === 'success' ? 'success' : 'error'
            });
        } catch (error) {
            setNotification({
                open: true,
                message: 'Error connecting to server',
                severity: 'error'
            });
        }
    };

    const handleFileUpload = async (e) => {
        e.preventDefault();
        const formDataToSend = new FormData();
        formDataToSend.append('file', formData.file);
        formDataToSend.append('tableName', formData.tableName);
        formDataToSend.append('selectedColumns', formData.selectedColumns);
        formDataToSend.append('host', formData.host);
        formDataToSend.append('port', formData.port);
        formDataToSend.append('database', formData.database);
        formDataToSend.append('user', formData.user);
        formDataToSend.append('token', formData.token);

        try {
            const response = await fetch('http://localhost:8080/api/transfer/file-to-clickhouse', {
                method: 'POST',
                body: formDataToSend
            });
            const data = await response.json();
            setNotification({
                open: true,
                message: data.message || 'File uploaded successfully',
                severity: 'success'
            });
            setShowPreview(true);
        } catch (error) {
            setNotification({
                open: true,
                message: 'Error uploading file',
                severity: 'error'
            });
        }
    };

    return (
        <Box component="form" sx={{ mt: 3 }}>
            <Typography variant="h6" gutterBottom>
                Connection Details
            </Typography>
            <TextField
                fullWidth
                label="Host"
                margin="normal"
                value={formData.host}
                onChange={(e) => setFormData({...formData, host: e.target.value})}
            />
            <TextField
                fullWidth
                label="Port"
                margin="normal"
                value={formData.port}
                onChange={(e) => setFormData({...formData, port: e.target.value})}
            />
            <TextField
                fullWidth
                label="Database"
                margin="normal"
                value={formData.database}
                onChange={(e) => setFormData({...formData, database: e.target.value})}
            />
            <TextField
                fullWidth
                label="User"
                margin="normal"
                value={formData.user}
                onChange={(e) => setFormData({...formData, user: e.target.value})}
            />
            <TextField
                fullWidth
                label="Token"
                type="password"
                margin="normal"
                value={formData.token}
                onChange={(e) => setFormData({...formData, token: e.target.value})}
            />
            <Button 
                variant="contained" 
                onClick={handleTestConnection}
                sx={{ mt: 2 }}
            >
                Test Connection
            </Button>

            <Typography variant="h6" sx={{ mt: 4 }}>
                File Upload
            </Typography>
            <Input
                type="file"
                onChange={(e) => setFormData({...formData, file: e.target.files[0]})}
                sx={{ mt: 2, display: 'block' }}
            />
            <TextField
                fullWidth
                label="Table Name"
                margin="normal"
                value={formData.tableName}
                onChange={(e) => setFormData({...formData, tableName: e.target.value})}
            />
            <Button 
                variant="contained" 
                onClick={handleFileUpload}
                sx={{ mt: 2 }}
                disabled={!formData.file || !formData.tableName}
            >
                Upload to ClickHouse
            </Button>

            {showPreview && formData.tableName && (
                <DataPreview 
                    tableName={formData.tableName}
                    connectionDetails={{
                        host: formData.host,
                        port: formData.port,
                        database: formData.database,
                        user: formData.user,
                        token: formData.token
                    }}
                />
            )}

            <Snackbar
                open={notification.open}
                autoHideDuration={6000}
                onClose={() => setNotification({...notification, open: false})}
            >
                <Alert 
                    onClose={() => setNotification({...notification, open: false})} 
                    severity={notification.severity}
                >
                    {notification.message}
                </Alert>
            </Snackbar>
        </Box>
    );
}

export default DataIngestionForm;
