# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml ./pom.xml
COPY business/pom.xml ./business/pom.xml
COPY business/ms-finance-service/pom.xml ./business/ms-finance-service/pom.xml
COPY business/ms-finance-service/src ./business/ms-finance-service/src
RUN mvn -f business/ms-finance-service/pom.xml clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/business/ms-finance-service/target/ms-finance-service-1.0.0.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
