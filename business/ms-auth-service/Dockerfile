# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml ./pom.xml
COPY business/pom.xml ./business/pom.xml
COPY business/ms-auth-service/pom.xml ./business/ms-auth-service/pom.xml
COPY business/ms-auth-service/src ./business/ms-auth-service/src
RUN mvn -f business/ms-auth-service/pom.xml clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/business/ms-auth-service/target/ms-auth-service-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
