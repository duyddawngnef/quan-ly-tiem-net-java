package gui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import bus.KhachHangBUS;
import bus.NhanVienBUS;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class LoginController implements Initializable {

    @FXML private ToggleButton btnRoleKhachHang;
    @FXML private ToggleButton btnRoleNhanVien;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Label lblError;
    @FXML private Label lblDbStatus;

    private final KhachHangBUS khachHangBUS = new KhachHangBUS();
    private final NhanVienBUS nhanVienBUS = new NhanVienBUS();
    private boolean isNhanVienMode = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup roleGroup = new ToggleGroup();
        btnRoleKhachHang.setToggleGroup(roleGroup);
        btnRoleNhanVien.setToggleGroup(roleGroup);
        btnRoleKhachHang.setSelected(true);

        txtPassword.setOnAction(e -> handleLogin());
        txtUsername.setOnAction(e -> txtPassword.requestFocus());

        txtUsername.textProperty().addListener((obs, o, n) -> clearError());
        txtPassword.textProperty().addListener((obs, o, n) -> clearError());

        kiemTraKetNoiDatabase();
    }

    private void kiemTraKetNoiDatabase() {
        setDbStatus("⏳ Đang kết nối database...", "#f39c12");
        btnLogin.setDisable(true);

        Thread thread = new Thread(() -> {
            boolean ketNoi = khachHangBUS.kiemTraKetNoi();
            Platform.runLater(() -> {
                if (ketNoi) {
                    setDbStatus("✅ Kết nối database thành công", "#27ae60");
                    btnLogin.setDisable(false);
                } else {
                    setDbStatus("❌ Không thể kết nối database!", "#e74c3c");
                    btnLogin.setDisable(true);
                    showError("Không thể kết nối database. Vui lòng kiểm tra MySQL!");
                }
            });
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void setDbStatus(String message, String color) {
        if (lblDbStatus != null) {
            lblDbStatus.setText(message);
            lblDbStatus.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px;");
            lblDbStatus.setVisible(true);
        }
    }

    @FXML
    public void handleRoleToggle() {
        isNhanVienMode = btnRoleNhanVien.isSelected();
        clearError();
    }

    @FXML
    public void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty()) {
            showError("Vui lòng nhập tên đăng nhập");
            txtUsername.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            showError("Vui lòng nhập mật khẩu");
            txtPassword.requestFocus();
            return;
        }

        btnLogin.setDisable(true);
        btnLogin.setText("Đang đăng nhập...");

        try {
            if (isNhanVienMode) {
                nhanVienBUS.dangNhap(username, password);
            } else {
                khachHangBUS.dangNhap(username, password);
            }
            // Đăng nhập thành công
            System.out.println("[LoginController] Đăng nhập thành công, đang mở main...");
            openMain();

        } catch (Exception e) {
            // Lỗi đăng nhập (sai mk, không tồn tại, v.v.)
            System.err.println("[LoginController] Lỗi đăng nhập: " + e.getMessage());
            showError(e.getMessage() != null ? e.getMessage() : "Đăng nhập thất bại!");
            txtPassword.clear();
            txtPassword.requestFocus();
        } finally {
            btnLogin.setDisable(false);
            btnLogin.setText("ĐĂNG NHẬP");
        }
    }

    @FXML
    public void handleRegister() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/register.fxml"));
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 700));
        } catch (Exception e) {
            showError("Không thể mở trang đăng ký: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openMain() {
        try {
            System.out.println("[LoginController] Đang load /fxml/main.fxml ...");
            URL fxmlUrl = getClass().getResource("/fxml/main.fxml");

            // Kiểm tra file main.fxml có tồn tại không
            if (fxmlUrl == null) {
                showError("Lỗi: Không tìm thấy file main.fxml tại /fxml/main.fxml");
                System.err.println("[LoginController] KHÔNG TÌM THẤY /fxml/main.fxml !");
                return;
            }

            Parent root = FXMLLoader.load(fxmlUrl);
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 760));
            stage.setTitle("Hệ Thống Quản Lý Tiệm Internet");
            stage.centerOnScreen();

        } catch (Exception e) {
            // Hiển thị lỗi chi tiết ra màn hình — không im lặng nữa
            String errorMsg = "Lỗi mở giao diện chính: " + e.getMessage();
            showError(errorMsg);
            System.err.println("[LoginController] " + errorMsg);
            e.printStackTrace(); // In stack trace đầy đủ ra console để debug
        }
    }

    private void showError(String msg) {
        lblError.setText(msg != null ? msg : "Lỗi không xác định");
        lblError.setVisible(true);
        lblError.setManaged(true);
    }

    private void clearError() {
        lblError.setText("");
        lblError.setVisible(false);
        lblError.setManaged(false);
    }
}