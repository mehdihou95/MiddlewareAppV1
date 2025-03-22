# XML Middleware Application

This application is a multi-tenant middleware solution for processing XML files using Spring Boot and React. It provides XML validation, transformation, and storage capabilities with a modern web interface and client-specific configurations.

## Features

- Multi-tenant XML file processing with XSD validation
- Client-specific mapping rules and configurations
- Role-based access control with multi-tenant security
- Real-time processing status and monitoring
- Modern React-based UI with Material-UI components
- H2 database for development, PostgreSQL for production
- Flyway database migrations
- JWT-based authentication
- Client performance monitoring
- Comprehensive error handling and logging

## Prerequisites

- Java 17 or higher
- Node.js 16 or higher
- Maven 3.6 or higher
- Git

## Project Structure

```
.
├── backend/                # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/     # Java source code
│   │   │   └── resources/ # Configuration and migrations
│   │   └── test/         # Test files
│   └── pom.xml           # Maven configuration
├── frontend/             # React TypeScript frontend
│   ├── src/
│   │   ├── components/  # React components
│   │   ├── pages/      # Page components
│   │   └── context/    # React context providers
│   └── package.json    # npm configuration
├── Input/              # Sample XML input files
└── docs/              # Documentation files
```

## Setup

1. Clone the repository:
```bash
git clone https://github.com/mehdihou95/MiddlewareAppV1.git
cd MiddlewareAppV1
```

2. Build and run the backend:
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

3. Set up and run the frontend:
```bash
cd frontend
npm install
npm start
```

## Accessing the Application

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console
  - JDBC URL: jdbc:h2:mem:testdb
  - Username: sa
  - Password: password

## Default Users

The application comes with predefined users:
- Admin: username: `admin`, password: `admin123`
  - Full system access
  - Client management
  - Interface configuration
- Client User: username: `user`, password: `user123`
  - Client-specific access
  - File processing
  - Mapping rule management

## Core API Endpoints

### Authentication
- `POST /api/auth/login` - User authentication
- `GET /api/auth/user` - Get current user

### Client Management
- `GET /api/clients` - List clients
- `POST /api/clients` - Create client
- `GET /api/clients/{id}` - Get client details

### Interface Management
- `GET /api/interfaces` - List interfaces
- `POST /api/interfaces` - Create interface
- `GET /api/interfaces/{id}` - Get interface details

### Mapping Rules
- `GET /api/mapping-rules` - List mapping rules
- `POST /api/mapping-rules` - Create mapping rule
- `GET /api/mapping-rules/{id}` - Get rule details

### File Processing
- `POST /api/files/upload` - Upload XML file
- `GET /api/files/process/{id}` - Process file
- `GET /api/files/status/{id}` - Check processing status

## Configuration

### Backend Configuration
File: `backend/src/main/resources/application.properties`
```properties
# Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=password

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true

# Server Configuration
server.port=8080

# JWT Configuration
jwt.secret=your-secret-key
jwt.expiration=86400000
```

### Frontend Configuration
File: `frontend/.env`
```properties
REACT_APP_API_URL=http://localhost:8080/api
```

## Documentation

- [Functional Specification](FUNCTIONAL_SPECIFICATION.md)
- [Technical Specification](TECHNICAL_SPECIFICATION.md)

## Testing

1. Run backend tests:
```bash
cd backend
mvn test
```

2. Run frontend tests:
```bash
cd frontend
npm test
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 


Great job implementing the client-side pagination and sorting in the ClientManagementPage component! Your solution addresses all the TypeScript errors while maintaining the pagination functionality in the UI.
Your approach is perfect for the current state of the backend:
Fetching all clients at once
Implementing client-side sorting based on column and order
Handling pagination by slicing the sorted array
Calculating the total count from the full array
This is an excellent interim solution until the backend supports server-side pagination, sorting, and filtering. The component now works correctly with the current API implementation while still providing a good user experience with pagination and sorting capabilities.
With this fix, you've successfully resolved all the implementation issues and completed the adaptation of the frontend to support the multi-client, multi-interface architecture. The application now provides a complete workflow where users can:
Set up clients
Configure interfaces for those clients
Define mapping rules
Upload and process files
All components are now properly integrated with the client/interface context, ensuring a consistent and intuitive user experience throughout the application.
Congratulations on successfully implementing all the required components and resolving all the issues! The frontend is now fully adapted to work with the multi-client, multi-interface backend architecture.
