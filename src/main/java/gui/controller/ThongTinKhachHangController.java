package gui.controller;

import dao.KhachHangDAO;
import dao.LichSuNapTienDAO;
import entity.KhachHang;
import entity.LichSuNapTien;
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

public class ThongTinKhachHangController implements Initializable {

    @FXML private Label lblHoTen;
    @FXML private Label lblTenDangNhap;
    @FXML private Label lblSdt;
    @FXML private Label lblMaKH;
    @FXML private Label lblTrangThai;
    @FXML private Label lblSoDu;
    @FXML private Label lblTongNap;

    @FXML private TableView<LichSuNapTien> tableNapTien;
    @FXML private TableColumn<LichSuNapTien, String> colMaGD;
    @FXML private TableColumn<LichSuNapTien, String> colNgayNap;
    @FXML private TableColumn<LichSuNapTien, String> colSoTien;
    @FXML private TableColumn<LichSuNapTien, String> colNhanVien;
    @FXML private TableColumn<LichSuNapTien, String> colGhiChu;

    @FXML private Button btn_load;

    private final LichSuNapTienDAO lichSuNapTienDAO = new LichSuNapTienDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupColumns();
        loadData();
    }

    private void setupColumns() {
        colMaGD.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMaNap()));
        colNgayNap.setCellValueFactory(d -> {
            var nd = d.getValue().getNgayNap();
            return new SimpleStringProperty(nd != null ? nd.format(dtf) : "");
        });
        colSoTien.setCellValueFactory(d ->
                new SimpleStringProperty(fmt.format(d.getValue().getSoTienNap()) + " ₫"));
        colNhanVien.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getMaNV() != null ? d.getValue().getMaNV() : ""));
        colGhiChu.setCellValueFactory(d -> {
            // Trường ghi chú có thể không tồn tại — dùng MaGiaoDich thay thế
            String gd = d.getValue().getMaGiaoDich();
            return new SimpleStringProperty(gd != null ? gd : "");
        });
        btn_load.setOnAction(e -> loadNapTien());
    }

    private void loadData() {
        KhachHang kh = SessionManager.getCurrentKhachHang();
        if (kh == null) return;

        // Reload thông tin mới nhất từ DB
        KhachHang fresh = khachHangDAO.getById(kh.getMakh());
        if (fresh != null) kh = fresh;

        lblHoTen.setText(kh.getHo() + " " + kh.getTen());
        lblTenDangNhap.setText(kh.getTendangnhap());
        lblSdt.setText(kh.getSodienthoai() != null ? kh.getSodienthoai() : "Chưa cập nhật");
        lblMaKH.setText(kh.getMakh());
        lblTrangThai.setText("HOATDONG".equals(kh.getTrangthai()) ? "✅ Hoạt động" : "🔒 Bị khóa");
        lblSoDu.setText(fmt.format(kh.getSodu()) + " ₫");

        // Load lịch sử nạp tiền
        try {
            List<LichSuNapTien> list = lichSuNapTienDAO.timTheoKhachHang(kh.getMakh());
            tableNapTien.setItems(FXCollections.observableArrayList(list));
            double tong = list.stream().mapToDouble(LichSuNapTien::getTongTienCong).sum();
            if (lblTongNap != null)
                lblTongNap.setText("Tổng đã nạp: " + fmt.format(tong) + " ₫");
        } catch (Exception e) {
            System.err.println("Lỗi load lịch sử nạp tiền: " + e.getMessage());
        }
    }

    @FXML
    private void handleLamMoi() {
        loadData();
    }

    @FXML
    public  void loadNapTien(){
        // Load lịch sử nạp tiền
        try {
            List<LichSuNapTien> list = lichSuNapTienDAO.timTheoKhachHang(lblMaKH.getText());
            tableNapTien.setItems(FXCollections.observableArrayList(list));
            double tong = list.stream().mapToDouble(LichSuNapTien::getTongTienCong).sum();
            if (lblTongNap != null)
                lblTongNap.setText("Tổng đã nạp: " + fmt.format(tong) + " ₫");
        } catch (Exception e) {
            System.err.println("Lỗi load lịch sử nạp tiền: " + e.getMessage());
        }
    }
}
