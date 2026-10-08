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

### 4.3 Paso 4 — Alinear tipos SQL con las entidades JPA

Se corrigió la discrepancia entre las entidades y el esquema migrado. En `Finance`, los campos `tipo` y `tipo_gasto` se almacenan como `VARCHAR(20)` y sus enumeraciones JPA se validan como texto. Por eso la migración quedó alineada con `@Enumerated(EnumType.STRING)`, evitando que JPA intente mapear un valor numérico o de tipo distinto al esperado.

También se hizo `tipo_gasto` obligatorio en la migración, coincidiendo con la restricción `nullable = false` de `Categoria`. Esto evita que Hibernate y PostgreSQL discrepen sobre la estructura real del modelo.

En `Auth`, se declararon explícitamente como `TEXT` los campos de texto que corresponden al esquema existente y se hicieron obligatorios los indicadores de verificación y la fecha de creación, manteniendo `ddl-auto: validate` como mecanismo de comprobación de que el modelo Java y el esquema SQL siguen coincidiendo.

### 4.4 Paso 5 — Dueño de tablas y limpieza del esquema

En esta etapa se definió la propiedad real del modelo de datos y se eliminaron las tablas que no tienen un alcance validado dentro del MVP actual.

| Tabla | Dueño actual | Estado |
|---|---|---|
| `usuarios` | `ms-auth-service` | ✅ Activa |
| `categorias` | `ms-finance-service` | ✅ Activa |
| `presupuestos` | `ms-finance-service` | ✅ Activa |
| `movimientos` | `ms-finance-service` | ✅ Activa |
| `comercios`, `facturas`, `factura_detalle`, `reglas_clasificacion`, `cuentas_bancarias`, `tarjetas_credito`, `creditos`, `inversiones` | sin dueño real en el MVP | ❌ Eliminadas del esquema actual |

**Regla aplicada:** cualquier tabla que no sea necesaria para el contexto implementado, no tenga entidad asociada ni servicio propietario claro, debe eliminarse del esquema inicial para evitar arquitectura “futura” no ejecutada.

### 4.5 Problemas deseables de resolver después

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

#### Paso 1 — Cerrar publicación externa de servicios internos

Se eliminaron las publicaciones de los puertos `8081` y `8083` en Docker
Compose. Auth y Finance continúan disponibles dentro de `finanzas-network`,
pero ya no aceptan conexiones directas desde el host. Gateway conserva el
puerto `8080` como única entrada pública de negocio.

#### Paso 2 — Validar JWT en Finance

Finance valida el token recibido en `Authorization: Bearer`, incluyendo firma,
expiración y el claim `userId`. Ese valor debe coincidir con `X-User-Id`; un
UUID aislado ya no concede identidad.

#### Paso 3 — Sanitizar headers de identidad en Gateway

Gateway elimina `X-User-Id` y `X-User-Username` enviados por el cliente antes de
procesar la petición. Después de validar el JWT, agrega únicamente los valores
derivados de sus claims y evita la suplantación mediante headers manipulados.

#### Paso 4 — Restringir Auth a login/register públicos

Auth deja públicos únicamente `POST /api/v1/auth/login` y
`POST /api/v1/auth/register`. Las demás rutas, incluidos Actuator y health,
requieren autenticación mediante matchers exactos.

#### Paso 5 — Proteger Actuator y Config Server

Config Server usa autenticación HTTP Basic y solo mantiene público
`/actuator/health`. La configuración y los demás endpoints Actuator requieren
credenciales. Gateway, Auth y Finance envían esas credenciales al Config
Server. Finance también protege sus endpoints Actuator.

#### Paso 6 — Eliminar defaults y exigir secretos externos

Se eliminaron secretos JWT y credenciales por defecto de Config Server y
PostgreSQL. Docker Compose exige mediante variables externas
`JWT_SECRET`, `CONFIG_SERVER_USER`, `CONFIG_SERVER_PASSWORD`, `DATABASE_URL`,
`DATABASE_USER`, `DATABASE_PASSWORD`, `POSTGRES_DB`, `POSTGRES_USER` y
`POSTGRES_PASSWORD`. También se eliminó `POSTGRES_HOST_AUTH_METHOD=trust`.

#### Paso 7 — Desactivar usuarios de prueba fuera de dev

`DataInitializer` solo se activa con el perfil Spring `dev`. Las cuentas
conocidas `admin/admin123` y `user/user123` no se crean durante arranques
Docker, QA o producción.

**Estado actual:** los pasos 1 al 7 de la Fase 2 están implementados en
`feature/reforzar-seguridad-servicios` y fueron fusionados en `main` mediante
el PR `#11`.

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

#### Paso 1 — Definir estrategia de base de datos

Se mantiene una base PostgreSQL compartida de forma temporal para Auth y
Finance, utilizando `finanzas_db`. Esta decisión conserva el funcionamiento
actual y evita una separación prematura del esquema.

La separación inmediata no es segura porque Finance utiliza `usuarios.id` como
referencia en `categorias`, `presupuestos`, `movimientos`, `comercios`,
`facturas` y otras tablas mediante claves foráneas. Separar las bases requiere
primero definir un contrato de identidad, migraciones y la propiedad de cada
tabla.

**Decisión:** mantener la base compartida durante la transición y preparar la
separación por servicio en una fase posterior, sin cambiar todavía las URLs de
conexión ni duplicar el esquema.

#### Paso 2 — Unificar nombres de variables

Se estableció `DATABASE_URL`, `DATABASE_USER` y `DATABASE_PASSWORD` como
contrato único para la conexión PostgreSQL de Auth y Finance. Compose,
los perfiles Docker y Railway utilizan esos mismos nombres. También se eliminó
el fallback JWT conocido de `railway.env`.

Las variables `SPRING_DATASOURCE_*` y `PG*` dejaron de utilizarse como
alternativas para estos servicios. Las credenciales de Config Server, JWT y
Eureka mantienen nombres separados porque representan integraciones distintas.

#### Paso 3 — Definir el perfil Docker

Docker utiliza el perfil Spring `docker` por defecto en Compose. Gateway, Auth,
Finance y Eureka cuentan con configuración `application-docker.yml` cuando
requieren valores locales de respaldo.

Además, los clientes de Config Server solicitan explícitamente el perfil
indicado por `SPRING_PROFILES_ACTIVE`, usando `docker` como valor por defecto.
Así, el perfil activo del contenedor y el perfil consultado en la configuración
centralizada permanecen alineados. Los perfiles `dev`, `qa` y `prod` pueden
seleccionarse cambiando una sola variable de entorno.

#### Paso 4 — Versionar y fijar la configuración centralizada

La configuración de los servicios quedó dentro de `config/config/`, versionada
por el repositorio principal y sin metadata Git anidada. Config Server utiliza
el repositorio nativo con la ubicación `file:/app/config/config` dentro de su
imagen Docker.

Se eliminó la dependencia de `~/config-repo` y de una rama Git local no
reproducible. El perfil `native` se activa explícitamente en Config Server y
la configuración se copia durante la construcción de la imagen.

#### Paso 5 — Eliminar dependencia del repositorio local de configuración

El Dockerfile de Config Server incorpora `config/` en la imagen final y Compose
ya no monta `../config` ni depende de archivos externos al contenedor. El stack
queda portable desde cualquier clon del proyecto y mantiene la configuración
centralizada en una fuente versionada.

#### Paso 6 — Validar propiedades obligatorias al arranque

Los clientes de Config Server ya no tienen una URL local implícita:
`CONFIG_SERVER_URI` debe estar definida. Los perfiles de Auth, Finance y
Gateway requieren `EUREKA_URL`, y Auth y Finance requieren
`DATABASE_URL`, `DATABASE_USER` y `DATABASE_PASSWORD`.

Los perfiles `qa` y `prod` fueron alineados al mismo contrato
`DATABASE_*` y dejaron de usar hosts ficticios o las variables obsoletas
`DB_USERNAME` y `DB_PASSWORD`. Compose conserva mensajes explícitos cuando
faltan credenciales o secretos obligatorios.

**Resultado:** una configuración incompleta falla durante la resolución de
propiedades, antes de iniciar el servicio con valores locales o ficticios.

#### Paso 7 — Validar `dev`, `qa` y `prod` con smoke tests

Se agregó `scripts/smoke-test-config-profiles.sh`. El script verifica que cada
servicio tenga configuración para `dev`, `qa` y `prod`, exige las variables
`DATABASE_*`, `EUREKA_URL` y `JWT_SECRET` correspondientes, rechaza hosts
ficticios y confirma que Docker Compose pueda renderizarse con valores de
prueba externos.

La validación es reproducible y no guarda secretos. Los ambientes QA y
Producción no se levantan como infraestructura local; el smoke test valida su
contrato de configuración hasta que existan esos despliegues.

**Salida:** cambiar un único perfil configura Gateway, Auth, Finance y Registry de forma coherente.

### 7.4 Fase 4 — Datos, migraciones y contratos (semanas 8–10)

**Objetivo:** controlar la evolución de la información.

#### Paso 1 — Incorporar migraciones versionadas

Se incorporó Flyway al servicio Auth como propietario de la historia de
migraciones de la base compartida. La configuración usa
`baseline-on-migrate` con versión `1` para registrar de forma segura las bases
existentes que fueron creadas con `database/init.sql`.

La migración `V1__registrar_esquema_actual.sql` contiene la creación completa
del esquema actual, incluyendo tablas, restricciones e índices. Las bases
existentes se registran con baseline `1`, mientras las instalaciones nuevas
ejecutan la migración desde cero.

Docker dejó de montar `database/init.sql` y `database/test-data.sql` en
PostgreSQL. Auth ejecuta Flyway antes de validar las entidades JPA, evitando
que el esquema se cree por dos mecanismos distintos. Los datos de prueba
quedan fuera del arranque normal y deberán cargarse mediante un proceso
explícito de desarrollo.

#### Paso 2 — Convertir el SQL inicial en migraciones versionadas

El esquema completo de `database/init.sql` fue trasladado a
`V1__registrar_esquema_actual.sql`, incluyendo las 12 tablas, sus restricciones
y los 16 índices definidos actualmente. Docker dejó de montar `init.sql` y
`test-data.sql` en PostgreSQL, por lo que Flyway es ahora el mecanismo único
para crear y evolucionar el esquema.

Las bases existentes se reconocen mediante `baseline-version: 1` y las nuevas
instalaciones ejecutan la migración V1 desde cero. Los datos de prueba no se
cargan automáticamente durante el arranque.

#### Paso 3 — Validar el esquema con JPA

Auth y Finance utilizan `spring.jpa.hibernate.ddl-auto: validate` en Docker,
dev, QA y producción. Hibernate ya no crea, actualiza ni elimina tablas; solo
comprueba que las entidades coincidan con el esquema administrado por Flyway.
Esto evita cambios estructurales automáticos y hace visibles las diferencias
entre código y base de datos durante el arranque.

#### Paso 4 — Alinear tipos SQL con las entidades JPA

Se corrigió la discrepancia entre las entidades y el esquema migrado: los
campos `tipo` y `tipo_gasto` de Finance se almacenan como `VARCHAR(20)` y sus
enumeraciones JPA se validan como texto. También se hizo `tipo_gasto`
obligatorio en la migración, coincidiendo con la restricción `nullable = false`
de `Categoria`. En Auth se declararon explícitamente como `TEXT` los campos de
texto que corresponden al esquema existente y se hicieron obligatorios los
indicadores de verificación y la fecha de creación, manteniendo `ddl-auto:
validate` como mecanismo de comprobación.

#### Paso 5 — Definir el dueño de cada tabla y eliminar tablas “futuras” sin plan

Se definió explícitamente el servicio propietario de cada tabla que forma parte
del MVP:

| Tabla | Servicio propietario | Motivo |
|---|---|---|
| `usuarios` | `ms-auth-service` | Auth crea, autentica y administra la identidad del usuario. |
| `categorias` | `ms-finance-service` | Finance administra las categorías asociadas al usuario. |
| `presupuestos` | `ms-finance-service` | Finance administra los límites mensuales por categoría. |
| `movimientos` | `ms-finance-service` | Finance administra ingresos y egresos del usuario. |

También se eliminaron del esquema vigente las tablas que no tienen entidad,
repositorio, endpoint ni servicio propietario en el alcance actual:
`comercios`, `reglas_clasificacion`, `facturas`, `factura_detalle`,
`cuentas_bancarias`, `tarjetas_credito`, `creditos` e `inversiones`.

La limpieza se aplicó tanto en `database/init.sql` como en la migración
`V1__registrar_esquema_actual.sql`. Además, se retiró de `movimientos` la
referencia a `factura_id`, porque `facturas` ya no pertenece al modelo activo.
De esta manera, la base de datos representa únicamente funcionalidades
implementadas y cada tabla activa tiene un dueño claro.

#### Paso 6 — Definir el contrato de identidad entre Auth y Finance

El contrato de identidad quedó definido así:

| Elemento | Contrato |
|---|---|
| Credencial de entrada | `Authorization: Bearer <JWT>` emitido por `ms-auth-service`. |
| Identificador canónico | Claim `userId` del JWT, con formato UUID. |
| Identidad propagada | El Gateway elimina `X-User-Id` y `X-User-Username` enviados por el cliente y los vuelve a crear después de validar el JWT. |
| Header consumido por Finance | `X-User-Id`, cuyo valor debe coincidir exactamente con el claim `userId`. |
| Validación en Finance | Firma, expiración, `userId` y `subject` del JWT; además compara el UUID del header con el claim. |
| Uso de la identidad | Los controllers de Finance reciben únicamente el `UUID` validado y los repositorios filtran por `user_id`. |
| Respuesta ante incumplimiento | `401 Unauthorized`; no se procesa ninguna operación de negocio. |

Este contrato evita que Finance tome la identidad del cuerpo de la petición o
de un header enviado directamente por el cliente. La validación se realiza dos
veces: en el Gateway para establecer la frontera externa y en Finance para
mantener la confianza de extremo a extremo. El `UserValidationInterceptor`
también rechaza tokens sin `userId` o sin `subject`, aunque la firma sea válida.

#### Paso 7 — Añadir paginación y límites a todos los listados

Los listados de categorías, movimientos y presupuestos dejaron de devolver
listas sin límite. Ahora aceptan `page` y `size`, usan consultas paginadas de
Spring Data y responden con `PageResponse`, que incluye contenido, página
actual, tamaño, total de elementos, total de páginas y los indicadores
`first`/`last`.

El tamaño por defecto es `20` elementos y el máximo permitido es `100`. Los
valores inválidos se normalizan para impedir páginas negativas o tamaños
ilimitados. La ordenación se mantiene estable: categorías por nombre,
movimientos por fecha y creación descendente, y presupuestos por período y
categoría.

También se paginaron los listados filtrados por tipo o período. Las consultas
de ejecución de presupuestos permanecen acotadas al período solicitado, por lo
que no exponen un listado histórico ilimitado.

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
