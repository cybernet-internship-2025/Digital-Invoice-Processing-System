FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/invoice-0.0.1.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]