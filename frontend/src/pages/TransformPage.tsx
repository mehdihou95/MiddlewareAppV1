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
} from '@mui/material';
import axios, { AxiosError } from 'axios';
import RefreshIcon from '@mui/icons-material/Refresh';

interface XmlElement {
  name: string;
  path: string;
  type: string;
  isAttribute: boolean;
}

interface MappingRule {
  id?: number;
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
  const [xmlElements, setXmlElements] = useState<XmlElement[]>([]);
  const [dbFields] = useState<{ field: string; type: string }[]>([
    { field: 'ASN_HEADER.asn_number', type: 'String' },
    { field: 'ASN_HEADER.shipment_date', type: 'LocalDate' },
    { field: 'ASN_HEADER.supplier_id', type: 'String' },
    { field: 'ASN_HEADER.supplier_name', type: 'String' },
    { field: 'ASN_LINE.line_number', type: 'Integer' },
    { field: 'ASN_LINE.item_number', type: 'String' },
    { field: 'ASN_LINE.quantity', type: 'Decimal' },
    { field: 'ASN_LINE.uom', type: 'String' },
    { field: 'ASN_LINE.description', type: 'String' }
  ]);
  const [selectedXmlElement, setSelectedXmlElement] = useState<XmlElement | null>(null);
  const [selectedDbField, setSelectedDbField] = useState('');
  const [mappingRules, setMappingRules] = useState<MappingRule[]>([]);
  const [openDialog, setOpenDialog] = useState(false);
  const [newMapping, setNewMapping] = useState<MappingRule>({
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

  useEffect(() => {
    loadXsdStructure();
    loadMappingRules();
  }, []);

  const loadXsdStructure = async () => {
    try {
      setXmlElements([]); // Clear existing elements
      console.log('Loading XSD structure...');
      const response = await axios.get('http://localhost:8080/api/mapping/xsd-structure', {
        params: { xsdPath: 'asn.xsd' },
        withCredentials: true
      });
      console.log('XSD structure loaded:', response.data);
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
        // Try to extract the detailed error message from the response
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
    }
  };

  const loadMappingRules = async () => {
    try {
      const response = await axios.get<MappingRule[]>('http://localhost:8080/api/mapping/rules', {
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
    }
  };

  const handleXmlElementClick = (element: XmlElement) => {
    setSelectedXmlElement(element);
  };

  const handleDbFieldClick = (field: string) => {
    setSelectedDbField(field);
    if (selectedXmlElement) {
      const isAttribute = selectedXmlElement.path.includes('@');
      const [tableName, fieldName] = field.split('.');
      const dbField = dbFields.find(f => f.field === field);
      
      setNewMapping({
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
    try {
      await axios.post('http://localhost:8080/api/mapping/save-configuration', mappingRules, {
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
          >
            Refresh XSD
          </Button>
          <Button
            variant="contained"
            color="primary"
            onClick={handleSaveAllMappings}
            disabled={mappingRules.length === 0}
          >
            Save Configuration
          </Button>
        </Box>
      </Box>
      
      <Box sx={{ display: 'flex', gap: 2 }}>
        {/* XML Elements Panel */}
        <Paper sx={{ flex: 1, p: 2, maxHeight: '400px', overflow: 'auto' }}>
          <Typography variant="h6" gutterBottom>XML Elements</Typography>
          <List>
            {xmlElements.map((element, index) => (
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
            ))}
          </List>
        </Paper>

        {/* Database Fields Panel */}
        <Paper sx={{ flex: 1, p: 2, maxHeight: '400px', overflow: 'auto' }}>
          <Typography variant="h6" gutterBottom>Database Fields</Typography>
          <List>
            {dbFields.map((field, index) => (
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
            ))}
          </List>
        </Paper>
      </Box>

      {/* Mapping Rules Table */}
      <Paper sx={{ p: 2, mt: 2 }}>
        <Typography variant="h6" gutterBottom>Mapping Rules</Typography>
        <List>
          {mappingRules.map((rule: MappingRule) => renderMappingRule(rule))}
        </List>
      </Paper>

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