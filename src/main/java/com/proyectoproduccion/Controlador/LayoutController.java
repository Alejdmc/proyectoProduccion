package com.proyectoproduccion.Controlador;

import com.proyectoproduccion.Util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class LayoutController {

    @FXML
    private BorderPane mainLayout;

    @FXML
    public void initialize() {

        SceneManager.setMainLayout(mainLayout);

        SceneManager.loadView("ordenesServicio.fxml");
    }

    @FXML
    private void irOrdenes() {
        SceneManager.loadView("ordenesServicio.fxml");
    }

    @FXML
    private void irVehiculos() {
        SceneManager.loadView("vehiculos.fxml");
    }

    @FXML
    private void irReportes() {
        SceneManager.loadView("reportes.fxml");
    }

    @FXML
    private void irClientes() {
        SceneManager.loadView("clientes.fxml");
    }
}