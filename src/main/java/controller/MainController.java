package controller;

import javafx.fxml.FXML;
import javafx.fmxl.Initalizable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private StackPane contentPane;

    @FXML
    private Label lblUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblUser.setText("Nhan vien: admin");
    }
}
