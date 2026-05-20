# 📋 Sistema de Auditoría con MongoDB

## ✅ ¿Qué hace el sistema de auditoría?

El sistema de auditoría registra **automáticamente** todas las operaciones que se realizan en la base de datos:

- ✅ **CREATE**: Cada vez que se crea un cliente, vehículo u orden
- ✅ **UPDATE**: Cada vez que se actualiza un registro (guarda datos anteriores y nuevos)
- ✅ **DELETE**: Cada vez que se elimina un registro (guarda los datos eliminados)

---

## 🎯 Características

### 1. **Registro Automático**
No necesitas hacer nada especial. Cada vez que uses:
- `DatabaseUtil.insertarCliente(cliente)`
- `DatabaseUtil.actualizarVehiculo(vehiculo)`
- `DatabaseUtil.eliminarOrden(id)`

El sistema **automáticamente** guardará un registro en MongoDB en la colección `auditoria`.

### 2. **Información Registrada**
Cada registro de auditoría contiene:
```json
{
  "operacion": "CREATE | UPDATE | DELETE",
  "tabla": "clientes | vehiculos | ordenes",
  "registroId": "123",
  "usuario": "sistema",
  "timestamp": "2026-05-20 14:30:45",
  "datosAnteriores": { /* solo en UPDATE/DELETE */ },
  "datosNuevos": { /* solo en CREATE/UPDATE */ }
}
```

### 3. **Compatible con MySQL, SQLite y MongoDB**
La auditoría funciona sin importar qué base de datos uses para los datos principales:
- Si usas **MySQL** → Los datos van a MySQL, la auditoría a MongoDB
- Si usas **SQLite** → Los datos van a SQLite, la auditoría a MongoDB
- Si usas **MongoDB** → Los datos Y la auditoría van a MongoDB

---

## 📊 Ejemplos de Registros de Auditoría

### Ejemplo CREATE (Nuevo Cliente)
```json
{
  "operacion": "CREATE",
  "tabla": "clientes",
  "registroId": "15",
  "usuario": "sistema",
  "timestamp": "2026-05-20 14:30:45",
  "datosNuevos": {
    "id": 15,
    "nombre": "Carlos Pérez",
    "telefono": "+57 310 456 7890",
    "email": "carlos.perez@email.com",
    "direccion": "Calle 45 #23-10, Bogotá"
  }
}
```

### Ejemplo UPDATE (Actualizar Vehículo)
```json
{
  "operacion": "UPDATE",
  "tabla": "vehiculos",
  "registroId": "8",
  "usuario": "sistema",
  "timestamp": "2026-05-20 15:12:30",
  "datosAnteriores": {
    "id": 8,
    "placa": "ABC123",
    "marca": "Toyota",
    "modelo": "Corolla",
    "anio": 2015,
    "clienteId": 5
  },
  "datosNuevos": {
    "id": 8,
    "placa": "ABC123",
    "marca": "Toyota",
    "modelo": "Corolla",
    "anio": 2015,
    "clienteId": 7
  }
}
```

### Ejemplo DELETE (Eliminar Orden)
```json
{
  "operacion": "DELETE",
  "tabla": "ordenes",
  "registroId": "22",
  "usuario": "sistema",
  "timestamp": "2026-05-20 16:45:10",
  "datosAnteriores": {
    "id": 22,
    "clienteId": 5,
    "vehiculoId": 8,
    "estado": "completada",
    "costoRepuestos": 150000,
    "horasTrabajo": 3,
    "costoHora": 50000,
    "manoObra": 150000,
    "subtotal": 300000,
    "iva": 57000,
    "total": 357000
  }
}
```

---

## 🔍 Consultas Útiles en MongoDB

### Ver todos los registros de auditoría
```javascript
db.auditoria.find().pretty()
```

### Ver solo las creaciones
```javascript
db.auditoria.find({ operacion: "CREATE" }).pretty()
```

### Ver cambios en clientes
```javascript
db.auditoria.find({ tabla: "clientes" }).pretty()
```

### Ver qué cambió en un registro específico
```javascript
db.auditoria.find({ 
  tabla: "vehiculos", 
  registroId: "8" 
}).pretty()
```

### Ver auditoría del día de hoy
```javascript
db.auditoria.find({ 
  timestamp: { $regex: "^2026-05-20" } 
}).pretty()
```

### Contar operaciones por tipo
```javascript
db.auditoria.aggregate([
  { $group: { 
      _id: "$operacion", 
      total: { $sum: 1 } 
  }}
])
```

### Ver últimas 10 operaciones
```javascript
db.auditoria.find().sort({ _id: -1 }).limit(10).pretty()
```

### Ver todas las eliminaciones
```javascript
db.auditoria.find({ operacion: "DELETE" }).pretty()
```

### Buscar cambios en un campo específico (ejemplo: cambios de estado)
```javascript
db.auditoria.find({
  tabla: "ordenes",
  operacion: "UPDATE",
  "datosAnteriores.estado": { $ne: null }
}).pretty()
```

---

## ⚙️ Configuración del Usuario

Por defecto, todas las operaciones se registran con el usuario `"sistema"`. 

Para cambiar el usuario actual (por ejemplo, después de login):

```java
// En tu LoginController después del login exitoso:
AuditoriaService.setUsuarioActual("juan.perez");

// Ahora todas las operaciones se registrarán con ese usuario
// hasta que cambies o cierres la aplicación
```

---

## 🛠️ Archivos Modificados

1. **`AuditoriaService.java`** (NUEVO)
   - Servicio principal de auditoría
   - Métodos: `registrarCreacion()`, `registrarActualizacion()`, `registrarEliminacion()`

2. **`AuditoriaRegistro.java`** (NUEVO)
   - Modelo para representar un registro de auditoría

3. **`DatabaseUtil.java`** (MODIFICADO)
   - Todos los métodos INSERT, UPDATE, DELETE ahora registran en auditoría
   - Agregados métodos auxiliares: `obtenerClientePorId()`, `obtenerVehiculoPorId()`, `obtenerOrdenPorId()`

---

## 🚀 Cómo Probar

### 1. **Asegúrate de tener MongoDB corriendo**
```powershell
# Verifica que MongoDB esté activo
mongosh --host 172.30.16.104 --port 27017
```

### 2. **Ejecuta tu aplicación**
- Selecciona cualquier base de datos (MySQL, SQLite o MongoDB)
- Realiza operaciones normales: crear clientes, actualizar vehículos, eliminar órdenes

### 3. **Verifica la auditoría en MongoDB**
```javascript
// Conecta a MongoDB
mongosh --host 172.30.16.104 --port 27017

// Cambia a tu base de datos
use taller_db

// Ver registros de auditoría
db.auditoria.find().pretty()

// Ver solo los últimos 5
db.auditoria.find().sort({ _id: -1 }).limit(5).pretty()
```

---

## 💡 Ventajas del Sistema

✅ **Trazabilidad Completa**: Siempre puedes saber quién hizo qué y cuándo  
✅ **Recuperación de Datos**: Los datos eliminados quedan guardados en la auditoría  
✅ **Detección de Cambios**: Puedes ver exactamente qué campos cambiaron  
✅ **Análisis de Uso**: Estadísticas sobre qué operaciones se hacen más  
✅ **Cumplimiento**: Registros para auditorías legales/empresariales  
✅ **Sin Impacto**: No afecta el rendimiento de la aplicación

---

## 🔒 Notas Importantes

1. **MongoDB debe estar funcionando** para que la auditoría funcione
2. Si MongoDB no está disponible, la auditoría simplemente no se guardará (pero tu app seguirá funcionando)
3. Los registros de auditoría **nunca se borran automáticamente** - crecerán con el tiempo
4. Puedes limpiar registros antiguos manualmente si lo necesitas:
   ```javascript
   // Eliminar auditorías anteriores a una fecha
   db.auditoria.deleteMany({ 
     timestamp: { $lt: "2026-01-01" } 
   })
   ```

---

## 📞 Uso Avanzado

### Crear índices para búsquedas más rápidas
```javascript
db.auditoria.createIndex({ operacion: 1 })
db.auditoria.createIndex({ tabla: 1 })
db.auditoria.createIndex({ registroId: 1 })
db.auditoria.createIndex({ timestamp: -1 })
```

### Exportar auditoría a JSON
```powershell
mongoexport --host 172.30.16.104 --db taller_db --collection auditoria --out auditoria_backup.json
```

### Generar reporte de cambios
```javascript
db.auditoria.aggregate([
  { $match: { operacion: "UPDATE", tabla: "ordenes" } },
  { $group: { 
      _id: "$registroId", 
      cambios: { $sum: 1 } 
  }},
  { $sort: { cambios: -1 } },
  { $limit: 10 }
])
```

---

**¡Tu sistema de auditoría está listo! 🎉**

