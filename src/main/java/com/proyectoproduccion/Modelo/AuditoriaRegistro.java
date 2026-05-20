package com.proyectoproduccion.Modelo;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Modelo para registrar operaciones de auditoría en MongoDB
 */
public class AuditoriaRegistro {
    private String id;
    private String operacion; // CREATE, UPDATE, DELETE
    private String tabla; // clientes, vehiculos, ordenes
    private String usuario; // Usuario que realizó la operación
    private LocalDateTime timestamp;
    private String registroId; // ID del registro afectado
    private Map<String, Object> datosAnteriores; // Para UPDATE y DELETE
    private Map<String, Object> datosNuevos; // Para CREATE y UPDATE
    private String descripcion;

    public AuditoriaRegistro() {
        this.timestamp = LocalDateTime.now();
        this.datosAnteriores = new HashMap<>();
        this.datosNuevos = new HashMap<>();
    }

    public AuditoriaRegistro(String operacion, String tabla, String registroId) {
        this();
        this.operacion = operacion;
        this.tabla = tabla;
        this.registroId = registroId;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public String getTabla() {
        return tabla;
    }

    public void setTabla(String tabla) {
        this.tabla = tabla;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getRegistroId() {
        return registroId;
    }

    public void setRegistroId(String registroId) {
        this.registroId = registroId;
    }

    public Map<String, Object> getDatosAnteriores() {
        return datosAnteriores;
    }

    public void setDatosAnteriores(Map<String, Object> datosAnteriores) {
        this.datosAnteriores = datosAnteriores;
    }

    public Map<String, Object> getDatosNuevos() {
        return datosNuevos;
    }

    public void setDatosNuevos(Map<String, Object> datosNuevos) {
        this.datosNuevos = datosNuevos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s en %s - Registro: %s - Usuario: %s",
                timestamp, operacion, tabla, registroId, usuario);
    }
}

