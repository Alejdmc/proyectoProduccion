-- =====================================================
-- SCRIPT COMPLETO SQLite - Sistema de Taller
-- Incluye: Tablas + Datos de Ejemplo
-- =====================================================

-- =====================================================
-- PASO 1: CREAR TABLAS
-- =====================================================

-- Tabla: clientes
CREATE TABLE IF NOT EXISTS clientes (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre      TEXT NOT NULL,
    telefono    TEXT,
    email       TEXT,
    direccion   TEXT
);

-- Tabla: vehiculos (con FK a clientes)
CREATE TABLE IF NOT EXISTS vehiculos (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    placa       TEXT NOT NULL UNIQUE,
    marca       TEXT NOT NULL,
    modelo      TEXT,
    anio        INTEGER,
    cliente_id  INTEGER NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- Tabla: ordenes (con FK a clientes y vehiculos)
CREATE TABLE IF NOT EXISTS ordenes (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    cliente_id       INTEGER NOT NULL,
    vehiculo_id      INTEGER NOT NULL,
    estado           TEXT NOT NULL DEFAULT 'RECIBIDO' CHECK(estado IN ('RECIBIDO', 'EN_PROCESO', 'ENTREGADO')),
    costo_repuestos  REAL NOT NULL DEFAULT 0.00,
    horas_trabajo    REAL NOT NULL DEFAULT 0.00,
    costo_hora       REAL NOT NULL DEFAULT 0.00,
    mano_obra        REAL NOT NULL DEFAULT 0.00,
    subtotal         REAL NOT NULL DEFAULT 0.00,
    iva              REAL NOT NULL DEFAULT 0.00,
    total            REAL NOT NULL DEFAULT 0.00,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    FOREIGN KEY (vehiculo_id) REFERENCES vehiculos(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- Indices para mejorar rendimiento
CREATE INDEX IF NOT EXISTS idx_vehiculo_cliente ON vehiculos(cliente_id);
CREATE INDEX IF NOT EXISTS idx_orden_cliente ON ordenes(cliente_id);
CREATE INDEX IF NOT EXISTS idx_orden_vehiculo ON ordenes(vehiculo_id);
CREATE INDEX IF NOT EXISTS idx_orden_estado ON ordenes(estado);

-- =====================================================
-- PASO 2: INSERTAR DATOS DE EJEMPLO
-- =====================================================

-- -----------------------------------------------------
-- CLIENTES (10 registros)
-- -----------------------------------------------------

INSERT INTO clientes (nombre, telefono, email, direccion) VALUES
('Juan Pérez García', '3001234567', 'juan.perez@email.com', 'Calle 45 #12-34, Bogotá'),
('María González López', '3109876543', 'maria.gonzalez@email.com', 'Carrera 10 #25-67, Medellín'),
('Carlos Rodríguez Martínez', '3201122334', 'carlos.rodriguez@email.com', 'Avenida 68 #100-23, Cali'),
('Ana Martínez Sánchez', '3152233445', 'ana.martinez@email.com', 'Calle 100 #15-45, Barranquilla'),
('Luis Hernández Díaz', '3183344556', 'luis.hernandez@email.com', 'Carrera 7 #32-18, Cartagena'),
('Laura Gómez Ruiz', '3124455667', 'laura.gomez@email.com', 'Calle 85 #20-30, Bucaramanga'),
('Pedro Ramírez Torres', '3165566778', 'pedro.ramirez@email.com', 'Avenida Jiménez #5-40, Pereira'),
('Sofia Castro Vargas', '3196677889', 'sofia.castro@email.com', 'Calle 50 #8-15, Manizales'),
('Diego Morales Ortiz', '3007788990', 'diego.morales@email.com', 'Carrera 15 #60-25, Ibagué'),
('Carmen Flores Jiménez', '3138899001', 'carmen.flores@email.com', 'Calle 72 #11-50, Pasto');

-- -----------------------------------------------------
-- VEHICULOS (11 registros)
-- -----------------------------------------------------

INSERT INTO vehiculos (placa, marca, modelo, anio, cliente_id) VALUES
-- Cliente 1: Juan Pérez (2 vehículos)
('ABC123', 'Toyota', 'Corolla', 2020, 1),
('DEF456', 'Chevrolet', 'Spark', 2018, 1),

-- Cliente 2: María González (1 vehículo)
('GHI789', 'Mazda', 'CX-5', 2021, 2),

-- Cliente 3: Carlos Rodríguez (2 vehículos)
('JKL012', 'Renault', 'Logan', 2019, 3),
('MNO345', 'Nissan', 'Sentra', 2022, 3),

-- Cliente 4: Ana Martínez (1 vehículo)
('PQR678', 'Hyundai', 'Tucson', 2020, 4),

-- Cliente 5: Luis Hernández (1 vehículo)
('STU901', 'Kia', 'Sportage', 2021, 5),

-- Cliente 6: Laura Gómez (2 vehículos)
('VWX234', 'Honda', 'Civic', 2019, 6),
('YZA567', 'Ford', 'Fiesta', 2017, 6),

-- Cliente 7: Pedro Ramírez (1 vehículo)
('BCD890', 'Volkswagen', 'Gol', 2018, 7),

-- Cliente 8: Sofia Castro (1 vehículo)
('EFG123', 'Suzuki', 'Swift', 2020, 8);

-- -----------------------------------------------------
-- ORDENES (12 registros)
-- -----------------------------------------------------

INSERT INTO ordenes (cliente_id, vehiculo_id, estado, costo_repuestos, horas_trabajo, costo_hora, mano_obra, subtotal, iva, total) VALUES

-- Órdenes ENTREGADAS (4 registros)
(1, 1, 'ENTREGADO', 150000.00, 3.0, 50000.00, 150000.00, 300000.00, 57000.00, 357000.00),
(2, 3, 'ENTREGADO', 250000.00, 4.5, 50000.00, 225000.00, 475000.00, 90250.00, 565250.00),
(3, 4, 'ENTREGADO', 80000.00, 2.0, 50000.00, 100000.00, 180000.00, 34200.00, 214200.00),
(6, 9, 'ENTREGADO', 75000.00, 1.5, 50000.00, 75000.00, 150000.00, 28500.00, 178500.00),

-- Órdenes EN_PROCESO (4 registros)
(1, 2, 'EN_PROCESO', 200000.00, 5.0, 50000.00, 250000.00, 450000.00, 85500.00, 535500.00),
(4, 6, 'EN_PROCESO', 180000.00, 3.5, 50000.00, 175000.00, 355000.00, 67450.00, 422450.00),
(5, 7, 'EN_PROCESO', 320000.00, 6.0, 50000.00, 300000.00, 620000.00, 117800.00, 737800.00),
(2, 3, 'EN_PROCESO', 280000.00, 5.5, 50000.00, 275000.00, 555000.00, 105450.00, 660450.00),

-- Órdenes RECIBIDAS (4 registros)
(6, 8, 'RECIBIDO', 100000.00, 2.5, 50000.00, 125000.00, 225000.00, 42750.00, 267750.00),
(3, 5, 'RECIBIDO', 450000.00, 8.0, 50000.00, 400000.00, 850000.00, 161500.00, 1011500.00),
(7, 10, 'RECIBIDO', 95000.00, 2.0, 50000.00, 100000.00, 195000.00, 37050.00, 232050.00),
(8, 11, 'RECIBIDO', 120000.00, 3.0, 50000.00, 150000.00, 270000.00, 51300.00, 321300.00);

-- =====================================================
-- PASO 3: VERIFICAR DATOS
-- =====================================================

-- Contar registros por tabla
SELECT
    'CLIENTES' AS tabla,
    COUNT(*) AS total
FROM clientes

UNION ALL

SELECT
    'VEHICULOS' AS tabla,
    COUNT(*) AS total
FROM vehiculos

UNION ALL

SELECT
    'ORDENES' AS tabla,
    COUNT(*) AS total
FROM ordenes;

-- Resumen de órdenes por estado
SELECT
    estado,
    COUNT(*) AS cantidad,
    ROUND(SUM(total), 2) AS total_ventas
FROM ordenes
GROUP BY estado
ORDER BY
    CASE estado
        WHEN 'RECIBIDO' THEN 1
        WHEN 'EN_PROCESO' THEN 2
        WHEN 'ENTREGADO' THEN 3
    END;

-- Total general
SELECT
    COUNT(*) AS total_ordenes,
    ROUND(SUM(total), 2) AS ventas_totales
FROM ordenes;

-- =====================================================
-- FIN DEL SCRIPT
-- =====================================================

