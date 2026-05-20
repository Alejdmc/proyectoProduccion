package com.proyectoproduccion.Util;

import com.proyectoproduccion.Modelo.Cliente;

public class PruebaAuditoriaDirecta {

    public static void main(String[] args) {
        System.out.println("===============================================");
        System.out.println("PRUEBA DIRECTA DE AUDITORÍA");
        System.out.println("===============================================\n");
        
        // Verificar configuración
        ConfigDB.cargarConfiguracion();
        System.out.println("Tipo de BD principal: " + ConfigDB.getDbType());
        System.out.println("Host MongoDB: " + ConfigDB.getProperty("mongodb.host", "localhost"));
        System.out.println("Puerto MongoDB: " + ConfigDB.getProperty("mongodb.port", "27017"));
        System.out.println();
        
        // Configurar usuario de auditoría
        AuditoriaService.setUsuarioActual("prueba_directa");
        
        try {
            // PRUEBA 1: Crear un cliente
            System.out.println("--- PRUEBA 1: CREAR CLIENTE ---");
            Cliente cliente = new Cliente(0, "PRUEBA Auditoría", "+57 300 123", 
                                         "prueba@test.com", "Calle Test");
            
            Cliente creado = DatabaseUtil.insertarCliente(cliente);
            System.out.println("✓ Cliente creado con ID: " + creado.getId());
            System.out.println();
            
            // Esperar un poco
            Thread.sleep(1000);
            
            // PRUEBA 2: Actualizar el cliente
            System.out.println("--- PRUEBA 2: ACTUALIZAR CLIENTE ---");
            System.out.println("Teléfono anterior: " + creado.getTelefono());
            creado.setTelefono("+57 310 999");
            DatabaseUtil.actualizarCliente(creado);
            System.out.println("✓ Cliente actualizado");
            System.out.println("Teléfono nuevo: " + creado.getTelefono());
            System.out.println();
            
            // Esperar un poco
            Thread.sleep(1000);
            
            // PRUEBA 3: Eliminar el cliente
            System.out.println("--- PRUEBA 3: ELIMINAR CLIENTE ---");
            DatabaseUtil.eliminarCliente(creado.getId());
            System.out.println("✓ Cliente eliminado");
            System.out.println();
            
            System.out.println("===============================================");
            System.out.println("COMPLETADO");
            System.out.println("===============================================");
            System.out.println("\nAhora verifica en MongoDB Compass:");
            System.out.println("1. Conecta a: mongodb://" + ConfigDB.getProperty("mongodb.host", "localhost") + ":" + ConfigDB.getProperty("mongodb.port", "27017"));
            System.out.println("2. Base de datos: " + ConfigDB.getProperty("mongodb.name", "taller_db"));
            System.out.println("3. Colección: auditoria");
            System.out.println("4. Busca registros con usuario: 'prueba_directa'");
            System.out.println("\nDeberías ver 3 registros:");
            System.out.println("  - CREATE (cliente creado)");
            System.out.println("  - UPDATE (teléfono cambiado)");
            System.out.println("  - DELETE (cliente eliminado con todos sus datos)");
            
        } catch (Exception e) {
            System.err.println("\n✗ ERROR:");
            e.printStackTrace();
            
            System.err.println("\nPOSIBLES CAUSAS:");
            System.err.println("1. MongoDB no está corriendo");
            System.err.println("2. La configuración de database.properties está incorrecta");
            System.err.println("3. No hay conexión a la base de datos principal");
            
            System.err.println("\nSOLUCIÓN:");
            System.err.println("- Ejecuta primero: VerificarMongoConexion.java");
            System.err.println("- Verifica que database.properties tenga:");
            System.err.println("  mongodb.host=172.30.16.49");
            System.err.println("  mongodb.port=27017");
            System.err.println("  mongodb.name=taller_db");
        }
    }
}

