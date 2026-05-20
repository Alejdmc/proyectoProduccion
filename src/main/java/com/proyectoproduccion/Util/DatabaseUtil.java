 package com.proyectoproduccion.Util;

import com.proyectoproduccion.Modelo.Cliente;
import com.proyectoproduccion.Modelo.Orden;
import com.proyectoproduccion.Modelo.Vehiculo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseUtil {

    private static boolean esMongoDB() {
        return "mongodb".equals(ConfigDB.getDbType());
    }

    // ==================== CLIENTES ====================

    public static ArrayList<Cliente> cargarClientes() {
        if (esMongoDB()) {
            return cargarClientesMongo();
        }
        return cargarClientesSQL();
    }

    private static ArrayList<Cliente> cargarClientesMongo() {
        ArrayList<Cliente> lista = new ArrayList<>();
        try {
            MongoDatabase db = ConexionMongo.getDatabase();
            MongoCollection<Document> collection = db.getCollection("clientes");
            
            for (Document doc : collection.find().sort(new Document("_id", 1))) {
                Cliente c = new Cliente(
                        doc.getInteger("_id", 0),
                        doc.getString("nombre"),
                        doc.getString("telefono"),
                        doc.getString("email"),
                        doc.getString("direccion")
                );
                lista.add(c);
            }
        } catch (Exception e) {
            System.err.println("Error cargando clientes de MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    private static ArrayList<Cliente> cargarClientesSQL() {
        ArrayList<Cliente> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, telefono, email, direccion FROM clientes ORDER BY id";
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Cliente c = new Cliente(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getString("direccion")
                );
                lista.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static Cliente insertarCliente(Cliente c) {
        if (esMongoDB()) {
            return insertarClienteMongo(c);
        }
        return insertarClienteSQL(c);
    }

    private static Cliente insertarClienteMongo(Cliente c) {
        try {
            MongoDatabase db = ConexionMongo.getDatabase();
            MongoCollection<Document> collection = db.getCollection("clientes");
            
            // Obtener el siguiente ID
            int nuevoId = obtenerSiguienteIdMongo("clientes");
            
            Document doc = new Document()
                    .append("_id", nuevoId)
                    .append("nombre", c.getNombre())
                    .append("telefono", c.getTelefono())
                    .append("email", c.getEmail())
                    .append("direccion", c.getDireccion());
            
            collection.insertOne(doc);
            c.setId(nuevoId);
            
            // Auditoría
            AuditoriaService.registrarCreacion("clientes", String.valueOf(nuevoId), AuditoriaService.clienteToMap(c));
        } catch (Exception e) {
            System.err.println("Error insertando cliente en MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
        return c;
    }

    private static Cliente insertarClienteSQL(Cliente c) {
        String sql = "INSERT INTO clientes (nombre, telefono, email, direccion) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getTelefono());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getDireccion());
            ps.executeUpdate();
            
            // Asegurar commit (importante para SQLite)
            if (!conn.getAutoCommit()) {
                conn.commit();
            }
            
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                c.setId(keys.getInt(1));
            }
            
            // Auditoría
            AuditoriaService.registrarCreacion("clientes", String.valueOf(c.getId()), AuditoriaService.clienteToMap(c));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

    public static void actualizarCliente(Cliente c) {
        if (esMongoDB()) {
            actualizarClienteMongo(c);
        } else {
            actualizarClienteSQL(c);
        }
    }

    private static void actualizarClienteMongo(Cliente c) {
        try {
            MongoDatabase db = ConexionMongo.getDatabase();
            MongoCollection<Document> collection = db.getCollection("clientes");
            
            // Obtener datos anteriores
            Document docAnterior = collection.find(new Document("_id", c.getId())).first();
            Cliente anterior = null;
            if (docAnterior != null) {
                anterior = new Cliente(
                    docAnterior.getInteger("_id", 0),
                    docAnterior.getString("nombre"),
                    docAnterior.getString("telefono"),
                    docAnterior.getString("email"),
                    docAnterior.getString("direccion")
                );
            }
            
            Document filtro = new Document("_id", c.getId());
            Document update = new Document("$set", new Document()
                    .append("nombre", c.getNombre())
                    .append("telefono", c.getTelefono())
                    .append("email", c.getEmail())
                    .append("direccion", c.getDireccion()));
            
            collection.updateOne(filtro, update);
            
            // Auditoría
            if (anterior != null) {
                AuditoriaService.registrarActualizacion("clientes", String.valueOf(c.getId()), 
                    AuditoriaService.clienteToMap(anterior), AuditoriaService.clienteToMap(c));
            }
        } catch (Exception e) {
            System.err.println("Error actualizando cliente en MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void actualizarClienteSQL(Cliente c) {
        // Obtener datos anteriores para auditoría
        Cliente anterior = obtenerClientePorId(c.getId());
        
        String sql = "UPDATE clientes SET nombre=?, telefono=?, email=?, direccion=? WHERE id=?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getTelefono());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getDireccion());
            ps.setInt(5, c.getId());
            ps.executeUpdate();
            
            // Asegurar commit
            if (!conn.getAutoCommit()) {
                conn.commit();
            }
            
            // Auditoría
            if (anterior != null) {
                AuditoriaService.registrarActualizacion("clientes", String.valueOf(c.getId()), 
                    AuditoriaService.clienteToMap(anterior), AuditoriaService.clienteToMap(c));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void eliminarCliente(int id) {
        if (esMongoDB()) {
            eliminarClienteMongo(id);
        } else {
            eliminarClienteSQL(id);
        }
    }

    private static void eliminarClienteMongo(int id) {
        try {
            MongoDatabase db = ConexionMongo.getDatabase();
            MongoCollection<Document> collection = db.getCollection("clientes");
            
            // Obtener datos anteriores
            Document docAnterior = collection.find(new Document("_id", id)).first();
            Cliente anterior = null;
            if (docAnterior != null) {
                anterior = new Cliente(
                    docAnterior.getInteger("_id", 0),
                    docAnterior.getString("nombre"),
                    docAnterior.getString("telefono"),
                    docAnterior.getString("email"),
                    docAnterior.getString("direccion")
                );
            }
            
            collection.deleteOne(new Document("_id", id));
            
            // Auditoría
            if (anterior != null) {
                AuditoriaService.registrarEliminacion("clientes", String.valueOf(id), 
                    AuditoriaService.clienteToMap(anterior));
            }
        } catch (Exception e) {
            System.err.println("Error eliminando cliente de MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void eliminarClienteSQL(int id) {
        // Obtener datos anteriores para auditoría
        Cliente anterior = obtenerClientePorId(id);
        
        String sql = "DELETE FROM clientes WHERE id=?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            
            // Asegurar commit
            if (!conn.getAutoCommit()) {
                conn.commit();
            }
            
            // Auditoría
            if (anterior != null) {
                AuditoriaService.registrarEliminacion("clientes", String.valueOf(id), 
                    AuditoriaService.clienteToMap(anterior));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean clienteTieneReferencias(int clienteId) {
        if (esMongoDB()) {
            return clienteTieneReferenciasMongo(clienteId);
        }
        return clienteTieneReferenciasSQL(clienteId);
    }

    private static boolean clienteTieneReferenciasMongo(int clienteId) {
        try {
            MongoDatabase db = ConexionMongo.getDatabase();
            
            long vehiculos = db.getCollection("vehiculos")
                    .countDocuments(new Document("cliente_id", clienteId));
            long ordenes = db.getCollection("ordenes")
                    .countDocuments(new Document("cliente_id", clienteId));
            
            return (vehiculos + ordenes) > 0;
        } catch (Exception e) {
            System.err.println("Error verificando referencias de cliente en MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private static boolean clienteTieneReferenciasSQL(int clienteId) {
        String sql = "SELECT (SELECT COUNT(*) FROM vehiculos WHERE cliente_id=?) + " +
                     "(SELECT COUNT(*) FROM ordenes WHERE cliente_id=?) AS total";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clienteId);
            ps.setInt(2, clienteId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static int obtenerSiguienteIdMongo(String coleccion) {
        try {
            MongoDatabase db = ConexionMongo.getDatabase();
            MongoCollection<Document> collection = db.getCollection(coleccion);
            
            Document ultimo = collection.find()
                    .sort(new Document("_id", -1))
                    .limit(1)
                    .first();
            
            if (ultimo != null) {
                return ultimo.getInteger("_id", 0) + 1;
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo siguiente ID: " + e.getMessage());
        }
        return 1;
    }

    // ==================== VEHICULOS ====================

    public static ArrayList<Vehiculo> cargarVehiculos() {
        if (esMongoDB()) {
            return cargarVehiculosMongo();
        }
        return cargarVehiculosSQL();
    }

    private static ArrayList<Vehiculo> cargarVehiculosMongo() {
        ArrayList<Vehiculo> lista = new ArrayList<>();
        try {
            MongoDatabase db = ConexionMongo.getDatabase();
            MongoCollection<Document> collection = db.getCollection("vehiculos");
            
            for (Document doc : collection.find().sort(new Document("_id", 1))) {
                Vehiculo v = new Vehiculo(
                        doc.getInteger("_id", 0),
                        doc.getString("placa"),
                        doc.getString("marca"),
                        doc.getString("modelo"),
                        doc.getInteger("anio", 0),
                        doc.getInteger("cliente_id", 0)
                );
                lista.add(v);
            }
        } catch (Exception e) {
            System.err.println("Error cargando vehículos de MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    private static ArrayList<Vehiculo> cargarVehiculosSQL() {
        ArrayList<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT id, placa, marca, modelo, anio, cliente_id FROM vehiculos ORDER BY id";
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Vehiculo v = new Vehiculo(
                        rs.getInt("id"),
                        rs.getString("placa"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getInt("anio"),
                        rs.getInt("cliente_id")
                );
                lista.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static Vehiculo insertarVehiculo(Vehiculo v) {
        if (esMongoDB()) {
            return insertarVehiculoMongo(v);
        }
        return insertarVehiculoSQL(v);
    }

    private static Vehiculo insertarVehiculoMongo(Vehiculo v) {
        try {
            MongoDatabase db = ConexionMongo.getDatabase();
            MongoCollection<Document> collection = db.getCollection("vehiculos");
            
            int nuevoId = obtenerSiguienteIdMongo("vehiculos");
            
            Document doc = new Document()
                    .append("_id", nuevoId)
                    .append("placa", v.getPlaca())
                    .append("marca", v.getMarca())
                    .append("modelo", v.getModelo())
                    .append("anio", v.getAnio())
                    .append("cliente_id", v.getClienteId());
            
            collection.insertOne(doc);
            v.setId(nuevoId);
            
            // Auditoría
            AuditoriaService.registrarCreacion("vehiculos", String.valueOf(nuevoId), AuditoriaService.vehiculoToMap(v));
        } catch (Exception e) {
            System.err.println("Error insertando vehículo en MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
        return v;
    }

    private static Vehiculo insertarVehiculoSQL(Vehiculo v) {
        String sql = "INSERT INTO vehiculos (placa, marca, modelo, anio, cliente_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setInt(4, v.getAnio());
            ps.setInt(5, v.getClienteId());
            ps.executeUpdate();
            
            // Asegurar commit
            if (!conn.getAutoCommit()) {
                conn.commit();
            }
            
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                v.setId(keys.getInt(1));
            }
            
            // Auditoría
            AuditoriaService.registrarCreacion("vehiculos", String.valueOf(v.getId()), AuditoriaService.vehiculoToMap(v));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return v;
    }

    public static void actualizarVehiculo(Vehiculo v) {
        if (esMongoDB()) {
            actualizarVehiculoMongo(v);
        } else {
            actualizarVehiculoSQL(v);
        }
    }

    private static void actualizarVehiculoMongo(Vehiculo v) {
        try {
            MongoDatabase db = ConexionMongo.getDatabase();
            MongoCollection<Document> collection = db.getCollection("vehiculos");
            
            // Obtener datos anteriores
            Document docAnterior = collection.find(new Document("_id", v.getId())).first();
            Vehiculo anterior = null;
            if (docAnterior != null) {
                anterior = new Vehiculo(
                    docAnterior.getInteger("_id", 0),
                    docAnterior.getString("placa"),
                    docAnterior.getString("marca"),
                    docAnterior.getString("modelo"),
                    docAnterior.getInteger("anio", 0),
                    docAnterior.getInteger("cliente_id", 0)
                );
            }
            
            Document filtro = new Document("_id", v.getId());
            Document update = new Document("$set", new Document()
                    .append("placa", v.getPlaca())
                    .append("marca", v.getMarca())
                    .append("modelo", v.getModelo())
                    .append("anio", v.getAnio())
                    .append("cliente_id", v.getClienteId()));
            
            collection.updateOne(filtro, update);
            
            // Auditoría
            if (anterior != null) {
                AuditoriaService.registrarActualizacion("vehiculos", String.valueOf(v.getId()), 
                    AuditoriaService.vehiculoToMap(anterior), AuditoriaService.vehiculoToMap(v));
            }
        } catch (Exception e) {
            System.err.println("Error actualizando vehículo en MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void actualizarVehiculoSQL(Vehiculo v) {
        // Obtener datos anteriores
        Vehiculo anterior = obtenerVehiculoPorId(v.getId());
        
        String sql = "UPDATE vehiculos SET placa=?, marca=?, modelo=?, anio=?, cliente_id=? WHERE id=?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setInt(4, v.getAnio());
            ps.setInt(5, v.getClienteId());
            ps.setInt(6, v.getId());
            ps.executeUpdate();
            
            // Asegurar commit
            if (!conn.getAutoCommit()) {
                conn.commit();
            }
            
            // Auditoría
            if (anterior != null) {
                AuditoriaService.registrarActualizacion("vehiculos", String.valueOf(v.getId()), 
                    AuditoriaService.vehiculoToMap(anterior), AuditoriaService.vehiculoToMap(v));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void eliminarVehiculo(int id) {
        if (esMongoDB()) {
            eliminarVehiculoMongo(id);
        } else {
            eliminarVehiculoSQL(id);
        }
    }

    private static void eliminarVehiculoMongo(int id) {
        try {
            MongoDatabase db = ConexionMongo.getDatabase();
            MongoCollection<Document> collection = db.getCollection("vehiculos");
            
            // Obtener datos anteriores
            Document docAnterior = collection.find(new Document("_id", id)).first();
            Vehiculo anterior = null;
            if (docAnterior != null) {
                anterior = new Vehiculo(
                    docAnterior.getInteger("_id", 0),
                    docAnterior.getString("placa"),
                    docAnterior.getString("marca"),
                    docAnterior.getString("modelo"),
                    docAnterior.getInteger("anio", 0),
                    docAnterior.getInteger("cliente_id", 0)
                );
            }
            
            collection.deleteOne(new Document("_id", id));
            
            // Auditoría
            if (anterior != null) {
                AuditoriaService.registrarEliminacion("vehiculos", String.valueOf(id), 
                    AuditoriaService.vehiculoToMap(anterior));
            }
        } catch (Exception e) {
            System.err.println("Error eliminando vehículo de MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void eliminarVehiculoSQL(int id) {
        // Obtener datos anteriores
        Vehiculo anterior = obtenerVehiculoPorId(id);
        
        String sql = "DELETE FROM vehiculos WHERE id=?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            
            // Asegurar commit
            if (!conn.getAutoCommit()) {
                conn.commit();
            }
            
            // Auditoría
            if (anterior != null) {
                AuditoriaService.registrarEliminacion("vehiculos", String.valueOf(id), 
                    AuditoriaService.vehiculoToMap(anterior));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean vehiculoTieneOrdenes(int vehiculoId) {
        if (esMongoDB()) {
            return vehiculoTieneOrdenesMongo(vehiculoId);
        }
        return vehiculoTieneOrdenesSQL(vehiculoId);
    }

    private static boolean vehiculoTieneOrdenesMongo(int vehiculoId) {
        try {
            MongoDatabase db = ConexionMongo.getDatabase();
            long count = db.getCollection("ordenes")
                    .countDocuments(new Document("vehiculo_id", vehiculoId));
            return count > 0;
        } catch (Exception e) {
            System.err.println("Error verificando órdenes de vehículo en MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private static boolean vehiculoTieneOrdenesSQL(int vehiculoId) {
        String sql = "SELECT COUNT(*) AS total FROM ordenes WHERE vehiculo_id=?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vehiculoId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==================== ORDENES ====================

    public static ArrayList<Orden> cargarOrdenes() {
        if (esMongoDB()) {
            return cargarOrdenesMongo();
        }
        return cargarOrdenesSQL();
    }

    private static ArrayList<Orden> cargarOrdenesMongo() {
        ArrayList<Orden> lista = new ArrayList<>();
        try {
            MongoDatabase db = ConexionMongo.getDatabase();
            MongoCollection<Document> collection = db.getCollection("ordenes");
            
            for (Document doc : collection.find().sort(new Document("_id", 1))) {
                Orden o = new Orden(
                        doc.getInteger("_id", 0),
                        doc.getInteger("cliente_id", 0),
                        doc.getInteger("vehiculo_id", 0),
                        doc.getString("estado")
                );
                o.setCostoRepuestos(doc.getDouble("costo_repuestos"));
                o.setHorasTrabajo(doc.getDouble("horas_trabajo"));
                o.setCostoHora(doc.getDouble("costo_hora"));
                o.setManoObra(doc.getDouble("mano_obra"));
                o.setSubtotal(doc.getDouble("subtotal"));
                o.setIva(doc.getDouble("iva"));
                o.setTotal(doc.getDouble("total"));
                lista.add(o);
            }
        } catch (Exception e) {
            System.err.println("Error cargando órdenes de MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    private static ArrayList<Orden> cargarOrdenesSQL() {
        ArrayList<Orden> lista = new ArrayList<>();
        String sql = "SELECT id, cliente_id, vehiculo_id, estado, costo_repuestos, horas_trabajo, " +
                     "costo_hora, mano_obra, subtotal, iva, total FROM ordenes ORDER BY id";
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Orden o = new Orden(
                        rs.getInt("id"),
                        rs.getInt("cliente_id"),
                        rs.getInt("vehiculo_id"),
                        rs.getString("estado")
                );
                o.setCostoRepuestos(rs.getDouble("costo_repuestos"));
                o.setHorasTrabajo(rs.getDouble("horas_trabajo"));
                o.setCostoHora(rs.getDouble("costo_hora"));
                o.setManoObra(rs.getDouble("mano_obra"));
                o.setSubtotal(rs.getDouble("subtotal"));
                o.setIva(rs.getDouble("iva"));
                o.setTotal(rs.getDouble("total"));
                lista.add(o);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static Orden insertarOrden(Orden o) {
        if (esMongoDB()) {
            return insertarOrdenMongo(o);
        }
        return insertarOrdenSQL(o);
    }

    private static Orden insertarOrdenMongo(Orden o) {
        try {
            MongoDatabase db = ConexionMongo.getDatabase();
            MongoCollection<Document> collection = db.getCollection("ordenes");
            
            int nuevoId = obtenerSiguienteIdMongo("ordenes");
            
            Document doc = new Document()
                    .append("_id", nuevoId)
                    .append("cliente_id", o.getClienteId())
                    .append("vehiculo_id", o.getVehiculoId())
                    .append("estado", o.getEstado())
                    .append("costo_repuestos", o.getCostoRepuestos())
                    .append("horas_trabajo", o.getHorasTrabajo())
                    .append("costo_hora", o.getCostoHora())
                    .append("mano_obra", o.getManoObra())
                    .append("subtotal", o.getSubtotal())
                    .append("iva", o.getIva())
                    .append("total", o.getTotal());
            
            collection.insertOne(doc);
            o.setId(nuevoId);
            
            // Auditoría
            AuditoriaService.registrarCreacion("ordenes", String.valueOf(nuevoId), AuditoriaService.ordenToMap(o));
        } catch (Exception e) {
            System.err.println("Error insertando orden en MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
        return o;
    }

    private static Orden insertarOrdenSQL(Orden o) {
        String sql = "INSERT INTO ordenes (cliente_id, vehiculo_id, estado, costo_repuestos, " +
                     "horas_trabajo, costo_hora, mano_obra, subtotal, iva, total) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, o.getClienteId());
            ps.setInt(2, o.getVehiculoId());
            ps.setString(3, o.getEstado());
            ps.setDouble(4, o.getCostoRepuestos());
            ps.setDouble(5, o.getHorasTrabajo());
            ps.setDouble(6, o.getCostoHora());
            ps.setDouble(7, o.getManoObra());
            ps.setDouble(8, o.getSubtotal());
            ps.setDouble(9, o.getIva());
            ps.setDouble(10, o.getTotal());
            ps.executeUpdate();
            
            // Asegurar commit
            if (!conn.getAutoCommit()) {
                conn.commit();
            }
            
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                o.setId(keys.getInt(1));
            }
            
            // Auditoría
            AuditoriaService.registrarCreacion("ordenes", String.valueOf(o.getId()), AuditoriaService.ordenToMap(o));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return o;
    }

    public static void actualizarOrden(Orden o) {
        if (esMongoDB()) {
            actualizarOrdenMongo(o);
        } else {
            actualizarOrdenSQL(o);
        }
    }

    private static void actualizarOrdenMongo(Orden o) {
        try {
            MongoDatabase db = ConexionMongo.getDatabase();
            MongoCollection<Document> collection = db.getCollection("ordenes");
            
            // Obtener datos anteriores
            Document docAnterior = collection.find(new Document("_id", o.getId())).first();
            Orden anterior = null;
            if (docAnterior != null) {
                anterior = new Orden(
                    docAnterior.getInteger("_id", 0),
                    docAnterior.getInteger("cliente_id", 0),
                    docAnterior.getInteger("vehiculo_id", 0),
                    docAnterior.getString("estado")
                );
                anterior.setCostoRepuestos(docAnterior.getDouble("costo_repuestos"));
                anterior.setHorasTrabajo(docAnterior.getDouble("horas_trabajo"));
                anterior.setCostoHora(docAnterior.getDouble("costo_hora"));
                anterior.setManoObra(docAnterior.getDouble("mano_obra"));
                anterior.setSubtotal(docAnterior.getDouble("subtotal"));
                anterior.setIva(docAnterior.getDouble("iva"));
                anterior.setTotal(docAnterior.getDouble("total"));
            }
            
            Document filtro = new Document("_id", o.getId());
            Document update = new Document("$set", new Document()
                    .append("cliente_id", o.getClienteId())
                    .append("vehiculo_id", o.getVehiculoId())
                    .append("estado", o.getEstado())
                    .append("costo_repuestos", o.getCostoRepuestos())
                    .append("horas_trabajo", o.getHorasTrabajo())
                    .append("costo_hora", o.getCostoHora())
                    .append("mano_obra", o.getManoObra())
                    .append("subtotal", o.getSubtotal())
                    .append("iva", o.getIva())
                    .append("total", o.getTotal()));
            
            collection.updateOne(filtro, update);
            
            // Auditoría
            if (anterior != null) {
                AuditoriaService.registrarActualizacion("ordenes", String.valueOf(o.getId()), 
                    AuditoriaService.ordenToMap(anterior), AuditoriaService.ordenToMap(o));
            }
        } catch (Exception e) {
            System.err.println("Error actualizando orden en MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void actualizarOrdenSQL(Orden o) {
        // Obtener datos anteriores
        Orden anterior = obtenerOrdenPorId(o.getId());
        
        String sql = "UPDATE ordenes SET cliente_id=?, vehiculo_id=?, estado=?, costo_repuestos=?, " +
                     "horas_trabajo=?, costo_hora=?, mano_obra=?, subtotal=?, iva=?, total=? WHERE id=?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, o.getClienteId());
            ps.setInt(2, o.getVehiculoId());
            ps.setString(3, o.getEstado());
            ps.setDouble(4, o.getCostoRepuestos());
            ps.setDouble(5, o.getHorasTrabajo());
            ps.setDouble(6, o.getCostoHora());
            ps.setDouble(7, o.getManoObra());
            ps.setDouble(8, o.getSubtotal());
            ps.setDouble(9, o.getIva());
            ps.setDouble(10, o.getTotal());
            ps.setInt(11, o.getId());
            ps.executeUpdate();
            
            // Asegurar commit
            if (!conn.getAutoCommit()) {
                conn.commit();
            }
            
            // Auditoría
            if (anterior != null) {
                AuditoriaService.registrarActualizacion("ordenes", String.valueOf(o.getId()), 
                    AuditoriaService.ordenToMap(anterior), AuditoriaService.ordenToMap(o));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void eliminarOrden(int id) {
        if (esMongoDB()) {
            eliminarOrdenMongo(id);
        } else {
            eliminarOrdenSQL(id);
        }
    }

    private static void eliminarOrdenMongo(int id) {
        try {
            MongoDatabase db = ConexionMongo.getDatabase();
            MongoCollection<Document> collection = db.getCollection("ordenes");
            
            // Obtener datos anteriores
            Document docAnterior = collection.find(new Document("_id", id)).first();
            Orden anterior = null;
            if (docAnterior != null) {
                anterior = new Orden(
                    docAnterior.getInteger("_id", 0),
                    docAnterior.getInteger("cliente_id", 0),
                    docAnterior.getInteger("vehiculo_id", 0),
                    docAnterior.getString("estado")
                );
                anterior.setCostoRepuestos(docAnterior.getDouble("costo_repuestos"));
                anterior.setHorasTrabajo(docAnterior.getDouble("horas_trabajo"));
                anterior.setCostoHora(docAnterior.getDouble("costo_hora"));
                anterior.setManoObra(docAnterior.getDouble("mano_obra"));
                anterior.setSubtotal(docAnterior.getDouble("subtotal"));
                anterior.setIva(docAnterior.getDouble("iva"));
                anterior.setTotal(docAnterior.getDouble("total"));
            }
            
            collection.deleteOne(new Document("_id", id));
            
            // Auditoría
            if (anterior != null) {
                AuditoriaService.registrarEliminacion("ordenes", String.valueOf(id), 
                    AuditoriaService.ordenToMap(anterior));
            }
        } catch (Exception e) {
            System.err.println("Error eliminando orden de MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void eliminarOrdenSQL(int id) {
        // Obtener datos anteriores
        Orden anterior = obtenerOrdenPorId(id);
        
        String sql = "DELETE FROM ordenes WHERE id=?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            
            // Asegurar commit
            if (!conn.getAutoCommit()) {
                conn.commit();
            }
            
            // Auditoría
            if (anterior != null) {
                AuditoriaService.registrarEliminacion("ordenes", String.valueOf(id), 
                    AuditoriaService.ordenToMap(anterior));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // ==================== MÉTODOS AUXILIARES PARA AUDITORÍA ====================
    
    private static Cliente obtenerClientePorId(int id) {
        if (esMongoDB()) {
            return obtenerClientePorIdMongo(id);
        }
        return obtenerClientePorIdSQL(id);
    }
    
    private static Cliente obtenerClientePorIdSQL(int id) {
        String sql = "SELECT id, nombre, telefono, email, direccion FROM clientes WHERE id=?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static Cliente obtenerClientePorIdMongo(int id) {
        try {
            MongoDatabase db = ConexionMongo.getDatabase();
            MongoCollection<Document> collection = db.getCollection("clientes");
            Document doc = collection.find(new Document("_id", id)).first();
            if (doc != null) {
                return new Cliente(
                    doc.getInteger("_id", 0),
                    doc.getString("nombre"),
                    doc.getString("telefono"),
                    doc.getString("email"),
                    doc.getString("direccion")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static Vehiculo obtenerVehiculoPorId(int id) {
        if (esMongoDB()) {
            return obtenerVehiculoPorIdMongo(id);
        }
        return obtenerVehiculoPorIdSQL(id);
    }
    
    private static Vehiculo obtenerVehiculoPorIdSQL(int id) {
        String sql = "SELECT id, placa, marca, modelo, anio, cliente_id FROM vehiculos WHERE id=?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Vehiculo(
                    rs.getInt("id"),
                    rs.getString("placa"),
                    rs.getString("marca"),
                    rs.getString("modelo"),
                    rs.getInt("anio"),
                    rs.getInt("cliente_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static Vehiculo obtenerVehiculoPorIdMongo(int id) {
        try {
            MongoDatabase db = ConexionMongo.getDatabase();
            MongoCollection<Document> collection = db.getCollection("vehiculos");
            Document doc = collection.find(new Document("_id", id)).first();
            if (doc != null) {
                return new Vehiculo(
                    doc.getInteger("_id", 0),
                    doc.getString("placa"),
                    doc.getString("marca"),
                    doc.getString("modelo"),
                    doc.getInteger("anio", 0),
                    doc.getInteger("cliente_id", 0)
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static Orden obtenerOrdenPorId(int id) {
        if (esMongoDB()) {
            return obtenerOrdenPorIdMongo(id);
        }
        return obtenerOrdenPorIdSQL(id);
    }
    
    private static Orden obtenerOrdenPorIdSQL(int id) {
        String sql = "SELECT id, cliente_id, vehiculo_id, estado, costo_repuestos, horas_trabajo, " +
                     "costo_hora, mano_obra, subtotal, iva, total FROM ordenes WHERE id=?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Orden o = new Orden(
                    rs.getInt("id"),
                    rs.getInt("cliente_id"),
                    rs.getInt("vehiculo_id"),
                    rs.getString("estado")
                );
                o.setCostoRepuestos(rs.getDouble("costo_repuestos"));
                o.setHorasTrabajo(rs.getDouble("horas_trabajo"));
                o.setCostoHora(rs.getDouble("costo_hora"));
                o.setManoObra(rs.getDouble("mano_obra"));
                o.setSubtotal(rs.getDouble("subtotal"));
                o.setIva(rs.getDouble("iva"));
                o.setTotal(rs.getDouble("total"));
                return o;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static Orden obtenerOrdenPorIdMongo(int id) {
        try {
            MongoDatabase db = ConexionMongo.getDatabase();
            MongoCollection<Document> collection = db.getCollection("ordenes");
            Document doc = collection.find(new Document("_id", id)).first();
            if (doc != null) {
                Orden o = new Orden(
                    doc.getInteger("_id", 0),
                    doc.getInteger("cliente_id", 0),
                    doc.getInteger("vehiculo_id", 0),
                    doc.getString("estado")
                );
                o.setCostoRepuestos(doc.getDouble("costo_repuestos"));
                o.setHorasTrabajo(doc.getDouble("horas_trabajo"));
                o.setCostoHora(doc.getDouble("costo_hora"));
                o.setManoObra(doc.getDouble("mano_obra"));
                o.setSubtotal(doc.getDouble("subtotal"));
                o.setIva(doc.getDouble("iva"));
                o.setTotal(doc.getDouble("total"));
                return o;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

