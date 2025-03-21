# XML Processing Application

This application is a full-stack solution for processing XML files using Spring Boot, Apache Camel, and React. It provides XML validation, transformation, and storage capabilities with a modern web interface.

## Features

- XML file processing with XSD validation
- Dynamic mapping configuration
- Role-based access control
- Real-time processing status
- Modern React-based UI
- H2 database for data storage

## Prerequisites

- Java 17 or higher
- Node.js 14 or higher
- Maven 3.6 or higher

## Project Structure

```
.
├── src/                    # Backend source files
│   ├── main/
│   │   ├── java/          # Java source code
│   │   └── resources/     # Configuration files
│   └── test/              # Test files
├── frontend/              # React frontend
├── inbox/                 # Directory for incoming XML files
├── processed/            # Directory for processed files
└── error/               # Directory for failed files
```

## Setup

1. Clone the repository:
```bash
git clone <repository-url>
cd xml-processor
```

2. Build the backend:
```bash
mvn clean install
```

3. Set up the frontend:
```bash
cd frontend
npm install
```

## Running the Application

1. Start the Spring Boot backend:
```bash
mvn spring-boot:run
```

2. Start the React frontend development server:
```bash
cd frontend
npm start
```

3. Access the application:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console

## Default Users

The application comes with two default users:
- Admin: username: `admin`, password: `admin123`
- User: username: `user`, password: `user123`

## API Endpoints

- `POST /api/mappings` - Create mapping rules
- `GET /api/mappings` - List all mappings
- `POST /api/schemas` - Upload XSD schema
- `GET /api/process-status` - Get file processing stats

## Directory Setup

Create the following directories in the project root:
```bash
mkdir inbox processed error
```

## Testing

1. Run backend tests:
```bash
mvn test
```

2. Run frontend tests:
```bash
cd frontend
npm test
```

## Configuration

The application can be configured through `src/main/resources/application.properties`:

```properties
camel.file.inbox=./inbox
h2.datasource.url=jdbc:h2:file:./data/middleware
h2.datasource.username=sa
h2.datasource.password=
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 