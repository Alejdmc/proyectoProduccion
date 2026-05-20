package com.proyectoproduccion.Util;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.proyectoproduccion.Modelo.Cliente;
import com.proyectoproduccion.Modelo.Vehiculo;
import com.proyectoproduccion.Modelo.Orden;
import org.bson.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class AuditoriaService {
    private static final String COLLECTION_NAME = "auditoria";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static String usuarioActual = "sistema";

    public static void setUsuarioActual(String usuario) {
        usuarioActual = usuario;
    }

    public static void registrarCreacion(String tabla, String registroId, Map<String, Object> datosNuevos) {
        try {
            guardarAuditoria("CREATE", tabla, registroId, null, datosNuevos);
        } catch (Exception e) {
            System.err.println("Error auditoría CREATE: " + e.getMessage());
        }
    }

    public static void registrarActualizacion(String tabla, String registroId, 
                                              Map<String, Object> datosAnteriores, 
                                              Map<String, Object> datosNuevos) {
        try {
            guardarAuditoria("UPDATE", tabla, registroId, datosAnteriores, datosNuevos);
        } catch (Exception e) {
            System.err.println("Error auditoría UPDATE: " + e.getMessage());
        }
    }

    public static void registrarEliminacion(String tabla, String registroId, Map<String, Object> datosAnteriores) {
        try {
            guardarAuditoria("DELETE", tabla, registroId, datosAnteriores, null);
        } catch (Exception e) {
            System.err.println("Error auditoría DELETE: " + e.getMessage());
        }
    }

    private static void guardarAuditoria(String operacion, String tabla, String registroId,
                                         Map<String, Object> datosAnteriores, Map<String, Object> datosNuevos) {
        try {
            MongoDatabase database = ConexionMongo.getDatabase();
            if (database == null) return;

            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
            
            Document doc = new Document()
                    .append("operacion", operacion)
                    .append("tabla", tabla)
                    .append("registroId", registroId)
                    .append("usuario", usuarioActual)
                    .append("timestamp", LocalDateTime.now().format(formatter));

            if (datosAnteriores != null && !datosAnteriores.isEmpty()) {
                doc.append("datosAnteriores", new Document(datosAnteriores));
            }
            if (datosNuevos != null && !datosNuevos.isEmpty()) {
                doc.append("datosNuevos", new Document(datosNuevos));
            }

            collection.insertOne(doc);
            System.out.println("✓ Auditoría: " + operacion + " en " + tabla + " #" + registroId);
        } catch (Exception e) {
            System.err.println("Error MongoDB auditoría: " + e.getMessage());
        }
    }

    public static Map<String, Object> clienteToMap(Cliente c) {
        Map<String, Object> m = new HashMap<>();
        if (c != null) {
            m.put("id", c.getId());
            m.put("nombre", c.getNombre());
            m.put("telefono", c.getTelefono());
            m.put("email", c.getEmail());
            m.put("direccion", c.getDireccion());
        }
        return m;
    }

    public static Map<String, Object> vehiculoToMap(Vehiculo v) {
        Map<String, Object> m = new HashMap<>();
        if (v != null) {
            m.put("id", v.getId());
            m.put("placa", v.getPlaca());
            m.put("marca", v.getMarca());
            m.put("modelo", v.getModelo());
            m.put("anio", v.getAnio());
            m.put("clienteId", v.getClienteId());
        }
        return m;
    }

    public static Map<String, Object> ordenToMap(Orden o) {
        Map<String, Object> m = new HashMap<>();
        if (o != null) {
            m.put("id", o.getId());
            m.put("clienteId", o.getClienteId());
            m.put("vehiculoId", o.getVehiculoId());
            m.put("estado", o.getEstado());
            m.put("costoRepuestos", o.getCostoRepuestos());
            m.put("horasTrabajo", o.getHorasTrabajo());
            m.put("costoHora", o.getCostoHora());
            m.put("manoObra", o.getManoObra());
            m.put("subtotal", o.getSubtotal());
            m.put("iva", o.getIva());
            m.put("total", o.getTotal());
        }
        return m;
    }
}




