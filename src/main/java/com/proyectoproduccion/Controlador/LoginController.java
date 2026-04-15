package com.proyectoproduccion.Controlador;

import com.proyectoproduccion.Util.Conexion;
import com.proyectoproduccion.Util.ConfigDB;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

public class LoginController {

    @FXML
    private TextField fieldUsuario;

    @FXML
    private PasswordField fieldPassword;

    @FXML
    private TextField fieldHost;

    @FXML
    private TextField fieldPort;

    @FXML
    private void initialize() {
        // Cargar valores desde database.properties
        if (fieldHost != null) {
            fieldHost.setText(ConfigDB.getHost());
        }
        if (fieldPort != null) {
            fieldPort.setText(ConfigDB.getPort());
        }
        if (fieldUsuario != null) {
            fieldUsuario.setText(ConfigDB.getUser());
        }
        if (fieldPassword != null) {
            fieldPassword.setText(ConfigDB.getPassword());
        }
    }

    @FXML
    private void login() {

        String user = fieldUsuario.getText().trim();
        String pass = fieldPassword.getText();
        String host = fieldHost != null ? fieldHost.getText().trim() : "localhost";
        String port = fieldPort != null ? fieldPort.getText().trim() : "3306";

        if (user.isEmpty()) {
            mostrarError("Ingrese el usuario de la base de datos");
            return;
        }

        if (host.isEmpty()) {
            host = "localhost";
        }

        if (port.isEmpty()) {
            port = "3306";
        }

        // Intentar conectar a MySQL con las credenciales ingresadas
        if (Conexion.probarConexion(host, port, user, pass)) {

            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/proyectoproduccion/layout.fxml")
                );

                Stage stage = (Stage) fieldUsuario.getScene().getWindow();
                stage.setScene(new Scene(loader.load()));
                stage.setMaximized(true);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            mostrarError("No se pudo conectar a la base de datos.\n\n" +
                        "Configuración actual:\n" +
                        "Host: " + host + "\n" +
                        "Puerto: " + port + "\n" +
                        "Usuario: " + user + "\n\n" +
                        "Verifica que:\n" +
                        "• MySQL esté corriendo\n" +
                        "• El host y puerto sean correctos\n" +
                        "• Las credenciales sean válidas\n" +
                        "• La base de datos 'taller_db' exista");

        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de conexión");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}