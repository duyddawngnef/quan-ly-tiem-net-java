package gui.dialog;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ChonDichVuDialog implements Initializable {

    @FXML private TextField txtSearch;
    @FXML private TableView<?> tableDichVu;
    @FXML private TableColumn<?, ?> colMaDV;
    @FXML private TableColumn<?, ?> colTenDV;
    @FXML private TableColumn<?, ?> colLoai;
    @FXML private TableColumn<?, ?> colGia;
    @FXML private TableColumn<?, ?> colTonKho;
    @FXML private TextField txtSoLuong;
    @FXML private Label lblError;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML private void handleSearch() {}

    @FXML private void handleChon() {}

    @FXML private void handleCancel() {}
}