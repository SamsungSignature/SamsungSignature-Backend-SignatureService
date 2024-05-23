FROM eclipse-temurin:17-jdk-alpine
ARG JAR_FILE=build/libs/*.jar
WORKDIR /app
COPY ${JAR_FILE} signature-service.jar
ENTRYPOINT ["java", "-jar", "/app/signature-service.jar"]