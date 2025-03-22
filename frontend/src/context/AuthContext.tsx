import React, { createContext, useContext, useState, useEffect } from 'react';
import axios from 'axios';
import qs from 'qs';

// Configure axios defaults
axios.defaults.withCredentials = true;
axios.defaults.baseURL = 'http://localhost:8080';

interface User {
  username: string;
  roles: string[];
  authenticated: boolean;
}

interface AuthContextType {
  user: User | null;
  loading: boolean;
  error: string | null;
  login: (username: string, password: string) => Promise<boolean>;
  logout: () => Promise<void>;
  isAuthenticated: boolean;
  hasRole: (role: string) => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const checkAuthStatus = async () => {
    try {
      const response = await axios.get('/api/user', {
        withCredentials: true,
        headers: {
          'Content-Type': 'application/json'
        }
      });
      if (response.status === 200 && response.data) {
        setUser({
          username: response.data.username,
          roles: response.data.roles.map((role: string) => role.replace('ROLE_', '')),
          authenticated: true
        });
      } else {
        setUser(null);
      }
    } catch (err) {
      // Silently handle initial auth check failure
      setUser(null);
    }
  };

  useEffect(() => {
    // Initial auth check without loading state
    checkAuthStatus();
  }, []);

  const login = async (username: string, password: string): Promise<boolean> => {
    try {
      setLoading(true);
      setError(null);

      const cleanUsername = username.trim();
      const cleanPassword = password.trim();

      if (!cleanUsername || !cleanPassword) {
        setError('Username and password are required');
        return false;
      }

      const data = qs.stringify({
        username: cleanUsername,
        password: cleanPassword
      });

      const response = await axios.post('/api/auth/login', data, {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        },
        withCredentials: true
      });

      if (response.status === 200) {
        await checkAuthStatus();
        return true;
      }
      return false;
    } catch (err: any) {
      console.error('Login error:', err.response || err);
      setError('Invalid username or password');
      setUser(null);
      return false;
    } finally {
      setLoading(false);
    }
  };

  const logout = async () => {
    try {
      await axios.post('/api/auth/logout', {}, { withCredentials: true });
      setUser(null);
      setError(null);
    } catch (err) {
      console.error('Logout error:', err);
      setError('Failed to logout');
    }
  };

  const hasRole = (role: string) => {
    return user?.roles.includes(role) ?? false;
  };

  const value = {
    user,
    loading,
    error,
    login,
    logout,
    isAuthenticated: !!user,
    hasRole,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export default AuthContext; 