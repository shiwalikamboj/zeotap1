import React, { useState } from 'react';
import { Box, Typography, Table, TableBody, TableCell, TableHead, TableRow, Paper, Button, Alert, CircularProgress } from '@mui/material';

function DataPreview({ tableName, connectionDetails }) {
    const [previewData, setPreviewData] = useState(null);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);

    const fetchPreview = async () => {
        try {
            setLoading(true);
            setError(null);
            const response = await fetch(
                'http://localhost:8080/api/transfer/preview', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        source: 'clickhouse',
                        connectionDetails: {
                            host: connectionDetails.host,
                            port: connectionDetails.port,
                            database: connectionDetails.database,
                            user: connectionDetails.user,
                            token: connectionDetails.token
                        },
                        tableName: tableName,
                        limit: 100
                    })
                }
            );
            
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Server error: ${response.status}`);
            }

            const data = await response.json();
            console.log('Preview response:', data);

            if (data && Array.isArray(data)) {
                setPreviewData(data);
            } else if (data && data.data && Array.isArray(data.data)) {
                setPreviewData(data.data);
            } else {
                throw new Error('Invalid data format received');
            }
        } catch (error) {
            console.error('Preview error:', error);
            setError(error.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Box sx={{ mt: 4 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                <Typography variant="h6">Preview Data for {tableName}</Typography>
                <Button 
                    variant="contained" 
                    onClick={fetchPreview}
                    color="primary"
                    disabled={loading}
                >
                    {loading ? 'Loading...' : 'Load Preview'}
                </Button>
            </Box>
            
            {loading && (
                <Box sx={{ display: 'flex', justifyContent: 'center', my: 2 }}>
                    <CircularProgress />
                </Box>
            )}
            
            {error && (
                <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                </Alert>
            )}

            {previewData && previewData.length > 0 && (
                <Paper sx={{ mt: 2, overflowX: 'auto' }}>
                    <Table>
                        <TableHead>
                            <TableRow>
                                {Object.keys(previewData[0]).map(key => (
                                    <TableCell key={key}>{key}</TableCell>
                                ))}
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {previewData.map((row, idx) => (
                                <TableRow key={idx}>
                                    {Object.values(row).map((value, i) => (
                                        <TableCell key={i}>{String(value)}</TableCell>
                                    ))}
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </Paper>
            )}
        </Box>
    );
}

export default DataPreview;
