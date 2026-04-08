package com.proyectoproduccion.Controlador;

import com.proyectoproduccion.Modelo.Cliente;
import com.proyectoproduccion.Modelo.Orden;
import com.proyectoproduccion.Modelo.Vehiculo;
import com.proyectoproduccion.Util.DatabaseUtil;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrdenesController {

    @FXML
    private TableView<Orden> tablaOrdenes;

    @FXML
    private TableColumn<Orden, Integer> colId;

    @FXML
    private TableColumn<Orden, String> colCliente;

    @FXML
    private TableColumn<Orden, String> colVehiculo;

    @FXML
    private TableColumn<Orden, String> colEstado;

    @FXML
    private ComboBox<Cliente> comboCliente;

    @FXML
    private ComboBox<Vehiculo> comboVehiculo;

    @FXML
    private ComboBox<String> comboEstado;

    @FXML
    private TextField fieldRepuestos;

    @FXML
    private TextField fieldHoras;

    @FXML
    private TextField fieldCostoHora;

    @FXML
    private Label labelManoObra;

    @FXML
    private Label labelSubtotal;

    @FXML
    private Label labelTotal;

    @FXML
    private Label labelPromedio;

    private ArrayList<Orden> listaOrdenes = new ArrayList<>();
    private ArrayList<Cliente> listaClientes = new ArrayList<>();
    private ArrayList<Vehiculo> listaVehiculos = new ArrayList<>();
    private ObservableList<Orden> datosTabla = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        listaClientes = DatabaseUtil.cargarClientes();
        listaVehiculos = DatabaseUtil.cargarVehiculos();

        // Configurar columnas de la tabla
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        colCliente.setCellValueFactory(cellData -> {
            int clienteId = cellData.getValue().getClienteId();
            String nombre = buscarNombreCliente(clienteId);
            return new SimpleStringProperty(nombre);
        });

        colVehiculo.setCellValueFactory(cellData -> {
            int vehiculoId = cellData.getValue().getVehiculoId();
            String placa = buscarPlacaVehiculo(vehiculoId);
            return new SimpleStringProperty(placa);
        });

        refrescarTabla();

        // Configurar ComboBox de clientes
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

        comboCliente.setOnAction(e -> filtrarVehiculosPorCliente());

        // Configurar ComboBox de vehículos
        comboVehiculo.setItems(FXCollections.observableArrayList(listaVehiculos));
        comboVehiculo.setConverter(new StringConverter<Vehiculo>() {
            @Override
            public String toString(Vehiculo v) {
                return v != null ? v.getPlaca() + " (" + v.getMarca() + " " + v.getModelo() + ")" : "";
            }

            @Override
            public Vehiculo fromString(String s) {
                return null;
            }
        });
    }

    private void refrescarTabla() {
        listaClientes = DatabaseUtil.cargarClientes();
        listaVehiculos = DatabaseUtil.cargarVehiculos();
        listaOrdenes = DatabaseUtil.cargarOrdenes();
        datosTabla.setAll(listaOrdenes);
        tablaOrdenes.setItems(datosTabla);
    }

    private void filtrarVehiculosPorCliente() {
        Cliente seleccionado = comboCliente.getValue();
        if (seleccionado != null) {
            List<Vehiculo> filtrados = listaVehiculos.stream()
                    .filter(v -> v.getClienteId() == seleccionado.getId())
                    .collect(Collectors.toList());
            comboVehiculo.setItems(FXCollections.observableArrayList(filtrados));
        } else {
            comboVehiculo.setItems(FXCollections.observableArrayList(listaVehiculos));
        }
        comboVehiculo.setValue(null);
    }

    private String buscarNombreCliente(int clienteId) {
        return listaClientes.stream()
                .filter(c -> c.getId() == clienteId)
                .map(Cliente::getNombre)
                .findFirst().orElse("Cliente #" + clienteId);
    }

    private String buscarPlacaVehiculo(int vehiculoId) {
        return listaVehiculos.stream()
                .filter(v -> v.getId() == vehiculoId)
                .map(Vehiculo::getPlaca)
                .findFirst().orElse("Vehículo #" + vehiculoId);
    }

    @FXML
    private void agregarOrden() {
        Cliente cliente = comboCliente.getValue();
        Vehiculo vehiculo = comboVehiculo.getValue();
        String estado = comboEstado.getValue();

        if (cliente == null || vehiculo == null || estado == null) {
            mostrarAlerta("Seleccione cliente, vehículo y estado");
            return;
        }

        Orden nueva = new Orden(0, cliente.getId(), vehiculo.getId(), estado);
        DatabaseUtil.insertarOrden(nueva);
        refrescarTabla();
        limpiarCampos();
    }

    @FXML
    private void eliminarOrden() {
        Orden seleccionada = tablaOrdenes.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAlerta("Seleccione una orden para eliminar");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro de eliminar la orden #" + seleccionada.getId() + "?");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            DatabaseUtil.eliminarOrden(seleccionada.getId());
            refrescarTabla();
            limpiarCampos();
            limpiarCalculos();
        }
    }

    @FXML
    private void editarOrden() {
        Orden seleccionada = tablaOrdenes.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAlerta("Seleccione una orden para editar");
            return;
        }

        Cliente cliente = comboCliente.getValue();
        Vehiculo vehiculo = comboVehiculo.getValue();
        String estado = comboEstado.getValue();

        if (cliente == null || vehiculo == null || estado == null) {
            mostrarAlerta("Seleccione cliente, vehículo y estado para editar");
            return;
        }

        seleccionada.setClienteId(cliente.getId());
        seleccionada.setVehiculoId(vehiculo.getId());
        seleccionada.setEstado(estado);

        DatabaseUtil.actualizarOrden(seleccionada);
        refrescarTabla();
        limpiarCampos();

        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Éxito");
        info.setHeaderText(null);
        info.setContentText("Orden actualizada correctamente");
        info.showAndWait();
    }

    @FXML
    private void seleccionarOrden() {
        Orden seleccionada = tablaOrdenes.getSelectionModel().getSelectedItem();

        if (seleccionada != null) {
            listaClientes.stream()
                    .filter(c -> c.getId() == seleccionada.getClienteId())
                    .findFirst()
                    .ifPresent(c -> comboCliente.setValue(c));

            filtrarVehiculosPorCliente();
            listaVehiculos.stream()
                    .filter(v -> v.getId() == seleccionada.getVehiculoId())
                    .findFirst()
                    .ifPresent(v -> comboVehiculo.setValue(v));

            comboEstado.setValue(seleccionada.getEstado());

            if (seleccionada.getCostoRepuestos() > 0) {
                fieldRepuestos.setText(String.valueOf(seleccionada.getCostoRepuestos()));
            }
            if (seleccionada.getHorasTrabajo() > 0) {
                fieldHoras.setText(String.valueOf(seleccionada.getHorasTrabajo()));
            }
            if (seleccionada.getCostoHora() > 0) {
                fieldCostoHora.setText(String.valueOf(seleccionada.getCostoHora()));
            }

            if (seleccionada.getTotal() > 0) {
                labelManoObra.setText(String.format("$%.2f", seleccionada.getManoObra()));
                labelSubtotal.setText(String.format("$%.2f", seleccionada.getSubtotal()));
                labelTotal.setText(String.format("$%.2f", seleccionada.getTotal()));
                double promedio = (seleccionada.getCostoRepuestos() + seleccionada.getManoObra() + seleccionada.getTotal()) / 3;
                labelPromedio.setText(String.format("$%.2f", promedio));
            } else {
                limpiarCalculos();
            }
        }
    }

    @FXML
    private void calcularPrecio() {
        Orden seleccionada = tablaOrdenes.getSelectionModel().getSelectedItem();

        try {
            double repuestos = Double.parseDouble(fieldRepuestos.getText());
            double horas = Double.parseDouble(fieldHoras.getText());
            double costoHora = Double.parseDouble(fieldCostoHora.getText());

            double manoObra = horas * costoHora;
            double subtotal = repuestos + manoObra;
            double iva = subtotal * 0.19;
            double total = subtotal + iva;

            double promedio = (repuestos + manoObra + total) / 3;

            labelManoObra.setText(String.format("$%.2f", manoObra));
            labelSubtotal.setText(String.format("$%.2f", subtotal));
            labelTotal.setText(String.format("$%.2f", total));
            labelPromedio.setText(String.format("$%.2f", promedio));

            if (seleccionada != null) {
                seleccionada.setCostoRepuestos(repuestos);
                seleccionada.setHorasTrabajo(horas);
                seleccionada.setCostoHora(costoHora);
                seleccionada.setManoObra(manoObra);
                seleccionada.setSubtotal(subtotal);
                seleccionada.setIva(iva);
                seleccionada.setTotal(total);

                DatabaseUtil.actualizarOrden(seleccionada);

                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Cálculo guardado");
                info.setHeaderText(null);
                info.setContentText("Los costos han sido guardados en la orden #" + seleccionada.getId());
                info.showAndWait();
            } else {
                mostrarAlerta("Seleccione una orden para guardar los cálculos");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Ingrese valores numéricos válidos en los campos de costos");
        }
    }

    private void limpiarCampos() {
        comboCliente.setValue(null);
        comboVehiculo.setValue(null);
        comboEstado.setValue(null);
        fieldRepuestos.clear();
        fieldHoras.clear();
        fieldCostoHora.clear();
    }

    private void limpiarCalculos() {
        labelManoObra.setText("$0.00");
        labelSubtotal.setText("$0.00");
        labelTotal.setText("$0.00");
        labelPromedio.setText("$0.00");
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}

