import React, { useState } from 'react';
import { useDropzone } from 'react-dropzone';
import {
  Box,
  Paper,
  Typography,
  Button,
  CircularProgress,
  Alert,
} from '@mui/material';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import axios from 'axios';
import { useClientInterface } from '../context/ClientInterfaceContext';
import ClientInterfaceSelector from './ClientInterfaceSelector';

const FileUpload = () => {
  const { selectedClient, selectedInterface } = useClientInterface();
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const onDrop = async (acceptedFiles: File[]) => {
    if (!selectedClient || !selectedInterface) {
      setError('Please select a client and interface before uploading files');
      return;
    }

    setError(null);
    setSuccess(null);
    setUploading(true);

    const formData = new FormData();
    acceptedFiles.forEach((file) => {
      formData.append('file', file);
    });
    formData.append('clientId', selectedClient.id.toString());
    formData.append('interfaceId', selectedInterface.id.toString());

    try {
      await axios.post('/api/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        withCredentials: true
      });
      setSuccess(`Successfully uploaded ${acceptedFiles.length} file(s)`);
    } catch (err) {
      const axiosError = err as any;
      let errorMessage = 'Failed to upload files. Please try again.';
      if (axiosError.response?.data?.message) {
        errorMessage = axiosError.response.data.message;
      }
      setError(errorMessage);
    } finally {
      setUploading(false);
    }
  };

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: {
      'application/xml': ['.xml'],
    },
    disabled: !selectedClient || !selectedInterface,
  });

  return (
    <Box sx={{ maxWidth: 600, mx: 'auto', textAlign: 'center' }}>
      <Typography variant="h4" gutterBottom>
        Upload XML Files
      </Typography>

      <ClientInterfaceSelector required />

      {!selectedClient || !selectedInterface ? (
        <Alert severity="info" sx={{ mt: 2 }}>
          Please select a client and interface before uploading files
        </Alert>
      ) : (
        <Paper
          {...getRootProps()}
          sx={{
            p: 6,
            mt: 3,
            border: '2px dashed',
            borderColor: isDragActive ? 'primary.main' : 'grey.300',
            backgroundColor: isDragActive ? 'action.hover' : 'background.paper',
            cursor: 'pointer',
            opacity: uploading ? 0.7 : 1,
            pointerEvents: uploading ? 'none' : 'auto',
          }}
        >
          <input {...getInputProps()} />
          <CloudUploadIcon sx={{ fontSize: 48, color: 'primary.main', mb: 2 }} />
          <Typography variant="h6" gutterBottom>
            {isDragActive
              ? 'Drop the files here...'
              : 'Drag and drop XML files here, or click to select files'}
          </Typography>
          <Typography variant="body2" color="textSecondary">
            Only XML files are accepted
          </Typography>
        </Paper>
      )}

      {uploading && (
        <Box sx={{ mt: 3, display: 'flex', justifyContent: 'center' }}>
          <CircularProgress />
        </Box>
      )}

      {error && (
        <Alert severity="error" sx={{ mt: 3 }}>
          {error}
        </Alert>
      )}

      {success && (
        <Alert severity="success" sx={{ mt: 3 }}>
          {success}
        </Alert>
      )}

      {selectedClient && selectedInterface && (
        <Button
          variant="contained"
          color="primary"
          sx={{ mt: 3 }}
          onClick={() => document.querySelector('input')?.click()}
          disabled={uploading}
        >
          Select Files
        </Button>
      )}
    </Box>
  );
};

export default FileUpload; 