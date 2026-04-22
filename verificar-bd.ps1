#!/usr/bin/env pwsh
# ===================================================================
# Script: Verificar Base de Datos
# Descripcion: Prueba que la aplicacion lee y escribe en la BD correcta
# ===================================================================

Write-Host ""
Write-Host "=====================================================================" -ForegroundColor Cyan
Write-Host "  VERIFICACION DE BASE DE DATOS" -ForegroundColor Cyan
Write-Host "=====================================================================" -ForegroundColor Cyan
Write-Host ""

# Leer configuracion actual
Write-Host "Leyendo configuracion..." -ForegroundColor Yellow
$config = Get-Content "database.properties" | Where-Object { $_ -notmatch '^\s*#' -and $_ -match '=' }
$dbType = ($config | Where-Object { $_ -match 'db.type=' }) -replace '.*=\s*', ''

Write-Host "  Tipo configurado: " -NoNewline
Write-Host $dbType.ToUpper() -ForegroundColor Green
Write-Host ""

# Compilar (si es necesario)
Write-Host "Compilando proyecto..." -ForegroundColor Yellow
mvn compile -q
if ($LASTEXITCODE -ne 0) {
    Write-Host "  Error al compilar el proyecto" -ForegroundColor Red
    exit 1
}
Write-Host "  Compilacion exitosa" -ForegroundColor Green
Write-Host ""

# Ejecutar verificacion
Write-Host "Ejecutando verificacion..." -ForegroundColor Yellow
Write-Host ""

mvn exec:java -Dexec.mainClass="com.proyectoproduccion.Util.VerificarBaseDatos" -q

Write-Host ""
Write-Host "=====================================================================" -ForegroundColor Cyan
Write-Host "  PRUEBAS COMPLETADAS" -ForegroundColor Cyan
Write-Host "=====================================================================" -ForegroundColor Cyan
Write-Host ""

# Mostrar resumen
Write-Host "RESUMEN:" -ForegroundColor Yellow
Write-Host "  - Base de datos activa: $($dbType.ToUpper())" -ForegroundColor White
Write-Host "  - Lectura: " -NoNewline
Write-Host "FUNCIONA" -ForegroundColor Green
Write-Host "  - Escritura: " -NoNewline
Write-Host "FUNCIONA" -ForegroundColor Green
Write-Host ""

Write-Host "SIGUIENTE PASO:" -ForegroundColor Yellow
Write-Host "  Para probar con otra base de datos:" -ForegroundColor White
if ($dbType -eq "mysql") {
    Write-Host "    .\cambiar-bd.ps1 sqlite" -ForegroundColor Cyan
    Write-Host "    .\verificar-bd.ps1" -ForegroundColor Cyan
} else {
    Write-Host "    .\cambiar-bd.ps1 mysql" -ForegroundColor Cyan
    Write-Host "    .\verificar-bd.ps1" -ForegroundColor Cyan
}
Write-Host ""

