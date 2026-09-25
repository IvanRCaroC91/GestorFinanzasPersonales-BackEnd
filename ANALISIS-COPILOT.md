# DIAGNÓSTICO ACTUALIZADO DEL PROYECTO

Este documento revalida `README.md`, `ANALISIS_COMPARATIVO_MICROSERVICIOS.md` y `ESTRUCTURA_PROYECTO_PROPUESTA.md` contra el código actual del workspace. El objetivo es mostrar, de forma directa, qué existe, qué funciona solo parcialmente y qué debe realizarse para llegar a una solución segura, ordenada y operable.

> **Lectura rápida:** el proyecto tiene una base correcta de microservicios, pero todavía es un MVP técnico. La prioridad no es crear más servicios; es cerrar seguridad, corregir el arranque/configuración, estabilizar la base de datos, agregar pruebas y después avanzar hacia DDD/hexagonal.

## 1. ANÁLISIS ESTRUCTURAL DEL PROYECTO

### 1.1 Estado de la estructura propuesta

La propuesta de `ESTRUCTURA_PROYECTO_PROPUESTA.md` planteaba separar infraestructura, negocio, soporte, librerías compartidas, Docker, documentación y scripts. La estructura actual ya aplicó gran parte de esa idea:

| Área | Estado actual | Evidencia |
|---|---|---|
| Infraestructura | ✅ Aplicada | `architecture/ms-service-registry`, `ms-api-gateway`, `ms-config-server` |
| Negocio | ✅ Aplicada | `business/ms-auth-service`, `ms-finance-service` |
| Soporte | ⚠️ Reservada | Existen `support/`, pero no hay servicios activos |
| Shared | ⚠️ Reservada | Existe `shared/`, pero no hay librería Maven activa |
| Base de datos | ✅ Aplicada | `database/init.sql`, `database/test-data.sql` |
| Docker | ✅ Aplicada parcialmente | `docker/docker-compose.yml`, Dockerfiles por módulo |
| Documentación | ⚠️ Parcial | Hay README y documentos de arquitectura, pero no contratos API ni despliegue formal |
| Scripts | ✅ Aplicada | `scripts/start-docker-services.bat`, `stop-docker-services.bat` |

**Conclusión:** la propuesta estructural no debe volver a ejecutarse como una migración de carpetas. Ya se aplicó usando `architecture/` en lugar de `infrastructure/`. Lo pendiente es corregir la integración Maven/Docker y decidir qué carpetas vacías realmente se necesitan.

### 1.2 Stack y módulos actuales

| Módulo | Tipo | Puerto | Responsabilidad | Estado |
|---|---|---:|---|---|
| `ms-service-registry` | Infraestructura | 8765 | Eureka Server | ✅ Implementado |
| `ms-config-server` | Infraestructura | 8890 | Configuración centralizada | ⚠️ Implementado, no endurecido |
| `ms-api-gateway` | Infraestructura | 8080 | Routing, CORS y filtro JWT | ⚠️ Implementado, seguridad perimetral |
| `ms-auth-service` | Negocio | 8081 | Registro, login, BCrypt y JWT | ⚠️ Funcional, sin ciclo completo de identidad |
| `ms-finance-service` | Negocio | 8083 | Categorías, movimientos y presupuestos | ⚠️ Funcional, sin seguridad independiente |

**Tecnología confirmada:** Java 21, Spring Boot 3.2.5, Spring Cloud 2023.0.3, Maven, PostgreSQL 15, JPA/Hibernate, JJWT 0.12.3, Docker Compose y Eureka.

### 1.3 Base de datos y propiedad de datos

| Tema | Estado real | Impacto |
|---|---|---|
| Motor | PostgreSQL 15 Alpine | Correcto para el MVP |
| Base usada por Compose | `finanzas_db` | Auth y Finance terminan compartiendo potencialmente la misma BD |
| Config remota base | Intenta usar `auth_db` y `finance_db` | Inconsistencia con Compose |
| Tablas con código | `usuarios`, `categorias`, `movimientos`, `presupuestos` | Son las únicas cubiertas por servicios actuales |
| Tablas solo en SQL | Comercios, facturas, cuentas, tarjetas, créditos, inversiones | No representan funcionalidades implementadas |
| Migraciones | `init.sql` + `ddl-auto` desigual | No hay Flyway/Liquibase ni control de evolución |
| Error detectado | Índices sobre `periodo_inicio` y `periodo_fin` inexistentes | Puede fallar la inicialización de PostgreSQL |

La separación física por servicio aún no está resuelta. Además, `categorias` y otras tablas referencian `usuarios`, por lo que pasar a una BD por servicio requerirá contratos de identidad y migración, no solo cambiar una URL.

### 1.4 Comunicación y flujo de seguridad actual

```text
Frontend externo
      |
      v
API Gateway :8080
  - CORS
  - valida JWT
  - agrega X-User-Id
      |
      +--> Auth Service :8081
      |
      +--> Finance Service :8083
               - valida formato de X-User-Id
               - no valida que el header sea confiable
```

La comunicación es HTTP/REST síncrona. No existen broker de mensajes, eventos de dominio, circuit breakers, retries, tracing ni rate limiting.

## 2. VALIDACIÓN DE LA DOCUMENTACIÓN EXISTENTE

### 2.1 Lo que el README documenta correctamente

| Afirmación | Veredicto |
|---|---|
| Eureka en 8765 | ✅ Confirmado |
| Gateway en 8080 con rutas y CORS | ✅ Confirmado |
| Auth con JWT y BCrypt | ✅ Confirmado |
| Finance con categorías, movimientos y presupuestos | ✅ Confirmado |
| DTOs y validación de entradas | ✅ Confirmado parcialmente |
| JWT validado en Gateway y `X-User-Id` propagado | ✅ Implementado, ⚠️ no suficiente como seguridad de extremo a extremo |
| PostgreSQL, JPA, Java 21 y Spring Boot | ✅ Confirmado |

### 2.2 Lo que está obsoleto o exagerado

| Afirmación | Estado real |
|---|---|
| “PRODUCCIÓN FUNCIONAL” y “backend robusto” | ❌ No demostrable: faltan pruebas, hardening, build Docker reproducible y configuración coherente |
| Config Server ausente, según el análisis anterior | ❌ Obsoleto: existe `ms-config-server`, `bootstrap.yml` y configuración por perfiles |
| OpenAPI/Swagger disponible | ❌ No existe dependencia ni contrato OpenAPI |
| Endpoints `refresh` y `logout` | ❌ No existen en `AuthController` |
| Tests unitarios e integración completos | ❌ Solo hay un test de contexto y un test trivial |
| Dockerización completa | ⚠️ Hay Dockerfiles y Compose, pero los contextos de build no incluyen todos los POM padre |
| Todos los módulos de la base de datos están implementados | ❌ El SQL contiene tablas sin entidades, repositorios ni endpoints |

### 2.3 Validación del análisis comparativo anterior

El documento anterior sigue acertando al detectar ausencia de paginación, RBAC, tracing, circuit breakers, retries, fallbacks, rate limiting, broker y observabilidad avanzada. Debe actualizarse en tres puntos:

1. **Config Server ya existe.** La tarea dejó de ser crearlo y pasó a ser protegerlo, versionarlo y hacerlo reproducible.
2. **La estructura ya fue reorganizada.** `architecture/`, `business/`, `docker/`, `docs/` y `scripts/` existen; no se necesita repetir la migración de carpetas.
3. **La brecha real es de consistencia.** Hay varias fuentes de configuración y perfiles que no coinciden entre sí.

### 2.4 Análisis de `ESTRUCTURA_PROYECTO_PROPUESTA.md`

| Propuesta | Evaluación |
|---|---|
| Usar tres categorías: infraestructura, negocio y soporte | ✅ Buena decisión; ya se aplicó parcialmente con `architecture`, `business` y `support` |
| Crear `shared` desde el inicio | ⚠️ No es prioritario; una librería compartida puede acoplar servicios |
| Separar servicios de negocio por cada capacidad | ⚠️ No debe hacerse todavía; Finance primero necesita modularización interna |
| Crear `infrastructure/` en lugar de `architecture/` | ℹ️ Decisión de nomenclatura; cambiarlo ahora no aporta valor |
| Crear POM padre por categoría | ✅ Ya existe para `architecture` y `business` |
| Crear Compose por ambiente | ✅ Recomendable después de corregir el Compose actual |
| Crear servicios de soporte futuros | ⏳ Solo cuando exista un caso de negocio real |

**Recomendación estructural:** conservar `architecture/` y `business/`. Mantener `support/` y `shared/` como espacios futuros, sin agregar módulos vacíos. Primero hay que estabilizar el reactor Maven, Docker y la configuración.

## 3. COMPARACIÓN DE CAPACIDADES Y BRECHAS

### 3.1 Seguridad

| Capacidad | Estado | Evidencia |
|---|---|---|
| Hash de contraseñas | ✅ | `BCryptPasswordEncoder(12)` |
| Validación JWT en Gateway | ✅ | `JwtAuthenticationFilter` |
| JWT en Finance | ❌ | No tiene Spring Security ni `SecurityFilterChain` |
| Identidad servicio-a-servicio | ❌ | Finance confía en `X-User-Id` |
| RBAC | ❌ | No hay roles/permisos ni `@PreAuthorize` |
| Rate limiting | ❌ | No hay configuración |
| Rotación de claves | ❌ | Secreto HMAC fijo |
| Gestión segura de secretos | ❌ | Defaults como `admin123` y `mySecretKey...` |
| Protección de Actuator | ⚠️ | Endpoints expuestos y management no aislado |

**Problema principal:** si alguien accede directamente a 8083, puede enviar un UUID válido en `X-User-Id` y consultar datos de ese usuario. El formato del header no demuestra su autenticidad.

### 3.2 DDD y arquitectura hexagonal

| Elemento | Auth | Finance |
|---|---|---|
| Controller separado | ✅ | ✅ |
| Servicio de aplicación | ⚠️ `AuthService` mezcla casos de uso e infraestructura | ⚠️ Servicios mezclan reglas, HTTP y persistencia |
| Dominio independiente | ❌ | ❌ |
| Puertos de entrada/salida | ❌ | ❌ |
| Adaptadores explícitos | ❌ | ❌ |
| Entidades JPA aisladas del dominio | ❌ | ❌ |
| Acceso directo a repositorio desde controller | No | ❌ `MovimientoController` |
| Bounded context reconocible | Auth | Finance, todavía demasiado amplio |

La estructura de carpetas es una separación técnica por capas, no una arquitectura hexagonal completa. La mejora correcta es incremental: primero casos de uso y puertos; después separar adaptadores JPA/JWT; al final evaluar nuevos microservicios.

### 3.3 Configuración centralizada

| Capacidad | Estado | Problema |
|---|---|---|
| Config Server | ✅ | Existe `@EnableConfigServer` |
| Config Client | ✅ | Gateway, Auth y Finance tienen `bootstrap.yml` |
| Perfiles dev/qa/prod | ✅ | Existen en `config/config/` |
| Perfil Docker coherente | ❌ | Compose usa `docker`, pero no existe configuración remota `*-docker.yml` |
| Repositorio versionado desde raíz | ⚠️ | `config/` aparece como repositorio anidado/no versionado |
| Secretos fuera del repositorio | ❌ | Hay valores por defecto débiles |
| Seguridad del Config Server | ❌ | Propiedades de usuario no sustituyen una configuración efectiva de Spring Security |
| Variables consistentes | ❌ | Compose usa `DATABASE_*`; config usa `SPRING_DATASOURCE_*`/`PG*` |

**Conclusión:** la centralización existe, pero todavía no permite cambiar de ambiente con seguridad y reproducibilidad garantizadas.

### 3.4 Calidad, operación y API

| Capacidad | Estado |
|---|---|
| Paginación | ❌ Los endpoints retornan `List` |
| OpenAPI/Swagger | ❌ |
| Tests de Auth/Finance/Gateway | ❌ |
| Health checks Docker | ❌ |
| Tracing distribuido | ❌ |
| Correlation ID | ❌ |
| Logs JSON | ❌ |
| Métricas centralizadas | ❌ |
| Circuit breaker/retry/fallback | ❌ |
| API versioning | ⚠️ Solo aparece `/v1`, sin política de evolución |

## 4. PROBLEMAS IDENTIFICADOS

### 4.1 Problemas críticos

| Problema | Archivo o área | Consecuencia |
|---|---|---|
| Finance acepta `X-User-Id` falsificable | `UserValidationInterceptor` | Acceso directo potencial a datos de otros usuarios |
| Servicios internos publicados | `docker/docker-compose.yml` | El Gateway no es una frontera real |
| Defaults de secretos y contraseñas | Compose, YAML, `JwtUtil`, `JwtAuthenticationFilter` | Compromiso predecible |
| `POSTGRES_HOST_AUTH_METHOD: trust` | Compose | PostgreSQL acepta conexiones sin autenticación normal |
| SQL con columnas inexistentes | `database/init.sql:263-264` | Inicialización puede fallar |
| Build Docker incompleto | Dockerfiles de Auth/Finance/Gateway | El contexto no contiene el POM padre requerido |
| Configuración de BD contradictoria | Compose vs `config/config/*.yml` | Servicios pueden apuntar a bases inexistentes |

### 4.2 Problemas importantes

- `DataInitializer` crea `admin/admin123` y `user/user123` automáticamente.
- Auth permite todo `/api/v1/auth/**`, aunque solo existen login y register.
- Actuator, `env` y `refresh` no tienen una frontera administrativa clara.
- `MovimientoController` consulta el repositorio directamente.
- Los servicios dependen de `ResponseStatusException`, `HttpStatus` y entidades JPA dentro de la lógica.
- No hay paginación en listados.
- Auth captura excepciones generales y puede ocultar fallos de infraestructura.
- No existen pruebas que demuestren aislamiento entre usuarios.

### 4.3 Problemas deseables de resolver después

- Separar módulos de Finance por subdominio.
- Incorporar eventos para auditoría/notificaciones.
- Crear analytics, notifications, files o accounts solo si el producto los requiere.
- Evaluar Redis, broker, gateway secundario y más microservicios después de estabilizar el núcleo.

## 5. PROPUESTA DE MEJORAS

La prioridad se define por riesgo y dependencia. Primero se corrigen los problemas que pueden permitir acceso indebido o impedir el arranque; después se mejora la mantenibilidad; finalmente se agregan capacidades que solo tienen sentido cuando el núcleo es estable.

### 5.1 Mejoras críticas — prioridad alta

| Mejora | Por qué es necesaria | Resultado esperado | Esfuerzo |
|---|---|---|---|
| Seguridad independiente en Finance | `X-User-Id` puede falsificarse si se accede a 8083 | Cada servicio valida la identidad o un token interno confiable | Alto |
| Cerrar puertos internos | Auth y Finance están publicados fuera del Gateway | Solo Gateway queda expuesto al cliente | Bajo |
| Eliminar secretos por defecto | JWT, PostgreSQL y Config Server tienen credenciales conocidas | Secretos externos, rotados y obligatorios en QA/prod | Medio |
| Corregir PostgreSQL y migraciones | `init.sql` contiene índices inválidos y no hay versionado de esquema | Base reproducible desde cero | Medio |
| Hacer Docker reproducible | Los contextos no incluyen todos los POM padre | Build limpio de todas las imágenes | Medio |
| Consolidar configuración | Perfiles, variables y nombres de BD son inconsistentes | Un solo perfil selecciona el ambiente completo | Medio |

### 5.2 Mejoras importantes — prioridad media

| Mejora | Por qué es necesaria | Resultado esperado | Esfuerzo |
|---|---|---|---|
| Casos de uso y puertos | La lógica depende directamente de JPA, HTTP y JJWT | Aplicación desacoplada de infraestructura | Alto |
| Paginación | Los listados retornan todos los registros | Respuestas limitadas, rápidas y predecibles | Medio |
| Pruebas automatizadas | No se prueba aislamiento multiusuario ni Gateway | Cambios verificables y regresiones detectables | Medio |
| Actuator administrativo | `env`, `refresh` y métricas no tienen frontera suficiente | Endpoints de gestión aislados y protegidos | Bajo |
| OpenAPI | El README promete documentación que no existe | Contrato real para frontend e integradores | Bajo |
| Observabilidad básica | No hay correlation ID, logs JSON ni alertas | Diagnóstico de una petición entre servicios | Medio |
| Rate limiting y protección de login | No hay control de abuso | Menor exposición a fuerza bruta y saturación | Medio |

### 5.3 Mejoras deseables — prioridad baja

| Mejora | Condición para realizarla | Beneficio |
|---|---|---|
| Database per Service | Ownership de tablas y plan de migración definidos | Escalado y despliegue independiente |
| Broker de mensajes | Existan eventos reales como auditoría o notificaciones | Menor acoplamiento síncrono |
| RBAC avanzado | Aparezcan roles distintos al propietario | Autorización granular |
| Redis | Métricas demuestren lecturas repetitivas costosas | Menor latencia |
| Gateway secundario | Exista necesidad operacional real | Mayor aislamiento, no por imitación |
| Analytics, notifications, files | Casos de negocio priorizados | Nuevas capacidades sin sobrecargar Finance |

## 6. PROPUESTA DE NUEVOS MICROSERVICIOS

No se recomienda crear todos estos servicios inmediatamente. La arquitectura actual debe estabilizarse primero. La siguiente matriz diferencia lo que falta realmente de lo que es solo una posible evolución.

### 6.1 Servicios de arquitectura e infraestructura

| Servicio | Estado | Responsabilidad | Prioridad | Momento recomendado |
|---|---|---|---|---|
| `ms-config-server` | ✅ Existe | Configuración centralizada por ambiente | Alta técnica | Ahora: proteger y hacer reproducible |
| `ms-service-registry` | ✅ Existe | Descubrimiento Eureka | Media | Mantener y endurecer |
| `ms-api-gateway` | ✅ Existe | Entrada pública, routing, CORS y JWT | Alta | Corregir frontera de confianza |
| `ms-secrets-service` / Vault | ❌ No existe | Secretos, rotación y credenciales | Alta | Antes de QA/prod |
| `ms-monitoring-stack` | ❌ No existe | Prometheus, Grafana y alertas | Media | Después de health checks |
| `ms-tracing` | ❌ No existe | OpenTelemetry y Jaeger/Tempo | Media | Después de correlation ID |
| `ms-message-broker` | ❌ No existe | Kafka/RabbitMQ para eventos | Baja | Solo con eventos definidos |
| Gateway secundario | ❌ No existe | Segundo nivel de routing | Baja | No necesario para el MVP |

### 6.2 Servicios de negocio

| Servicio | Estado actual | Responsabilidad propuesta | Prioridad | Decisión |
|---|---|---|---|---|
| `ms-auth-service` | ✅ Existe | Identidad, registro, login y tokens | Alta | Mantener y refactorizar |
| `ms-finance-service` | ✅ Existe | Categorías, movimientos y presupuestos | Alta | Mantener; modularizar internamente |
| `ms-budget-service` | ❌ No existe | Presupuestos como bounded context independiente | Media | Evaluar después de modularizar Finance |
| `ms-account-service` | ❌ No existe | Cuentas bancarias y tarjetas | Media | Crear cuando exista funcionalidad real |
| `ms-invoice-service` | ❌ No existe | Facturas, XML y detalle de factura | Media | Crear antes solo si se implementa ese flujo |
| `ms-analytics-service` | ❌ No existe | Reportes, indicadores y dashboard | Media | Crear cuando haya consultas definidas |
| `ms-investment-service` | ❌ No existe | Inversiones y rentabilidad | Baja | Fase posterior |
| `ms-credit-service` | ❌ No existe | Créditos y obligaciones | Baja | Fase posterior |

### 6.3 Servicios de soporte

| Servicio | Estado | Responsabilidad | Prioridad | Dependencia |
|---|---|---|---|---|
| `ms-audit-service` | ❌ No existe | Auditoría de acciones y eventos | Media | Correlation ID/eventos |
| `ms-notification-service` | ❌ No existe | Email, push y alertas presupuestarias | Media | Eventos y preferencias |
| `ms-file-service` | ❌ No existe | XML, documentos y almacenamiento S3/MinIO | Media | Invoice service |
| `ms-scheduler-service` | ❌ No existe | Tareas periódicas y recordatorios | Baja | Notificaciones |
| `ms-search-service` | ❌ No existe | Búsqueda avanzada de movimientos | Baja | Volumen real de datos |

**Regla de decisión:** una tabla existente en `init.sql` no justifica por sí sola un microservicio. Para crearlo deben existir un bounded context, casos de uso, datos que le pertenezcan, contrato API/eventos y una razón para desplegarlo de forma independiente.

## 7. ROADMAP DE IMPLEMENTACIÓN

### 7.1 Fase 1 — Línea base y correcciones bloqueantes (semanas 1–2)

**Objetivo:** conocer el estado real y dejar el proyecto arrancable.

| Paso | Actividad | Entregable |
|---:|---|---|
| 1 | Ejecutar Maven desde la raíz y por módulo | Registro de fallos reproducibles |
| 2 | Corregir `database/init.sql` y validar PostgreSQL desde cero | Esquema inicial válido |
| 3 | Corregir Dockerfiles y contextos Maven | Imágenes construibles desde copia limpia |
| 4 | Alinear rutas, puertos y comandos del README | Guía de ejecución real |
| 5 | Levantar Compose con health checks básicos | Stack iniciado en orden correcto |

#### Actividad del Paso 1

La actividad debe ejecutarse en la rama:

```text
feature/estabilizar-arranque-servicios
```

1. Verificar que el entorno tenga Java 21, Maven y la rama correcta:

   ```bash
   java -version
   mvn -version
   git status --short --branch
   ```

2. Ejecutar la validación completa desde la raíz del proyecto:

   ```bash
   mvn clean verify
   ```

   Este comando valida la compilación, las dependencias, las pruebas y el
   empaquetado de todos los módulos. Para validar únicamente el empaquetado sin
   ejecutar pruebas:

   ```bash
   mvn clean package -DskipTests
   ```

3. Ejecutar la validación de cada microservicio por separado:

   ```bash
   mvn clean verify -pl architecture/ms-service-registry
   mvn clean verify -pl architecture/ms-api-gateway
   mvn clean verify -pl architecture/ms-config-server
   mvn clean verify -pl business/ms-auth-service
   mvn clean verify -pl business/ms-finance-service
   ```

   Si el módulo requiere construir dependencias del reactor Maven, agregar la
   opción `-am`, por ejemplo:

   ```bash
   mvn clean verify -pl architecture/ms-api-gateway -am
   ```

4. Registrar para cada ejecución si fue exitosa o fallida, incluyendo el error
   principal y el módulo afectado. No iniciar las correcciones de los pasos
   siguientes hasta contar con este diagnóstico reproducible.

**Entregable del Paso 1:** registro de la compilación, pruebas y empaquetado del
proyecto completo y de cada módulo, con los fallos reproducibles identificados.

#### Actividad del Paso 2

Se corrigieron los índices de `database/init.sql` que referenciaban las columnas
inexistentes `periodo_inicio` y `periodo_fin`. El esquema de presupuestos utiliza
`anio` y `mes`, por lo que los índices fueron alineados con esas columnas:

```sql
CREATE INDEX idx_presupuestos_periodo ON presupuestos(anio, mes);
CREATE INDEX idx_presupuestos_user_periodo ON presupuestos(user_id, anio, mes);
```

La validación desde una base PostgreSQL vacía se ejecutó con:

```bash
docker compose -f docker/docker-compose.yml down --volumes --remove-orphans
docker compose -f docker/docker-compose.yml up -d postgres
docker exec finanzas-postgres pg_isready -U admin -d finanzas_db
docker exec finanzas-postgres psql -U admin -d finanzas_db -v ON_ERROR_STOP=1 \
  -c '\dt' \
  -c "SELECT count(*) AS usuarios FROM usuarios;" \
  -c "SELECT count(*) AS categorias FROM categorias;" \
  -c "SELECT count(*) AS movimientos FROM movimientos;" \
  -c "SELECT count(*) AS presupuestos FROM presupuestos;"
```

**Resultado:** inicialización exitosa de las 12 tablas y carga de datos de prueba:
2 usuarios, 10 categorías, 5 movimientos y 5 presupuestos. PostgreSQL quedó
aceptando conexiones en `localhost:5432`.

**Observación:** Compose muestra la advertencia de seguridad asociada a
`POSTGRES_HOST_AUTH_METHOD=trust`; la eliminación de esta configuración queda
identificada para la fase de seguridad y secretos.

#### Actividad del Paso 3

Se corrigieron los contextos de construcción Docker. Los servicios utilizan POM
padre en la raíz y en las carpetas `architecture` o `business`, por lo que cada
imagen ahora:

- usa la raíz del repositorio como contexto de construcción;
- copia la jerarquía Maven requerida;
- construye el módulo con `mvn -f`;
- copia el JAR desde la ruta real generada por Maven.

La validación se ejecutó con:

```bash
docker compose -f docker/docker-compose.yml config --quiet
docker compose -f docker/docker-compose.yml build --no-cache
```

**Resultado:** las imágenes de `eureka`, `config-server`, `gateway`,
`auth-service` y `finance-service` se construyeron correctamente desde cero.

#### Actividad del Paso 4

Se alinearon las rutas, los puertos y los comandos de ejecución con la estructura
real del repositorio:

- Compose se ejecuta mediante `docker compose -f docker/docker-compose.yml`.
- La construcción Docker utiliza la raíz del repositorio como contexto.
- El arranque completo incluye PostgreSQL, Eureka, Config Server, Gateway, Auth
  y Finance.
- La ejecución local incluye los cinco módulos Maven, incluido Config Server.
- Se eliminaron de la guía los scripts `.bat` que no existen en el repositorio.

Comandos documentados para validar y arrancar el stack:

```bash
docker compose -f docker/docker-compose.yml config --quiet
docker compose -f docker/docker-compose.yml build
docker compose -f docker/docker-compose.yml up -d
docker compose -f docker/docker-compose.yml ps
```

**Resultado:** la documentación utiliza rutas existentes, puertos coherentes y
comandos reproducibles desde la raíz del proyecto.

#### Actividad del Paso 5

Se agregaron health checks para PostgreSQL y los servicios Spring mediante
`/actuator/health`. Las dependencias de Compose esperan el estado `healthy` y
respetan el orden:

```text
PostgreSQL → Eureka → Config Server → Gateway/Auth/Finance
```

La validación se ejecuta con:

```bash
docker compose -f docker/docker-compose.yml config --quiet
docker compose -f docker/docker-compose.yml up -d
docker compose -f docker/docker-compose.yml ps
```

**Resultado:** el stack fue iniciado y validado correctamente. PostgreSQL,
Eureka, Config Server, Gateway, Auth y Finance aparecen activos con estado
`healthy`. También se corrigieron la etiqueta `main` del repositorio de
configuración, el acceso de escritura requerido por JGit y las URLs de base de
datos para usar el host Docker `postgres`.

**Entregable:** stack Docker iniciado respetando las dependencias de salud y con
estado verificable por servicio.

#### Validación de puertos frente a THAPP

THAPP utiliza los puertos `8761`, `8762`, `8443`, `9000`, `8181`, `8042` a
`8048`, `4200`, `3306` y `587`. El proyecto de Finanzas tenía una colisión en
`8761`, utilizado por Eureka en ambos proyectos.

Para evitarla, Eureka de Finanzas fue trasladado al puerto `8765`. La distribución
actual queda así:

| Servicio Finanzas | Puerto |
|---|---:|
| Eureka | 8765 |
| Config Server | 8890 |
| API Gateway | 8080 |
| Auth Service | 8081 |
| Finance Service | 8083 |
| PostgreSQL | 5432 |

Ninguno de estos puertos coincide con los puertos declarados por THAPP.

**Salida:** una máquina limpia puede construir, inicializar la BD y arrancar los servicios.

### 7.2 Fase 2 — Seguridad y secretos (semanas 3–5)

**Objetivo:** cerrar el principal riesgo de exposición de datos.

| Paso | Actividad | Entregable |
|---:|---|---|
| 1 | Quitar publicación externa de 8081 y 8083 | Solo 8080 es entrada pública |
| 2 | Validar JWT o token interno también en Finance | Ningún `X-User-Id` aislado otorga identidad |
| 3 | Sanitizar headers de identidad en Gateway | El cliente no puede imponer `X-User-Id` |
| 4 | Restringir Auth a login/register públicos | Superficie pública mínima |
| 5 | Proteger Actuator y Config Server | Management separado y autenticado |
| 6 | Eliminar defaults y rotar JWT secret | Secretos gestionados externamente |
| 7 | Desactivar usuarios de prueba fuera de dev | Sin credenciales conocidas en QA/prod |

**Pruebas de salida:** token inválido, acceso directo a Finance, UUID de otro usuario y header falsificado deben terminar en 401/403.

### 7.3 Fase 3 — Configuración por ambiente (semanas 6–7)

**Objetivo:** cambiar de ambiente sin editar cada microservicio.

| Paso | Actividad | Entregable |
|---:|---|---|
| 1 | Elegir BD compartida temporal o `auth_db`/`finance_db` | Decisión documentada |
| 2 | Unificar nombres de variables | Contrato de configuración |
| 3 | Crear perfil `docker` o mapearlo explícitamente a `dev` | Configuración Docker funcional |
| 4 | Versionar/fijar el repositorio de configuración | Revisión reproducible |
| 5 | Eliminar dependencia de `~/config-repo` | Compose portable |
| 6 | Validar propiedades obligatorias al arranque | Fallo temprano y explícito |
| 7 | Probar `dev`, `qa` y `prod` con smoke tests | Matriz de ambientes validada |

**Salida:** cambiar un único perfil configura Gateway, Auth, Finance y Registry de forma coherente.

### 7.4 Fase 4 — Datos, migraciones y contratos (semanas 8–10)

**Objetivo:** controlar la evolución de la información.

1. Incorporar Flyway o Liquibase.
2. Convertir el SQL inicial en migraciones versionadas.
3. Usar `ddl-auto: validate` fuera de desarrollo.
4. Alinear tipos SQL con las entidades JPA.
5. Definir el dueño de cada tabla y eliminar tablas “futuras” sin plan.
6. Definir el contrato de identidad entre Auth y Finance.
7. Añadir paginación y límites a todos los listados.

**Salida:** el esquema se puede recrear y actualizar sin cambios manuales ni `ddl-auto: update`.

### 7.5 Fase 5 — DDD y arquitectura hexagonal (semanas 11–14)

**Objetivo:** desacoplar reglas de negocio de Spring y la base de datos.

1. Separar en Auth los casos de uso de registro y autenticación.
2. Separar Finance internamente en categorías, movimientos y presupuestos.
3. Crear puertos de entrada y salida.
4. Crear adaptadores JPA, JWT y BCrypt.
5. Mover reglas de negocio a dominio/aplicación.
6. Eliminar acceso directo a repositorios desde controllers.
7. Unificar excepciones y respuestas HTTP en adaptadores.

**Salida:** los casos de uso pueden probarse sin levantar MVC ni JPA; todavía no se crean nuevos microservicios por reflejo.

### 7.6 Fase 6 — Pruebas y observabilidad (semanas 15–17)

**Objetivo:** demostrar comportamiento y facilitar soporte.

1. Tests unitarios de casos de uso y reglas.
2. Tests de integración con PostgreSQL.
3. Tests de seguridad e IDOR.
4. Tests de contrato del Gateway.
5. OpenAPI generado desde los endpoints reales.
6. Correlation ID y logs JSON sin secretos/PII.
7. Métricas, dashboards y alertas de 5xx, latencia, DB, Eureka y Config Server.

**Salida:** cada flujo crítico tiene pruebas automatizadas y una petición puede seguirse entre servicios.

### 7.7 Fase 7 — Evolución y nuevos servicios (semanas 18+)

**Objetivo:** escalar solo donde exista una necesidad demostrada.

1. Migrar gradualmente a Database per Service.
2. Crear primero `ms-audit-service` o `ms-notification-service` si los eventos lo justifican.
3. Crear `ms-analytics-service` cuando existan reportes definidos.
4. Evaluar `ms-account-service` e `ms-invoice-service` con sus propios modelos.
5. Incorporar broker, tracing avanzado, Redis o gateway secundario según métricas.

**Salida:** cada nuevo servicio tiene propósito, bounded context, datos, contrato, pruebas y operación independiente.

## 8. RIESGOS Y DECISIONES

| Riesgo/decisión | Recomendación |
|---|---|
| Separar Finance demasiado pronto | Modularizar primero dentro del mismo servicio |
| Crear `shared` con DTOs de negocio | Compartir solo utilidades técnicas estables |
| Mantener Config Server sin repositorio controlado | Versionar y revisar configuración como código |
| Migrar BD sin ownership | Primero definir quién es dueño de cada tabla |
| Agregar Kafka/Redis por moda | Esperar un caso de uso y una métrica |
| Confiar solo en Gateway | Cada servicio debe validar su frontera de confianza |

## 9. CONCLUSIONES Y RECOMENDACIONES

### Resumen ejecutivo

El proyecto tiene una estructura organizada y ya aplicó la separación propuesta entre infraestructura y negocio. También dispone de Eureka, Gateway, Config Server, Auth y Finance. El problema no es la falta de componentes, sino la falta de consistencia operativa y seguridad de extremo a extremo.

### Orden recomendado de trabajo

1. **Seguridad y secretos.**
2. **Configuración y arranque reproducibles.**
3. **SQL, migraciones y Docker.**
4. **Pruebas de seguridad y contratos.**
5. **DDD/hexagonal incremental.**
6. **Observabilidad.**
7. **Separación de bases y nuevos servicios.**

### Conclusión

La estructura actual debe conservarse; no conviene repetir la migración propuesta en `ESTRUCTURA_PROYECTO_PROPUESTA.md`. El siguiente hito debe ser un MVP operable y verificable, no una arquitectura más grande. Cuando las fases 1 a 5 estén completas, el proyecto podrá evaluarse con criterios reales de producción y decidir si necesita nuevos bounded contexts o servicios independientes.
