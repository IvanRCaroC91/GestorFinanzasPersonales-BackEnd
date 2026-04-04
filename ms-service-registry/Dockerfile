# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml ./pom.xml
COPY ms-service-registry/pom.xml ./ms-service-registry/pom.xml
COPY ms-service-registry/src ./ms-service-registry/src
RUN cd ms-service-registry && mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/ms-service-registry/target/ms-service-registry-1.0.0.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
