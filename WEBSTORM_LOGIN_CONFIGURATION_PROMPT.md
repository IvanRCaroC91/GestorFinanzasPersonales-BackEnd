# 🤖 PROMPT PARA WINDSURF - CONFIGURACIÓN LOGIN FRONTEND

## **INSTRUCCIONES PARA WINDSURF/WEBSTORM**

Analiza el proyecto React + Vite existente y configura el login para que funcione con el backend de microservicios. El frontend no debe tener datos de prueba hardcoded.

---

## **📋 CONTEXTO DEL SISTEMA**

### **Backend - Microservicios:**
- **API Gateway:** `http://localhost:8080` (entrada única)
- **Auth Service:** `http://localhost:8081` (autenticación JWT)
- **Finance Service:** `http://localhost:8083` (lógica de negocio)
- **Service Registry:** `http://localhost:8761` (descubrimiento)

### **Endpoint de Login:**
```
POST http://localhost:8080/api/v1/auth/login
Headers: Content-Type: application/json
Body: {"username": "admin", "password": "admin123"}
```

### **Respuesta del Backend:**
```json
{
  "success": true,
  "message": "Login exitoso",
  "token": "jwt-token-aquí",
  "username": "admin",
  "email": "admin@finanzas.com",
  "userId": "uuid-generado-automaticamente"
}
```

---

## **🎯 TAREAS A REALIZAR**

### **1. VERIFICAR ESTRUCTURA ACTUAL:**
- Buscar componentes de login existentes
- Identificar estructura de archivos del proyecto
- Verificar si ya existe configuración de API
- Revisar rutas configuradas

### **2. CONFIGURACIÓN DE ENTORNO:**
- Crear archivo `.env.local` si no existe
- Agregar variable: `VITE_API_BASE_URL=http://localhost:8080`
- Verificar que no haya datos de prueba hardcoded

### **3. ADAPTAR FORMULARIO DE LOGIN:**
- Modificar el handleSubmit existente para usar el endpoint del backend
- Implementar manejo de errores del backend
- Guardar token, username y userId en localStorage
- Redirigir a `/home` cuando el login sea exitoso

### **4. CONFIGURACIÓN DE PETICIONES:**
- Implementar interceptores para agregar token JWT automáticamente
- Configurar header `X-User-Id` para peticiones a finanzas
- Manejar errores 401 (token expirado)

### **5. RUTAS PROTEGIDAS:**
- Verificar que exista redirección automática si ya está logueado
- Configurar ruta `/home` como página principal después del login
- Implementar protección de rutas si no existe

### **6. ELIMINAR DATOS HARDCODED:**
- Buscar y eliminar cualquier usuario/contraseña hardcoded
- Remover UUIDs fijos del código
- Asegurar que todo venga del formulario de login

---

## **🔍 VALIDACIONES A REALIZAR**

### **Backend Conectividad:**
- Verificar que el endpoint `http://localhost:8080/api/v1/auth/login` responda
- Probar con credenciales `admin/admin123`
- Confirmar que la respuesta incluya `userId`

### **Frontend Integration:**
- Asegurar que `import.meta.env.VITE_API_BASE_URL` funcione
- Verificar que el localStorage guarde los datos correctamente
- Confirmar redirección a `/home` después del login exitoso

### **Security Headers:**
- Verificar que las peticiones a finanzas incluyan `Authorization: Bearer <token>`
- Confirmar que las peticiones incluyan `X-User-Id: <userId>`

---

## **⚠️ RESTRICCIONES IMPORTANTES**

### **NO AGREGAR DATOS DE PRUEBA:**
- ❌ No crear usuarios hardcoded en el frontend
- ❌ No agregar UUIDs fijos en variables de entorno
- ❌ No implementar lógica de autenticación en el frontend
- ✅ Todo debe venir dinámicamente del backend

### **MANTENER ARQUITECTURA STATELESS:**
- ❌ No crear sesiones en el frontend
- ❌ No manejar estado de autenticación complejo
- ✅ Usar JWT token del backend
- ✅ Guardar solo token y datos básicos en localStorage

### **RESPECTAR CÓDIGO EXISTENTE:**
- ✅ Adaptar componentes existentes, no reemplazar
- ✅ Mantener estilos y estructura actual
- ✅ Solo modificar la lógica del handleSubmit
- ✅ Preservar la experiencia de usuario existente

---

## **📁 ARCHIVOS A VERIFICAR/MODIFICAR**

### **Busca estos archivos:**
- `.env.local` (crear si no existe)
- Componente de login (probablemente `Login.jsx` o `LoginForm.jsx`)
- Configuración de rutas (`App.jsx` o similar)
- Archivos de configuración de API (si existen)

### **Si no existen, créalos:**
- `.env.local` con la variable de entorno
- Configuración básica de fetch/axios para el login

---

## **🎯 RESULTADO ESPERADO**

El frontend debe:
1. ✅ Enviar username/password al backend
2. ✅ Recibir token y userId del backend
3. ✅ Guardar datos en localStorage
4. ✅ Redirigir a `/home` automáticamente
5. ✅ No tener ningún dato de prueba hardcoded
6. ✅ Funcionar con el backend existente

---

## **🚀 INSTRUCCIÓN FINAL**

"Analiza el proyecto React + Vite existente y configura el login para que funcione con el backend de microservicios. Adapta solo lo necesario, elimina cualquier dato de prueba hardcoded, y asegúrate que el flujo sea: formulario → backend → token → redirección al home. No reescribas componentes existentes, solo modifica la lógica de autenticación."
