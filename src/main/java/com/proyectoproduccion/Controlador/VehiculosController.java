package com.proyectoproduccion.Controlador;

import com.proyectoproduccion.Modelo.Vehiculo;
import com.proyectoproduccion.Modelo.Cliente;
import com.proyectoproduccion.Util.DatabaseUtil;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.Optional;

public class VehiculosController {

    @FXML
    private TableView<Vehiculo> tablaVehiculos;

    @FXML
    private TableColumn<Vehiculo, Integer> columID;

    @FXML
    private TableColumn<Vehiculo, String> columPlaca;

    @FXML
    private TableColumn<Vehiculo, String> columMarca;

    @FXML
    private TableColumn<Vehiculo, String> columModelo;

    @FXML
    private TableColumn<Vehiculo, Integer> columAnio;

    @FXML
    private TableColumn<Vehiculo, String> btnCliente;

    @FXML
    private TextField fieldBuscarPlc;

    @FXML
    private Button btnNvoVehi;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnEliminar;

    private ArrayList<Vehiculo> listaVehiculos = new ArrayList<>();
    private ArrayList<Cliente> listaClientes = new ArrayList<>();
    private ObservableList<Vehiculo> datosTabla = FXCollections.observableArrayList();
    private FilteredList<Vehiculo> datosFiltrados;

    @FXML
    public void initialize() {
        listaClientes = DatabaseUtil.cargarClientes();

        columID.setCellValueFactory(new PropertyValueFactory<>("id"));
        columPlaca.setCellValueFactory(new PropertyValueFactory<>("placa"));
        columMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        columModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        columAnio.setCellValueFactory(new PropertyValueFactory<>("anio"));

        // Columna Cliente: lookup nombre por clienteId
        btnCliente.setCellValueFactory(cellData -> {
            int clienteId = cellData.getValue().getClienteId();
            String nombre = listaClientes.stream()
                    .filter(c -> c.getId() == clienteId)
                    .map(Cliente::getNombre)
                    .findFirst().orElse("Sin cliente");
            return new SimpleStringProperty(nombre);
        });

        refrescarTabla();

        datosFiltrados = new FilteredList<>(datosTabla, p -> true);

        fieldBuscarPlc.textProperty().addListener((observable, oldValue, newValue) -> {
            datosFiltrados.setPredicate(vehiculo -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String filtro = newValue.toLowerCase();
                return vehiculo.getPlaca().toLowerCase().contains(filtro) ||
                       vehiculo.getMarca().toLowerCase().contains(filtro) ||
                       vehiculo.getModelo().toLowerCase().contains(filtro);
            });
        });

        tablaVehiculos.setItems(datosFiltrados);

        btnNvoVehi.setOnAction(e -> agregarVehiculo());
        btnEditar.setOnAction(e -> editarVehiculo());
        btnEliminar.setOnAction(e -> eliminarVehiculo());
    }

    private void refrescarTabla() {
        listaClientes = DatabaseUtil.cargarClientes();
        listaVehiculos = DatabaseUtil.cargarVehiculos();
        datosTabla.setAll(listaVehiculos);
    }

    private void agregarVehiculo() {
        Dialog<Vehiculo> dialog = crearDialogoVehiculo(null);
        Optional<Vehiculo> resultado = dialog.showAndWait();

        resultado.ifPresent(vehiculo -> {
            DatabaseUtil.insertarVehiculo(vehiculo);
            refrescarTabla();
        });
    }

    private void editarVehiculo() {
        Vehiculo seleccionado = tablaVehiculos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Seleccione un vehículo para editar");
            return;
        }

        Dialog<Vehiculo> dialog = crearDialogoVehiculo(seleccionado);
        Optional<Vehiculo> resultado = dialog.showAndWait();

        resultado.ifPresent(vehiculo -> {
            seleccionado.setPlaca(vehiculo.getPlaca());
            seleccionado.setMarca(vehiculo.getMarca());
            seleccionado.setModelo(vehiculo.getModelo());
            seleccionado.setAnio(vehiculo.getAnio());
            seleccionado.setClienteId(vehiculo.getClienteId());
            DatabaseUtil.actualizarVehiculo(seleccionado);
            refrescarTabla();
        });
    }

    private void eliminarVehiculo() {
        Vehiculo seleccionado = tablaVehiculos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Seleccione un vehículo para eliminar");
            return;
        }

        // Integridad referencial verificada en la BD
        if (DatabaseUtil.vehiculoTieneOrdenes(seleccionado.getId())) {
            mostrarAlerta("No se puede eliminar: este vehículo tiene órdenes de servicio asociadas. Elimine las órdenes primero.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro de eliminar el vehículo con placa " + seleccionado.getPlaca() + "?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            DatabaseUtil.eliminarVehiculo(seleccionado.getId());
            refrescarTabla();
        }
    }

    private Dialog<Vehiculo> crearDialogoVehiculo(Vehiculo vehiculo) {
        Dialog<Vehiculo> dialog = new Dialog<>();
        dialog.setTitle(vehiculo == null ? "Nuevo Vehículo" : "Editar Vehículo");
        dialog.setHeaderText(null);

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setStyle("-fx-padding: 20;");

        TextField fieldPlaca = new TextField();
        fieldPlaca.setPromptText("Placa");
        TextField fieldMarca = new TextField();
        fieldMarca.setPromptText("Marca");
        TextField fieldModelo = new TextField();
        fieldModelo.setPromptText("Modelo");
        TextField fieldAnio = new TextField();
        fieldAnio.setPromptText("Año");

        ComboBox<Cliente> comboCliente = new ComboBox<>();
        comboCliente.setItems(FXCollections.observableArrayList(listaClientes));
        comboCliente.setConverter(new StringConverter<Cliente>() {
            @Override
            public String toString(Cliente c) {
                return c != null ? c.getNombre() : "";
            }

            @Override
            public Cliente fromString(String s) {
                return null;
            }
        });
        comboCliente.setPromptText("Seleccionar cliente");

        if (vehiculo != null) {
            fieldPlaca.setText(vehiculo.getPlaca());
            fieldMarca.setText(vehiculo.getMarca());
            fieldModelo.setText(vehiculo.getModelo());
            fieldAnio.setText(String.valueOf(vehiculo.getAnio()));
            listaClientes.stream()
                    .filter(c -> c.getId() == vehiculo.getClienteId())
                    .findFirst()
                    .ifPresent(comboCliente::setValue);
        }

        grid.add(new Label("Placa:"), 0, 0);
        grid.add(fieldPlaca, 1, 0);
        grid.add(new Label("Marca:"), 0, 1);
        grid.add(fieldMarca, 1, 1);
        grid.add(new Label("Modelo:"), 0, 2);
        grid.add(fieldModelo, 1, 2);
        grid.add(new Label("Año:"), 0, 3);
        grid.add(fieldAnio, 1, 3);
        grid.add(new Label("Cliente:"), 0, 4);
        grid.add(comboCliente, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                if (fieldPlaca.getText().isEmpty() || fieldMarca.getText().isEmpty()) {
                    mostrarAlerta("La placa y marca son obligatorios");
                    return null;
                }
                if (comboCliente.getValue() == null) {
                    mostrarAlerta("Debe seleccionar un cliente");
                    return null;
                }
                int anio = 0;
                try {
                    anio = Integer.parseInt(fieldAnio.getText());
                } catch (NumberFormatException e) {
                    mostrarAlerta("El año debe ser un número válido");
                    return null;
                }
                return new Vehiculo(0, fieldPlaca.getText(), fieldMarca.getText(),
                        fieldModelo.getText(), anio, comboCliente.getValue().getId());
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
