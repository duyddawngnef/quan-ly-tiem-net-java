package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML private TextField txtHoTen;
    @FXML private TextField txtSdt;
    @FXML private TextField txtEmail;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPwd;
    @FXML private Label lblError;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML private void handleRegister() {}

    @FXML private void handleBack() {}
}