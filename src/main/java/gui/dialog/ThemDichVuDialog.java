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

    @FXML
    private Label lblDialogTitle;
    @FXML
    private Label lblError;
    @FXML
    private Button btnSave;

    // Form fields — aligned with FXML fx:id names
    @FXML
    private TextField txtMaDV;
    @FXML
    private TextField txtTenDV;
    @FXML
    private ComboBox<String> cboLoaiDV;
    @FXML
    private TextField txtGia;
    @FXML
    private TextField txtDonVi;
    @FXML
    private TextField txtTonKho;
    @FXML
    private ComboBox<String> cboTrangThai;
    @FXML
    private TextArea txtMoTa;

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
        if (txtMaDV != null)
            txtMaDV.setEditable(true);
    }

    public void setEntity(Object entity) {
        if (entity instanceof DichVu) {
            currentEntity = (DichVu) entity;
            isEditMode = true;
            if (lblDialogTitle != null)
                lblDialogTitle.setText("Cập nhật dịch vụ");
            if (txtMaDV != null)
                txtMaDV.setEditable(false);
            populateFields(currentEntity);
        } else {
            currentEntity = null;
            isEditMode = false;
            if (lblDialogTitle != null)
                lblDialogTitle.setText("Thêm dịch vụ mới");
            if (cboTrangThai != null)
                cboTrangThai.setValue("DANGBAN");
            if (txtTonKho != null)
                txtTonKho.setText("0");
        }
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    private void populateFields(DichVu dv) {
        if (txtMaDV != null)
            txtMaDV.setText(dv.getMaDV());
        if (txtTenDV != null)
            txtTenDV.setText(dv.getTenDV());
        if (cboLoaiDV != null)
            cboLoaiDV.setValue(dv.getLoaiDV());
        if (txtGia != null)
            txtGia.setText(String.valueOf((long) dv.getDonGia()));
        if (txtDonVi != null)
            txtDonVi.setText(dv.getDonViTinh());
        if (txtTonKho != null)
            txtTonKho.setText(String.valueOf(dv.getSoLuongTon()));
        if (cboTrangThai != null)
            cboTrangThai.setValue(dv.getTrangThai());
    }

    @FXML
    public void handleSave() {
        clearError();
        if (!validateFields())
            return;

        try {
            DichVu dv = buildEntity();
            if (isEditMode) {
                dichVuBUS.suaDichVu(dv);
            } else {
                dichVuBUS.themDichVu(dv);
            }
            if (onSaveCallback != null)
                onSaveCallback.run();
            closeDialog();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private DichVu buildEntity() {
        String ma = txtMaDV != null ? txtMaDV.getText().trim() : "";
        String ten = txtTenDV != null ? txtTenDV.getText().trim() : "";
        String loai = cboLoaiDV != null ? cboLoaiDV.getValue() : "";
        double donGia = txtGia != null ? Double.parseDouble(txtGia.getText().replace(",", "").trim()) : 0;
        String dvt = txtDonVi != null ? txtDonVi.getText().trim() : "";
        int soLuong = txtTonKho != null ? Integer.parseInt(txtTonKho.getText().trim()) : 0;
        String tt = cboTrangThai != null ? cboTrangThai.getValue() : "DANGBAN";

        DichVu dv = isEditMode ? currentEntity : new DichVu();
        dv.setMaDV(ma);
        dv.setTenDV(ten);
        dv.setLoaiDV(loai);
        dv.setDonGia(donGia);
        dv.setDonViTinh(dvt);
        dv.setSoLuongTon(soLuong);
        dv.setTrangThai(tt);
        return dv;
    }

    private boolean validateFields() {
        if (txtMaDV != null && txtMaDV.getText().trim().isEmpty()) {
            showError("Vui lòng nhập mã dịch vụ");
            return false;
        }
        if (txtTenDV != null && txtTenDV.getText().trim().isEmpty()) {
            showError("Vui lòng nhập tên dịch vụ");
            return false;
        }
        if (txtGia != null) {
            try {
                Double.parseDouble(txtGia.getText().replace(",", "").trim());
            } catch (NumberFormatException e) {
                showError("Đơn giá không hợp lệ");
                return false;
            }
        }
        if (txtTonKho != null) {
            try {
                Integer.parseInt(txtTonKho.getText().trim());
            } catch (NumberFormatException e) {
                showError("Số lượng tồn phải là số nguyên");
                return false;
            }
        }
        return true;
    }

    @FXML
    public void handleCancel() {
        closeDialog();
    }

    private void showError(String msg) {
        if (lblError != null) {
            lblError.setText(msg);
            lblError.setVisible(true);
        }
    }

    private void clearError() {
        if (lblError != null) {
            lblError.setText("");
            lblError.setVisible(false);
        }
    }

    private void closeDialog() {
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }
}
