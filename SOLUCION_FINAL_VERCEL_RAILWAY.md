# Solución Final: Error Vercel + Railway

## Problema Identificado
Timeout de 10 segundos al conectar frontend (Vercel) con backend (Railway)

## Causa Raíz
El API Gateway recibe las peticiones pero no puede comunicarse internamente con el Auth Service a través de Eureka.

## URLs Confirmadas
- Frontend: `https://gestor-finanzas-personales-front-1nste8xft.vercel.app`
- API Gateway: `https://api-gateway-production-f3ef.up.railway.app`
- Auth Service: `auth-service.railway.internal` (privado)

## Pasos para Solucionar

### 1. Configurar Variables de Entorno en Railway

**API Gateway:**
```
SPRING_PROFILES_ACTIVE=docker
FRONTEND_URL=https://gestor-finanzas-personales-front-1nste8xft.vercel.app
JWT_SECRET=mySecretKey123456789012345678901234567890
EUREKA_URL=http://service-registry:8761/eureka/
RAILWAY_PUBLIC_DOMAIN=api-gateway-production-f3ef.up.railway.app
```

**Auth Service:**
```
SPRING_PROFILES_ACTIVE=docker
DATABASE_URL=tu-url-postgresql-railway
DATABASE_USER=tu-usuario-db
DATABASE_PASSWORD=tu-password-db
JWT_SECRET=mySecretKey123456789012345678901234567890
EUREKA_URL=http://gestorfinanzaspersonales-backend:8761/eureka/
```

**Service Registry:**
```
SPRING_PROFILES_ACTIVE=docker
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://gestorfinanzaspersonales-backend:8761/eureka/
```

### 2. Verificar Configuración Frontend

Asegúrate que tu frontend use:
```javascript
const API_BASE_URL = 'https://api-gateway-production-f3ef.up.railway.app'
```

### 3. Redesplegar Servicios

1. Actualiza las variables de entorno en cada servicio Railway
2. Redespliega todos los servicios en orden:
   - Service Registry primero
   - Auth Service segundo
   - API Gateway tercero

### 4. Verificar Funcionamiento

```bash
# Test health check
curl https://api-gateway-production-f3ef.up.railway.app/actuator/health

# Test login endpoint
curl -X POST https://api-gateway-production-f3ef.up.railway.app/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"test123"}'
```

## Configuración CORS Actualizada

Los archivos ya incluyen soporte para:
- `https://*.vercel.app` - tu frontend
- `https://*.railway.app` - servicios Railway
- Desarrollo local

## Próximos Pasos

1. **Configura las variables de entorno** en Railway dashboard
2. **Redespliega los servicios** con la nueva configuración
3. **Testea el login** desde tu frontend Vercel
4. **Verifica logs** si el problema persiste

El timeout debería resolverse una vez que los servicios puedan comunicarse internamente a través de Eureka.
