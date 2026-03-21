package gui.controller;

import bus.PhienSuDungBUS;
import dao.GoiDichVuKhachHangDAO;
import dao.HoaDonDAO;
import dao.KhachHangDAO;
import dao.MayTinhDAO;
import dao.PhienSuDungDAO;
import dao.SuDungDichVuDAO;
import entity.PhienSuDung;
import utils.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class PhienCuaToiController implements Initializable {

    @FXML private VBox    cardPhienDangChay;
    @FXML private Label   lblMaMay;
    @FXML private Label   lblGioBatDau;
    @FXML private Label   lblLoaiTT;
    @FXML private Label   lblKhongCoPhien;
    @FXML private Button  btnKetThucPhien;

    @FXML private TableView<PhienSuDung> tablePhien;
    @FXML private TableColumn<PhienSuDung, String> colMaPhien;
    @FXML private TableColumn<PhienSuDung, String> colMaMay;
    @FXML private TableColumn<PhienSuDung, String> colGioBD;
    @FXML private TableColumn<PhienSuDung, String> colGioKT;
    @FXML private TableColumn<PhienSuDung, String> colTongGio;
    @FXML private TableColumn<PhienSuDung, String> colTienChoi;
    @FXML private TableColumn<PhienSuDung, String> colTrangThai;

    private final PhienSuDungDAO phienDAO = new PhienSuDungDAO();
    private final PhienSuDungBUS phienBUS = new PhienSuDungBUS(
            new PhienSuDungDAO(), new MayTinhDAO(), new KhachHangDAO(),
            new GoiDichVuKhachHangDAO(), new SuDungDichVuDAO(), new HoaDonDAO());

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupColumns();
        loadData();
    }

    private void setupColumns() {
        colMaPhien.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMaPhien()));
        colMaMay.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMaMay()));
        colGioBD.setCellValueFactory(d -> {
            var t = d.getValue().getGioBatDau();
            return new SimpleStringProperty(t != null ? t.format(dtf) : "");
        });
        colGioKT.setCellValueFactory(d -> {
            var t = d.getValue().getGioKetThuc();
            return new SimpleStringProperty(t != null ? t.format(dtf) : "Đang chạy");
        });
        colTongGio.setCellValueFactory(d ->
                new SimpleStringProperty(String.format("%.2f h", d.getValue().getTongGio())));
        colTienChoi.setCellValueFactory(d ->
                new SimpleStringProperty(String.format("%,.0f ₫", d.getValue().getTienGioChoi())));
        colTrangThai.setCellValueFactory(d -> {
            String tt = d.getValue().getTrangThai();
            return new SimpleStringProperty("DANGCHOI".equals(tt) ? "🟢 Đang chạy" : "✅ Đã kết thúc");
        });
        btnKetThucPhien.setOnAction(e -> handleKetThucPhien());

    }

    private void loadData() {
        String maKH = SessionManager.getCurrentMaKH();
        if (maKH == null) return;

        try {
            // Phiên đang chạy
            PhienSuDung phienHienTai = phienDAO.getPhienDangChoiByKhachHang(maKH);
            if (phienHienTai != null) {
                cardPhienDangChay.setVisible(true);
                cardPhienDangChay.setManaged(true);
                lblKhongCoPhien.setVisible(false);
                lblKhongCoPhien.setManaged(false);

                lblMaMay.setText(phienHienTai.getMaMay());
                lblGioBatDau.setText(phienHienTai.getGioBatDau() != null
                        ? phienHienTai.getGioBatDau().format(dtf) : "");
                lblLoaiTT.setText(phienHienTai.getLoaiThanhToan());
            } else {
                cardPhienDangChay.setVisible(false);
                cardPhienDangChay.setManaged(false);
                lblKhongCoPhien.setVisible(true);
                lblKhongCoPhien.setManaged(true);
            }

            // Lịch sử tất cả phiên
            ArrayList<PhienSuDung> list = phienDAO.getPhienByKhachHang(maKH);
            tablePhien.setItems(FXCollections.observableArrayList(list));

        } catch (Exception e) {
            System.err.println("Lỗi load phiên: " + e.getMessage());
        }
    }

    @FXML
    private void handleLamMoi() {
        loadData();
    }

    @FXML
    private void handleKetThucPhien() {
        String maKH = SessionManager.getCurrentMaKH();
        if (maKH == null) return;

        // Hỏi xác nhận trước khi kết thúc phiên
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận kết thúc phiên");
        confirm.setHeaderText("Bạn muốn kết thúc phiên sử dụng?");
        confirm.setContentText("Phiên sẽ bị kết thúc ngay lập tức và hóa đơn sẽ được lập.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return; // Người dùng hủy
        }

        try {
            phienBUS.ketThucPhienKhiDangXuat(maKH);
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Thành công");
            success.setHeaderText(null);
            success.setContentText("✅ Phiên đã được kết thúc thành công!");
            success.showAndWait();

            // Reload lại UI
            loadData();
        } catch (Exception e) {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Lỗi");
            err.setHeaderText("Không thể kết thúc phiên");
            err.setContentText(e.getMessage());
            err.showAndWait();
        }
    }
}
