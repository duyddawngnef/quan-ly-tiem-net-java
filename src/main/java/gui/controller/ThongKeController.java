package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ThongKeController implements Initializable {

    @FXML private ToggleGroup tgKieuThongKe;
    @FXML private DatePicker dpTu;
    @FXML private DatePicker dpDen;
    @FXML private Label lblTongThu;
    @FXML private Label lblTongChi;
    @FXML private Label lblLoiNhuan;
    @FXML private Label lblSoHD;
    @FXML private Label lblSoPhieu;
    @FXML private Label lblTiLe;

    // Tab Nhân viên
    @FXML private ComboBox<String> cmbNamNV;
    @FXML private TableView<?> tableThongKeNV;
    @FXML private TableColumn<?, ?> colNhanVien;
    @FXML private TableColumn<?, ?> colQ1NV;
    @FXML private TableColumn<?, ?> colQ2NV;
    @FXML private TableColumn<?, ?> colQ3NV;
    @FXML private TableColumn<?, ?> colQ4NV;
    @FXML private TableColumn<?, ?> colTCNV;

    // Tab Khách hàng
    @FXML private ComboBox<String> cmbNamKH;
    @FXML private TableView<?> tableThongKeKH;
    @FXML private TableColumn<?, ?> colKhachHangTK;
    @FXML private TableColumn<?, ?> colQ1KH;
    @FXML private TableColumn<?, ?> colQ2KH;
    @FXML private TableColumn<?, ?> colQ3KH;
    @FXML private TableColumn<?, ?> colQ4KH;
    @FXML private TableColumn<?, ?> colTCKH;

    // Tab Dịch vụ
    @FXML private ComboBox<String> cmbNamDV;
    @FXML private TableView<?> tableThongKeDV;
    @FXML private TableColumn<?, ?> colDichVuTK;
    @FXML private TableColumn<?, ?> colQ1DV;
    @FXML private TableColumn<?, ?> colQ2DV;
    @FXML private TableColumn<?, ?> colQ3DV;
    @FXML private TableColumn<?, ?> colQ4DV;
    @FXML private TableColumn<?, ?> colTCDV;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML private void handleThongKe() {}

    @FXML private void handleThongKeNV() {}

    @FXML private void handleThongKeKH() {}

    @FXML private void handleThongKeDV() {}

    @FXML private void handleExportExcel() {}

    @FXML private void handlePrint() {}
}