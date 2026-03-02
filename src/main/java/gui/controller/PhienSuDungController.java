package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class PhienSuDungController implements Initializable {

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbTrangThai;
    @FXML private DatePicker dpTu;
    @FXML private DatePicker dpDen;
    @FXML private Button btnMoPhien;
    @FXML private Button btnKetThuc;

    @FXML private TableView<?> tablePhien;
    @FXML private TableColumn<?, ?> colMaPhien;
    @FXML private TableColumn<?, ?> colMaMay;
    @FXML private TableColumn<?, ?> colKhachHang;
    @FXML private TableColumn<?, ?> colGioBatDau;
    @FXML private TableColumn<?, ?> colGioKetThuc;
    @FXML private TableColumn<?, ?> colThoiGian;
    @FXML private TableColumn<?, ?> colTienPhi;
    @FXML private TableColumn<?, ?> colTrangThai;

    @FXML private Label lblMaPhien;
    @FXML private Label lblMaMay;
    @FXML private Label lblKhachHang;
    @FXML private Label lblSoDu;
    @FXML private Label lblGioBatDau;
    @FXML private Label lblThoiGian;
    @FXML private Label lblTienPhi;
    @FXML private Label lblTrangThai;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML private void handleSearch() {}

    @FXML private void handleTableClick(MouseEvent e) {}

    @FXML private void handleMoPhien() {}

    @FXML private void handleKetThucPhien() {}

    @FXML private void handleRefresh() {}
}