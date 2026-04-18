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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
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
    private TableColumn<Orden, String> colTotal;

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

        colTotal.setCellValueFactory(cellData -> {
            double total = cellData.getValue().getTotal();
            return new SimpleStringProperty(total > 0 ? String.format("$%.2f", total) : "-");
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

        // Listener para seleccionar orden al hacer clic en la tabla
        tablaOrdenes.setOnMouseClicked(e -> seleccionarOrden());

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
                refrescarTabla();
                tablaOrdenes.getSelectionModel().select(seleccionada);

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

    @FXML
    private void nuevoClienteRapido() {
        Dialog<Cliente> dialog = new Dialog<>();
        dialog.setTitle("Nuevo Cliente");
        dialog.setHeaderText("Crear un nuevo cliente");

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setStyle("-fx-padding: 20;");

        TextField fieldNombre = new TextField();
        fieldNombre.setPromptText("Nombre completo");
        TextField fieldTelefono = new TextField();
        fieldTelefono.setPromptText("Teléfono");
        TextField fieldEmail = new TextField();
        fieldEmail.setPromptText("Email");
        TextField fieldDireccion = new TextField();
        fieldDireccion.setPromptText("Dirección");

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

        dialog.showAndWait().ifPresent(cliente -> {
            DatabaseUtil.insertarCliente(cliente);
            listaClientes = DatabaseUtil.cargarClientes();
            comboCliente.setItems(FXCollections.observableArrayList(listaClientes));
            comboCliente.setValue(cliente);
            
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Cliente Creado");
            info.setHeaderText(null);
            info.setContentText("Cliente '" + cliente.getNombre() + "' creado exitosamente.");
            info.showAndWait();
        });
    }

    @FXML
    private void nuevoVehiculoRapido() {
        if (listaClientes.isEmpty()) {
            mostrarAlerta("Primero debe crear al menos un cliente");
            return;
        }

        Dialog<Vehiculo> dialog = new Dialog<>();
        dialog.setTitle("Nuevo Vehículo");
        dialog.setHeaderText("Crear un nuevo vehículo");

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setStyle("-fx-padding: 20;");

        TextField fieldPlaca = new TextField();
        fieldPlaca.setPromptText("ABC123");
        TextField fieldMarca = new TextField();
        fieldMarca.setPromptText("Toyota, Chevrolet, etc.");
        TextField fieldModelo = new TextField();
        fieldModelo.setPromptText("Corolla, Spark, etc.");
        TextField fieldAnio = new TextField();
        fieldAnio.setPromptText("2024");

        ComboBox<Cliente> comboClienteDialog = new ComboBox<>();
        comboClienteDialog.setItems(FXCollections.observableArrayList(listaClientes));
        comboClienteDialog.setConverter(new StringConverter<Cliente>() {
            @Override
            public String toString(Cliente c) {
                return c != null ? c.getNombre() : "";
            }
            @Override
            public Cliente fromString(String s) {
                return null;
            }
        });
        comboClienteDialog.setPromptText("Seleccionar dueño");
        
        // Pre-seleccionar el cliente actual si hay uno
        if (comboCliente.getValue() != null) {
            comboClienteDialog.setValue(comboCliente.getValue());
        }

        grid.add(new Label("Placa:"), 0, 0);
        grid.add(fieldPlaca, 1, 0);
        grid.add(new Label("Marca:"), 0, 1);
        grid.add(fieldMarca, 1, 1);
        grid.add(new Label("Modelo:"), 0, 2);
        grid.add(fieldModelo, 1, 2);
        grid.add(new Label("Año:"), 0, 3);
        grid.add(fieldAnio, 1, 3);
        grid.add(new Label("Dueño:"), 0, 4);
        grid.add(comboClienteDialog, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                if (fieldPlaca.getText().isEmpty() || fieldMarca.getText().isEmpty()) {
                    mostrarAlerta("Placa y Marca son obligatorios");
                    return null;
                }
                if (comboClienteDialog.getValue() == null) {
                    mostrarAlerta("Debe seleccionar un dueño");
                    return null;
                }
                int anio = 0;
                try {
                    anio = Integer.parseInt(fieldAnio.getText());
                } catch (NumberFormatException e) {
                    anio = 2024;
                }
                return new Vehiculo(0, fieldPlaca.getText(), fieldMarca.getText(),
                        fieldModelo.getText(), anio, comboClienteDialog.getValue().getId());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(vehiculo -> {
            DatabaseUtil.insertarVehiculo(vehiculo);
            listaVehiculos = DatabaseUtil.cargarVehiculos();
            
            // Actualizar combo de clientes y seleccionar el dueño del vehículo
            listaClientes.stream()
                .filter(c -> c.getId() == vehiculo.getClienteId())
                .findFirst()
                .ifPresent(c -> comboCliente.setValue(c));
            
            filtrarVehiculosPorCliente();
            comboVehiculo.setValue(vehiculo);
            
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Vehículo Creado");
            info.setHeaderText(null);
            info.setContentText("Vehículo '" + vehiculo.getPlaca() + "' creado exitosamente.");
            info.showAndWait();
        });
    }

    @FXML
    private void generarComprobante() {
        Orden seleccionada = tablaOrdenes.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAlerta("Seleccione una orden para generar el comprobante");
            return;
        }

        if (seleccionada.getTotal() <= 0) {
            mostrarAlerta("Esta orden no tiene costos calculados. Primero ingrese los datos y presione 'Calcular'");
            return;
        }

        String nombreCliente = buscarNombreCliente(seleccionada.getClienteId());
        String placaVehiculo = buscarPlacaVehiculo(seleccionada.getVehiculoId());
        
        // Buscar info completa del vehículo
        String infoVehiculo = listaVehiculos.stream()
            .filter(v -> v.getId() == seleccionada.getVehiculoId())
            .map(v -> v.getPlaca() + " - " + v.getMarca() + " " + v.getModelo() + " (" + v.getAnio() + ")")
            .findFirst().orElse(placaVehiculo);

        String comprobante = String.format(
            "═══════════════════════════════════════════\n" +
            "         COMPROBANTE DE SERVICIO           \n" +
            "              TALLER AUTOMOTRIZ            \n" +
            "═══════════════════════════════════════════\n\n" +
            "Orden #: %d\n" +
            "Estado: %s\n\n" +
            "───────────────────────────────────────────\n" +
            "CLIENTE: %s\n" +
            "VEHÍCULO: %s\n" +
            "───────────────────────────────────────────\n\n" +
            "DETALLE DE COSTOS:\n\n" +
            "  Costo de Repuestos:    $%,.2f\n" +
            "  Horas de Trabajo:      %.1f hrs\n" +
            "  Costo por Hora:        $%,.2f\n" +
            "  ─────────────────────────────\n" +
            "  Mano de Obra:          $%,.2f\n" +
            "  ─────────────────────────────\n" +
            "  SUBTOTAL:              $%,.2f\n" +
            "  IVA (19%%):             $%,.2f\n" +
            "  ═════════════════════════════\n" +
            "  TOTAL A PAGAR:         $%,.2f\n" +
            "  ═════════════════════════════\n\n" +
            "═══════════════════════════════════════════\n" +
            "        ¡Gracias por su preferencia!       \n" +
            "═══════════════════════════════════════════",
            seleccionada.getId(),
            seleccionada.getEstado(),
            nombreCliente,
            infoVehiculo,
            seleccionada.getCostoRepuestos(),
            seleccionada.getHorasTrabajo(),
            seleccionada.getCostoHora(),
            seleccionada.getManoObra(),
            seleccionada.getSubtotal(),
            seleccionada.getIva(),
            seleccionada.getTotal()
        );

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Comprobante de Servicio");
        alert.setHeaderText("Orden #" + seleccionada.getId() + " - " + nombreCliente);
        
        TextArea textArea = new TextArea(comprobante);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setStyle("-fx-font-family: monospace; -fx-font-size: 12px;");
        textArea.setPrefWidth(450);
        textArea.setPrefHeight(500);
        
        alert.getDialogPane().setContent(textArea);
        alert.getDialogPane().setPrefWidth(500);
        alert.showAndWait();
    }

    @FXML
    private void verResumenTotal() {
        if (listaOrdenes.isEmpty()) {
            mostrarAlerta("No hay órdenes registradas");
            return;
        }

        // Calcular estadísticas
        double totalGeneral = 0;
        double totalRepuestos = 0;
        double totalManoObra = 0;
        double totalIVA = 0;
        int ordenesConCosto = 0;
        int ordenesPendientes = 0;
        int ordenesEnProceso = 0;
        int ordenesCompletadas = 0;

        for (Orden o : listaOrdenes) {
            if (o.getTotal() > 0) {
                totalGeneral += o.getTotal();
                totalRepuestos += o.getCostoRepuestos();
                totalManoObra += o.getManoObra();
                totalIVA += o.getIva();
                ordenesConCosto++;
            }
            String estado = o.getEstado().toLowerCase();
            if (estado.contains("pendiente")) ordenesPendientes++;
            else if (estado.contains("proceso")) ordenesEnProceso++;
            else if (estado.contains("completada") || estado.contains("entregad")) ordenesCompletadas++;
        }

        double promedioOrden = ordenesConCosto > 0 ? totalGeneral / ordenesConCosto : 0;

        String resumen = String.format(
            "════════════════════════════════════════════\n" +
            "          RESUMEN GENERAL DEL TALLER        \n" +
            "════════════════════════════════════════════\n\n" +
            "ESTADÍSTICAS DE ÓRDENES:\n" +
            "─────────────────────────────────────────────\n" +
            "  Total de órdenes:           %d\n" +
            "  Órdenes con costos:         %d\n" +
            "  Órdenes pendientes:         %d\n" +
            "  Órdenes en proceso:         %d\n" +
            "  Órdenes completadas:        %d\n\n" +
            "RESUMEN FINANCIERO:\n" +
            "─────────────────────────────────────────────\n" +
            "  Total en Repuestos:         $%,.2f\n" +
            "  Total Mano de Obra:         $%,.2f\n" +
            "  Total IVA recaudado:        $%,.2f\n" +
            "  ═════════════════════════════════════\n" +
            "  INGRESOS TOTALES:           $%,.2f\n" +
            "  ═════════════════════════════════════\n\n" +
            "  Promedio por orden:         $%,.2f\n\n" +
            "════════════════════════════════════════════",
            listaOrdenes.size(),
            ordenesConCosto,
            ordenesPendientes,
            ordenesEnProceso,
            ordenesCompletadas,
            totalRepuestos,
            totalManoObra,
            totalIVA,
            totalGeneral,
            promedioOrden
        );

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Resumen Total del Taller");
        alert.setHeaderText("Estadísticas y Finanzas");
        
        TextArea textArea = new TextArea(resumen);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setStyle("-fx-font-family: monospace; -fx-font-size: 12px;");
        textArea.setPrefWidth(400);
        textArea.setPrefHeight(450);
        
        alert.getDialogPane().setContent(textArea);
        alert.getDialogPane().setPrefWidth(450);
        alert.showAndWait();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}

