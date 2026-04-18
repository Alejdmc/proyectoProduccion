package com.proyectoproduccion;

import com.proyectoproduccion.Util.Conexion;
import com.proyectoproduccion.Util.ConfigDB;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Mostrar diálogo para elegir base de datos
        ButtonType btnMySQL = new ButtonType("MySQL", ButtonBar.ButtonData.LEFT);
        ButtonType btnSQLite = new ButtonType("SQLite", ButtonBar.ButtonData.RIGHT);

        Alert seleccion = new Alert(Alert.AlertType.CONFIRMATION);
        seleccion.setTitle("Sistema de Taller");
        seleccion.setHeaderText("Seleccionar Base de Datos");
        seleccion.setContentText("MySQL: Requiere servidor MySQL activo\nSQLite: Base de datos local (sin servidor)");
        seleccion.getButtonTypes().setAll(btnMySQL, btnSQLite);

        Optional<ButtonType> resultado = seleccion.showAndWait();

        if (resultado.isEmpty()) {
            System.exit(0);
            return;
        }

        if (resultado.get() == btnSQLite) {
            ConfigDB.setDbType("sqlite");
        } else {
            ConfigDB.setDbType("mysql");
        }

        System.out.println("Base de datos seleccionada: " + ConfigDB.getDbType().toUpperCase());
        ConfigDB.mostrarConfiguracion();

        // Probar conexión
        if (Conexion.probarConexion()) {
            System.out.println("Conexion exitosa");

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/proyectoproduccion/layout.fxml")
            );
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("Sistema de Taller - " + ConfigDB.getDbType().toUpperCase());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } else {
            System.err.println("Error de conexion");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Conexion");
            alert.setHeaderText("No se pudo conectar a " + ConfigDB.getDbType().toUpperCase());

            if ("mysql".equals(ConfigDB.getDbType())) {
                alert.setContentText(
                    "Verifica que:\n" +
                    "1. MySQL este ejecutandose\n" +
                    "2. Los datos en database.properties sean correctos\n" +
                    "3. La base de datos 'taller_db' exista\n\n" +
                    "Host: " + ConfigDB.getHost() + "\n" +
                    "Usuario: " + ConfigDB.getUser()
                );
            } else {
                alert.setContentText(
                    "No se pudo acceder al archivo SQLite.\n" +
                    "Verifica que el archivo taller_db.sqlite exista."
                );
            }
            alert.showAndWait();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/proyectoproduccion/login.fxml")
            );
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("Sistema de Taller - Login");
            stage.setScene(scene);
            stage.show();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
