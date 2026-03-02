package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private Label lblUserName;
    @FXML private Label lblUserRole;
    @FXML private Label lblPageTitle;
    @FXML private Label lblCurrentUser;
    @FXML private Label lblCurrentTime;
    @FXML private StackPane contentPane;
    @FXML private Button btnSodoMay;
    @FXML private Button btnMayTinh;
    @FXML private Button btnKhuMay;
    @FXML private Button btnPhienSuDung;
    @FXML private Button btnDichVu;
    @FXML private Button btnGoiDichVu;
    @FXML private Button btnKhuyenMai;
    @FXML private Button btnKhachHang;
    @FXML private Button btnNapTien;
    @FXML private Button btnNhanVien;
    @FXML private Button btnHoaDon;
    @FXML private Button btnNhapHang;
    @FXML private Button btnThongKe;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML public void showSodoMay() {}
    @FXML public void showMayTinh() {}
    @FXML public void showKhuMay() {}
    @FXML public void showPhienSuDung() {}
    @FXML public void showDichVu() {}
    @FXML public void showGoiDichVu() {}
    @FXML public void showKhuyenMai() {}
    @FXML public void showKhachHang() {}
    @FXML public void showNapTien() {}
    @FXML public void showNhanVien() {}
    @FXML public void showHoaDon() {}
    @FXML public void showNhapHang() {}
    @FXML public void showThongKe() {}

    @FXML private void handleLogout() {}
}