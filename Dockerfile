# Stage 1: Build
FROM maven:3.9.3-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy pom first to leverage caching
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN mvn dependency:go-offline

# Copy all source files
COPY src ./src

# Build the app, skip tests for faster builds
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]