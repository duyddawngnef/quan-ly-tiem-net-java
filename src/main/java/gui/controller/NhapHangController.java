package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class NhapHangController implements Initializable {

    @FXML private TextField txtSearch;
    @FXML private DatePicker dpTu;
    @FXML private DatePicker dpDen;
    @FXML private Button btnXoa;
    @FXML private Button btnThemCT;
    @FXML private Button btnXoaCT;
    @FXML private Button btnLuuPhieu;
    @FXML private Label lblTongTien;
    @FXML private Label lblCTTitle;

    // Master
    @FXML private TableView<?> tablePhieu;
    @FXML private TableColumn<?, ?> colMaPhieu;
    @FXML private TableColumn<?, ?> colNhaCungCap;
    @FXML private TableColumn<?, ?> colNgayNhap;
    @FXML private TableColumn<?, ?> colNhanVien;
    @FXML private TableColumn<?, ?> colTongTien;
    @FXML private TableColumn<?, ?> colGhiChu;

    // Sub
    @FXML private TableView<?> tableChiTiet;
    @FXML private TableColumn<?, ?> colSTT;
    @FXML private TableColumn<?, ?> colTenSP;
    @FXML private TableColumn<?, ?> colSoLuong;
    @FXML private TableColumn<?, ?> colDonGia;
    @FXML private TableColumn<?, ?> colThanhTien;
    @FXML private TableColumn<?, ?> colGhiChuCT;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML private void handleSearch() {}

    @FXML private void handlePhieuSelect(MouseEvent e) {}

    @FXML private void handleTaoPhieu() {}

    @FXML private void handleXoa() {}

    @FXML private void handleThemChiTiet() {}

    @FXML private void handleXoaChiTiet() {}

    @FXML private void handleLuuPhieu() {}
}