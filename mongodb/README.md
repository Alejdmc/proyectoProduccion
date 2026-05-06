# 📦 Datos MongoDB - Sistema de Gestión de Taller Mecánico

Este directorio contiene archivos JSON listos para importar en MongoDB, con datos de ejemplo realistas para el sistema de gestión de taller mecánico.

## 📋 Contenido

### Archivos JSON

- **`clientes.json`** - 12 clientes con datos completos
- **`vehiculos.json`** - 15 vehículos con VINs, placas, marcas y modelos
- **`ordenes.json`** - 15 órdenes de servicio con estados, costos y descripciones

### Scripts de Importación

- **`importar-mongodb.ps1`** - Script para Windows PowerShell
- **`importar-mongodb.sh`** - Script para Linux/Mac (Bash)

---

## 🚀 Instalación y Uso

### Prerrequisitos

1. **MongoDB instalado y ejecutándose**
   ```bash
   # Verificar que MongoDB está activo
   mongosh --version
   ```

2. **MongoDB Database Tools instalado**
   - Descarga desde: https://www.mongodb.com/try/download/database-tools
   - Asegúrate de que `mongoimport` esté en el PATH

### Importación en Windows

```powershell
# Navega al directorio mongodb
cd C:\Users\sala7\IdeaProjects\proyectoProduccion2\mongodb

# Ejecuta el script de importación
.\importar-mongodb.ps1
```

### Importación en Linux/Mac

```bash
# Navega al directorio mongodb
cd /ruta/a/proyectoProduccion2/mongodb

# Da permisos de ejecución al script
chmod +x importar-mongodb.sh

# Ejecuta el script
./importar-mongodb.sh
```

### Importación Manual (Individual)

Si prefieres importar colección por colección:

```bash
# Importar clientes
mongoimport --db=taller_db --collection=clientes --file=clientes.json --jsonArray --drop

# Importar vehículos
mongoimport --db=taller_db --collection=vehiculos --file=vehiculos.json --jsonArray --drop

# Importar órdenes
mongoimport --db=taller_db --collection=ordenes --file=ordenes.json --jsonArray --drop
```

---

## 🔍 Verificación de Datos

### Conectarse a MongoDB

```bash
mongosh mongodb://localhost:27017/taller_db
```

### Consultas de Verificación

```javascript
// Contar documentos
db.clientes.countDocuments()   // Debe retornar 12
db.vehiculos.countDocuments()  // Debe retornar 15
db.ordenes.countDocuments()    // Debe retornar 15

// Ver todos los clientes
db.clientes.find().pretty()

// Ver vehículos de un cliente específico
db.vehiculos.find({ cliente_id: 1 })

// Ver órdenes en proceso
db.ordenes.find({ estado: "EN_PROCESO" })

// Buscar vehículo por placa
db.vehiculos.findOne({ placa: "ABC123" })

// Ver órdenes con total mayor a $500,000
db.ordenes.find({ total: { $gt: 500000 } })
```

---

## 📊 Estructura de Datos

### Colección: `clientes`

```json
{
  "_id": 1,
  "nombre": "Carlos Andrés Rodríguez",
  "telefono": "+57 300 123 4567",
  "email": "carlos.rodriguez@email.com",
  "direccion": "Calle 45 #23-56, Bogotá"
}
```

**Campos:**
- `_id` (Number) - ID único del cliente
- `nombre` (String) - Nombre completo del cliente
- `telefono` (String) - Número de teléfono
- `email` (String) - Correo electrónico
- `direccion` (String) - Dirección completa

### Colección: `vehiculos`

```json
{
  "_id": 1,
  "placa": "ABC123",
  "marca": "Toyota",
  "modelo": "Corolla",
  "anio": 2020,
  "cliente_id": 1,
  "vin": "5YFBURHE5HP123456"
}
```

**Campos:**
- `_id` (Number) - ID único del vehículo
- `placa` (String) - Placa única del vehículo
- `marca` (String) - Marca del vehículo
- `modelo` (String) - Modelo del vehículo
- `anio` (Number) - Año de fabricación
- `cliente_id` (Number) - ID del cliente propietario
- `vin` (String) - Vehicle Identification Number

### Colección: `ordenes`

```json
{
  "_id": 1,
  "cliente_id": 1,
  "vehiculo_id": 1,
  "estado": "ENTREGADO",
  "costo_repuestos": 250000,
  "horas_trabajo": 3.5,
  "costo_hora": 50000,
  "mano_obra": 175000,
  "subtotal": 425000,
  "iva": 80750,
  "total": 505750,
  "fecha_entrada": "2026-04-15T08:30:00Z",
  "fecha_salida": "2026-04-16T14:00:00Z",
  "descripcion": "Cambio de aceite, filtros y revisión general de frenos"
}
```

**Campos:**
- `_id` (Number) - ID único de la orden
- `cliente_id` (Number) - ID del cliente
- `vehiculo_id` (Number) - ID del vehículo
- `estado` (String) - Estado: "RECIBIDO", "EN_PROCESO", "ENTREGADO"
- `costo_repuestos` (Number) - Costo de repuestos en pesos
- `horas_trabajo` (Number) - Horas de trabajo
- `costo_hora` (Number) - Costo por hora de trabajo
- `mano_obra` (Number) - Total mano de obra
- `subtotal` (Number) - Subtotal antes de IVA
- `iva` (Number) - IVA (19%)
- `total` (Number) - Total a pagar
- `fecha_entrada` (Date) - Fecha de entrada del vehículo
- `fecha_salida` (Date) - Fecha de salida (null si aún está en taller)
- `descripcion` (String) - Descripción del trabajo realizado

---

## 🔗 Relaciones entre Colecciones

### Vehículos → Clientes
```javascript
// Consulta con lookup (JOIN)
db.vehiculos.aggregate([
  {
    $lookup: {
      from: "clientes",
      localField: "cliente_id",
      foreignField: "_id",
      as: "cliente_info"
    }
  }
])
```

### Órdenes → Clientes y Vehículos
```javascript
// Obtener orden completa con datos de cliente y vehículo
db.ordenes.aggregate([
  {
    $lookup: {
      from: "clientes",
      localField: "cliente_id",
      foreignField: "_id",
      as: "cliente"
    }
  },
  {
    $lookup: {
      from: "vehiculos",
      localField: "vehiculo_id",
      foreignField: "_id",
      as: "vehiculo"
    }
  },
  { $unwind: "$cliente" },
  { $unwind: "$vehiculo" }
])
```

---

## 📈 Estadísticas de los Datos

- **12 clientes** registrados
- **15 vehículos** de diferentes marcas (Toyota, Chevrolet, Mazda, Renault, etc.)
- **15 órdenes de servicio:**
  - 6 Entregadas
  - 5 En Proceso
  - 4 Recibidas
- **Rango de costos:** $202,300 - $2,201,500 COP
- **Total en facturación:** ~$10,964,150 COP

---

## 🛠️ Índices Recomendados

Para mejorar el rendimiento de las consultas:

```javascript
// Conectarse a la base de datos
use taller_db

// Crear índices
db.clientes.createIndex({ email: 1 })
db.vehiculos.createIndex({ placa: 1 }, { unique: true })
db.vehiculos.createIndex({ cliente_id: 1 })
db.ordenes.createIndex({ cliente_id: 1 })
db.ordenes.createIndex({ vehiculo_id: 1 })
db.ordenes.createIndex({ estado: 1 })
db.ordenes.createIndex({ fecha_entrada: -1 })
```

---

## 🔄 Exportar Datos

Si necesitas exportar los datos desde MongoDB:

```bash
# Exportar todas las colecciones
mongoexport --db=taller_db --collection=clientes --out=clientes_backup.json --jsonArray
mongoexport --db=taller_db --collection=vehiculos --out=vehiculos_backup.json --jsonArray
mongoexport --db=taller_db --collection=ordenes --out=ordenes_backup.json --jsonArray
```

---

## 🧹 Limpiar Base de Datos

Si necesitas eliminar todos los datos:

```javascript
// En mongosh
use taller_db
db.clientes.drop()
db.vehiculos.drop()
db.ordenes.drop()

// O eliminar la base de datos completa
db.dropDatabase()
```

---

## 📝 Notas Adicionales

- Los datos son ficticios y generados para pruebas
- Los VINs son formatos válidos pero no corresponden a vehículos reales
- Los teléfonos usan el formato colombiano (+57)
- Los precios están en pesos colombianos (COP)
- El IVA está calculado al 19% (estándar en Colombia)
- Las fechas están en formato ISO 8601 (UTC)

---

## 🆘 Solución de Problemas

### Error: "mongoimport no se reconoce"
- Instala MongoDB Database Tools
- Agrega la carpeta de instalación al PATH del sistema

### Error: "Connection refused"
- Verifica que MongoDB esté ejecutándose: `mongosh`
- Inicia MongoDB: `mongod` o usando el servicio del sistema

### Error: "duplicate key error"
- Si ya existen datos, usa `--drop` en el comando de importación
- O elimina la colección antes de importar

---

## 📧 Soporte

Para problemas o preguntas sobre la importación de datos, consulta:
- Documentación oficial de MongoDB: https://docs.mongodb.com/
- MongoDB University (cursos gratuitos): https://university.mongodb.com/

---

## ✅ Checklist de Importación

- [ ] MongoDB instalado y ejecutándose
- [ ] MongoDB Database Tools instalado
- [ ] Navegado al directorio `mongodb`
- [ ] Ejecutado script de importación
- [ ] Verificado conteo de documentos
- [ ] Probado consultas de ejemplo
- [ ] Creado índices recomendados

---

**¡Listo! Tu base de datos MongoDB está configurada con datos de ejemplo. 🎉**

