-- =====================================================
-- BASE DE DATOS: Sistema de Taller - Proyecto Produccion
-- Motor: SQLite
-- =====================================================

-- SQLite no necesita CREATE DATABASE, usa archivos

-- =====================================================
-- TABLA: clientes (entidad padre)
-- =====================================================
CREATE TABLE IF NOT EXISTS clientes (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre      TEXT NOT NULL,
    telefono    TEXT,
    email       TEXT,
    direccion   TEXT
);

-- =====================================================
-- TABLA: vehiculos (FK → clientes)
-- =====================================================
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

-- =====================================================
-- TABLA: ordenes
-- =====================================================
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

-- =====================================================
-- INDICES (solo los necesarios)
-- =====================================================
CREATE INDEX IF NOT EXISTS idx_vehiculo_cliente ON vehiculos(cliente_id);
CREATE INDEX IF NOT EXISTS idx_orden_cliente ON ordenes(cliente_id);
CREATE INDEX IF NOT EXISTS idx_orden_vehiculo ON ordenes(vehiculo_id);
CREATE INDEX IF NOT EXISTS idx_orden_estado ON ordenes(estado);

