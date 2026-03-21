package gui.dialog;

import bus.KhachHangBUS;
import entity.KhachHang;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ThemKhachHangDialog implements Initializable {

    @FXML private Label lblTitle;
    @FXML private TextField txtHo;
    @FXML private TextField txtTen;
    @FXML private TextField txtSDT;
    @FXML private ComboBox<String> cboTrangThai;
    @FXML private Label lblError;
    @FXML private Button btnCancel;
    @FXML private Button btnSave;

    private final KhachHangBUS khachHangBUS = new KhachHangBUS();
    private KhachHang entity;
    private boolean isEditMode = false;
    private Runnable onSaveCallback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (cboTrangThai != null) {
            cboTrangThai.getItems().setAll("HOATDONG", "NGUNG");
            cboTrangThai.setValue("HOATDONG");
        }
    }

    // Hàm nhận dữ liệu từ KhachHangController truyền sang
    public void setEntity(Object obj) {
        if (obj instanceof KhachHang) {
            this.entity = (KhachHang) obj;
            this.isEditMode = true;

            lblTitle.setText("SỬA THÔNG TIN KHÁCH HÀNG");
            txtHo.setText(entity.getHo() != null ? entity.getHo() : "");
            txtTen.setText(entity.getTen() != null ? entity.getTen() : "");
            txtSDT.setText(entity.getSodienthoai() != null ? entity.getSodienthoai() : "");
            cboTrangThai.setValue(entity.getTrangthai() != null ? entity.getTrangthai() : "HOATDONG");
        } else {
            lblTitle.setText("THÊM KHÁCH HÀNG MỚI");
        }
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    private void handleSave() {
        lblError.setText("");
        try {
            KhachHang kh = isEditMode ? entity : new KhachHang();

            String ho = txtHo.getText().trim();
            String ten = txtTen.getText().trim();
            String sdt = txtSDT.getText().trim();

            kh.setHo(ho);
            kh.setTen(ten);
            kh.setSodienthoai(sdt);

            // --- TỰ ĐỘNG TẠO TÀI KHOẢN NGẦM ---
            if (!isEditMode) {
                if (sdt.isEmpty()) {
                    throw new Exception("Vui lòng nhập số điện thoại (dùng làm tài khoản đăng nhập)!");
                }
                kh.setTendangnhap(sdt); // Lấy SĐT làm tên đăng nhập
                kh.setMatkhau("123456"); // Mật khẩu mặc định
            }
            // -----------------------------------

            kh.setTrangthai(cboTrangThai.getValue());

            if (isEditMode) {
                khachHangBUS.suaKhachHang(kh);
            } else {
                khachHangBUS.themKhachHang(kh);
            }

            // Gọi callback để tự động làm mới bảng bên ngoài
            if (onSaveCallback != null) onSaveCallback.run();
            closeDialog();

        } catch (Exception e) {
            lblError.setText(e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}