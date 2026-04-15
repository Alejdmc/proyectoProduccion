package com.proyectoproduccion.Util;

import com.proyectoproduccion.Modelo.Cliente;
import com.proyectoproduccion.Modelo.Vehiculo;
import com.proyectoproduccion.Modelo.Orden;

/**
 * Clase de utilidad para insertar datos de prueba en la base de datos
 * Ejecuta este código una sola vez para poblar las tablas con datos de ejemplo
 */
public class InsertarDatosPrueba {

    public static void main(String[] args) {
        System.out.println("=== INSERTANDO DATOS DE PRUEBA ===\n");
        
        // Insertar clientes
        System.out.println("Insertando clientes...");
        Cliente[] clientes = insertarClientes();
        System.out.println("✓ " + clientes.length + " clientes insertados\n");
        
        // Insertar vehículos
        System.out.println("Insertando vehículos...");
        Vehiculo[] vehiculos = insertarVehiculos(clientes);
        System.out.println("✓ " + vehiculos.length + " vehículos insertados\n");
        
        // Insertar órdenes
        System.out.println("Insertando órdenes de servicio...");
        Orden[] ordenes = insertarOrdenes(clientes, vehiculos);
        System.out.println("✓ " + ordenes.length + " órdenes insertadas\n");
        
        System.out.println("=== DATOS DE PRUEBA INSERTADOS EXITOSAMENTE ===");
    }

    /**
     * Inserta clientes de ejemplo
     */
    private static Cliente[] insertarClientes() {
        Cliente[] clientes = {
            new Cliente(0, "Juan Pérez García", "3001234567", "juan.perez@email.com", "Calle 45 #12-34, Bogotá"),
            new Cliente(0, "María González López", "3109876543", "maria.gonzalez@email.com", "Carrera 10 #25-67, Medellín"),
            new Cliente(0, "Carlos Rodríguez Martínez", "3201122334", "carlos.rodriguez@email.com", "Avenida 68 #100-23, Cali"),
            new Cliente(0, "Ana Martínez Sánchez", "3152233445", "ana.martinez@email.com", "Calle 100 #15-45, Barranquilla"),
            new Cliente(0, "Luis Hernández Díaz", "3183344556", "luis.hernandez@email.com", "Carrera 7 #32-18, Cartagena"),
            new Cliente(0, "Laura Gómez Ruiz", "3124455667", "laura.gomez@email.com", "Calle 85 #20-30, Bucaramanga"),
            new Cliente(0, "Pedro Ramírez Torres", "3165566778", "pedro.ramirez@email.com", "Avenida Jiménez #5-40, Pereira"),
            new Cliente(0, "Sofia Castro Vargas", "3196677889", "sofia.castro@email.com", "Calle 50 #8-15, Manizales"),
            new Cliente(0, "Diego Morales Ortiz", "3007788990", "diego.morales@email.com", "Carrera 15 #60-25, Ibagué"),
            new Cliente(0, "Carmen Flores Jiménez", "3138899001", "carmen.flores@email.com", "Calle 72 #11-50, Pasto")
        };

        for (int i = 0; i < clientes.length; i++) {
            clientes[i] = DatabaseUtil.insertarCliente(clientes[i]);
            System.out.println("  - " + clientes[i].getNombre() + " (ID: " + clientes[i].getId() + ")");
        }

        return clientes;
    }

    /**
     * Inserta vehículos de ejemplo asociados a los clientes
     */
    private static Vehiculo[] insertarVehiculos(Cliente[] clientes) {
        Vehiculo[] vehiculos = {
            // Cliente 1: Juan Pérez (2 vehículos)
            new Vehiculo(0, "ABC123", "Toyota", "Corolla", 2020, clientes[0].getId()),
            new Vehiculo(0, "DEF456", "Chevrolet", "Spark", 2018, clientes[0].getId()),

            // Cliente 2: María González (1 vehículo)
            new Vehiculo(0, "GHI789", "Mazda", "CX-5", 2021, clientes[1].getId()),

            // Cliente 3: Carlos Rodríguez (2 vehículos)
            new Vehiculo(0, "JKL012", "Renault", "Logan", 2019, clientes[2].getId()),
            new Vehiculo(0, "MNO345", "Nissan", "Sentra", 2022, clientes[2].getId()),

            // Cliente 4: Ana Martínez (1 vehículo)
            new Vehiculo(0, "PQR678", "Hyundai", "Tucson", 2020, clientes[3].getId()),

            // Cliente 5: Luis Hernández (1 vehículo)
            new Vehiculo(0, "STU901", "Kia", "Sportage", 2021, clientes[4].getId()),

            // Cliente 6: Laura Gómez (2 vehículos)
            new Vehiculo(0, "VWX234", "Honda", "Civic", 2019, clientes[5].getId()),
            new Vehiculo(0, "YZA567", "Ford", "Fiesta", 2017, clientes[5].getId()),

            // Cliente 7: Pedro Ramírez (1 vehículo)
            new Vehiculo(0, "BCD890", "Volkswagen", "Gol", 2018, clientes[6].getId()),

            // Cliente 8: Sofia Castro (1 vehículo)
            new Vehiculo(0, "EFG123", "Suzuki", "Swift", 2020, clientes[7].getId())
        };

        for (int i = 0; i < vehiculos.length; i++) {
            vehiculos[i] = DatabaseUtil.insertarVehiculo(vehiculos[i]);
            System.out.println("  - " + vehiculos[i].getPlaca() + " - " + 
                             vehiculos[i].getMarca() + " " + vehiculos[i].getModelo() + 
                             " (ID: " + vehiculos[i].getId() + ")");
        }

        return vehiculos;
    }

    /**
     * Inserta órdenes de servicio de ejemplo
     */
    private static Orden[] insertarOrdenes(Cliente[] clientes, Vehiculo[] vehiculos) {
        // Órdenes completadas
        Orden orden1 = crearOrden(clientes[0].getId(), vehiculos[0].getId(), "ENTREGADO", 150000.00, 3.0);
        Orden orden2 = crearOrden(clientes[1].getId(), vehiculos[2].getId(), "ENTREGADO", 250000.00, 4.5);
        Orden orden3 = crearOrden(clientes[2].getId(), vehiculos[3].getId(), "ENTREGADO", 80000.00, 2.0);

        // Órdenes en proceso
        Orden orden4 = crearOrden(clientes[0].getId(), vehiculos[1].getId(), "EN_PROCESO", 200000.00, 5.0);
        Orden orden5 = crearOrden(clientes[3].getId(), vehiculos[5].getId(), "EN_PROCESO", 180000.00, 3.5);
        Orden orden6 = crearOrden(clientes[4].getId(), vehiculos[6].getId(), "EN_PROCESO", 320000.00, 6.0);

        // Órdenes recibidas
        Orden orden7 = crearOrden(clientes[5].getId(), vehiculos[7].getId(), "RECIBIDO", 100000.00, 2.5);
        Orden orden8 = crearOrden(clientes[2].getId(), vehiculos[4].getId(), "RECIBIDO", 450000.00, 8.0);
        Orden orden9 = crearOrden(clientes[6].getId(), vehiculos[9].getId(), "RECIBIDO", 95000.00, 2.0);
        Orden orden10 = crearOrden(clientes[7].getId(), vehiculos[10].getId(), "RECIBIDO", 120000.00, 3.0);

        // Órdenes adicionales
        Orden orden11 = crearOrden(clientes[5].getId(), vehiculos[8].getId(), "ENTREGADO", 75000.00, 1.5);
        Orden orden12 = crearOrden(clientes[1].getId(), vehiculos[2].getId(), "EN_PROCESO", 280000.00, 5.5);

        Orden[] ordenes = {orden1, orden2, orden3, orden4, orden5, orden6, 
                          orden7, orden8, orden9, orden10, orden11, orden12};

        for (int i = 0; i < ordenes.length; i++) {
            ordenes[i] = DatabaseUtil.insertarOrden(ordenes[i]);
            System.out.println("  - Orden #" + ordenes[i].getId() + " - " + 
                             ordenes[i].getEstado() + " - Total: $" + 
                             String.format("%,.0f", ordenes[i].getTotal()));
        }

        return ordenes;
    }

    /**
     * Crea una orden con cálculos automáticos
     */
    private static Orden crearOrden(int clienteId, int vehiculoId, String estado, 
                                   double costoRepuestos, double horasTrabajo) {
        Orden orden = new Orden(0, clienteId, vehiculoId, estado);
        orden.setCostoRepuestos(costoRepuestos);
        orden.setHorasTrabajo(horasTrabajo);
        orden.setCostoHora(50000.00); // Costo por hora estándar
        orden.calcularPrecios(); // Calcula mano de obra, subtotal, IVA y total
        return orden;
    }
}

