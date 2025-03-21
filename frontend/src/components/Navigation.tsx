import React from 'react';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import { AppBar, Toolbar, Typography, Button, Box, Divider, IconButton, Menu, MenuItem } from '@mui/material';
import UploadFileIcon from '@mui/icons-material/UploadFile';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ErrorIcon from '@mui/icons-material/Error';
import LoginIcon from '@mui/icons-material/Login';
import LogoutIcon from '@mui/icons-material/Logout';
import AdminPanelSettingsIcon from '@mui/icons-material/AdminPanelSettings';
import BrushIcon from '@mui/icons-material/Brush';
import TransformIcon from '@mui/icons-material/Transform';
import AccountCircle from '@mui/icons-material/AccountCircle';
import { useAuth } from '../context/AuthContext';
import HistoryIcon from '@mui/icons-material/History';

const Navigation = () => {
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const handleMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  if (!user?.authenticated) {
    return (
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            XML Processor
          </Typography>
          <Button
            color="inherit"
            component={RouterLink}
            to="/login"
            startIcon={<LoginIcon />}
          >
            Login
          </Button>
        </Toolbar>
      </AppBar>
    );
  }

  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          XML Processor
        </Typography>
        <Box sx={{ display: 'flex', gap: 2 }}>
          {/* File Processing Section */}
          <Button
            color="inherit"
            component={RouterLink}
            to="/"
            startIcon={<UploadFileIcon />}
            sx={{ mr: 2 }}
          >
            Upload
          </Button>
          <Button
            color="inherit"
            component={RouterLink}
            to="/history"
            startIcon={<HistoryIcon />}
            sx={{ mr: 2 }}
          >
            History
          </Button>
          <Button
            color="inherit"
            component={RouterLink}
            to="/transform"
            startIcon={<TransformIcon />}
            sx={{ mr: 2 }}
          >
            Transform
          </Button>

          <Divider orientation="vertical" flexItem sx={{ bgcolor: 'white' }} />

          {/* Admin Section */}
          {user.roles.includes('ADMIN') && (
            <Button
              color="inherit"
              component={RouterLink}
              to="/admin"
              startIcon={<AdminPanelSettingsIcon />}
            >
              Admin
            </Button>
          )}
          <Button
            color="inherit"
            component={RouterLink}
            to="/ux"
            startIcon={<BrushIcon />}
          >
            UX
          </Button>

          <Divider orientation="vertical" flexItem sx={{ bgcolor: 'white' }} />

          <IconButton
            size="large"
            aria-label="account of current user"
            aria-controls="menu-appbar"
            aria-haspopup="true"
            onClick={handleMenu}
            color="inherit"
          >
            <AccountCircle />
          </IconButton>
          <Menu
            id="menu-appbar"
            anchorEl={anchorEl}
            anchorOrigin={{
              vertical: 'top',
              horizontal: 'right',
            }}
            keepMounted
            transformOrigin={{
              vertical: 'top',
              horizontal: 'right',
            }}
            open={Boolean(anchorEl)}
            onClose={handleClose}
          >
            <MenuItem onClick={handleLogout}>Logout</MenuItem>
          </Menu>
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default Navigation; 