package gui.dialog;

import bus.KhuyenMaiBUS;
import entity.ChuongTrinhKhuyenMai;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ThemKhuyenMaiDialog implements Initializable {

    @FXML private Label lblDialogTitle;
    @FXML private Label lblError;
    @FXML private Button btnSave;

    @FXML private TextField txtMaCTKM;
    @FXML private TextField txtTenCT;
    @FXML private ComboBox<String> cboLoaiKM;
    @FXML private TextField txtGiaTriKM;
    @FXML private TextField txtDieuKienToiThieu;
    @FXML private DatePicker dateNgayBatDau;
    @FXML private DatePicker dateNgayKetThuc;
    @FXML private ComboBox<String> cboTrangThai;

    private final KhuyenMaiBUS khuyenMaiBUS = new KhuyenMaiBUS();
    private boolean isEditMode = false;
    private ChuongTrinhKhuyenMai currentEntity;
    private Runnable onSaveCallback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (cboLoaiKM != null)
            cboLoaiKM.getItems().setAll("PHANTRAM", "SOTIEN", "TANGGIO");
        if (cboTrangThai != null)
            cboTrangThai.getItems().setAll("HOATDONG", "NGUNG");

        if (cboLoaiKM != null) {
            cboLoaiKM.setOnAction(e -> updateGiaTriHint());
        }
    }

    private void updateGiaTriHint() {
        if (cboLoaiKM == null || txtGiaTriKM == null) return;
        String loai = cboLoaiKM.getValue();
        if (loai == null) return;
        switch (loai) {
            case "PHANTRAM":
                txtGiaTriKM.setPromptText("VD: 10 (= 10%)");
                break;
            case "SOTIEN":
                txtGiaTriKM.setPromptText("VD: 50000 (= 50,000 VNĐ)");
                break;
            case "TANGGIO":
                txtGiaTriKM.setPromptText("VD: 1.5 (= 1.5 giờ)");
                break;
        }
    }

    public void setEntity(Object entity) {
        if (entity instanceof ChuongTrinhKhuyenMai) {
            currentEntity = (ChuongTrinhKhuyenMai) entity;
            isEditMode = true;
            if (lblDialogTitle != null) lblDialogTitle.setText("Cập nhật khuyến mãi");
            if (txtMaCTKM != null) txtMaCTKM.setEditable(false);
            populateFields(currentEntity);
        } else {
            currentEntity = null;
            isEditMode = false;
            if (lblDialogTitle != null) lblDialogTitle.setText("Thêm chương trình khuyến mãi");
            if (cboLoaiKM    != null) cboLoaiKM.setValue("PHANTRAM");
            if (cboTrangThai != null) cboTrangThai.setValue("HOATDONG");
            if (dateNgayBatDau  != null) dateNgayBatDau.setValue(LocalDate.now());
            if (dateNgayKetThuc != null) dateNgayKetThuc.setValue(LocalDate.now().plusDays(30));
        }
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    private void populateFields(ChuongTrinhKhuyenMai km) {
        if (txtMaCTKM           != null) txtMaCTKM.setText(km.getMaCTKM());
        if (txtTenCT            != null) txtTenCT.setText(km.getTenCT());
        if (cboLoaiKM           != null) cboLoaiKM.setValue(km.getLoaiKM());
        if (txtGiaTriKM         != null) txtGiaTriKM.setText(String.valueOf(km.getGiaTriKM()));
        if (txtDieuKienToiThieu != null) txtDieuKienToiThieu.setText(String.valueOf((long) km.getDieuKienToiThieu()));
        if (dateNgayBatDau  != null && km.getNgayBatDau()  != null)
            dateNgayBatDau.setValue(km.getNgayBatDau());
        if (dateNgayKetThuc != null && km.getNgayKetThuc() != null)
            dateNgayKetThuc.setValue(km.getNgayKetThuc());
        if (cboTrangThai != null) cboTrangThai.setValue(km.getTrangThai());
    }

    @FXML
    public void handleSave() {
        clearError();
        if (!validateFields()) return;
        try {
            ChuongTrinhKhuyenMai km = buildEntity();
            if (isEditMode) {
                khuyenMaiBUS.suaKhuyenMai(km);
            } else {
                khuyenMaiBUS.themKhuyenMai(km);
            }
            if (onSaveCallback != null) onSaveCallback.run();
            closeDialog();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private ChuongTrinhKhuyenMai buildEntity() {
        ChuongTrinhKhuyenMai km = isEditMode ? currentEntity : new ChuongTrinhKhuyenMai();
        if (txtMaCTKM != null) km.setMaCTKM(txtMaCTKM.getText().trim());
        if (txtTenCT  != null) km.setTenCT(txtTenCT.getText().trim());
        if (cboLoaiKM != null) km.setLoaiKM(cboLoaiKM.getValue());
        if (txtGiaTriKM != null) {
            try { km.setGiaTriKM(Double.parseDouble(txtGiaTriKM.getText().trim())); }
            catch (NumberFormatException ignored) {}
        }
        if (txtDieuKienToiThieu != null) {
            try { km.setDieuKienToiThieu(Double.parseDouble(txtDieuKienToiThieu.getText().replace(",", "").trim())); }
            catch (NumberFormatException ignored) {}
        }
        if (dateNgayBatDau  != null && dateNgayBatDau.getValue()  != null)
            km.setNgayBatDau(dateNgayBatDau.getValue());
        if (dateNgayKetThuc != null && dateNgayKetThuc.getValue() != null)
            km.setNgayKetThuc(dateNgayKetThuc.getValue());
        if (cboTrangThai != null) km.setTrangThai(cboTrangThai.getValue());
        return km;
    }

    private boolean validateFields() {
        if (txtMaCTKM != null && txtMaCTKM.getText().trim().isEmpty()) {
            showError("Vui lòng nhập mã CTKM"); return false;
        }
        if (txtTenCT != null && txtTenCT.getText().trim().isEmpty()) {
            showError("Vui lòng nhập tên CTKM"); return false;
        }
        if (cboLoaiKM != null && cboLoaiKM.getValue() == null) {
            showError("Vui lòng chọn loại khuyến mãi"); return false;
        }
        if (txtGiaTriKM != null) {
            try {
                double val = Double.parseDouble(txtGiaTriKM.getText().trim());
                if (val <= 0) { showError("Giá trị KM phải lớn hơn 0"); return false; }
            } catch (NumberFormatException e) {
                showError("Giá trị KM không hợp lệ"); return false;
            }
        }
        if (txtDieuKienToiThieu != null && !txtDieuKienToiThieu.getText().trim().isEmpty()) {
            try {
                double val = Double.parseDouble(txtDieuKienToiThieu.getText().replace(",", "").trim());
                if (val < 0) { showError("Điều kiện tối thiểu phải >= 0"); return false; }
            } catch (NumberFormatException e) {
                showError("Điều kiện tối thiểu không hợp lệ"); return false;
            }
        }
        if (dateNgayBatDau != null && dateNgayKetThuc != null
                && dateNgayBatDau.getValue() != null && dateNgayKetThuc.getValue() != null
                && !dateNgayKetThuc.getValue().isAfter(dateNgayBatDau.getValue())) {
            showError("Ngày kết thúc phải sau ngày bắt đầu"); return false;
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