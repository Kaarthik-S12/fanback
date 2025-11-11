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
