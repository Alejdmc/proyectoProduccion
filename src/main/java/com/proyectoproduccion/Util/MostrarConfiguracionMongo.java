package com.proyectoproduccion.Util;

public class MostrarConfiguracionMongo {
    
    public static void main(String[] args) {
        System.out.println("===============================================");
        System.out.println("CONFIGURACIÓN ACTUAL DE MONGODB");
        System.out.println("===============================================\n");
        
        ConfigDB.cargarConfiguracion();
        
        System.out.println("Archivo: database.properties\n");
        
        System.out.println("BASE DE DATOS PRINCIPAL:");
        System.out.println("  Tipo: " + ConfigDB.getDbType());
        System.out.println();
        
        System.out.println("CONFIGURACIÓN MONGODB (para auditoría):");
        String mongoHost = ConfigDB.getProperty("mongodb.host", "NO CONFIGURADO");
        String mongoPort = ConfigDB.getProperty("mongodb.port", "NO CONFIGURADO");
        String mongoName = ConfigDB.getProperty("mongodb.name", "NO CONFIGURADO");
        String mongoUser = ConfigDB.getProperty("mongodb.user", "");
        
        System.out.println("  mongodb.host = " + mongoHost);
        System.out.println("  mongodb.port = " + mongoPort);
        System.out.println("  mongodb.name = " + mongoName);
        
        if (mongoUser != null && !mongoUser.isEmpty()) {
            System.out.println("  mongodb.user = " + mongoUser);
            System.out.println("  mongodb.password = ****");
        } else {
            System.out.println("  (Sin autenticación)");
        }
        
        System.out.println();
        System.out.println("URI COMPLETA:");
        System.out.println("  " + ConfigDB.getMongoURI().replaceAll(":[^:/@]+@", ":****@"));
        System.out.println();
        
        System.out.println("CONECTAR EN MONGODB COMPASS:");
        System.out.println("  mongodb://" + mongoHost + ":" + mongoPort);
        System.out.println();
        
        System.out.println("===============================================");
        System.out.println("PASOS PARA VERIFICAR:");
        System.out.println("===============================================");
        System.out.println("1. Abre MongoDB Compass");
        System.out.println("2. Nueva conexión: mongodb://" + mongoHost + ":" + mongoPort);
        System.out.println("3. Click en 'Connect'");
        System.out.println("4. Busca la base de datos: " + mongoName);
        System.out.println("5. Busca la colección: auditoria");
        System.out.println();
        System.out.println("Si NO puedes conectar:");
        System.out.println("  - Verifica que MongoDB esté corriendo");
        System.out.println("  - Verifica el host: " + mongoHost);
        System.out.println("  - Verifica el puerto: " + mongoPort);
        System.out.println("  - Prueba desde línea de comandos:");
        System.out.println("    mongosh --host " + mongoHost + " --port " + mongoPort);
        System.out.println();
        
        System.out.println("===============================================");
        System.out.println("SIGUIENTE PASO:");
        System.out.println("===============================================");
        System.out.println("Ejecuta: VerificarMongoConexion.java");
        System.out.println("         (para probar la conexión desde Java)");
        System.out.println();
    }
}

