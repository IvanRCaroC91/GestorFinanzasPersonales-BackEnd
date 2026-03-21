-- =========================
-- BASE DE DATOS: GESTOR FINANZAS PERSONALES
-- =========================
-- Schema completo para el sistema de gestión de finanzas personales
-- Compatible con PostgreSQL 14+

-- =========================
-- TIPOS ENUM
-- =========================
CREATE TYPE tipo_movimiento AS ENUM ('INGRESO', 'EGRESO');
CREATE TYPE tipo_gasto AS ENUM ('NECESARIO', 'NO NECESARIO', 'OCASIONAL');
CREATE TYPE estado_factura AS ENUM ('PENDIENTE', 'PROCESADA', 'CANCELADA');
CREATE TYPE tipo_credito AS ENUM ('TARJETA', 'VIVIENDA', 'VEHICULO', 'ESTUDIO', 'LIBRE');
CREATE TYPE tipo_inversion AS ENUM ('CDT', 'AHORRO', 'FONDO', 'OTRO');

-- =========================
-- USUARIOS
-- =========================
CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username TEXT UNIQUE NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    primer_nombre TEXT NOT NULL,
    primer_apellido TEXT NOT NULL,
    segundo_nombre TEXT,
    segundo_apellido TEXT,
    celular TEXT UNIQUE NOT NULL,
    email_verificado BOOLEAN DEFAULT FALSE,
    celular_verificado BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- =========================
-- CATEGORIAS
-- =========================
CREATE TABLE categorias (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
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
);

-- =========================
-- COMERCIOS
-- =========================
CREATE TABLE comercios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    nombre TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT comercios_nombre_usuario_unique
        UNIQUE (user_id, nombre)
);

-- =========================
-- REGLAS DE CLASIFICACION
-- =========================
CREATE TABLE reglas_clasificacion (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    comercio_id UUID,
    palabra_clave TEXT,
    categoria_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (comercio_id) REFERENCES comercios(id) ON DELETE CASCADE,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE CASCADE
);

-- =========================
-- FACTURAS (ORIGEN)
-- =========================
CREATE TABLE facturas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    comercio_id UUID,
    fecha DATE NOT NULL,
    total NUMERIC(12,2) CHECK (total >= 0),
    estado estado_factura DEFAULT 'PENDIENTE',
    xml_original TEXT,
    moneda TEXT DEFAULT 'COP',
    created_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (comercio_id) REFERENCES comercios(id) ON DELETE SET NULL
);

-- =========================
-- DETALLE FACTURA
-- =========================
CREATE TABLE factura_detalle (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    factura_id UUID NOT NULL,
    categoria_id UUID NOT NULL,
    descripcion TEXT NOT NULL,
    cantidad NUMERIC(10,2) CHECK (cantidad > 0),
    valor_unitario NUMERIC(12,2) CHECK (valor_unitario >= 0),
    total NUMERIC(12,2) CHECK (total >= 0),
    created_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (factura_id) REFERENCES facturas(id) ON DELETE CASCADE,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE RESTRICT
);

-- =========================
-- MOVIMIENTOS
-- =========================
CREATE TABLE movimientos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    categoria_id UUID NOT NULL,
    factura_id UUID,
    descripcion TEXT NOT NULL,
    tipo tipo_movimiento NOT NULL,
    valor NUMERIC(12,2) NOT NULL,
    fecha DATE NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE RESTRICT,
    FOREIGN KEY (factura_id) REFERENCES facturas(id) ON DELETE SET NULL
);

-- =========================
-- CUENTAS BANCARIAS
-- =========================
CREATE TABLE cuentas_bancarias (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    nombre TEXT NOT NULL,
    banco TEXT NOT NULL,
    tipo_cuenta TEXT NOT NULL,
    numero_cuenta TEXT,
    saldo_actual NUMERIC(12,2) DEFAULT 0,
    moneda TEXT DEFAULT 'COP',
    activa BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- =========================
-- TARJETAS DE CRÉDITO
-- =========================
CREATE TABLE tarjetas_credito (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    banco TEXT NOT NULL,
    nombre TEXT NOT NULL,
    tipo tipo_credito NOT NULL,
    limite_credito NUMERIC(12,2) CHECK (limite_credito >= 0),
    saldo_actual NUMERIC(12,2) DEFAULT 0,
    fecha_corte INTEGER,
    dia_pago INTEGER,
    activa BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- =========================
-- CRÉDITOS
-- =========================
CREATE TABLE creditos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    tarjeta_id UUID,
    banco TEXT NOT NULL,
    descripcion TEXT NOT NULL,
    tipo tipo_credito NOT NULL,
    monto_total NUMERIC(12,2) CHECK (monto_total >= 0),
    tasa_interes NUMERIC(5,2) CHECK (tasa_interes >= 0),
    plazo_meses INTEGER CHECK (plazo_meses > 0),
    cuota_mensual NUMERIC(12,2) CHECK (cuota_mensual >= 0),
    saldo_pendiente NUMERIC(12,2) CHECK (saldo_pendiente >= 0),
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE,
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (tarjeta_id) REFERENCES tarjetas_credito(id) ON DELETE SET NULL
);

-- =========================
-- INVERSIONES
-- =========================
CREATE TABLE inversiones (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    nombre TEXT NOT NULL,
    tipo tipo_inversion NOT NULL,
    institucion TEXT NOT NULL,
    monto_invertido NUMERIC(12,2) CHECK (monto_invertido >= 0),
    monto_actual NUMERIC(12,2) CHECK (monto_actual >= 0),
    tasa_retorno NUMERIC(5,2),
    fecha_inicio DATE NOT NULL,
    fecha_vencimiento DATE,
    activa BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- =========================
-- ÍNDICES
-- =========================
CREATE INDEX idx_usuarios_username ON usuarios(username);
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_categorias_user_id ON categorias(user_id);
CREATE INDEX idx_comercios_user_id ON comercios(user_id);
CREATE INDEX idx_facturas_user_id ON facturas(user_id);
CREATE INDEX idx_facturas_fecha ON facturas(fecha);
CREATE INDEX idx_movimientos_user_id ON movimientos(user_id);
CREATE INDEX idx_movimientos_fecha ON movimientos(fecha);
CREATE INDEX idx_movimientos_categoria ON movimientos(categoria_id);
CREATE INDEX idx_reglas_clasificacion_user_id ON reglas_clasificacion(user_id);
