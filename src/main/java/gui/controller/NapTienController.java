package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.util.ResourceBundle;

public class NapTienController implements Initializable {

    @FXML private TextField txtTimKH;
    @FXML private TextField txtSoTienNap;
    @FXML private TextField txtGhiChu;
    @FXML private TextField txtNhanVien;
    @FXML private VBox infoKHPane;
    @FXML private Label lblMaKH;
    @FXML private Label lblHoTen;
    @FXML private Label lblSoDu;
    @FXML private Label lblDiem;
    @FXML private Label lblError;
    @FXML private DatePicker dpFilter;

    @FXML private TableView<?> tableLichSu;
    @FXML private TableColumn<?, ?> colMaNap;
    @FXML private TableColumn<?, ?> colMaKH;
    @FXML private TableColumn<?, ?> colSoTien;
    @FXML private TableColumn<?, ?> colNgayNap;
    @FXML private TableColumn<?, ?> colNhanVien;
    @FXML private TableColumn<?, ?> colGhiChu;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML private void handleTimKH() {}

    @FXML private void handleChonKH() {}

    @FXML private void handleQuickAmount(javafx.event.ActionEvent e) {}

    @FXML private void handleNapTien() {}

    @FXML private void handleFilter() {}
}