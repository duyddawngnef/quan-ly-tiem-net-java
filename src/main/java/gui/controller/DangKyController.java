package gui.controller;

import bus.KhachHangBUS;
import entity.KhachHang;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class DangKyController implements Initializable {

    @FXML private TextField        txtHo;
    @FXML private TextField        txtTen;
    @FXML private TextField        txtSdt;
    @FXML private TextField        txtUsername;
    @FXML private PasswordField    txtPassword;
    @FXML private PasswordField    txtConfirmPassword;
    @FXML private Label            lblError;
    @FXML private Button           btnDangKy;

    private final KhachHangBUS khachHangBUS = new KhachHangBUS();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Enter key trên confirm password → submit
        txtConfirmPassword.setOnAction(e -> handleDangKy());
    }

    @FXML
    private void handleDangKy() {
        lblError.setStyle("-fx-text-fill: red;");
        lblError.setText("");

        String ho       = txtHo.getText().trim();
        String ten      = txtTen.getText().trim();
        String sdt      = txtSdt.getText().trim();
        String username = txtUsername.getText().trim();
        String pass     = txtPassword.getText();
        String passConf = txtConfirmPassword.getText();

        // Validate phía client trước
        if (ho.isEmpty()) { lblError.setText("Vui lòng nhập Họ."); return; }
        if (ten.isEmpty()) { lblError.setText("Vui lòng nhập Tên."); return; }
        if (sdt.isEmpty()) { lblError.setText("Vui lòng nhập số điện thoại."); return; }
        if (username.isEmpty()) { lblError.setText("Vui lòng nhập tên đăng nhập."); return; }
        if (pass.isEmpty()) { lblError.setText("Vui lòng nhập mật khẩu."); return; }
        if (pass.length() < 6) { lblError.setText("Mật khẩu phải ít nhất 6 ký tự."); return; }
        if (!pass.equals(passConf)) { lblError.setText("Mật khẩu xác nhận không khớp!"); return; }

        try {
            btnDangKy.setDisable(true);

            KhachHang kh = new KhachHang();
            kh.setHo(ho);
            kh.setTen(ten);
            kh.setSodienthoai(sdt);
            kh.setTendangnhap(username);
            kh.setMatkhau(pass);
            kh.setSodu(0);

            khachHangBUS.dangKy(kh);

            // Thành công → thông báo và quay về đăng nhập
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Đăng ký thành công");
            alert.setHeaderText(null);
            alert.setContentText(" Tài khoản \"" + username + "\" đã được tạo thành công!\nVui lòng đăng nhập để tiếp tục.");
            alert.showAndWait();

            quayVeDangNhap();

        } catch (Exception e) {
            lblError.setText(e.getMessage());
        } finally {
            btnDangKy.setDisable(false);
        }
    }

    @FXML
    private void handleQuayLai() {
        quayVeDangNhap();
    }

    private void quayVeDangNhap() {
        try {
            Stage stage = (Stage) btnDangKy.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Đăng nhập");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
