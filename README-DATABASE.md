# 📊 **Configuración de Base de Datos - Gestor Finanzas Personales**

## 🐳 **Configuración Docker para Equipo**

### **📋 **Requisitos Previos**
- Docker Desktop instalado
- Git clonado del proyecto

### **🚀 **Pasos para Configurar Base de Datos**

#### **✅ **1. Iniciar Base de Datos con Datos de Prueba**
```bash
# Desde la raíz del proyecto
docker-compose up -d postgres
```

**¿Qué sucede?**
- Docker crea el contenedor `finanzas-postgres`
- Ejecuta automáticamente `01-init.sql` (crea estructura)
- Ejecuta automáticamente `02-test-data.sql` (inserta datos de prueba)

#### **✅ **2. Verificar Datos de Prueba**
```bash
# Conectar a la base de datos
docker exec -it finanzas-postgres psql -U admin -d finanzas_db

# Ver usuarios
SELECT username, email, primer_nombre FROM usuarios;

# Ver categorías
SELECT u.username, c.nombre, c.tipo 
FROM categorias c 
JOIN usuarios u ON c.user_id = u.id;
```

### **👥 **Usuarios de Prueba Disponibles**

| Usuario | Contraseña | Nombre | Email | Celular |
|---------|------------|---------|--------|---------|
| **admin** | `admin123` | Juan Pérez | admin@finanzas.com | +573011234567 |
| **user** | `user123` | María García | user@finanzas.com | +573022345678 |

### **🔧 **Configuración de Microservicios**

#### **✅ **Application.yml (Auth Service)**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/finanzas_db
    username: admin
    password: admin123
    driver-class-name: org.postgresql.Driver
```

#### **✅ **Variables de Entorno (Opcional)**
```bash
# Para desarrollo
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=finanzas_db
export DB_USER=admin
export DB_PASSWORD=admin123
```

### **📁 **Estructura de Archivos SQL**

```
database/
├── init.sql          # ✅ Estructura completa (11 tablas)
├── test-data.sql     # ✅ Datos de prueba (usuarios, categorías, movimientos)
└── README-DATABASE.md # 📖 Este archivo
```

### **🔄 **Reiniciar Base de Datos (si es necesario)**

#### **✅ **Opción 1: Limpiar y Recrear**
```bash
# Detener y eliminar contenedor
docker-compose down postgres

# Eliminar volumen (¡cuidado! borra todos los datos)
docker volume rm gestorfinanzaspersonales-backend_postgres_data

# Iniciar nuevamente con datos frescos
docker-compose up -d postgres
```

#### **✅ **Opción 2: Solo Reiniciar**
```bash
# Reiniciar contenedor (mantiene datos)
docker-compose restart postgres
```

### **🧪 **Pruebas de Conexión**

#### **✅ **Prueba con PowerShell**
```powershell
# Probar conexión a PostgreSQL
docker exec finanzas-postgres psql -U admin -d finanzas_db -c "SELECT version();"

# Probar usuarios
docker exec finanzas-postgres psql -U admin -d finanzas_db -c "SELECT username, email FROM usuarios;"
```

#### **✅ **Prueba con Spring Boot**
```bash
# Iniciar Auth Service
mvn spring-boot:run -pl ms-auth-service

# Verificar en logs que conecte a PostgreSQL
```

### **🔐 **Configuración JWT**

#### **✅ **Variables de Entorno**
```bash
# Para producción (cambiar estos valores)
export JWT_SECRET=mySecretKey123456789012345678901234567890
export JWT_EXPIRATION=86400000  # 24 horas
```

### **🌐 **Endpoints de Prueba**

#### **✅ **Login con Admin**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

#### **✅ **Login con User**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"user123"}'
```

### **📊 **Datos de Prueba Incluidos**

#### **✅ **Usuarios (2)**
- **admin**: Datos completos, email y celular verificados
- **user**: Datos completos, email y celular verificados

#### **✅ **Categorías (10)**
- **Admin**: Salario, Alquiler, Supermercado, Transporte, Entretenimiento, Emergencias
- **User**: Salario, Comida, Facturas, Ocio

#### **✅ **Movimientos (5)**
- Ingresos y egresos de ejemplo
- Fechas del mes actual
- Montos realistas (COP)

### **🚨 **Solución de Problemas**

#### **❌ **Error: "Base de datos no existe"**
```bash
# Verificar que el contenedor esté corriendo
docker ps | grep finanzas-postgres

# Verificar logs
docker logs finanzas-postgres
```

#### **❌ **Error: "Tabla no existe"**
```bash
# Reconstruir desde cero
docker-compose down postgres
docker volume rm gestorfinanzaspersonales-backend_postgres_data
docker-compose up -d postgres
```

#### **❌ **Error: "Usuario/contraseña incorrectos"**
```bash
# Verificar credenciales en docker-compose.yml
# Usuario: admin / Contraseña: admin123
```

### **🔄 **Para tu Compañero**

#### **✅ **Pasos Rápidos**
```bash
# 1. Clonar repositorio
git clone <repositorio>
cd GestorFinanzasPersonales-BackEnd

# 2. Iniciar base de datos
docker-compose up -d postgres

# 3. Esperar 30 segundos y verificar
docker exec finanzas-postgres psql -U admin -d finanzas_db -c "SELECT COUNT(*) FROM usuarios;"

# 4. Iniciar microservicios
mvn spring-boot:run -pl ms-service-registry
mvn spring-boot:run -pl ms-auth-service
mvn spring-boot:run -pl ms-api-gateway

# 5. Probar login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### **✅ **Verificación Final**

Si todo está configurado correctamente:
- ✅ Base de datos accesible en `localhost:5432`
- ✅ Usuarios `admin/admin123` y `user/user123` funcionan
- ✅ Microservicios conectan a PostgreSQL
- ✅ Login retorna token JWT válido

**🎉 ¡Listo para desarrollo en equipo!**
