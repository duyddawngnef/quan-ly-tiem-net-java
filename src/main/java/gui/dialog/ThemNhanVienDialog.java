package gui.dialog;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ThemNhanVienDialog implements Initializable {

    @FXML private Label lblTitle;
    @FXML private TextField txtMaNV;
    @FXML private TextField txtHoTen;
    @FXML private TextField txtSdt;
    @FXML private TextField txtEmail;
    @FXML private TextField txtLuong;
    @FXML private TextField txtCCCD;
    @FXML private TextField txtDiaChi;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cmbChucVu;
    @FXML private DatePicker dpNgayVaoLam;
    @FXML private Label lblError;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML private void handleSave() {}

    @FXML private void handleCancel() {}
}