# INSERTAR DATOS DE EJEMPLO

## Ya tienes las tablas creadas. Ahora vamos a insertar los datos.

### OPCION 1: Copiar y pegar el SQL (MAS RAPIDO)

1. En MySQL Workbench, **abre una nueva pestaña** de Query (Ctrl + T)

2. **Copia y pega** todo este SQL:

```sql
-- =====================================================
-- DATOS DE EJEMPLO - Sistema de Taller
-- =====================================================

USE taller_db;

-- =====================================================
-- INSERTAR CLIENTES
-- =====================================================

INSERT INTO clientes (nombre, telefono, email, direccion) VALUES
('Juan Perez Garcia', '3001234567', 'juan.perez@email.com', 'Calle 45 #12-34, Bogota'),
('Maria Gonzalez Lopez', '3109876543', 'maria.gonzalez@email.com', 'Carrera 10 #25-67, Medellin'),
('Carlos Rodriguez Martinez', '3201122334', 'carlos.rodriguez@email.com', 'Avenida 68 #100-23, Cali'),
('Ana Martinez Sanchez', '3152233445', 'ana.martinez@email.com', 'Calle 100 #15-45, Barranquilla'),
('Luis Hernandez Diaz', '3183344556', 'luis.hernandez@email.com', 'Carrera 7 #32-18, Cartagena'),
('Laura Gomez Ruiz', '3124455667', 'laura.gomez@email.com', 'Calle 85 #20-30, Bucaramanga'),
('Pedro Ramirez Torres', '3165566778', 'pedro.ramirez@email.com', 'Avenida Jimenez #5-40, Pereira'),
('Sofia Castro Vargas', '3196677889', 'sofia.castro@email.com', 'Calle 50 #8-15, Manizales'),
('Diego Morales Ortiz', '3007788990', 'diego.morales@email.com', 'Carrera 15 #60-25, Ibague'),
('Carmen Flores Jimenez', '3138899001', 'carmen.flores@email.com', 'Calle 72 #11-50, Pasto');

-- =====================================================
-- INSERTAR VEHICULOS
-- =====================================================

INSERT INTO vehiculos (placa, marca, modelo, anio, cliente_id) VALUES
-- Cliente 1: Juan Perez (2 vehiculos)
('ABC123', 'Toyota', 'Corolla', 2020, 1),
('DEF456', 'Chevrolet', 'Spark', 2018, 1),

-- Cliente 2: Maria Gonzalez (1 vehiculo)
('GHI789', 'Mazda', 'CX-5', 2021, 2),

-- Cliente 3: Carlos Rodriguez (2 vehiculos)
('JKL012', 'Renault', 'Logan', 2019, 3),
('MNO345', 'Nissan', 'Sentra', 2022, 3),

-- Cliente 4: Ana Martinez (1 vehiculo)
('PQR678', 'Hyundai', 'Tucson', 2020, 4),

-- Cliente 5: Luis Hernandez (1 vehiculo)
('STU901', 'Kia', 'Sportage', 2021, 5),

-- Cliente 6: Laura Gomez (2 vehiculos)
('VWX234', 'Honda', 'Civic', 2019, 6),
('YZA567', 'Ford', 'Fiesta', 2017, 6),

-- Cliente 7: Pedro Ramirez (1 vehiculo)
('BCD890', 'Volkswagen', 'Gol', 2018, 7),

-- Cliente 8: Sofia Castro (1 vehiculo)
('EFG123', 'Suzuki', 'Swift', 2020, 8);

-- =====================================================
-- INSERTAR ORDENES DE SERVICIO
-- =====================================================

INSERT INTO ordenes (cliente_id, vehiculo_id, estado, costo_repuestos, horas_trabajo, costo_hora, mano_obra, subtotal, iva, total) VALUES
-- Ordenes completadas
(1, 1, 'ENTREGADO', 150000.00, 3.0, 50000.00, 150000.00, 300000.00, 57000.00, 357000.00),
(2, 3, 'ENTREGADO', 250000.00, 4.5, 50000.00, 225000.00, 475000.00, 90250.00, 565250.00),
(3, 4, 'ENTREGADO', 80000.00, 2.0, 50000.00, 100000.00, 180000.00, 34200.00, 214200.00),

-- Ordenes en proceso
(1, 2, 'EN_PROCESO', 200000.00, 5.0, 50000.00, 250000.00, 450000.00, 85500.00, 535500.00),
(4, 6, 'EN_PROCESO', 180000.00, 3.5, 50000.00, 175000.00, 355000.00, 67450.00, 422450.00),
(5, 7, 'EN_PROCESO', 320000.00, 6.0, 50000.00, 300000.00, 620000.00, 117800.00, 737800.00),

-- Ordenes recibidas
(6, 8, 'RECIBIDO', 100000.00, 2.5, 50000.00, 125000.00, 225000.00, 42750.00, 267750.00),
(3, 5, 'RECIBIDO', 450000.00, 8.0, 50000.00, 400000.00, 850000.00, 161500.00, 1011500.00),
(7, 10, 'RECIBIDO', 95000.00, 2.0, 50000.00, 100000.00, 195000.00, 37050.00, 232050.00),
(8, 11, 'RECIBIDO', 120000.00, 3.0, 50000.00, 150000.00, 270000.00, 51300.00, 321300.00),

-- Ordenes adicionales
(6, 9, 'ENTREGADO', 75000.00, 1.5, 50000.00, 75000.00, 150000.00, 28500.00, 178500.00),
(2, 3, 'EN_PROCESO', 280000.00, 5.5, 50000.00, 275000.00, 555000.00, 105450.00, 660450.00);

-- =====================================================
-- VERIFICAR DATOS INSERTADOS
-- =====================================================

-- Contar registros
SELECT
    (SELECT COUNT(*) FROM clientes) AS total_clientes,
    (SELECT COUNT(*) FROM vehiculos) AS total_vehiculos,
    (SELECT COUNT(*) FROM ordenes) AS total_ordenes;

-- Mostrar resumen de ordenes por estado
SELECT
    estado,
    COUNT(*) AS cantidad,
    SUM(total) AS total_ventas
FROM ordenes
GROUP BY estado
ORDER BY
    FIELD(estado, 'RECIBIDO', 'EN_PROCESO', 'ENTREGADO');
```

3. **Ejecuta** el SQL: Haz clic en el icono del **rayo** (Execute) o presiona **Ctrl + Shift + Enter**

4. **Verifica los resultados** - Deberías ver al final:
   - **10 clientes** insertados
   - **11 vehículos** insertados
   - **12 órdenes** insertadas

### OPCION 2: Abrir el archivo

1. En MySQL Workbench: **File > Open SQL Script...**
2. Selecciona: `C:\Users\sala7\IdeaProjects\proyectoProduccion\datos_ejemplo.sql`
3. Haz clic en **Execute**

## Datos insertados

### Clientes (10):
- Juan Perez, Maria Gonzalez, Carlos Rodriguez, Ana Martinez, Luis Hernandez
- Laura Gomez, Pedro Ramirez, Sofia Castro, Diego Morales, Carmen Flores

### Vehiculos (11):
- Varios vehículos de diferentes marcas (Toyota, Chevrolet, Mazda, Renault, etc.)

### Ordenes (12):
- **3 Entregadas** - Total: $1,115,950
- **4 En Proceso** - Total: $2,356,200  
- **5 Recibidas** - Total: $1,844,100

## Siguiente paso

Una vez insertados los datos, puedes **ejecutar la aplicación**:

```powershell
.\iniciar.ps1
```

O si estás en Mac:
```bash
./iniciar-mac.sh
```

