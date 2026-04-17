# =====================================================
# Script para cambiar entre MySQL y SQLite
# =====================================================

param(
    [Parameter(Mandatory=$true)]
    [ValidateSet('mysql', 'sqlite')]
    [string]$tipo
)

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Cambiando a $($tipo.ToUpper())" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

if ($tipo -eq "mysql") {
    # Guardar copia de seguridad si existe
    if (Test-Path "database.properties") {
        Copy-Item "database.properties" "database.properties.backup" -Force
        Write-Host "Copia de seguridad creada: database.properties.backup" -ForegroundColor Gray
    }

    # Verificar que existe el archivo de MySQL
    if (Test-Path "database.properties.example") {
        Copy-Item "database.properties.example" "database.properties" -Force
        Write-Host "Configuracion MySQL cargada desde database.properties.example" -ForegroundColor Green
    } else {
        Write-Host "ERROR: No se encuentra database.properties.example" -ForegroundColor Red
        Write-Host "Edita database.properties manualmente y establece db.type=mysql" -ForegroundColor Yellow
        exit 1
    }

    Write-Host ""
    Write-Host "MySQL configurado!" -ForegroundColor Green
    Write-Host "Edita database.properties con tus credenciales MySQL" -ForegroundColor Yellow

} elseif ($tipo -eq "sqlite") {
    # Guardar copia de seguridad si existe
    if (Test-Path "database.properties") {
        Copy-Item "database.properties" "database.properties.backup" -Force
        Write-Host "Copia de seguridad creada: database.properties.backup" -ForegroundColor Gray
    }

    # Verificar que existe el archivo de SQLite
    if (Test-Path "database.properties.sqlite") {
        Copy-Item "database.properties.sqlite" "database.properties" -Force
        Write-Host "Configuracion SQLite cargada" -ForegroundColor Green
    } else {
        Write-Host "ERROR: No se encuentra database.properties.sqlite" -ForegroundColor Red
        exit 1
    }

    # Verificar si existe la base de datos SQLite
    if (-not (Test-Path "taller_db.sqlite")) {
        Write-Host ""
        Write-Host "Base de datos SQLite no encontrada." -ForegroundColor Yellow
        Write-Host "Ejecutando setup-sqlite.ps1..." -ForegroundColor Yellow
        Write-Host ""
        .\setup-sqlite.ps1
    } else {
        Write-Host ""
        Write-Host "SQLite configurado!" -ForegroundColor Green
        Write-Host "Usando base de datos: taller_db.sqlite" -ForegroundColor White
    }
}

Write-Host ""
Write-Host "Base de datos activa: $($tipo.ToUpper())" -ForegroundColor Cyan
Write-Host ""

