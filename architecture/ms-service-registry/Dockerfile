# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml ./pom.xml
COPY architecture/pom.xml ./architecture/pom.xml
COPY architecture/ms-service-registry/pom.xml ./architecture/ms-service-registry/pom.xml
COPY architecture/ms-service-registry/src ./architecture/ms-service-registry/src
RUN mvn -f architecture/ms-service-registry/pom.xml clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/architecture/ms-service-registry/target/ms-service-registry-1.0.0.jar app.jar
EXPOSE 8765
ENTRYPOINT ["java", "-jar", "app.jar"]
