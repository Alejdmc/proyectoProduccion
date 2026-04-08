package com.proyectoproduccion.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL = "jdbc:mysql://localhost:3306/taller_db?useSSL=false&serverTimezone=UTC";
    private static String user = "";
    private static String password = "";

    /**
     * Configura las credenciales de la base de datos.
     * Se llama desde el LoginController al autenticarse.
     */
    public static void setCredenciales(String usuario, String contrasena) {
        user = usuario;
        password = contrasena;
    }

    /**
     * Intenta conectar con las credenciales proporcionadas.
     * Lanza SQLException si las credenciales son incorrectas o la BD no está disponible.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, user, password);
    }

    /**
     * Prueba la conexión. Retorna true si las credenciales son válidas.
     */
    public static boolean probarConexion(String usuario, String contrasena) {
        try (Connection conn = DriverManager.getConnection(URL, usuario, contrasena)) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
