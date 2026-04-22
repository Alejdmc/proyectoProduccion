#!/usr/bin/env pwsh
# ===================================================================
# Script: Cambiar Base de Datos
# Uso: .\cambiar-db.ps1 [mysql|sqlite]
# ===================================================================

param(
    [Parameter(Mandatory=$false)]
    [ValidateSet("mysql", "sqlite", "")]
    [string]$tipo = ""
)

Write-Host ""
Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host "  CAMBIAR BASE DE DATOS" -ForegroundColor Cyan
Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host ""

# Leer configuración actual
$configActual = Get-Content "database.properties" | Where-Object { $_ -match 'db.type=' }
$tipoActual = ($configActual -split '=')[1].Trim()

Write-Host "Configuración actual: " -NoNewline
Write-Host $tipoActual.ToUpper() -ForegroundColor Yellow
Write-Host ""

# Si no se especificó tipo, mostrar menú
if ($tipo -eq "") {
    Write-Host "Seleccione la base de datos:" -ForegroundColor Yellow
    Write-Host "  1. MySQL (red 172.30.16.36)"
    Write-Host "  2. SQLite (local taller_db.sqlite)"
    Write-Host "  3. Cancelar"
    Write-Host ""
    $opcion = Read-Host "Opción"

    switch ($opcion) {
        "1" { $tipo = "mysql" }
        "2" { $tipo = "sqlite" }
        default {
            Write-Host "Operación cancelada" -ForegroundColor Gray
            exit 0
        }
    }
    Write-Host ""
}

# Verificar si ya está en ese tipo
if ($tipoActual -eq $tipo) {
    Write-Host "✓ Ya estás usando $($tipo.ToUpper())" -ForegroundColor Green
    Write-Host ""
    exit 0
}

# Cambiar configuración
Write-Host "Cambiando a $($tipo.ToUpper())..." -ForegroundColor Yellow

$content = Get-Content "database.properties"
if ($tipo -eq "sqlite") {
    $content = $content -replace 'db.type=mysql','db.type=sqlite'
} else {
    $content = $content -replace 'db.type=sqlite','db.type=mysql'
}
$content | Set-Content "database.properties"

Write-Host "✓ Configuración actualizada" -ForegroundColor Green
Write-Host ""

# Verificar que el cambio se hizo
$configNueva = Get-Content "database.properties" | Where-Object { $_ -match 'db.type=' }
$tipoNuevo = ($configNueva -split '=')[1].Trim()

Write-Host "Nueva configuración: " -NoNewline
Write-Host $tipoNuevo.ToUpper() -ForegroundColor Cyan
Write-Host ""

# Mostrar información adicional
if ($tipo -eq "sqlite") {
    Write-Host "INFORMACIÓN DE SQLITE:" -ForegroundColor Yellow
    if (Test-Path "taller_db.sqlite") {
        $tamano = (Get-Item "taller_db.sqlite").Length
        Write-Host "  ✓ Archivo: taller_db.sqlite ($('{0:N0}' -f $tamano) bytes)" -ForegroundColor White
    } else {
        Write-Host "  ⚠ Archivo taller_db.sqlite NO existe" -ForegroundColor Red
        Write-Host "  Ejecuta: .\setup-sqlite.ps1" -ForegroundColor Yellow
    }
} else {
    Write-Host "INFORMACIÓN DE MYSQL:" -ForegroundColor Yellow
    $host = ($content | Where-Object { $_ -match 'db.host=' -and $_ -notmatch '^\s*#' }) -replace '.*=\s*', ''
    Write-Host "  Host: $host" -ForegroundColor White
    Write-Host "  Base de datos: taller_db" -ForegroundColor White
}

Write-Host ""
Write-Host "SIGUIENTE PASO:" -ForegroundColor Yellow
Write-Host "  Ejecutar la aplicación: " -NoNewline
Write-Host ".\iniciar.ps1" -ForegroundColor Cyan
Write-Host ""
Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host ""

