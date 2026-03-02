package gui.dialog;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ThemGoiDichVuDialog implements Initializable {

    @FXML private Label lblTitle;
    @FXML private TextField txtMaGoi;
    @FXML private TextField txtTenGoi;
    @FXML private TextField txtSoGio;
    @FXML private TextField txtGia;
    @FXML private TextField txtUuDai;
    @FXML private TextField txtHanSuDung;
    @FXML private TextArea txtMoTa;
    @FXML private Label lblError;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML private void handleSave() {}

    @FXML private void handleCancel() {}
}