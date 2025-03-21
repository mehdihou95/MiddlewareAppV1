import React, { useState, useEffect } from 'react';
import {
  Box,
  Paper,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  CircularProgress,
  Alert,
  Chip,
  IconButton,
  Tooltip,
} from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ErrorIcon from '@mui/icons-material/Error';
import WarningIcon from '@mui/icons-material/Warning';
import InfoIcon from '@mui/icons-material/Info';
import axios from 'axios';

interface ProcessedFile {
  id: number;
  filename: string;
  processedDate: string;
  status: string;
  recordsProcessed?: number;
  errorMessage?: string;
}

const ProcessedFiles = () => {
  const [files, setFiles] = useState<ProcessedFile[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchProcessedFiles = async () => {
      try {
        const response = await axios.get('/api/files/processed');
        setFiles(response.data);
      } catch (err) {
        setError('Failed to load processed files');
      } finally {
        setLoading(false);
      }
    };

    fetchProcessedFiles();
    // Refresh every 30 seconds
    const interval = setInterval(fetchProcessedFiles, 30000);
    return () => clearInterval(interval);
  }, []);

  const getStatusChip = (status: string) => {
    switch (status.toLowerCase()) {
      case 'success':
        return <Chip icon={<CheckCircleIcon />} label="Success" color="success" />;
      case 'error':
        return <Chip icon={<ErrorIcon />} label="Error" color="error" />;
      case 'warning':
        return <Chip icon={<WarningIcon />} label="Warning" color="warning" />;
      default:
        return <Chip icon={<InfoIcon />} label={status} color="default" />;
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Alert severity="error" sx={{ mt: 2 }}>
        {error}
      </Alert>
    );
  }

  return (
    <Box sx={{ maxWidth: 1200, mx: 'auto' }}>
      <Typography variant="h4" gutterBottom>
        Processed Files History
      </Typography>
      <TableContainer component={Paper} sx={{ mt: 2 }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>File Name</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Processed Date</TableCell>
              <TableCell>Records Processed</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {files.length === 0 ? (
              <TableRow>
                <TableCell colSpan={5} align="center">
                  <Typography sx={{ py: 2 }}>
                    No processed files found
                  </Typography>
                </TableCell>
              </TableRow>
            ) : (
              files.map((file) => (
                <TableRow key={file.id}>
                  <TableCell>{file.filename}</TableCell>
                  <TableCell>{getStatusChip(file.status)}</TableCell>
                  <TableCell>
                    {new Date(file.processedDate).toLocaleString()}
                  </TableCell>
                  <TableCell>{file.recordsProcessed || 'N/A'}</TableCell>
                  <TableCell>
                    {file.errorMessage && (
                      <Tooltip title={file.errorMessage}>
                        <IconButton size="small" color="error">
                          <InfoIcon />
                        </IconButton>
                      </Tooltip>
                    )}
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
};

export default ProcessedFiles; 