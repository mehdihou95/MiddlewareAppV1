export interface Client {
  id: number;
  name: string;
  code: string;
  status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';
  description?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Interface {
  id: number;
  name: string;
  clientId: number;
  type: string;
  description?: string;
  isActive: boolean;
  priority?: number;
  rootElement?: string;
  namespace?: string;
  schemaPath?: string;
  createdAt?: string;
  updatedAt?: string;
  configuration?: {
    xsdPath?: string;
    rootElement?: string;
    namespace?: string;
    schemaPath?: string;
  };
}

export interface MappingRule {
  id?: number;
  clientId: number;
  interfaceId: number;
  xmlPath: string;
  databaseField: string;
  xsdElement: string;
  tableName: string;
  dataType: string;
  isAttribute: boolean;
  description: string;
}

export interface ProcessedFile {
  id: number;
  filename: string;
  processedDate: string;
  status: string;
  recordsProcessed: number;
  clientId: number;
  clientName: string;
  interfaceId: number;
  interfaceName: string;
} 