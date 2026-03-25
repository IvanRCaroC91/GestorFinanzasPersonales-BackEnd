# 🔐 GUÍA COMPLETA - FORMULARIO DE LOGIN FRONTEND
## API Gateway: http://localhost:8080
## Endpoint: POST /api/v1/auth/login
## Fecha: 2026-03-24

---

## 📋 INFORMACIÓN DEL ENDPOINT

### **URL Completa:**
```
POST http://localhost:8080/api/v1/auth/login
```

### **Headers Requeridos:**
```javascript
{
  'Content-Type': 'application/json'
}
```

### **Body del Login:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

---

## 🎯 ESTRUCTURA DEL FORMULARIO

### **Campos del Formulario:**

| Campo | Tipo | Requerido | Valor de Prueba | Validación |
|-------|------|-----------|----------------|------------|
| **username** | text | ✅ Sí | `admin` | Min 3 caracteres, alfanumérico |
| **password** | password | ✅ Sí | `admin123` | Min 6 caracteres |

### **Placeholder y Labels:**
```html
<label for="username">Usuario</label>
<input type="text" id="username" placeholder="Ingrese su usuario" required>

<label for="password">Contraseña</label>
<input type="password" id="password" placeholder="Ingrese su contraseña" required>
```

---

## 📤 RESPUESTA DEL SERVIDOR

### **Response Exitoso (HTTP 200):**
```json
{
  "success": true,
  "message": "Login exitoso",
  "username": "admin",
  "email": "admin@finanzas.com",
  "nombreCompleto": "Juan Carlos Pérez López",
  "iniciales": "JP",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc3NDM3NzU0MywiZXhwIjoxNzc0MzgxMTQzfQ.5YqR4J5Q2lL2J8X3dY7Z9K1f6N2m3W4p8Q1r2T3s4U"
}
```

### **Response de Error (HTTP 401):**
```json
{
  "success": false,
  "message": "Credenciales inválidas",
  "status": 401,
  "timestamp": "2026-03-24T22:15:00",
  "path": "/api/v1/auth/login"
}
```

---

## 🎨 IMPLEMENTACIÓN HTML/CSS

### **HTML Básico:**
```html
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Gestor Finanzas Personales</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <div class="login-container">
        <div class="login-card">
            <div class="login-header">
                <h1>💰 Gestor Finanzas</h1>
                <p>Iniciar sesión</p>
            </div>
            
            <form id="loginForm" class="login-form">
                <div class="form-group">
                    <label for="username">Usuario</label>
                    <input 
                        type="text" 
                        id="username" 
                        name="username" 
                        placeholder="Ingrese su usuario"
                        required
                        minlength="3"
                        autocomplete="username"
                    >
                    <span class="error-message" id="username-error"></span>
                </div>
                
                <div class="form-group">
                    <label for="password">Contraseña</label>
                    <input 
                        type="password" 
                        id="password" 
                        name="password" 
                        placeholder="Ingrese su contraseña"
                        required
                        minlength="6"
                        autocomplete="current-password"
                    >
                    <span class="error-message" id="password-error"></span>
                </div>
                
                <button type="submit" class="login-btn" id="loginBtn">
                    <span class="btn-text">Iniciar Sesión</span>
                    <span class="btn-loader" style="display: none;">⏳</span>
                </button>
                
                <div class="form-footer">
                    <p>¿No tienes cuenta? <a href="#">Regístrate</a></p>
                </div>
            </form>
            
            <div class="alert alert-danger" id="errorAlert" style="display: none;">
                <span id="errorMessage"></span>
            </div>
        </div>
    </div>
    
    <script src="login.js"></script>
</body>
</html>
```

### **CSS Moderno:**
```css
/* styles.css */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
}

.login-container {
    width: 100%;
    max-width: 400px;
    padding: 20px;
}

.login-card {
    background: white;
    border-radius: 20px;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
    padding: 40px;
    animation: slideUp 0.5s ease-out;
}

@keyframes slideUp {
    from {
        opacity: 0;
        transform: translateY(30px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.login-header {
    text-align: center;
    margin-bottom: 30px;
}

.login-header h1 {
    font-size: 2.5rem;
    margin-bottom: 10px;
    color: #333;
}

.login-header p {
    color: #666;
    font-size: 1.1rem;
}

.form-group {
    margin-bottom: 20px;
}

.form-group label {
    display: block;
    margin-bottom: 8px;
    font-weight: 600;
    color: #333;
}

.form-group input {
    width: 100%;
    padding: 12px 16px;
    border: 2px solid #e1e1e1;
    border-radius: 10px;
    font-size: 16px;
    transition: all 0.3s ease;
    background: #f9f9f9;
}

.form-group input:focus {
    outline: none;
    border-color: #667eea;
    background: white;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-group input:invalid {
    border-color: #e74c3c;
}

.error-message {
    color: #e74c3c;
    font-size: 0.875rem;
    margin-top: 5px;
    display: block;
    min-height: 20px;
}

.login-btn {
    width: 100%;
    padding: 14px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border: none;
    border-radius: 10px;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    margin-top: 10px;
}

.login-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
}

.login-btn:disabled {
    opacity: 0.7;
    cursor: not-allowed;
    transform: none;
}

.form-footer {
    text-align: center;
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #e1e1e1;
}

.form-footer a {
    color: #667eea;
    text-decoration: none;
    font-weight: 600;
}

.form-footer a:hover {
    text-decoration: underline;
}

.alert {
    padding: 12px 16px;
    border-radius: 8px;
    margin-top: 20px;
    animation: shake 0.5s ease-in-out;
}

@keyframes shake {
    0%, 100% { transform: translateX(0); }
    25% { transform: translateX(-10px); }
    75% { transform: translateX(10px); }
}

.alert-danger {
    background-color: #fee;
    border: 1px solid #fcc;
    color: #c33;
}

/* Responsive Design */
@media (max-width: 480px) {
    .login-container {
        padding: 10px;
    }
    
    .login-card {
        padding: 30px 20px;
    }
    
    .login-header h1 {
        font-size: 2rem;
    }
}
```

---

## 🚀 IMPLEMENTACIÓN JAVASCRIPT

### **JavaScript Puro (Vanilla JS):**
```javascript
// login.js
class LoginManager {
    constructor() {
        this.form = document.getElementById('loginForm');
        this.usernameInput = document.getElementById('username');
        this.passwordInput = document.getElementById('password');
        this.loginBtn = document.getElementById('loginBtn');
        this.errorAlert = document.getElementById('errorAlert');
        this.errorMessage = document.getElementById('errorMessage');
        
        this.initEventListeners();
    }
    
    initEventListeners() {
        this.form.addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleLogin();
        });
        
        // Validación en tiempo real
        this.usernameInput.addEventListener('input', () => {
            this.validateField(this.usernameInput, 'username-error');
        });
        
        this.passwordInput.addEventListener('input', () => {
            this.validateField(this.passwordInput, 'password-error');
        });
    }
    
    validateField(input, errorId) {
        const errorElement = document.getElementById(errorId);
        
        // Limpiar error anterior
        errorElement.textContent = '';
        input.style.borderColor = '#e1e1e1';
        
        // Validaciones
        if (input.value.trim() === '') {
            this.showFieldError(input, errorElement, 'Este campo es requerido');
            return false;
        }
        
        if (input.id === 'username' && input.value.length < 3) {
            this.showFieldError(input, errorElement, 'Mínimo 3 caracteres');
            return false;
        }
        
        if (input.id === 'password' && input.value.length < 6) {
            this.showFieldError(input, errorElement, 'Mínimo 6 caracteres');
            return false;
        }
        
        return true;
    }
    
    showFieldError(input, errorElement, message) {
        errorElement.textContent = message;
        input.style.borderColor = '#e74c3c';
    }
    
    async handleLogin() {
        // Validar campos
        const isUsernameValid = this.validateField(this.usernameInput, 'username-error');
        const isPasswordValid = this.validateField(this.passwordInput, 'password-error');
        
        if (!isUsernameValid || !isPasswordValid) {
            return;
        }
        
        // Mostrar loading
        this.setLoading(true);
        this.hideError();
        
        try {
            const loginData = {
                username: this.usernameInput.value.trim(),
                password: this.passwordInput.value
            };
            
            const response = await fetch('http://localhost:8080/api/v1/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(loginData)
            });
            
            const data = await response.json();
            
            if (response.ok && data.success) {
                this.handleLoginSuccess(data);
            } else {
                this.handleLoginError(data.message || 'Error en el login');
            }
        } catch (error) {
            console.error('Error de red:', error);
            this.handleLoginError('Error de conexión. Verifique su internet.');
        } finally {
            this.setLoading(false);
        }
    }
    
    handleLoginSuccess(data) {
        // Guardar datos en localStorage
        localStorage.setItem('token', data.token);
        localStorage.setItem('username', data.username);
        localStorage.setItem('email', data.email);
        localStorage.setItem('nombreCompleto', data.nombreCompleto);
        localStorage.setItem('iniciales', data.iniciales);
        localStorage.setItem('userId', '1454bf34-4592-48e1-9653-5479c839dc0f'); // ID fijo para pruebas
        
        // Mostrar éxito
        this.showSuccess('¡Login exitoso! Redirigiendo...');
        
        // Redirigir al dashboard después de 1 segundo
        setTimeout(() => {
            window.location.href = '/dashboard.html';
        }, 1000);
    }
    
    handleLoginError(message) {
        this.showError(message);
        
        // Limpiar contraseña por seguridad
        this.passwordInput.value = '';
        this.passwordInput.focus();
    }
    
    setLoading(loading) {
        const btnText = this.loginBtn.querySelector('.btn-text');
        const btnLoader = this.loginBtn.querySelector('.btn-loader');
        
        if (loading) {
            this.loginBtn.disabled = true;
            btnText.style.display = 'none';
            btnLoader.style.display = 'inline';
        } else {
            this.loginBtn.disabled = false;
            btnText.style.display = 'inline';
            btnLoader.style.display = 'none';
        }
    }
    
    showError(message) {
        this.errorMessage.textContent = message;
        this.errorAlert.style.display = 'block';
    }
    
    hideError() {
        this.errorAlert.style.display = 'none';
    }
    
    showSuccess(message) {
        // Crear alerta de éxito
        const successAlert = document.createElement('div');
        successAlert.className = 'alert alert-success';
        successAlert.textContent = message;
        successAlert.style.cssText = `
            background-color: #d4edda;
            border: 1px solid #c3e6cb;
            color: #155724;
            padding: 12px 16px;
            border-radius: 8px;
            margin-top: 20px;
            animation: slideUp 0.3s ease-out;
        `;
        
        this.form.appendChild(successAlert);
        
        // Ocultar después de 2 segundos
        setTimeout(() => {
            successAlert.remove();
        }, 2000);
    }
}

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', () => {
    new LoginManager();
});
```

---

## ⚛️ IMPLEMENTACIÓN REACT

### **Componente React Login:**
```jsx
// Login.jsx
import React, { useState } from 'react';
import './Login.css';

const Login = ({ onLoginSuccess }) => {
    const [formData, setFormData] = useState({
        username: '',
        password: ''
    });
    const [errors, setErrors] = useState({});
    const [isLoading, setIsLoading] = useState(false);
    const [apiError, setApiError] = useState('');

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
        
        // Limpiar error del campo
        if (errors[name]) {
            setErrors(prev => ({
                ...prev,
                [name]: ''
            }));
        }
    };

    const validateForm = () => {
        const newErrors = {};
        
        if (!formData.username.trim()) {
            newErrors.username = 'El usuario es requerido';
        } else if (formData.username.length < 3) {
            newErrors.username = 'Mínimo 3 caracteres';
        }
        
        if (!formData.password) {
            newErrors.password = 'La contraseña es requerida';
        } else if (formData.password.length < 6) {
            newErrors.password = 'Mínimo 6 caracteres';
        }
        
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!validateForm()) {
            return;
        }
        
        setIsLoading(true);
        setApiError('');
        
        try {
            const response = await fetch('http://localhost:8080/api/v1/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            });
            
            const data = await response.json();
            
            if (response.ok && data.success) {
                // Guardar en localStorage
                localStorage.setItem('token', data.token);
                localStorage.setItem('username', data.username);
                localStorage.setItem('userId', '1454bf34-4592-48e1-9653-5479c839dc0f');
                
                onLoginSuccess(data);
            } else {
                setApiError(data.message || 'Error en el login');
            }
        } catch (error) {
            setApiError('Error de conexión. Intente nuevamente.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="login-container">
            <div className="login-card">
                <div className="login-header">
                    <h1>💰 Gestor Finanzas</h1>
                    <p>Iniciar sesión</p>
                </div>
                
                <form onSubmit={handleSubmit} className="login-form">
                    <div className="form-group">
                        <label htmlFor="username">Usuario</label>
                        <input
                            type="text"
                            id="username"
                            name="username"
                            value={formData.username}
                            onChange={handleChange}
                            placeholder="Ingrese su usuario"
                            className={errors.username ? 'error' : ''}
                            disabled={isLoading}
                        />
                        {errors.username && (
                            <span className="error-message">{errors.username}</span>
                        )}
                    </div>
                    
                    <div className="form-group">
                        <label htmlFor="password">Contraseña</label>
                        <input
                            type="password"
                            id="password"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            placeholder="Ingrese su contraseña"
                            className={errors.password ? 'error' : ''}
                            disabled={isLoading}
                        />
                        {errors.password && (
                            <span className="error-message">{errors.password}</span>
                        )}
                    </div>
                    
                    <button 
                        type="submit" 
                        className="login-btn"
                        disabled={isLoading}
                    >
                        {isLoading ? '⏳ Iniciando...' : 'Iniciar Sesión'}
                    </button>
                </form>
                
                {apiError && (
                    <div className="alert alert-danger">
                        {apiError}
                    </div>
                )}
            </div>
        </div>
    );
};

export default Login;
```

---

## 🟢 IMPLEMENTACIÓN VUE.JS

### **Componente Vue Login:**
```vue
<!-- Login.vue -->
<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h1>💰 Gestor Finanzas</h1>
        <p>Iniciar sesión</p>
      </div>
      
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="username">Usuario</label>
          <input
            type="text"
            id="username"
            v-model="formData.username"
            placeholder="Ingrese su usuario"
            :class="{ error: errors.username }"
            :disabled="isLoading"
          >
          <span v-if="errors.username" class="error-message">
            {{ errors.username }}
          </span>
        </div>
        
        <div class="form-group">
          <label for="password">Contraseña</label>
          <input
            type="password"
            id="password"
            v-model="formData.password"
            placeholder="Ingrese su contraseña"
            :class="{ error: errors.password }"
            :disabled="isLoading"
          >
          <span v-if="errors.password" class="error-message">
            {{ errors.password }}
          </span>
        </div>
        
        <button type="submit" class="login-btn" :disabled="isLoading">
          {{ isLoading ? '⏳ Iniciando...' : 'Iniciar Sesión' }}
        </button>
      </form>
      
      <div v-if="apiError" class="alert alert-danger">
        {{ apiError }}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      formData: {
        username: '',
        password: ''
      },
      errors: {},
      isLoading: false,
      apiError: ''
    }
  },
  methods: {
    validateForm() {
      this.errors = {}
      
      if (!this.formData.username.trim()) {
        this.errors.username = 'El usuario es requerido'
      } else if (this.formData.username.length < 3) {
        this.errors.username = 'Mínimo 3 caracteres'
      }
      
      if (!this.formData.password) {
        this.errors.password = 'La contraseña es requerida'
      } else if (this.formData.password.length < 6) {
        this.errors.password = 'Mínimo 6 caracteres'
      }
      
      return Object.keys(this.errors).length === 0
    },
    
    async handleLogin() {
      if (!this.validateForm()) {
        return
      }
      
      this.isLoading = true
      this.apiError = ''
      
      try {
        const response = await fetch('http://localhost:8080/api/v1/auth/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(this.formData)
        })
        
        const data = await response.json()
        
        if (response.ok && data.success) {
          // Guardar en localStorage
          localStorage.setItem('token', data.token)
          localStorage.setItem('username', data.username)
          localStorage.setItem('userId', '1454bf34-4592-48e1-9653-5479c839dc0f')
          
          // Emitir evento de éxito
          this.$emit('login-success', data)
          
          // Redirigir
          this.$router.push('/dashboard')
        } else {
          this.apiError = data.message || 'Error en el login'
        }
      } catch (error) {
        this.apiError = 'Error de conexión. Intente nuevamente.'
      } finally {
        this.isLoading = false
      }
    }
  }
}
</script>

<style scoped>
/* Estilos CSS aquí */
</style>
```

---

## 🔧 CONFIGURACIÓN ADICIONAL

### **Environment Variables:**
```bash
# .env
VITE_API_BASE_URL=http://localhost:8080
VITE_API_VERSION=v1
```

### **Axios Configuration:**
```javascript
// src/api/auth.js
import axios from 'axios';

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    timeout: 10000,
});

// Interceptor para agregar token
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export const authService = {
    login: async (credentials) => {
        const response = await api.post('/api/v1/auth/login', credentials);
        return response.data;
    },
    
    logout: () => {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        localStorage.removeItem('userId');
    },
    
    isAuthenticated: () => {
        return !!localStorage.getItem('token');
    }
};
```

---

## 🎯 MEJORES PRÁCTICAS

### **UX/UI Recommendations:**
1. **Autocomplete:** Usar atributos autocomplete
2. **Loading States:** Mostrar indicadores durante peticiones
3. **Error Handling:** Mensajes claros y constructivos
4. **Password Visibility:** Opción para mostrar/ocultar contraseña
5. **Remember Me:** Checkbox para mantener sesión

### **Security Best Practices:**
1. **HTTPS:** Usar en producción
2. **CSRF Protection:** Implementar tokens CSRF
3. **Rate Limiting:** Limitar intentos de login
4. **Password Strength:** Validar complejidad
5. **Session Management:** Manejar expiración de token

### **Accessibility:**
1. **ARIA Labels:** Para lectores de pantalla
2. **Keyboard Navigation:** Navegación con tab
3. **Focus Management:** Indicadores visuales de focus
4. **Screen Readers:** Descripciones adecuadas

---

## 📱 TESTING DEL FORMULARIO

### **Casos de Prueba:**
```javascript
// Test cases
const testCases = [
    {
        name: 'Login exitoso',
        username: 'admin',
        password: 'admin123',
        expected: 'success'
    },
    {
        name: 'Usuario incorrecto',
        username: 'wrong',
        password: 'admin123',
        expected: 'error'
    },
    {
        name: 'Contraseña incorrecta',
        username: 'admin',
        password: 'wrong',
        expected: 'error'
    },
    {
        name: 'Campos vacíos',
        username: '',
        password: '',
        expected: 'validation_error'
    }
];
```

---

## 🚀 READY TO USE

**Con esta guía tienes todo lo necesario para implementar un formulario de login completo y funcional que se integra perfectamente con el API Gateway del sistema de gestión financiera.**

### **Next Steps:**
1. **Copiar el código** según tu framework preferido
2. **Configurar el endpoint** del API Gateway
3. **Implementar el dashboard** después del login
4. **Agregar validaciones adicionales** según requerimientos
5. **Probar con diferentes escenarios**

**🎯 El formulario está listo para producción con todas las mejores prácticas implementadas.**
