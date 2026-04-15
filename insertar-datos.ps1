# Script para insertar datos de prueba en la base de datos
# Uso: .\insertar-datos.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Insertar Datos de Prueba - Taller" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Configurar JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"

# Preguntar al usuario
Write-Host "Este script insertará datos de prueba en la base de datos:" -ForegroundColor Yellow
Write-Host "  - 10 Clientes" -ForegroundColor White
Write-Host "  - 11 Vehículos" -ForegroundColor White
Write-Host "  - 12 Órdenes de Servicio" -ForegroundColor White
Write-Host ""

$confirmacion = Read-Host "¿Deseas continuar? (S/N)"

if ($confirmacion -ne "S" -and $confirmacion -ne "s") {
    Write-Host "Operación cancelada." -ForegroundColor Red
    exit
}

Write-Host ""
Write-Host "Compilando el proyecto..." -ForegroundColor Green
.\mvnw.cmd compile -q

if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Compilación exitosa" -ForegroundColor Green
    Write-Host ""
    Write-Host "Insertando datos..." -ForegroundColor Green
    Write-Host ""

    # Ejecutar la clase principal
    .\mvnw.cmd exec:java -Dexec.mainClass="com.proyectoproduccion.Util.InsertarDatosPrueba" -q

    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "========================================" -ForegroundColor Green
        Write-Host "  ✓ Datos insertados correctamente" -ForegroundColor Green
        Write-Host "========================================" -ForegroundColor Green
    } else {
        Write-Host ""
        Write-Host "✗ Error al insertar datos" -ForegroundColor Red
        Write-Host "Verifica que la base de datos esté configurada correctamente" -ForegroundColor Yellow
    }
} else {
    Write-Host "✗ Error en la compilación" -ForegroundColor Red
}

Write-Host ""
Write-Host "Presiona cualquier tecla para continuar..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

