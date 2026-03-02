package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class HoaDonController implements Initializable {

    @FXML private TextField txtSearch;
    @FXML private DatePicker dpTu;
    @FXML private DatePicker dpDen;
    @FXML private ComboBox<String> cmbNhanVien;
    @FXML private Label lblTongHD;

    // Master
    @FXML private TableView<?> tableHoaDon;
    @FXML private TableColumn<?, ?> colMaHD;
    @FXML private TableColumn<?, ?> colKhachHang;
    @FXML private TableColumn<?, ?> colNhanVien;
    @FXML private TableColumn<?, ?> colNgayLap;
    @FXML private TableColumn<?, ?> colTongTien;
    @FXML private TableColumn<?, ?> colGiamGia;
    @FXML private TableColumn<?, ?> colThanhToan;
    @FXML private TableColumn<?, ?> colTrangThai;

    // Sub
    @FXML private Label lblChiTietTitle;
    @FXML private Label lblTongTienCT;
    @FXML private TableView<?> tableChiTiet;
    @FXML private TableColumn<?, ?> colSTT;
    @FXML private TableColumn<?, ?> colTenSP;
    @FXML private TableColumn<?, ?> colSoLuong;
    @FXML private TableColumn<?, ?> colDonGia;
    @FXML private TableColumn<?, ?> colThanhTienCT;
    @FXML private TableColumn<?, ?> colGhiChu;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML private void handleSearch() {}

    @FXML private void handleHoaDonSelect(MouseEvent e) {}

    @FXML private void handleExportPDF() {}

    @FXML private void handleExportExcel() {}
}