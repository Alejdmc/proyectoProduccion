# Script de inicio rápido con configuración automática
# Uso: .\iniciar.ps1

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "    Sistema de Taller - Inicio Rápido" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Configurar JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"

# Verificar que existe database.properties
if (!(Test-Path "database.properties")) {
    Write-Host "⚠ No se encontró database.properties" -ForegroundColor Yellow
    Write-Host ""

    if (Test-Path "database.properties.example") {
        Write-Host "Se encontró database.properties.example" -ForegroundColor Green
        $respuesta = Read-Host "¿Deseas crear database.properties desde el ejemplo? (S/N)"

        if ($respuesta -eq "S" -or $respuesta -eq "s") {
            Copy-Item "database.properties.example" "database.properties"
            Write-Host "✓ Archivo database.properties creado" -ForegroundColor Green
            Write-Host ""
            Write-Host "IMPORTANTE: Edita database.properties y configura tus credenciales" -ForegroundColor Yellow
            Write-Host ""
            $continuar = Read-Host "¿Deseas editar el archivo ahora? (S/N)"
            if ($continuar -eq "S" -or $continuar -eq "s") {
                notepad database.properties
                Write-Host ""
                Write-Host "Presiona Enter después de guardar los cambios..." -ForegroundColor Cyan
                Read-Host
            }
        }
    } else {
        Write-Host "Creando database.properties con valores por defecto..." -ForegroundColor Cyan
        @"
# Configuración de Conexión MySQL
db.host=localhost
db.port=3306
db.name=taller_db
db.user=root
db.password=
"@ | Out-File -FilePath "database.properties" -Encoding UTF8
        Write-Host "✓ Archivo creado con valores por defecto (XAMPP)" -ForegroundColor Green
    }
    Write-Host ""
}

# Mostrar configuración que se usará
Write-Host "Configuración actual:" -ForegroundColor Cyan
if (Test-Path "database.properties") {
    Get-Content "database.properties" | Where-Object { $_ -notmatch "^#" -and $_ -notmatch "^\s*$" } | ForEach-Object {
        if ($_ -match "password") {
            Write-Host "  db.password=***" -ForegroundColor Gray
        } else {
            Write-Host "  $_" -ForegroundColor Gray
        }
    }
}
Write-Host ""

# Verificar MySQL
Write-Host "Verificando MySQL..." -ForegroundColor Yellow
$mysqlProcess = Get-Process mysqld -ErrorAction SilentlyContinue

if ($mysqlProcess) {
    Write-Host "✓ MySQL está corriendo (PID: $($mysqlProcess.Id))" -ForegroundColor Green
} else {
    Write-Host "⚠ MySQL NO está corriendo" -ForegroundColor Yellow
    Write-Host ""
    $respuesta = Read-Host "¿Deseas abrir el Panel de XAMPP? (S/N)"
    if ($respuesta -eq "S" -or $respuesta -eq "s") {
        if (Test-Path "C:\xampp\xampp-control.exe") {
            Start-Process "C:\xampp\xampp-control.exe"
            Write-Host ""
            Write-Host "Panel de XAMPP abierto." -ForegroundColor Green
            Write-Host "Click 'Start' en MySQL y presiona Enter cuando esté listo..." -ForegroundColor Cyan
            Read-Host
        } else {
            Write-Host "No se encontró XAMPP. Inicia MySQL manualmente..." -ForegroundColor Yellow
            Read-Host "Presiona Enter cuando MySQL esté corriendo"
        }
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  Iniciando aplicación..." -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

# Ejecutar la aplicación
.\mvnw.cmd javafx:run

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "✗ Error al ejecutar la aplicación" -ForegroundColor Red
    Write-Host ""
    Read-Host "Presiona Enter para salir"
}

