package com.proyectoproduccion.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    
    // Contador de conexiones para debug
    private static int conexionesActivas = 0;

    public static Connection getConnection() throws SQLException {
        String url = ConfigDB.getURL();
        String dbType = ConfigDB.getDbType();
        
        try {
            Connection conn = null;
            
            if ("sqlite".equals(dbType)) {
                Class.forName("org.sqlite.JDBC");
                conn = DriverManager.getConnection(url);
            } else {
                // Cargar driver MySQL explícitamente
                Class.forName("com.mysql.cj.jdbc.Driver");
                String user = ConfigDB.getUser();
                String password = ConfigDB.getPassword();
                
                // Intentar conexión con timeout
                DriverManager.setLoginTimeout(5); // 5 segundos timeout
                conn = DriverManager.getConnection(url, user, password);
            }
            
            if (conn != null) {
                conexionesActivas++;
                // Asegurar que autoCommit esté activado para evitar transacciones pendientes
                if (!conn.getAutoCommit()) {
                    conn.setAutoCommit(true);
                }
            }
            
            return conn;
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error de conexión a " + dbType + ": " + e.getMessage());
            if (e.getMessage().contains("Host") && e.getMessage().contains("blocked")) {
                System.err.println("⚠ SOLUCIÓN: Ejecuta 'FLUSH HOSTS;' en MySQL o usa el script fix-mysql-simple.ps1");
            }
            throw e;
        }
    }

    public static boolean probarConexion() {
        String dbType = ConfigDB.getDbType();
        
        if ("mongodb".equals(dbType)) {
            // Para MongoDB, usar la clase especializada
            return ConexionMongo.probarConexion();
        } else {
            // Para MySQL y SQLite
            try (Connection conn = getConnection()) {
                boolean conectado = conn != null && !conn.isClosed();
                if (conectado) {
                    System.out.println("✓ Conexión exitosa a " + dbType.toUpperCase());
                }
                return conectado;
            } catch (SQLException e) {
                System.err.println("✗ Error al conectar a " + dbType.toUpperCase() + ": " + e.getMessage());
                return false;
            }
        }
    }

    public static boolean probarConexion(String host, String port, String usuario, String contrasena) {
        String urlPrueba = "jdbc:mysql://" + host + ":" + port + "/" + 
                          ConfigDB.getDatabase() + 
                          "?useSSL=false&serverTimezone=UTC&connectTimeout=5000";
        try (Connection conn = DriverManager.getConnection(urlPrueba, usuario, contrasena)) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Error en prueba de conexión: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtiene el número de conexiones activas (aprox.)
     */
    public static int getConexionesActivas() {
        return conexionesActivas;
    }
}
