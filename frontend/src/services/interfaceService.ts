import axios from 'axios';
import { Interface } from '../types';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

export const interfaceService = {
  getAllInterfaces: async () : Promise<Interface[]> => {
    const response = await axios.get(`${API_URL}/interfaces`);
    return response.data;
  },

  getInterfaceById: async (id: number): Promise<Interface> => {
    const response = await axios.get(`${API_URL}/interfaces/${id}`);
    return response.data;
  },

  createInterface: async (interfaceData: Omit<Interface, 'id'>): Promise<Interface> => {
    const response = await axios.post(`${API_URL}/interfaces`, interfaceData);
    return response.data;
  },

  updateInterface: async (id: number, interfaceData: Partial<Interface>): Promise<Interface> => {
    const response = await axios.put(`${API_URL}/interfaces/${id}`, interfaceData);
    return response.data;
  },

  deleteInterface: async (id: number): Promise<void> => {
    await axios.delete(`${API_URL}/interfaces/${id}`);
  },

  getInterfaceMappings: async (id: number): Promise<any[]> => {
    const response = await axios.get(`${API_URL}/interfaces/${id}/mappings`);
    return response.data;
  },

  updateInterfaceMappings: async (id: number, mappings: any[]): Promise<any[]> => {
    const response = await axios.put(`${API_URL}/interfaces/${id}/mappings`, mappings);
    return response.data;
  },
  
  getInterfacesByClientId: async (clientId: number): Promise<Interface[]> => {
    const response = await axios.get(`${API_URL}/clients/${clientId}/interfaces`);
    return response.data;
  }
}; 