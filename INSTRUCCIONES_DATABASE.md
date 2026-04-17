# INSTRUCCIONES PARA CREAR LA BASE DE DATOS

## Ya tienes MySQL Workbench abierto y conectado

### OPCION 1: Copiar y pegar el SQL (MAS RAPIDO)

1. En MySQL Workbench, crea una nueva pestaña de Query:
   - Menu: **File > New Query Tab**
   - O presiona: **Ctrl + T**

2. **Copia y pega** todo este SQL en la nueva pestaña:

```sql
-- =====================================================
-- BASE DE DATOS: Sistema de Taller - Proyecto Produccion
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
-- INDICES (solo los necesarios)
-- =====================================================
CREATE INDEX idx_vehiculo_cliente ON vehiculos(cliente_id);
CREATE INDEX idx_orden_cliente    ON ordenes(cliente_id);
CREATE INDEX idx_orden_vehiculo   ON ordenes(vehiculo_id);
CREATE INDEX idx_orden_estado     ON ordenes(estado);
```

3. **Ejecuta** el SQL:
   - Haz clic en el icono del **rayo** (Execute) en la barra de herramientas
   - O presiona: **Ctrl + Shift + Enter**

4. **Verifica** que se crearon las tablas:
   ```sql
   SHOW TABLES;
   ```
   
   Deberías ver:
   - clientes
   - vehiculos
   - ordenes

### OPCION 2: Abrir el archivo SQL

1. En MySQL Workbench, ve a:
   **File > Open SQL Script...**

2. Navega a:
   `C:\Users\sala7\IdeaProjects\proyectoProduccion\database.sql`

3. Haz clic en **Open**

4. Haz clic en el icono del **rayo** (Execute) para ejecutar

## Siguientes Pasos

Una vez creadas las tablas, puedes:

1. **Insertar datos de prueba** ejecutando en MySQL Workbench:
   ```sql
   -- Ver archivo datos_ejemplo.sql
   ```

2. **Ejecutar la aplicacion Java**:
   ```powershell
   .\iniciar.ps1
   ```

## Notas

- Tu configuracion actual en database.properties:
  - Host: 172.30.16.52
  - Puerto: 3306
  - Usuario: damonroy86
  - Base de datos: taller_db

- Ya tienes esta configuracion funcionando en MySQL Workbench

