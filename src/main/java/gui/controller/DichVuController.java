package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class DichVuController implements Initializable {

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbLoaiDV;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;

    @FXML private TableView<?> tableDichVu;
    @FXML private TableColumn<?, ?> colMaDV;
    @FXML private TableColumn<?, ?> colTenDV;
    @FXML private TableColumn<?, ?> colLoai;
    @FXML private TableColumn<?, ?> colGia;
    @FXML private TableColumn<?, ?> colDonVi;
    @FXML private TableColumn<?, ?> colTonKho;
    @FXML private TableColumn<?, ?> colMoTa;
    @FXML private TableColumn<?, ?> colTrangThai;

    @FXML private Label lblFormTitle;
    @FXML private TextField txtMaDV;
    @FXML private TextField txtTenDV;
    @FXML private TextField txtGia;
    @FXML private TextField txtDonVi;
    @FXML private TextField txtTonKho;
    @FXML private ComboBox<String> cmbLoaiDVForm;
    @FXML private TextArea txtMoTa;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML private void handleSearch() {}

    @FXML private void handleRowSelect() {}

    @FXML private void handleThem() {}

    @FXML private void handleSua() {}

    @FXML private void handleXoa() {}

    @FXML private void handleSave() {}

    @FXML private void handleCancel() {}
}