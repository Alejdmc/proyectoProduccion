package com.proyectoproduccion.Controlador;

import com.proyectoproduccion.Modelo.Orden;
import com.proyectoproduccion.Modelo.Cliente;
import com.proyectoproduccion.Modelo.Vehiculo;
import com.proyectoproduccion.Util.DatabaseUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.ArrayList;

public class ReportesController {

    @FXML
    private Label valorOrdenes;

    @FXML
    private Label valorProceso;

    @FXML
    private Label valorEntregadas;

    @FXML
    private Label valorClientes;

    @FXML
    private Label valorVehiculos;

    @FXML
    private Label valorIngresos;

    @FXML
    public void initialize() {
        cargarEstadisticas();
    }

    private void cargarEstadisticas() {
        ArrayList<Orden> ordenes = DatabaseUtil.cargarOrdenes();
        ArrayList<Cliente> clientes = DatabaseUtil.cargarClientes();
        ArrayList<Vehiculo> vehiculos = DatabaseUtil.cargarVehiculos();

        // Contar órdenes por estado
        int totalOrdenes = ordenes.size();
        int enProceso = 0;
        int entregadas = 0;
        double totalIngresos = 0;

        for (Orden orden : ordenes) {
            switch (orden.getEstado()) {
                case "EN_PROCESO":
                    enProceso++;
                    break;
                case "ENTREGADO":
                    entregadas++;
                    totalIngresos += orden.getTotal();
                    break;
            }
        }

        // Actualizar etiquetas
        if (valorOrdenes != null) {
            valorOrdenes.setText(String.valueOf(totalOrdenes));
        }
        if (valorProceso != null) {
            valorProceso.setText(String.valueOf(enProceso));
        }
        if (valorEntregadas != null) {
            valorEntregadas.setText(String.valueOf(entregadas));
        }
        if (valorClientes != null) {
            valorClientes.setText(String.valueOf(clientes.size()));
        }
        if (valorVehiculos != null) {
            valorVehiculos.setText(String.valueOf(vehiculos.size()));
        }
        if (valorIngresos != null) {
            valorIngresos.setText(String.format("$%.2f", totalIngresos));
        }
    }

    @FXML
    private void actualizarReportes() {
        cargarEstadisticas();
    }
}
