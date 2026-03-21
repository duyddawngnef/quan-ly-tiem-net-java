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

    @FXML private Label lblTitle;

    @FXML private TextField txtMaKhu;
    @FXML private TextField txtTenKhu;
    @FXML private TextField txtGiaCoSo;
    @FXML private TextField txtSoMayToiDa;
    @FXML private ComboBox<String> cboTrangThai;

    @FXML private Label lblError;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    private final KhuMayBUS khuMayBUS = new KhuMayBUS();

    private KhuMay entity;
    private boolean isEditMode = false;
    private Runnable onSaveCallback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (cboTrangThai != null) {
            cboTrangThai.getItems().setAll("HOATDONG");
            cboTrangThai.setValue("HOATDONG"); // mặc định hợp lý cho khu mới
        }
        clearError();
    }

    /** entity == null => INSERT, entity != null => EDIT */
    public void setEntity(KhuMay km) {
        this.entity = km;
        this.isEditMode = (km != null);

        clearError();

        if (isEditMode) {
            if (lblTitle != null) lblTitle.setText("Sửa Khu Máy");

            if (txtMaKhu != null) {
                txtMaKhu.setDisable(true);
                txtMaKhu.setDisable(true); // khóa mã khu khi sửa
            }
            if (txtTenKhu != null) txtTenKhu.setText(km.getTenkhu());
            if (txtGiaCoSo != null) txtGiaCoSo.setText(String.valueOf(km.getGiacoso()));
            if (txtSoMayToiDa != null) txtSoMayToiDa.setText(String.valueOf(km.getSomaytoida()));
            if (cboTrangThai != null) cboTrangThai.setDisable(true);

        } else {
            if (lblTitle != null) lblTitle.setText("Thêm Khu Máy");

            if (txtMaKhu != null) {
                txtMaKhu.clear();
                txtMaKhu.setDisable(true);
            }
            if (txtTenKhu != null) txtTenKhu.clear();
            if (txtGiaCoSo != null) txtGiaCoSo.clear();
            if (txtSoMayToiDa != null) txtSoMayToiDa.clear();
            if (cboTrangThai != null) cboTrangThai.setDisable(true);
        }
    }

    public void setOnSaveCallback(Runnable cb) {
        this.onSaveCallback = cb;
    }

    @FXML
    public void handleSave() {
        clearError();

        String maKhu = txtMaKhu != null ? txtMaKhu.getText().trim() : "";
        String tenKhu = txtTenKhu != null ? txtTenKhu.getText().trim() : "";


        if (tenKhu.isEmpty()) {
            setError("Tên khu không được để trống");
            return;
        }

        double giaCoSo;
        try {
            giaCoSo = Double.parseDouble(txtGiaCoSo != null ? txtGiaCoSo.getText().replace(",", "").trim() : "0");
            if (giaCoSo < 0) {
                setError("Giá cơ sở phải >= 0");
                return;
            }
        } catch (NumberFormatException e) {
            setError("Giá cơ sở không hợp lệ");
            return;
        }

        int soMayToiDa;
        try {
            soMayToiDa = Integer.parseInt(txtSoMayToiDa != null ? txtSoMayToiDa.getText().trim() : "0");
            if (soMayToiDa < 0) {
                setError("Số máy tối đa phải >= 0");
                return;
            }
            if (soMayToiDa > 100) {
                // khớp ràng buộc BUS.themKhuMay
                setError("Số máy tối đa trong một khu không được vượt quá 100");
                return;
            }
        } catch (NumberFormatException e) {
            setError("Số máy tối đa không hợp lệ");
            return;
        }

        String trangThai = (cboTrangThai != null && cboTrangThai.getValue() != null)
                ? cboTrangThai.getValue()
                : "HOATDONG";

        KhuMay km = isEditMode ? entity : new KhuMay();
        if (!isEditMode) km.setMakhu(maKhu);
        km.setTenkhu(tenKhu);
        km.setGiacoso(giaCoSo);
        km.setSomaytoida(soMayToiDa);
        km.setTrangthai(trangThai);

        try {
            if (isEditMode) {
                khuMayBUS.suaKhuMay(km);
                ThongBaoDialog.showSuccess(getStage(), "Cập nhật khu máy thành công!");
            } else {
                khuMayBUS.themKhuMay(km);
                ThongBaoDialog.showSuccess(getStage(), "Thêm khu máy thành công!");
            }

            if (onSaveCallback != null) onSaveCallback.run();
            closeDialog();
        } catch (Exception e) {
            // BUS sẽ throw khi không đủ quyền / trạng thái không HOATDONG / ràng buộc số máy...
            setError(e.getMessage());
        }
    }

    @FXML
    public void handleCancel() {
        closeDialog();
    }

    private void setError(String msg) {
        if (lblError != null) lblError.setText(msg);
    }

    private void clearError() {
        if (lblError != null) lblError.setText("");
    }

    private void closeDialog() {
        if (btnCancel != null && btnCancel.getScene() != null) {
            ((Stage) btnCancel.getScene().getWindow()).close();
        }
    }

    private Stage getStage() {
        return btnSave != null && btnSave.getScene() != null
                ? (Stage) btnSave.getScene().getWindow()
                : null;
    }
}
