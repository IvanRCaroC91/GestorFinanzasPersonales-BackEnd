# Diagnóstico de Error Vercel + Railway

## Problema
Timeout de 10 segundos al intentar conectar frontend (Vercel) con backend (Railway)

## Checklist de Verificación

### 1. Variables de Entorno en Railway
Configura estas variables en cada servicio en Railway:

**API Gateway:**
```
SPRING_PROFILES_ACTIVE=docker
FRONTEND_URL=https://tu-app.vercel.app
JWT_SECRET=tu-secreto-seguro
EUREKA_URL=http://ms-service-registry:8761/eureka/
```

**Auth Service:**
```
SPRING_PROFILES_ACTIVE=docker
DATABASE_URL=tu-url-postgresql-railway
DATABASE_USER=tu-usuario-db
DATABASE_PASSWORD=tu-password-db
JWT_SECRET=mismo-secreto-que-gateway
EUREKA_URL=http://ms-service-registry:8761/eureka/
```

### 2. URLs Reales de Railway
Reemplaza con tus URLs reales:
- Frontend Vercel: `https://tu-app.vercel.app`
- API Gateway Railway: `https://tu-gateway.railway.app`
- Auth Service Railway: `https://tu-auth-service.railway.app`

### 3. Configuración Frontend
Verifica que tu frontend apunte a:
```javascript
const API_BASE_URL = 'https://tu-gateway.railway.app'
```

### 4. Pruebas Directas
Testea los endpoints directamente:

```bash
# Health check del gateway
curl https://tu-gateway.railway.app/actuator/health

# Health check del auth service
curl https://tu-auth-service.railway.app/actuator/health

# Test endpoint de login
curl -X POST https://tu-gateway.railway.app/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"test123"}'
```

### 5. Logs en Railway
Revisa los logs en Railway dashboard:
- ¿Los servicios inician correctamente?
- ¿Hay errores de conexión a base de datos?
- ¿Eureka registra los servicios?

### 6. Red Railway
Verifica que todos los servicios estén en la misma red Railway y puedan comunicarse entre sí.

## Pasos si no funciona

1. **Verifica que los servicios estén corriendo** en Railway dashboard
2. **Confirma las URLs públicas** de cada servicio
3. **Revisa logs de errores** en cada servicio
4. **Testea endpoints directamente** con curl
5. **Verifica configuración CORS** en los logs del gateway

## Comandos Útiles

```bash
# Verificar conectividad
Test-NetConnection -ComputerName "tu-gateway.railway.app" -Port 443

# Verificar respuesta HTTP
curl -I https://tu-gateway.railway.app/actuator/health
```
