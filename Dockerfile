# Use an official Maven image to build the Spring Boot app
FROM maven:3.9.9-eclipse-temurin-23 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and install dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

#use official openjdk image
FROM openjdk:23-jdk-slim

# Set the working directory
WORKDIR /app

# copy from the built jar file from build stage
COPY --from=build /app/target/arebbus-0.0.1-SNAPSHOT.jar .

# expose port
EXPOSE 6996


ENTRYPOINT ["java", "-jar", "arebbus-0.0.1-SNAPSHOT.jar"]