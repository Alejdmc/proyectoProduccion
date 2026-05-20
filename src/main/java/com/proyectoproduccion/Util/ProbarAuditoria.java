package com.proyectoproduccion.Util;

import com.proyectoproduccion.Modelo.Cliente;
import com.proyectoproduccion.Modelo.Vehiculo;
import com.proyectoproduccion.Modelo.Orden;

/**
 * Clase de prueba para demostrar el sistema de auditoría
 * 
 * FUNCIONAMIENTO:
 * - Los datos principales (clientes, vehículos, órdenes) se guardan en MySQL/SQLite/MongoDB
 * - La auditoría SIEMPRE se guarda en MongoDB (colección 'auditoria')
 * 
 * PRUEBA:
 * 1. Ejecuta esta clase
 * 2. Ve a MongoDB y ejecuta: db.auditoria.find().pretty()
 * 3. Verás todos los registros de auditoría
 */
public class ProbarAuditoria {

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("PRUEBA DE SISTEMA DE AUDITORÍA");
        System.out.println("==============================================");
        System.out.println();
        
        // Configurar usuario de auditoría (opcional)
        AuditoriaService.setUsuarioActual("prueba_sistema");
        
        String dbType = ConfigDB.getDbType();
        System.out.println("📊 Base de datos PRINCIPAL: " + dbType.toUpperCase());
        System.out.println("📋 Auditoría se guardará en: MONGODB");
        System.out.println();
        
        // ===========================================
        // PRUEBA 1: CREAR CLIENTE
        // ===========================================
        System.out.println("--- PRUEBA 1: CREAR CLIENTE ---");
        Cliente nuevoCliente = new Cliente(0, "TEST Cliente Auditoría", 
                                          "+57 300 1234567", 
                                          "test@auditoria.com", 
                                          "Calle Test #123");
        
        Cliente clienteCreado = DatabaseUtil.insertarCliente(nuevoCliente);
        System.out.println("✓ Cliente creado con ID: " + clienteCreado.getId());
        System.out.println("  → Datos guardados en: " + dbType.toUpperCase());
        System.out.println("  → Auditoría guardada en: MONGODB (operacion=CREATE)");
        System.out.println();
        
        // ===========================================
        // PRUEBA 2: ACTUALIZAR CLIENTE
        // ===========================================
        System.out.println("--- PRUEBA 2: ACTUALIZAR CLIENTE ---");
        System.out.println("Datos anteriores: " + clienteCreado.getTelefono());
        
        clienteCreado.setTelefono("+57 310 9876543");
        clienteCreado.setEmail("nuevo@email.com");
        DatabaseUtil.actualizarCliente(clienteCreado);
        
        System.out.println("✓ Cliente actualizado (ID: " + clienteCreado.getId() + ")");
        System.out.println("  → Nuevos datos: " + clienteCreado.getTelefono());
        System.out.println("  → Actualización en: " + dbType.toUpperCase());
        System.out.println("  → Auditoría en MONGODB con:");
        System.out.println("     - datosAnteriores (teléfono viejo)");
        System.out.println("     - datosNuevos (teléfono nuevo)");
        System.out.println();
        
        // ===========================================
        // PRUEBA 3: CREAR Y ELIMINAR VEHÍCULO
        // ===========================================
        System.out.println("--- PRUEBA 3: CREAR Y ELIMINAR VEHÍCULO ---");
        Vehiculo vehiculo = new Vehiculo(0, "TEST999", "Toyota", "Corolla", 2020, clienteCreado.getId());
        Vehiculo vehiculoCreado = DatabaseUtil.insertarVehiculo(vehiculo);
        
        System.out.println("✓ Vehículo creado con ID: " + vehiculoCreado.getId());
        System.out.println("  → Datos en: " + dbType.toUpperCase());
        System.out.println("  → Auditoría CREATE en: MONGODB");
        System.out.println();
        
        System.out.println("Eliminando vehículo...");
        DatabaseUtil.eliminarVehiculo(vehiculoCreado.getId());
        System.out.println("✓ Vehículo eliminado");
        System.out.println("  → Datos eliminados de: " + dbType.toUpperCase());
        System.out.println("  → Auditoría DELETE en MONGODB con:");
        System.out.println("     - datosAnteriores (placa, marca, modelo, etc.)");
        System.out.println();
        
        // ===========================================
        // PRUEBA 4: ORDEN
        // ===========================================
        System.out.println("--- PRUEBA 4: CREAR ORDEN ---");
        Orden orden = new Orden(0, clienteCreado.getId(), 1, "pendiente");
        orden.setCostoRepuestos(100000);
        orden.setHorasTrabajo(2);
        orden.setCostoHora(50000);
        orden.calcularPrecios();
        
        Orden ordenCreada = DatabaseUtil.insertarOrden(orden);
        System.out.println("✓ Orden creada con ID: " + ordenCreada.getId());
        System.out.println("  → Total: $" + ordenCreada.getTotal());
        System.out.println("  → Datos en: " + dbType.toUpperCase());
        System.out.println("  → Auditoría CREATE en: MONGODB");
        System.out.println();
        
        // ===========================================
        // LIMPIAR DATOS DE PRUEBA
        // ===========================================
        System.out.println("--- LIMPIEZA ---");
        DatabaseUtil.eliminarOrden(ordenCreada.getId());
        DatabaseUtil.eliminarCliente(clienteCreado.getId());
        System.out.println("✓ Datos de prueba eliminados de " + dbType.toUpperCase());
        System.out.println("✓ Auditoría de eliminación guardada en MONGODB");
        System.out.println();
        
        // ===========================================
        // RESUMEN
        // ===========================================
        System.out.println("==============================================");
        System.out.println("RESUMEN DE LA PRUEBA");
        System.out.println("==============================================");
        System.out.println("✓ Base de datos principal: " + dbType.toUpperCase());
        System.out.println("✓ Auditoría: MONGODB");
        System.out.println();
        System.out.println("Operaciones registradas en MongoDB:");
        System.out.println("  1. CREATE cliente");
        System.out.println("  2. UPDATE cliente");
        System.out.println("  3. CREATE vehículo");
        System.out.println("  4. DELETE vehículo");
        System.out.println("  5. CREATE orden");
        System.out.println("  6. DELETE orden");
        System.out.println("  7. DELETE cliente");
        System.out.println();
        System.out.println("Para ver la auditoría, ejecuta en MongoDB:");
        System.out.println("  mongosh --host 172.30.16.104");
        System.out.println("  use taller_db");
        System.out.println("  db.auditoria.find({ usuario: 'prueba_sistema' }).pretty()");
        System.out.println();
        System.out.println("==============================================");
    }
}

