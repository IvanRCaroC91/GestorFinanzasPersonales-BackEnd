# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml ./pom.xml
COPY architecture/pom.xml ./architecture/pom.xml
COPY architecture/ms-api-gateway/pom.xml ./architecture/ms-api-gateway/pom.xml
COPY architecture/ms-api-gateway/src ./architecture/ms-api-gateway/src
RUN mvn -f architecture/ms-api-gateway/pom.xml clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/architecture/ms-api-gateway/target/ms-api-gateway-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
