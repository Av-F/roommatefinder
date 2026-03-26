# Roommate Finder

A Spring Boot application for discovering and connecting with potential roommates. Users can register, create detailed profiles, and search for compatible living partners based on location, age, and preferences.

## Features

- **User Authentication**: JWT-based login and registration with secure password hashing (BCrypt)
- **Profile Management**: Create and manage detailed roommate profiles
- **Advanced Search**: Search for profiles by city, age range, and preferences
- **RESTful API**: Clean, well-structured REST endpoints with DTOs
- **Database Persistence**: JPA/Hibernate integration with support for H2 and MySQL

## Tech Stack

- **Backend**: Java 17, Spring Boot 4.0.3
- **Database**: H2 (development), MySQL (production)
- **Security**: Spring Security, JWT (JJWT)
- **Build Tool**: Maven
- **ORM**: Spring Data JPA, Hibernate
- **Validation**: Jakarta Bean Validation
- **Utilities**: Lombok

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Git

## Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/<YOUR_USERNAME>/roommatefinder.git
cd roommatefinder
```

### 2. Build the Project
```bash
mvn clean install
```

### 3. Run the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

## API Endpoints

### Authentication
- `POST /auth/login` - Login with email and password
- `POST /users/register` - Register a new user

### Profiles
- `POST /profiles` - Create a new profile (requires authentication)
- `GET /profiles/search` - Search profiles by city, age, or preferences (requires authentication)

### Users
- `GET /users` - Get all users (requires authentication)

## Database

- **Development**: H2 in-memory database
  - H2 Console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa` (no password)

- **Production**: Configure MySQL connection in `application.properties`

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/mates/roommatefinder/
│   │       ├── config/          # Security & JWT configuration
│   │       ├── controller/       # REST endpoints
│   │       ├── dto/              # Data Transfer Objects
│   │       ├── model/            # JPA entities
│   │       ├── repository/       # Data access layer
│   │       ├── security/         # Custom security components
│   │       └── service/          # Business logic
│   └── resources/
│       └── application.properties  # Configuration
└── test/
    └── java/
        └── com/mates/roommatefinder/
            └── RoommatefinderApplicationTests.java
```

## Future Enhancements

- Combine multiple search filters simultaneously
- Add profile updates and deletion
- Implement user matching algorithm
- Add messaging/chat functionality
- Integrate with location-based services
- Add comprehensive unit and integration tests

## License

This project is open source and available under the MIT License.

## Author

Avi

---

**Note**: The JWT secret key is currently hardcoded for development purposes. In production, use environment variables or a secure config server.
