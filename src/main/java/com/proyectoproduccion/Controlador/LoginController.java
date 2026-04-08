package com.proyectoproduccion.Controlador;

import com.proyectoproduccion.Util.Conexion;

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
    private void login() {

        String user = fieldUsuario.getText().trim();
        String pass = fieldPassword.getText();

        if (user.isEmpty()) {
            mostrarError("Ingrese el usuario de la base de datos");
            return;
        }

        // Intentar conectar a MySQL con las credenciales ingresadas
        if (Conexion.probarConexion(user, pass)) {

            // Guardar credenciales para toda la sesión
            Conexion.setCredenciales(user, pass);

            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/proyectoproduccion/layout.fxml")
                );

                Stage stage = (Stage) fieldUsuario.getScene().getWindow();
                stage.setScene(new Scene(loader.load()));

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            mostrarError("No se pudo conectar a la base de datos.\nVerifique usuario, contraseña y que MySQL esté corriendo.");

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