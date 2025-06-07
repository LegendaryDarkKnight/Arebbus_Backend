FROM eclipse-temurin:23-jdk

WORKDIR /app

#for github actions app-jar(as it is the artifact) else target
COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
