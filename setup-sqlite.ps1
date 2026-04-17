# =====================================================
# Script para configurar SQLite
# =====================================================

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Configurando SQLite" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Configurar JAVA_HOME si no esta configurado
if (-not $env:JAVA_HOME) {
    if (Test-Path "C:\Program Files\Java\jdk-21") {
        $env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
    }
}

# Compilar el proyecto
Write-Host "Compilando proyecto..." -ForegroundColor Yellow
.\mvnw.cmd clean compile

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "ERROR al compilar" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Ejecutando SQLiteSetup..." -ForegroundColor Yellow
Write-Host ""

# Ejecutar SQLiteSetup usando Maven
$mainClass = "com.proyectoproduccion.Util.SQLiteSetup"
.\mvnw.cmd exec:java "-Dexec.mainClass=$mainClass"

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "  SQLite configurado exitosamente!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Siguiente paso:" -ForegroundColor Yellow
    Write-Host "Copia el archivo database.properties.sqlite a database.properties" -ForegroundColor White
    Write-Host ""
    Write-Host "Comando:" -ForegroundColor Cyan
    Write-Host "  Copy-Item database.properties.sqlite database.properties -Force" -ForegroundColor White
    Write-Host ""
}

