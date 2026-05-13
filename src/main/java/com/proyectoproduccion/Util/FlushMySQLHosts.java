package com.proyectoproduccion.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;
import java.io.FileInputStream;

/**
 * Utilidad para ejecutar FLUSH HOSTS en MySQL y desbloquear hosts bloqueados
 * por demasiados errores de conexión.
 */
public class FlushMySQLHosts {
    
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  Utilidad para Desbloquear Host en MySQL");
        System.out.println("=================================================");
        System.out.println();
        
        try {
            // Cargar configuración
            Properties props = new Properties();
            props.load(new FileInputStream("database.properties"));
            
            String host = props.getProperty("db.host", "localhost");
            String port = props.getProperty("db.port", "3306");
            String database = props.getProperty("db.name", "taller_db");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");
            
            System.out.println("Configuración:");
            System.out.println("  Host: " + host);
            System.out.println("  Puerto: " + port);
            System.out.println("  Usuario: " + user);
            System.out.println();
            
            // Intentar conexión directa para ejecutar FLUSH HOSTS
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database 
                       + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            
            System.out.println("Intentando conectar a MySQL...");
            
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                System.out.println("[✓] Conexión establecida exitosamente!");
                System.out.println();
                
                // Ejecutar FLUSH HOSTS
                System.out.println("Ejecutando: FLUSH HOSTS;");
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("FLUSH HOSTS");
                    System.out.println("[✓] FLUSH HOSTS ejecutado exitosamente!");
                    System.out.println();
                    System.out.println("=================================================");
                    System.out.println("  El host ha sido desbloqueado correctamente");
                    System.out.println("  Ahora puedes conectarte normalmente");
                    System.out.println("=================================================");
                }
            }
            
        } catch (java.sql.SQLException e) {
            System.err.println();
            System.err.println("=================================================");
            System.err.println("  ERROR: No se pudo conectar a MySQL");
            System.err.println("=================================================");
            System.err.println();
            System.err.println("Mensaje de error:");
            System.err.println("  " + e.getMessage());
            System.err.println();
            
            if (e.getMessage().contains("blocked")) {
                System.err.println("El host sigue bloqueado. Posibles soluciones:");
                System.err.println();
                System.err.println("1. Pide al administrador del servidor que ejecute:");
                System.err.println("   mysqladmin -h localhost flush-hosts");
                System.err.println();
                System.err.println("2. O que se conecte directamente al servidor y ejecute:");
                System.err.println("   mysql> FLUSH HOSTS;");
                System.err.println();
                System.err.println("3. O reinicie el servicio MySQL");
                System.err.println();
            } else if (e.getMessage().contains("Access denied")) {
                System.err.println("Las credenciales son incorrectas.");
                System.err.println("Verifica database.properties");
                System.err.println();
            } else {
                System.err.println("Error de conexión. Verifica que:");
                System.err.println("  - El servidor MySQL esté activo");
                System.err.println("  - La IP y puerto sean correctos");
                System.err.println("  - No haya firewall bloqueando la conexión");
                System.err.println();
            }
            
            System.exit(1);
            
        } catch (Exception e) {
            System.err.println();
            System.err.println("ERROR INESPERADO:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}

