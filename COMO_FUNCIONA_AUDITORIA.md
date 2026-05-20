# 🎯 CÓMO FUNCIONA LA AUDITORÍA

## ✅ CONCEPTO PRINCIPAL

**Los datos van a una base de datos, la auditoría va a MongoDB**

---

## 📊 ARQUITECTURA

```
┌─────────────────────────────────────────────────────────────┐
│                    TU APLICACIÓN JAVAFX                      │
│                                                               │
│  Usuario crea/actualiza/elimina:                             │
│  - Clientes                                                   │
│  - Vehículos                                                  │
│  - Órdenes                                                    │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ↓
┌─────────────────────────────────────────────────────────────┐
│                    DatabaseUtil.java                         │
│                                                               │
│  Guarda en BD principal:                                     │
│  insertarCliente()  ───→  MySQL / SQLite / MongoDB          │
│  actualizarVehiculo() ───→  MySQL / SQLite / MongoDB        │
│  eliminarOrden()  ───→  MySQL / SQLite / MongoDB            │
│                                                               │
│  Y TAMBIÉN llama:                                            │
│  AuditoriaService.registrarXXX() ───→  MongoDB (auditoría)  │
└─────────────────────────────────────────────────────────────┘
                     │
                     ↓
         ┌──────────┴──────────┐
         │                     │
         ↓                     ↓
┌────────────────┐    ┌────────────────┐
│  BD PRINCIPAL  │    │    MONGODB     │
│                │    │   (Auditoría)  │
│  MySQL    o    │    │                │
│  SQLite   o    │    │  Colección:    │
│  MongoDB       │    │  'auditoria'   │
│                │    │                │
│  - clientes    │    │  Registra:     │
│  - vehiculos   │    │  - CREATE      │
│  - ordenes     │    │  - UPDATE      │
│                │    │  - DELETE      │
└────────────────┘    └────────────────┘
```

---

## 🔄 FLUJO DE OPERACIONES

### Ejemplo 1: CREAR CLIENTE (MySQL como BD principal)

```
1. Usuario crea cliente "Juan Pérez"
                ↓
2. DatabaseUtil.insertarCliente()
                ↓
        ┌───────┴────────┐
        │                │
        ↓                ↓
   [MySQL]        [AuditoriaService]
        │                │
        ↓                ↓
   Cliente guardado   MongoDB guarda:
   en tabla          {
   'clientes'          "operacion": "CREATE",
   ID: 15              "tabla": "clientes",
                       "registroId": "15",
                       "datosNuevos": {
                         "nombre": "Juan Pérez",
                         "telefono": "...",
                         ...
                       }
                     }
```

### Ejemplo 2: ACTUALIZAR VEHÍCULO (SQLite como BD principal)

```
1. Usuario cambia placa de "ABC123" a "XYZ789"
                ↓
2. DatabaseUtil.actualizarVehiculo()
                ↓
   a) Consulta datos anteriores del vehículo
                ↓
        ┌───────┴────────┐
        │                │
        ↓                ↓
   [SQLite]       [AuditoriaService]
        │                │
        ↓                ↓
   Vehículo            MongoDB guarda:
   actualizado         {
   en tabla              "operacion": "UPDATE",
   'vehiculos'           "tabla": "vehiculos",
   Placa: XYZ789         "registroId": "8",
                         "datosAnteriores": {
                           "placa": "ABC123"
                         },
                         "datosNuevos": {
                           "placa": "XYZ789"
                         }
                       }
```

### Ejemplo 3: ELIMINAR ORDEN (MySQL como BD principal)

```
1. Usuario elimina orden #22
                ↓
2. DatabaseUtil.eliminarOrden()
                ↓
   a) Consulta datos de la orden antes de eliminar
                ↓
        ┌───────┴────────┐
        │                │
        ↓                ↓
   [MySQL]         [AuditoriaService]
        │                │
        ↓                ↓
   Orden eliminada    MongoDB guarda:
   de tabla           {
   'ordenes'            "operacion": "DELETE",
                        "tabla": "ordenes",
                        "registroId": "22",
                        "datosAnteriores": {
                          "estado": "completada",
                          "total": 357000,
                          ...
                        }
                      }
```

---

## 💡 CASOS DE USO

### Caso 1: Trabajas con MySQL

```
Aplicación:
  ├─ Base de datos principal: MySQL (172.30.16.104)
  │  └─ Tablas: clientes, vehiculos, ordenes
  │
  └─ Auditoría: MongoDB (172.30.16.104)
     └─ Colección: auditoria
```

**Operación:**
1. Creas un cliente → Va a MySQL
2. Automáticamente → Auditoría va a MongoDB
3. Actualizas el cliente → Cambio en MySQL + Auditoría en MongoDB
4. Eliminas el cliente → Borra de MySQL + Guarda en MongoDB auditoría

### Caso 2: Trabajas con SQLite

```
Aplicación:
  ├─ Base de datos principal: SQLite (taller_db.sqlite)
  │  └─ Tablas: clientes, vehiculos, ordenes
  │
  └─ Auditoría: MongoDB (172.30.16.104)
     └─ Colección: auditoria
```

**Operación:**
1. Creas un vehículo → Va a SQLite
2. Automáticamente → Auditoría va a MongoDB
3. Actualizas el vehículo → Cambio en SQLite + Auditoría en MongoDB
4. Eliminas el vehículo → Borra de SQLite + Guarda en MongoDB auditoría

### Caso 3: Trabajas con MongoDB

```
Aplicación:
  ├─ Base de datos principal: MongoDB (172.30.16.104)
  │  └─ Colecciones: clientes, vehiculos, ordenes
  │
  └─ Auditoría: MongoDB (172.30.16.104)
     └─ Colección: auditoria (DIFERENTE colección)
```

**Operación:**
1. Creas una orden → Va a colección 'ordenes'
2. Automáticamente → Auditoría va a colección 'auditoria'
3. Actualizas la orden → Cambio en 'ordenes' + Auditoría en 'auditoria'
4. Eliminas la orden → Borra de 'ordenes' + Guarda en 'auditoria'

---

## 🔍 VERIFICACIÓN

### Ver los datos principales:

**Si usas MySQL:**
```sql
mysql> SELECT * FROM clientes;
mysql> SELECT * FROM vehiculos;
mysql> SELECT * FROM ordenes;
```

**Si usas SQLite:**
```sql
sqlite3> SELECT * FROM clientes;
sqlite3> SELECT * FROM vehiculos;
sqlite3> SELECT * FROM ordenes;
```

**Si usas MongoDB:**
```javascript
db.clientes.find().pretty()
db.vehiculos.find().pretty()
db.ordenes.find().pretty()
```

### Ver la auditoría (SIEMPRE en MongoDB):

```javascript
// Conecta a MongoDB
mongosh --host 172.30.16.104

// Cambia a la base de datos
use taller_db

// Ver auditoría
db.auditoria.find().pretty()

// Ver últimas operaciones
db.auditoria.find().sort({ _id: -1 }).limit(10).pretty()

// Ver operaciones CREATE
db.auditoria.find({ operacion: "CREATE" }).pretty()

// Ver cambios en clientes
db.auditoria.find({ tabla: "clientes" }).pretty()
```

---

## ✅ VENTAJAS DE ESTA ARQUITECTURA

### 1. **Separación de Responsabilidades**
- Datos operacionales → MySQL/SQLite (rápido, transaccional)
- Datos de auditoría → MongoDB (flexible, escalable)

### 2. **Independencia**
- Si falla MongoDB → Tu aplicación sigue funcionando
- Si necesitas limpiar MongoDB → Tus datos principales no se afectan

### 3. **Flexibilidad**
- Puedes cambiar de MySQL a SQLite → La auditoría sigue funcionando
- Puedes analizar auditoría sin tocar datos principales

### 4. **Escalabilidad**
- La auditoría crece independientemente
- MongoDB maneja millones de registros de auditoría sin problema

### 5. **Recuperación**
- Datos eliminados → Quedan en MongoDB auditoría
- Puedes "deshacer" operaciones usando la auditoría

---

## 🎯 RESUMEN

```
┌──────────────────────────────────────┐
│    TU APLICACIÓN                     │
├──────────────────────────────────────┤
│                                      │
│  CREATE/UPDATE/DELETE                │
│           │                          │
│           ├──→ MySQL/SQLite/MongoDB  │  (Datos)
│           │                          │
│           └──→ MongoDB               │  (Auditoría)
│                                      │
└──────────────────────────────────────┘

RESULTADO:
✓ Datos en la BD que elijas
✓ Auditoría SIEMPRE en MongoDB
✓ Todo automático
✓ Sin código extra
```

---

## 🚀 PARA PROBAR

1. **Ejecuta la clase de prueba:**
   ```
   ProbarAuditoria.java
   ```

2. **Usa tu aplicación normalmente:**
   - Crea clientes
   - Actualiza vehículos
   - Elimina órdenes

3. **Verifica en MongoDB:**
   ```javascript
   db.auditoria.find().pretty()
   ```

**¡Verás todos los cambios registrados!** 🎉

---

**¿Está claro? Los datos van a MySQL/SQLite, la auditoría va a MongoDB.**

