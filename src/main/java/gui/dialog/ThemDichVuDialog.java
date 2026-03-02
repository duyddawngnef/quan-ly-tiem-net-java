package gui.dialog;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ThemDichVuDialog implements Initializable {

    @FXML private Label lblTitle;
    @FXML private TextField txtMaDV;
    @FXML private TextField txtTenDV;
    @FXML private TextField txtGia;
    @FXML private TextField txtDonVi;
    @FXML private TextField txtTonKho;
    @FXML private ComboBox<String> cmbLoaiDV;
    @FXML private TextArea txtMoTa;
    @FXML private Label lblError;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML private void handleSave() {}

    @FXML private void handleCancel() {}
}