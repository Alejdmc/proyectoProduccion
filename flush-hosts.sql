-- =================================================
-- DESBLOQUEAR HOST EN MYSQL
-- =================================================
--
-- Este script desbloquea hosts que fueron bloqueados
-- por demasiados errores de conexión.
--
-- INSTRUCCIONES:
-- 1. Conéctate al servidor MySQL desde una máquina
--    diferente o directamente en el servidor
-- 2. Abre este archivo en MySQL Workbench
-- 3. Ejecuta el script (Ctrl+Shift+Enter)
-- =================================================

-- Desbloquear todos los hosts bloqueados
FLUSH HOSTS;

-- Verificar configuración actual
SELECT @@max_connect_errors AS 'Límite actual de errores';

-- (OPCIONAL) Aumentar el límite de errores permitidos
-- Descomenta la siguiente línea si quieres aumentarlo:
-- SET GLOBAL max_connect_errors = 1000000;

-- Confirmar que se ejecutó correctamente
SELECT 'Host desbloqueado exitosamente!' AS 'Estado';

