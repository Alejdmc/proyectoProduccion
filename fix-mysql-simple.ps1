# Script simple para solucionar MySQL FLUSH HOSTS
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  SOLUCION MySQL FLUSH HOSTS" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Leer configuracion
$config = Get-Content "database.properties"
$host_mysql = "172.30.16.104"
$user = "damonroy86"
$password = "67001386"

Write-Host "Configuracion detectada:" -ForegroundColor White
Write-Host "  Host: $host_mysql" -ForegroundColor Gray
Write-Host "  Usuario: $user" -ForegroundColor Gray
Write-Host ""

# Crear archivo SQL
$sqlContent = "FLUSH HOSTS;"
Set-Content -Path "fix-flush.sql" -Value $sqlContent -NoNewline

Write-Host "Archivo SQL creado: fix-flush.sql" -ForegroundColor Green
Write-Host ""

Write-Host "INSTRUCCIONES:" -ForegroundColor Yellow
Write-Host ""
Write-Host "1. SI TIENES MYSQL CLIENT EN EL PATH:" -ForegroundColor Cyan
Write-Host "   Ejecuta este comando:" -ForegroundColor White
Write-Host "   mysql -h $host_mysql -u $user -p$password -e `"FLUSH HOSTS;`"" -ForegroundColor Green
Write-Host ""

Write-Host "2. SI USAS MYSQL WORKBENCH:" -ForegroundColor Cyan
Write-Host "   a) Abre MySQL Workbench" -ForegroundColor White
Write-Host "   b) Conectate al servidor: $host_mysql" -ForegroundColor White
Write-Host "   c) Abre una nueva query" -ForegroundColor White
Write-Host "   d) Ejecuta: FLUSH HOSTS;" -ForegroundColor Green
Write-Host ""

Write-Host "3. DESDE PHPMYADMIN o SIMILAR:" -ForegroundColor Cyan
Write-Host "   Ejecuta en la consola SQL: FLUSH HOSTS;" -ForegroundColor Green
Write-Host ""

Write-Host "4. SI TIENES ACCESO SSH AL SERVIDOR:" -ForegroundColor Cyan
Write-Host "   Conectate y ejecuta:" -ForegroundColor White
Write-Host "   mysql -u root -p -e `"FLUSH HOSTS;`"" -ForegroundColor Green
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "PREVENCION FUTURA:" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Este problema ocurre por:" -ForegroundColor White
Write-Host "  - Demasiados intentos de conexion fallidos" -ForegroundColor Gray
Write-Host "  - Credenciales incorrectas repetidas" -ForegroundColor Gray
Write-Host "  - Conexiones no cerradas correctamente" -ForegroundColor Gray
Write-Host ""
Write-Host "El codigo ya esta optimizado con:" -ForegroundColor Green
Write-Host "  - try-with-resources para cerrar conexiones" -ForegroundColor Gray
Write-Host "  - Autocommit habilitado" -ForegroundColor Gray
Write-Host ""
Write-Host "Para evitar este problema en el futuro:" -ForegroundColor White
Write-Host "  1. Verifica credenciales antes de conectar" -ForegroundColor Gray
Write-Host "  2. No ejecutes loops que conecten muchas veces" -ForegroundColor Gray
Write-Host "  3. Pide al admin aumentar max_connect_errors" -ForegroundColor Gray
Write-Host ""

Write-Host "Presiona Enter para salir..."
$null = Read-Host

