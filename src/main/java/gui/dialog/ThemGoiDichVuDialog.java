package gui.dialog;

import bus.GoiDichVuBUS;
import entity.GoiDichVu;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ThemGoiDichVuDialog implements Initializable {

    @FXML private Label lblDialogTitle;
    @FXML private Label lblError;
    @FXML private Button btnSave;

    @FXML private TextField txtMaGoi;
    @FXML private TextField txtTenGoi;
    @FXML private ComboBox<String> cboLoaiGoi;
    @FXML private TextField txtSoGio;
    @FXML private TextField txtNgayHieuLuc;
    @FXML private TextField txtGiaGoc;
    @FXML private TextField txtGiaGoi;
    @FXML private TextField txtApdungKhu;
    @FXML private ComboBox<String> cboTrangThai;

    private final GoiDichVuBUS goiDichVuBUS = new GoiDichVuBUS();
    private boolean isEditMode = false;
    private GoiDichVu currentEntity;
    private Runnable onSaveCallback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (cboLoaiGoi != null)
            cboLoaiGoi.getItems().setAll("THANG", "TUAN", "NGAY", "GIO");
        if (cboTrangThai != null)
            cboTrangThai.getItems().setAll("HOATDONG", "NGUNG");
    }

    public void setEntity(Object entity) {
        if (entity instanceof GoiDichVu) {
            currentEntity = (GoiDichVu) entity;
            isEditMode = true;
            if (lblDialogTitle != null) lblDialogTitle.setText("Cập nhật gói dịch vụ");
            if (txtMaGoi != null) txtMaGoi.setEditable(false);
            populateFields(currentEntity);
        } else {
            currentEntity = null;
            isEditMode = false;
            if (lblDialogTitle != null) lblDialogTitle.setText("Thêm gói dịch vụ mới");
            if (cboTrangThai != null) cboTrangThai.setValue("HOATDONG");
        }
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    private void populateFields(GoiDichVu g) {
        if (txtMaGoi      != null) txtMaGoi.setText(g.getMagoi());
        if (txtTenGoi     != null) txtTenGoi.setText(g.getTengoi());
        if (cboLoaiGoi    != null) cboLoaiGoi.setValue(g.getLoaigoi());
        if (txtSoGio      != null) txtSoGio.setText(String.valueOf(g.getSogio()));
        if (txtNgayHieuLuc!= null) txtNgayHieuLuc.setText(String.valueOf(g.getSongayhieuluc()));
        if (txtGiaGoc     != null) txtGiaGoc.setText(String.valueOf((long) g.getGiagoc()));
        if (txtGiaGoi     != null) txtGiaGoi.setText(String.valueOf((long) g.getGiagoi()));
        if (txtApdungKhu  != null) txtApdungKhu.setText(g.getApdungchokhu());
        if (cboTrangThai  != null) cboTrangThai.setValue(g.getTrangthai());
    }

    @FXML
    public void handleSave() {
        clearError();
        if (!validateFields()) return;
        try {
            GoiDichVu g = buildEntity();
            if (isEditMode) goiDichVuBUS.suaGoiDichVu(g);
            else            goiDichVuBUS.themGoiDichVu(g);
            if (onSaveCallback != null) onSaveCallback.run();
            closeDialog();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private GoiDichVu buildEntity() {
        GoiDichVu g = isEditMode ? currentEntity : new GoiDichVu();
        if (txtMaGoi      != null) g.setMagoi(txtMaGoi.getText().trim());
        if (txtTenGoi     != null) g.setTengoi(txtTenGoi.getText().trim());
        if (cboLoaiGoi    != null) g.setLoaigoi(cboLoaiGoi.getValue());
        if (txtSoGio      != null) {
            try { g.setSogio(Double.parseDouble(txtSoGio.getText().trim())); } catch (Exception ignored) {}
        }
        if (txtNgayHieuLuc!= null) {
            try { g.setSongayhieuluc(Integer.parseInt(txtNgayHieuLuc.getText().trim())); } catch (Exception ignored) {}
        }
        if (txtGiaGoc     != null) {
            try { g.setGiagoc(Double.parseDouble(txtGiaGoc.getText().replace(",","").trim())); } catch (Exception ignored) {}
        }
        if (txtGiaGoi     != null) {
            try { g.setGiagoi(Double.parseDouble(txtGiaGoi.getText().replace(",","").trim())); } catch (Exception ignored) {}
        }
        if (txtApdungKhu  != null) g.setApdungchokhu(txtApdungKhu.getText().trim());
        if (cboTrangThai  != null) g.setTrangthai(cboTrangThai.getValue());
        return g;
    }

    private boolean validateFields() {
        if (txtMaGoi  != null && txtMaGoi.getText().trim().isEmpty()) { showError("Vui lòng nhập mã gói"); return false; }
        if (txtTenGoi != null && txtTenGoi.getText().trim().isEmpty()) { showError("Vui lòng nhập tên gói"); return false; }
        if (txtSoGio  != null) {
            try { Double.parseDouble(txtSoGio.getText().trim()); }
            catch (NumberFormatException e) { showError("Số giờ không hợp lệ"); return false; }
        }
        if (txtGiaGoi != null) {
            try { Double.parseDouble(txtGiaGoi.getText().replace(",","").trim()); }
            catch (NumberFormatException e) { showError("Giá gói không hợp lệ"); return false; }
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
