# Stage 1: Build
FROM maven:3.9.12-eclipse-temurin-17 AS builder

# Set working directory
WORKDIR /app

# Copy pom first to leverage Docker cache
COPY pom.xml .

# Copy source code
COPY src ./src

# Build the project with annotation processing enabled
RUN mvn clean package -DskipTests -B

# Stage 2: Run
FROM eclipse-temurin:17-jdk-jammy

# Set working directory
WORKDIR /app

# Copy built jar from builder stage
COPY --from=builder /app/target/roommatefinder-0.0.1-SNAPSHOT.jar ./roommatefinder.jar

# Expose the port
EXPOSE 8080

# Start Spring Boot
ENTRYPOINT ["java","-jar","roommatefinder.jar"]