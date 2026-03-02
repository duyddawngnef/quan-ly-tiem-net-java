package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class KhuyenMaiController implements Initializable {

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbLoai;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;

    @FXML private TableView<?> tableKhuyenMai;
    @FXML private TableColumn<?, ?> colMaKM;
    @FXML private TableColumn<?, ?> colTenKM;
    @FXML private TableColumn<?, ?> colLoai;
    @FXML private TableColumn<?, ?> colGiaTri;
    @FXML private TableColumn<?, ?> colNgayBD;
    @FXML private TableColumn<?, ?> colNgayKT;
    @FXML private TableColumn<?, ?> colDieuKien;
    @FXML private TableColumn<?, ?> colTrangThai;

    @FXML private Label lblFormTitle;
    @FXML private TextField txtMaKM;
    @FXML private TextField txtTenKM;
    @FXML private TextField txtGiaTri;
    @FXML private ComboBox<String> cmbLoaiKM;
    @FXML private DatePicker dpNgayBD;
    @FXML private DatePicker dpNgayKT;
    @FXML private TextArea txtDieuKien;
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