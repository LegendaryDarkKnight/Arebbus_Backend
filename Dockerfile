# Use the official OpenJDK 23 runtime as a parent image
FROM openjdk:23-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/app.jar app.jar

# Expose the port your application runs on
EXPOSE 6996

# Set environment variables (optional defaults)
ENV JAVA_OPTS=""

# Add a health check (optional but recommended)
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:6996/health || exit 1

# Run the jar file
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]