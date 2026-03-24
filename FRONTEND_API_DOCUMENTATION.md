# 📚 DOCUMENTACIÓN API FRONTEND - GESTOR FINANZAS PERSONALES
## API Gateway: http://localhost:8080
## Fecha: 2026-03-24
## Para Desarrolladores Frontend

---

## 🔐 AUTENTICACIÓN - REQUERIDO PARA TODAS LAS PETICIONES

### **Login - Obtener JWT Token**
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Response:**
```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc3NDM3NzU0MywiZXhwIjoxNzc0MzgxMTQzfQ.5YqR4J5Q2lL2J8X3dY7Z9K1f6N2m3W4p8Q1r2T3s4U",
  "expiresIn": 3600
}
```

### **Headers Requeridos para todas las peticiones:**
```javascript
headers: {
  'Authorization': 'Bearer ' + token,
  'X-User-Id': '1454bf34-4592-48e1-9653-5479c839dc0f',
  'Content-Type': 'application/json'
}
```

### **Manejo de Token Expirado:**
- **Duración:** 1 hora (3600 segundos)
- **Error 401/403:** Solicitar nuevo login
- **Refresh:** Implementar auto-refresh antes de expiración

---

## 📊 ENDPOINTS DE CATEGORÍAS

### **GET - Listar todas las categorías**
```http
GET /api/v1/finance/categorias
```

**Response:**
```json
{
  "success": true,
  "message": "Categorías listadas correctamente",
  "data": [
    {
      "id": "uuid",
      "nombre": "Alimentos",
      "tipo": "EGRESO",
      "tipoGasto": "NECESARIO",
      "icono": "🍔",
      "color": "#FF6B6B",
      "activa": true,
      "createdAt": "2026-03-24T10:00:00"
    }
  ]
}
```

### **POST - Crear categoría**
```http
POST /api/v1/finance/categorias
```

**Body:**
```json
{
  "nombre": "Nueva Categoría",
  "tipo": "EGRESO",           // "EGRESO" o "INGRESO"
  "tipoGasto": "NECESARIO",    // "NECESARIO", "DESEO", "CULTURA", "INVERSION"
  "icono": "🛒",               // Opcional
  "color": "#FF6B6B",          // Opcional
  "padreId": null              // Opcional, para subcategorías
}
```

**Response:**
```json
{
  "success": true,
  "message": "Categoría creada correctamente",
  "data": {
    "id": "uuid",
    "nombre": "Nueva Categoría",
    "tipo": "EGRESO",
    "tipoGasto": "NECESARIO",
    "icono": "🛒",
    "color": "#FF6B6B",
    "activa": true,
    "createdAt": "2026-03-24T10:00:00"
  }
}
```

### **PUT - Actualizar categoría**
```http
PUT /api/v1/finance/categorias/{id}
```

### **DELETE - Eliminar categoría**
```http
DELETE /api/v1/finance/categorias/{id}
```

---

## 💰 ENDPOINTS DE MOVIMIENTOS

### **GET - Listar todos los movimientos**
```http
GET /api/v1/finance/movimientos
```

**Response:**
```json
{
  "success": true,
  "message": "Movimientos listados correctamente",
  "data": [
    {
      "id": "uuid",
      "categoriaId": "uuid",
      "categoriaNombre": "Alimentos",
      "descripcion": "Compra supermercado",
      "tipo": "EGRESO",
      "valor": 50000,
      "fecha": "2026-03-24",
      "createdAt": "2026-03-24T10:00:00"
    }
  ]
}
```

### **GET - Filtrar movimientos por tipo**
```http
GET /api/v1/finance/movimientos?tipo=EGRESO
```

### **POST - Crear movimiento**
```http
POST /api/v1/finance/movimientos
```

**Body:**
```json
{
  "categoriaId": "uuid",
  "descripcion": "Compra en supermercado",
  "tipo": "EGRESO",           // "EGRESO" o "INGRESO"
  "valor": 75000,
  "fecha": "2026-03-24",      // Formato YYYY-MM-DD
  "facturaId": null           // Opcional
}
```

**Response:**
```json
{
  "success": true,
  "message": "Movimiento creado correctamente",
  "data": {
    "id": "uuid",
    "categoriaId": "uuid",
    "descripcion": "Compra en supermercado",
    "tipo": "EGRESO",
    "valor": 75000,
    "fecha": "2026-03-24",
    "createdAt": "2026-03-24T10:00:00"
  }
}
```

### **PUT - Actualizar movimiento**
```http
PUT /api/v1/finance/movimientos/{id}
```

### **DELETE - Eliminar movimiento**
```http
DELETE /api/v1/finance/movimientos/{id}
```

---

## 📈 ENDPOINTS DE PRESUPUESTOS

### **GET - Listar todos los presupuestos**
```http
GET /api/v1/finance/presupuestos
```

**Response:**
```json
{
  "success": true,
  "message": "Presupuestos listados correctamente",
  "data": [
    {
      "id": "uuid",
      "categoriaId": "uuid",
      "categoriaNombre": "Alimentos",
      "montoLimite": 300000,
      "montoActual": 150000,
      "porcentajeUsado": 50.0,
      "periodoInicio": "2026-03-01",
      "periodoFin": "2026-03-31",
      "activo": true,
      "createdAt": "2026-03-24T10:00:00"
    }
  ]
}
```

### **POST - Crear presupuesto**
```http
POST /api/v1/finance/presupuestos
```

**Body:**
```json
{
  "categoriaId": "uuid",
  "montoLimite": 300000,
  "periodoInicio": "2026-03-01",    // Formato YYYY-MM-DD
  "periodoFin": "2026-03-31"        // Formato YYYY-MM-DD
}
```

**Response:**
```json
{
  "success": true,
  "message": "Presupuesto creado correctamente",
  "data": {
    "id": "uuid",
    "categoriaId": "uuid",
    "montoLimite": 300000,
    "montoActual": 0,
    "porcentajeUsado": 0.0,
    "periodoInicio": "2026-03-01",
    "periodoFin": "2026-03-31",
    "activo": true,
    "createdAt": "2026-03-24T10:00:00"
  }
}
```

### **PUT - Actualizar presupuesto**
```http
PUT /api/v1/finance/presupuestos/{id}
```

### **DELETE - Eliminar presupuesto**
```http
DELETE /api/v1/finance/presupuestos/{id}
```

---

## 📊 ENDPOINTS DE ESTADÍSTICAS Y REPORTES

### **GET - Resumen financiero del mes**
```http
GET /api/v1/finance/resumen?anio=2026&mes=3
```

**Response:**
```json
{
  "success": true,
  "message": "Resumen financiero obtenido correctamente",
  "data": {
    "totalIngresos": 2500000,
    "totalEgresos": 1800000,
    "saldo": 700000,
    "ahorro": 500000,
    "porcentajeAhorro": 20.0,
    "presupuestos": [
      {
        "categoria": "Alimentos",
        "presupuestado": 300000,
        "gastado": 150000,
        "porcentaje": 50.0,
        "estado": "NORMAL"
      }
    ]
  }
}
```

### **GET - Movimientos por período**
```http
GET /api/v1/finance/movimientos/periodo?inicio=2026-03-01&fin=2026-03-31
```

### **GET - Estadísticas por categoría**
```http
GET /api/v1/finance/estadisticas/categorias?anio=2026&mes=3
```

---

## 🎨 TIPOS DE DATOS Y ENUMS

### **TipoMovimiento:**
```javascript
const TIPO_MOVIMIENTO = {
  INGRESO: 'INGRESO',
  EGRESO: 'EGRESO'
};
```

### **TipoGasto:**
```javascript
const TIPO_GASTO = {
  NECESARIO: 'NECESARIO',
  DESEO: 'DESEO',
  CULTURA: 'CULTURA',
  INVERSION: 'INVERSION'
};
```

### **Estados de Presupuesto:**
```javascript
const ESTADO_PRESUPUESTO = {
  NORMAL: 'NORMAL',        // < 80%
  ADVERTENCIA: 'ADVERTENCIA', // 80-95%
  EXCEDIDO: 'EXCEDIDO',    // 95-100%
  SOBREPASADO: 'SOBREPASADO' // > 100%
};
```

---

## 🚨 MANEJO DE ERRORES

### **Estructura de Error Estandarizada:**
```json
{
  "success": false,
  "message": "Mensaje descriptivo del error",
  "status": 400,           // HTTP Status Code
  "timestamp": "2026-03-24T10:00:00",
  "path": "/api/v1/finance/categorias",
  "details": []            // Opcional, para errores de validación
}
```

### **Códigos de Error Comunes:**
- **400 Bad Request:** Error de validación en el body
- **401 Unauthorized:** Token JWT inválido o ausente
- **403 Forbidden:** Token expirado o inválido
- **404 Not Found:** Recurso no encontrado
- **409 Conflict:** Duplicidad (ej: categoría con mismo nombre)
- **500 Internal Server Error:** Error del servidor

---

## 🔧 EJEMPLOS DE IMPLEMENTACIÓN

### **JavaScript/React - Login:**
```javascript
const login = async (username, password) => {
  try {
    const response = await fetch('http://localhost:8080/api/v1/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ username, password })
    });
    
    const data = await response.json();
    
    if (data.success) {
      localStorage.setItem('token', data.token);
      localStorage.setItem('userId', '1454bf34-4592-48e1-9653-5479c839dc0f');
      return data;
    } else {
      throw new Error(data.message);
    }
  } catch (error) {
    console.error('Error en login:', error);
    throw error;
  }
};
```

### **JavaScript/React - API Client:**
```javascript
class FinanceAPI {
  constructor() {
    this.baseURL = 'http://localhost:8080/api/v1/finance';
    this.headers = this.getHeaders();
  }

  getHeaders() {
    const token = localStorage.getItem('token');
    const userId = localStorage.getItem('userId');
    
    return {
      'Authorization': `Bearer ${token}`,
      'X-User-Id': userId,
      'Content-Type': 'application/json'
    };
  }

  async getCategorias() {
    const response = await fetch(`${this.baseURL}/categorias`, {
      headers: this.headers
    });
    return response.json();
  }

  async createCategoria(categoria) {
    const response = await fetch(`${this.baseURL}/categorias`, {
      method: 'POST',
      headers: this.headers,
      body: JSON.stringify(categoria)
    });
    return response.json();
  }

  async getMovimientos() {
    const response = await fetch(`${this.baseURL}/movimientos`, {
      headers: this.headers
    });
    return response.json();
  }

  async createMovimiento(movimiento) {
    const response = await fetch(`${this.baseURL}/movimientos`, {
      method: 'POST',
      headers: this.headers,
      body: JSON.stringify(movimiento)
    });
    return response.json();
  }
}
```

### **Vue.js - Composable:**
```javascript
// composables/useFinance.js
import { ref } from 'vue';

export function useFinance() {
  const token = ref(localStorage.getItem('token'));
  const userId = ref('1454bf34-4592-48e1-9653-5479c839dc0f');

  const headers = {
    'Authorization': `Bearer ${token.value}`,
    'X-User-Id': userId.value,
    'Content-Type': 'application/json'
  };

  const getCategorias = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/v1/finance/categorias', {
        headers
      });
      const data = await response.json();
      return data.data;
    } catch (error) {
      console.error('Error al obtener categorías:', error);
      throw error;
    }
  };

  return {
    getCategorias,
    // ... otros métodos
  };
}
```

---

## 🎯 CONSIDERACIONES FRONTEND

### **UX/UI Recommendations:**
1. **Auto-refresh token** 5 minutos antes de expirar
2. **Loading states** para todas las peticiones
3. **Error boundaries** para manejar errores de red
4. **Offline support** con localStorage caching
5. **Real-time updates** con WebSocket (futuro)

### **Performance Tips:**
1. **Pagination** para listas grandes
2. **Caching** de categorías y presupuestos
3. **Lazy loading** de movimientos
4. **Debouncing** en búsquedas
5. **Optimistic updates** para mejor UX

### **Security Considerations:**
1. **Never store** passwords en localStorage
2. **Use HTTPS** en producción
3. **Validate inputs** antes de enviar
4. **Handle CORS** correctamente
5. **Implement rate limiting** en cliente

---

## 🌐 CORS CONFIGURATION

### **Gateway CORS Settings:**
```yaml
# application.yml del Gateway
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOrigins: "http://localhost:5173"
            allowedMethods: "*"
            allowedHeaders: "*"
            allowCredentials: true
```

### **Frontend URLs Permitidas:**
- **Development:** `http://localhost:5173` (Vite)
- **Development:** `http://localhost:3000` (React)
- **Production:** Configurar dominio del frontend

---

## 📱 ESTRUCTURA DE DATOS PARA FRONTEND

### **Models TypeScript:**
```typescript
interface Categoria {
  id: string;
  nombre: string;
  tipo: 'INGRESO' | 'EGRESO';
  tipoGasto?: 'NECESARIO' | 'DESEO' | 'CULTURA' | 'INVERSION';
  icono?: string;
  color?: string;
  padreId?: string;
  activa: boolean;
  createdAt: string;
}

interface Movimiento {
  id: string;
  categoriaId: string;
  categoriaNombre?: string;
  descripcion: string;
  tipo: 'INGRESO' | 'EGRESO';
  valor: number;
  fecha: string;
  facturaId?: string;
  createdAt: string;
}

interface Presupuesto {
  id: string;
  categoriaId: string;
  categoriaNombre?: string;
  montoLimite: number;
  montoActual: number;
  porcentajeUsado: number;
  periodoInicio: string;
  periodoFin: string;
  activo: boolean;
  createdAt: string;
}

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data?: T;
  status?: number;
  timestamp?: string;
  path?: string;
  details?: string[];
}
```

---

## 🚀 QUICK START FRONTEND

### **1. Configurar Environment:**
```bash
# .env
VITE_API_BASE_URL=http://localhost:8080
VITE_API_VERSION=v1
```

### **2. Instalar Dependencias:**
```bash
npm install axios
npm install @types/node
```

### **3. Configurar Axios Instance:**
```javascript
// src/api/config.js
import axios from 'axios';

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
});

// Interceptor para agregar headers
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
    config.headers['X-User-Id'] = '1454bf34-4592-48e1-9653-5479c839dc0f';
  }
  return config;
});

// Interceptor para manejar errores
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Redirect to login
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

---

## 📞 SOPORTE Y CONTACTO

### **Información Técnica:**
- **API Gateway:** `http://localhost:8080`
- **Health Check:** `http://localhost:8080/actuator/health`
- **Service Registry:** `http://localhost:8761`
- **Environment:** Development

### **Endpoints de Desarrollo:**
- **Auth Service:** `http://localhost:8081`
- **Finance Service:** `http://localhost:8083`
- **Database:** PostgreSQL (localhost:5432)

---

**🎯 LISTO PARA EMPEZAR EL DESARROLLO FRONTEND**

Con esta documentación tienes todo lo necesario para integrar el frontend con el API Gateway del sistema de gestión financiera personal.
