# Script para encontrar la configuración de MySQL Workbench
# Esto te ayudará a saber qué IP y puerto usar

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Configuración de MySQL Workbench" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$workbenchConfigPath = "$env:APPDATA\MySQL\Workbench\connections.xml"
$workbenchConfigPath2 = "$env:APPDATA\MySQL\Workbench\server_instances.xml"

Write-Host "Buscando configuración de MySQL Workbench..." -ForegroundColor Yellow
Write-Host ""

if (Test-Path $workbenchConfigPath) {
    Write-Host "✓ Archivo de conexiones encontrado:" -ForegroundColor Green
    Write-Host "  $workbenchConfigPath" -ForegroundColor Gray
    Write-Host ""
    Write-Host "Contenido (primeras líneas):" -ForegroundColor Cyan
    Get-Content $workbenchConfigPath -Head 50
} else {
    Write-Host "✗ No se encontró connections.xml" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "INSTRUCCIONES MANUALES:" -ForegroundColor Yellow
Write-Host ""
Write-Host "Para ver tu configuración en MySQL Workbench:" -ForegroundColor White
Write-Host "1. Abre MySQL Workbench" -ForegroundColor White
Write-Host "2. En la pantalla principal, busca tu conexión" -ForegroundColor White
Write-Host "3. Click en el ícono de 'tuerca' ⚙️ o 'Configure'" -ForegroundColor White
Write-Host "4. Anota estos datos:" -ForegroundColor White
Write-Host "   - Hostname: (ejemplo: 127.0.0.1 o 192.168.x.x)" -ForegroundColor Cyan
Write-Host "   - Port: (ejemplo: 3306)" -ForegroundColor Cyan
Write-Host "   - Username: (ejemplo: root)" -ForegroundColor Cyan
Write-Host ""

Write-Host "========================================" -ForegroundColor Green
Write-Host ""

$hostname = Read-Host "Ingresa el Hostname/IP (ejemplo: 127.0.0.1)"
$port = Read-Host "Ingresa el Puerto (presiona Enter para usar 3306)"
$username = Read-Host "Ingresa el Username (presiona Enter para usar 'root')"

if ([string]::IsNullOrWhiteSpace($port)) {
    $port = "3306"
}

if ([string]::IsNullOrWhiteSpace($username)) {
    $username = "root"
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  Configuración detectada:" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host "  Hostname: $hostname" -ForegroundColor Yellow
Write-Host "  Puerto: $port" -ForegroundColor Yellow
Write-Host "  Usuario: $username" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

Write-Host "Guardando configuración..." -ForegroundColor Cyan

# Crear archivo de configuración
$config = @"
# Configuración de conexión MySQL
# Generado automáticamente

MYSQL_HOST=$hostname
MYSQL_PORT=$port
MYSQL_USER=$username
MYSQL_URL=jdbc:mysql://${hostname}:${port}/taller_db?useSSL=false&serverTimezone=UTC
"@

$config | Out-File -FilePath "mysql-config.txt" -Encoding UTF8

Write-Host "✓ Configuración guardada en: mysql-config.txt" -ForegroundColor Green
Write-Host ""
Write-Host "Presiona cualquier tecla para continuar..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

