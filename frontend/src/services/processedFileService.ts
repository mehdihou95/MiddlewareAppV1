import axios from 'axios';
import { ProcessedFile } from '../types';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

export const processedFileService = {
  getProcessedFiles: async (clientId: number, interfaceId: number): Promise<ProcessedFile[]> => {
    const response = await axios.get(
      `${API_URL}/files/processed?clientId=${clientId}&interfaceId=${interfaceId}`,
      { withCredentials: true }
    );
    return response.data;
  },
}; 