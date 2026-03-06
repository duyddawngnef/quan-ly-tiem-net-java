package gui.dialog;

import bus.NhanVienBUS;
import entity.NhanVien;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ThemNhanVienDialog implements Initializable {

    @FXML private Label lblDialogTitle;
    @FXML private Label lblError;
    @FXML private Button btnSave;

    @FXML private TextField txtMaNV;
    @FXML private TextField txtHo;
    @FXML private TextField txtTen;
    @FXML private ComboBox<String> cboChucVu;
    @FXML private TextField txtTenDangNhap;
    @FXML private PasswordField txtMatKhau;
    @FXML private PasswordField txtXacNhanMK;
    @FXML private ComboBox<String> cboTrangThai;

    private final NhanVienBUS nhanVienBUS = new NhanVienBUS();
    private boolean isEditMode = false;
    private NhanVien currentEntity;
    private Runnable onSaveCallback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (cboChucVu != null)
            cboChucVu.getItems().setAll("QUANLY", "NHANVIEN", "THUNGAN");
        if (cboTrangThai != null)
            cboTrangThai.getItems().setAll("DANGLAMVIEC", "NGHIVIEC");
    }

    public void setEntity(Object entity) {
        if (entity instanceof NhanVien) {
            currentEntity = (NhanVien) entity;
            isEditMode = true;
            if (lblDialogTitle != null) lblDialogTitle.setText("Cập nhật nhân viên");
            if (txtMaNV != null) txtMaNV.setEditable(false);
            // Khi edit, mật khẩu không bắt buộc
            if (txtMatKhau  != null) txtMatKhau.setPromptText("Để trống nếu không đổi");
            if (txtXacNhanMK!= null) txtXacNhanMK.setPromptText("Để trống nếu không đổi");
            populateFields(currentEntity);
        } else {
            currentEntity = null;
            isEditMode = false;
            if (lblDialogTitle != null) lblDialogTitle.setText("Thêm nhân viên mới");
            if (cboChucVu   != null) cboChucVu.setValue("NHANVIEN");
            if (cboTrangThai!= null) cboTrangThai.setValue("DANGLAMVIEC");
        }
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    private void populateFields(NhanVien nv) {
        if (txtMaNV       != null) txtMaNV.setText(nv.getManv());
        if (txtHo         != null) txtHo.setText(nv.getHo());
        if (txtTen        != null) txtTen.setText(nv.getTen());
        if (cboChucVu     != null) cboChucVu.setValue(nv.getChucvu());
        if (txtTenDangNhap!= null) txtTenDangNhap.setText(nv.getTendangnhap());
        if (cboTrangThai  != null) cboTrangThai.setValue(nv.getTrangthai());
        // Không điền mật khẩu khi edit
    }

    @FXML
    public void handleSave() {
        clearError();
        if (!validateFields()) return;
        try {
            NhanVien nv = buildEntity();
            if (isEditMode) {
                // Nếu có nhập mật khẩu mới thì đổi
                String mk = txtMatKhau != null ? txtMatKhau.getText() : "";
                if (!mk.isEmpty()) {
                    nhanVienBUS.resetMatKhau(nv.getManv(), mk);
                }
                nhanVienBUS.suaNhanVien(nv);
            } else {
                nhanVienBUS.themNhanVien(nv);
            }
            if (onSaveCallback != null) onSaveCallback.run();
            closeDialog();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private NhanVien buildEntity() {
        NhanVien nv = isEditMode ? currentEntity : new NhanVien();
        if (txtMaNV       != null) nv.setManv(txtMaNV.getText().trim());
        if (txtHo         != null) nv.setHo(txtHo.getText().trim());
        if (txtTen        != null) nv.setTen(txtTen.getText().trim());
        if (cboChucVu     != null) nv.setChucvu(cboChucVu.getValue());
        if (txtTenDangNhap!= null) nv.setTendangnhap(txtTenDangNhap.getText().trim());
        if (!isEditMode && txtMatKhau != null) nv.setMatkhau(txtMatKhau.getText());
        if (cboTrangThai  != null) nv.setTrangthai(cboTrangThai.getValue());
        return nv;
    }

    private boolean validateFields() {
        if (txtMaNV        != null && txtMaNV.getText().trim().isEmpty())        { showError("Vui lòng nhập mã NV"); return false; }
        if (txtHo          != null && txtHo.getText().trim().isEmpty())          { showError("Vui lòng nhập họ"); return false; }
        if (txtTen         != null && txtTen.getText().trim().isEmpty())         { showError("Vui lòng nhập tên"); return false; }
        if (txtTenDangNhap != null && txtTenDangNhap.getText().trim().isEmpty()) { showError("Vui lòng nhập tên đăng nhập"); return false; }
        if (!isEditMode) {
            // Thêm mới: bắt buộc nhập mật khẩu
            String mk      = txtMatKhau  != null ? txtMatKhau.getText()  : "";
            String xacNhan = txtXacNhanMK!= null ? txtXacNhanMK.getText(): "";
            if (mk.isEmpty())             { showError("Vui lòng nhập mật khẩu"); return false; }
            if (mk.length() < 6)          { showError("Mật khẩu phải có ít nhất 6 ký tự"); return false; }
            if (!mk.equals(xacNhan))      { showError("Mật khẩu xác nhận không khớp"); return false; }
        } else {
            // Edit: nếu nhập mk mới thì phải xác nhận khớp
            String mk      = txtMatKhau  != null ? txtMatKhau.getText()  : "";
            String xacNhan = txtXacNhanMK!= null ? txtXacNhanMK.getText(): "";
            if (!mk.isEmpty() && !mk.equals(xacNhan)) { showError("Mật khẩu xác nhận không khớp"); return false; }
        }
        return true;
    }

    @FXML
    public void handleCancel() { closeDialog(); }

    private void showError(String msg) {
        if (lblError != null) { lblError.setText(msg); lblError.setVisible(true); }
    }

    private void clearError() {
        if (lblError != null) { lblError.setText(""); lblError.setVisible(false); }
    }

    private void closeDialog() {
        ((Stage) btnSave.getScene().getWindow()).close();
    }
}
