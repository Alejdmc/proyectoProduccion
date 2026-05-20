package com.proyectoproduccion.Util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Clase para cargar la configuración de la base de datos desde database.properties
 */
public class ConfigDB {

    private static Properties properties = new Properties();
    private static boolean cargado = false;
    private static String tipoSeleccionado = null;

    // Valores por defecto si no se encuentra el archivo
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_MONGO_PORT = "27017";
    private static final String DEFAULT_DATABASE = "taller_db";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";
    private static final String DEFAULT_DB_TYPE = "mysql";

    public static void setDbType(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            tipoSeleccionado = null;
            return;
        }
        tipoSeleccionado = tipo.trim().toLowerCase();
    }

    public static void cargarConfiguracion() {
        if (cargado) {
            return; // Ya está cargado
        }

        try {
            // Intentar cargar desde el archivo en la raíz del proyecto
            InputStream input = new FileInputStream("database.properties");
            properties.load(input);
            input.close();
            normalizarPropiedades();
            cargado = true;
            System.out.println("✓ Configuración cargada desde database.properties");
        } catch (IOException e) {
            // Si no existe el archivo, intentar desde resources
            try {
                InputStream input = ConfigDB.class.getClassLoader().getResourceAsStream("database.properties");
                if (input != null) {
                    properties.load(input);
                    input.close();
                    normalizarPropiedades();
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
     * Evita problemas por espacios accidentales en claves/valores del properties.
     */
    private static void normalizarPropiedades() {
        Properties normalizadas = new Properties();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = String.valueOf(entry.getKey()).trim();
            String value = String.valueOf(entry.getValue()).trim();
            normalizadas.setProperty(key, value);
        }
        properties = normalizadas;
    }

    private static String getTrimmedProperty(String key, String defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? defaultValue : trimmed;
    }

    /**
     * Obtiene el host del servidor (MySQL o MongoDB)
     */
    public static String getHost() {
        cargarConfiguracion();
        String dbType = getDbType();
        if ("mongodb".equals(dbType)) {
            return getTrimmedProperty("mongodb.host",
                   getTrimmedProperty("db.host", DEFAULT_HOST));
        }
        return getTrimmedProperty("db.host", DEFAULT_HOST);
    }

    /**
     * Obtiene el puerto del servidor (MySQL o MongoDB)
     */
    public static String getPort() {
        cargarConfiguracion();
        String dbType = getDbType();
        if ("mongodb".equals(dbType)) {
            return getTrimmedProperty("mongodb.port",
                    getTrimmedProperty("db.port", DEFAULT_MONGO_PORT));
        }
        return getTrimmedProperty("db.port", DEFAULT_PORT);
    }

    /**
     * Obtiene el nombre de la base de datos
     */
    public static String getDatabase() {
        cargarConfiguracion();
        String dbType = getDbType();
        if ("mongodb".equals(dbType)) {
            return getTrimmedProperty("mongodb.name",
                   getTrimmedProperty("db.name", DEFAULT_DATABASE));
        }
        return getTrimmedProperty("db.name", DEFAULT_DATABASE);
    }

    /**
     * Obtiene el usuario (MySQL o MongoDB)
     */
    public static String getUser() {
        cargarConfiguracion();
        String dbType = getDbType();
        if ("mongodb".equals(dbType)) {
            return getTrimmedProperty("mongodb.user", "");
        }
        return getTrimmedProperty("db.user", DEFAULT_USER);
    }

    /**
     * Obtiene la contraseña (MySQL o MongoDB)
     */
    public static String getPassword() {
        cargarConfiguracion();
        String dbType = getDbType();
        if ("mongodb".equals(dbType)) {
            return getTrimmedProperty("mongodb.password", "");
        }
        return getTrimmedProperty("db.password", DEFAULT_PASSWORD);
    }

    public static String getMongoAuthSource() {
        cargarConfiguracion();
        return getTrimmedProperty("mongodb.authSource", "admin");
    }

    public static String getDbType() {
        if (tipoSeleccionado != null) {
            return tipoSeleccionado;
        }
        cargarConfiguracion();
        return getTrimmedProperty("db.type", DEFAULT_DB_TYPE).toLowerCase();
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
        } else if ("mongodb".equals(dbType)) {
            // Para MongoDB, retornar la URI de conexión
            return getMongoURI();
        } else {
            // Para MySQL (por defecto) con parámetros optimizados
            return "jdbc:mysql://" + getHost() + ":" + getPort() + "/" + 
                   getDatabase() + 
                   "?useSSL=false" +
                   "&serverTimezone=UTC" +
                   "&autoReconnect=true" +
                   "&maxReconnects=3" +
                   "&initialTimeout=2" +
                   "&useUnicode=true" +
                   "&characterEncoding=UTF-8";
        }
    }
    
    /**
     * Obtiene la URI de conexión para MongoDB
     * Siempre lee los valores de mongodb.* independientemente del tipo de BD principal
     */
    public static String getMongoURI() {
        cargarConfiguracion();
        
        // Leer directamente las propiedades de MongoDB
        String host = getTrimmedProperty("mongodb.host", 
                     getTrimmedProperty("db.host", DEFAULT_HOST));
        String port = getTrimmedProperty("mongodb.port", DEFAULT_MONGO_PORT);
        String database = getTrimmedProperty("mongodb.name", 
                         getTrimmedProperty("db.name", DEFAULT_DATABASE));
        String user = getTrimmedProperty("mongodb.user", "");
        String password = getTrimmedProperty("mongodb.password", "");
        String authSource = getTrimmedProperty("mongodb.authSource", "admin");
        
        // Si hay usuario y contraseña, incluirlos en la URI
        if (user != null && !user.isEmpty() && password != null && !password.isEmpty()) {
            return String.format("mongodb://%s:%s@%s:%s/%s?authSource=%s",
                user, password, host, port, database, authSource);
        } else {
            return String.format("mongodb://%s:%s/%s", host, port, database);
        }
    }

    /**
     * Obtiene una propiedad específica del archivo de configuración
     */
    public static String getProperty(String key, String defaultValue) {
        cargarConfiguracion();
        return properties.getProperty(key, defaultValue);
    }

    public static void mostrarConfiguracion() {
        cargarConfiguracion();
        System.out.println("Configuracion de Base de Datos");
        System.out.println("Tipo: " + getDbType().toUpperCase());

        if ("sqlite".equals(getDbType())) {
            System.out.println("Archivo: " + properties.getProperty("db.file", "taller_db.sqlite"));
        } else if ("mongodb".equals(getDbType())) {
            System.out.println("Host: " + getHost());
            System.out.println("Puerto: " + getPort());
            System.out.println("Database: " + getDatabase());
            if (getUser() != null && !getUser().isEmpty()) {
                System.out.println("Usuario: " + getUser());
            }
        } else {
            System.out.println("Host: " + getHost());
            System.out.println("Puerto: " + getPort());
            System.out.println("Database: " + getDatabase());
            System.out.println("Usuario: " + getUser());
        }
    }
}
