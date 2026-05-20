package com.proyectoproduccion.Util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class VerificarMongoConexion {

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("DIAGNÓSTICO DE CONEXIÓN MONGODB");
        System.out.println("==============================================\n");

        // Leer configuración
        String host = ConfigDB.getProperty("mongodb.host", "localhost");
        String port = ConfigDB.getProperty("mongodb.port", "27017");
        String dbName = ConfigDB.getProperty("mongodb.name", "taller_db");

        System.out.println("Configuración leída de database.properties:");
        System.out.println("  Host: " + host);
        System.out.println("  Puerto: " + port);
        System.out.println("  Base de datos: " + dbName);
        System.out.println();

        String uri = "mongodb://" + host + ":" + port;
        System.out.println("URI de conexión: " + uri);
        System.out.println();

        // Intentar conectar
        System.out.println("Intentando conectar a MongoDB...");
        MongoClient mongoClient = null;

        try {
            mongoClient = MongoClients.create(uri);
            MongoDatabase database = mongoClient.getDatabase(dbName);

            System.out.println("✓ CONEXIÓN EXITOSA\n");

            // Listar colecciones
            System.out.println("Colecciones existentes:");
            for (String name : database.listCollectionNames()) {
                MongoCollection<Document> col = database.getCollection(name);
                long count = col.countDocuments();
                System.out.println("  - " + name + " (" + count + " documentos)");
            }
            System.out.println();

            // Verificar colección de auditoría
            System.out.println("Verificando colección 'auditoria':");
            MongoCollection<Document> auditoria = database.getCollection("auditoria");
            long auditoriaCount = auditoria.countDocuments();

            if (auditoriaCount == 0) {
                System.out.println("  ! La colección 'auditoria' está VACÍA");
                System.out.println("  → Esto es normal si no has usado la aplicación aún");
            } else {
                System.out.println("  ✓ Hay " + auditoriaCount + " registros de auditoría");
                System.out.println("\n  Último registro:");
                Document ultimo = auditoria.find().sort(new Document("_id", -1)).limit(1).first();
                if (ultimo != null) {
                    System.out.println("    Operación: " + ultimo.getString("operacion"));
                    System.out.println("    Tabla: " + ultimo.getString("tabla"));
                    System.out.println("    Timestamp: " + ultimo.getString("timestamp"));
                }
            }
            System.out.println();

            // Insertar registro de prueba
            System.out.println("Insertando registro de prueba...");
            Document testDoc = new Document()
                    .append("operacion", "TEST")
                    .append("tabla", "prueba")
                    .append("registroId", "0")
                    .append("usuario", "verificacion")
                    .append("timestamp", java.time.LocalDateTime.now().toString())
                    .append("descripcion", "Prueba de conexión desde VerificarMongoConexion");

            auditoria.insertOne(testDoc);
            System.out.println("✓ Registro de prueba insertado\n");

            // Verificar
            long nuevoCount = auditoria.countDocuments();
            System.out.println("Total de registros ahora: " + nuevoCount);
            System.out.println();

            System.out.println("==============================================");
            System.out.println("RESULTADO: MongoDB está FUNCIONANDO");
            System.out.println("==============================================");
            System.out.println("\nAbre MongoDB Compass y conecta a:");
            System.out.println("  mongodb://" + host + ":" + port);
            System.out.println("\nLuego ve a:");
            System.out.println("  Base de datos: " + dbName);
            System.out.println("  Colección: auditoria");
            System.out.println("\nDeberías ver el registro de prueba.");

        } catch (Exception e) {
            System.err.println("\n✗ ERROR DE CONEXIÓN:");
            System.err.println("  " + e.getMessage());
            System.err.println("\nPosibles causas:");
            System.err.println("  1. MongoDB no está corriendo en " + host + ":" + port);
            System.err.println("  2. Hay un firewall bloqueando la conexión");
            System.err.println("  3. El host o puerto están mal configurados");
            System.err.println("\nSolución:");
            System.err.println("  - Verifica que MongoDB esté corriendo");
            System.err.println("  - Prueba conectarte con MongoDB Compass primero");
            e.printStackTrace();
        } finally {
            if (mongoClient != null) {
                mongoClient.close();
            }
        }
    }
}

