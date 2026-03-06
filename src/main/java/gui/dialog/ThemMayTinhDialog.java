package gui.dialog;

import bus.KhuMayBUS;
import bus.MayTinhBUS;
import entity.KhuMay;
import entity.MayTinh;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ThemMayTinhDialog implements Initializable {

    @FXML private Label lblDialogTitle;
    @FXML private Label lblError;
    @FXML private Button btnSave;

    @FXML private TextField txtMaMay;
    @FXML private TextField txtTenMay;
    @FXML private ComboBox<String> cboMaKhu;
    @FXML private TextArea txtCauHinh;
    @FXML private TextField txtGiaMoiGio;
    @FXML private ComboBox<String> cboTrangThai;

    private final MayTinhBUS mayTinhBUS = new MayTinhBUS();
    private final KhuMayBUS khuMayBUS   = new KhuMayBUS();
    private boolean isEditMode = false;
    private MayTinh currentEntity;
    private Runnable onSaveCallback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (cboTrangThai != null)
            cboTrangThai.getItems().setAll("TRONG", "BAOTRI", "TATMAY");

        // Load khu máy combo
        if (cboMaKhu != null) {
            try {
                List<KhuMay> khuList = khuMayBUS.getAllKhuMay();
                khuList.forEach(k -> cboMaKhu.getItems().add(k.getMaKhu()));
            } catch (Exception ignored) {}
        }
    }

    public void setEntity(Object entity) {
        if (entity instanceof MayTinh) {
            currentEntity = (MayTinh) entity;
            isEditMode = true;
            if (lblDialogTitle != null) lblDialogTitle.setText("Cập nhật máy tính");
            if (txtMaMay != null) txtMaMay.setEditable(false);
            populateFields(currentEntity);
        } else {
            currentEntity = null;
            isEditMode = false;
            if (lblDialogTitle != null) lblDialogTitle.setText("Thêm máy tính mới");
            if (cboTrangThai != null) cboTrangThai.setValue("TRONG");
        }
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    private void populateFields(MayTinh m) {
        if (txtMaMay    != null) txtMaMay.setText(m.getMamay());
        if (txtTenMay   != null) txtTenMay.setText(m.getTenmay());
        if (cboMaKhu    != null) cboMaKhu.setValue(m.getMakhu());
        if (txtCauHinh  != null) txtCauHinh.setText(m.getCauhinh());
        if (txtGiaMoiGio!= null && m.getGiamoigio() != null)
            txtGiaMoiGio.setText(String.valueOf(m.getGiamoigio().longValue()));
        if (cboTrangThai!= null) cboTrangThai.setValue(m.getTrangthai());
    }

    @FXML
    public void handleSave() {
        clearError();
        if (!validateFields()) return;
        try {
            MayTinh m = buildEntity();
            if (isEditMode) mayTinhBUS.suaMayTinh(m);
            else            mayTinhBUS.themMayTinh(m);
            if (onSaveCallback != null) onSaveCallback.run();
            closeDialog();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private MayTinh buildEntity() {
        MayTinh m = isEditMode ? currentEntity : new MayTinh();
        if (txtMaMay    != null) m.setMamay(txtMaMay.getText().trim());
        if (txtTenMay   != null) m.setTenmay(txtTenMay.getText().trim());
        if (cboMaKhu    != null) m.setMakhu(cboMaKhu.getValue());
        if (txtCauHinh  != null) m.setCauhinh(txtCauHinh.getText().trim());
        if (txtGiaMoiGio!= null) {
            try { m.setGiamoigio(Double.parseDouble(txtGiaMoiGio.getText().replace(",","").trim())); }
            catch (Exception ignored) {}
        }
        if (cboTrangThai!= null) m.setTrangthai(cboTrangThai.getValue());
        return m;
    }

    private boolean validateFields() {
        if (txtMaMay   != null && txtMaMay.getText().trim().isEmpty())   { showError("Vui lòng nhập mã máy"); return false; }
        if (txtTenMay  != null && txtTenMay.getText().trim().isEmpty())  { showError("Vui lòng nhập tên máy"); return false; }
        if (cboMaKhu   != null && cboMaKhu.getValue() == null)          { showError("Vui lòng chọn khu máy"); return false; }
        if (txtGiaMoiGio != null) {
            try { Double.parseDouble(txtGiaMoiGio.getText().replace(",","").trim()); }
            catch (NumberFormatException e) { showError("Giá mỗi giờ không hợp lệ"); return false; }
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
