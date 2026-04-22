package com.proyectoproduccion.Util;

import com.proyectoproduccion.Modelo.Cliente;
import java.sql.*;

public class ProbarCommitSQLite {

    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("PRUEBA DE COMMIT EN SQLITE");
        System.out.println("=".repeat(70));
        System.out.println();

        // Asegurar que estamos usando SQLite
        String tipoDb = ConfigDB.getDbType();
        System.out.println("Base de datos: " + tipoDb.toUpperCase());
        
        if (!"sqlite".equals(tipoDb)) {
            System.out.println("⚠ ADVERTENCIA: Esta prueba es para SQLite");
            System.out.println("  Cambia db.type=sqlite en database.properties");
            return;
        }
        
        System.out.println();

        try (Connection conn = Conexion.getConnection()) {
            System.out.println("1. ESTADO DE AUTOCOMMIT:");
            System.out.println("   AutoCommit: " + conn.getAutoCommit());
            System.out.println();

            // Contar clientes antes
            System.out.println("2. CLIENTES ANTES DE INSERTAR:");
            int clientesAntes = contarClientes(conn);
            System.out.println("   Total: " + clientesAntes);
            System.out.println();

            // Insertar un cliente usando DatabaseUtil
            System.out.println("3. INSERTANDO CLIENTE CON DatabaseUtil:");
            String nombrePrueba = "TEST_COMMIT_" + System.currentTimeMillis();
            Cliente nuevo = new Cliente(0, nombrePrueba, "3001234567", 
                                       "test@commit.com", "Test Commit");
            
            Cliente insertado = DatabaseUtil.insertarCliente(nuevo);
            System.out.println("   ✓ Cliente insertado");
            System.out.println("   ID: " + insertado.getId());
            System.out.println("   Nombre: " + insertado.getNombre());
            System.out.println();

            // Verificar inmediatamente en la MISMA conexión
            System.out.println("4. VERIFICANDO EN LA MISMA CONEXION:");
            int clientesDespues1 = contarClientes(conn);
            System.out.println("   Total: " + clientesDespues1);
            System.out.println("   Diferencia: " + (clientesDespues1 - clientesAntes));
            System.out.println();

            // Verificar en una NUEVA conexión
            System.out.println("5. VERIFICANDO EN NUEVA CONEXION:");
            try (Connection conn2 = Conexion.getConnection()) {
                int clientesDespues2 = contarClientes(conn2);
                System.out.println("   Total: " + clientesDespues2);
                System.out.println("   Diferencia: " + (clientesDespues2 - clientesAntes));
                
                // Buscar el cliente específico
                Cliente encontrado = buscarCliente(conn2, insertado.getId());
                if (encontrado != null) {
                    System.out.println("   ✓ Cliente encontrado en nueva conexión");
                    System.out.println("   Nombre: " + encontrado.getNombre());
                } else {
                    System.out.println("   ✗ Cliente NO encontrado en nueva conexión");
                }
            }
            System.out.println();

            // Limpiar
            System.out.println("6. LIMPIANDO CLIENTE DE PRUEBA:");
            DatabaseUtil.eliminarCliente(insertado.getId());
            
            // Verificar eliminación
            try (Connection conn3 = Conexion.getConnection()) {
                int clientesFinal = contarClientes(conn3);
                System.out.println("   Total después de eliminar: " + clientesFinal);
                if (clientesFinal == clientesAntes) {
                    System.out.println("   ✓ Cliente eliminado correctamente");
                } else {
                    System.out.println("   ⚠ Contadores no coinciden");
                }
            }

        } catch (SQLException e) {
            System.out.println("✗ ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
        System.out.println("=".repeat(70));
        System.out.println("PRUEBA COMPLETADA");
        System.out.println("=".repeat(70));
    }

    private static int contarClientes(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM clientes";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    private static Cliente buscarCliente(Connection conn, int id) throws SQLException {
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

