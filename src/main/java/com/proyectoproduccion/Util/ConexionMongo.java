package com.proyectoproduccion.Util;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.concurrent.TimeUnit;

/**
 * Clase para gestionar la conexión a MongoDB
 */
public class ConexionMongo {
    
    private static MongoClient mongoClient = null;
    private static MongoDatabase database = null;
    
    /**
     * Obtiene la conexión a MongoDB (Singleton)
     */
    public static MongoDatabase getDatabase() {
        if (database == null) {
            conectar();
        }
        return database;
    }
    
    /**
     * Establece la conexión con MongoDB
     */
    private static void conectar() {
        try {
            String uri = ConfigDB.getMongoURI();
            String dbName = ConfigDB.getDatabase();
            
            ConnectionString connectionString = new ConnectionString(uri);
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .applyToConnectionPoolSettings(builder -> 
                        builder.maxConnectionIdleTime(60000, TimeUnit.MILLISECONDS))
                    .build();
            
            mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase(dbName);
            
            System.out.println("✓ Conexión a MongoDB establecida: " + dbName);
            
        } catch (Exception e) {
            System.err.println("✗ Error al conectar a MongoDB: " + e.getMessage());
            throw new RuntimeException("No se pudo conectar a MongoDB", e);
        }
    }
    
    /**
     * Prueba la conexión a MongoDB
     */
    public static boolean probarConexion() {
        try {
            MongoDatabase db = getDatabase();
            if (db == null) {
                return false;
            }
            
            // Intentar hacer un ping al servidor
            Document ping = new Document("ping", 1);
            db.runCommand(ping);
            
            System.out.println("✓ Conexión MongoDB exitosa");
            return true;
            
        } catch (Exception e) {
            System.err.println("✗ Error al probar conexión MongoDB: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Prueba la conexión con parámetros específicos
     */
    public static boolean probarConexion(String host, String port, String dbName) {
        try {
            String uri = "mongodb://" + host + ":" + port;
            MongoClient testClient = MongoClients.create(uri);
            MongoDatabase testDb = testClient.getDatabase(dbName);
            
            Document ping = new Document("ping", 1);
            testDb.runCommand(ping);
            
            testClient.close();
            return true;
            
        } catch (Exception e) {
            System.err.println("Error al probar conexión: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Prueba la conexión con autenticación
     */
    public static boolean probarConexion(String host, String port, String dbName, String usuario, String password) {
        try {
            String uri;
            if (usuario != null && !usuario.isEmpty() && password != null && !password.isEmpty()) {
                uri = String.format("mongodb://%s:%s@%s:%s/%s?authSource=admin", 
                    usuario, password, host, port, dbName);
            } else {
                uri = "mongodb://" + host + ":" + port + "/" + dbName;
            }
            
            MongoClient testClient = MongoClients.create(uri);
            MongoDatabase testDb = testClient.getDatabase(dbName);
            
            Document ping = new Document("ping", 1);
            testDb.runCommand(ping);
            
            testClient.close();
            return true;
            
        } catch (Exception e) {
            System.err.println("Error al probar conexión: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Cierra la conexión con MongoDB
     */
    public static void cerrarConexion() {
        if (mongoClient != null) {
            try {
                mongoClient.close();
                mongoClient = null;
                database = null;
                System.out.println("✓ Conexión MongoDB cerrada");
            } catch (Exception e) {
                System.err.println("Error al cerrar conexión MongoDB: " + e.getMessage());
            }
        }
    }
    
    /**
     * Obtiene el cliente MongoDB
     */
    public static MongoClient getClient() {
        if (mongoClient == null) {
            conectar();
        }
        return mongoClient;
    }
}

