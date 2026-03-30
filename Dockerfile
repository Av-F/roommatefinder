# ========================
# Stage 1: Build
# ========================
FROM maven:3.9.2-eclipse-temurin-17 AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml first to leverage Docker cache
COPY pom.xml .

# Download dependencies only (speeds up rebuilds)
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# ========================
# Stage 2: Run
# ========================
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/target/roommatefinder-0.0.1-SNAPSHOT.jar app.jar

# Expose port your Spring Boot app runs on
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java","-jar","app.jar"]