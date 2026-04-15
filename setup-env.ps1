# Script para configurar el entorno de desarrollo
# Ejecuta este script antes de usar Maven: .\setup-env.ps1

$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
Write-Host "JAVA_HOME configurado a: $env:JAVA_HOME" -ForegroundColor Green
Write-Host "Ahora puedes ejecutar: .\mvnw.cmd clean compile" -ForegroundColor Yellow

