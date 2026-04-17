# CONEXION MYSQL Y SQLITE

## Sistema Dual: MySQL + SQLite

Tu aplicación ahora soporta **DOS tipos de bases de datos**:

### 1. **MySQL** (Servidor remoto o local)
- ✅ Requiere servidor MySQL activo
- ✅ Ideal para producción
- ✅ Múltiples usuarios simultáneos
- ✅ Ya lo tienes configurado en: 172.30.16.52

### 2. **SQLite** (Archivo local)
- ✅ NO requiere servidor
- ✅ Ideal para desarrollo local
- ✅ Base de datos en un archivo .sqlite
- ✅ Fácil de transportar

---

## OPCION A: Usar SQLite (Recomendado para empezar)

### Paso 1: Crear la base de datos SQLite

```powershell
.\setup-sqlite.ps1
```

Esto creará:
- `taller_db.sqlite` (archivo de base de datos)
- Todas las tablas (clientes, vehiculos, ordenes)
- Datos de ejemplo (10 clientes, 11 vehículos, 12 órdenes)

### Paso 2: Cambiar a SQLite

```powershell
.\cambiar-bd.ps1 sqlite
```

### Paso 3: Ejecutar la aplicación

```powershell
.\iniciar.ps1
```

---

## OPCION B: Usar MySQL (Ya configurado)

### Paso 1: Asegurar que tienes MySQL configurado

Tu archivo `database.properties` ya tiene:
- db.type=mysql
- db.host=172.30.16.52
- db.user=damonroy86
- db.password=67001386

### Paso 2: Verificar que las tablas existen

Ya insertaste los datos con MySQL Workbench (✓ completado)

### Paso 3: Ejecutar la aplicación

```powershell
.\iniciar.ps1
```

---

## Cambiar entre MySQL y SQLite

### Cambiar a SQLite:
```powershell
.\cambiar-bd.ps1 sqlite
```

### Cambiar a MySQL:
```powershell
.\cambiar-bd.ps1 mysql
```

---

## Archivo: database.properties

Este archivo controla qué base de datos usar. Solo necesitas cambiar una línea:

### Para MySQL:
```properties
db.type=mysql
db.host=172.30.16.52
db.port=3306
db.name=taller_db
db.user=damonroy86
db.password=67001386
```

### Para SQLite:
```properties
db.type=sqlite
db.file=taller_db.sqlite
```

---

## Ventajas de cada uno

### MySQL
| Ventaja | Descripción |
|---------|-------------|
| ✅ Producción | Ideal para aplicaciones reales |
| ✅ Multiusuario | Varios usuarios a la vez |
| ✅ Escalable | Maneja grandes volúmenes de datos |
| ❌ Requiere servidor | Necesita MySQL instalado y corriendo |

### SQLite
| Ventaja | Descripción |
|---------|-------------|
| ✅ Simple | No requiere instalación de servidor |
| ✅ Portátil | Un solo archivo .sqlite |
| ✅ Rápido | Para desarrollo local |
| ❌ Un usuario | No soporta múltiples conexiones concurrentes |

---

## Archivos creados

| Archivo | Descripción |
|---------|-------------|
| `database.properties` | Configuración activa (MySQL o SQLite) |
| `database.properties.sqlite` | Plantilla para SQLite |
| `database-sqlite.sql` | Script SQL para crear tablas en SQLite |
| `taller_db.sqlite` | Base de datos SQLite (se crea al ejecutar setup) |
| `setup-sqlite.ps1` | Script para crear SQLite con datos |
| `cambiar-bd.ps1` | Script para cambiar entre MySQL y SQLite |

---

## Migrar datos de MySQL a SQLite

Si quieres copiar los datos que tienes en MySQL a SQLite:

1. **Opción automática**: Ejecuta `setup-sqlite.ps1` (incluye datos de ejemplo)

2. **Opción manual**: 
   - Los datos ya están en MySQL
   - SQLite se crea con los mismos datos de ejemplo
   - Si modificaste datos en MySQL, necesitarías exportarlos

---

## Recomendación

### Para desarrollo local (Mac sin servidor MySQL):
```powershell
.\cambiar-bd.ps1 sqlite
.\iniciar.ps1
```

### Para trabajar con el servidor remoto:
```powershell
.\cambiar-bd.ps1 mysql
.\iniciar.ps1
```

---

## Solución de problemas

### Error: "No suitable driver found"
- **Solución**: Compila el proyecto primero
  ```powershell
  .\mvnw.cmd clean compile
  ```

### SQLite no se crea
- **Solución**: Ejecuta el setup manualmente
  ```powershell
  .\setup-sqlite.ps1
  ```

### No puedo conectar a MySQL
- **Solución**: Verifica que el servidor esté activo y las credenciales sean correctas
  ```powershell
  # Edita database.properties y verifica:
  # db.host, db.user, db.password
  ```

---

## Siguiente paso

Ahora tienes **flexibilidad total**:

1. **En Windows con MySQL remoto**: Ya funciona ✅
2. **En Mac sin MySQL**: Usa SQLite
3. **Para demos sin internet**: Usa SQLite

**¿Qué prefieres usar ahora?**
- MySQL (servidor remoto) 
- SQLite (archivo local)

