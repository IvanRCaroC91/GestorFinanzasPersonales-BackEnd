-- =========================
-- DATOS DE PRUEBA - GESTOR FINANZAS PERSONALES
-- =========================
-- Usuarios de prueba con contraseñas BCrypt hasheadas
-- admin123 -> $2a$12$cEsB39AmsAivNPaBTXEEUO2B2NSOzo.VFucefCJGSkuMwX.ufoMn6
-- user123 -> $2a$12$F3KyVnDN9WaLv1rKKAju2eR215pS6d2/bnTUeTqmCTN7qEHLNeVy.

-- =========================
-- INSERTAR USUARIOS DE PRUEBA
-- =========================
INSERT INTO usuarios (username, email, password, primer_nombre, primer_apellido, segundo_nombre, segundo_apellido, celular, email_verificado, celular_verificado) VALUES
('admin', 'admin@finanzas.com', '$2a$12$cEsB39AmsAivNPaBTXEEUO2B2NSOzo.VFucefCJGSkuMwX.ufoMn6', 'Juan', 'Pérez', 'Carlos', 'López', '+573011234567', true, true),
('user', 'user@finanzas.com', '$2a$12$F3KyVnDN9WaLv1rKKAju2eR215pS6d2/bnTUeTqmCTN7qEHLNeVy.', 'María', 'García', 'Ana', 'Martínez', '+573022345678', true, true);

-- =========================
-- CATEGORIAS DE EJEMPLO
-- =========================
INSERT INTO categorias (user_id, nombre, tipo, tipo_gasto) VALUES
-- Categorías para admin (ID se obtendrá dinámicamente)
((SELECT id FROM usuarios WHERE username = 'admin'), 'Salario', 'INGRESO', 'NECESARIO'),
((SELECT id FROM usuarios WHERE username = 'admin'), 'Alquiler', 'EGRESO', 'NECESARIO'),
((SELECT id FROM usuarios WHERE username = 'admin'), 'Supermercado', 'EGRESO', 'NECESARIO'),
((SELECT id FROM usuarios WHERE username = 'admin'), 'Transporte', 'EGRESO', 'NECESARIO'),
((SELECT id FROM usuarios WHERE username = 'admin'), 'Entretenimiento', 'EGRESO', 'NO NECESARIO'),
((SELECT id FROM usuarios WHERE username = 'admin'), 'Emergencias', 'EGRESO', 'OCASIONAL'),

-- Categorías para user
((SELECT id FROM usuarios WHERE username = 'user'), 'Salario', 'INGRESO', 'NECESARIO'),
((SELECT id FROM usuarios WHERE username = 'user'), 'Comida', 'EGRESO', 'NECESARIO'),
((SELECT id FROM usuarios WHERE username = 'user'), 'Facturas', 'EGRESO', 'NECESARIO'),
((SELECT id FROM usuarios WHERE username = 'user'), 'Ocio', 'EGRESO', 'NO NECESARIO');

-- =========================
-- MOVIMIENTOS DE EJEMPLO
-- =========================
INSERT INTO movimientos (user_id, categoria_id, monto, descripcion, tipo, fecha) VALUES
-- Movimientos para admin
((SELECT id FROM usuarios WHERE username = 'admin'), 
 (SELECT id FROM categorias WHERE user_id = (SELECT id FROM usuarios WHERE username = 'admin') AND nombre = 'Salario'), 
 3000000.00, 'Salario mensual', 'INGRESO', '2026-01-15'),
((SELECT id FROM usuarios WHERE username = 'admin'), 
 (SELECT id FROM categorias WHERE user_id = (SELECT id FROM usuarios WHERE username = 'admin') AND nombre = 'Alquiler'), 
 800000.00, 'Alquiler apartamento', 'EGRESO', '2026-01-05'),
((SELECT id FROM usuarios WHERE username = 'admin'), 
 (SELECT id FROM categorias WHERE user_id = (SELECT id FROM usuarios WHERE username = 'admin') AND nombre = 'Supermercado'), 
 450000.00, 'Compras mes', 'EGRESO', '2026-01-10'),

-- Movimientos para user
((SELECT id FROM usuarios WHERE username = 'user'), 
 (SELECT id FROM categorias WHERE user_id = (SELECT id FROM usuarios WHERE username = 'user') AND nombre = 'Salario'), 
 2000000.00, 'Salario quincenal', 'INGRESO', '2026-01-15'),
((SELECT id FROM usuarios WHERE username = 'user'), 
 (SELECT id FROM categorias WHERE user_id = (SELECT id FROM usuarios WHERE username = 'user') AND nombre = 'Comida'), 
 300000.00, 'Supermercado', 'EGRESO', '2026-01-08');

-- =========================
-- COMENTARIOS DE USO
-- =========================
/*
USUARIOS DE PRUEBA:
- Usuario: admin / Contraseña: admin123
- Usuario: user / Contraseña: user123

ESTRUCTURA:
- 2 usuarios con datos completos
- 10 categorías (5 por usuario)
- 5 movimientos de ejemplo

PARA USAR:
1. Ejecutar init.sql primero (crea estructura)
2. Ejecutar test-data.sql después (inserta datos)

NOTAS:
- Los hashes BCrypt son para strength=12
- Los usuarios tienen email y celular verificados
- Las fechas son ejemplos del mes actual
*/
