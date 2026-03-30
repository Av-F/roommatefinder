# ---- Build Stage ----
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy everything
COPY . .

# Build the jar
RUN mvn clean package -DskipTests


# ---- Run Stage ----
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copy jar from builder
COPY --from=builder /app/target/*.jar app.jar

# Render uses dynamic port
ENV PORT=8080

EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]