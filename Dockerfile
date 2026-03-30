# Stage 1: Build
FROM maven:3.9.4-eclipse-temurin-17 AS builder

# Set working directory
WORKDIR /app

# Copy only the pom.xml first to leverage Docker cache for dependencies
COPY pom.xml .

# Download dependencies (this speeds up subsequent builds)
RUN mvn dependency:go-offline

# Copy the entire project
COPY src ./src

# Build the project (skip tests for faster builds)
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jdk-jammy

# Set working directory
WORKDIR /app

# Copy the jar built in the previous stage
COPY --from=builder /app/target/roommatefinder-0.0.1-SNAPSHOT.jar ./roommatefinder.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Set the entry point
ENTRYPOINT ["java","-jar","roommatefinder.jar"]