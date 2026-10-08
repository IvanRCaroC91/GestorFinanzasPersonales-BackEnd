-- =========================
-- BASE DE DATOS: GESTOR FINANZAS PERSONALES
-- =========================
-- Esquema vigente del MVP.
-- Dueño de tablas:
--   * ms-auth-service -> usuarios
--   * ms-finance-service -> categorias, presupuestos, movimientos
-- Tablas futuras no planificadas y no implementadas en la versión actual:
--   comercios, facturas, factura_detalle, reglas_clasificacion,
--   cuentas_bancarias, tarjetas_credito, creditos, inversiones
-- Se eliminan para mantener el modelo de dominio consistente con el alcance real.

-- =========================
-- DUEÑO: ms-auth-service
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
-- DUEÑO: ms-finance-service
-- =========================
CREATE TABLE categorias (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    nombre TEXT NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    tipo_gasto VARCHAR(20) DEFAULT 'NECESARIO',
    categoria_padre_id UUID NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_categoria_usuario
        FOREIGN KEY (user_id)
        REFERENCES usuarios(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_categoria_padre
        FOREIGN KEY (categoria_padre_id)
        REFERENCES categorias(id)
        ON DELETE SET NULL,
    CONSTRAINT categorias_nombre_usuario_unique
        UNIQUE (user_id, nombre)
);

CREATE TABLE presupuestos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    categoria_id UUID NOT NULL,
    monto_limite NUMERIC(12,2) NOT NULL CHECK (monto_limite >= 0),
    anio INT NOT NULL CHECK (anio >= 2000 AND anio <= 2100),
    mes INT NOT NULL CHECK (mes >= 1 AND mes <= 12),
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_presupuesto_usuario
        FOREIGN KEY (user_id)
        REFERENCES usuarios(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_presupuesto_categoria
        FOREIGN KEY (categoria_id)
        REFERENCES categorias(id)
        ON DELETE CASCADE,
    CONSTRAINT presupuestos_usuario_categoria_mes_anio_unique
        UNIQUE (user_id, categoria_id, anio, mes)
);

CREATE TABLE movimientos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    categoria_id UUID NOT NULL,
    descripcion TEXT NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    valor NUMERIC(12,2) NOT NULL,
    fecha DATE NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_movimiento_usuario
        FOREIGN KEY (user_id)
        REFERENCES usuarios(id)
        ON DELETE CASCADE,
    CONSTRAINT chk_movimientos_valor
        CHECK (valor >= 0),
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE RESTRICT
);

-- =========================
-- ÍNDICES DEL MVP
-- =========================
CREATE INDEX idx_presupuestos_user_id ON presupuestos(user_id);
CREATE INDEX idx_presupuestos_categoria_id ON presupuestos(categoria_id);
CREATE INDEX idx_presupuestos_periodo ON presupuestos(anio, mes);
CREATE INDEX idx_presupuestos_user_periodo ON presupuestos(user_id, anio, mes);
CREATE INDEX idx_usuarios_username ON usuarios(username);
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_categorias_user_id ON categorias(user_id);
CREATE INDEX idx_movimientos_user_id ON movimientos(user_id);
CREATE INDEX idx_movimientos_fecha ON movimientos(fecha);
CREATE INDEX idx_movimientos_categoria ON movimientos(categoria_id);
CREATE INDEX idx_movimientos_tipo ON movimientos(tipo);
CREATE INDEX idx_movimientos_user_categoria_fecha ON movimientos(user_id, categoria_id, fecha);
