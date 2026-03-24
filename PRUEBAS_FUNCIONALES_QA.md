# PRUEBAS FUNCIONALES QA - MS-FINANCE-SERVICE
## Fecha: 2026-03-24
## Tester: QA Senior
## Ambiente: Local (localhost:8083)

### RESULTADOS DE PRUEBAS:

#### ✅ PRUEBA 1: Crear categoría (caso exitoso)
- **Endpoint:** POST /api/v1/finance/categorias
- **Resultado:** HTTP 201 ✅
- **Datos:** Categoría "Test QA" creada exitosamente
- **ID:** 8ebb63d2-bfe0-41c1-9994-12dca46c070a

#### ❌ PRUEBA 2: Crear categoría duplicada (negativo)
- **Endpoint:** POST /api/v1/finance/categorias
- **Resultado:** HTTP 500 (debería ser 409)
- **Problema:** Validación de duplicados desactivada
- **Acción:** Activada validación en CategoriaService.crearCategoria()

#### ✅ PRUEBA 3: Obtener categorías
- **Endpoint:** GET /api/v1/finance/categorias
- **Resultado:** HTTP 200 ✅
- **Datos:** Retorna lista completa de 13 categorías del usuario

#### ✅ PRUEBA 4: Crear presupuesto
- **Endpoint:** POST /api/v1/finance/presupuestos
- **Resultado:** HTTP 201 ✅
- **Datos:** Presupuesto creado con categoría Test QA
- **ID:** 33715088-1108-498d-acac-fad1aa026701

#### ✅ PRUEBA 5: Crear movimiento
- **Endpoint:** POST /api/v1/finance/movimientos
- **Resultado:** HTTP 201 ✅
- **Datos:** Movimiento creado con categoría Salario
- **ID:** f5110679-8c53-4d51-a48f-9709630632eb

#### ✅ PRUEBA 6: Obtener ejecución de presupuesto
- **Endpoint:** GET /api/v1/finance/presupuestos/{id}/ejecucion
- **Resultado:** HTTP 200 ✅
- **Datos:** Retorna cálculos correctos (montoLimite: 300000, totalGastado: 0, disponible: 300000)

#### ❌ PRUEBA 7: Endpoint inexistente (negativo)
- **Endpoint:** GET /api/v1/finance/no-existe
- **Resultado:** HTTP 500 (debería ser 404)
- **Problema:** No hay manejo global de 404

#### ❌ PRUEBA 8: Datos inválidos (negativo)
- **Endpoint:** POST /api/v1/finance/categorias
- **Resultado:** HTTP 400 ✅ (código correcto)
- **Problema:** No retorna mensaje de error específico

#### ✅ PRUEBA 9: Validación de integridad
- **Endpoint:** POST /api/v1/finance/movimientos
- **Resultado:** HTTP 404 ✅
- **Datos:** categoríaId inexistente retorna 404 correctamente
- **Problema:** No retorna mensaje de error específico

#### ✅ PRUEBA 10: Flujo completo
- **Paso 1:** Crear categoría - HTTP 201 ✅
- **Paso 2:** Crear presupuesto - HTTP 201 ✅
- **Paso 3:** Crear movimiento - HTTP 201 ✅
- **Paso 4:** Consultar ejecución - HTTP 200 ✅
- **Resultado:** Flujo completo funcional con datos consistentes

### OBSERVACIONES IMPORTANTES:
1. **curl.exe en PowerShell falla** - Se debe usar Invoke-WebRequest
2. **Validación de duplicados** - Estaba desactivada, ahora activada
3. **Manejo de errores** - Mejorar mensajes en respuestas 400/404
4. **Endpoints 404** - Implementar manejo global

### ESTADO GENERAL: 
**FUNCIONAL CON OBSERVACIONES** - Sistema operativo pero requiere ajustes menores
