package com.proyectoproduccion.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    // La configuración se carga automáticamente desde database.properties
    // Si no existe el archivo, usa valores por defecto

    /**
     * Obtiene una conexión a la base de datos usando la configuración
     * del archivo database.properties
     */
    public static Connection getConnection() throws SQLException {
        String url = ConfigDB.getURL();
        String user = ConfigDB.getUser();
        String password = ConfigDB.getPassword();
        
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Prueba la conexión con la configuración del archivo database.properties
     */
    public static boolean probarConexion() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Error al conectar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Prueba la conexión con credenciales específicas (para el login manual)
     */
    public static boolean probarConexion(String host, String port, String usuario, String contrasena) {
        String urlPrueba = "jdbc:mysql://" + host + ":" + port + "/" + 
                          ConfigDB.getDatabase() + "?useSSL=false&serverTimezone=UTC";
        try (Connection conn = DriverManager.getConnection(urlPrueba, usuario, contrasena)) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Obtiene la información de conexión actual (para debugging)
     */
    public static String getInfoConexion() {
        return "Host: " + ConfigDB.getHost() + ", Puerto: " + ConfigDB.getPort() + 
               ", Base de datos: " + ConfigDB.getDatabase();
    }

    /**
     * Muestra la configuración actual
     */
    public static void mostrarConfiguracion() {
        ConfigDB.mostrarConfiguracion();
    }
}
