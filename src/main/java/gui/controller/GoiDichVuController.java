package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class GoiDichVuController implements Initializable {

    @FXML private TextField txtSearch;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;

    @FXML private TableView<?> tableGoi;
    @FXML private TableColumn<?, ?> colMaGoi;
    @FXML private TableColumn<?, ?> colTenGoi;
    @FXML private TableColumn<?, ?> colSoGio;
    @FXML private TableColumn<?, ?> colGia;
    @FXML private TableColumn<?, ?> colUuDai;
    @FXML private TableColumn<?, ?> colHanSuDung;
    @FXML private TableColumn<?, ?> colMoTa;
    @FXML private TableColumn<?, ?> colTrangThai;

    @FXML private Label lblFormTitle;
    @FXML private TextField txtMaGoi;
    @FXML private TextField txtTenGoi;
    @FXML private TextField txtSoGio;
    @FXML private TextField txtGia;
    @FXML private TextField txtUuDai;
    @FXML private TextField txtHanSuDung;
    @FXML private TextArea txtMoTa;

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