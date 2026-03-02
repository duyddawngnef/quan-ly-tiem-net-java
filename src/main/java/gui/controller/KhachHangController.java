package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.util.ResourceBundle;

public class KhachHangController implements Initializable {

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbLoaiTK;
    @FXML private VBox advancedSearchPane;
    @FXML private ComboBox<String> cmbOperatorSoDu;
    @FXML private TextField txtSoDuFilter;
    @FXML private DatePicker dpNgayTaoTu;
    @FXML private DatePicker dpNgayTaoDen;
    @FXML private Label lblTongKH;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;

    @FXML private TableView<?> tableKhachHang;
    @FXML private TableColumn<?, ?> colMaKH;
    @FXML private TableColumn<?, ?> colHoTen;
    @FXML private TableColumn<?, ?> colSdt;
    @FXML private TableColumn<?, ?> colEmail;
    @FXML private TableColumn<?, ?> colSoDu;
    @FXML private TableColumn<?, ?> colGioConLai;
    @FXML private TableColumn<?, ?> colLoaiTK;
    @FXML private TableColumn<?, ?> colNgayTao;
    @FXML private TableColumn<?, ?> colTrangThai;

    @FXML private VBox formPane;
    @FXML private Label lblFormTitle;
    @FXML private TextField txtMaKH;
    @FXML private TextField txtHoTen;
    @FXML private TextField txtSdt;
    @FXML private TextField txtEmail;
    @FXML private TextField txtDiaChi;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML private void handleSearch() {}

    @FXML private void toggleAdvancedSearch() {}

    @FXML private void handleAdvancedSearch() {}

    @FXML private void handleRowSelect() {}

    @FXML private void handleThem() {}

    @FXML private void handleSua() {}

    @FXML private void handleXoa() {}

    @FXML private void handleSave() {}

    @FXML private void handleCancel() {}

    @FXML private void handleExport() {}
}