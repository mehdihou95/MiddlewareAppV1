# XML Processor Middleware Application - Technical Specification

## 1. System Architecture

### 1.1 Frontend Architecture
- Framework: React with TypeScript
- State Management: React Context API
- UI Components: Material-UI (MUI)
- HTTP Client: Axios
- Development Server: Node.js
- Build Tool: Create React App

### 1.2 Backend Architecture
- Framework: Spring Boot
- Language: Java 17
- Database: H2 (embedded)
- XML Processing: JAXP (Java API for XML Processing)
- Build Tool: Maven
- Server: Embedded Tomcat

### 1.3 System Components
- Frontend Application (Port 3000)
- Backend API Server (Port 8080)
- H2 Database Console (Port 8080/h2-console)
- File System Storage for XML/XSD

## 2. Frontend Implementation

### 2.1 Core Components
- App.tsx: Main application component and routing
- AuthContext.tsx: Authentication context provider
- Login.tsx: User authentication interface
- TransformPage.tsx: XML mapping configuration
- ProcessedFiles.tsx: History view component
- UploadPage.tsx: File upload interface

### 2.2 Features
- Protected routes with authentication
- Real-time file processing status
- Dynamic XML element mapping
- Auto-refreshing history view
- Responsive layout design
- Error handling and user feedback

### 2.3 API Integration
- Authentication endpoints
- File upload API
- XML processing status API
- Mapping configuration API
- History retrieval API

## 3. Backend Implementation

### 3.1 Controllers
- AuthController: Authentication management
- FileController: File upload handling
- MappingController: Mapping configuration
- ProcessingController: XML processing
- StatusController: Processing status

### 3.2 Services
- XmlProcessorService: XML file processing
- XsdService: XSD structure parsing
- MappingService: Mapping rule management
- AuthService: User authentication
- FileService: File operations

### 3.3 Models
- AsnHeader: ASN header information
- AsnLine: ASN line items
- ProcessedFile: File processing records
- MappingRule: XML-to-DB mapping rules
- User: User authentication data

## 4. Database Schema

### 4.1 Tables
- ASN_HEADER
  - id (PK)
  - asn_number
  - shipment_date
  - supplier_id
  - supplier_name

- ASN_LINE
  - id (PK)
  - header_id (FK)
  - line_number
  - item_number
  - quantity
  - uom
  - description

- PROCESSED_FILE
  - id (PK)
  - filename
  - processed_date
  - status
  - error_message

- MAPPING_RULE
  - id (PK)
  - xml_path
  - database_field
  - table_name
  - data_type
  - is_attribute
  - description

- USER
  - id (PK)
  - username
  - password
  - role

## 5. API Endpoints

### 5.1 Authentication
- POST /api/auth/login
- POST /api/auth/logout
- GET /api/auth/status

### 5.2 File Processing
- POST /api/upload
- GET /api/files/status/{id}
- GET /api/files/history

### 5.3 Mapping Configuration
- GET /api/mapping/xsd-structure
- GET /api/mapping/rules
- POST /api/mapping/rules
- DELETE /api/mapping/rules/{id}
- POST /api/mapping/save-configuration

## 6. Security Implementation

### 6.1 Authentication
- Session-based authentication
- Password encryption
- Role-based access control
- CSRF protection
- Secure session management

### 6.2 Data Security
- Input validation
- XML external entity prevention
- SQL injection protection
- File upload validation
- Secure error handling

## 7. Error Handling

### 7.1 Frontend Error Handling
- API error interceptors
- User feedback components
- Form validation
- File type validation
- Network error handling

### 7.2 Backend Error Handling
- Global exception handler
- Custom error responses
- Validation error handling
- File processing errors
- Database operation errors

## 8. Testing Strategy

### 8.1 Frontend Testing
- Component unit tests
- Integration tests
- End-to-end tests
- User interface testing
- Cross-browser testing

### 8.2 Backend Testing
- Unit tests for services
- Integration tests
- API endpoint testing
- XML processing tests
- Database operation tests

## 9. Deployment

### 9.1 Development Environment
- Local development setup
- H2 database configuration
- Development server settings
- Hot reload configuration
- Debug logging

### 9.2 Production Environment
- Production build process
- Database configuration
- Server deployment
- Environment variables
- Logging configuration

## 10. Monitoring and Maintenance

### 10.1 Logging
- Application logs
- Error tracking
- Performance monitoring
- User activity logging
- System health checks

### 10.2 Maintenance
- Database backups
- Log rotation
- Performance optimization
- Security updates
- Bug fixes and patches 