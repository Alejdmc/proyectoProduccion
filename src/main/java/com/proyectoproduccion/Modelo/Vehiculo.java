package com.proyectoproduccion.Modelo;

public class Vehiculo {

    private int id;
    private String placa;
    private String marca;
    private String modelo;
    private int anio;
    private int clienteId;

    public Vehiculo(int id, String placa, String marca, String modelo, int anio, int clienteId) {
        this.id = id;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.clienteId = clienteId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public int getAnio() {
        return anio;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }
}
