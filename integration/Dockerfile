FROM maven:3.9-amazoncorretto-21-al2023 AS BUILDER
WORKDIR /app
COPY . .
#COPY src ./src
RUN mvn -f integration/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=BUILDER /app/integration/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]