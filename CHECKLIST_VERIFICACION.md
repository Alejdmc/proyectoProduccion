# ✅ CHECKLIST DE VERIFICACIÓN: Auditoría con MySQL/SQLite

## 🎯 OBJETIVO
Confirmar que cuando usas MySQL o SQLite, la auditoría se guarda en MongoDB.

---

## 📋 PASOS PARA VERIFICAR

### 1. **Preparación**

- [ ] MongoDB está corriendo (host: 172.30.16.104, puerto: 27017)
- [ ] database.properties configurado correctamente
- [ ] Proyecto compila sin errores

### 2. **Prueba con MySQL**

#### A. Configurar MySQL
```properties
# En database.properties
db.type=mysql
db.host=172.30.16.104
db.port=3306
db.name=taller_db
db.user=damonroy86
db.password=67001386
```

#### B. Ejecutar aplicación
- [ ] Ejecutar Main.java
- [ ] Seleccionar "MySQL" en el diálogo de inicio
- [ ] Crear un cliente nuevo

#### C. Verificar en MySQL
```sql
-- Conecta a MySQL
mysql -h 172.30.16.104 -u damonroy86 -p

-- Ver el cliente creado
USE taller_db;
SELECT * FROM clientes ORDER BY id DESC LIMIT 1;
```

#### D. Verificar en MongoDB (AUDITORÍA)
```javascript
// Conecta a MongoDB
mongosh --host 172.30.16.104

// Ver la auditoría
use taller_db
db.auditoria.find().sort({ _id: -1 }).limit(1).pretty()

// Deberías ver algo como:
{
  operacion: "CREATE",
  tabla: "clientes",
  registroId: "XX",
  usuario: "sistema",
  timestamp: "2026-05-20 ...",
  datosNuevos: {
    nombre: "...",
    telefono: "...",
    ...
  }
}
```

✅ **SI VES EL REGISTRO EN MONGODB** → ¡FUNCIONA!

---

### 3. **Prueba con SQLite**

#### A. Configurar SQLite
```properties
# En database.properties
db.type=sqlite
```

#### B. Ejecutar aplicación
- [ ] Ejecutar Main.java
- [ ] Seleccionar "SQLite" en el diálogo de inicio
- [ ] Actualizar un vehículo existente

#### C. Verificar en SQLite
```bash
sqlite3 taller_db.sqlite
SELECT * FROM vehiculos ORDER BY id;
```

#### D. Verificar en MongoDB (AUDITORÍA)
```javascript
mongosh --host 172.30.16.104
use taller_db

// Ver última actualización
db.auditoria.find({ operacion: "UPDATE" }).sort({ _id: -1 }).limit(1).pretty()

// Deberías ver:
{
  operacion: "UPDATE",
  tabla: "vehiculos",
  registroId: "XX",
  datosAnteriores: { ... },  // ← Datos viejos
  datosNuevos: { ... }       // ← Datos nuevos
}
```

✅ **SI VES EL REGISTRO CON datosAnteriores Y datosNuevos** → ¡FUNCIONA!

---

### 4. **Prueba eliminación**

#### A. Eliminar una orden
- [ ] En la aplicación, eliminar una orden de servicio

#### B. Verificar eliminación en MySQL/SQLite
```sql
-- MySQL
SELECT * FROM ordenes WHERE id = XX;
-- Resultado: Empty set (eliminada)

-- SQLite
SELECT * FROM ordenes WHERE id = XX;
-- Resultado: (sin resultados)
```

#### C. Verificar en MongoDB (AUDITORÍA)
```javascript
mongosh --host 172.30.16.104
use taller_db

// Ver última eliminación
db.auditoria.find({ operacion: "DELETE" }).sort({ _id: -1 }).limit(1).pretty()

// Deberías ver:
{
  operacion: "DELETE",
  tabla: "ordenes",
  registroId: "XX",
  datosAnteriores: {  // ← ¡Los datos eliminados están aquí!
    estado: "...",
    total: ...,
    ...
  }
}
```

✅ **SI VES LOS DATOS ELIMINADOS EN datosAnteriores** → ¡FUNCIONA!

---

## 🚀 PRUEBA RÁPIDA CON SCRIPT

### Opción 1: Ejecutar ProbarAuditoria.java

```bash
# En IntelliJ IDEA:
1. Abre: src/main/java/com/proyectoproduccion/Util/ProbarAuditoria.java
2. Click derecho → Run 'ProbarAuditoria.main()'
3. Verás en la consola todas las operaciones
4. Luego verifica en MongoDB
```

### Opción 2: Usar la aplicación normal

```bash
1. Ejecuta Main.java
2. Selecciona MySQL o SQLite
3. Crea/actualiza/elimina registros
4. Ve a MongoDB y ejecuta:
   db.auditoria.find().sort({ _id: -1 }).limit(10).pretty()
```

---

## 🔍 CONSULTAS DE VERIFICACIÓN EN MongoDB

### Ver todas las operaciones de auditoría
```javascript
db.auditoria.countDocuments()
// Debería ser > 0
```

### Ver operaciones por tipo
```javascript
db.auditoria.aggregate([
  { $group: { 
      _id: "$operacion", 
      total: { $sum: 1 } 
  }}
])

// Resultado esperado:
// { _id: "CREATE", total: X }
// { _id: "UPDATE", total: Y }
// { _id: "DELETE", total: Z }
```

### Ver últimas 5 operaciones
```javascript
db.auditoria.find()
  .sort({ _id: -1 })
  .limit(5)
  .forEach(doc => {
    print(`${doc.timestamp} - ${doc.operacion} en ${doc.tabla} #${doc.registroId}`)
  })

// Ejemplo de salida:
// 2026-05-20 14:30:00 - CREATE en clientes #15
// 2026-05-20 14:25:00 - UPDATE en vehiculos #8
// 2026-05-20 14:20:00 - DELETE en ordenes #22
```

### Verificar que funciona con MySQL y SQLite
```javascript
// Buscar diferentes fuentes
db.auditoria.distinct("tabla")

// Debería ver:
// [ "clientes", "vehiculos", "ordenes" ]
```

---

## ✅ CONFIRMACIÓN EXITOSA

Si verificaste todos los puntos anteriores y:

- ✅ Los datos se guardan en MySQL o SQLite
- ✅ La auditoría se guarda en MongoDB
- ✅ Las operaciones CREATE aparecen en MongoDB
- ✅ Las operaciones UPDATE muestran datosAnteriores y datosNuevos
- ✅ Las operaciones DELETE muestran los datos eliminados

**¡Tu sistema de auditoría funciona perfectamente!** 🎉

---

## ❌ Si algo no funciona

### Problema: No aparecen registros en MongoDB

**Posibles causas:**
1. MongoDB no está corriendo
2. database.properties no tiene la configuración de MongoDB
3. Error de conexión a MongoDB

**Solución:**
```bash
# 1. Verificar que MongoDB está corriendo
mongosh --host 172.30.16.104

# 2. Verificar database.properties
mongodb.host=172.30.16.104
mongodb.port=27017
mongodb.name=taller_db

# 3. Ver errores en la consola de la aplicación
# Busca mensajes como: "Error MongoDB auditoría: ..."
```

### Problema: La aplicación no funciona

**Solución:**
```
La auditoría está diseñada para NO detener la aplicación.
Si MongoDB falla, verás mensajes en consola pero la aplicación sigue.
Tu código SIEMPRE funciona, con o sin auditoría.
```

---

## 📞 COMANDOS ÚTILES

### Ver todo en MongoDB
```javascript
use taller_db
db.getCollectionNames()
// Debería incluir: ["auditoria", "clientes", "vehiculos", "ordenes"]

db.auditoria.find().pretty()
```

### Limpiar auditoría (si quieres empezar de cero)
```javascript
// ¡CUIDADO! Esto borra todos los registros de auditoría
// db.auditoria.deleteMany({})
```

### Exportar auditoría
```bash
mongoexport --host 172.30.16.104 \
  --db taller_db \
  --collection auditoria \
  --out auditoria_backup.json
```

---

## 🎯 RESUMEN

```
┌─────────────────────────────────────┐
│ VERIFICACIÓN COMPLETA               │
├─────────────────────────────────────┤
│                                     │
│ 1. [✓] Datos en MySQL/SQLite        │
│ 2. [✓] Auditoría en MongoDB         │
│ 3. [✓] CREATE registrado            │
│ 4. [✓] UPDATE con antes/después     │
│ 5. [✓] DELETE con datos guardados   │
│                                     │
│ ¡SISTEMA FUNCIONANDO!               │
└─────────────────────────────────────┘
```

---

**¿Todo funciona? ¡Perfecto! Tu sistema de auditoría dual está completamente operativo.** 🚀

