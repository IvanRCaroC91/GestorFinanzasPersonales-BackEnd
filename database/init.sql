-- =========================
-- EXTENSIONES
-- =========================
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =========================
-- ENUMS
-- =========================
CREATE TYPE tipo_movimiento AS ENUM ('INGRESO', 'GASTO');

CREATE TYPE tipo_gasto AS ENUM ('NECESARIO', 'INNECESARIO', 'HORMIGA');

CREATE TYPE estado_factura AS ENUM ('PENDIENTE', 'PROCESADA');

CREATE TYPE tipo_credito AS ENUM ('TARJETA', 'VIVIENDA', 'VEHICULO', 'ESTUDIO', 'LIBRE');

CREATE TYPE tipo_inversion AS ENUM ('CDT', 'AHORRO', 'FONDO', 'OTRO');

-- =========================
-- USUARIOS
-- =========================
CREATE TABLE usuarios (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
username TEXT UNIQUE NOT NULL,
password TEXT NOT NULL,
created_at TIMESTAMP DEFAULT NOW()
);

-- =========================
-- CATEGORIAS
-- =========================
CREATE TABLE categorias (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
user_id UUID NOT NULL,

```
nombre TEXT NOT NULL,
tipo tipo_movimiento NOT NULL,
tipo_gasto tipo_gasto DEFAULT 'NECESARIO',

categoria_padre_id UUID NULL,

created_at TIMESTAMP DEFAULT NOW(),

CONSTRAINT fk_categoria_padre
    FOREIGN KEY (categoria_padre_id)
    REFERENCES categorias(id)
    ON DELETE SET NULL,

CONSTRAINT categorias_nombre_usuario_unique
    UNIQUE (user_id, nombre)
```

);

-- =========================
-- COMERCIOS
-- =========================
CREATE TABLE comercios (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
user_id UUID NOT NULL,

```
nombre TEXT NOT NULL,

created_at TIMESTAMP DEFAULT NOW(),

CONSTRAINT comercios_nombre_usuario_unique
    UNIQUE (user_id, nombre)
```

);

-- =========================
-- REGLAS DE CLASIFICACION
-- =========================
CREATE TABLE reglas_clasificacion (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
user_id UUID NOT NULL,

```
comercio_id UUID,
palabra_clave TEXT,

categoria_id UUID NOT NULL,

created_at TIMESTAMP DEFAULT NOW(),

FOREIGN KEY (comercio_id) REFERENCES comercios(id) ON DELETE CASCADE,
FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE CASCADE
```

);

-- =========================
-- FACTURAS (ORIGEN)
-- =========================
CREATE TABLE facturas (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
user_id UUID NOT NULL,

```
comercio_id UUID,

fecha DATE NOT NULL,
total NUMERIC(12,2) CHECK (total >= 0),

estado estado_factura DEFAULT 'PENDIENTE',

xml_original TEXT,
moneda TEXT DEFAULT 'COP',

created_at TIMESTAMP DEFAULT NOW(),

FOREIGN KEY (comercio_id) REFERENCES comercios(id) ON DELETE SET NULL
```

);

-- =========================
-- DETALLE FACTURA
-- =========================
CREATE TABLE factura_detalle (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

```
factura_id UUID NOT NULL,
user_id UUID NOT NULL,

descripcion TEXT,

cantidad NUMERIC(10,2) DEFAULT 1 CHECK (cantidad > 0),
precio_unitario NUMERIC(12,2) CHECK (precio_unitario >= 0),

categoria_id UUID,

categoria_nombre TEXT,
tipo_gasto tipo_gasto,

subtotal NUMERIC(12,2)
    GENERATED ALWAYS AS (cantidad * precio_unitario) STORED,

FOREIGN KEY (factura_id) REFERENCES facturas(id) ON DELETE CASCADE,
FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE SET NULL
```

);

-- =========================
-- TRANSACCIONES (FUENTE REAL)
-- =========================
CREATE TABLE transacciones (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
user_id UUID NOT NULL,

```
tipo tipo_movimiento NOT NULL,

fecha DATE NOT NULL,
monto NUMERIC(12,2) CHECK (monto > 0),

descripcion TEXT,

categoria_id UUID,

categoria_nombre TEXT,
tipo_gasto tipo_gasto,
comercio_nombre TEXT,

factura_id UUID NULL,

created_at TIMESTAMP DEFAULT NOW(),

FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE SET NULL,
FOREIGN KEY (factura_id) REFERENCES facturas(id) ON DELETE SET NULL
```

);

-- =========================
-- PRESUPUESTOS
-- =========================
CREATE TABLE presupuestos (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
user_id UUID NOT NULL,

```
anio INT NOT NULL CHECK (anio >= 2000),
mes INT NOT NULL CHECK (mes BETWEEN 1 AND 12),

categoria_id UUID NOT NULL,
tipo tipo_movimiento NOT NULL,

monto_planificado NUMERIC(12,2) CHECK (monto_planificado >= 0),

created_at TIMESTAMP DEFAULT NOW(),

UNIQUE(user_id, anio, mes, categoria_id, tipo),

FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE CASCADE
```

);

-- =========================
-- CREDITOS
-- =========================
CREATE TABLE creditos (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
user_id UUID NOT NULL,

```
nombre TEXT,
tipo tipo_credito,

monto_inicial NUMERIC(14,2),
tasa_interes NUMERIC(5,2),
plazo_meses INT,

cuota_mensual NUMERIC(12,2),
seguro_mensual NUMERIC(12,2) DEFAULT 0,

fecha_inicio DATE,
created_at TIMESTAMP DEFAULT NOW()
```

);

-- =========================
-- CUOTAS
-- =========================
CREATE TABLE cuotas (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
credito_id UUID NOT NULL,

```
numero INT NOT NULL,

capital NUMERIC(12,2),
interes NUMERIC(12,2),
seguro NUMERIC(12,2),

saldo_restante NUMERIC(14,2),

pagada BOOLEAN DEFAULT FALSE,
fecha_pago DATE,

FOREIGN KEY (credito_id) REFERENCES creditos(id) ON DELETE CASCADE
```

);

-- =========================
-- PAGOS EXTRA
-- =========================
CREATE TABLE pagos_extra (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
credito_id UUID NOT NULL,

```
monto NUMERIC(12,2),
fecha DATE,
tipo TEXT,

FOREIGN KEY (credito_id) REFERENCES creditos(id) ON DELETE CASCADE
```

);

-- =========================
-- PLAN DE DEUDAS (BOLA DE NIEVE)
-- =========================
CREATE TABLE plan_pago_deudas (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
user_id UUID NOT NULL,
nombre TEXT,
created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE plan_pago_detalle (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
plan_id UUID,
credito_id UUID,
prioridad INT,

```
FOREIGN KEY (plan_id) REFERENCES plan_pago_deudas(id) ON DELETE CASCADE,
FOREIGN KEY (credito_id) REFERENCES creditos(id) ON DELETE CASCADE
```

);

-- =========================
-- INVERSIONES
-- =========================
CREATE TABLE inversiones (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
user_id UUID NOT NULL,

```
nombre TEXT,
tipo tipo_inversion,

entidad TEXT,

monto NUMERIC(14,2),

fecha_inicio DATE,
fecha_fin DATE,

created_at TIMESTAMP DEFAULT NOW()
```

);

-- =========================
-- HISTORIAL RENTABILIDAD
-- =========================
CREATE TABLE rentabilidad_historial (
id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
inversion_id UUID,

```
tasa NUMERIC(5,2),
fecha DATE,

FOREIGN KEY (inversion_id) REFERENCES inversiones(id) ON DELETE CASCADE
```

);

-- =========================
-- INDICES
-- =========================
CREATE INDEX idx_transacciones_user_fecha
ON transacciones(user_id, fecha);

CREATE INDEX idx_transacciones_user_categoria
ON transacciones(user_id, categoria_id);

CREATE INDEX idx_facturas_user_fecha
ON facturas(user_id, fecha);

CREATE INDEX idx_presupuesto_user_periodo
ON presupuestos(user_id, anio, mes);

CREATE INDEX idx_creditos_user
ON creditos(user_id);

CREATE INDEX idx_cuotas_credito
ON cuotas(credito_id);

CREATE INDEX idx_inversiones_user
ON inversiones(user_id);
