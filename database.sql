-- =====================================================
-- BASE DE DATOS: Sistema de Taller - Proyecto Producción
-- Motor: MySQL
-- =====================================================

CREATE DATABASE IF NOT EXISTS taller_db;
USE taller_db;

-- =====================================================
-- TABLA: clientes (entidad padre)
-- =====================================================
CREATE TABLE clientes (
                          id          INT NOT NULL AUTO_INCREMENT,
                          nombre      VARCHAR(150) NOT NULL,
                          telefono    VARCHAR(20),
                          email       VARCHAR(150),
                          direccion   VARCHAR(255),
                          PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- TABLA: vehiculos (FK → clientes)
-- =====================================================
CREATE TABLE vehiculos (
                           id          INT NOT NULL AUTO_INCREMENT,
                           placa       VARCHAR(20) NOT NULL,
                           marca       VARCHAR(100) NOT NULL,
                           modelo      VARCHAR(100),
                           anio        INT,
                           cliente_id  INT NOT NULL,
                           PRIMARY KEY (id),
                           UNIQUE KEY uk_placa (placa),
                           CONSTRAINT fk_vehiculo_cliente
                               FOREIGN KEY (cliente_id) REFERENCES clientes(id)
                                   ON UPDATE CASCADE
                                   ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- TABLA: ordenes
-- =====================================================
CREATE TABLE ordenes (
                         id               INT NOT NULL AUTO_INCREMENT,
                         cliente_id       INT NOT NULL,
                         vehiculo_id      INT NOT NULL,
                         estado           ENUM('RECIBIDO', 'EN_PROCESO', 'ENTREGADO') NOT NULL DEFAULT 'RECIBIDO',
                         costo_repuestos  DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                         horas_trabajo    DECIMAL(8,2) NOT NULL DEFAULT 0.00,
                         costo_hora       DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                         mano_obra        DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                         subtotal         DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                         iva              DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                         total            DECIMAL(12,2) NOT NULL DEFAULT 0.00,
                         PRIMARY KEY (id),

                         CONSTRAINT fk_orden_cliente
                             FOREIGN KEY (cliente_id) REFERENCES clientes(id)
                                 ON UPDATE CASCADE
                                 ON DELETE RESTRICT,

                         CONSTRAINT fk_orden_vehiculo
                             FOREIGN KEY (vehiculo_id) REFERENCES vehiculos(id)
                                 ON UPDATE CASCADE
                                 ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- ÍNDICES (solo los necesarios)
-- =====================================================
CREATE INDEX idx_vehiculo_cliente ON vehiculos(cliente_id);
CREATE INDEX idx_orden_cliente    ON ordenes(cliente_id);
CREATE INDEX idx_orden_vehiculo   ON ordenes(vehiculo_id);
CREATE INDEX idx_orden_estado     ON ordenes(estado);