# Script para solucionar problema de MySQL - FLUSH HOSTS
# Ejecutar cuando MySQL bloquea por demasiadas conexiones

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  FIX MYSQL - FLUSH HOSTS" -ForegroundColor Yellow
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Leer configuración
$config = Get-Content "database.properties" | Where-Object { $_ -match "^\s*db\." -and $_ -notmatch "^#" }

$host_mysql = ($config | Where-Object { $_ -match "db\.host" }) -replace ".*=", "" | ForEach-Object { $_.Trim() }
$user = ($config | Where-Object { $_ -match "db\.user" }) -replace ".*=", "" | ForEach-Object { $_.Trim() }
$password = ($config | Where-Object { $_ -match "db\.password" }) -replace ".*=", "" | ForEach-Object { $_.Trim() }

Write-Host "Host: $host_mysql" -ForegroundColor White
Write-Host "Usuario: $user" -ForegroundColor White
Write-Host ""

# Crear archivo SQL
$sqlFile = "fix-mysql-flush.sql"
"FLUSH HOSTS;" | Out-File -FilePath $sqlFile -Encoding UTF8 -NoNewline

Write-Host "OPCIÓN 1: Ejecutar con mysql client (comando)" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Gray
if ($password -eq "") {
    Write-Host "mysql -h $host_mysql -u $user -e `"FLUSH HOSTS;`"" -ForegroundColor Yellow
} else {
    Write-Host "mysql -h $host_mysql -u $user -p$password -e `"FLUSH HOSTS;`"" -ForegroundColor Yellow
}
Write-Host ""

Write-Host "OPCIÓN 2: Conectar manualmente" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Gray
Write-Host "mysql -h $host_mysql -u $user -p" -ForegroundColor Yellow
Write-Host "Luego ejecutar: FLUSH HOSTS;" -ForegroundColor Yellow
Write-Host ""

Write-Host "OPCIÓN 3: Usar archivo SQL" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Gray
Write-Host "Archivo creado: $sqlFile" -ForegroundColor White
Write-Host "Ejecutar en MySQL Workbench o tu cliente favorito" -ForegroundColor White
Write-Host ""

# Intentar ejecutar automáticamente
Write-Host "Intentando ejecutar FLUSH HOSTS automáticamente..." -ForegroundColor Cyan

# Verificar si mysql está en el PATH
$mysqlPath = Get-Command mysql -ErrorAction SilentlyContinue

if ($mysqlPath) {
    Write-Host "Cliente MySQL encontrado. Ejecutando..." -ForegroundColor Green

    try {
        if ($password -eq "") {
            $resultado = & mysql -h $host_mysql -u $user -e "FLUSH HOSTS;" 2>&1
        } else {
            $resultado = & mysql -h $host_mysql -u $user "-p$password" -e "FLUSH HOSTS;" 2>&1
        }

        if ($LASTEXITCODE -eq 0) {
            Write-Host ""
            Write-Host "[OK] FLUSH HOSTS ejecutado exitosamente!" -ForegroundColor Green
            Write-Host ""
        } else {
            Write-Host ""
            Write-Host "[ERROR] No se pudo ejecutar: $resultado" -ForegroundColor Red
            Write-Host ""
        }

    } catch {
        Write-Host ""
        Write-Host "[ERROR] Error al ejecutar: $_" -ForegroundColor Red
        Write-Host ""
        Write-Host "Por favor, ejecute manualmente:" -ForegroundColor Yellow
        Write-Host "mysql -h $host_mysql -u $user -p" -ForegroundColor White
        Write-Host "Luego: FLUSH HOSTS;" -ForegroundColor White
    }
} else {
    Write-Host "Cliente MySQL no encontrado en el PATH" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Por favor, ejecute manualmente uno de los comandos de arriba" -ForegroundColor Yellow
    Write-Host "O instale mysql client y agregue al PATH" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  SOLUCIONES ADICIONALES" -ForegroundColor Yellow
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "1. Si no puedes ejecutar FLUSH HOSTS:" -ForegroundColor White
Write-Host "   - Reinicia el servidor MySQL" -ForegroundColor Gray
Write-Host "   - O contacta al administrador del servidor" -ForegroundColor Gray
Write-Host ""
Write-Host "2. Para prevenir este problema:" -ForegroundColor White
Write-Host "   - El codigo ya usa try-with-resources" -ForegroundColor Green
Write-Host "   - Verifica que no haya loops infinitos" -ForegroundColor Gray
Write-Host "   - Revisa logs de MySQL para errores de autenticacion" -ForegroundColor Gray
Write-Host ""
Write-Host "3. Ajustar configuracion MySQL (my.cnf o my.ini):" -ForegroundColor White
Write-Host "   max_connect_errors = 1000000" -ForegroundColor Gray
Write-Host "   max_connections = 500" -ForegroundColor Gray
Write-Host ""

Write-Host "Presiona Enter para continuar..."
Read-Host


