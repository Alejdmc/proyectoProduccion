@echo off
echo ========================================
echo RECARGAR DEPENDENCIAS Y EJECUTAR PRUEBA
echo ========================================
echo.

cd /d "%~dp0"

echo 1. Limpiando proyecto...
call mvnw.cmd clean

echo.
echo 2. Compilando proyecto...
call mvnw.cmd compile

echo.
echo 3. Ejecutando prueba de configuracion...
call mvnw.cmd exec:java -Dexec.mainClass="com.proyectoproduccion.Util.MostrarConfiguracionMongo"

echo.
echo ========================================
echo COMPLETADO
echo ========================================
pause

