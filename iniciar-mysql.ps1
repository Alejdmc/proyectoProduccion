# Script para iniciar MySQL de XAMPP
# Uso: .\iniciar-mysql.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "       Iniciar MySQL (XAMPP)" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$xamppPath = "C:\xampp"
$mysqlPath = "$xamppPath\mysql\bin\mysqld.exe"
$xamppControl = "$xamppPath\xampp-control.exe"

# Verificar que XAMPP existe
if (!(Test-Path $mysqlPath)) {
    Write-Host "✗ Error: No se encontró MySQL de XAMPP en $xamppPath" -ForegroundColor Red
    Write-Host ""
    Write-Host "Por favor, verifica que XAMPP esté instalado correctamente." -ForegroundColor Yellow
    pause
    exit
}

Write-Host "✓ XAMPP detectado en: $xamppPath" -ForegroundColor Green
Write-Host ""

# Verificar si MySQL ya está corriendo
$mysqlProcess = Get-Process mysqld -ErrorAction SilentlyContinue

if ($mysqlProcess) {
    Write-Host "✓ MySQL ya está corriendo!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Proceso ID: $($mysqlProcess.Id)" -ForegroundColor Gray
    Write-Host ""
} else {
    Write-Host "Iniciando MySQL de XAMPP..." -ForegroundColor Yellow
    Write-Host ""

    # Intentar abrir el panel de control de XAMPP
    if (Test-Path $xamppControl) {
        Write-Host "Abriendo Panel de Control de XAMPP..." -ForegroundColor Cyan
        Start-Process $xamppControl
        Write-Host ""
        Write-Host "========================================" -ForegroundColor Yellow
        Write-Host "  ACCIÓN REQUERIDA" -ForegroundColor Yellow
        Write-Host "========================================" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "En el panel de XAMPP que se abrió:" -ForegroundColor White
        Write-Host "1. Busca la fila 'MySQL'" -ForegroundColor White
        Write-Host "2. Click en el botón 'Start'" -ForegroundColor White
        Write-Host "3. Espera a que el botón cambie a 'Stop' (fondo verde)" -ForegroundColor White
        Write-Host ""
    } else {
        Write-Host "No se encontró el panel de control de XAMPP." -ForegroundColor Yellow
        Write-Host ""
        Write-Host "Inicia MySQL manualmente:" -ForegroundColor White
        Write-Host "1. Abre el Panel de Control de XAMPP desde el menú de inicio" -ForegroundColor White
        Write-Host "2. Click en 'Start' junto a MySQL" -ForegroundColor White
        Write-Host ""
    }
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Verificación de Conexión" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$respuesta = Read-Host "¿Deseas probar la conexión ahora? (S/N)"

if ($respuesta -eq "S" -or $respuesta -eq "s") {
    Write-Host ""
    Write-Host "Esperando 3 segundos para que MySQL termine de iniciar..." -ForegroundColor Gray
    Start-Sleep -Seconds 3

    Write-Host ""
    Write-Host "Probando conexión..." -ForegroundColor Cyan
    Write-Host ""

    $env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
    .\mvnw.cmd compile exec:java "-Dexec.mainClass=com.proyectoproduccion.Util.ProbarConexion"
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  Información Importante" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Credenciales de MySQL en XAMPP:" -ForegroundColor White
Write-Host "  Usuario: root" -ForegroundColor Yellow
Write-Host "  Contraseña: (vacía/en blanco)" -ForegroundColor Yellow
Write-Host ""
Write-Host "URL de phpMyAdmin:" -ForegroundColor White
Write-Host "  http://localhost/phpmyadmin" -ForegroundColor Cyan
Write-Host ""
Write-Host "Presiona cualquier tecla para continuar..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

