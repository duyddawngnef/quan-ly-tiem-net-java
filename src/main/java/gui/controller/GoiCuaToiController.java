package gui.controller;

import dao.GoiDichVuKhachHangDAO;
import entity.GoiDichVuKhachHang;
import utils.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class GoiCuaToiController implements Initializable {

    @FXML private TableView<GoiDichVuKhachHang> tableGoi;
    @FXML private TableColumn<GoiDichVuKhachHang, String> colMaGoiKH;
    @FXML private TableColumn<GoiDichVuKhachHang, String> colTenGoi;
    @FXML private TableColumn<GoiDichVuKhachHang, String> colSoGioBD;
    @FXML private TableColumn<GoiDichVuKhachHang, String> colSoGioConLai;
    @FXML private TableColumn<GoiDichVuKhachHang, String> colNgayMua;
    @FXML private TableColumn<GoiDichVuKhachHang, String> colNgayHetHan;
    @FXML private TableColumn<GoiDichVuKhachHang, String> colGiaMua;
    @FXML private TableColumn<GoiDichVuKhachHang, String> colTrangThai;
    @FXML private Label lblTotal;

    private final GoiDichVuKhachHangDAO goiDAO = new GoiDichVuKhachHangDAO();
    private final NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupColumns();
        loadData();
    }

    private void setupColumns() {
        colMaGoiKH.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMagoikh()));
        // MaGoi dùng làm tên gói (có thể join với bảng GoiDichVu sau nếu cần)
        colTenGoi.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMagoi()));
        colSoGioBD.setCellValueFactory(d ->
                new SimpleStringProperty(String.format("%.1f h", d.getValue().getSogiobandau())));
        colSoGioConLai.setCellValueFactory(d ->
                new SimpleStringProperty(String.format("%.1f h", d.getValue().getSogioconlai())));
        colNgayMua.setCellValueFactory(d -> {
            var t = d.getValue().getNgaymua();
            return new SimpleStringProperty(t != null ? t.format(dtf) : "");
        });
        colNgayHetHan.setCellValueFactory(d -> {
            var t = d.getValue().getNgayhethan();
            return new SimpleStringProperty(t != null ? t.format(dtf) : "");
        });
        colGiaMua.setCellValueFactory(d ->
                new SimpleStringProperty(fmt.format(d.getValue().getGiamua()) + " ₫"));
        colTrangThai.setCellValueFactory(d -> {
            String tt = d.getValue().getTrangthai();
            if ("CONHAN".equals(tt)) return new SimpleStringProperty("✅ Còn hạn");
            return new SimpleStringProperty("❌ " + (tt != null ? tt : "Hết hạn"));
        });
    }

    private void loadData() {
        String maKH = SessionManager.getCurrentMaKH();
        if (maKH == null) return;

        List<GoiDichVuKhachHang> list = goiDAO.getByKhachHang(maKH);
        if (list != null) {
            tableGoi.setItems(FXCollections.observableArrayList(list));
            long active = list.stream().filter(g -> "CONHAN".equals(g.getTrangthai())).count();
            if (lblTotal != null)
                lblTotal.setText("Tổng: " + list.size() + " gói | Còn hạn: " + active);
        } else {
            tableGoi.setItems(FXCollections.emptyObservableList());
            if (lblTotal != null) lblTotal.setText("Bạn chưa có gói dịch vụ nào.");
        }
    }

    @FXML
    private void handleLamMoi() {
        loadData();
    }
}
