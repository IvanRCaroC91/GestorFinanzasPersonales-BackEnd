FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml ./pom.xml
COPY architecture/pom.xml ./architecture/pom.xml
COPY architecture/ms-config-server/pom.xml ./architecture/ms-config-server/pom.xml
COPY architecture/ms-config-server/src ./architecture/ms-config-server/src
RUN mvn -f architecture/ms-config-server/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/architecture/ms-config-server/target/ms-config-server-1.0.0.jar app.jar
EXPOSE 8890
ENTRYPOINT ["java", "-jar", "app.jar"]
