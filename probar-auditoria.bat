@echo off
cd /d "%~dp0"

echo ========================================
echo EJECUTAR PRUEBA DE AUDITOR

IA COMPLETA
echo ========================================
echo.

set CLASSPATH=target/classes
set CLASSPATH=%CLASSPATH%;%USERPROFILE%\.m2\repository\org\xerial\sqlite-jdbc\3.45.1.0\sqlite-jdbc-3.45.1.0.jar
set CLASSPATH=%CLASSPATH%;%USERPROFILE%\.m2\repository\org\mongodb\mongodb-driver-sync\4.11.1\mongodb-driver-sync-4.11.1.jar
set CLASSPATH=%CLASSPATH%;%USERPROFILE%\.m2\repository\org\mongodb\bson\4.11.1\bson-4.11.1.jar
set CLASSPATH=%CLASSPATH%;%USERPROFILE%\.m2\repository\org\mongodb\mongodb-driver-core\4.11.1\mongodb-driver-core-4.11.1.jar
set CLASSPATH=%CLASSPATH%;%USERPROFILE%\.m2\repository\org\slf4j\slf4j-api\2.0.9\slf4j-api-2.0.9.jar
set CLASSPATH=%CLASSPATH%;%USERPROFILE%\.m2\repository\org\slf4j\slf4j-simple\2.0.12\slf4j-simple-2.0.12.jar

echo Ejecutando prueba...
java -cp "%CLASSPATH%" com.proyectoproduccion.Util.PruebaAuditoriaDirecta

echo.
echo ========================================
echo AHORA VERIFICA EN MONGODB COMPASS
echo ========================================
echo 1. Conecta a: mongodb://172.30.16.49:27017
echo 2. Base de datos: taller_db
echo 3. Coleccion: auditoria
echo 4. Busca registros con usuario: 'prueba_directa'
echo.
pause

