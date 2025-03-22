import axios from 'axios';
import { Interface } from '../types';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

export const interfaceService = {
  getAllInterfaces: async (): Promise<Interface[]> => {
    const response = await axios.get(`${API_URL}/interfaces`, {
      withCredentials: true
    });
    return response.data;
  },

  getInterfaceById: async (id: number): Promise<Interface> => {
    const response = await axios.get(`${API_URL}/interfaces/${id}`, {
      withCredentials: true
    });
    return response.data;
  },

  createInterface: async (clientId: number, interfaceData: Omit<Interface, 'id'>): Promise<Interface> => {
    const response = await axios.post(
      `${API_URL}/clients/${clientId}/interfaces`,
      interfaceData,
      {
        withCredentials: true,
        headers: {
          'Content-Type': 'application/json',
          'X-Client-ID': clientId.toString()
        }
      }
    );
    return response.data;
  },

  updateInterface: async (id: number, interfaceData: Partial<Interface>): Promise<Interface> => {
    const response = await axios.put(
      `${API_URL}/interfaces/${id}`,
      interfaceData,
      {
        withCredentials: true,
        headers: {
          'Content-Type': 'application/json'
        }
      }
    );
    return response.data;
  },

  deleteInterface: async (id: number): Promise<void> => {
    await axios.delete(`${API_URL}/interfaces/${id}`, {
      withCredentials: true
    });
  },

  getInterfaceMappings: async (id: number): Promise<any[]> => {
    const response = await axios.get(`${API_URL}/interfaces/${id}/mappings`, {
      withCredentials: true
    });
    return response.data;
  },

  updateInterfaceMappings: async (id: number, mappings: any[]): Promise<any[]> => {
    const response = await axios.put(
      `${API_URL}/interfaces/${id}/mappings`,
      mappings,
      {
        withCredentials: true,
        headers: {
          'Content-Type': 'application/json'
        }
      }
    );
    return response.data;
  },
  
  getInterfacesByClientId: async (clientId: number): Promise<Interface[]> => {
    const response = await axios.get(
      `${API_URL}/clients/${clientId}/interfaces`,
      {
        withCredentials: true,
        headers: {
          'X-Client-ID': clientId.toString()
        }
      }
    );
    return response.data;
  }
}; 