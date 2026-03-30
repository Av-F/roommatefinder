# Use Maven + JDK 17 image for building
FROM maven:3.9.3-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy only Maven files first to leverage Docker cache
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Download dependencies (caches better)
RUN mvn dependency:go-offline -B

# Copy the rest of the source code
COPY src ./src

# Build the application (skip tests to speed up)
RUN mvn clean package -DskipTests

# ---------------------------
# Stage 2: runtime image
# ---------------------------
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the built jar from previous stage
COPY --from=build /app/target/roommatefinder-0.0.1-SNAPSHOT.jar ./app.jar

# Expose default Spring Boot port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java","-jar","app.jar"]