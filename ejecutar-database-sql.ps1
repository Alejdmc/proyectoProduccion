# =====================================================
# Script final: Ejecutar database.sql usando Java
# =====================================================

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Ejecutando database.sql" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Configurar JAVA_HOME si no esta configurado
if (-not $env:JAVA_HOME) {
    if (Test-Path "C:\Program Files\Java\jdk-21") {
        $env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
        Write-Host "JAVA_HOME configurado: $env:JAVA_HOME" -ForegroundColor Green
    }
}

# Compilar y ejecutar usando Maven
Write-Host "Compilando proyecto..." -ForegroundColor Yellow
.\mvnw.cmd clean compile

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "ERROR al compilar" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Ejecutando EjecutarDatabaseSQL..." -ForegroundColor Yellow
Write-Host ""

.\mvnw.cmd exec:java -Dexec.mainClass="com.proyectoproduccion.Util.EjecutarDatabaseSQL"

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "Hubo un error al ejecutar." -ForegroundColor Red
    Write-Host ""
    Write-Host "ALTERNATIVA: Usa MySQL Workbench" -ForegroundColor Yellow
    Write-Host "1. Abre MySQL Workbench y conectate" -ForegroundColor White
    Write-Host "2. Crea una nueva pestaña de Query (Ctrl+T)" -ForegroundColor White
    Write-Host "3. Ejecuta este comando SQL:" -ForegroundColor White
    Write-Host ""
    Get-Content "database.sql"
}

