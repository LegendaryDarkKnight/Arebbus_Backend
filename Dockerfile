# Use a lightweight base image with JDK
FROM eclipse-temurin:23-jre

# Create an app directory
WORKDIR /app

# Copy the built JAR file into the image
COPY target/app.jar app.jar

# Expose port if needed (e.g., 8080)
EXPOSE 6996

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
