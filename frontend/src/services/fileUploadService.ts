import axios from 'axios';
import { ProcessedFile } from '../types';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

export const fileUploadService = {
  uploadFile: async (file: File, clientId: number, interfaceId: number): Promise<ProcessedFile> => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('clientId', clientId.toString());
    formData.append('interfaceId', interfaceId.toString());

    const response = await axios.post(`${API_URL}/upload`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    return response.data;
  },

  // Add method to check file size before upload
  validateFileSize: (file: File, maxSize: number = 10 * 1024 * 1024): boolean => {
    return file.size <= maxSize;
  },

  // Add method to check file type
  validateFileType: (file: File): boolean => {
    const allowedTypes = ['application/xml', 'text/xml'];
    return allowedTypes.includes(file.type);
  }
}; 