# ClickHouse File Ingestor

A web application for ingesting CSV files into ClickHouse database with a user-friendly interface.

## Features

- ClickHouse connection management
- CSV file upload and processing
- Automatic data type inference
- Data preview functionality
- Table creation and data ingestion

## Prerequisites

- Java 17
- Node.js and npm
- ClickHouse Server
- Maven

## Installation

1. **Clone the repository**
bash
git clone https://github.com/yourusername/clickhouse-file-ingestor.git
cd clickhouse-file-ingestor
2.backend setup
cd backend/demo
mvn clean install
3.Frontend stepup
cd frontend
npm install
4.Start ClickHouse Server
clickhouse-server
5.Start Backend Server
cd backend/demo
mvn spring-boot:run
## Usage
1. Access the application at http://localhost:3000
2. Configure ClickHouse connection settings
3. Upload CSV file and specify table name
4. Preview and confirm data types
5. Start ingestion process
## API Endpoints
- POST /api/transfer/test-connection : Test ClickHouse connection
- POST /api/transfer/preview : Preview table data
- POST /api/transfer/upload : Upload and process CSV file
## Technology Stack
- Frontend: React, Material-UI
- Backend: Spring Boot
- Database: ClickHouse
- Build Tools: Maven, npm
## Contributing
1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request
