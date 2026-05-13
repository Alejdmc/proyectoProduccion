# Script para importar datos JSON a MongoDB
# Ejecutar desde la raíz del proyecto

$mongoHost = "172.30.16.104"
$mongoPort = "27017"
$dbName = "taller_db"

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "  IMPORTAR DATOS A MONGODB" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Configuración:" -ForegroundColor Yellow
Write-Host "  Host: $mongoHost"
Write-Host "  Puerto: $mongoPort"
Write-Host "  Base de datos: $dbName"
Write-Host ""

# Verificar si mongoimport está disponible
$mongoimport = Get-Command mongoimport -ErrorAction SilentlyContinue
if (-not $mongoimport) {
    Write-Host "✗ mongoimport no está instalado o no está en PATH" -ForegroundColor Red
    Write-Host ""
    Write-Host "Alternativa: usar mongosh para importar manualmente:" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "mongosh mongodb://$mongoHost`:$mongoPort/$dbName" -ForegroundColor White
    Write-Host "use $dbName" -ForegroundColor White
    Write-Host "db.clientes.insertMany(" -ForegroundColor White -NoNewline
    Write-Host "[contenido de mongodb/clientes.json]" -ForegroundColor Gray -NoNewline
    Write-Host ")" -ForegroundColor White
    Write-Host ""
    exit 1
}

# Importar clientes
Write-Host "Importando clientes..." -ForegroundColor Yellow
& mongoimport --host $mongoHost --port $mongoPort --db $dbName --collection clientes --file mongodb/clientes.json --jsonArray
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Clientes importados" -ForegroundColor Green
} else {
    Write-Host "✗ Error al importar clientes" -ForegroundColor Red
}

# Importar vehículos
Write-Host "Importando vehículos..." -ForegroundColor Yellow
& mongoimport --host $mongoHost --port $mongoPort --db $dbName --collection vehiculos --file mongodb/vehiculos.json --jsonArray
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Vehículos importados" -ForegroundColor Green
} else {
    Write-Host "✗ Error al importar vehículos" -ForegroundColor Red
}

# Importar órdenes
Write-Host "Importando órdenes..." -ForegroundColor Yellow
& mongoimport --host $mongoHost --port $mongoPort --db $dbName --collection ordenes --file mongodb/ordenes.json --jsonArray
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Órdenes importadas" -ForegroundColor Green
} else {
    Write-Host "✗ Error al importar órdenes" -ForegroundColor Red
}

Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "  ✓ IMPORTACIÓN COMPLETADA" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Para verificar los datos, ejecuta:" -ForegroundColor Yellow
Write-Host "  mvn compile exec:java -Dexec.mainClass=com.proyectoproduccion.Util.ProbarMongoDB" -ForegroundColor White
Write-Host ""

