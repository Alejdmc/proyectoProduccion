package com.proyectoproduccion.Util;

import com.proyectoproduccion.Modelo.Cliente;
import java.sql.*;

public class VerificarBaseDatos {

    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("VERIFICACION DE BASE DE DATOS");
        System.out.println("=".repeat(70));
        System.out.println();

        // 1. Verificar tipo de base de datos configurada
        String tipoDb = ConfigDB.getDbType();
        System.out.println("1. TIPO DE BASE DE DATOS CONFIGURADA:");
        System.out.println("   Tipo: " + tipoDb.toUpperCase());
        System.out.println("   URL:  " + ConfigDB.getURL());
        System.out.println();

        // 2. Probar conexión
        System.out.println("2. PROBANDO CONEXION:");
        try (Connection conn = Conexion.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("   ✓ Conexión exitosa");
            System.out.println("   Driver: " + meta.getDriverName());
            System.out.println("   Versión: " + meta.getDriverVersion());
            System.out.println("   Producto: " + meta.getDatabaseProductName());
            System.out.println();

            // 3. Contar registros existentes ANTES
            System.out.println("3. REGISTROS ANTES DE LA PRUEBA:");
            int clientesAntes = contarRegistros(conn, "clientes");
            int vehiculosAntes = contarRegistros(conn, "vehiculos");
            int ordenesAntes = contarRegistros(conn, "ordenes");
            System.out.println("   Clientes:  " + clientesAntes);
            System.out.println("   Vehículos: " + vehiculosAntes);
            System.out.println("   Órdenes:   " + ordenesAntes);
            System.out.println();

            // 4. Mostrar algunos clientes existentes
            System.out.println("4. ULTIMOS 5 CLIENTES EN LA BASE DE DATOS:");
            mostrarUltimosClientes(conn, 5);
            System.out.println();

            // 5. Insertar un cliente de prueba
            System.out.println("5. INSERTANDO CLIENTE DE PRUEBA:");
            String nombrePrueba = "PRUEBA_" + System.currentTimeMillis();
            Cliente clientePrueba = new Cliente(0, nombrePrueba, "3001234567", 
                                               "prueba@test.com", "Dirección de Prueba");
            
            Cliente insertado = DatabaseUtil.insertarCliente(clientePrueba);
            
            if (insertado != null && insertado.getId() > 0) {
                System.out.println("   ✓ Cliente insertado exitosamente");
                System.out.println("   ID generado: " + insertado.getId());
                System.out.println("   Nombre: " + insertado.getNombre());
                System.out.println();

                // 6. Verificar que se guardó
                System.out.println("6. VERIFICANDO QUE SE GUARDO:");
                Cliente verificado = buscarClientePorId(conn, insertado.getId());
                if (verificado != null) {
                    System.out.println("   ✓ Cliente encontrado en la base de datos");
                    System.out.println("   Nombre: " + verificado.getNombre());
                    System.out.println("   Teléfono: " + verificado.getTelefono());
                    System.out.println("   Email: " + verificado.getEmail());
                    System.out.println();

                    // 7. Eliminar el cliente de prueba
                    System.out.println("7. LIMPIANDO CLIENTE DE PRUEBA:");
                    DatabaseUtil.eliminarCliente(insertado.getId());
                    System.out.println("   ✓ Cliente de prueba eliminado");
                    System.out.println();
                } else {
                    System.out.println("   ✗ ERROR: Cliente NO encontrado después de insertar");
                    System.out.println();
                }
            } else {
                System.out.println("   ✗ ERROR: No se pudo insertar el cliente");
                System.out.println();
            }

            // 8. Contar registros finales
            System.out.println("8. REGISTROS DESPUES DE LA PRUEBA:");
            int clientesDespues = contarRegistros(conn, "clientes");
            int vehiculosDespues = contarRegistros(conn, "vehiculos");
            int ordenesDespues = contarRegistros(conn, "ordenes");
            System.out.println("   Clientes:  " + clientesDespues);
            System.out.println("   Vehículos: " + vehiculosDespues);
            System.out.println("   Órdenes:   " + ordenesDespues);
            System.out.println();

            // Verificar que los contadores no cambiaron
            if (clientesAntes == clientesDespues) {
                System.out.println("   ✓ Contadores correctos (cliente de prueba eliminado)");
            } else {
                System.out.println("   ⚠ Diferencia en contadores (esto es normal si no se eliminó)");
            }

        } catch (SQLException e) {
            System.out.println("   ✗ ERROR DE CONEXION:");
            System.out.println("   " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
        System.out.println("=".repeat(70));
        System.out.println("VERIFICACION COMPLETADA");
        System.out.println("=".repeat(70));
    }

    private static int contarRegistros(Connection conn, String tabla) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM " + tabla;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    private static void mostrarUltimosClientes(Connection conn, int limite) throws SQLException {
        String sql = "SELECT id, nombre, telefono FROM clientes ORDER BY id DESC LIMIT ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limite);
            ResultSet rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.printf("   [%d] ID: %d | Nombre: %s | Tel: %s%n",
                        count, rs.getInt("id"), rs.getString("nombre"), rs.getString("telefono"));
            }
            if (count == 0) {
                System.out.println("   (No hay clientes en la base de datos)");
            }
        }
    }

    private static Cliente buscarClientePorId(Connection conn, int id) throws SQLException {
        String sql = "SELECT id, nombre, telefono, email, direccion FROM clientes WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Cliente(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getString("direccion")
                );
            }
        }
        return null;
    }
}

