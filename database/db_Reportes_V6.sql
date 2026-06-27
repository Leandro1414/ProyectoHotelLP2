-- =============================================================
-- HOTEL EXCLUSIVE - ACTUALIZACION DE REPORTES V6
-- Este script NO elimina tablas ni datos. Ejecutarlo una sola vez
-- sobre una base LP2Final existente antes de usar los reportes.
-- =============================================================

USE LP2Final;


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
