import axios from 'axios';
import { Client, Interface } from '../types';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

export const clientService = {
  getAllClients: async (): Promise<Client[]> => {
    const response = await axios.get(`${API_URL}/clients`, {
      withCredentials: true
    });
    return response.data;
  },

  getClientById: async (id: number): Promise<Client> => {
    const response = await axios.get(`${API_URL}/clients/${id}`, {
      withCredentials: true
    });
    return response.data;
  },

  createClient: async (clientData: Omit<Client, 'id'>): Promise<Client> => {
    const response = await axios.post(`${API_URL}/clients`, clientData, {
      withCredentials: true
    });
    return response.data;
  },

  updateClient: async (id: number, clientData: Partial<Client>): Promise<Client> => {
    const response = await axios.put(`${API_URL}/clients/${id}`, clientData, {
      withCredentials: true
    });
    return response.data;
  },

  deleteClient: async (id: number): Promise<void> => {
    await axios.delete(`${API_URL}/clients/${id}`, {
      withCredentials: true
    });
  },

  getClientInterfaces: async (id: number): Promise<Interface[]> => {
    const response = await axios.get(`${API_URL}/clients/${id}/interfaces`, {
      withCredentials: true
    });
    return response.data;
  },

  onboardNewClient: async (clientData: Omit<Client, 'id'>): Promise<Client> => {
    const response = await axios.post(`${API_URL}/client-onboarding`, clientData, {
      withCredentials: true
    });
    return response.data;
  },

  cloneClientConfiguration: async (sourceClientId: number, newClientData: Omit<Client, 'id'>): Promise<Client> => {
    const response = await axios.post(
      `${API_URL}/client-onboarding/clone/${sourceClientId}`, 
      newClientData,
      {
        withCredentials: true
      }
    );
    return response.data;
  }
}; 