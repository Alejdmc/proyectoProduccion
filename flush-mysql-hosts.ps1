# Script para desbloquear el host en MySQL ejecutando FLUSH HOSTS
# Asegurate de tener mysqladmin en el PATH o mysql client instalado

Write-Host "=== Desbloqueando host en MySQL ===" -ForegroundColor Cyan
Write-Host ""

# Credenciales desde database.properties
$mysqlHost = "172.30.16.104"
$mysqlUser = "damonroy86"
$mysqlPassword = "67001386"

Write-Host "Intentando ejecutar FLUSH HOSTS en $mysqlHost..." -ForegroundColor Yellow
Write-Host ""

# Opcion 1: Usando mysqladmin
Write-Host "Metodo 1: Usando mysqladmin" -ForegroundColor Green
$command1 = "mysqladmin -h $mysqlHost -u $mysqlUser -p$mysqlPassword flush-hosts"
Write-Host "Ejecutando: mysqladmin -h $mysqlHost -u $mysqlUser -p[OCULTA] flush-hosts"

try {
    $result = Invoke-Expression $command1 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[OK] Host desbloqueado exitosamente!" -ForegroundColor Green
        exit 0
    } else {
        Write-Host "[ERROR] mysqladmin fallo o no esta instalado" -ForegroundColor Red
        Write-Host $result -ForegroundColor Red
    }
} catch {
    Write-Host "[ERROR] mysqladmin no esta disponible" -ForegroundColor Red
}

Write-Host ""
Write-Host "Metodo 2: Usando mysql client" -ForegroundColor Green
Write-Host "Ejecutando: mysql -h $mysqlHost -u $mysqlUser -p[OCULTA] -e 'FLUSH HOSTS;'"

# Opcion 2: Usando mysql client
$command2 = "mysql -h $mysqlHost -u $mysqlUser -p$mysqlPassword -e `"FLUSH HOSTS;`""

try {
    $result = Invoke-Expression $command2 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[OK] Host desbloqueado exitosamente!" -ForegroundColor Green
        exit 0
    } else {
        Write-Host "[ERROR] mysql client fallo" -ForegroundColor Red
        Write-Host $result -ForegroundColor Red
    }
} catch {
    Write-Host "[ERROR] mysql client no esta disponible" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== INSTRUCCIONES MANUALES ===" -ForegroundColor Yellow
Write-Host ""
Write-Host "Si los metodos automaticos no funcionaron, ejecuta manualmente:"
Write-Host ""
Write-Host "1. Abre una terminal y ejecuta:" -ForegroundColor Cyan
Write-Host "   mysql -h $mysqlHost -u $mysqlUser -p"
Write-Host ""
Write-Host "2. Cuando pida password, ingresa:" -ForegroundColor Cyan
Write-Host "   $mysqlPassword"
Write-Host ""
Write-Host "3. Una vez conectado, ejecuta:" -ForegroundColor Cyan
Write-Host "   FLUSH HOSTS;"
Write-Host ""
Write-Host "4. Sal con:" -ForegroundColor Cyan
Write-Host "   exit"
Write-Host ""

