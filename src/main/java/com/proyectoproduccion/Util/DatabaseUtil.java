 package com.proyectoproduccion.Util;

import com.proyectoproduccion.Modelo.Cliente;
import com.proyectoproduccion.Modelo.Orden;
import com.proyectoproduccion.Modelo.Vehiculo;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseUtil {

    // ==================== CLIENTES ====================

    public static ArrayList<Cliente> cargarClientes() {
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
        String sql = "INSERT INTO clientes (nombre, telefono, email, direccion) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getTelefono());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getDireccion());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                c.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

    public static void actualizarCliente(Cliente c) {
        String sql = "UPDATE clientes SET nombre=?, telefono=?, email=?, direccion=? WHERE id=?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getTelefono());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getDireccion());
            ps.setInt(5, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void eliminarCliente(int id) {
        String sql = "DELETE FROM clientes WHERE id=?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean clienteTieneReferencias(int clienteId) {
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

    // ==================== VEHICULOS ====================

    public static ArrayList<Vehiculo> cargarVehiculos() {
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
        String sql = "INSERT INTO vehiculos (placa, marca, modelo, anio, cliente_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setInt(4, v.getAnio());
            ps.setInt(5, v.getClienteId());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                v.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return v;
    }

    public static void actualizarVehiculo(Vehiculo v) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void eliminarVehiculo(int id) {
        String sql = "DELETE FROM vehiculos WHERE id=?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean vehiculoTieneOrdenes(int vehiculoId) {
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
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                o.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return o;
    }

    public static void actualizarOrden(Orden o) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void eliminarOrden(int id) {
        String sql = "DELETE FROM ordenes WHERE id=?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

