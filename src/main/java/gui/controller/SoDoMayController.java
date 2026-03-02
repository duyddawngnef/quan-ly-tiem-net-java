package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import java.net.URL;
import java.util.ResourceBundle;

public class SoDoMayController implements Initializable {

    @FXML private ComboBox<String> cmbKhuMay;
    @FXML private FlowPane machineGrid;
    @FXML private Label lblTong;
    @FXML private Label lblTrong;
    @FXML private Label lblDangDung;
    @FXML private Label lblBaoTri;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML private void handleFilter() {}

    @FXML private void handleRefresh() {}
}