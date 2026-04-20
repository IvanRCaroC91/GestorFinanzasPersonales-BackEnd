# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml ./pom.xml
COPY ms-auth-service/pom.xml ./ms-auth-service/pom.xml
COPY ms-auth-service/src ./ms-auth-service/src
RUN cd ms-auth-service && mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/ms-auth-service/target/ms-auth-service-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
