# Sistema de Taller - Gestión de Órdenes de Servicio

Sistema de escritorio para gestión de talleres mecánicos desarrollado en JavaFX.

Compatible con Windows y Mac. Soporta MySQL y SQLite.

## Características

- Gestión de Clientes
- Gestión de Vehículos
- Órdenes de Servicio con seguimiento de estados
- Cálculo automático de costos con IVA
- Generación de comprobantes
- Resumen financiero del taller
- Selección de base de datos al iniciar (MySQL o SQLite)

## Requisitos

- Java 21 o superior
- Maven (incluido en el proyecto)
- MySQL 8.0+ (opcional, si se usa MySQL)

## Instalación

### 1. Clonar o descargar el proyecto

### 2. Crear la base de datos

Con MySQL:
```sql
source database.sql;
```

Con SQLite: El archivo `taller_db.sqlite` ya está incluido.

### 3. Configurar conexión (solo para MySQL)

Editar `database.properties`:

```properties
db.host=localhost
db.port=3306
db.name=taller_db
db.user=root
db.password=
```

### 4. Ejecutar

Mac:
```bash
chmod +x iniciar-mac.sh
./iniciar-mac.sh
```

Windows:
```cmd
mvnw.cmd javafx:run
```

Al iniciar, seleccionar la base de datos a usar: MySQL o SQLite.

## Estructura del Proyecto

```
proyectoProduccion/
├── database.properties      # Configuración de conexión
├── database.sql             # Script MySQL
├── database-sqlite.sql      # Script SQLite
├── taller_db.sqlite         # Base de datos SQLite
├── pom.xml                  # Configuración Maven
├── iniciar-mac.sh           # Script de inicio Mac
│
├── src/main/java/com/proyectoproduccion/
│   ├── Main.java
│   ├── Controlador/
│   │   ├── ClientesController.java
│   │   ├── VehiculosController.java
│   │   ├── OrdenesController.java
│   │   ├── ReportesController.java
│   │   └── LayoutController.java
│   ├── Modelo/
│   │   ├── Cliente.java
│   │   ├── Vehiculo.java
│   │   └── Orden.java
│   └── Util/
│       ├── ConfigDB.java
│       ├── Conexion.java
│       ├── DatabaseUtil.java
│       └── SceneManager.java
│
└── src/main/resources/com/proyectoproduccion/
    ├── layout.fxml
    ├── clientes.fxml
    ├── vehiculos.fxml
    ├── ordenesServicio.fxml
    └── reportes.fxml
```

## Base de Datos

### Tablas

**clientes**
- id, nombre, telefono, email, direccion

**vehiculos**
- id, placa, marca, modelo, anio, cliente_id

**ordenes**
- id, cliente_id, vehiculo_id, estado
- costo_repuestos, horas_trabajo, costo_hora
- mano_obra, subtotal, iva, total

## Uso

### Crear Cliente
1. Ir a la sección Clientes
2. Clic en "Nuevo Cliente"
3. Llenar datos y guardar

### Crear Vehículo
1. Ir a la sección Vehículos
2. Clic en "Nuevo Vehículo"
3. Seleccionar cliente, llenar datos y guardar

### Crear Orden de Servicio
1. Ir a la sección Órdenes
2. Seleccionar cliente y vehículo (o crearlos con el botón +)
3. Seleccionar estado
4. Clic en "Agregar Orden"

### Calcular Costos
1. Seleccionar una orden de la tabla
2. Ingresar: Repuestos, Horas de Trabajo, Costo por Hora
3. Clic en "Calcular"
4. Los costos se guardan automáticamente

### Generar Comprobante
1. Seleccionar una orden con costos calculados
2. Clic en "Generar Comprobante"

### Ver Resumen Total
1. Clic en "Ver Resumen Total"
2. Muestra estadísticas generales e ingresos del taller

## Comandos Maven

```bash
# Compilar
./mvnw compile

# Ejecutar
./mvnw javafx:run

# Limpiar y compilar
./mvnw clean compile
```

## Solución de Problemas

### No conecta a MySQL
- Verificar que MySQL esté ejecutándose
- Revisar credenciales en database.properties
- Verificar que la base de datos taller_db exista

### No conecta a SQLite
- Verificar que el archivo taller_db.sqlite exista en la raíz del proyecto

## Tecnologías

- Java 21
- JavaFX 21
- MySQL 8.0 / SQLite
- Maven
- JDBC
