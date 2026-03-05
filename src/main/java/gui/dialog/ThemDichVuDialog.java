package gui.dialog;

import bus.DichVuBUS;
import entity.DichVu;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ThemDichVuDialog implements Initializable {

    @FXML private Label lblDialogTitle;
    @FXML private Label lblError;
    @FXML private Button btnSave;

    // Form fields
    @FXML private TextField txtMaDV;
    @FXML private TextField txtTenDV;
    @FXML private ComboBox<String> cboLoaiDV;
    @FXML private TextField txtDonGia;
    @FXML private TextField txtDonViTinh;
    @FXML private TextField txtSoLuongTon;
    @FXML private ComboBox<String> cboTrangThai;

    private final DichVuBUS dichVuBUS = new DichVuBUS();
    private boolean isEditMode = false;
    private DichVu currentEntity;
    private Runnable onSaveCallback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (cboLoaiDV != null)
            cboLoaiDV.getItems().setAll("DOANUONG", "TIENICH", "GIAITRI", "KHAC");
        if (cboTrangThai != null)
            cboTrangThai.getItems().setAll("DANGBAN", "NGUNGBAN");

        // Chỉ cho sửa mã khi thêm mới
        if (txtMaDV != null) txtMaDV.setEditable(true);
    }

    public void setEntity(Object entity) {
        if (entity instanceof DichVu) {
            currentEntity = (DichVu) entity;
            isEditMode = true;
            if (lblDialogTitle != null) lblDialogTitle.setText("Cập nhật dịch vụ");
            if (txtMaDV != null) txtMaDV.setEditable(false);
            populateFields(currentEntity);
        } else {
            currentEntity = null;
            isEditMode = false;
            if (lblDialogTitle != null) lblDialogTitle.setText("Thêm dịch vụ mới");
            if (cboTrangThai != null) cboTrangThai.setValue("DANGBAN");
            if (txtSoLuongTon != null) txtSoLuongTon.setText("0");
        }
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    private void populateFields(DichVu dv) {
        if (txtMaDV      != null) txtMaDV.setText(dv.getMadv());
        if (txtTenDV     != null) txtTenDV.setText(dv.getTendv());
        if (cboLoaiDV    != null) cboLoaiDV.setValue(dv.getLoaidv());
        if (txtDonGia    != null) txtDonGia.setText(String.valueOf((long) dv.getDongia()));
        if (txtDonViTinh != null) txtDonViTinh.setText(dv.getDonvitinh());
        if (txtSoLuongTon!= null) txtSoLuongTon.setText(String.valueOf(dv.getSoluongton()));
        if (cboTrangThai != null) cboTrangThai.setValue(dv.getTrangthai());
    }

    @FXML
    public void handleSave() {
        clearError();
        if (!validateFields()) return;

        try {
            DichVu dv = buildEntity();
            if (isEditMode) {
                dichVuBUS.suaDichVu(dv);
            } else {
                dichVuBUS.themDichVu(dv);
            }
            if (onSaveCallback != null) onSaveCallback.run();
            closeDialog();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private DichVu buildEntity() {
        String ma     = txtMaDV      != null ? txtMaDV.getText().trim()      : "";
        String ten    = txtTenDV     != null ? txtTenDV.getText().trim()      : "";
        String loai   = cboLoaiDV   != null ? cboLoaiDV.getValue()           : "";
        double donGia = txtDonGia   != null ? Double.parseDouble(txtDonGia.getText().replace(",","").trim()) : 0;
        String dvt    = txtDonViTinh!= null ? txtDonViTinh.getText().trim()  : "";
        int soLuong   = txtSoLuongTon!= null ? Integer.parseInt(txtSoLuongTon.getText().trim()) : 0;
        String tt     = cboTrangThai != null ? cboTrangThai.getValue()        : "DANGBAN";

        DichVu dv = isEditMode ? currentEntity : new DichVu();
        dv.setMadv(ma);
        dv.setTendv(ten);
        dv.setLoaidv(loai);
        dv.setDongia(donGia);
        dv.setDonvitinh(dvt);
        dv.setSoluongton(soLuong);
        dv.setTrangthai(tt);
        return dv;
    }

    private boolean validateFields() {
        if (txtMaDV != null && txtMaDV.getText().trim().isEmpty()) {
            showError("Vui lòng nhập mã dịch vụ"); return false;
        }
        if (txtTenDV != null && txtTenDV.getText().trim().isEmpty()) {
            showError("Vui lòng nhập tên dịch vụ"); return false;
        }
        if (txtDonGia != null) {
            try { Double.parseDouble(txtDonGia.getText().replace(",","").trim()); }
            catch (NumberFormatException e) { showError("Đơn giá không hợp lệ"); return false; }
        }
        if (txtSoLuongTon != null) {
            try { Integer.parseInt(txtSoLuongTon.getText().trim()); }
            catch (NumberFormatException e) { showError("Số lượng tồn phải là số nguyên"); return false; }
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
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }
}
