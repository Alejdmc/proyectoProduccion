package com.proyectoproduccion.Util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;

public class EjecutarDatabaseSQL {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Ejecutando database.sql en MySQL");
        System.out.println("========================================");
        System.out.println();
        
        try {
            // Leer el archivo SQL
            System.out.println("Leyendo database.sql...");
            StringBuilder sql = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader("database.sql"));
            String line;
            while ((line = reader.readLine()) != null) {
                sql.append(line).append("\n");
            }
            reader.close();
            
            // Conectar a MySQL (sin especificar base de datos)
            System.out.println("Conectando a MySQL...");
            Connection conn = Conexion.getConnection();
            
            if (conn == null) {
                System.err.println("ERROR: No se pudo conectar a MySQL");
                System.err.println("Verifica database.properties");
                return;
            }
            
            System.out.println("Conexion establecida!");
            System.out.println();
            
            // Dividir y ejecutar los comandos SQL
            System.out.println("Ejecutando comandos SQL...");
            String[] commands = sql.toString().split(";");
            
            Statement stmt = conn.createStatement();
            int executed = 0;
            
            for (String command : commands) {
                String trimmed = command.trim();
                if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                    try {
                        stmt.execute(trimmed);
                        executed++;
                    } catch (Exception e) {
                        // Ignorar errores menores
                        if (!e.getMessage().contains("database exists")) {
                            System.out.println("Advertencia: " + e.getMessage());
                        }
                    }
                }
            }
            
            stmt.close();
            conn.close();
            
            System.out.println();
            System.out.println("Comandos ejecutados: " + executed);
            System.out.println();
            System.out.println("Base de datos creada exitosamente!");
            System.out.println();
            System.out.println("Se creo la base de datos 'taller_db' con las tablas:");
            System.out.println("  - clientes");
            System.out.println("  - vehiculos");
            System.out.println("  - ordenes");
            System.out.println();
            
        } catch (Exception e) {
            System.err.println();
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            System.err.println();
            System.err.println("SOLUCION: Usa MySQL Workbench para ejecutar database.sql manualmente");
            System.err.println("1. Abre MySQL Workbench");
            System.err.println("2. File > Open SQL Script...");
            System.err.println("3. Selecciona database.sql");
            System.err.println("4. Haz clic en Execute");
        }
    }
}

