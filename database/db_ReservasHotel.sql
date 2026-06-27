-- =============================================================
-- HOTEL EXCLUSIVE - INSTALACIÓN INICIAL
-- ADVERTENCIA: ESTE SCRIPT ELIMINA LA BASE LP2Final SI YA EXISTE.
-- =============================================================

DROP DATABASE IF EXISTS LP2Final;
CREATE DATABASE LP2Final
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE LP2Final;

CREATE TABLE clientes (
  id_cliente INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(150) NOT NULL,
  ruc_dni VARCHAR(11) NOT NULL,
  direccion VARCHAR(255),
  telefono VARCHAR(15),
  CONSTRAINT uk_cliente_ruc_dni UNIQUE (ruc_dni),
  CONSTRAINT ck_cliente_documento CHECK (CHAR_LENGTH(ruc_dni) IN (8, 11)),
  CONSTRAINT ck_cliente_telefono CHECK (telefono IS NULL OR telefono REGEXP '^[0-9]{7,15}$')
) ENGINE=InnoDB;

CREATE TABLE empleados (
  id_empleado INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  apellido VARCHAR(100) NOT NULL,
  dni VARCHAR(8) NOT NULL,
  cargo VARCHAR(30) NOT NULL,
  telefono VARCHAR(15),
  CONSTRAINT uk_empleado_dni UNIQUE (dni),
  CONSTRAINT ck_empleado_dni CHECK (CHAR_LENGTH(dni) = 8),
  CONSTRAINT ck_empleado_cargo CHECK (cargo IN ('RECEPCIONISTA','ADMINISTRADOR','SUPERVISOR','GERENTE')),
  CONSTRAINT ck_empleado_telefono CHECK (telefono IS NULL OR telefono REGEXP '^[0-9]{7,15}$')
) ENGINE=InnoDB;

CREATE TABLE tipos_habitacion (
  id_tipo INT AUTO_INCREMENT PRIMARY KEY,
  nombre_tipo VARCHAR(50) NOT NULL,
  descripcion TEXT,
  precio_noche DECIMAL(10,2) NOT NULL,
  capacidad_personas INT NOT NULL,
  CONSTRAINT uk_tipo_nombre UNIQUE (nombre_tipo),
  CONSTRAINT ck_tipo_precio CHECK (precio_noche > 0),
  CONSTRAINT ck_tipo_capacidad CHECK (capacidad_personas > 0)
) ENGINE=InnoDB;

CREATE TABLE habitaciones (
  id_habitacion INT AUTO_INCREMENT PRIMARY KEY,
  id_tipo INT NOT NULL,
  numero_habitacion VARCHAR(10) NOT NULL,
  piso INT NOT NULL,
  estado VARCHAR(30) NOT NULL DEFAULT 'DISPONIBLE',
  CONSTRAINT uk_habitacion_numero UNIQUE (numero_habitacion),
  CONSTRAINT ck_habitacion_piso CHECK (piso > 0),
  CONSTRAINT ck_habitacion_estado CHECK (estado IN ('DISPONIBLE','MANTENIMIENTO','LIMPIEZA','FUERA_DE_SERVICIO')),
  CONSTRAINT fk_habitacion_tipo FOREIGN KEY (id_tipo) REFERENCES tipos_habitacion(id_tipo)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE usuarios (
  id_usuario INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  apellido VARCHAR(100) NOT NULL,
  correo VARCHAR(150) NOT NULL,
  password VARCHAR(100) NOT NULL,
  rol VARCHAR(20) NOT NULL DEFAULT 'RECEPCIONISTA',
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT uk_usuario_correo UNIQUE (correo),
  CONSTRAINT ck_usuario_rol CHECK (rol IN ('ADMIN','RECEPCIONISTA'))
) ENGINE=InnoDB;

CREATE TABLE reservas (
  id_reserva INT AUTO_INCREMENT PRIMARY KEY,
  id_cliente INT NOT NULL,
  id_empleado INT NOT NULL,
  fecha_reserva DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_ingreso DATE NOT NULL,
  fecha_salida DATE NOT NULL,
  estado_reserva VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
  tipo_comprobante VARCHAR(20),
  numero_comprobante VARCHAR(50),
  cantidad_huespedes INT NOT NULL DEFAULT 1,
  monto_total DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  CONSTRAINT uk_reserva_comprobante UNIQUE (numero_comprobante),
  CONSTRAINT ck_reserva_fechas CHECK (fecha_salida > fecha_ingreso),
  CONSTRAINT ck_reserva_estado CHECK (estado_reserva IN ('PENDIENTE','CONFIRMADA','EN_CURSO','FINALIZADA','CANCELADA')),
  CONSTRAINT ck_reserva_comprobante CHECK (tipo_comprobante IS NULL OR tipo_comprobante IN ('BOLETA','FACTURA')),
  CONSTRAINT ck_reserva_huespedes CHECK (cantidad_huespedes > 0),
  CONSTRAINT ck_reserva_total CHECK (monto_total >= 0),
  CONSTRAINT fk_reserva_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_reserva_empleado FOREIGN KEY (id_empleado) REFERENCES empleados(id_empleado)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  INDEX idx_reserva_fechas (fecha_ingreso, fecha_salida),
  INDEX idx_reserva_estado (estado_reserva)
) ENGINE=InnoDB;

CREATE TABLE detalle_reservas (
  id_detalle INT AUTO_INCREMENT PRIMARY KEY,
  id_reserva INT NOT NULL,
  id_habitacion INT NOT NULL,
  precio_aplicado DECIMAL(10,2) NOT NULL,
  CONSTRAINT uk_detalle_reserva_habitacion UNIQUE (id_reserva, id_habitacion),
  CONSTRAINT ck_detalle_precio CHECK (precio_aplicado > 0),
  CONSTRAINT fk_detalle_reserva FOREIGN KEY (id_reserva) REFERENCES reservas(id_reserva)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_detalle_habitacion FOREIGN KEY (id_habitacion) REFERENCES habitaciones(id_habitacion)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  INDEX idx_detalle_habitacion (id_habitacion)
) ENGINE=InnoDB;

CREATE TABLE servicios_adicionales (
  id_servicio INT AUTO_INCREMENT PRIMARY KEY,
  nombre_servicio VARCHAR(100) NOT NULL,
  precio DECIMAL(10,2) NOT NULL,
  CONSTRAINT uk_servicio_nombre UNIQUE (nombre_servicio),
  CONSTRAINT ck_servicio_precio CHECK (precio > 0)
) ENGINE=InnoDB;

CREATE TABLE servicios_reserva (
  id_servicio_reserva INT AUTO_INCREMENT PRIMARY KEY,
  id_reserva INT NOT NULL,
  id_servicio INT NOT NULL,
  cantidad INT NOT NULL DEFAULT 1,
  precio_aplicado DECIMAL(10,2) NOT NULL,
  fecha_consumo DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT ck_consumo_cantidad CHECK (cantidad > 0),
  CONSTRAINT ck_consumo_precio CHECK (precio_aplicado > 0),
  CONSTRAINT fk_consumo_reserva FOREIGN KEY (id_reserva) REFERENCES reservas(id_reserva)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_consumo_servicio FOREIGN KEY (id_servicio) REFERENCES servicios_adicionales(id_servicio)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  INDEX idx_consumo_reserva (id_reserva)
) ENGINE=InnoDB;

-- Usuario administrador inicial.
-- Correo: admin@hotel.com
-- Contraseña: Admin123*
INSERT INTO usuarios (nombre, apellido, correo, password, rol, activo) VALUES
('Administrador', 'Principal', 'admin@hotel.com', '$2a$10$WQdI7nxx.GqbRROzo9xRBu0DmGUvIn8w9HDcmsD0uozGGI49YejC2', 'ADMIN', TRUE);

INSERT INTO clientes (nombre, ruc_dni, direccion, telefono) VALUES
('Carlos Alberto Mendoza Ruiz', '45812740', 'Av. Larco 456, Miraflores - Lima', '999888101'),
('Ana María Aguilar Zacarías', '10161204', 'Calle San José 123, Chiclayo', '999888102'),
('Luis Fernando Torres Puno', '20406502', 'Jr. Grau 789, Juliaca - Puno', '999888103'),
('María Esperanza Caisa Beltrán', '20331634', 'Urb. El Sol Mz. F Lote 12, Trujillo', '999888104'),
('Juan Carlos San Luis Echevarría', '20102259', 'Av. Arequipa 2500, Lince - Lima', '999888105');

INSERT INTO empleados (nombre, apellido, dni, cargo, telefono) VALUES
('Alexis', 'García', '71234561', 'RECEPCIONISTA', '911111111'),
('Samuel', 'López', '71234562', 'RECEPCIONISTA', '922222222'),
('Enzo', 'Mendoza', '71234563', 'ADMINISTRADOR', '933333333'),
('Nikolas', 'Torres', '71234564', 'SUPERVISOR', '944444444');

INSERT INTO tipos_habitacion (nombre_tipo, descripcion, precio_noche, capacidad_personas) VALUES
('Simple Estándar', 'Cama de plaza y media, escritorio de trabajo y baño propio.', 120.00, 1),
('Doble Ejecutiva', 'Dos camas, Wi-Fi, aire acondicionado y escritorio.', 220.00, 2),
('Suite Matrimonial VIP', 'Cama King Size, jacuzzi, minibar y vista panorámica.', 450.00, 2);

INSERT INTO habitaciones (id_tipo, numero_habitacion, piso, estado) VALUES
(1, '101', 1, 'DISPONIBLE'),
(1, '102', 1, 'DISPONIBLE'),
(2, '201', 2, 'DISPONIBLE'),
(2, '202', 2, 'DISPONIBLE'),
(3, '301', 3, 'DISPONIBLE'),
(3, '302', 3, 'MANTENIMIENTO');

INSERT INTO servicios_adicionales (nombre_servicio, precio) VALUES
('Room Service - Cena Ejecutiva', 45.00),
('Lavandería Express - Por prenda', 15.00),
('Mini Bar Premium', 25.00),
('Cochera Privada - Por día', 20.00);

INSERT INTO reservas
(id_cliente, id_empleado, fecha_ingreso, fecha_salida, estado_reserva, tipo_comprobante, numero_comprobante, cantidad_huespedes, monto_total) VALUES
(1, 1, '2026-06-15', '2026-06-18', 'CONFIRMADA', 'FACTURA', 'F001-000001', 1, 455.00),
(2, 2, '2026-06-20', '2026-06-22', 'CONFIRMADA', 'BOLETA', 'B001-000002', 2, 485.00),
(3, 1, '2026-07-01', '2026-07-05', 'PENDIENTE', NULL, NULL, 2, 1800.00);

INSERT INTO detalle_reservas (id_reserva, id_habitacion, precio_aplicado) VALUES
(1, 1, 120.00),
(2, 3, 220.00),
(3, 5, 450.00);

INSERT INTO servicios_reserva (id_reserva, id_servicio, cantidad, precio_aplicado) VALUES
(1, 3, 2, 25.00),
(1, 2, 3, 15.00),
(2, 1, 1, 45.00);

-- =============================================================
-- VISTAS PARA JASPERREPORTS - VERSION V6
-- =============================================================

-- Reporte general de reservas registradas en la fecha actual.
CREATE OR REPLACE VIEW vista_reservas_hoy AS
SELECT
  r.id_reserva,
  r.numero_comprobante,
  r.fecha_reserva,
  c.nombre AS cliente_nombre,
  c.ruc_dni AS cliente_documento,
  r.fecha_ingreso,
  r.fecha_salida,
  COALESCE(
    GROUP_CONCAT(h.numero_habitacion ORDER BY h.numero_habitacion SEPARATOR ', '),
    'Sin asignar'
  ) AS habitaciones,
  r.estado_reserva,
  r.monto_total
FROM reservas r
INNER JOIN clientes c ON c.id_cliente = r.id_cliente
LEFT JOIN detalle_reservas dr ON dr.id_reserva = r.id_reserva
LEFT JOIN habitaciones h ON h.id_habitacion = dr.id_habitacion
WHERE DATE(r.fecha_reserva) = CURDATE()
GROUP BY
  r.id_reserva, r.numero_comprobante, r.fecha_reserva,
  c.nombre, c.ruc_dni, r.fecha_ingreso, r.fecha_salida,
  r.estado_reserva, r.monto_total;

-- Reporte general de servicios consumidos en la fecha actual.
CREATE OR REPLACE VIEW vista_consumos_hoy AS
SELECT
  sr.id_servicio_reserva,
  r.id_reserva,
  r.numero_comprobante,
  c.nombre AS cliente_nombre,
  sa.nombre_servicio,
  sr.cantidad,
  sr.precio_aplicado,
  (sr.cantidad * sr.precio_aplicado) AS subtotal,
  sr.fecha_consumo
FROM servicios_reserva sr
INNER JOIN reservas r ON r.id_reserva = sr.id_reserva
INNER JOIN clientes c ON c.id_cliente = r.id_cliente
INNER JOIN servicios_adicionales sa ON sa.id_servicio = sr.id_servicio
WHERE DATE(sr.fecha_consumo) = CURDATE();

-- Vista parametrizada por id_reserva para el comprobante individual.
CREATE OR REPLACE VIEW vista_comprobante_reserva AS
SELECT
  r.id_reserva,
  COALESCE(r.numero_comprobante, CONCAT('RES-', LPAD(r.id_reserva, 6, '0'))) AS numero_comprobante,
  COALESCE(r.tipo_comprobante, 'RESERVA') AS tipo_comprobante,
  r.fecha_reserva,
  c.nombre AS cliente_nombre,
  c.ruc_dni AS cliente_documento,
  c.direccion AS cliente_direccion,
  CONCAT(e.nombre, ' ', e.apellido) AS empleado_nombre,
  r.fecha_ingreso,
  r.fecha_salida,
  r.cantidad_huespedes,
  r.estado_reserva,
  1 AS item_orden,
  dr.id_detalle AS item_id,
  CONCAT('Hab. ', h.numero_habitacion, ' - ', th.nombre_tipo) AS concepto,
  DATEDIFF(r.fecha_salida, r.fecha_ingreso) AS cantidad,
  dr.precio_aplicado AS precio_unitario,
  (DATEDIFF(r.fecha_salida, r.fecha_ingreso) * dr.precio_aplicado) AS subtotal,
  r.monto_total
FROM reservas r
INNER JOIN clientes c ON c.id_cliente = r.id_cliente
INNER JOIN empleados e ON e.id_empleado = r.id_empleado
INNER JOIN detalle_reservas dr ON dr.id_reserva = r.id_reserva
INNER JOIN habitaciones h ON h.id_habitacion = dr.id_habitacion
INNER JOIN tipos_habitacion th ON th.id_tipo = h.id_tipo

UNION ALL

SELECT
  r.id_reserva,
  COALESCE(r.numero_comprobante, CONCAT('RES-', LPAD(r.id_reserva, 6, '0'))) AS numero_comprobante,
  COALESCE(r.tipo_comprobante, 'RESERVA') AS tipo_comprobante,
  r.fecha_reserva,
  c.nombre AS cliente_nombre,
  c.ruc_dni AS cliente_documento,
  c.direccion AS cliente_direccion,
  CONCAT(e.nombre, ' ', e.apellido) AS empleado_nombre,
  r.fecha_ingreso,
  r.fecha_salida,
  r.cantidad_huespedes,
  r.estado_reserva,
  2 AS item_orden,
  sr.id_servicio_reserva AS item_id,
  sa.nombre_servicio AS concepto,
  sr.cantidad AS cantidad,
  sr.precio_aplicado AS precio_unitario,
  (sr.cantidad * sr.precio_aplicado) AS subtotal,
  r.monto_total
FROM reservas r
INNER JOIN clientes c ON c.id_cliente = r.id_cliente
INNER JOIN empleados e ON e.id_empleado = r.id_empleado
INNER JOIN servicios_reserva sr ON sr.id_reserva = r.id_reserva
INNER JOIN servicios_adicionales sa ON sa.id_servicio = sr.id_servicio;

-- Consultas de verificacion:
-- SELECT * FROM vista_reservas_hoy;
-- SELECT * FROM vista_consumos_hoy;
-- SELECT * FROM vista_comprobante_reserva WHERE id_reserva = 1;

