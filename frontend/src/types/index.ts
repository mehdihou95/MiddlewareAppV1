export interface Client {
  id: number;
  name: string;
  description?: string;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Interface {
  id: number;
  name: string;
  description?: string;
  schemaPath?: string;
  clientId: number;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface MappingRule {
  id: number;
  sourceField: string;
  targetField: string;
  transformationType: string;
  interfaceId: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface ProcessedFile {
  id: number;
  fileName: string;
  status: string;
  clientId: number;
  interfaceId: number;
  processedAt?: string;
  errorMessage?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface User {
  id: number;
  username: string;
  password?: string;
  email: string;
  firstName: string;
  lastName: string;
  enabled: boolean;
  roles: string[];
  createdAt?: string;
  updatedAt?: string;
  lastLogin?: string;
  failedLoginAttempts?: number;
  accountLocked: boolean;
  passwordResetToken?: string;
  passwordResetExpiry?: string;
}

export interface PageResponse<T> {
  content: T[];
  pageable: {
    sort: {
      sorted: boolean;
      unsorted: boolean;
      empty: boolean;
    };
    pageNumber: number;
    pageSize: number;
    offset: number;
    paged: boolean;
    unpaged: boolean;
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
  sort: {
    sorted: boolean;
    unsorted: boolean;
    empty: boolean;
  };
  numberOfElements: number;
  size: number;
  number: number;
  empty: boolean;
}

export interface AuditLog {
  id: number;
  action: string;
  username: string;
  clientId?: number;
  details: string;
  ipAddress: string;
  userAgent?: string;
  requestMethod?: string;
  requestUrl?: string;
  requestParams?: string;
  responseStatus?: number;
  errorMessage?: string;
  createdAt: string;
  executionTime?: number;
} 