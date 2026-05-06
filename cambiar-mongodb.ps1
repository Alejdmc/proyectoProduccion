# ========================================
# Script para cambiar a MongoDB
# ========================================

Write-Host "Cambiando configuración a MongoDB..." -ForegroundColor Cyan

# Backup del archivo actual
if (Test-Path "database.properties") {
    Copy-Item "database.properties" "database.properties.backup" -Force
    Write-Host "✓ Backup creado: database.properties.backup" -ForegroundColor Green
}

# Crear nuevo database.properties para MongoDB
$config = @"
# Configuración para MongoDB
db.type=mongodb
db.host=localhost
db.port=27017
db.name=taller_db

# Si MongoDB requiere autenticación, configura usuario y contraseña:
db.user=
db.password=

# SQLite (no usado cuando db.type=mongodb)
db.file=taller_db.sqlite
"@

$config | Out-File -FilePath "database.properties" -Encoding UTF8

Write-Host "✓ Configuración cambiada a MongoDB" -ForegroundColor Green
Write-Host ""
Write-Host "Configuración actual:" -ForegroundColor Yellow
Write-Host "  Tipo: MongoDB" -ForegroundColor White
Write-Host "  Host: localhost" -ForegroundColor White
Write-Host "  Puerto: 27017" -ForegroundColor White
Write-Host "  Base de datos: taller_db" -ForegroundColor White
Write-Host ""
Write-Host "Para usar datos importados:" -ForegroundColor Yellow
Write-Host "  cd mongodb" -ForegroundColor White
Write-Host "  .\importar-mongodb.ps1" -ForegroundColor Cyan
Write-Host ""
Write-Host "Para volver a MySQL:" -ForegroundColor Yellow
Write-Host "  .\cambiar-mysql.ps1" -ForegroundColor White
Write-Host ""

