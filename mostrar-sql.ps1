# =====================================================
# Script para ejecutar database.sql en MySQL
# Metodo simple para MySQL Workbench
# =====================================================

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " INSTRUCCIONES PARA MYSQL WORKBENCH" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Ya estas conectado a MySQL Workbench. Sigue estos pasos:" -ForegroundColor Yellow
Write-Host ""
Write-Host "1. En MySQL Workbench, abre una nueva pestaña de Query" -ForegroundColor White
Write-Host "   (File > New Query Tab o Ctrl+T)" -ForegroundColor Gray
Write-Host ""
Write-Host "2. Copia y pega el siguiente SQL completo:" -ForegroundColor White
Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "COPIAR DESDE AQUI" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

Get-Content "database.sql"

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "COPIAR HASTA AQUI" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "3. Haz clic en el boton Execute (icono de rayo)" -ForegroundColor White
Write-Host ""
Write-Host "4. Verifica que se crearon las tablas ejecutando:" -ForegroundColor White
Write-Host "   SHOW TABLES;" -ForegroundColor Cyan
Write-Host ""
Write-Host "Deberas ver:" -ForegroundColor Yellow
Write-Host "  - clientes" -ForegroundColor White
Write-Host "  - vehiculos" -ForegroundColor White
Write-Host "  - ordenes" -ForegroundColor White
Write-Host ""

