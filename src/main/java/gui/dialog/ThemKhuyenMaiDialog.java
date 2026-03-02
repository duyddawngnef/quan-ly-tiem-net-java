package gui.dialog;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ThemKhuyenMaiDialog implements Initializable {

    @FXML private Label lblTitle;
    @FXML private TextField txtMaKM;
    @FXML private TextField txtTenKM;
    @FXML private TextField txtGiaTri;
    @FXML private ComboBox<String> cmbLoaiKM;
    @FXML private DatePicker dpNgayBD;
    @FXML private DatePicker dpNgayKT;
    @FXML private TextArea txtDieuKien;
    @FXML private TextArea txtMoTa;
    @FXML private Label lblError;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML private void handleSave() {}

    @FXML private void handleCancel() {}
}