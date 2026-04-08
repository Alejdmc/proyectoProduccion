package com.proyectoproduccion.Util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import java.net.URL;

public class SceneManager {

    public static BorderPane mainLayout;

    public static void setMainLayout(BorderPane layout) {
        mainLayout = layout;
    }

    public static void loadView(String fxml) {

        try {
            if (mainLayout == null) {
                throw new IllegalStateException("mainLayout no esta inicializado en SceneManager");
            }

            URL resource = SceneManager.class.getResource("/com/proyectoproduccion/" + fxml);
            if (resource == null) {
                throw new IllegalArgumentException("No se encontro el archivo FXML: " + fxml);
            }

            FXMLLoader loader = new FXMLLoader(resource);

            Node view = loader.load();

            mainLayout.setCenter(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}