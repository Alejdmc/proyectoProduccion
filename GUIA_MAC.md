# 🍎 Guía de Configuración para Mac

## ✅ Sistema de Taller en Mac con XAMPP

Esta guía te ayudará a configurar y ejecutar el proyecto en tu Mac.

---

## 📋 Requisitos Previos

- ✅ **Java 21** (JDK)
- ✅ **XAMPP para Mac** (incluye MySQL)
- ✅ **Terminal** (incluido en Mac)

---

## 🚀 Instalación Paso a Paso

### 1️⃣ Instalar Java 21

```bash
# Verificar si ya tienes Java 21
java -version

# Si no lo tienes, descarga e instala desde:
# https://www.oracle.com/java/technologies/downloads/
```

### 2️⃣ Instalar XAMPP para Mac

```bash
# Descarga XAMPP desde:
# https://www.apachefriends.org/download.html

# Selecciona: XAMPP for macOS
# Instala en: /Applications/XAMPP
```

### 3️⃣ Configurar database.properties

**Opción A - Usar el archivo de ejemplo:**

```bash
# Desde la terminal, en la raíz del proyecto:
cp database.properties.mac database.properties

# Editar con nano:
nano database.properties

# O con TextEdit:
open -a TextEdit database.properties
```

**Opción B - Crear manualmente:**

Crea un archivo `database.properties` con:

```properties
db.host=localhost
db.port=3306
db.name=taller_db
db.user=root
db.password=
```

---

## 🔧 Iniciar MySQL en Mac

### Opción 1: Interfaz Gráfica (Más fácil)

```
1. Abre la carpeta Applications
2. Doble click en "XAMPP"
3. Click en "Manage Servers"
4. Selecciona "MySQL Database"
5. Click "Start"
6. Verifica que aparezca "Running" en verde
```

### Opción 2: Terminal (Más rápido)

```bash
# Iniciar MySQL
sudo /Applications/XAMPP/xamppfiles/xampp startmysql

# Detener MySQL
sudo /Applications/XAMPP/xamppfiles/xampp stopmysql

# Reiniciar MySQL
sudo /Applications/XAMPP/xamppfiles/xampp restartmysql
```

### Verificar que MySQL está corriendo:

```bash
# Verificar proceso
ps aux | grep mysql

# Verificar puerto 3306
lsof -i :3306
```

---

## 🗄️ Crear la Base de Datos

### Opción 1: phpMyAdmin (Recomendado)

```
1. Abre tu navegador
2. Ve a: http://localhost/phpmyadmin
3. Usuario: root
4. Contraseña: (dejar vacío)
5. Click en "Nuevo" o "New Database"
6. Nombre: taller_db
7. Cotejamiento: utf8mb4_general_ci
8. Click "Crear"
```

### Opción 2: Terminal

```bash
# Conectar a MySQL
/Applications/XAMPP/xamppfiles/bin/mysql -u root

# Crear base de datos
CREATE DATABASE taller_db;

# Verificar
SHOW DATABASES;

# Salir
exit;
```

### Importar el script SQL:

```bash
# Desde la raíz del proyecto
/Applications/XAMPP/xamppfiles/bin/mysql -u root taller_db < database.sql
```

---

## 🏃 Ejecutar la Aplicación

### Método 1: Script Automático (Recomendado) 🔥

```bash
# Dar permisos de ejecución (solo la primera vez)
chmod +x iniciar-mac.sh

# Ejecutar
./iniciar-mac.sh
```

Este script:
- ✅ Configura JAVA_HOME automáticamente
- ✅ Verifica que existe database.properties
- ✅ Verifica que MySQL está corriendo
- ✅ Ofrece iniciarlo si no está corriendo
- ✅ Ejecuta la aplicación

### Método 2: Maven Wrapper Manual

```bash
# Configurar JAVA_HOME
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

# Compilar
./mvnw clean compile

# Ejecutar
./mvnw javafx:run
```

---

## 🔍 Diagnóstico de Conexión

### Probar la conexión a MySQL:

```bash
# Configurar JAVA_HOME
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

# Ejecutar diagnóstico
./mvnw exec:java -Dexec.mainClass="com.proyectoproduccion.Util.ProbarConexion"
```

Esto probará automáticamente diferentes configuraciones y te dirá cuál funciona.

---

## 📊 Insertar Datos de Prueba

### Opción 1: Con Java

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
./mvnw exec:java -Dexec.mainClass="com.proyectoproduccion.Util.InsertarDatosPrueba"
```

### Opción 2: Con SQL

```bash
/Applications/XAMPP/xamppfiles/bin/mysql -u root taller_db < datos_ejemplo.sql
```

---

## 🛠️ Solución de Problemas

### ❌ Error: "JAVA_HOME not set"

```bash
# Verificar Java instalado
/usr/libexec/java_home -V

# Configurar JAVA_HOME
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

# Agregarlo permanentemente a .zshrc o .bash_profile:
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 21)' >> ~/.zshrc
source ~/.zshrc
```

### ❌ Error: "Can't connect to MySQL server"

```bash
# 1. Verificar que MySQL está corriendo
ps aux | grep mysql

# 2. Si no está corriendo, iniciarlo:
sudo /Applications/XAMPP/xamppfiles/xampp startmysql

# 3. Verificar el puerto:
lsof -i :3306
```

### ❌ Error: "Access Denied for user 'root'"

```bash
# 1. Verificar en phpMyAdmin:
# http://localhost/phpmyadmin

# 2. Verificar database.properties:
cat database.properties

# 3. Asegurarte de que db.password está vacío:
db.password=
```

### ❌ Error: "Unknown database 'taller_db'"

```bash
# La base de datos no existe, créala:
/Applications/XAMPP/xamppfiles/bin/mysql -u root -e "CREATE DATABASE taller_db;"

# O importa el script:
/Applications/XAMPP/xamppfiles/bin/mysql -u root taller_db < database.sql
```

### ❌ Puerto 3306 ocupado

Si ya tienes MySQL instalado:

```bash
# Detener MySQL de sistema
brew services stop mysql

# O cambiar el puerto en database.properties:
db.port=3307
```

---

## 📝 Comandos Útiles para Mac

### Gestión de MySQL (XAMPP):

```bash
# Iniciar
sudo /Applications/XAMPP/xamppfiles/xampp startmysql

# Detener
sudo /Applications/XAMPP/xamppfiles/xampp stopmysql

# Estado
ps aux | grep mysqld

# Logs
tail -f /Applications/XAMPP/xamppfiles/logs/mysql_error.log
```

### Gestión del Proyecto:

```bash
# Compilar
./mvnw clean compile

# Ejecutar
./mvnw javafx:run

# Limpiar
./mvnw clean

# Diagnosticar conexión
./mvnw exec:java -Dexec.mainClass="com.proyectoproduccion.Util.ProbarConexion"
```

### Acceso Directo a MySQL:

```bash
# Conectar a MySQL
/Applications/XAMPP/xamppfiles/bin/mysql -u root

# O con la base de datos específica
/Applications/XAMPP/xamppfiles/bin/mysql -u root taller_db
```

---

## 🔐 Configuración para Trabajo Remoto

Si trabajas desde Windows y quieres probar en Mac (o viceversa):

### En tu Mac (XAMPP):

```properties
# database.properties en Mac
db.host=localhost
db.port=3306
db.user=root
db.password=
```

### Desde Windows conectándote al Mac:

```properties
# database.properties en Windows
db.host=192.168.1.XXX  # IP de tu Mac
db.port=3306
db.user=root
db.password=
```

Para encontrar la IP de tu Mac:

```bash
# En Mac, ejecuta:
ifconfig | grep "inet " | grep -v 127.0.0.1
```

---

## 📦 Estructura de Archivos para Mac

```
proyectoProduccion/
├── database.properties          # Tu configuración (Mac)
├── database.properties.mac      # Plantilla para Mac
├── iniciar-mac.sh              # Script de inicio para Mac
├── mvnw                        # Maven wrapper (Mac/Linux)
├── pom.xml
└── src/
```

---

## ✅ Checklist de Inicio en Mac

- [ ] Java 21 instalado
- [ ] XAMPP instalado en /Applications/XAMPP
- [ ] MySQL de XAMPP iniciado
- [ ] Base de datos `taller_db` creada
- [ ] Archivo `database.properties` configurado
- [ ] Script `database.sql` importado
- [ ] Proyecto compilado sin errores
- [ ] Aplicación ejecutándose correctamente

---

## 🎯 Inicio Rápido (TL;DR)

```bash
# 1. Configurar
cp database.properties.mac database.properties
nano database.properties

# 2. Iniciar MySQL
sudo /Applications/XAMPP/xamppfiles/xampp startmysql

# 3. Crear BD
/Applications/XAMPP/xamppfiles/bin/mysql -u root -e "CREATE DATABASE taller_db;"
/Applications/XAMPP/xamppfiles/bin/mysql -u root taller_db < database.sql

# 4. Ejecutar
chmod +x iniciar-mac.sh
./iniciar-mac.sh
```

---

## 📞 Soporte

Si tienes problemas:

1. Ejecuta el diagnóstico:
   ```bash
   ./mvnw exec:java -Dexec.mainClass="com.proyectoproduccion.Util.ProbarConexion"
   ```

2. Verifica los logs:
   ```bash
   tail -f /Applications/XAMPP/xamppfiles/logs/mysql_error.log
   ```

3. Revisa la configuración:
   ```bash
   cat database.properties
   ```

---

**¡Listo para usar en Mac! 🍎**

```bash
./iniciar-mac.sh
```

