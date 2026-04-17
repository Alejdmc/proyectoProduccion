package com.proyectoproduccion.Util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class VerificarSQLite {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Verificando Base de Datos SQLite");
        System.out.println("========================================");
        System.out.println();
        
        try {
            Connection conn = Conexion.getConnection();
            
            if (conn == null) {
                System.err.println("ERROR: No se pudo conectar a la base de datos");
                return;
            }
            
            System.out.println("- Conexion exitosa!");
            System.out.println("- Tipo de BD: " + ConfigDB.getDbType().toUpperCase());
            System.out.println("- URL: " + ConfigDB.getURL());
            System.out.println();
            
            Statement stmt = conn.createStatement();
            
            // Contar clientes
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM clientes");
            rs.next();
            int clientes = rs.getInt(1);
            rs.close();
            
            // Contar vehículos
            rs = stmt.executeQuery("SELECT COUNT(*) FROM vehiculos");
            rs.next();
            int vehiculos = rs.getInt(1);
            rs.close();
            
            // Contar órdenes
            rs = stmt.executeQuery("SELECT COUNT(*) FROM ordenes");
            rs.next();
            int ordenes = rs.getInt(1);
            rs.close();
            
            System.out.println("DATOS EN LA BASE DE DATOS:");
            System.out.println("  - Clientes:  " + clientes);
            System.out.println("  - Vehiculos: " + vehiculos);
            System.out.println("  - Ordenes:   " + ordenes);
            System.out.println();
            
            // Mostrar algunos clientes
            System.out.println("PRIMEROS 5 CLIENTES:");
            rs = stmt.executeQuery("SELECT id, nombre, telefono FROM clientes LIMIT 5");
            while (rs.next()) {
                System.out.println("  " + rs.getInt("id") + ". " + 
                                 rs.getString("nombre") + " - " + 
                                 rs.getString("telefono"));
            }
            rs.close();
            System.out.println();
            
            // Mostrar resumen de órdenes
            System.out.println("RESUMEN DE ORDENES:");
            rs = stmt.executeQuery("SELECT estado, COUNT(*) as cantidad, SUM(total) as total FROM ordenes GROUP BY estado");
            while (rs.next()) {
                System.out.println("  " + rs.getString("estado") + ": " + 
                                 rs.getInt("cantidad") + " ordenes - $" + 
                                 String.format("%.2f", rs.getDouble("total")));
            }
            rs.close();
            
            stmt.close();
            conn.close();
            
            System.out.println();
            System.out.println("========================================");
            System.out.println("  SQLite funcionando correctamente!");
            System.out.println("========================================");
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

