# Roommate Finder/Mates backend

Try here!
https://av-f.github.io/mates/#/

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

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/mates/roommatefinder/
│   │       ├── config/          # Security & JWT configuration (in theory, need to move things out of security in time)
│   │       ├── controller/       # REST endpoints
│   │       ├── dto/              # Data Transfer Objects
│   │       ├── model/            # JPA entities
│   │       ├── repository/       # Data access layer
│   │       ├── security/         # Custom security components
│   │       └── service/          # Business logic
│   └── resources/
│       └── application.properties  # Configuration
└── test/ (no coverage yet beyond postman tests)
    └── java/
        └── com/mates/roommatefinder/
            └── RoommatefinderApplicationTests.java
```

## Future Enhancements
- junit test cases for the service class and make sure that code coverage was 100% in both the controller and service classes.
- Include more specific models for the inclusion of pictures, lease information, account deletion
- Swtich to a MySQL database
- optimization of my imports and remove any possible extraneous code not needed in the project


## License

This project is open source and available under the MIT License.

## Author

Avraham

---

**Note**: The JWT secret key is currently hardcoded for development purposes. In production, use environment variables or a secure config server.
