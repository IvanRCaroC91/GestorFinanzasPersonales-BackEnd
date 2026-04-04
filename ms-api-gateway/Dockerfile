# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml ./pom.xml
COPY ms-api-gateway/pom.xml ./ms-api-gateway/pom.xml
COPY ms-api-gateway/src ./ms-api-gateway/src
RUN cd ms-api-gateway && mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/ms-api-gateway/target/ms-api-gateway-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
