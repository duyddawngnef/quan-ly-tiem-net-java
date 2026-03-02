package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class KhuMayController implements Initializable {

    @FXML private TextField txtSearch;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;

    @FXML private TableView<?> tableKhuMay;
    @FXML private TableColumn<?, ?> colMaKhu;
    @FXML private TableColumn<?, ?> colTenKhu;
    @FXML private TableColumn<?, ?> colViTri;
    @FXML private TableColumn<?, ?> colSoMay;
    @FXML private TableColumn<?, ?> colGhiChu;

    @FXML private Label lblFormTitle;
    @FXML private TextField txtMaKhu;
    @FXML private TextField txtTenKhu;
    @FXML private TextField txtViTri;
    @FXML private TextArea txtGhiChu;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML private void handleSearch() {}

    @FXML private void handleRowSelect() {}

    @FXML private void handleThem() {}

    @FXML private void handleSua() {}

    @FXML private void handleXoa() {}

    @FXML private void handleSave() {}

    @FXML private void handleCancel() {}
}