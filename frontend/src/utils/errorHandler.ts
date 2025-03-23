import { AxiosError } from 'axios';

export interface ErrorResponse {
    status: number;
    message: string;
    details: string;
    timestamp: string;
    validationErrors?: string[];
}

export const handleApiError = (error: unknown, setError: (message: string) => void) => {
    if (error instanceof AxiosError) {
        const errorResponse = error.response?.data as ErrorResponse;
        
        if (errorResponse.validationErrors) {
            // Handle validation errors
            setError(errorResponse.validationErrors.join(', '));
        } else if (errorResponse.message) {
            // Handle specific error messages
            setError(errorResponse.message);
        } else {
            // Handle generic error messages
            switch (error.response?.status) {
                case 400:
                    setError('Invalid request. Please check your input.');
                    break;
                case 401:
                    setError('Authentication required. Please log in.');
                    break;
                case 403:
                    setError('Access denied. You do not have permission to perform this action.');
                    break;
                case 404:
                    setError('The requested resource was not found.');
                    break;
                case 409:
                    setError('A conflict occurred. The resource may already exist.');
                    break;
                case 422:
                    setError('The provided data is invalid.');
                    break;
                case 500:
                    setError('An internal server error occurred. Please try again later.');
                    break;
                default:
                    setError('An unexpected error occurred. Please try again later.');
            }
        }
    } else if (error instanceof Error) {
        // Handle non-Axios errors
        setError(error.message);
    } else {
        // Handle unknown errors
        setError('An unexpected error occurred. Please try again later.');
    }
};

export const isValidationError = (error: unknown): boolean => {
    if (error instanceof AxiosError) {
        const errorResponse = error.response?.data as ErrorResponse;
        return errorResponse.validationErrors !== undefined;
    }
    return false;
};

export const getValidationErrors = (error: unknown): string[] => {
    if (error instanceof AxiosError) {
        const errorResponse = error.response?.data as ErrorResponse;
        return errorResponse.validationErrors || [];
    }
    return [];
}; 