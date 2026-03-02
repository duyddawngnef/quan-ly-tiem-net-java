package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class MayTinhController implements Initializable {

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbKhuMay;
    @FXML private ComboBox<String> cmbTrangThai;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;

    @FXML private TableView<?> tableMayTinh;
    @FXML private TableColumn<?, ?> colMaMay;
    @FXML private TableColumn<?, ?> colTenMay;
    @FXML private TableColumn<?, ?> colKhuMay;
    @FXML private TableColumn<?, ?> colGiaMoiGio;
    @FXML private TableColumn<?, ?> colCauHinh;
    @FXML private TableColumn<?, ?> colTrangThai;
    @FXML private TableColumn<?, ?> colGhiChu;

    @FXML private Label lblFormTitle;
    @FXML private TextField txtMaMay;
    @FXML private TextField txtTenMay;
    @FXML private TextField txtGiaMoiGio;
    @FXML private TextField txtCPU;
    @FXML private TextField txtRAM;
    @FXML private TextField txtVGA;
    @FXML private ComboBox<String> cmbKhuMayForm;
    @FXML private ComboBox<String> cmbTrangThaiForm;
    @FXML private TextArea txtGhiChu;

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