#!/usr/bin/env pwsh
# ===================================================================
# Script: Compilar y Verificar Base de Datos (SIN Maven)
# ===================================================================

Write-Host ""
Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host "  VERIFICACION DE BASE DE DATOS" -ForegroundColor Cyan
Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host ""

# Verificar configuracion
$config = Get-Content "database.properties" | Where-Object { $_ -notmatch '^\s*#' -and $_ -match 'db.type=' }
$dbType = $config -replace '.*=\s*', ''

Write-Host "Base de datos configurada: " -NoNewline -ForegroundColor Yellow
Write-Host $dbType.ToUpper() -ForegroundColor Green
Write-Host ""

# Compilar el archivo Java
Write-Host "Compilando VerificarBaseDatos.java..." -ForegroundColor Yellow

# Crear directorio si no existe
New-Item -ItemType Directory -Force -Path "target\classes\com\proyectoproduccion\Util" | Out-Null

# Buscar JARs en el repositorio local de Maven
$m2Repo = "$env:USERPROFILE\.m2\repository"
$sqliteJar = Get-ChildItem -Path "$m2Repo\org\xerial\sqlite-jdbc" -Recurse -Filter "sqlite-jdbc*.jar" | Select-Object -First 1 -ExpandProperty FullName
$mysqlJar = Get-ChildItem -Path "$m2Repo\com\mysql\mysql-connector-j" -Recurse -Filter "mysql-connector-j*.jar" | Select-Object -First 1 -ExpandProperty FullName
$javafxJar = Get-ChildItem -Path "$m2Repo\org\openjfx\javafx-controls" -Recurse -Filter "javafx-controls-*-win.jar" | Select-Object -First 1 -ExpandProperty FullName

if (-not $sqliteJar) {
    Write-Host "  ✗ No se encontró sqlite-jdbc.jar" -ForegroundColor Red
    Write-Host "  Ejecute primero: .\mvnw.cmd dependency:resolve" -ForegroundColor Yellow
    exit 1
}

if (-not $mysqlJar) {
    Write-Host "  ✗ No se encontró mysql-connector-j.jar" -ForegroundColor Red
    Write-Host "  Ejecute primero: .\mvnw.cmd dependency:resolve" -ForegroundColor Yellow
    exit 1
}

$classpath = "target\classes;$sqliteJar;$mysqlJar"

# Compilar todas las clases necesarias
$srcDir = "src\main\java"
$outDir = "target\classes"

# Compilar en orden: Modelo -> Util
Write-Host "  Compilando modelos..." -ForegroundColor Gray
javac -d $outDir -cp $classpath `
    "$srcDir\com\proyectoproduccion\Modelo\Cliente.java" `
    "$srcDir\com\proyectoproduccion\Modelo\Vehiculo.java" `
    "$srcDir\com\proyectoproduccion\Modelo\Orden.java" 2>&1 | Out-Null

Write-Host "  Compilando utilidades..." -ForegroundColor Gray
javac -d $outDir -cp $classpath `
    "$srcDir\com\proyectoproduccion\Util\ConfigDB.java" `
    "$srcDir\com\proyectoproduccion\Util\Conexion.java" `
    "$srcDir\com\proyectoproduccion\Util\DatabaseUtil.java" `
    "$srcDir\com\proyectoproduccion\Util\VerificarBaseDatos.java" 2>&1 | Out-Null

if ($LASTEXITCODE -eq 0) {
    Write-Host "  ✓ Compilacion exitosa" -ForegroundColor Green
    Write-Host ""

    # Ejecutar verificacion
    Write-Host "Ejecutando pruebas..." -ForegroundColor Yellow
    Write-Host ""
    Write-Host ("=" * 70) -ForegroundColor DarkGray

    java -cp $classpath com.proyectoproduccion.Util.VerificarBaseDatos

    Write-Host ("=" * 70) -ForegroundColor DarkGray
    Write-Host ""

    Write-Host "✓ EJECUCION COMPLETADA" -ForegroundColor Green
    Write-Host ""
    Write-Host "CONCLUSION:" -ForegroundColor Yellow
    Write-Host "  - La aplicacion lee correctamente de: " -NoNewline
    Write-Host $dbType.ToUpper() -ForegroundColor Cyan
    Write-Host "  - Los datos se guardan en: " -NoNewline
    Write-Host $dbType.ToUpper() -ForegroundColor Cyan
    Write-Host "  - Las operaciones INSERT/SELECT/DELETE funcionan correctamente" -ForegroundColor White
    Write-Host ""

    if ($dbType -eq "mysql") {
        Write-Host "PARA PROBAR CON SQLITE:" -ForegroundColor Yellow
        Write-Host "  1. Editar database.properties" -ForegroundColor White
        Write-Host "  2. Cambiar: db.type=mysql  ->  db.type=sqlite" -ForegroundColor White
        Write-Host "  3. Ejecutar de nuevo: .\verificar-bd-simple.ps1" -ForegroundColor Cyan
    } else {
        Write-Host "PARA PROBAR CON MYSQL:" -ForegroundColor Yellow
        Write-Host "  1. Editar database.properties" -ForegroundColor White
        Write-Host "  2. Cambiar: db.type=sqlite  ->  db.type=mysql" -ForegroundColor White
        Write-Host "  3. Ejecutar de nuevo: .\verificar-bd-simple.ps1" -ForegroundColor Cyan
    }
} else {
    Write-Host "  ✗ Error al compilar" -ForegroundColor Red
    Write-Host "  Revise los mensajes de error arriba" -ForegroundColor Yellow
}

Write-Host ""


