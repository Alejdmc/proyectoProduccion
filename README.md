# 🚗 Sistema de Taller - Gestión de Órdenes de Servicio

Sistema de escritorio para gestión de talleres mecánicos desarrollado en JavaFX.

**Compatible con Windows y Mac** 🪟 🍎

## ✨ Características

- 🚗 **Gestión de Vehículos** - Registro y control de vehículos
- 👥 **Gestión de Clientes** - Base de datos de clientes
- 📋 **Órdenes de Servicio** - Seguimiento completo de reparaciones
- 💰 **Cálculo Automático** - Presupuestos con IVA incluido
- 📊 **Reportes** - Estadísticas y análisis de negocio
- 🔐 **Configuración Automática** - Conexión sin login manual
- 🌍 **Multi-plataforma** - Funciona en Windows y Mac

---

## 🚀 Inicio Rápido

### 1. Requisitos

- ✅ **Java 21 o superior** (JDK)
- ✅ **Maven** (incluido con el proyecto)
- ✅ **MySQL 8.0+** (XAMPP recomendado)
- ✅ **MySQL Workbench** (opcional, para gestión de BD)

### 2. Configuración (Primera vez)

#### 🪟 Para Windows

```powershell
# 1. Configurar credenciales
copy database.properties.example database.properties
notepad database.properties

# 2. Iniciar XAMPP → Start MySQL

# 3. Ejecutar
.\iniciar.ps1
```

**📖 Guía detallada:** Ver `README.md` sección Windows

#### 🍎 Para Mac

```bash
# 1. Configurar credenciales
cp database.properties.mac database.properties
nano database.properties

# 2. Iniciar MySQL
sudo /Applications/XAMPP/xamppfiles/xampp startmysql

# 3. Ejecutar
chmod +x iniciar-mac.sh
./iniciar-mac.sh
```

**📖 Guía completa para Mac:** Ver `GUIA_MAC.md`

---

## 📁 Estructura del Proyecto

```
proyectoProduccion/
├── database.properties          # ⚙️ Tu configuración (no en Git)
├── database.properties.example  # 📄 Plantilla Windows
├── database.properties.mac      # 📄 Plantilla Mac
├── database.sql                 # 🗄️ Script de creación de BD
├── datos_ejemplo.sql            # 📊 Datos de prueba
├── pom.xml                      # 📦 Configuración Maven
├── iniciar.ps1                  # 🚀 Script Windows
├── iniciar-mac.sh              # 🚀 Script Mac
├── GUIA_MAC.md                 # 📖 Guía completa para Mac
│
├── src/main/java/com/proyectoproduccion/
│   ├── Main.java                # 🎯 Clase principal
│   │
│   ├── Controlador/             # 🎮 Controladores JavaFX
│   │   ├── ClientesController.java
│   │   ├── VehiculosController.java
│   │   ├── OrdenesController.java
│   │   ├── ReportesController.java
│   │   ├── LayoutController.java
│   │   └── LoginController.java
│   │
│   ├── Modelo/                  # 📦 Modelos de datos
│   │   ├── Cliente.java
│   │   ├── Vehiculo.java
│   │   └── Orden.java
│   │
│   └── Util/                    # 🔧 Utilidades
│       ├── ConfigDB.java        # Lectura de configuración
│       ├── Conexion.java        # Conexión a MySQL
│       ├── DatabaseUtil.java    # Operaciones CRUD
│       ├── SceneManager.java    # Gestión de vistas
│       ├── ProbarConexion.java  # Diagnóstico
│       └── InsertarDatosPrueba.java
│
└── src/main/resources/com/proyectoproduccion/
    ├── login.fxml               # 🖼️ Pantalla de login
    ├── layout.fxml              # 🖼️ Layout principal
    ├── clientes.fxml            # 🖼️ Vista de clientes
    ├── vehiculos.fxml           # 🖼️ Vista de vehículos
    ├── ordenesServicio.fxml     # 🖼️ Vista de órdenes
    └── reportes.fxml            # 🖼️ Vista de reportes
```

---

## 🔧 Configuración Avanzada

### Diferentes Ambientes

#### Desarrollo Local (XAMPP)
```properties
db.host=localhost
db.port=3306
db.user=root
db.password=
```

#### Servidor de Pruebas
```properties
db.host=192.168.1.100
db.port=3306
db.user=taller_test
db.password=test123
```

#### Producción
```properties
db.host=servidor.empresa.com
db.port=3306
db.user=taller_prod
db.password=P@ssw0rd_S3gur0
```

---

## 📊 Base de Datos

### Tablas Principales

#### `clientes`
- id (PK, AUTO_INCREMENT)
- nombre (VARCHAR)
- telefono (VARCHAR)
- email (VARCHAR)
- direccion (VARCHAR)

#### `vehiculos`
- id (PK, AUTO_INCREMENT)
- placa (UNIQUE)
- marca (VARCHAR)
- modelo (VARCHAR)
- anio (INT)
- cliente_id (FK → clientes)

#### `ordenes`
- id (PK, AUTO_INCREMENT)
- cliente_id (FK → clientes)
- vehiculo_id (FK → vehiculos)
- estado (ENUM: RECIBIDO, EN_PROCESO, ENTREGADO)
- costo_repuestos (DECIMAL)
- horas_trabajo (DECIMAL)
- costo_hora (DECIMAL)
- mano_obra (DECIMAL)
- subtotal (DECIMAL)
- iva (DECIMAL)
- total (DECIMAL)

### Insertar Datos de Prueba

**PowerShell:**
```powershell
.\insertar-datos.ps1
```

**O con Maven:**
```powershell
.\mvnw.cmd exec:java -Dexec.mainClass="com.proyectoproduccion.Util.InsertarDatosPrueba"
```

**O con SQL:**
```sql
source datos_ejemplo.sql;
```

---

## 🛠️ Scripts Útiles

### Windows (PowerShell)

| Script | Descripción |
|--------|-------------|
| `.\iniciar.ps1` | Inicia la aplicación con verificaciones |
| `.\insertar-datos.ps1` | Inserta datos de prueba |
| `.\iniciar-mysql.ps1` | Inicia MySQL de XAMPP |
| `.\detectar-mysql-config.ps1` | Detecta configuración de Workbench |

### Comandos Maven

```powershell
# Compilar
.\mvnw.cmd compile

# Limpiar y compilar
.\mvnw.cmd clean compile

# Ejecutar aplicación
.\mvnw.cmd javafx:run

# Ejecutar diagnóstico
.\mvnw.cmd exec:java -Dexec.mainClass="com.proyectoproduccion.Util.ProbarConexion"

# Insertar datos de prueba
.\mvnw.cmd exec:java -Dexec.mainClass="com.proyectoproduccion.Util.InsertarDatosPrueba"
```

---

## 🐛 Solución de Problemas

### Error: "No se pudo conectar a la base de datos"

**Causa**: MySQL no está corriendo

**Solución**:
1. Abre Panel de Control de XAMPP
2. Click "Start" en MySQL
3. Vuelve a ejecutar la aplicación

### Error: "Access Denied"

**Causa**: Credenciales incorrectas en `database.properties`

**Solución**:
1. Edita `database.properties`
2. Usa las mismas credenciales de MySQL Workbench
3. Guarda y vuelve a ejecutar

### Error: "Unknown database 'taller_db'"

**Causa**: La base de datos no existe

**Solución**:
```sql
-- En MySQL Workbench:
source database.sql;
```

### Error: "JAVA_HOME not found"

**Causa**: Variable de entorno no configurada

**Solución**:
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
```

O ejecuta: `.\iniciar.ps1` (lo configura automáticamente)

---

## 📚 Documentación Adicional

- [CONFIGURACION_AUTOMATICA.md](CONFIGURACION_AUTOMATICA.md) - Guía completa del sistema de configuración
- [INSERTAR_DATOS.md](INSERTAR_DATOS.md) - Cómo insertar datos en las tablas
- [CONEXION_IP_LOCAL.md](CONEXION_IP_LOCAL.md) - Conexión con IP local
- [SOLUCIONAR_MYSQL.md](SOLUCIONAR_MYSQL.md) - Solución de problemas MySQL

---

## 🔒 Seguridad

### Proteger Credenciales

El archivo `database.properties` está en `.gitignore` para no subir credenciales a Git.

**Para compartir el proyecto:**
1. Comparte `database.properties.example`
2. NO compartas `database.properties`
3. Cada desarrollador crea su propio `database.properties`

---

## 🎯 Características del Sistema

### Módulo de Clientes
- ✅ Agregar, editar, eliminar clientes
- ✅ Búsqueda en tiempo real
- ✅ Validación de datos
- ✅ Integridad referencial

### Módulo de Vehículos
- ✅ Registro de vehículos por cliente
- ✅ Placa única
- ✅ Historial de vehículos
- ✅ Filtrado por cliente

### Módulo de Órdenes
- ✅ Estados: RECIBIDO, EN_PROCESO, ENTREGADO
- ✅ Cálculo automático de costos
- ✅ IVA del 19% automático
- ✅ Mano de obra = horas × costo/hora
- ✅ Total = subtotal + IVA

### Módulo de Reportes
- ✅ Estadísticas de órdenes
- ✅ Análisis por estado
- ✅ Totales de ventas
- ✅ Reportes visuales

---

## 🚀 Próximas Funcionalidades

- [ ] Reportes en PDF
- [ ] Gráficos estadísticos
- [ ] Sistema de usuarios
- [ ] Respaldo automático de BD
- [ ] Exportar a Excel
- [ ] Envío de correos a clientes

---

## 👨‍💻 Desarrollo

### Tecnologías Utilizadas

- **Java 21** - Lenguaje de programación
- **JavaFX 21** - Framework de interfaz gráfica
- **MySQL 8.0** - Base de datos
- **Maven** - Gestión de dependencias
- **JDBC** - Conexión a base de datos

### Dependencias (pom.xml)

```xml
<dependencies>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>21.0.6</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>21.0.6</version>
    </dependency>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <version>8.3.0</version>
    </dependency>
</dependencies>
```

---

## 📝 Licencia

Este proyecto es parte de un trabajo académico.

---

## 📞 Soporte

Si tienes problemas:
1. Revisa la documentación en la carpeta del proyecto
2. Ejecuta el diagnóstico: `.\mvnw.cmd exec:java -Dexec.mainClass="com.proyectoproduccion.Util.ProbarConexion"`
3. Verifica que MySQL esté corriendo
4. Revisa `database.properties`

---

## ✅ Checklist de Inicio

- [ ] Java 21 instalado
- [ ] MySQL instalado (XAMPP)
- [ ] MySQL corriendo
- [ ] Base de datos `taller_db` creada
- [ ] Archivo `database.properties` configurado
- [ ] Aplicación compilada sin errores
- [ ] Conexión exitosa a la base de datos

---

**¡Listo para usar! 🎉**

```powershell
.\iniciar.ps1
```
