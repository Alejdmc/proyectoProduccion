# 🎯 EJEMPLO PRÁCTICO: Auditoría con MySQL

## Escenario: Usas MySQL como base de datos principal

### PASO 1: Crear un cliente

```java
// Tu código en la aplicación:
Cliente cliente = new Cliente(0, "María García", "+57 310 555 1234", 
                             "maria@email.com", "Calle 10 #5-20");
DatabaseUtil.insertarCliente(cliente);
```

**¿Qué pasa internamente?**

1. **DatabaseUtil** detecta que estás usando MySQL
2. Ejecuta esto en **MySQL**:
   ```sql
   INSERT INTO clientes (nombre, telefono, email, direccion)
   VALUES ('María García', '+57 310 555 1234', 'maria@email.com', 'Calle 10 #5-20');
   -- Cliente guardado con ID: 25
   ```

3. **AuditoriaService** guarda esto en **MongoDB**:
   ```javascript
   db.auditoria.insertOne({
     operacion: "CREATE",
     tabla: "clientes",
     registroId: "25",
     usuario: "sistema",
     timestamp: "2026-05-20 14:30:00",
     datosNuevos: {
       id: 25,
       nombre: "María García",
       telefono: "+57 310 555 1234",
       email: "maria@email.com",
       direccion: "Calle 10 #5-20"
     }
   })
   ```

---

### PASO 2: Actualizar el cliente

```java
// Tu código en la aplicación:
cliente.setTelefono("+57 320 999 8888");
DatabaseUtil.actualizarCliente(cliente);
```

**¿Qué pasa internamente?**

1. **DatabaseUtil** primero consulta los datos actuales en **MySQL**:
   ```sql
   SELECT * FROM clientes WHERE id = 25;
   -- Obtiene: telefono = '+57 310 555 1234'
   ```

2. Luego actualiza en **MySQL**:
   ```sql
   UPDATE clientes 
   SET telefono = '+57 320 999 8888'
   WHERE id = 25;
   ```

3. **AuditoriaService** guarda en **MongoDB**:
   ```javascript
   db.auditoria.insertOne({
     operacion: "UPDATE",
     tabla: "clientes",
     registroId: "25",
     usuario: "sistema",
     timestamp: "2026-05-20 14:35:00",
     datosAnteriores: {
       id: 25,
       nombre: "María García",
       telefono: "+57 310 555 1234",  // ← VIEJO
       email: "maria@email.com",
       direccion: "Calle 10 #5-20"
     },
     datosNuevos: {
       id: 25,
       nombre: "María García",
       telefono: "+57 320 999 8888",  // ← NUEVO
       email: "maria@email.com",
       direccion: "Calle 10 #5-20"
     }
   })
   ```

---

### PASO 3: Eliminar el cliente

```java
// Tu código en la aplicación:
DatabaseUtil.eliminarCliente(25);
```

**¿Qué pasa internamente?**

1. **DatabaseUtil** primero consulta los datos en **MySQL**:
   ```sql
   SELECT * FROM clientes WHERE id = 25;
   -- Obtiene todos los datos del cliente
   ```

2. Luego elimina de **MySQL**:
   ```sql
   DELETE FROM clientes WHERE id = 25;
   -- Cliente eliminado
   ```

3. **AuditoriaService** guarda en **MongoDB**:
   ```javascript
   db.auditoria.insertOne({
     operacion: "DELETE",
     tabla: "clientes",
     registroId: "25",
     usuario: "sistema",
     timestamp: "2026-05-20 14:40:00",
     datosAnteriores: {
       id: 25,
       nombre: "María García",
       telefono: "+57 320 999 8888",
       email: "maria@email.com",
       direccion: "Calle 10 #5-20"
     }
   })
   ```

---

## 📊 RESULTADO FINAL

### En MySQL (Base de datos principal):
```sql
mysql> SELECT * FROM clientes WHERE id = 25;
Empty set (0.00 sec)
-- ✓ Cliente eliminado
```

### En MongoDB (Auditoría):
```javascript
db.auditoria.find({ registroId: "25" }).pretty()

// Resultado: 3 registros
{
  operacion: "CREATE",     // ← Cuándo se creó
  tabla: "clientes",
  registroId: "25",
  timestamp: "2026-05-20 14:30:00",
  datosNuevos: { ... }
}

{
  operacion: "UPDATE",     // ← Qué cambió
  tabla: "clientes",
  registroId: "25",
  timestamp: "2026-05-20 14:35:00",
  datosAnteriores: { telefono: "+57 310 555 1234" },
  datosNuevos: { telefono: "+57 320 999 8888" }
}

{
  operacion: "DELETE",     // ← Cuándo se eliminó (y los datos eliminados)
  tabla: "clientes",
  registroId: "25",
  timestamp: "2026-05-20 14:40:00",
  datosAnteriores: { ... todos los datos ... }
}
```

---

## 💡 VENTAJAS

✅ **Historial completo**: Puedes ver toda la vida del cliente #25  
✅ **Recuperación**: Tienes los datos del cliente eliminado  
✅ **Trazabilidad**: Sabes qué cambió y cuándo  
✅ **Separación**: MySQL tiene solo datos activos, MongoDB tiene el historial  

---

## 🔍 CONSULTAS ÚTILES

### Ver historial completo de un cliente
```javascript
db.auditoria.find({ 
  tabla: "clientes", 
  registroId: "25" 
}).sort({ timestamp: 1 }).pretty()
```

### Ver qué clientes fueron eliminados hoy
```javascript
db.auditoria.find({
  tabla: "clientes",
  operacion: "DELETE",
  timestamp: { $regex: "^2026-05-20" }
}).pretty()
```

### Recuperar datos de un cliente eliminado
```javascript
db.auditoria.findOne({
  tabla: "clientes",
  operacion: "DELETE",
  registroId: "25"
}).datosAnteriores

// Resultado:
{
  id: 25,
  nombre: "María García",
  telefono: "+57 320 999 8888",
  email: "maria@email.com",
  direccion: "Calle 10 #5-20"
}
// ¡Puedes restaurar estos datos si es necesario!
```

---

## 🎯 RESUMEN VISUAL

```
TU APLICACIÓN
      │
      ├──> insertarCliente()
      │         │
      │         ├──→ MySQL: INSERT cliente
      │         └──→ MongoDB: Auditoría CREATE
      │
      ├──> actualizarCliente()
      │         │
      │         ├──→ MySQL: UPDATE cliente
      │         └──→ MongoDB: Auditoría UPDATE (antes + después)
      │
      └──> eliminarCliente()
                │
                ├──→ MySQL: DELETE cliente
                └──→ MongoDB: Auditoría DELETE (guardar datos)

RESULTADO:
  MySQL     → Solo datos actuales
  MongoDB   → Historial completo de cambios
```

---

**¡Esto es exactamente lo que ya está implementado en tu sistema!** 🚀

Funciona igual si usas SQLite en lugar de MySQL. La auditoría SIEMPRE va a MongoDB.

