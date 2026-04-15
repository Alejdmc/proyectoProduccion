package com.proyectoproduccion;

import com.proyectoproduccion.Util.Conexion;
import com.proyectoproduccion.Util.ConfigDB;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Mostrar la configuración que se está usando
        System.out.println("\n===========================================");
        System.out.println("  INICIANDO SISTEMA DE TALLER");
        System.out.println("===========================================\n");
        ConfigDB.mostrarConfiguracion();
        System.out.println();

        // Probar la conexión con la configuración del archivo
        System.out.println("Probando conexión a la base de datos...");
        
        if (Conexion.probarConexion()) {
            // Conexión exitosa - ir directo al layout principal
            System.out.println("✓ Conexión exitosa!\n");
            
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/proyectoproduccion/layout.fxml")
            );
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("Sistema de Taller - " + ConfigDB.getDatabase());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
            
        } else {
            // Error de conexión - mostrar mensaje y login manual
            System.err.println("✗ No se pudo conectar con la configuración de database.properties\n");
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Conexión");
            alert.setHeaderText("No se pudo conectar a MySQL");
            alert.setContentText(
                "La aplicación no pudo conectarse a la base de datos.\n\n" +
                "Configuración actual:\n" +
                "• Host: " + ConfigDB.getHost() + "\n" +
                "• Puerto: " + ConfigDB.getPort() + "\n" +
                "• Usuario: " + ConfigDB.getUser() + "\n\n" +
                "Verifica que:\n" +
                "1. MySQL esté ejecutándose\n" +
                "2. Las credenciales en database.properties sean correctas\n" +
                "3. La base de datos '" + ConfigDB.getDatabase() + "' exista\n\n" +
                "Edita el archivo database.properties en la raíz del proyecto."
            );
            alert.showAndWait();
            
            // Cargar pantalla de login manual como fallback
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

