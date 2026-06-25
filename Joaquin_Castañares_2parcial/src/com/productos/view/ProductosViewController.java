package com.productos.view;

import com.productos.controller.ProductoController;
import com.productos.exception.ProductoCodigoDuplicadoException;
import com.productos.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ProductosViewController implements Initializable {

    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colTipo;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colMarca;
    @FXML private TableColumn<Producto, String> colModelo;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, String> colStock;
    @FXML private TableColumn<Producto, String> colProveedor;
    @FXML private TableColumn<Producto, String> colExtra;

    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtMarca;
    @FXML private TextField txtModelo;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private ComboBox<Proveedor> cmbProveedor;
    @FXML private Label lblExtra;
    @FXML private TextField txtExtra;
    @FXML private Label lblInfo;

    private ProductoController controller;
    private ObservableList<Producto> listaObservable;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        controller = new ProductoController();

        cmbTipo.setItems(FXCollections.observableArrayList("Electronico", "Alimenticio"));
        cmbTipo.setValue("Electronico");
        cmbTipo.setOnAction(e -> actualizarLabelExtra());

        cmbProveedor.setItems(FXCollections.observableArrayList(controller.getListaProveedores()));

        configurarColumnas();
        listaObservable = FXCollections.observableArrayList(controller.getListaProductos());
        tblProductos.setItems(listaObservable);

        tblProductos.getSelectionModel().selectedItemProperty().addListener((obs, viejo, sel) -> {
            if (sel != null) cargarEnFormulario(sel);
        });
    }

    private void actualizarLabelExtra() {
        lblExtra.setText(cmbTipo.getValue().equals("Electronico") ? "Garantía (meses):" : "Fecha vencimiento (YYYY-MM-DD):");
    }

    private void configurarColumnas() {
        colTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTipo()));
        colCodigo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCodigo()));
        colMarca.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMarca()));
        colModelo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getModelo()));
        colPrecio.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().getPrecio()).asObject());
        colStock.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getStock())));
        colProveedor.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getProveedor() != null ? d.getValue().getProveedor().getRazonSocial() : ""));
        colExtra.setCellValueFactory(d -> {
            String val = switch (d.getValue()) {
                case Electronico e -> e.getGarantiaMeses() + " meses";
                case Alimenticio a -> a.getFechaVencimiento().toString();
                default -> "";
            };
            return new SimpleStringProperty(val);
        });
    }

    @FXML
    private void onAgregar(ActionEvent e) {
        try {
            Producto p = construirProducto();
            controller.agregar(p);
            refrescar();
            limpiar();
            mostrarInfo("✔ Producto agregado.", false);
        } catch (ProductoCodigoDuplicadoException ex) {
            mostrarAlerta("Duplicado", ex.getMessage());
        } catch (IOException ex) {
            mostrarAlerta("Error", ex.getMessage());
        } catch (NumberFormatException ex) {
            mostrarAlerta("Error de formato", "Verificá precio, stock y atributo extra.");
        }
    }

    @FXML
    private void onModificar(ActionEvent e) {
        Producto sel = tblProductos.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarAlerta("Sin selección", "Seleccioná un producto."); return; }
        try {
            String marca    = txtMarca.getText().trim();
            String modelo   = txtModelo.getText().trim();
            double precio   = Double.parseDouble(txtPrecio.getText().trim());
            int stock       = Integer.parseInt(txtStock.getText().trim());
            Proveedor prov  = cmbProveedor.getValue();
            String extra    = txtExtra.getText().trim();
            controller.modificar(sel.getCodigo(), marca, modelo, precio, stock, prov, extra);
            refrescar();
            limpiar();
            mostrarInfo("✔ Producto modificado.", false);
        } catch (IOException ex) {
            mostrarAlerta("Error", ex.getMessage());
        }
    }

    @FXML
    private void onEliminar(ActionEvent e) {
        Producto sel = tblProductos.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarAlerta("Sin selección", "Seleccioná un producto."); return; }
        try {
            controller.eliminar(sel.getCodigo());
            refrescar();
            limpiar();
            mostrarInfo("✔ Producto eliminado.", false);
        } catch (IOException ex) {
            mostrarAlerta("Error", ex.getMessage());
        }
    }

    @FXML
    private void onLimpiar(ActionEvent e) { limpiar(); }

    @FXML
    private void onExportar(ActionEvent e) {
        try {
            controller.exportarCaros("productos_caros.txt");
            mostrarInfo("✔ Exportado en productos_caros.txt", false);
        } catch (IOException ex) {
            mostrarAlerta("Error", ex.getMessage());
        }
    }

    @FXML
    private void onVerInventario(ActionEvent e) {
        double total = controller.calcularTotalInventario();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Valor total del inventario");
        alert.setHeaderText(null);
        alert.setContentText(String.format("El valor total del inventario es: $%.2f", total));
        alert.showAndWait();
    }

    private Producto construirProducto() {
        String tipo    = cmbTipo.getValue();
        String codigo  = txtCodigo.getText().trim();
        String marca   = txtMarca.getText().trim();
        String modelo  = txtModelo.getText().trim();
        double precio  = Double.parseDouble(txtPrecio.getText().trim());
        int stock      = Integer.parseInt(txtStock.getText().trim());
        Proveedor prov = cmbProveedor.getValue();
        String extra   = txtExtra.getText().trim();

        return switch (tipo) {
            case "Electronico" -> new Electronico(codigo, marca, modelo, precio, stock, prov,
                                                   Integer.parseInt(extra));
            case "Alimenticio" -> new Alimenticio(codigo, marca, modelo, precio, stock, prov,
                                                   LocalDate.parse(extra));
            default -> null;
        };
    }

    private void cargarEnFormulario(Producto p) {
        cmbTipo.setValue(p.getTipo());
        txtCodigo.setText(p.getCodigo());
        txtCodigo.setEditable(false);
        txtMarca.setText(p.getMarca());
        txtModelo.setText(p.getModelo());
        txtPrecio.setText(String.valueOf(p.getPrecio()));
        txtStock.setText(String.valueOf(p.getStock()));
        cmbProveedor.setValue(p.getProveedor());
        String extra = switch (p) {
            case Electronico el -> String.valueOf(el.getGarantiaMeses());
            case Alimenticio al -> al.getFechaVencimiento().toString();
            default -> "";
        };
        txtExtra.setText(extra);
        actualizarLabelExtra();
    }

    private void refrescar() {
        listaObservable.setAll(controller.getListaProductos());
        tblProductos.refresh();
    }

    private void limpiar() {
        txtCodigo.clear();
        txtCodigo.setEditable(true);
        txtMarca.clear();
        txtModelo.clear();
        txtPrecio.clear();
        txtStock.clear();
        txtExtra.clear();
        cmbTipo.setValue("Electronico");
        cmbProveedor.setValue(null);
        tblProductos.getSelectionModel().clearSelection();
        lblInfo.setText("");
        actualizarLabelExtra();
    }

    private void mostrarInfo(String msg, boolean error) {
        lblInfo.setText(msg);
        lblInfo.setStyle(error ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}