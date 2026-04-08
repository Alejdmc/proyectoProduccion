package com.proyectoproduccion.Modelo;

public class Orden {

    private int id;
    private int clienteId;
    private int vehiculoId;
    private String estado;

    // Campos para los cálculos
    private double costoRepuestos;
    private double horasTrabajo;
    private double costoHora;
    private double manoObra;
    private double subtotal;
    private double iva;
    private double total;

    public Orden(int id, int clienteId, int vehiculoId, String estado) {
        this.id = id;
        this.clienteId = clienteId;
        this.vehiculoId = vehiculoId;
        this.estado = estado;
        this.costoRepuestos = 0;
        this.horasTrabajo = 0;
        this.costoHora = 0;
        this.manoObra = 0;
        this.subtotal = 0;
        this.iva = 0;
        this.total = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public int getVehiculoId() {
        return vehiculoId;
    }

    public String getEstado() {
        return estado;
    }

    public double getCostoRepuestos() {
        return costoRepuestos;
    }

    public double getHorasTrabajo() {
        return horasTrabajo;
    }

    public double getCostoHora() {
        return costoHora;
    }

    public double getManoObra() {
        return manoObra;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getIva() {
        return iva;
    }

    public double getTotal() {
        return total;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public void setVehiculoId(int vehiculoId) {
        this.vehiculoId = vehiculoId;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setCostoRepuestos(double costoRepuestos) {
        this.costoRepuestos = costoRepuestos;
    }

    public void setHorasTrabajo(double horasTrabajo) {
        this.horasTrabajo = horasTrabajo;
    }

    public void setCostoHora(double costoHora) {
        this.costoHora = costoHora;
    }

    public void setManoObra(double manoObra) {
        this.manoObra = manoObra;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    // Método para calcular precios
    public void calcularPrecios() {
        this.manoObra = this.horasTrabajo * this.costoHora;
        this.subtotal = this.costoRepuestos + this.manoObra;
        this.iva = this.subtotal * 0.19;
        this.total = this.subtotal + this.iva;
    }
}