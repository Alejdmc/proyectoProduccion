# ========================================
# Script para cambiar a MySQL
# ========================================

Write-Host "Cambiando configuración a MySQL..." -ForegroundColor Cyan

# Backup del archivo actual
if (Test-Path "database.properties") {
    Copy-Item "database.properties" "database.properties.backup" -Force
    Write-Host "✓ Backup creado: database.properties.backup" -ForegroundColor Green
}

# Crear nuevo database.properties para MySQL
$config = @"
# Configuración para MySQL
db.type=mysql

# MySQL localhost (XAMPP)
db.host=localhost
db.port=3306
db.name=taller_db
db.user=root
db.password=

# SQLite (no usado cuando db.type=mysql)
db.file=taller_db.sqlite
"@

$config | Out-File -FilePath "database.properties" -Encoding UTF8

Write-Host "✓ Configuración cambiada a MySQL" -ForegroundColor Green
Write-Host ""
Write-Host "Configuración actual:" -ForegroundColor Yellow
Write-Host "  Tipo: MySQL" -ForegroundColor White
Write-Host "  Host: localhost" -ForegroundColor White
Write-Host "  Puerto: 3306" -ForegroundColor White
Write-Host "  Base de datos: taller_db" -ForegroundColor White
Write-Host "  Usuario: root" -ForegroundColor White
Write-Host ""
Write-Host "Para volver a MongoDB:" -ForegroundColor Yellow
Write-Host "  .\cambiar-mongodb.ps1" -ForegroundColor White
Write-Host ""

