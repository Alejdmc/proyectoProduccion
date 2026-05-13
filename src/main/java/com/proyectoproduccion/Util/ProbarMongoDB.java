package com.proyectoproduccion.Util;

import com.proyectoproduccion.Modelo.Cliente;
import com.proyectoproduccion.Modelo.Vehiculo;
import com.proyectoproduccion.Modelo.Orden;

import java.util.ArrayList;

/**
 * Utilidad para probar la conexión y lectura de datos desde MongoDB
 */
public class ProbarMongoDB {

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("  TEST: LECTURA DE DATOS DESDE MONGODB");
        System.out.println("===========================================\n");

        // Forzar MongoDB como tipo de BD
        ConfigDB.setDbType("mongodb");
        System.out.println("Tipo de BD configurado: " + ConfigDB.getDbType());
        System.out.println("Host: " + ConfigDB.getHost());
        System.out.println("Puerto: " + ConfigDB.getPort());
        System.out.println("Base de datos: " + ConfigDB.getDatabase());
        System.out.println();

        // Probar conexión
        System.out.println("--- Probando conexión ---");
        if (ConexionMongo.probarConexion()) {
            System.out.println("✓ Conexión exitosa a MongoDB\n");
        } else {
            System.err.println("✗ Error al conectar a MongoDB");
            System.err.println("Detalle: " + ConexionMongo.getUltimoErrorDetalle());
            return;
        }

        // Probar carga de clientes
        System.out.println("--- Cargando clientes ---");
        ArrayList<Cliente> clientes = DatabaseUtil.cargarClientes();
        System.out.println("Total clientes: " + clientes.size());
        if (!clientes.isEmpty()) {
            System.out.println("Primeros 3 clientes:");
            for (int i = 0; i < Math.min(3, clientes.size()); i++) {
                Cliente c = clientes.get(i);
                System.out.printf("  [%d] %s - %s%n", c.getId(), c.getNombre(), c.getTelefono());
            }
        }
        System.out.println();

        // Probar carga de vehículos
        System.out.println("--- Cargando vehículos ---");
        ArrayList<Vehiculo> vehiculos = DatabaseUtil.cargarVehiculos();
        System.out.println("Total vehículos: " + vehiculos.size());
        if (!vehiculos.isEmpty()) {
            System.out.println("Primeros 3 vehículos:");
            for (int i = 0; i < Math.min(3, vehiculos.size()); i++) {
                Vehiculo v = vehiculos.get(i);
                System.out.printf("  [%d] %s - %s %s (%d) - Cliente ID: %d%n",
                        v.getId(), v.getPlaca(), v.getMarca(), v.getModelo(),
                        v.getAnio(), v.getClienteId());
            }
        }
        System.out.println();

        // Probar carga de órdenes
        System.out.println("--- Cargando órdenes ---");
        ArrayList<Orden> ordenes = DatabaseUtil.cargarOrdenes();
        System.out.println("Total órdenes: " + ordenes.size());
        if (!ordenes.isEmpty()) {
            System.out.println("Primeras 3 órdenes:");
            for (int i = 0; i < Math.min(3, ordenes.size()); i++) {
                Orden o = ordenes.get(i);
                System.out.printf("  [%d] Cliente:%d Vehículo:%d Estado:%s Total:$%.2f%n",
                        o.getId(), o.getClienteId(), o.getVehiculoId(),
                        o.getEstado(), o.getTotal());
            }
        }
        System.out.println();

        System.out.println("===========================================");
        System.out.println("  ✓ PRUEBA COMPLETADA");
        System.out.println("===========================================");
    }
}

