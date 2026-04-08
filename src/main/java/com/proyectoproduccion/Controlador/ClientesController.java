package com.proyectoproduccion.Controlador;

import com.proyectoproduccion.Modelo.Cliente;
import com.proyectoproduccion.Util.DatabaseUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Optional;

public class ClientesController {

    @FXML
    private TableView<Cliente> tablaClientes;

    @FXML
    private TableColumn<Cliente, Integer> columID;

    @FXML
    private TableColumn<Cliente, String> columNombre;

    @FXML
    private TableColumn<Cliente, String> columTel;

    @FXML
    private TableColumn<Cliente, String> columEmail;

    @FXML
    private TableColumn<Cliente, String> colDirec;

    @FXML
    private TextField fieldBuscarCli;

    @FXML
    private Button btnNvoCliente;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnEliminar;

    private ArrayList<Cliente> listaClientes = new ArrayList<>();
    private ObservableList<Cliente> datosTabla = FXCollections.observableArrayList();
    private FilteredList<Cliente> datosFiltrados;

    @FXML
    public void initialize() {
        // Configurar columnas de la tabla
        columID.setCellValueFactory(new PropertyValueFactory<>("id"));
        columNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columTel.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        columEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colDirec.setCellValueFactory(new PropertyValueFactory<>("direccion"));

        refrescarTabla();

        // Configurar filtro de búsqueda
        datosFiltrados = new FilteredList<>(datosTabla, p -> true);

        fieldBuscarCli.textProperty().addListener((observable, oldValue, newValue) -> {
            datosFiltrados.setPredicate(cliente -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String filtro = newValue.toLowerCase();
                return cliente.getNombre().toLowerCase().contains(filtro) ||
                       cliente.getTelefono().toLowerCase().contains(filtro) ||
                       cliente.getEmail().toLowerCase().contains(filtro);
            });
        });

        tablaClientes.setItems(datosFiltrados);

        // Configurar botones
        btnNvoCliente.setOnAction(e -> agregarCliente());
        btnEditar.setOnAction(e -> editarCliente());
        btnEliminar.setOnAction(e -> eliminarCliente());
    }

    private void refrescarTabla() {
        listaClientes = DatabaseUtil.cargarClientes();
        datosTabla.setAll(listaClientes);
    }

    private void agregarCliente() {
        Dialog<Cliente> dialog = crearDialogoCliente(null);
        Optional<Cliente> resultado = dialog.showAndWait();

        resultado.ifPresent(cliente -> {
            DatabaseUtil.insertarCliente(cliente);
            refrescarTabla();
        });
    }

    private void editarCliente() {
        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Seleccione un cliente para editar");
            return;
        }

        Dialog<Cliente> dialog = crearDialogoCliente(seleccionado);
        Optional<Cliente> resultado = dialog.showAndWait();

        resultado.ifPresent(cliente -> {
            seleccionado.setNombre(cliente.getNombre());
            seleccionado.setTelefono(cliente.getTelefono());
            seleccionado.setEmail(cliente.getEmail());
            seleccionado.setDireccion(cliente.getDireccion());
            DatabaseUtil.actualizarCliente(seleccionado);
            refrescarTabla();
        });
    }

    private void eliminarCliente() {
        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Seleccione un cliente para eliminar");
            return;
        }

        // Integridad referencial verificada en la BD
        if (DatabaseUtil.clienteTieneReferencias(seleccionado.getId())) {
            mostrarAlerta("No se puede eliminar: este cliente tiene vehículos u órdenes asociadas. Elimínelos primero.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro de eliminar al cliente " + seleccionado.getNombre() + "?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            DatabaseUtil.eliminarCliente(seleccionado.getId());
            refrescarTabla();
        }
    }

    private Dialog<Cliente> crearDialogoCliente(Cliente cliente) {
        Dialog<Cliente> dialog = new Dialog<>();
        dialog.setTitle(cliente == null ? "Nuevo Cliente" : "Editar Cliente");
        dialog.setHeaderText(null);

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setStyle("-fx-padding: 20;");

        TextField fieldNombre = new TextField();
        fieldNombre.setPromptText("Nombre");
        TextField fieldTelefono = new TextField();
        fieldTelefono.setPromptText("Teléfono");
        TextField fieldEmail = new TextField();
        fieldEmail.setPromptText("Email");
        TextField fieldDireccion = new TextField();
        fieldDireccion.setPromptText("Dirección");

        if (cliente != null) {
            fieldNombre.setText(cliente.getNombre());
            fieldTelefono.setText(cliente.getTelefono());
            fieldEmail.setText(cliente.getEmail());
            fieldDireccion.setText(cliente.getDireccion());
        }

        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(fieldNombre, 1, 0);
        grid.add(new Label("Teléfono:"), 0, 1);
        grid.add(fieldTelefono, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(fieldEmail, 1, 2);
        grid.add(new Label("Dirección:"), 0, 3);
        grid.add(fieldDireccion, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                if (fieldNombre.getText().isEmpty()) {
                    mostrarAlerta("El nombre es obligatorio");
                    return null;
                }
                return new Cliente(0, fieldNombre.getText(), fieldTelefono.getText(),
                        fieldEmail.getText(), fieldDireccion.getText());
            }
            return null;
        });

        return dialog;
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
