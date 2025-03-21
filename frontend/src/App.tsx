import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import Container from '@mui/material/Container';
import theme from './theme';
import Navigation from './components/Navigation';
import Login from './components/Login';
import FileUpload from './components/FileUpload';
import ProcessedFiles from './components/ProcessedFiles';
import PrivateRoute from './components/PrivateRoute';
import TransformPage from './pages/TransformPage';
import { AuthProvider } from './context/AuthContext';

const App = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <Router>
          <Navigation />
          <Container sx={{ mt: 4 }}>
            <Routes>
              <Route 
                path="/login" 
                element={
                  <Login />
                } 
              />
              <Route
                path="/"
                element={
                  <PrivateRoute>
                    <FileUpload />
                  </PrivateRoute>
                }
              />
              <Route
                path="/history"
                element={
                  <PrivateRoute>
                    <ProcessedFiles />
                  </PrivateRoute>
                }
              />
              <Route
                path="/transform"
                element={
                  <PrivateRoute>
                    <TransformPage />
                  </PrivateRoute>
                }
              />
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </Container>
        </Router>
      </AuthProvider>
    </ThemeProvider>
  );
};

export default App; 