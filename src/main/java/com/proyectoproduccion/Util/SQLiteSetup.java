package com.proyectoproduccion.Util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Utilidad para crear y configurar la base de datos SQLite
 */
public class SQLiteSetup {

    /**
     * Crea la base de datos SQLite con todas las tablas
     */
    public static void crearBaseDatos() {
        String sqliteFile = "taller_db.sqlite";
        String url = "jdbc:sqlite:" + sqliteFile;
        
        System.out.println("========================================");
        System.out.println("  Creando Base de Datos SQLite");
        System.out.println("========================================");
        System.out.println();
        
        try {
            // Cargar el driver SQLite explícitamente
            Class.forName("org.sqlite.JDBC");
            
            // Conectar a SQLite (crea el archivo si no existe)
            Connection conn = DriverManager.getConnection(url);
            System.out.println("- Archivo SQLite creado: " + sqliteFile);
            
            // Leer y ejecutar database-sqlite.sql
            System.out.println("Creando tablas...");
            BufferedReader reader = new BufferedReader(new FileReader("database-sqlite.sql"));
            StringBuilder currentCommand = new StringBuilder();
            String line;
            Statement stmt = conn.createStatement();
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Ignorar líneas vacías y comentarios
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                
                currentCommand.append(line).append(" ");
                
                // Si la línea termina con ;, ejecutar el comando
                if (line.endsWith(";")) {
                    String command = currentCommand.toString().trim();
                    if (!command.isEmpty()) {
                        try {
                            stmt.execute(command);
                        } catch (Exception e) {
                            // Ignorar errores de "already exists"
                            if (!e.getMessage().contains("already exists")) {
                                System.err.println("Error ejecutando: " + command.substring(0, Math.min(50, command.length())) + "...");
                                System.err.println("  " + e.getMessage());
                            }
                        }
                    }
                    currentCommand = new StringBuilder();
                }
            }
            reader.close();
            
            stmt.close();
            conn.close();
            
            System.out.println("- Tablas creadas exitosamente");
            System.out.println();
            System.out.println("Base de datos SQLite lista!");
            System.out.println("Archivo: " + sqliteFile);
            System.out.println();
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inserta datos de ejemplo en SQLite
     */
    public static void insertarDatosEjemplo() {
        String sqliteFile = "taller_db.sqlite";
        String url = "jdbc:sqlite:" + sqliteFile;
        
        System.out.println("========================================");
        System.out.println("  Insertando Datos de Ejemplo");
        System.out.println("========================================");
        System.out.println();
        
        try {
            // Cargar el driver SQLite explícitamente
            Class.forName("org.sqlite.JDBC");
            
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            
            // Insertar clientes
            System.out.println("Insertando clientes...");
            stmt.execute("INSERT INTO clientes (nombre, telefono, email, direccion) VALUES " +
                "('Juan Perez Garcia', '3001234567', 'juan.perez@email.com', 'Calle 45 #12-34, Bogota'), " +
                "('Maria Gonzalez Lopez', '3109876543', 'maria.gonzalez@email.com', 'Carrera 10 #25-67, Medellin'), " +
                "('Carlos Rodriguez Martinez', '3201122334', 'carlos.rodriguez@email.com', 'Avenida 68 #100-23, Cali'), " +
                "('Ana Martinez Sanchez', '3152233445', 'ana.martinez@email.com', 'Calle 100 #15-45, Barranquilla'), " +
                "('Luis Hernandez Diaz', '3183344556', 'luis.hernandez@email.com', 'Carrera 7 #32-18, Cartagena'), " +
                "('Laura Gomez Ruiz', '3124455667', 'laura.gomez@email.com', 'Calle 85 #20-30, Bucaramanga'), " +
                "('Pedro Ramirez Torres', '3165566778', 'pedro.ramirez@email.com', 'Avenida Jimenez #5-40, Pereira'), " +
                "('Sofia Castro Vargas', '3196677889', 'sofia.castro@email.com', 'Calle 50 #8-15, Manizales'), " +
                "('Diego Morales Ortiz', '3007788990', 'diego.morales@email.com', 'Carrera 15 #60-25, Ibague'), " +
                "('Carmen Flores Jimenez', '3138899001', 'carmen.flores@email.com', 'Calle 72 #11-50, Pasto')");
            
            // Insertar vehículos
            System.out.println("Insertando vehiculos...");
            stmt.execute("INSERT INTO vehiculos (placa, marca, modelo, anio, cliente_id) VALUES " +
                "('ABC123', 'Toyota', 'Corolla', 2020, 1), " +
                "('DEF456', 'Chevrolet', 'Spark', 2018, 1), " +
                "('GHI789', 'Mazda', 'CX-5', 2021, 2), " +
                "('JKL012', 'Renault', 'Logan', 2019, 3), " +
                "('MNO345', 'Nissan', 'Sentra', 2022, 3), " +
                "('PQR678', 'Hyundai', 'Tucson', 2020, 4), " +
                "('STU901', 'Kia', 'Sportage', 2021, 5), " +
                "('VWX234', 'Honda', 'Civic', 2019, 6), " +
                "('YZA567', 'Ford', 'Fiesta', 2017, 6), " +
                "('BCD890', 'Volkswagen', 'Gol', 2018, 7), " +
                "('EFG123', 'Suzuki', 'Swift', 2020, 8)");
            
            // Insertar órdenes
            System.out.println("Insertando ordenes...");
            stmt.execute("INSERT INTO ordenes (cliente_id, vehiculo_id, estado, costo_repuestos, horas_trabajo, costo_hora, mano_obra, subtotal, iva, total) VALUES " +
                "(1, 1, 'ENTREGADO', 150000.00, 3.0, 50000.00, 150000.00, 300000.00, 57000.00, 357000.00), " +
                "(2, 3, 'ENTREGADO', 250000.00, 4.5, 50000.00, 225000.00, 475000.00, 90250.00, 565250.00), " +
                "(3, 4, 'ENTREGADO', 80000.00, 2.0, 50000.00, 100000.00, 180000.00, 34200.00, 214200.00), " +
                "(1, 2, 'EN_PROCESO', 200000.00, 5.0, 50000.00, 250000.00, 450000.00, 85500.00, 535500.00), " +
                "(4, 6, 'EN_PROCESO', 180000.00, 3.5, 50000.00, 175000.00, 355000.00, 67450.00, 422450.00), " +
                "(5, 7, 'EN_PROCESO', 320000.00, 6.0, 50000.00, 300000.00, 620000.00, 117800.00, 737800.00), " +
                "(6, 8, 'RECIBIDO', 100000.00, 2.5, 50000.00, 125000.00, 225000.00, 42750.00, 267750.00), " +
                "(3, 5, 'RECIBIDO', 450000.00, 8.0, 50000.00, 400000.00, 850000.00, 161500.00, 1011500.00), " +
                "(7, 10, 'RECIBIDO', 95000.00, 2.0, 50000.00, 100000.00, 195000.00, 37050.00, 232050.00), " +
                "(8, 11, 'RECIBIDO', 120000.00, 3.0, 50000.00, 150000.00, 270000.00, 51300.00, 321300.00), " +
                "(6, 9, 'ENTREGADO', 75000.00, 1.5, 50000.00, 75000.00, 150000.00, 28500.00, 178500.00), " +
                "(2, 3, 'EN_PROCESO', 280000.00, 5.5, 50000.00, 275000.00, 555000.00, 105450.00, 660450.00)");
            
            stmt.close();
            conn.close();
            
            System.out.println();
            System.out.println("- Datos insertados exitosamente!");
            System.out.println("  - 10 clientes");
            System.out.println("  - 11 vehiculos");
            System.out.println("  - 12 ordenes");
            System.out.println();
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        crearBaseDatos();
        insertarDatosEjemplo();
        
        System.out.println("========================================");
        System.out.println("  SQLite listo para usar!");
        System.out.println("========================================");
        System.out.println();
        System.out.println("Para usar SQLite en tu aplicacion:");
        System.out.println("1. Copia database.properties.sqlite a database.properties");
        System.out.println("2. O edita database.properties y cambia db.type=sqlite");
        System.out.println();
    }
}

