package gui.dialog;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ThemMayTinhDialog implements Initializable {

    @FXML private Label lblTitle;
    @FXML private TextField txtMaMay;
    @FXML private TextField txtTenMay;
    @FXML private TextField txtGiaMoiGio;
    @FXML private TextField txtCPU;
    @FXML private TextField txtRAM;
    @FXML private TextField txtVGA;
    @FXML private TextField txtManHinh;
    @FXML private ComboBox<String> cmbKhuMay;
    @FXML private ComboBox<String> cmbTrangThai;
    @FXML private TextArea txtGhiChu;
    @FXML private Label lblError;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML private void handleSave() {}

    @FXML private void handleCancel() {}
}