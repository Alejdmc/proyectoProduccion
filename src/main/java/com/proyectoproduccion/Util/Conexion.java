package com.proyectoproduccion.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    public static Connection getConnection() throws SQLException {
        String url = ConfigDB.getURL();
        String dbType = ConfigDB.getDbType();
        
        try {
            if ("sqlite".equals(dbType)) {
                Class.forName("org.sqlite.JDBC");
                return DriverManager.getConnection(url);
            } else {
                // Cargar driver MySQL explícitamente
                Class.forName("com.mysql.cj.jdbc.Driver");
                String user = ConfigDB.getUser();
                String password = ConfigDB.getPassword();
                return DriverManager.getConnection(url, user, password);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver no encontrado: " + e.getMessage());
        }
    }

    public static boolean probarConexion() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Error al conectar: " + e.getMessage());
            return false;
        }
    }

    public static boolean probarConexion(String host, String port, String usuario, String contrasena) {
        String urlPrueba = "jdbc:mysql://" + host + ":" + port + "/" + 
                          ConfigDB.getDatabase() + "?useSSL=false&serverTimezone=UTC";
        try (Connection conn = DriverManager.getConnection(urlPrueba, usuario, contrasena)) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
