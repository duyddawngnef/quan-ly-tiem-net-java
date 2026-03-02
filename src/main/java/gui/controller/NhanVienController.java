package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class NhanVienController implements Initializable {

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbChucVu;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;

    @FXML private TableView<?> tableNhanVien;
    @FXML private TableColumn<?, ?> colMaNV;
    @FXML private TableColumn<?, ?> colHoTen;
    @FXML private TableColumn<?, ?> colSdt;
    @FXML private TableColumn<?, ?> colChucVu;
    @FXML private TableColumn<?, ?> colLuong;
    @FXML private TableColumn<?, ?> colNgayVaoLam;
    @FXML private TableColumn<?, ?> colTrangThai;

    @FXML private Label lblFormTitle;
    @FXML private TextField txtMaNV;
    @FXML private TextField txtHoTen;
    @FXML private TextField txtSdt;
    @FXML private TextField txtEmail;
    @FXML private TextField txtLuong;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cmbChucVuForm;
    @FXML private DatePicker dpNgayVaoLam;

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