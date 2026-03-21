package gui.controller;

import bus.KhachHangBUS;
import bus.NhanVienBUS;
import bus.PhienSuDungBUS;
import dao.*;
import entity.KhachHang;
import entity.NhanVien;
import utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private Button btnLogin;
    @FXML private Button btnTabKhachHang;
    @FXML private Button btnTabNhanVien;

    /** true = đang ở tab Khách hàng, false = tab Nhân viên */
    private boolean isKhachHangTab = true;

    private final NhanVienBUS nhanVienBUS = new NhanVienBUS();
    private final KhachHangBUS khachHangBUS = new KhachHangBUS();
    private final PhienSuDungBUS phienBUS = new PhienSuDungBUS(
            new PhienSuDungDAO(), new MayTinhDAO(), new KhachHangDAO(),
            new GoiDichVuKhachHangDAO(), new SuDungDichVuDAO(), new HoaDonDAO());

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (lblError != null) lblError.setText("");
        // Mặc định tab Khách hàng được active khi mở màn hình
        applyTabStyle();
    }

    // ===== CHỌN TAB KHÁCH HÀNG =====
    @FXML
    private void handleSelectKhachHang() {
        isKhachHangTab = true;
        applyTabStyle();
        clearError();
    }

    // ===== CHỌN TAB NHÂN VIÊN =====
    @FXML
    private void handleSelectNhanVien() {
        isKhachHangTab = false;
        applyTabStyle();
        clearError();
    }

    // ===== NÚT ĐĂNG NHẬP DUY NHẤT =====
    @FXML
    private void handleLogin() {
        if (isKhachHangTab) {
            doLoginKhachHang();
        } else {
            doLoginNhanVien();
        }
    }

    // ===== ĐĂNG NHẬP KHÁCH HÀNG =====
    private void doLoginKhachHang() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            return;
        }

        try {
            KhachHang kh = khachHangBUS.dangNhap(username, password);
            if (kh != null) {
                SessionManager.setCurrentKhachHang(kh);
//                phienBUS.moPhienTuDongKhiDangNhap(kh.getMakh());
                chuyenHuongMain();
            } else {
                showError("Tên đăng nhập hoặc mật khẩu không đúng.");
            }
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    // ===== ĐĂNG NHẬP NHÂN VIÊN =====
    private void doLoginNhanVien() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            return;
        }

        try {
            NhanVien nv = nhanVienBUS.dangNhap(username, password);
            if (nv != null) {
                // SessionManager.setCurrentUser() được gọi trong NhanVienBUS.dangNhap()
                chuyenHuongMain();
            } else {
                showError("Tên đăng nhập hoặc mật khẩu không đúng. Vui lòng thử lại!");
            }
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    // ===== CHUYỂN HƯỚNG MAIN =====
    private void chuyenHuongMain() throws Exception {
        Stage currentStage = (Stage) btnLogin.getScene().getWindow();
        currentStage.close();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Hệ Thống Quản Lý Tiệm Net");
        stage.setScene(new Scene(root, 1280, 800));
        stage.setMaximized(true);
        stage.show();
    }

    // ===== APPLY STYLE CHO TAB =====
    private void applyTabStyle() {
        if (btnTabKhachHang == null || btnTabNhanVien == null) return;

        if (isKhachHangTab) {
            // KH active
            btnTabKhachHang.getStyleClass().removeAll("login-role-btn-inactive");
            if (!btnTabKhachHang.getStyleClass().contains("login-role-btn-active"))
                btnTabKhachHang.getStyleClass().add("login-role-btn-active");

            btnTabNhanVien.getStyleClass().removeAll("login-role-btn-active");
            if (!btnTabNhanVien.getStyleClass().contains("login-role-btn-inactive"))
                btnTabNhanVien.getStyleClass().add("login-role-btn-inactive");
        } else {
            // NV active
            btnTabNhanVien.getStyleClass().removeAll("login-role-btn-inactive");
            if (!btnTabNhanVien.getStyleClass().contains("login-role-btn-active"))
                btnTabNhanVien.getStyleClass().add("login-role-btn-active");

            btnTabKhachHang.getStyleClass().removeAll("login-role-btn-active");
            if (!btnTabKhachHang.getStyleClass().contains("login-role-btn-inactive"))
                btnTabKhachHang.getStyleClass().add("login-role-btn-inactive");
        }
    }

    private void showError(String msg) {
        if (lblError != null) {
            lblError.setText(msg);
            lblError.setStyle("-fx-text-fill: #C62828;");
        }
    }

    private void clearError() {
        if (lblError != null) {
            lblError.setText("");
            lblError.setStyle("");
        }
    }

    // ===== MỞ MÀN HÌNH ĐĂNG KÝ =====
    @FXML
    private void handleMoDangKy() {
        try {
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dangKy.fxml"));
            stage.setScene(new Scene(loader.load()));
            stage.centerOnScreen();
            stage.setTitle("Đăng ký tài khoản");
        } catch (Exception e) {
            showError("Không thể mở màn hình đăng ký: " + e.getMessage());
            e.printStackTrace();
        }
    }
}