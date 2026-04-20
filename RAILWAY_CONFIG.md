# Configuración para Railway - URLs Actualizadas

## URLs de Producción

### API Gateway
- **URL**: `https://api-gateway-production-f3ef.up.railway.app`
- **Puerto**: 8080
- **Internal**: `api-gateway.railway.internal`

### Variables de Entorne Actualizadas

#### API Gateway
```bash
PORT=8080
SPRING_PROFILES_ACTIVE=railway
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka-service-production.up.railway.app/eureka/
JWT_SECRET=mySecretKey123456789012345678901234567890
RAILWAY_PUBLIC_DOMAIN=api-gateway-production-f3ef.up.railway.app
FRONTEND_URL=https://gestor-finanzas-personales-front-gezyi3jbs.vercel.app
```

#### Auth Service
```bash
PORT=8081
SPRING_PROFILES_ACTIVE=railway
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres.railway.internal:5432/railway
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=tu_password_railway
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka-service-production.up.railway.app/eureka/
JWT_SECRET=mySecretKey123456789012345678901234567890
JWT_EXPIRATION=86400000
RAILWAY_STATIC_URL=auth-service-production.up.railway.app
```

#### Finance Service
```bash
PORT=8083
SPRING_PROFILES_ACTIVE=railway
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres.railway.internal:5432/railway
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=tu_password_railway
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka-service-production.up.railway.app/eureka/
RAILWAY_STATIC_URL=finance-service-production.up.railway.app
```

## Endpoints Actualizados

### Autenticación
- `POST https://api-gateway-production-f3ef.up.railway.app/api/v1/auth/login`
- `POST https://api-gateway-production-f3ef.up.railway.app/api/v1/auth/register`

### Finanzas
- `GET https://api-gateway-production-f3ef.up.railway.app/api/v1/finance/movimientos`
- `POST https://api-gateway-production-f3ef.up.railway.app/api/v1/finance/movimientos`

### Health Checks
- `GET https://api-gateway-production-f3ef.up.railway.app/actuator/health`
