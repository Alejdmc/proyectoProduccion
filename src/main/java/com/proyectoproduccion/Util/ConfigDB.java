package com.proyectoproduccion.Util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Clase para cargar la configuración de la base de datos desde database.properties
 */
public class ConfigDB {

    private static Properties properties = new Properties();
    private static boolean cargado = false;

    // Valores por defecto si no se encuentra el archivo
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DATABASE = "taller_db";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";
    private static final String DEFAULT_DB_TYPE = "mysql"; // mysql o sqlite

    /**
     * Carga la configuración desde el archivo database.properties
     */
    public static void cargarConfiguracion() {
        if (cargado) {
            return; // Ya está cargado
        }

        try {
            // Intentar cargar desde el archivo en la raíz del proyecto
            InputStream input = new FileInputStream("database.properties");
            properties.load(input);
            input.close();
            cargado = true;
            System.out.println("✓ Configuración cargada desde database.properties");
        } catch (IOException e) {
            // Si no existe el archivo, intentar desde resources
            try {
                InputStream input = ConfigDB.class.getClassLoader().getResourceAsStream("database.properties");
                if (input != null) {
                    properties.load(input);
                    input.close();
                    cargado = true;
                    System.out.println("✓ Configuración cargada desde resources/database.properties");
                } else {
                    // Si tampoco está en resources, usar valores por defecto
                    cargarValoresPorDefecto();
                    System.out.println("⚠ No se encontró database.properties, usando valores por defecto");
                }
            } catch (IOException ex) {
                cargarValoresPorDefecto();
                System.out.println("⚠ Error al cargar configuración, usando valores por defecto");
            }
        }
    }

    /**
     * Carga valores por defecto si no existe el archivo
     */
    private static void cargarValoresPorDefecto() {
        properties.setProperty("db.type", DEFAULT_DB_TYPE);
        properties.setProperty("db.host", DEFAULT_HOST);
        properties.setProperty("db.port", DEFAULT_PORT);
        properties.setProperty("db.name", DEFAULT_DATABASE);
        properties.setProperty("db.user", DEFAULT_USER);
        properties.setProperty("db.password", DEFAULT_PASSWORD);
        cargado = true;
    }

    /**
     * Obtiene el host del servidor MySQL
     */
    public static String getHost() {
        cargarConfiguracion();
        return properties.getProperty("db.host", DEFAULT_HOST);
    }

    /**
     * Obtiene el puerto del servidor MySQL
     */
    public static String getPort() {
        cargarConfiguracion();
        return properties.getProperty("db.port", DEFAULT_PORT);
    }

    /**
     * Obtiene el nombre de la base de datos
     */
    public static String getDatabase() {
        cargarConfiguracion();
        return properties.getProperty("db.name", DEFAULT_DATABASE);
    }

    /**
     * Obtiene el usuario de MySQL
     */
    public static String getUser() {
        cargarConfiguracion();
        return properties.getProperty("db.user", DEFAULT_USER);
    }

    /**
     * Obtiene la contraseña de MySQL
     */
    public static String getPassword() {
        cargarConfiguracion();
        return properties.getProperty("db.password", DEFAULT_PASSWORD);
    }

    /**
     * Obtiene el tipo de base de datos (mysql o sqlite)
     */
    public static String getDbType() {
        cargarConfiguracion();
        return properties.getProperty("db.type", DEFAULT_DB_TYPE).toLowerCase();
    }

    /**
     * Obtiene la URL completa de conexión JDBC
     */
    public static String getURL() {
        cargarConfiguracion();
        String dbType = getDbType();
        
        if ("sqlite".equals(dbType)) {
            // Para SQLite, usar un archivo local
            String dbFile = properties.getProperty("db.file", "taller_db.sqlite");
            return "jdbc:sqlite:" + dbFile;
        } else {
            // Para MySQL (por defecto)
            return "jdbc:mysql://" + getHost() + ":" + getPort() + "/" + 
                   getDatabase() + "?useSSL=false&serverTimezone=UTC";
        }
    }

    /**
     * Muestra la configuración actual (sin mostrar la contraseña completa)
     */
    public static void mostrarConfiguracion() {
        cargarConfiguracion();
        System.out.println("===========================================");
        System.out.println("  Configuración de Base de Datos");
        System.out.println("===========================================");
        System.out.println("Tipo:     " + getDbType().toUpperCase());
        
        if ("sqlite".equals(getDbType())) {
            System.out.println("Archivo:  " + properties.getProperty("db.file", "taller_db.sqlite"));
        } else {
            System.out.println("Host:     " + getHost());
            System.out.println("Puerto:   " + getPort());
            System.out.println("Database: " + getDatabase());
            System.out.println("Usuario:  " + getUser());
            String pwd = getPassword();
            System.out.println("Password: " + (pwd.isEmpty() ? "(vacía)" : "***"));
        }
        System.out.println("URL:      " + getURL());
        System.out.println("===========================================");
    }
}

