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
  IconButton,
  Alert,
  CircularProgress,
  Chip,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Grid,
  TablePagination,
  Button,
} from '@mui/material';
import RefreshIcon from '@mui/icons-material/Refresh';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { useClientInterface } from '../context/ClientInterfaceContext';
import { processedFileService } from '../services/processedFileService';
import ClientInterfaceSelector from './ClientInterfaceSelector';

interface ProcessedFile {
  id: number;
  filename: string;
  processedDate: string;
  status: string;
  recordsProcessed: number;
  clientId: number;
  clientName: string;
  interfaceId: number;
  interfaceName: string;
}

const ProcessedFiles: React.FC = () => {
  const { selectedClient, selectedInterface } = useClientInterface();
  const [files, setFiles] = useState<ProcessedFile[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [totalCount, setTotalCount] = useState(0);
  const [statusFilter, setStatusFilter] = useState<string>('');
  const [dateRange, setDateRange] = useState<{
    startDate: Date | null;
    endDate: Date | null;
  }>({
    startDate: null,
    endDate: null,
  });

  const loadFiles = async () => {
    if (!selectedClient || !selectedInterface) {
      setFiles([]);
      setTotalCount(0);
      return;
    }

    try {
      setLoading(true);
      const response = await processedFileService.getProcessedFiles(
        selectedClient.id,
        selectedInterface.id
      );

      // Filter files based on status and date range
      let filteredFiles = response;

      if (statusFilter) {
        filteredFiles = filteredFiles.filter(file => file.status === statusFilter);
      }

      if (dateRange.startDate) {
        filteredFiles = filteredFiles.filter(
          file => new Date(file.processedDate) >= dateRange.startDate!
        );
      }

      if (dateRange.endDate) {
        filteredFiles = filteredFiles.filter(
          file => new Date(file.processedDate) <= dateRange.endDate!
        );
      }

      setTotalCount(filteredFiles.length);

      // Handle pagination client-side
      const startIndex = page * rowsPerPage;
      const endIndex = startIndex + rowsPerPage;
      const paginatedFiles = filteredFiles.slice(startIndex, endIndex);
      
      setFiles(paginatedFiles);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load processed files');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadFiles();
  }, [selectedClient, selectedInterface, page, rowsPerPage, statusFilter, dateRange]);

  const handleChangePage = (event: unknown, newPage: number) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event: React.ChangeEvent<HTMLInputElement>) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleRefresh = () => {
    loadFiles();
  };

  return (
    <Box sx={{ maxWidth: 1200, mx: 'auto', p: 3 }}>
      <Typography variant="h4" gutterBottom>
        Processed Files
      </Typography>

      <ClientInterfaceSelector required />

      {!selectedClient || !selectedInterface ? (
        <Alert severity="info" sx={{ mt: 2 }}>
          Please select both a client and an interface to view processed files
        </Alert>
      ) : (
        <>
          <Grid container spacing={2} sx={{ my: 2 }}>
            <Grid item xs={12} sm={4}>
              <FormControl fullWidth>
                <InputLabel>Status Filter</InputLabel>
                <Select
                  value={statusFilter}
                  label="Status Filter"
                  onChange={(e) => setStatusFilter(e.target.value)}
                >
                  <MenuItem value="">All</MenuItem>
                  <MenuItem value="COMPLETED">Completed</MenuItem>
                  <MenuItem value="FAILED">Failed</MenuItem>
                  <MenuItem value="PROCESSING">Processing</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={4}>
              <LocalizationProvider dateAdapter={AdapterDateFns}>
                <DatePicker
                  label="Start Date"
                  value={dateRange.startDate}
                  onChange={(date) =>
                    setDateRange((prev) => ({ ...prev, startDate: date }))
                  }
                />
              </LocalizationProvider>
            </Grid>
            <Grid item xs={12} sm={4}>
              <LocalizationProvider dateAdapter={AdapterDateFns}>
                <DatePicker
                  label="End Date"
                  value={dateRange.endDate}
                  onChange={(date) =>
                    setDateRange((prev) => ({ ...prev, endDate: date }))
                  }
                />
              </LocalizationProvider>
            </Grid>
          </Grid>

          <Box sx={{ display: 'flex', justifyContent: 'flex-end', mb: 2 }}>
            <Button
              startIcon={<RefreshIcon />}
              onClick={handleRefresh}
              variant="outlined"
            >
              Refresh
            </Button>
          </Box>

          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          {loading ? (
            <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
              <CircularProgress />
            </Box>
          ) : (
            <>
              <TableContainer component={Paper}>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>Filename</TableCell>
                      <TableCell>Processed Date</TableCell>
                      <TableCell>Status</TableCell>
                      <TableCell>Records Processed</TableCell>
                      <TableCell>Client</TableCell>
                      <TableCell>Interface</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {files.length === 0 ? (
                      <TableRow>
                        <TableCell colSpan={6} align="center">
                          <Typography sx={{ py: 2 }}>
                            No processed files found matching the current filters
                          </Typography>
                        </TableCell>
                      </TableRow>
                    ) : (
                      files.map((file) => (
                        <TableRow key={file.id}>
                          <TableCell>{file.filename}</TableCell>
                          <TableCell>
                            {new Date(file.processedDate).toLocaleString()}
                          </TableCell>
                          <TableCell>
                            <Chip
                              label={file.status}
                              color={
                                file.status === 'COMPLETED'
                                  ? 'success'
                                  : file.status === 'FAILED'
                                  ? 'error'
                                  : 'default'
                              }
                              size="small"
                            />
                          </TableCell>
                          <TableCell>{file.recordsProcessed}</TableCell>
                          <TableCell>{file.clientName}</TableCell>
                          <TableCell>{file.interfaceName}</TableCell>
                        </TableRow>
                      ))
                    )}
                  </TableBody>
                </Table>
              </TableContainer>

              <TablePagination
                component="div"
                count={totalCount}
                page={page}
                onPageChange={handleChangePage}
                rowsPerPage={rowsPerPage}
                onRowsPerPageChange={handleChangeRowsPerPage}
                rowsPerPageOptions={[5, 10, 25, 50]}
              />
            </>
          )}
        </>
      )}
    </Box>
  );
};

export default ProcessedFiles; 