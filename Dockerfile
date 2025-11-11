# Use a small, efficient Java runtime
FROM eclipse-temurin:21-jre

# Set working directory inside the container
WORKDIR /app

# Copy your Spring Boot jar into the container
COPY target/*.jar app.jar

# Expose the app's port (same as Spring Boot’s default)
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]

# ---- Build Stage ----
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy all pom.xml + source files
COPY pom.xml .
COPY src ./src

# Build the project (skipping tests for speed)
RUN mvn clean package -DskipTests

# ---- Run Stage ----
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the jar file from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Set environment port (Render sets PORT)
ENV PORT=8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","app.jar"]
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
