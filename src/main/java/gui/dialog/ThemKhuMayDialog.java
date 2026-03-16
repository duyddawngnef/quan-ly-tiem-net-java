package gui.dialog;

import bus.KhuMayBUS;
import entity.KhuMay;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
        import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ThemKhuMayDialog implements Initializable {

    @FXML private Label    lblDialogTitle;
    @FXML private Label    lblError;
    @FXML private Button   btnSave;

    @FXML private TextField         txtMaKhu;
    @FXML private TextField         txtTenKhu;
    @FXML private TextField         txtGiaCoso;
    @FXML private TextField         txtSoMayToiDa;
    @FXML private ComboBox<String>  cboTrangThai;

    private final KhuMayBUS khuMayBUS = new KhuMayBUS();
    private boolean isEditMode = false;
    private KhuMay  currentEntity;
    private Runnable onSaveCallback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (cboTrangThai != null)
            cboTrangThai.getItems().setAll("HOATDONG", "NGUNG");
    }

    // Gọi từ Controller để set entity (null = thêm mới, non-null = sửa)
    public void setEntity(Object entity) {
        if (entity instanceof KhuMay) {
            currentEntity = (KhuMay) entity;
            isEditMode = true;
            if (lblDialogTitle != null) lblDialogTitle.setText("Cập nhật khu máy");
            if (txtMaKhu       != null) txtMaKhu.setEditable(false);
            populateFields(currentEntity);
        } else {
            currentEntity = null;
            isEditMode = false;
            if (lblDialogTitle != null) lblDialogTitle.setText("Thêm khu máy mới");
            if (cboTrangThai   != null) cboTrangThai.setValue("HOATDONG");
        }
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    private void populateFields(KhuMay km) {
        if (txtMaKhu      != null) txtMaKhu.setText(km.getMakhu());
        if (txtTenKhu     != null) txtTenKhu.setText(km.getTenkhu());
        if (txtGiaCoso    != null) txtGiaCoso.setText(String.valueOf((long) km.getGiacoso()));
        if (txtSoMayToiDa != null) txtSoMayToiDa.setText(String.valueOf(km.getSomaytoida()));
        if (cboTrangThai  != null) cboTrangThai.setValue(km.getTrangthai());
    }

    @FXML
    public void handleSave() {
        clearError();
        if (!validateFields()) return;
        try {
            KhuMay km = buildEntity();
            if (isEditMode) khuMayBUS.suaKhuMay(km);
            else            khuMayBUS.themKhuMay(km);
            if (onSaveCallback != null) onSaveCallback.run();
            closeDialog();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void handleCancel() { closeDialog(); }

    // ==================== PRIVATE ====================

    private KhuMay buildEntity() {
        KhuMay km = isEditMode ? currentEntity : new KhuMay();
        if (txtMaKhu      != null) km.setMakhu(txtMaKhu.getText().trim());
        if (txtTenKhu     != null) km.setTenkhu(txtTenKhu.getText().trim());
        if (txtGiaCoso    != null) {
            try { km.setGiacoso(Double.parseDouble(txtGiaCoso.getText().replace(",", "").trim())); }
            catch (NumberFormatException ignored) {}
        }
        if (txtSoMayToiDa != null) {
            try { km.setSomaytoida(Integer.parseInt(txtSoMayToiDa.getText().trim())); }
            catch (NumberFormatException ignored) {}
        }
        if (cboTrangThai  != null) km.setTrangthai(cboTrangThai.getValue());
        return km;
    }

    private boolean validateFields() {
        if (txtTenKhu != null && txtTenKhu.getText().trim().isEmpty()) {
            showError("Vui lòng nhập tên khu"); return false;
        }
        if (txtGiaCoso != null) {
            try {
                double gia = Double.parseDouble(txtGiaCoso.getText().replace(",", "").trim());
                if (gia <= 0) { showError("Giá cơ sở phải lớn hơn 0"); return false; }
            } catch (NumberFormatException e) {
                showError("Giá cơ sở không hợp lệ"); return false;
            }
        }
        if (txtSoMayToiDa != null) {
            try {
                int so = Integer.parseInt(txtSoMayToiDa.getText().trim());
                if (so < 0) { showError("Số máy tối đa không được âm"); return false; }
            } catch (NumberFormatException e) {
                showError("Số máy tối đa không hợp lệ"); return false;
            }
        }
        if (cboTrangThai != null && cboTrangThai.getValue() == null) {
            showError("Vui lòng chọn trạng thái"); return false;
        }
        return true;
    }

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