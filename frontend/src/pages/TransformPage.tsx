import React, { useState, useEffect } from 'react';
import {
  Box,
  Paper,
  Typography,
  List,
  ListItem,
  ListItemText,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Snackbar,
  Alert,
  CircularProgress,
} from '@mui/material';
import axios, { AxiosError } from 'axios';
import RefreshIcon from '@mui/icons-material/Refresh';
import { useClientInterface } from '../context/ClientInterfaceContext';
import ClientInterfaceSelector from '../components/ClientInterfaceSelector';

interface XmlElement {
  name: string;
  path: string;
  type: string;
  isAttribute: boolean;
}

interface MappingRule {
  id?: number;
  clientId: number;
  interfaceId: number;
  xmlPath: string;
  databaseField: string;
  xsdElement: string;
  tableName: string;
  dataType: string;
  isAttribute: boolean;
  description: string;
}

interface SnackbarState {
  open: boolean;
  message: string;
  severity: 'success' | 'error' | 'info' | 'warning';
}

const TransformPage: React.FC = () => {
  const { selectedClient, selectedInterface } = useClientInterface();
  const [xmlElements, setXmlElements] = useState<XmlElement[]>([]);
  const [dbFields, setDbFields] = useState<{ field: string; type: string }[]>([]);
  const [selectedXmlElement, setSelectedXmlElement] = useState<XmlElement | null>(null);
  const [selectedDbField, setSelectedDbField] = useState('');
  const [mappingRules, setMappingRules] = useState<MappingRule[]>([]);
  const [openDialog, setOpenDialog] = useState(false);
  const [newMapping, setNewMapping] = useState<MappingRule>({
    clientId: 0,
    interfaceId: 0,
    xmlPath: '',
    databaseField: '',
    xsdElement: '',
    tableName: '',
    dataType: '',
    isAttribute: false,
    description: ''
  });
  const [snackbar, setSnackbar] = useState<SnackbarState>({
    open: false,
    message: '',
    severity: 'info'
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (selectedClient && selectedInterface) {
      loadXsdStructure();
      loadMappingRules();
      loadDatabaseFields();
    }
  }, [selectedClient, selectedInterface]);

  const loadXsdStructure = async () => {
    if (!selectedClient || !selectedInterface) return;

    try {
      setLoading(true);
      setXmlElements([]); // Clear existing elements
      const response = await axios.get('http://localhost:8080/api/mapping/xsd-structure', {
        params: { 
          clientId: selectedClient.id,
          interfaceId: selectedInterface.id,
          xsdPath: selectedInterface.configuration?.xsdPath || 'asn.xsd'
        },
        withCredentials: true
      });

      if (Array.isArray(response.data) && response.data.length > 0) {
        setXmlElements(response.data);
      } else {
        throw new Error('Invalid XSD structure received');
      }
    } catch (error) {
      const axiosError = error as AxiosError;
      console.error('Error loading XSD structure:', axiosError);
      
      let errorMessage = 'Failed to load XML structure';
      if (axiosError.response?.data) {
        const data = axiosError.response.data as any;
        errorMessage = data.message || data.error || JSON.stringify(data);
      } else if (axiosError.message) {
        errorMessage = axiosError.message;
      }
      
      setSnackbar({
        open: true,
        message: errorMessage,
        severity: 'error'
      });
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const loadDatabaseFields = async () => {
    if (!selectedClient || !selectedInterface) return;

    try {
      setLoading(true);
      const response = await axios.get('http://localhost:8080/api/mapping/database-fields', {
        params: { 
          clientId: selectedClient.id,
          interfaceId: selectedInterface.id
        },
        withCredentials: true
      });
      setDbFields(response.data);
    } catch (error) {
      console.error('Error loading database fields:', error);
      setSnackbar({
        open: true,
        message: 'Failed to load database fields',
        severity: 'error'
      });
    } finally {
      setLoading(false);
    }
  };

  const loadMappingRules = async () => {
    if (!selectedClient || !selectedInterface) return;

    try {
      setLoading(true);
      const response = await axios.get<MappingRule[]>('http://localhost:8080/api/mapping/rules', {
        params: { 
          clientId: selectedClient.id,
          interfaceId: selectedInterface.id
        },
        withCredentials: true
      });
      setMappingRules(response.data);
    } catch (error) {
      console.error('Error loading mapping rules:', error);
      setSnackbar({
        open: true,
        message: 'Failed to load mapping rules',
        severity: 'error'
      });
    } finally {
      setLoading(false);
    }
  };

  const handleXmlElementClick = (element: XmlElement) => {
    setSelectedXmlElement(element);
  };

  const handleDbFieldClick = (field: string) => {
    setSelectedDbField(field);
    if (selectedXmlElement && selectedClient && selectedInterface) {
      const isAttribute = selectedXmlElement.path.includes('@');
      const [tableName, fieldName] = field.split('.');
      const dbField = dbFields.find(f => f.field === field);
      
      setNewMapping({
        clientId: selectedClient.id,
        interfaceId: selectedInterface.id,
        xmlPath: selectedXmlElement.path,
        databaseField: fieldName,
        xsdElement: selectedXmlElement.name,
        tableName: tableName,
        dataType: dbField?.type || 'String',
        isAttribute: isAttribute,
        description: `Map ${selectedXmlElement.name} to ${fieldName}`
      });
      setOpenDialog(true);
    }
  };

  const handleSaveMapping = async () => {
    if (!selectedClient || !selectedInterface) {
      setSnackbar({
        open: true,
        message: 'Please select a client and interface first',
        severity: 'error'
      });
      return;
    }

    try {
      if (!newMapping.xmlPath || !newMapping.databaseField) {
        setSnackbar({
          open: true,
          message: 'Please select both XML element and database field',
          severity: 'error'
        });
        return;
      }

      const response = await axios.post<MappingRule>('http://localhost:8080/api/mapping/rules', newMapping, {
        withCredentials: true
      });
      setMappingRules([...mappingRules, response.data]);
      setOpenDialog(false);
      setSelectedXmlElement(null);
      setSelectedDbField('');
      setSnackbar({
        open: true,
        message: 'Mapping rule saved successfully',
        severity: 'success'
      });
    } catch (error) {
      console.error('Error saving mapping rule:', error);
      setSnackbar({
        open: true,
        message: 'Failed to save mapping rule',
        severity: 'error'
      });
    }
  };

  const handleDeleteMapping = async (id: number) => {
    try {
      await axios.delete(`http://localhost:8080/api/mapping/rules/${id}`, {
        withCredentials: true
      });
      setMappingRules(mappingRules.filter(rule => rule.id !== id));
      setSnackbar({
        open: true,
        message: 'Mapping rule deleted successfully',
        severity: 'success'
      });
    } catch (error) {
      console.error('Error deleting mapping rule:', error);
      setSnackbar({
        open: true,
        message: 'Failed to delete mapping rule',
        severity: 'error'
      });
    }
  };

  const handleSaveAllMappings = async () => {
    if (!selectedClient || !selectedInterface) {
      setSnackbar({
        open: true,
        message: 'Please select a client and interface first',
        severity: 'error'
      });
      return;
    }

    try {
      await axios.post('http://localhost:8080/api/mapping/save-configuration', {
        clientId: selectedClient.id,
        interfaceId: selectedInterface.id,
        mappings: mappingRules
      }, {
        withCredentials: true
      });
      setSnackbar({
        open: true,
        message: 'Mapping configuration saved successfully',
        severity: 'success'
      });
    } catch (error) {
      console.error('Error saving mapping configuration:', error);
      setSnackbar({
        open: true,
        message: 'Failed to save mapping configuration',
        severity: 'error'
      });
    }
  };

  const handleRefreshXsd = async () => {
    try {
      setSnackbar({
        open: true,
        message: 'Refreshing XSD structure...',
        severity: 'info'
      });
      await loadXsdStructure();
      setSnackbar({
        open: true,
        message: 'XSD structure refreshed successfully',
        severity: 'success'
      });
    } catch (error) {
      console.error('Error refreshing XSD:', error);
      setSnackbar({
        open: true,
        message: 'Failed to refresh XSD structure',
        severity: 'error'
      });
    }
  };

  const renderMappingRule = (rule: MappingRule) => {
    return (
      <div key={rule.id} className="mapping-rule">
        <Typography>
          {rule.xmlPath} → {rule.tableName}.{rule.databaseField}
        </Typography>
        <Typography variant="body2" color="textSecondary">
          {rule.description || 'No description'}
        </Typography>
        <Button
          color="error"
          onClick={() => handleDeleteMapping(rule.id!)}
          style={{ float: 'right' }}
        >
          DELETE
        </Button>
      </div>
    );
  };

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', p: 3, gap: 2 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
        <Typography variant="h4">XML to Database Mapping</Typography>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button
            variant="outlined"
            color="primary"
            onClick={handleRefreshXsd}
            startIcon={<RefreshIcon />}
            disabled={!selectedClient || !selectedInterface}
          >
            Refresh XSD
          </Button>
          <Button
            variant="contained"
            color="primary"
            onClick={handleSaveAllMappings}
            disabled={!selectedClient || !selectedInterface || mappingRules.length === 0}
          >
            Save Configuration
          </Button>
        </Box>
      </Box>

      <ClientInterfaceSelector required />

      {!selectedClient || !selectedInterface ? (
        <Alert severity="info">
          Please select a client and interface to view and manage mapping rules
        </Alert>
      ) : loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
          <CircularProgress />
        </Box>
      ) : (
        <>
          <Box sx={{ display: 'flex', gap: 2 }}>
            {/* XML Elements Panel */}
            <Paper sx={{ flex: 1, p: 2, maxHeight: '400px', overflow: 'auto' }}>
              <Typography variant="h6" gutterBottom>XML Elements</Typography>
              <List>
                {xmlElements.length === 0 ? (
                  <ListItem>
                    <ListItemText primary="No XML elements available" />
                  </ListItem>
                ) : (
                  xmlElements.map((element, index) => (
                    <ListItem
                      key={index}
                      button
                      selected={selectedXmlElement?.path === element.path}
                      onClick={() => handleXmlElementClick(element)}
                    >
                      <ListItemText 
                        primary={element.name}
                        secondary={element.path}
                      />
                    </ListItem>
                  ))
                )}
              </List>
            </Paper>

            {/* Database Fields Panel */}
            <Paper sx={{ flex: 1, p: 2, maxHeight: '400px', overflow: 'auto' }}>
              <Typography variant="h6" gutterBottom>Database Fields</Typography>
              <List>
                {dbFields.length === 0 ? (
                  <ListItem>
                    <ListItemText primary="No database fields available" />
                  </ListItem>
                ) : (
                  dbFields.map((field, index) => (
                    <ListItem
                      key={index}
                      button
                      selected={selectedDbField === field.field}
                      onClick={() => handleDbFieldClick(field.field)}
                    >
                      <ListItemText 
                        primary={field.field}
                        secondary={`Type: ${field.type}`}
                      />
                    </ListItem>
                  ))
                )}
              </List>
            </Paper>
          </Box>

          {/* Mapping Rules Table */}
          <Paper sx={{ p: 2, mt: 2 }}>
            <Typography variant="h6" gutterBottom>Mapping Rules</Typography>
            <List>
              {mappingRules.length === 0 ? (
                <ListItem>
                  <ListItemText primary="No mapping rules defined yet" />
                </ListItem>
              ) : (
                mappingRules.map((rule: MappingRule) => renderMappingRule(rule))
              )}
            </List>
          </Paper>
        </>
      )}

      {/* Mapping Dialog */}
      <Dialog open={openDialog} onClose={() => setOpenDialog(false)}>
        <DialogTitle>Create Mapping Rule</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            margin="normal"
            label="XML Path"
            value={newMapping.xmlPath}
            disabled
          />
          <TextField
            fullWidth
            margin="normal"
            label="Database Field"
            value={newMapping.databaseField}
            disabled
          />
          <TextField
            fullWidth
            margin="normal"
            label="Description"
            value={newMapping.description}
            onChange={(e) => setNewMapping({...newMapping, description: e.target.value})}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
          <Button onClick={handleSaveMapping} color="primary">Save</Button>
        </DialogActions>
      </Dialog>

      {/* Snackbar for notifications */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={6000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
      >
        <Alert severity={snackbar.severity} onClose={() => setSnackbar({ ...snackbar, open: false })}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default TransformPage;