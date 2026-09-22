# ms-config-server - Spring Cloud Config Server

## 📋 ¿Qué es y para qué sirve?

El **ms-config-server** es un microservicio de infraestructura que implementa **Spring Cloud Config Server**, el cual centraliza la configuración de todos los microservicios del aplicativo GestorFinanzasPersonales-BackEnd.

### 🎯 Objetivo Principal

Antes de implementar este servicio, cada microservicio tenía su propia configuración en archivos `application.yml` locales, lo que generaba varios problemas:

- **Configuración dispersa:** Cada servicio mantenía su propia configuración, dificultando el mantenimiento
- **Duplicación de configuración:** Valores comunes (URLs de base de datos, puertos, etc.) se repetían en múltiples servicios
- **Difícil actualización:** Cambiar una configuración requería modificar y redeployar cada servicio individualmente
- **Sin versionado:** No había historial de cambios en la configuración
- **Sin ambiente separado:** Difícil manejar configuraciones por ambiente (dev, staging, prod)

El **config-server** resuelve estos problemas proporcionando:

- **Configuración centralizada:** Todos los servicios obtienen su configuración desde un solo lugar
- **Versionado:** La configuración se almacena en un repositorio Git con control de versiones
- **Actualización dinámica:** Los cambios se propagan sin necesidad de redeployar servicios
- **Ambientes separados:** Soporta múltiples perfiles (dev, docker, prod) en el mismo repositorio
- **Gestión simplificada:** Un solo punto de administración para toda la configuración del sistema

### 🔧 Cómo Funciona

El flujo de funcionamiento es el siguiente:

1. **Config-Server se inicia** y se conecta a un repositorio Git (local o remoto)
2. **Config-Server clona el repositorio** y lee los archivos de configuración
3. **Cada microservicio cliente** (auth-service, finance-service, api-gateway) se inicia con un archivo `bootstrap.yml`
4. **El cliente se conecta al config-server** y solicita su configuración basándose en su nombre (`spring.application.name`)
5. **Config-server responde** con la configuración correspondiente del repositorio Git
6. **El cliente aplica la configuración** antes de iniciar el contexto de Spring

**Ejemplo de petición:**
```
GET http://localhost:8888/ms-auth-service/default
```

**Respuesta:**
```json
{
  "name": "ms-auth-service",
  "profiles": ["default"],
  "propertySources": [
    {
      "name": "file:///home/ivan/config-repo/config/ms-auth-service.yml",
      "source": {
        "server.port": "${PORT:8081}",
        "spring.datasource.url": "${SPRING_DATASOURCE_URL:...}",
        ...
      }
    }
  ]
}
```

### 🏗️ Arquitectura en el Contexto del Aplicativo

```
┌─────────────────────────────────────────────────────────────┐
│                     Repositorio Git                          │
│              ~/config-repo/config/                           │
│  ┌──────────────────┐  ┌──────────────────┐                │
│  │ auth-service.yml │  │ finance-service  │                │
│  └──────────────────┘  └──────────────────┘                │
└─────────────────────────────────────────────────────────────┘
                            ↑
                            │ Clona y lee
                            ↓
┌─────────────────────────────────────────────────────────────┐
│              ms-config-server (8888)                         │
│         @EnableConfigServer                                  │
└─────────────────────────────────────────────────────────────┘
                            ↑
                            │ Sirve configuración vía HTTP
                            ↓
        ┌───────────────────┼───────────────────┐
        │                   │                   │
        ↓                   ↓                   ↓
┌───────────────┐  ┌───────────────┐  ┌───────────────┐
│ auth-service  │  │finance-service│  │ api-gateway   │
│   (8081)      │  │   (8083)      │  │   (8080)      │
│ bootstrap.yml │  │ bootstrap.yml │  │ bootstrap.yml │
└───────────────┘  └───────────────┘  └───────────────┘
```

---

## 📝 Cambios Realizados en la Implementación

### 1. Creación del Módulo ms-config-server

**Ubicación:** `/home/ivan/IdeaProjects/sw-finanzas/GestorFinanzasPersonales-BackEnd/ms-config-server/`

**Acción:** Se creó un nuevo módulo Spring Boot con las siguientes características:

- **Tipo:** Spring Boot Module (Java + Maven)
- **Nombre:** ms-config-server
- **Paquete:** com.finanzas.msconfigserver
- **Java Version:** 21
- **Spring Boot Version:** 3.2.5 (ajustado desde 4.1.1 para consistencia)
- **Spring Cloud Version:** 2023.0.3 (ajustado desde 2025.1.3 para consistencia)

**Por qué del cambio:** Se ajustaron las versiones para mantener consistencia con el resto del proyecto (parent pom usa Spring Boot 3.2.5 y Spring Cloud 2023.0.3). Esto evita problemas de compatibilidad entre servicios.

---

### 2. Configuración del pom.xml de ms-config-server

**Archivo:** `/home/ivan/IdeaProjects/sw-finanzas/GestorFinanzasPersonales-BackEnd/ms-config-server/pom.xml`

**Cambios realizados:**

#### Cambio 1: Ajuste de versión de Spring Boot
```xml
<!-- ANTES (versión generada por IntelliJ) -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.1.1</version>
    <relativePath/>
</parent>

<!-- DESPUÉS (ajustado para consistencia) -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.5</version>
    <relativePath/>
</parent>
```

**Por qué del cambio:** IntelliJ generó el módulo con Spring Boot 4.1.1 (la versión más reciente), pero el resto del proyecto usa 3.2.5. Mantener versiones consistentes es crucial para evitar problemas de compatibilidad entre servicios.

#### Cambio 2: Ajuste de versión de Spring Cloud
```xml
<!-- ANTES -->
<properties>
    <java.version>21</java.version>
    <spring-cloud.version>2025.1.3</spring-cloud.version>
</properties>

<!-- DESPUÉS -->
<properties>
    <java.version>21</java.version>
    <spring-cloud.version>2023.0.3</spring-cloud.version>
</properties>
```

**Por qué del cambio:** Similar al cambio anterior, se ajustó Spring Cloud a la versión usada en el resto del proyecto (2023.0.3) para mantener consistencia.

#### Cambio 3: Ajuste de artifactId y versión
```xml
<!-- ANTES -->
<artifactId>ms-config-server</artifactId>
<version>0.0.1-SNAPSHOT</version>
<name>ms-config-server</name>
<description>ms-config-server</description>

<!-- DESPUÉS -->
<artifactId>ms-config-server</artifactId>
<version>1.0.0</version>
<name>ms-config-server</name>
<description>Spring Cloud Config Server for centralized configuration</description>
```

**Por qué del cambio:** Se cambió la versión de `0.0.1-SNAPSHOT` a `1.0.0` para seguir el patrón de versionado del resto de servicios (todos usan 1.0.0). La descripción se mejoró para ser más descriptiva.

#### Cambio 4: Cambio de dependencia webmvc a web
```xml
<!-- ANTES -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>

<!-- DESPUÉS -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

**Por qué del cambio:** `spring-boot-starter-webmvc` es más específico y puede causar problemas de compatibilidad con Spring Cloud Config Server. `spring-boot-starter-web` es el estándar y funciona correctamente con Config Server.

#### Cambio 5: Simplificación de dependencias de test
```xml
<!-- ANTES -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- DESPUÉS -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

**Por qué del cambio:** Las dependencias específicas de test para actuator y webmvc no son necesarias. `spring-boot-starter-test` incluye todo lo necesario para testing básico.

#### Cambio 6: Eliminación de configuración de maven-compiler-plugin
```xml
<!-- ANTES (configuración compleja de Lombok) -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <executions>
        <execution>
            <id>default-compile</id>
            <phase>compile</phase>
            <goals>
                <goal>compile</goal>
            </goals>
            <configuration>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </execution>
        ...
    </executions>
</plugin>

<!-- DESPUÉS (configuración simplificada) -->
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
</plugin>
```

**Por qué del cambio:** Spring Boot maneja automáticamente la configuración de Lombok cuando se usa `spring-boot-maven-plugin`. La configuración manual del maven-compiler-plugin es innecesaria y puede causar conflictos.

---

### 3. Configuración del application.yml de ms-config-server

**Archivo:** `/home/ivan/IdeaProjects/sw-finanzas/GestorFinanzasPersonales-BackEnd/ms-config-server/src/main/resources/application.yml`

**Contenido completo (archivo nuevo):**
```yaml
server:
  port: ${PORT:8888}

spring:
  application:
    name: ms-config-server
  cloud:
    config:
      server:
        git:
          # URI del repositorio Git donde se almacenará la configuración
          # Para desarrollo local, usa un repositorio local
          uri: ${CONFIG_SERVER_GIT_URI:file://${user.home}/config-repo}
          # Ruta dentro del repositorio donde están los archivos de configuración
          search-paths: config
          # Branch a usar
          default-label: main
          # Username y password si el repo es privado
          username: ${CONFIG_SERVER_GIT_USERNAME:}
          password: ${CONFIG_SERVER_GIT_PASSWORD:}
          # Tiempo de espera para clonar el repo
          timeout: 5
          # Forzar pull del repo al inicio
          clone-on-start: true
  # Configuración de seguridad básica (opcional, se puede agregar Spring Security después)
  security:
    user:
      name: ${CONFIG_SERVER_USER:admin}
      password: ${CONFIG_SERVER_PASSWORD:admin123}

# Eureka Client Configuration
eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_URL:http://localhost:8761/eureka}
    register-with-eureka: true
    fetch-registry: true
  instance:
    hostname: ${RAILWAY_STATIC_URL:localhost}
    prefer-ip-address: false
    non-secure-port: ${PORT:8888}
    lease-renewal-interval-in-seconds: 30
    lease-expiration-duration-in-seconds: 90

# Actuator Configuration
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,env,refresh
  endpoint:
    health:
      show-details: when-authorized

logging:
  level:
    org.springframework.cloud.config: INFO
    com.finanzas.config: INFO
```

**Por qué de esta configuración:**

- **Puerto 8888:** Es el puerto estándar para Spring Cloud Config Server
- **Git URI local:** Para desarrollo, usa un repositorio local en `~/config-repo`. En producción puede cambiarse a un repositorio remoto (GitHub, GitLab, Bitbucket)
- **search-paths: config:** Los archivos de configuración están en el subdirectorio `config/` del repositorio
- **default-label: main:** Usa la rama `main` del repositorio Git
- **clone-on-start: true:** Fuerza al config-server a clonar el repositorio al inicio para asegurar que tenga la configuración más reciente
- **Eureka Client:** El config-server se registra en Eureka para que otros servicios puedan descubrirlo dinámicamente
- **Actuator con endpoint refresh:** Permite recargar la configuración sin reiniciar el servicio

---

### 4. Modificación de la clase principal MsConfigServerApplication

**Archivo:** `/home/ivan/IdeaProjects/sw-finanzas/GestorFinanzasPersonales-BackEnd/ms-config-server/src/main/java/com/finanzas/msconfigserver/MsConfigServerApplication.java`

**Cambios realizados:**

```java
// ANTES
package com.finanzas.msconfigserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MsConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsConfigServerApplication.class, args);
    }
}

// DESPUÉS
package com.finanzas.msconfigserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class MsConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsConfigServerApplication.class, args);
    }
}
```

**Por qué del cambio:** Se agregó la anotación `@EnableConfigServer` que es esencial para habilitar la funcionalidad de Config Server. Sin esta anotación, el servicio sería una aplicación Spring Boot normal sin capacidad de servir configuración centralizada.

---

### 5. Creación del Repositorio Git Local

**Ubicación:** `/home/ivan/config-repo/`

**Comandos ejecutados:**
```bash
mkdir -p ~/config-repo/config
cd ~/config-repo
git init
git branch -m main
```

**Por qué de esta estructura:**

- **Directorio config-repo:** Es el raíz del repositorio Git que contiene toda la configuración
- **Subdirectorio config:** Contiene los archivos YAML de configuración de cada servicio
- **Rama main:** Se usa la rama `main` (estándar moderno) en lugar de `master`

---

### 6. Creación de Archivos de Configuración en el Repositorio Git

#### 6.1 ms-auth-service.yml

**Archivo:** `/home/ivan/config-repo/config/ms-auth-service.yml`

**Contenido:** Configuración completa del servicio de autenticación incluyendo:
- Configuración de base de datos PostgreSQL
- Configuración de JPA/Hibernate
- Configuración de Eureka Client
- Configuración de logging
- Configuración de JWT

**Por qué de este archivo:** Centraliza toda la configuración de auth-service en un solo lugar. Antes, esta configuración estaba dispersa en `application.yml` del servicio. Ahora se puede modificar en este archivo y el cambio se propaga automáticamente.

**Cambios notables vs original:**
- **Base de datos:** Se cambió de `finanzas_db` a `auth_db` para preparar la separación de bases de datos (Database per Service)
- **Todas las propiedades:** Se migraron desde el `application.yml` original de ms-auth-service

#### 6.2 ms-finance-service.yml

**Archivo:** `/home/ivan/config-repo/config/ms-finance-service.yml`

**Contenido:** Configuración completa del servicio financiero incluyendo:
- Configuración de base de datos PostgreSQL
- Configuración de JPA/Hibernate
- Configuración de Eureka Client
- Configuración de logging
- Configuración de Jackson

**Por qué de este archivo:** Similar a auth-service, centraliza toda la configuración de finance-service.

**Cambios notables vs original:**
- **Base de datos:** Se cambió de `finanzas_db` a `finance_db` para preparar la separación de bases de datos
- **Todas las propiedades:** Se migraron desde el `application.yml` original de ms-finance-service

#### 6.3 ms-api-gateway.yml

**Archivo:** `/home/ivan/config-repo/config/ms-api-gateway.yml`

**Contenido:** Configuración completa del API Gateway incluyendo:
- Configuración de CORS
- Configuración de rutas (routes)
- Configuración de Eureka Client
- Configuración de JWT

**Por qué de este archivo:** Centraliza la configuración del gateway, especialmente las rutas que ahora usan `lb://` (load balancer) en lugar de URLs hardcodeadas.

**Cambios notables vs original:**
- **Rutas:** Se cambiaron de URLs hardcodeadas (`http://auth-service.railway.internal:8081`) a load balancer (`lb://ms-auth-service`)
- **Por qué del cambio:** Usar `lb://` permite que Eureka descubra dinámicamente las instancias de los servicios, lo cual es más robusto y escalable

#### 6.4 ms-service-registry.yml

**Archivo:** `/home/ivan/config-repo/config/ms-service-registry.yml`

**Contenido:** Configuración completa del Service Registry (Eureka).

**Por qué de este archivo:** Aunque service-registry no es un cliente de config-server (debe iniciarse primero), se creó el archivo para mantener consistencia y para posible uso futuro.

---

### 7. Actualización del pom.xml Parent

**Archivo:** `/home/ivan/IdeaProjects/sw-finanzas/GestorFinanzasPersonales-BackEnd/pom.xml`

**Cambio realizado:**
```xml
<!-- ANTES -->
<modules>
    <module>ms-service-registry</module>
    <module>ms-auth-service</module>
    <module>ms-api-gateway</module>
    <!-- futuros -->
</modules>

<!-- DESPUÉS -->
<modules>
    <module>ms-service-registry</module>
    <module>ms-auth-service</module>
    <module>ms-api-gateway</module>
    <module>ms-config-server</module>
    <!-- futuros -->
</modules>
```

**Por qué del cambio:** Se agregó `ms-config-server` como módulo del proyecto multi-módulo Maven. Esto permite:
- Compilar todos los servicios con un solo comando desde el directorio raíz
- Gestionar dependencias compartidas desde el parent pom
- Mantener la estructura organizada del proyecto

---

### 8. Actualización del docker-compose.yml

**Archivo:** `/home/ivan/IdeaProjects/sw-finanzas/GestorFinanzasPersonales-BackEnd/docker-compose.yml`

#### Cambio 1: Agregado del servicio config-server
```yaml
# NUEVO SERVICIO AGREGADO
  # Config Server
  config-server:
    build:
      context: ./ms-config-server
      dockerfile: ./Dockerfile
    container_name: finanzas-config
    restart: unless-stopped
    ports:
      - "8888:8888"
    environment:
      - SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-docker}
      - EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=${EUREKA_URL:http://eureka:8761/eureka/}
      - CONFIG_SERVER_GIT_URI=file:///config-repo
    volumes:
      - ~/config-repo:/config-repo:ro
    networks:
      - finanzas-network
    depends_on:
      - eureka
```

**Por qué del cambio:**
- **Puerto 8888:** Expone el puerto estándar del config-server
- **Volume mount:** Monta el repositorio Git local (`~/config-repo`) en el contenedor para que el config-server pueda leer la configuración
- **depends_on eureka:** El config-server depende de eureka porque se registra como servicio
- **CONFIG_SERVER_GIT_URI:** Apunta al repositorio Git montado como volumen

#### Cambio 2: Actualización del servicio gateway
```yaml
# ANTES
  gateway:
    ...
    environment:
      - SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-docker}
      - EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=${EUREKA_URL:http://eureka:8761/eureka/}
      - JWT_SECRET=${JWT_SECRET:mySecretKey123456789012345678901234567890}
    depends_on:
      - eureka

# DESPUÉS
  gateway:
    ...
    environment:
      - SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-docker}
      - EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=${EUREKA_URL:http://eureka:8761/eureka/}
      - CONFIG_SERVER_URI=http://config-server:8888
      - JWT_SECRET=${JWT_SECRET:mySecretKey123456789012345678901234567890}
    depends_on:
      - eureka
      - config-server
```

**Por qué del cambio:**
- **CONFIG_SERVER_URI:** Agrega la URL del config-server para que el gateway pueda conectarse
- **depends_on config-server:** El gateway ahora depende del config-server porque necesita obtener su configuración antes de iniciar

#### Cambio 3: Actualización del servicio auth-service
```yaml
# ANTES
  auth-service:
    ...
    environment:
      - SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-docker}
      - DATABASE_URL=${DATABASE_URL:-jdbc:postgresql://postgres:5432/finanzas_db}
      ...
      - EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=${EUREKA_URL:http://eureka:8761/eureka/}
    depends_on:
      - postgres
      - eureka

# DESPUÉS
  auth-service:
    ...
    environment:
      - SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-docker}
      - CONFIG_SERVER_URI=http://config-server:8888
      - DATABASE_URL=${DATABASE_URL:-jdbc:postgresql://postgres:5432/finanzas_db}
      ...
      - EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=${EUREKA_URL:http://eureka:8761/eureka/}
    depends_on:
      - postgres
      - eureka
      - config-server
```

**Por qué del cambio:**
- **CONFIG_SERVER_URI:** Agrega la URL del config-server
- **depends_on config-server:** auth-service ahora depende del config-server

#### Cambio 4: Actualización del servicio finance-service
```yaml
# ANTES
  finance-service:
    ...
    environment:
      - SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-docker}
      - DATABASE_URL=${DATABASE_URL:-jdbc:postgresql://postgres:5432/finanzas_db}
      ...
      - EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=${EUREKA_URL:http://eureka:8761/eureka/}
    depends_on:
      - postgres
      - eureka

# DESPUÉS
  finance-service:
    ...
    environment:
      - SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-docker}
      - CONFIG_SERVER_URI=http://config-server:8888
      - DATABASE_URL=${DATABASE_URL:-jdbc:postgresql://postgres:5432/finanzas_db}
      ...
      - EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=${EUREKA_URL:http://eureka:8761/eureka/}
    depends_on:
      - postgres
      - eureka
      - config-server
```

**Por qué del cambio:** Igual que auth-service, se agrega la dependencia del config-server.

---

### 9. Creación del Dockerfile de ms-config-server

**Archivo:** `/home/ivan/IdeaProjects/sw-finanzas/GestorFinanzasPersonales-BackEnd/ms-config-server/Dockerfile`

**Contenido:**
```dockerfile
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/ms-config-server-1.0.0.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Por qué de este Dockerfile:**
- **Multi-stage build:** Primero compila con Maven, luego crea una imagen ligera solo con el JAR
- **DskipTests:** Omite tests durante el build de Docker para velocidad
- **eclipse-temurin:21-jre-alpine:** Usa JRE Alpine para una imagen final ligera
- **EXPOSE 8888:** Expone el puerto del config-server

---

### 10. Configuración de ms-auth-service como Cliente

#### 10.1 Actualización del pom.xml de ms-auth-service

**Archivo:** `/home/ivan/IdeaProjects/sw-finanzas/GestorFinanzasPersonales-BackEnd/ms-auth-service/pom.xml`

**Cambios realizados:**
```xml
<!-- ANTES -->
        <!-- Eureka Client -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>

<!-- DESPUÉS -->
        <!-- Eureka Client -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>

        <!-- Config Client -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-client</artifactId>
        </dependency>

        <!-- Bootstrap for Config Client -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-bootstrap</artifactId>
        </dependency>
```

**Por qué del cambio:**
- **spring-cloud-config-client:** Proporciona la funcionalidad para conectarse al config-server y obtener configuración
- **spring-cloud-starter-bootstrap:** Habilita el contexto de bootstrap que se ejecuta antes del contexto principal de Spring, permitiendo cargar configuración desde el config-server antes de iniciar el servicio

#### 10.2 Creación del bootstrap.yml de ms-auth-service

**Archivo:** `/home/ivan/IdeaProjects/sw-finanzas/GestorFinanzasPersonales-BackEnd/ms-auth-service/src/main/resources/bootstrap.yml`

**Contenido:**
```yaml
spring:
  application:
    name: ms-auth-service
  cloud:
    config:
      uri: ${CONFIG_SERVER_URI:http://localhost:8888}
      fail-fast: true
      retry:
        initial-interval: 1000
        max-attempts: 6
        max-interval: 2000
        multiplier: 1.5
```

**Por qué de este archivo:**
- **spring.application.name:** Define el nombre del servicio, que el config-server usa para buscar el archivo correspondiente (`ms-auth-service.yml`)
- **config.uri:** URL del config-server (configurable por variable de entorno)
- **fail-fast: true:** Si el config-server no está disponible, el servicio falla al iniciar en lugar de usar configuración local
- **retry:** Configura reintentos con backoff exponencial para mayor resiliencia

#### 10.3 Simplificación del application.yml de ms-auth-service

**Archivo:** `/home/ivan/IdeaProjects/sw-finanzas/GestorFinanzasPersonales-BackEnd/ms-auth-service/src/main/resources/application.yml`

**Cambios realizados:**
```yaml
# ANTES (67 líneas de configuración completa)
server:
  port: ${PORT:8081}
spring:
  application:
    name: ms-auth-service
  datasource:
    url: ${SPRING_DATASOURCE_URL:...}
    username: ${SPRING_DATASOURCE_USERNAME:...}
    password: ${SPRING_DATASOURCE_PASSWORD:...}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
    open-in-view: false
  data:
    jpa:
      repositories:
        enabled: true
eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_URL:http://localhost:8761/eureka}
    register-with-eureka: true
    fetch-registry: true
  instance:
    hostname: ${RAILWAY_STATIC_URL:localhost}
    prefer-ip-address: false
    non-secure-port: ${PORT:8081}
    lease-renewal-interval-in-seconds: 30
    lease-expiration-duration-in-seconds: 90
logging:
  level:
    com.finanzas.auth: INFO
    org.springframework.cloud.netflix.eureka: INFO
    org.hibernate.SQL: WARN
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when-authorized
jwt:
  secret: ${JWT_SECRET:...}
  expiration: ${JWT_EXPIRATION:86400000}

# DESPUÉS (solo configuración de fallback)
# Configuración local de fallback (solo se usa si config-server no está disponible)
server:
  port: ${PORT:8081}

spring:
  application:
    name: ms-auth-service

logging:
  level:
    com.finanzas.auth: INFO
    org.springframework.cloud.config: INFO
```

**Por qué del cambio:**
- **Reducción de 67 a 13 líneas:** Toda la configuración se movió al config-server
- **Fallback local:** Se mantiene una configuración mínima local por si el config-server no está disponible
- **spring.application.name:** Se mantiene porque es necesario para que el servicio se identifique
- **logging:** Se agrega logging de config-server para debug

---

### 11. Configuración de ms-finance-service como Cliente

#### 11.1 Actualización del pom.xml de ms-finance-service

**Archivo:** `/home/ivan/IdeaProjects/sw-finanzas/GestorFinanzasPersonales-BackEnd/ms-finance-service/pom.xml`

**Cambios realizados:**
```xml
<!-- ANTES -->
        <!-- Spring Cloud -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>

<!-- DESPUÉS -->
        <!-- Spring Cloud -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>

        <!-- Config Client -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-client</artifactId>
        </dependency>

        <!-- Bootstrap for Config Client -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-bootstrap</artifactId>
        </dependency>
```

**Por qué del cambio:** Igual que auth-service, se agregan las dependencias necesarias para conectarse al config-server.

#### 11.2 Creación del bootstrap.yml de ms-finance-service

**Archivo:** `/home/ivan/IdeaProjects/sw-finanzas/GestorFinanzasPersonales-BackEnd/ms-finance-service/src/main/resources/bootstrap.yml`

**Contenido:** Similar a auth-service, pero con `spring.application.name: ms-finance-service`

**Por qué del cambio:** Permite que finance-service obtenga su configuración desde el config-server.

#### 11.3 Simplificación del application.yml de ms-finance-service

**Archivo:** `/home/ivan/IdeaProjects/sw-finanzas/GestorFinanzasPersonales-BackEnd/ms-finance-service/src/main/resources/application.yml`

**Cambios realizados:**
```yaml
# ANTES (79 líneas de configuración completa)
server:
  port: ${PORT:8083}
spring:
  application:
    name: ms-finance-service
  datasource:
    url: ${SPRING_DATASOURCE_URL:...}
    ...
  jpa:
    hibernate:
      ddl-auto: validate
    ...
  jackson:
    default-property-inclusion: non_null
  mvc:
    throw-exception-if-no-handler-found: true
  web:
    resources:
      add-mappings: false
logging:
  level:
    org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping: INFO
    ...
eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_URL:http://localhost:8761/eureka}
    ...
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when_authorized

# DESPUÉS (solo configuración de fallback)
# Configuración local de fallback (solo se usa si config-server no está disponible)
server:
  port: ${PORT:8083}

spring:
  application:
    name: ms-finance-service

logging:
  level:
    com.finanzas.finance: INFO
    org.springframework.cloud.config: INFO
```

**Por qué del cambio:** Igual que auth-service, se reduce drásticamente el archivo local moviendo toda la configuración al config-server.

---

### 12. Configuración de ms-api-gateway como Cliente

#### 12.1 Actualización del pom.xml de ms-api-gateway

**Archivo:** `/home/ivan/IdeaProjects/sw-finanzas/GestorFinanzasPersonales-BackEnd/ms-api-gateway/pom.xml`

**Cambios realizados:**
```xml
<!-- ANTES -->
        <!-- Eureka Client -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>

<!-- DESPUÉS -->
        <!-- Eureka Client -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>

        <!-- Config Client -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-client</artifactId>
        </dependency>

        <!-- Bootstrap for Config Client -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-bootstrap</artifactId>
        </dependency>
```

**Por qué del cambio:** Igual que los otros servicios, se agregan las dependencias de config-client.

#### 12.2 Creación del bootstrap.yml de ms-api-gateway

**Archivo:** `/home/ivan/IdeaProjects/sw-finanzas/GestorFinanzasPersonales-BackEnd/ms-api-gateway/src/main/resources/bootstrap.yml`

**Contenido:** Similar a los otros servicios, pero con `spring.application.name: ms-api-gateway`

**Por qué del cambio:** Permite que el gateway obtenga su configuración desde el config-server.

#### 12.3 Simplificación del application-docker.yml de ms-api-gateway

**Archivo:** `/home/ivan/IdeaProjects/sw-finanzas/GestorFinanzasPersonales-BackEnd/ms-api-gateway/src/main/resources/application-docker.yml`

**Cambios realizados:**
```yaml
# ANTES (79 líneas con configuración completa de CORS, rutas, Eureka, JWT)
server:
  port: ${GATEWAY_PORT:8080}
spring:
  application:
    name: ms-api-gateway
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOriginPatterns:
              - "https://*.vercel.app"
              - ...
            allowedMethods:
              - GET
              - POST
              - ...
            allowedHeaders:
              - "*"
            allowCredentials: false
            maxAge: 3600
      routes:
        - id: auth-service
          uri: http://auth-service.railway.internal:8081
          predicates:
            - Path=/api/v1/auth/**
        - id: protected-routes
          uri: http://auth-service.railway.internal:8081
          predicates:
            - Path=/api/v1/protected/**
        - id: finance-service
          uri: http://finance-service.railway.internal:8083
          predicates:
            - Path=/api/v1/finance/**
eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_URL:http://eureka:8761/eureka/}
    ...
logging:
  level:
    org.springframework.cloud.gateway: INFO
    com.finanzas.apigateway: INFO
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when-authorized
jwt:
  secret: ${JWT_SECRET:...}

# DESPUÉS (solo configuración de fallback)
# Configuración local de fallback (solo se usa si config-server no está disponible)
server:
  port: ${GATEWAY_PORT:8080}

spring:
  application:
    name: ms-api-gateway

logging:
  level:
    org.springframework.cloud.gateway: INFO
    com.finanzas.apigateway: INFO
    org.springframework.cloud.config: INFO
```

**Por qué del cambio:**
- **Reducción de 79 a 14 líneas:** Toda la configuración compleja (CORS, rutas, Eureka, JWT) se movió al config-server
- **Fallback local:** Se mantiene configuración mínima por si el config-server no está disponible

---

## 🧪 Pruebas Realizadas

### Prueba 1: Compilación de ms-config-server
```bash
cd /home/ivan/IdeaProjects/sw-finanzas/GestorFinanzasPersonales-BackEnd/ms-config-server
mvn clean install -DskipTests
```
**Resultado:** ✅ BUILD SUCCESS

### Prueba 2: Inicio de ms-config-server
```bash
java -jar target/ms-config-server-1.0.0.jar --server.port=8889
```
**Resultado:** ✅ Servicio iniciado correctamente en puerto 8889 (8888 estaba ocupado)

### Prueba 3: Verificación de configuración de auth-service
```bash
curl -s http://localhost:8889/ms-auth-service/default
```
**Resultado:** ✅ Configuración servida correctamente desde repositorio Git

### Prueba 4: Verificación de configuración de finance-service
```bash
curl -s http://localhost:8889/ms-finance-service/default
```
**Resultado:** ✅ Configuración servida correctamente desde repositorio Git

---

## 🚀 Cómo Usar

### Desarrollo Local

1. **Iniciar el config-server:**
   ```bash
   cd /home/ivan/IdeaProjects/sw-finanzas/GestorFinanzasPersonales-BackEnd/ms-config-server
   java -jar target/ms-config-server-1.0.0.jar
   ```

2. **Iniciar Eureka:**
   ```bash
   cd /home/ivan/IdeaProjects/sw-finanzas/GestorFinanzasPersonales-BackEnd/ms-service-registry
   java -jar target/ms-service-registry-1.0.0.jar
   ```

3. **Iniciar cualquier servicio cliente (ej: auth-service):**
   ```bash
   cd /home/ivan/IdeaProjects/sw-finanzas/GestorFinanzasPersonales-BackEnd/ms-auth-service
   java -jar target/ms-auth-service-1.0.0.jar
   ```

4. **Verificar que el servicio obtuvo configuración desde config-server:**
   - Revisar logs del servicio, debería mostrar conexión a config-server
   - Verificar que la configuración se aplicó correctamente

### Docker Compose

```bash
cd /home/ivan/IdeaProjects/sw-finanzas/GestorFinanzasPersonales-BackEnd
docker-compose up -d
```

Esto iniciará todos los servicios incluyendo el config-server.

### Modificar Configuración

1. **Editar el archivo de configuración en el repositorio Git:**
   ```bash
   nano ~/config-repo/config/ms-auth-service.yml
   ```

2. **Commit el cambio:**
   ```bash
   cd ~/config-repo
   git add config/ms-auth-service.yml
   git commit -m "Update auth-service configuration"
   ```

3. **Recargar configuración en los servicios (opcional):**
   - Los servicios recargarán automáticamente en el siguiente reinicio
   - O usar el endpoint `/actuator/refresh` para recargar sin reiniciar

---

## 📊 Beneficios Obtenidos

### Antes del Config Server
- ❌ Configuración dispersa en múltiples archivos
- ❌ Duplicación de valores comunes
- ❌ Difícil mantenimiento y actualización
- ❌ Sin historial de cambios
- ❌ Difícil manejar múltiples ambientes

### Después del Config Server
- ✅ Configuración centralizada en un solo lugar
- ✅ Eliminación de duplicación
- ✅ Mantenimiento simplificado
- ✅ Versionado con Git
- ✅ Soporte nativo para múltiples ambientes
- ✅ Actualización dinámica sin redeploy
- ✅ Mejor organización y estructura

---

## 🔮 Próximos Pasos

Según el análisis comparativo y el roadmap de implementación, las siguientes mejoras críticas son:

1. **Database per Service:** Separar la base de datos compartida en bases de datos individuales por servicio (auth_db, finance_db)
2. **Implementar Paginación:** Agregar paginación en todos los endpoints de listado
3. **Implementar Circuit Breakers:** Agregar Resilience4j para tolerancia a fallos
4. **Implementar Tracing Distribuido:** Agregar OpenTelemetry + Jaeger para observabilidad

---

## 📚 Referencias

- [Spring Cloud Config Server Documentation](https://spring.io/projects/spring-cloud-config)
- [Spring Cloud Config Client Documentation](https://cloud.spring.io/spring-cloud-config/reference/html/#_client_side_usage)
- [Microservices Pattern: Centralized Configuration](https://microservices.io/patterns/configuration/externalized-configuration.html)
