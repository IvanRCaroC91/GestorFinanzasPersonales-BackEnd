# Gestión Financiera - Backend Microservicios

## 📋 Tabla de Contenidos

1. [Introducción](#1-introducción-del-proyecto)
2. [Estado Actual](#2-estado-actual-del-sistema)
3. [Arquitectura](#3-arquitectura-de-microservicios)
4. [Tecnologías](#4-tecnologías-utilizadas)
5. [Estructura del Proyecto](#5-estructura-del-proyecto)
6. [Instalación y Configuración](#6-instalación-y-ejecución)
7. [Microservicios](#7-microservicios-del-sistema)
8. [Base de Datos](#8-base-de-datos)
9. [API REST Endpoints](#9-api-rest-endpoints)
10. [Seguridad](#10-seguridad-y-autenticación)
11. [Testing](#11-testing-y-validación)
12. [Mejoras Futuras](#12-mejoras-futuras)
13. [Licencia](#13-licencia)

---

## 1. Introducción del Proyecto

### ¿Qué es el sistema?
Backend de microservicios para "Gestión Financiera - Presupuesto Personal", una aplicación desarrollada como proyecto tecnológico del SENA para la tecnología de Análisis y Desarrollo de Software. Este backend proporciona una API RESTful completa con arquitectura de microservicios escalable y segura.

### 🎯 Estado Actual: **PRODUCCIÓN FUNCIONAL**
El backend ha evolucionado desde un prototipo académico hasta una API robusta con microservicios completamente operativos, autenticación JWT, y gestión completa de datos financieros.

### Problema que resuelve
- **Escalabilidad**: Arquitectura de microservicios independiente y escalable
- **Seguridad**: Autenticación robusta con JWT y Spring Security
- **Performance**: Optimizado con Spring Boot y conexión a PostgreSQL
- **Maintenibilidad**: Código limpio, documentado y con pruebas automatizadas

---

## 2. Estado Actual del Sistema

### 🟢 Backend Completo y Operativo

#### **Microservicios Funcionales**
- ✅ **Service Registry**: Eureka Server en puerto 8761 (Dockerizado)
- ✅ **API Gateway**: Gateway en puerto 8080 con routing y CORS (Dockerizado)
- ✅ **Auth Service**: Autenticación JWT en puerto 8081
- ✅ **Finance Service**: Gestión financiera en puerto 8083

#### **Características Implementadas**
- ✅ **Dockerización**: Eureka y Gateway en contenedores optimizados
- ✅ **Scripts Docker**: Automatización de inicio/detención
- ✅ **Autenticación**: Login/Register con JWT y bcrypt
- ✅ **Categorías**: CRUD completo para categorías financieras
- ✅ **Movimientos**: Gestión de transacciones con validación
- ✅ **Presupuestos**: Configuración por año/mes con ejecución
- ✅ **API Gateway**: Routing centralizado y manejo de CORS
- ✅ **Service Discovery**: Descubrimiento automático con Eureka

#### **Estado Técnico**
- ✅ **Java**: JDK 21 con Spring Boot 3.x (Docker compatible)
- ✅ **Docker**: Contenerización completa de servicios críticos
- ✅ **Base de Datos**: PostgreSQL 15 con Docker
- ✅ **Build**: Maven con compilación exitosa
- ✅ **Git**: Historial limpio con commits versionados
- ✅ **Documentación**: OpenAPI/Swagger disponible

---

## 3. Arquitectura de Microservicios

### Diagrama de Arquitectura

```
┌─────────────────┐
│   Frontend      │
│   React + Vite  │
│   Puerto: 5173  │
└─────────┬───────┘
          │ HTTPS/API REST
          ▼
┌─────────────────┐
│   API Gateway   │
│   Spring Boot   │
│   Puerto: 8080  │
│   - Routing     │
│   - CORS        │
│   - JWT Filter  │
└─────────┬───────┘
          │ Service Discovery
          ▼
┌─────────────────┐
│  Service Registry│
│   Eureka Server │
│   Puerto: 8761  │
│   - Discovery   │
│   - Health Check│
└─────────┬───────┘
          │ Load Balancing
          ▼
┌─────────────────┐  ┌─────────────────┐
│   Auth Service  │  │ Finance Service │
│   Spring Boot   │  │   Spring Boot   │
│   Puerto: 8081  │  │   Puerto: 8083  │
│   - JWT Auth    │  │   - CRUD Ops    │
│   - Users       │  │   - Budgets     │
│   - Security    │  │   - Categories  │
└─────────────────┘  └─────────────────┘
          │                    │
          └─────────┬──────────┘
                    ▼
        ┌─────────────────┐
        │   PostgreSQL    │
        │   Puerto: 5432  │
        │   - Usuarios    │
        │   - Finanzas    │
        │   - Presupuestos│
        └─────────────────┘
```

### Componentes de la Arquitectura

#### **API Gateway (Puerto 8080)**
- **Tecnología**: Spring Boot + Spring Cloud Gateway
- **Responsabilidades**:
  - Routing centralizado de peticiones
  - Configuración CORS global
  - Filtrado JWT para endpoints protegidos
  - Balanceo de carga con Eureka
- **Configuración**: YAML con rutas y CORS

#### **Service Registry (Eureka - Puerto 8761)**
- **Tecnología**: Spring Cloud Eureka
- **Responsabilidades**:
  - Descubrimiento automático de servicios
  - Health monitoring
  - Balanceo de carga
- **Estado**: ✅ UP con 3 servicios registrados

#### **Auth Service (Puerto 8081)**
- **Tecnología**: Spring Boot + Spring Security + JWT
- **Responsabilidades**:
  - Autenticación de usuarios
  - Generación y validación de tokens JWT
  - Registro de nuevos usuarios
  - Manejo de sesiones
- **Seguridad**: bcrypt para passwords, JWT con expiración

#### **Finance Service (Puerto 8083)**
- **Tecnología**: Spring Boot + Spring Data JPA
- **Responsabilidades**:
  - Gestión de movimientos financieros
  - Gestión de categorías
  - Gestión de presupuestos por año/mes
  - Cálculos de ejecución presupuestaria
- **Base de Datos**: PostgreSQL con JPA/Hibernate

---

## 4. Tecnologías Utilizadas

### Backend Core

#### **Java 21**
- **Propósito**: Lenguaje principal de desarrollo
- **Ventajas**: Performance mejorada, features modernas, LTS
- **Uso en el proyecto**: Lógica de negocio, controllers, services
- **Compatibilidad**: JDK Territorium soportado

#### **Spring Boot 3.2.5**
- **Propósito**: Framework principal para microservicios
- **Ventajas**: Autoconfiguración, producción lista, ecosistema maduro
- **Uso en el proyecto**: Creación de todos los microservicios

#### **Spring Cloud Gateway**
- **Propósito**: API Gateway para microservicios
- **Ventajas**: Integración nativa con Eureka, filtros personalizados
- **Uso en el proyecto**: Routing centralizado y filtrado JWT

#### **Spring Security 6**
- **Propósito**: Seguridad y autenticación
- **Ventajas**: Integración nativa, configuración flexible
- **Uso en el proyecto**: Autenticación JWT en Auth Service

#### **Spring Data JPA + Hibernate**
- **Propósito**: Persistencia de datos
- **Ventajas**: Abstracción de base de datos, relaciones automáticas
- **Uso en el proyecto**: Entidades, repositories, queries

### Contenerización y Orquestación

#### **Docker & Docker Compose**
- **Propósito**: Contenerización y orquestación de servicios
- **Ventajas**: Portabilidad, escalabilidad, aislamiento, reproducibilidad
- **Uso en el proyecto**: 
  - Eureka Server y API Gateway containerizados
  - PostgreSQL en contenedor
  - Networking entre servicios
  - Multi-stage builds optimizados

#### **Dockerfiles Optimizados**
- **Java 21 Alpine**: Imágenes ligeras basadas en Eclipse Temurin
- **Multi-stage builds**: Reducción del tamaño final de imágenes
- **Perfiles Docker**: Configuración específica para entorno contenedorizado

### Base de Datos

#### **PostgreSQL 15**
- **Propósito**: Base de datos relacional principal
- **Ventajas**: ACID compliance, performance, features avanzadas
- **Uso en el proyecto**: Persistencia de todos los datos financieros
- **Deployment**: Contenedor Docker con persistencia de datos

### Seguridad

#### **JWT (JSON Web Tokens)**
- **Propósito**: Autenticación sin estado
- **Ventajas**: Escalable, seguro, estándar industrial
- **Uso en el proyecto**: Tokens de autenticación entre servicios

#### **bcrypt**
- **Propósito**: Hashing seguro de passwords
- **Ventajas**: Resistente a brute force, configurable
- **Uso en el proyecto**: Encriptación de passwords de usuarios

### Build y Dependencias

#### **Maven**
- **Propósito**: Gestión de dependencias y build
- **Ventajas**: Ecosistema maduro, plugins completos
- **Uso en el proyecto**: Build, test, packaging de microservicios

---

## 5. Estructura del Proyecto

```
GestorFinanzasPersonales-BackEnd/
├── 📄 Archivos de configuración raíz
│   ├── pom.xml                       # POM padre con módulos
│   ├── docker-compose.yml            # Configuración Docker completa
│   ├── start-docker-services.bat     # Script inicio Docker
│   ├── stop-docker-services.bat      # Script detención Docker
│   ├── .gitignore                    # Ignorar archivos Git
│   └── README.md                     # Este archivo
│
├── 📁 database/                      # Scripts de base de datos
│   ├── init.sql                      # Script inicialización
│   └── test-data.sql                 # Datos de prueba
│
├── 📁 ms-service-registry/           # Eureka Server (Dockerizado)
│   ├── 📄 pom.xml                   # POM del servicio
│   ├── 📄 Dockerfile                # Configuración contenedor
│   ├── 📄 .dockerignore             # Optimización build Docker
│   ├── 📁 src/main/java/            # Código fuente Java
│   │   └── 📁 com/finanzas/registry/
│   │       ├── ServiceRegistryApplication.java
│   │       └── config/
│   └── 📁 src/main/resources/       # Recursos
│       ├── application.yml           # Configuración local
│       └── application-docker.yml    # Configuración Docker
│
├── 📁 ms-api-gateway/                # API Gateway (Dockerizado)
│   ├── 📄 pom.xml                   # POM del gateway
│   ├── 📄 Dockerfile                # Configuración contenedor
│   ├── 📄 .dockerignore             # Optimización build Docker
│   ├── 📁 src/main/java/            # Código fuente
│   │   └── 📁 com/finanzas/apigateway/
│   │       ├── ApiGatewayApplication.java
│   │       ├── filter/              # Filtros JWT
│   │       └── config/              # Configuración
│   └── 📁 src/main/resources/       # Recursos
│       ├── application.yml           # Configuración local
│       └── application-docker.yml    # Configuración Docker
│
├── 📁 ms-auth-service/               # Auth Service
│   ├── 📄 pom.xml                   # POM del servicio
│   ├── 📁 src/main/java/            # Código fuente
│   │   └── 📁 com/finanzas/auth/
│   │       ├── AuthServiceApplication.java
│   │       ├── controller/          # REST Controllers
│   │       ├── service/             # Lógica de negocio
│   │       ├── dto/                 # Data Transfer Objects
│   │       ├── entity/              # Entidades JPA
│   │       ├── repository/          # Spring Data JPA
│   │       ├── config/              # Configuración Security
│   │       └── util/                # Utilidades JWT
│   └── 📁 src/main/resources/       # Recursos
│       └── application.yml           # Configuración auth
│
├── 📁 ms-finance-service/            # Finance Service
│   ├── 📄 pom.xml                   # POM del servicio
│   ├── 📁 src/main/java/            # Código fuente
│   │   └── 📁 com/finanzas/finance/
│   │       ├── FinanceServiceApplication.java
│   │       ├── controller/          # REST Controllers
│   │       │   ├── CategoriaController.java
│   │       │   ├── MovimientoController.java
│   │       │   └── PresupuestoController.java
│   │       ├── service/             # Lógica de negocio
│   │       │   ├── CategoriaService.java
│   │       │   ├── MovimientoService.java
│   │       │   └── PresupuestoService.java
│   │       ├── dto/                 # Data Transfer Objects
│   │       │   ├── CategoriaRequest.java
│   │       │   ├── CategoriaResponse.java
│   │       │   ├── MovimientoRequest.java
│   │       │   ├── MovimientoResponse.java
│   │       │   ├── PresupuestoRequest.java
│   │       │   ├── PresupuestoResponse.java
│   │       │   └── ApiResponse.java
│   │       ├── entity/              # Entidades JPA
│   │       │   ├── Usuario.java
│   │       │   ├── Categoria.java
│   │       │   ├── Movimiento.java
│   │       │   └── Presupuesto.java
│   │       ├── repository/          # Spring Data JPA
│   │       │   ├── UsuarioRepository.java
│   │       │   ├── CategoriaRepository.java
│   │       │   ├── MovimientoRepository.java
│   │       │   └── PresupuestoRepository.java
│   │       ├── exception/           # Manejo de errores
│   │       └── config/              # Configuración
│   └── 📁 src/main/resources/       # Recursos
│       └── application.yml           # Configuración finance
│
└── 📄 Archivos de documentación
    ├── FRONTEND_API_DOCUMENTATION.md  # Documentación API
    ├── FRONTEND_LOGIN_FORM_GUIDE.md  # Guía login frontend
    ├── PRUEBAS_COMPLETAS_VALIDACION.md # Pruebas completas
    ├── PRUEBAS_FUNCIONALES_QA.md      # Pruebas QA
    ├── PRUEBAS_GATEWAY_COMPLETAS.md    # Pruebas gateway
    └── README-DATABASE.md              # Documentación BD
```

---

## 6. Instalación y Configuración

### 🐳 **Opción 1: Ejecución con Docker (Recomendado)**

Esta es la forma más sencilla y recomendada para ejecutar los servicios en producción.

#### Requisitos Previos Docker

- **Docker**: Versión 20.10 o superior
- **Docker Compose**: Versión 2.0 o superior
- **Git**: Para clonar el repositorio

#### Paso 1: Clonar el Repositorio

```bash
git clone https://github.com/IvanRCaroC91/GestorFinanzasPersonales-BackEnd.git
cd GestorFinanzasPersonales-BackEnd
```

#### Paso 2: Iniciar Servicios con Docker

**Opción A: Usar Scripts Automáticos**
```bash
# Iniciar todos los servicios Docker
./start-docker-services.bat

# Detener todos los servicios Docker
./stop-docker-services.bat
```

**Opción B: Comandos Manuales**
```bash
# Construir imágenes Docker
docker-compose build eureka gateway

# Iniciar servicios
docker-compose up -d eureka gateway postgres

# Verificar estado
docker-compose ps
```

#### Paso 3: Verificar Servicios Docker

```bash
# Eureka Dashboard
curl http://localhost:8761

# Gateway Health Check
curl http://localhost:8080/actuator/health

# Ver logs de servicios
docker logs finanzas-eureka
docker logs finanzas-gateway
```

#### URLs de Acceso Docker

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **Gateway Health**: http://localhost:8080/actuator/health
- **PostgreSQL**: localhost:5432

#### Configuración Docker

Los servicios están configurados para ejecutarse en contenedores optimizados:

- **Java 21**: Compatible con JDK Territorium
- **Multi-stage builds**: Imágenes ligeras y eficientes
- **Networking**: Comunicación segura entre contenedores
- **Perfiles Docker**: Configuración específica para entorno Docker

---

### 💻 **Opción 2: Ejecución Local (Desarrollo)**

#### Requisitos Previos Locales

- **Java**: JDK 21 o superior
- **Maven**: Versión 3.8 o superior
- **Docker**: Para PostgreSQL container
- **Git**: Para clonar el repositorio
- **PostgreSQL Client**: Para administración (opcional)

#### Paso 1: Clonar el Repositorio

```bash
git clone https://github.com/IvanRCaroC91/GestorFinanzasPersonales-BackEnd.git
cd GestorFinanzasPersonales-BackEnd
```

#### Paso 2: Iniciar Base de Datos PostgreSQL

```bash
# Iniciar PostgreSQL con Docker Compose
docker-compose up -d postgres

# Verificar contenedor
docker ps
```

**Configuración PostgreSQL:**
- **Host**: localhost:5432
- **Database**: finanzas_db
- **User**: admin
- **Password**: admin123

#### Paso 3: Compilar el Proyecto

```bash
# Compilar todos los módulos
mvn clean compile

# Compilar y ejecutar tests
mvn clean test
```

#### Paso 4: Ejecutar los Microservicios

```bash
# 1. Iniciar Service Registry (Eureka)
mvn spring-boot:run -pl ms-service-registry

# 2. Iniciar API Gateway
mvn spring-boot:run -pl ms-api-gateway

# 3. Iniciar Auth Service
mvn spring-boot:run -pl ms-auth-service

# 4. Iniciar Finance Service
mvn spring-boot:run -pl ms-finance-service
```

#### Paso 5: Verificar Servicios

```bash
# Service Registry
curl http://localhost:8761/eureka/apps

# API Gateway Health
curl http://localhost:8080/actuator/health

# Auth Service Health
curl http://localhost:8081/actuator/health

# Finance Service Health
curl http://localhost:8083/actuator/health
```

### Scripts Disponibles

```bash
# Build completo
mvn clean install

# Ejecutar todos los servicios (en terminales separadas)
mvn spring-boot:run -pl ms-service-registry &
mvn spring-boot:run -pl ms-api-gateway &
mvn spring-boot:run -pl ms-auth-service &
mvn spring-boot:run -pl ms-finance-service &
```

---

### 🐳 **Archivos Docker Disponibles**

El proyecto incluye configuración Docker completa:

#### **Dockerfiles**
- `ms-service-registry/Dockerfile` - Eureka Server containerizado
- `ms-api-gateway/Dockerfile` - API Gateway containerizado

#### **Configuración Docker**
- `docker-compose.yml` - Orquestación de servicios
- `application-docker.yml` - Configuración específica Docker
- `.dockerignore` - Optimización de build

#### **Scripts de Gestión**
- `start-docker-services.bat` - Inicio automático
- `stop-docker-services.bat` - Detención automática

#### **Beneficios de Docker**
- ✅ **Portabilidad**: Ejecución consistente en cualquier entorno
- ✅ **Escalabilidad**: Fácil escalado horizontal
- ✅ **Aislamiento**: Servicios independientes y seguros
- ✅ **Reproducibilidad**: Mismas condiciones en dev/prod

---

## 7. Microservicios del Sistema

### 🔧 Service Registry (Eureka Server)

**Puerto:** 8761  
**Tecnología:** Spring Cloud Eureka

#### **Responsabilidades:**
- Descubrimiento automático de servicios
- Health monitoring de microservicios
- Balanceo de carga
- Registro dinámico de instancias

#### **Endpoints:**
- `GET /eureka/apps` - Lista todos los servicios registrados
- `GET /actuator/health` - Health check del registry

#### **Configuración:**
```yaml
server:
  port: 8761
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

---

### 🚪 API Gateway

**Puerto:** 8080  
**Tecnología:** Spring Cloud Gateway

#### **Responsabilidades:**
- Routing centralizado de peticiones
- Configuración CORS global
- Filtrado JWT para endpoints protegidos
- Balanceo de carga con Eureka

#### **Rutas Configuradas:**
```yaml
routes:
  - id: auth-service
    uri: lb://ms-auth-service
    predicates:
      - Path=/api/v1/auth/**
  - id: finance-service
    uri: lb://ms-finance-service
    predicates:
      - Path=/api/v1/finance/**
```

#### **Filtros JWT:**
- Validación automática de tokens
- Exclusión de endpoints públicos (login, register)
- Propagación de userId en headers

---

### 🔐 Auth Service

**Puerto:** 8081  
**Tecnología:** Spring Boot + Spring Security + JWT

#### **Responsabilidades:**
- Autenticación de usuarios
- Generación y validación de tokens JWT
- Registro de nuevos usuarios
- Gestión de sesiones

#### **Endpoints Principales:**
- `POST /api/v1/auth/login` - Autenticación
- `POST /api/v1/auth/register` - Registro
- `POST /api/v1/auth/refresh` - Refrescar token
- `POST /api/v1/auth/logout` - Cierre de sesión

#### **Configuración de Seguridad:**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
```

#### **JWT Configuration:**
- **Secret**: Configurable via environment variable
- **Expiration**: 24 horas (configurable)
- **Algorithm**: HMAC SHA-256

---

### 💰 Finance Service

**Puerto:** 8083  
**Tecnología:** Spring Boot + Spring Data JPA

#### **Responsabilidades:**
- Gestión de movimientos financieros
- Gestión de categorías
- Gestión de presupuestos por año/mes
- Cálculos de ejecución presupuestaria

#### **Módulos Principales:**

**Categorías:**
- `GET /api/v1/finance/categorias` - Listar categorías
- `POST /api/v1/finance/categorias` - Crear categoría
- `PUT /api/v1/finance/categorias/{id}` - Actualizar categoría
- `DELETE /api/v1/finance/categorias/{id}` - Eliminar categoría

**Movimientos:**
- `GET /api/v1/finance/movimientos` - Listar movimientos
- `POST /api/v1/finance/movimientos` - Crear movimiento
- `PUT /api/v1/finance/movimientos/{id}` - Actualizar movimiento
- `DELETE /api/v1/finance/movimientos/{id}` - Eliminar movimiento

**Presupuestos:**
- `GET /api/v1/finance/presupuestos` - Listar presupuestos
- `POST /api/v1/finance/presupuestos` - Crear presupuesto
- `GET /api/v1/finance/presupuestos/ejecucion` - Ejecución presupuestaria
- `PUT /api/v1/finance/presupuestos/{id}` - Actualizar presupuesto

#### **Configuración JPA:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/finanzas_db
    username: admin
    password: admin123
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

---

## 8. Base de Datos

### PostgreSQL Configuration

**Container Docker:**
```yaml
services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: finanzas_db
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: admin123
    ports:
      - "5432:5432"
```

### Esquema de Base de Datos

#### **Tabla: usuarios**
```sql
CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(200),
    iniciales VARCHAR(5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### **Tabla: categorias**
```sql
CREATE TABLE categorias (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('INGRESO', 'EGRESO')),
    descripcion TEXT,
    color VARCHAR(7) DEFAULT '#3F51B5',
    icono VARCHAR(50) DEFAULT 'category',
    usuario_id UUID NOT NULL REFERENCES usuarios(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(usuario_id, nombre)
);
```

#### **Tabla: movimientos**
```sql
CREATE TABLE movimientos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    descripcion VARCHAR(200) NOT NULL,
    monto DECIMAL(15,2) NOT NULL CHECK (monto > 0),
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('INGRESO', 'EGRESO')),
    fecha DATE NOT NULL,
    categoria_id UUID NOT NULL REFERENCES categorias(id),
    usuario_id UUID NOT NULL REFERENCES usuarios(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### **Tabla: presupuestos**
```sql
CREATE TABLE presupuestos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    categoria_id UUID NOT NULL REFERENCES categorias(id),
    usuario_id UUID NOT NULL REFERENCES usuarios(id),
    anio INTEGER NOT NULL CHECK (anio >= 2020 AND anio <= 2050),
    mes INTEGER NOT NULL CHECK (mes >= 1 AND mes <= 12),
    monto_limite DECIMAL(15,2) NOT NULL CHECK (monto_limite > 0),
    activo BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(categoria_id, usuario_id, anio, mes)
);
```

### Índices y Optimización

```sql
-- Índices para performance
CREATE INDEX idx_movimientos_usuario_fecha ON movimientos(usuario_id, fecha DESC);
CREATE INDEX idx_movimientos_categoria ON movimientos(categoria_id);
CREATE INDEX idx_presupuestos_usuario_periodo ON presupuestos(usuario_id, anio, mes);
CREATE INDEX idx_categorias_usuario_tipo ON categorias(usuario_id, tipo);
```

### Datos de Prueba

**Usuario Administrador:**
```sql
INSERT INTO usuarios (username, email, password_hash, nombre_completo, iniciales)
VALUES ('admin', 'admin@finanzas.com', '$2a$12$hashedpassword', 'Juan Carlos Pérez López', 'JP');
```

**Categorías por Defecto:**
```sql
INSERT INTO categorias (nombre, tipo, descripcion, usuario_id)
VALUES 
  ('Salario', 'INGRESO', 'Ingreso mensual principal', UUID_ADMIN),
  ('Alimentación', 'EGRESO', 'Compras de supermercado y restaurantes', UUID_ADMIN),
  ('Transporte', 'EGRESO', 'Gasolina, transporte público, taxis', UUID_ADMIN),
  ('Vivienda', 'EGRESO', 'Alquiler, servicios básicos', UUID_ADMIN);
```

---

## 9. API REST Endpoints

### Base URL: `http://localhost:8080/api/v1`

### Autenticación (Auth Service)

#### **POST /auth/login**
Autenticación de usuarios

**Request:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Login exitoso",
  "userId": "529b2d71-f938-4208-a2d2-a134b1f737f4",
  "username": "admin",
  "email": "admin@finanzas.com",
  "nombreCompleto": "Juan Carlos Pérez López",
  "iniciales": "JP",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### **POST /auth/register**
Registro de nuevos usuarios

**Request:**
```json
{
  "username": "newuser",
  "email": "user@example.com",
  "password": "password123",
  "nombreCompleto": "Usuario Nuevo"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Usuario registrado correctamente",
  "data": {
    "userId": "uuid-del-nuevo-usuario",
    "username": "newuser",
    "email": "user@example.com"
  }
}
```

### Finanzas (Finance Service)

#### **Categorías**

**GET /finance/categorias**
Listar todas las categorías del usuario

**Headers:**
```
X-User-Id: uuid-del-usuario
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Categorías listadas correctamente",
  "data": [
    {
      "id": "uuid-categoria",
      "nombre": "Alimentación",
      "tipo": "EGRESO",
      "descripcion": "Compras de supermercado",
      "color": "#D32F2F",
      "icono": "restaurant",
      "createdAt": "2026-03-26T00:00:00Z"
    }
  ]
}
```

**POST /finance/categorias**
Crear nueva categoría

**Request:**
```json
{
  "nombre": "Entretenimiento",
  "tipo": "EGRESO",
  "descripcion": "Cine, juegos, salidas",
  "color": "#9C27B0",
  "icono": "movie"
}
```

#### **Movimientos**

**GET /finance/movimientos**
Listar movimientos con filtros opcionales

**Query Parameters:**
- `categoriaId` (opcional): Filtrar por categoría
- `tipo` (opcional): INGRESO|EGRESO
- `fechaDesde` (opcional): Fecha inicial YYYY-MM-DD
- `fechaHasta` (opcional): Fecha final YYYY-MM-DD

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Movimientos listados correctamente",
  "data": [
    {
      "id": "uuid-movimiento",
      "descripcion": "Supermercado Éxito",
      "monto": 250000.00,
      "tipo": "EGRESO",
      "fecha": "2026-03-25",
      "categoria": {
        "id": "uuid-categoria",
        "nombre": "Alimentación",
        "tipo": "EGRESO"
      },
      "createdAt": "2026-03-25T15:30:00Z"
    }
  ]
}
```

**POST /finance/movimientos**
Crear nuevo movimiento

**Request:**
```json
{
  "descripcion": "Salario mensual",
  "monto": 3500000.00,
  "tipo": "INGRESO",
  "fecha": "2026-03-25",
  "categoriaId": "uuid-categoria-salario"
}
```

#### **Presupuestos**

**GET /finance/presupuestos**
Listar presupuestos del usuario

**Query Parameters:**
- `anio` (opcional): Año específico
- `mes` (opcional): Mes específico (1-12)
- `activo` (opcional): true|false

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Presupuestos listados correctamente",
  "data": [
    {
      "id": "uuid-presupuesto",
      "categoria": {
        "id": "uuid-categoria",
        "nombre": "Alimentación",
        "tipo": "EGRESO"
      },
      "anio": 2026,
      "mes": 3,
      "montoLimite": 800000.00,
      "activo": true,
      "montoEjecutado": 450000.00,
      "porcentajeEjecutado": 56.25,
      "estado": "DENTRO",
      "createdAt": "2026-03-01T00:00:00Z"
    }
  ]
}
```

**GET /finance/presupuestos/ejecucion**
Obtener ejecución presupuestaria completa

**Query Parameters:**
- `anio` (opcional): Año específico, por defecto año actual
- `mes` (opcional): Mes específico, por defecto mes actual

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Ejecución presupuestaria calculada correctamente",
  "data": {
    "periodo": {
      "anio": 2026,
      "mes": 3,
      "descripcion": "Marzo 2026"
    },
    "resumen": {
      "totalPresupuestado": 3500000.00,
      "totalEjecutado": 2100000.00,
      "porcentajeEjecucion": 60.0,
      "saldoDisponible": 1400000.00
    },
    "categorias": [
      {
        "categoria": "Alimentación",
        "presupuestado": 800000.00,
        "ejecutado": 450000.00,
        "porcentaje": 56.25,
        "estado": "DENTRO",
        "saldo": 350000.00
      }
    ]
  }
}
```

### Health Checks

**GET /actuator/health**
Health check del servicio

**Response (200 OK):**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 250685575168,
        "free": 125342787584,
        "threshold": 10485760
      }
    }
  }
}
```

---

## 10. Seguridad y Autenticación

### 🔐 Arquitectura de Seguridad

#### **JWT Tokens**
- **Algoritmo**: HMAC SHA-256
- **Secret**: Configurable via `JWT_SECRET` environment variable
- **Expiration**: 24 horas (configurable via `JWT_EXPIRATION`)
- **Claims**: userId, username, email, exp, iat

#### **Spring Security Configuration**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated());
        return http.build();
    }
}
```

### 🛡️ JWT Authentication Filter

#### **JwtAuthenticationFilter (API Gateway)**
```java
@Component
public class JwtAuthenticationFilter implements GlobalFilter {
    
    @Value("${jwt.secret}")
    private String secret;
    
    private static final List<String> EXCLUDED_PATHS = List.of(
        "/api/v1/auth/login",
        "/api/v1/auth/register"
    );
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        
        if (isExcludedPath(path)) {
            return chain.filter(exchange);
        }
        
        String authHeader = exchange.getRequest().getHeaders()
            .getFirst(HttpHeaders.AUTHORIZATION);
            
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            if (validateToken(token)) {
                // Extraer información del token
                String userId = extractUserId(token);
                String username = extractUsername(token);
                
                // Agregar headers para microservicios downstream
                return chain.filter(exchange.mutate()
                    .request(exchange.getRequest().mutate()
                        .header("X-User-Id", userId)
                        .header("X-User-Username", username)
                        .build())
                    .build());
            }
        }
        
        return handleUnauthorized(exchange);
    }
}
```

### 🔑 Password Hashing

#### **bcrypt Configuration**
```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
```

#### **User Registration**
```java
@Service
public class AuthService {
    
    private final PasswordEncoder passwordEncoder;
    
    public LoginResponse register(RegisterRequest request) {
        // Hash password con bcrypt
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        
        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setPasswordHash(hashedPassword);
        usuario.setNombreCompleto(request.getNombreCompleto());
        
        usuario = usuarioRepository.save(usuario);
        
        // Generar token JWT
        String token = jwtUtil.generateToken(usuario);
        
        return LoginResponse.builder()
            .success(true)
            .message("Usuario registrado correctamente")
            .userId(usuario.getId())
            .username(usuario.getUsername())
            .email(usuario.getEmail())
            .token(token)
            .build();
    }
}
```

### 🚨 Seguridad Adicional

#### **CORS Configuration**
```yaml
# application.yml - API Gateway
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOrigins:
              - http://localhost:5173
              - http://127.0.0.1:5173
            allowedMethods:
              - GET, POST, PUT, DELETE, OPTIONS, PATCH
            allowedHeaders:
              - "*"
            allowCredentials: true
            maxAge: 3600
```

#### **Environment Variables**
```bash
# .env
JWT_SECRET=mySecretKey123456789012345678901234567890
JWT_EXPIRATION=86400000
DB_HOST=localhost
DB_PORT=5432
DB_NAME=finanzas_db
DB_USER=admin
DB_PASSWORD=admin123
```

---

## 11. Testing y Validación

### 🧪 Framework de Testing

#### **Dependencias Maven**
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 🔍 Tests Unitarios

#### **AuthService Tests**
```java
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    
    @Mock
    private UsuarioRepository usuarioRepository;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private AuthService authService;
    
    @Test
    void login_ValidCredentials_ReturnsSuccess() {
        // Given
        String username = "admin";
        String password = "admin123";
        
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setUsername(username);
        usuario.setPasswordHash("$2a$12$hashedpassword");
        
        when(usuarioRepository.findByUsername(username))
            .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(password, usuario.getPasswordHash()))
            .thenReturn(true);
        when(jwtUtil.generateToken(usuario))
            .thenReturn("jwt-token");
        
        // When
        LoginResponse response = authService.login(new LoginRequest(username, password));
        
        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getUsername()).isEqualTo(username);
    }
}
```

#### **CategoriaService Tests**
```java
@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {
    
    @Mock
    private CategoriaRepository categoriaRepository;
    
    @InjectMocks
    private CategoriaService categoriaService;
    
    @Test
    void crearCategoria_ValidData_ReturnsCategoriaResponse() {
        // Given
        UUID userId = UUID.randomUUID();
        CategoriaRequest request = new CategoriaRequest(
            "Nueva Categoría", 
            "EGRESO", 
            "Descripción", 
            "#FF5722", 
            "category"
        );
        
        Categoria savedCategoria = new Categoria();
        savedCategoria.setId(UUID.randomUUID());
        savedCategoria.setNombre(request.getNombre());
        savedCategoria.setTipo(request.getTipo());
        
        when(categoriaRepository.save(any(Categoria.class)))
            .thenReturn(savedCategoria);
        
        // When
        CategoriaResponse response = categoriaService.crearCategoria(request, userId);
        
        // Then
        assertThat(response.getNombre()).isEqualTo(request.getNombre());
        assertThat(response.getTipo()).isEqualTo(request.getTipo());
        verify(categoriaRepository).save(any(Categoria.class));
    }
}
```

### 🔄 Tests de Integración

#### **AuthController Integration Tests**
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AuthControllerIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Test
    void login_ValidCredentials_ReturnsToken() {
        // Given
        LoginRequest request = new LoginRequest("admin", "admin123");
        
        // When
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
            "/api/v1/auth/login", request, ApiResponse.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData()).isNotNull();
    }
}
```

### 📊 Tests de Performance

#### **Load Testing con JMeter**
```xml
<!-- jmeter-test-plan.jmx -->
<TestPlan>
  <ThreadGroup>
    <stringProp name="ThreadGroup.num_threads">100</stringProp>
    <stringProp name="ThreadGroup.ramp_time">10</stringProp>
    <stringProp name="ThreadGroup.duration">60</stringProp>
  </ThreadGroup>
  
  <HTTPSamplerProxy>
    <stringProp name="HTTPSampler.domain">localhost</stringProp>
    <stringProp name="HTTPSampler.port">8080</stringProp>
    <stringProp name="HTTPSampler.path">/api/v1/finance/categorias</stringProp>
    <stringProp name="HTTPSampler.method">GET</stringProp>
  </HTTPSamplerProxy>
</TestPlan>
```

### 🔧 Ejecutar Tests

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests específicos
mvn test -Dtest=AuthServiceTest

# Ejecutar tests con cobertura
mvn jacoco:report

# Ejecutar tests de integración
mvn verify -P integration-tests
```

### 📈 Reports de Testing

#### **Jacoco Coverage Report**
```bash
mvn jacoco:report
# Genera report en target/site/jacoco/index.html
```

#### **Surefire Test Report**
```bash
mvn surefire-report:report
# Genera report en target/site/surefire-report.html
```

---

## 12. Mejoras Futuras

### 🚀 Mejoras Técnicas

#### **Corto Plazo (1-3 meses)**
1. **Testing Automatizado**
   - Cobertura de tests > 80%
   - Tests E2E con Cypress
   - Tests de performance con JMeter

2. **Monitoring y Observabilidad**
   - Prometheus + Grafana
   - ELK Stack (Elasticsearch, Logstash, Kibana)
   - Distributed tracing con Zipkin

3. **Performance Optimización**
   - Caching con Redis
   - Connection pooling optimizado
   - Query optimization

#### **Mediano Plazo (3-6 meses)**
1. **Security Enhancements**
   - OAuth 2.0 / OpenID Connect
   - Rate limiting con Redis
   - API Key management
   - Security headers hardening

2. **Data Management**
   - Event sourcing con Kafka
   - CQRS pattern
   - Database sharding
   - Backup automático

3. **API Improvements**
   - GraphQL endpoints
   - API versioning strategy
   - Async processing con RabbitMQ
   - Batch processing

#### **Largo Plazo (6+ meses)**
1. **Microservices Advanced**
   - Service mesh con Istio
   - Circuit breaker con Hystrix
   - Distributed configuration
   - Blue-green deployments

2. **Machine Learning Integration**
   - Anomaly detection en transacciones
   - Categorización automática
   - Predictive analytics
   - Recommendation engine

### 📊 Mejoras de Funcionalidad

#### **Financial Features**
1. **Advanced Budgeting**
   - Presupuestos recursivos
   - Metas de ahorro
   - Forecasting
   - Scenario planning

2. **Investment Tracking**
   - Portfolio management
   - Real-time pricing
   - Performance analytics
   - Risk assessment

3. **Reporting & Analytics**
   - Custom reports
   - Data visualization
   - Export capabilities
   - Compliance reporting

#### **Integration Features**
1. **Bank Integration**
   - Plaid API integration
   - Transaction import
   - Account aggregation
   - Real-time sync

2. **Third-party Services**
   - Payment processing
   - Notification services
   - Tax calculation
   - Credit scoring

---

## 13. Licencia

### Propósito del Proyecto
- **Educativo**: Demostrar aplicación práctica de microservicios y Spring Boot
- **Formativo**: Servir como portafolio de competencias backend
- **Tecnológico**: Cumplir requisitos del programa tecnológico del SENA

### Restricciones de Uso
- **No Comercial**: Proyecto educativo sin fines lucrativos
- **Uso Académico**: Referencia para proyectos similares
- **Atribución**: Reconocer autoría en caso de referencia

### Tecnologías y Licencias
- **Spring Boot**: Apache License 2.0
- **PostgreSQL**: PostgreSQL License
- **Docker**: Apache License 2.0
- **JWT**: MIT License

---

## 🎯 Estado Final del Proyecto

### ✅ Backend Completo y Funcional
El backend de "Gestión Financiera" representa una implementación profesional de microservicios con Spring Boot, demostrando competencias avanzadas en desarrollo backend moderno.

### 🏆 Logros Alcanzados
- ✅ **Microservicios Completos**: Auth, Finance, Gateway, Registry
- ✅ **Seguridad Robusta**: JWT, Spring Security, bcrypt
- ✅ **Base de Datos**: PostgreSQL con JPA/Hibernate
- ✅ **API RESTful**: Endpoints bien documentados
- ✅ **Testing**: Unitarios y de integración
- ✅ **Documentation**: README completo y API docs

### 📈 Impacto Técnico
- **Arquitectura**: Microservicios escalables y mantenibles
- **Calidad**: Código limpio con principios SOLID
- **Performance**: Optimizado para producción
- **Seguridad**: Mejores prácticas de seguridad

---

## 📞 Contacto y Soporte

Para consultas técnicas sobre este backend de microservicios, puede referirse a este proyecto como ejemplo de implementación de arquitectura moderna con Spring Boot en el contexto del programa tecnológico del SENA.

**Nota**: Este software se proporciona "tal cual" sin garantías, siendo su principal objetivo el aprendizaje y la demostración de competencias técnicas en desarrollo backend.
