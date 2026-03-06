package gui.controller;

import bus.GoiDichVuBUS;
import bus.GoiDichVuKhachHangBUS;
import dao.GoiDichVuKhachHangDAO;
import dao.KhachHangDAO;
import entity.GoiDichVu;
import entity.GoiDichVuKhachHang;
import entity.KhachHang;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * MuaGoiController - Controller xử lý mua gói dịch vụ cho khách hàng
 *
 * Chức năng:
 * - Tìm khách hàng theo mã
 * - Hiển thị danh sách gói dịch vụ có sẵn
 * - Xem chi tiết gói được chọn + tính toán số dư sau khi mua
 * - Mua gói dịch vụ cho khách hàng (trừ tiền, tạo GoiDichVuKhachHang)
 * - Hiển thị danh sách gói đã mua còn hiệu lực của khách hàng
 */
public class MuaGoiController {

    // ============== FXML COMPONENTS - Thông tin KH ==============

    @FXML
    private TextField txtMaKH;
    @FXML
    private Label lblTenKH;
    @FXML
    private Label lblSoDu;
    @FXML
    private Label lblSDT;

    // ============== FXML COMPONENTS - Bảng gói dịch vụ ==============

    @FXML
    private TableView<GoiDichVu> tableGoiDV;
    @FXML
    private TableColumn<GoiDichVu, String> colMaGoi;
    @FXML
    private TableColumn<GoiDichVu, String> colTenGoi;
    @FXML
    private TableColumn<GoiDichVu, String> colLoaiGoi;
    @FXML
    private TableColumn<GoiDichVu, Double> colSoGio;
    @FXML
    private TableColumn<GoiDichVu, Integer> colSoNgayHL;
    @FXML
    private TableColumn<GoiDichVu, Double> colGiaGoi;
    @FXML
    private TableColumn<GoiDichVu, String> colApDungKhu;
    @FXML
    private TableColumn<GoiDichVu, String> colTrangThai;

    // ============== FXML COMPONENTS - Chi tiết gói chọn ==============

    @FXML
    private Label lblMaGoi;
    @FXML
    private Label lblTenGoi;
    @FXML
    private Label lblLoaiGoi;
    @FXML
    private Label lblSoGio;
    @FXML
    private Label lblHieuLuc;
    @FXML
    private Label lblGiaGoc;
    @FXML
    private Label lblGiaGoi;
    @FXML
    private Label lblSoDuSauMua;

    // ============== FXML COMPONENTS - Bảng gói KH đã mua ==============

    @FXML
    private TableView<GoiDichVuKhachHang> tableGoiKH;
    @FXML
    private TableColumn<GoiDichVuKhachHang, String> colGoiKH_Ma;
    @FXML
    private TableColumn<GoiDichVuKhachHang, String> colGoiKH_TenGoi;
    @FXML
    private TableColumn<GoiDichVuKhachHang, Double> colGoiKH_GioConLai;
    @FXML
    private TableColumn<GoiDichVuKhachHang, String> colGoiKH_HetHan;

    // ============== FXML COMPONENTS - Khác ==============

    @FXML
    private Button btnTimKH;
    @FXML
    private Button btnMuaGoi;
    @FXML
    private Label lblTrangThai;

    // ============== BUSINESS LAYER ==============

    private final GoiDichVuBUS goiDichVuBUS = new GoiDichVuBUS();
    private final GoiDichVuKhachHangBUS goiDichVuKhachHangBUS = new GoiDichVuKhachHangBUS();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final GoiDichVuKhachHangDAO goiDichVuKhachHangDAO = new GoiDichVuKhachHangDAO();

    private final DecimalFormat df = new DecimalFormat("#,###");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ObservableList<GoiDichVu> danhSachGoiDV = FXCollections.observableArrayList();
    private ObservableList<GoiDichVuKhachHang> danhSachGoiKH = FXCollections.observableArrayList();

    private KhachHang khachHangHienTai = null;
    private GoiDichVu goiDuocChon = null;

    // ============== INITIALIZE ==============

    @FXML
    public void initialize() {
        setupTableGoiDV();
        setupTableGoiKH();
        setupGoiDVSelection();
        loadDanhSachGoiDV();
    }

    /**
     * Cấu hình cột cho bảng gói dịch vụ có sẵn
     */
    private void setupTableGoiDV() {
        colMaGoi.setCellValueFactory(new PropertyValueFactory<>("maGoi"));
        colTenGoi.setCellValueFactory(new PropertyValueFactory<>("tenGoi"));
        colLoaiGoi.setCellValueFactory(new PropertyValueFactory<>("loaiGoi"));
        colSoGio.setCellValueFactory(new PropertyValueFactory<>("soGio"));
        colSoNgayHL.setCellValueFactory(new PropertyValueFactory<>("soNgayHieuLuc"));
        colGiaGoi.setCellValueFactory(new PropertyValueFactory<>("giaGoi"));
        colApDungKhu.setCellValueFactory(new PropertyValueFactory<>("apDungChoKhu"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));

        // Format cột giá tiền
        colGiaGoi.setCellFactory(col -> new TableCell<GoiDichVu, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : df.format(item) + " đ");
            }
        });

        tableGoiDV.setItems(danhSachGoiDV);
    }

    /**
     * Cấu hình cột cho bảng gói KH đã mua
     */
    private void setupTableGoiKH() {
        colGoiKH_Ma.setCellValueFactory(new PropertyValueFactory<>("maGoiKH"));
        colGoiKH_TenGoi.setCellValueFactory(new PropertyValueFactory<>("maGoi"));
        colGoiKH_GioConLai.setCellValueFactory(new PropertyValueFactory<>("soGioConLai"));
        colGoiKH_HetHan.setCellValueFactory(cellData -> {
            GoiDichVuKhachHang g = cellData.getValue();
            String hetHan = g.getNgayHetHan() != null ? g.getNgayHetHan().format(dtf) : "—";
            return new SimpleStringProperty(hetHan);
        });

        tableGoiKH.setItems(danhSachGoiKH);
    }

    /**
     * Bắt sự kiện chọn gói trên bảng
     */
    private void setupGoiDVSelection() {
        tableGoiDV.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    goiDuocChon = newVal;
                    if (newVal != null) {
                        hienThiChiTietGoi(newVal);
                    } else {
                        clearChiTietGoi();
                    }
                });
    }

    // ============== LOAD DỮ LIỆU ==============

    /**
     * Load danh sách gói dịch vụ đang hoạt động
     */
    private void loadDanhSachGoiDV() {
        try {
            List<GoiDichVu> list = goiDichVuBUS.getDanhSachGoiDV();
            // Chỉ hiển thị gói đang hoạt động
            list.removeIf(g -> !"HOATDONG".equals(g.getTrangThai()) && !"CONHAN".equals(g.getTrangThai()));
            danhSachGoiDV.setAll(list);
            setTrangThai("Đã tải " + list.size() + " gói dịch vụ đang hoạt động");
        } catch (Exception e) {
            showError("Lỗi tải dữ liệu", e.getMessage());
        }
    }

    /**
     * Load danh sách gói đã mua của KH hiện tại
     */
    private void loadGoiKhachHang(String maKH) {
        try {
            List<GoiDichVuKhachHang> list = goiDichVuKhachHangDAO.getConHieuLuc(maKH);
            danhSachGoiKH.setAll(list);
        } catch (Exception e) {
            System.err.println("Lỗi load gói KH: " + e.getMessage());
        }
    }

    // ============== EVENT HANDLERS ==============

    /**
     * Xử lý nút Tìm khách hàng
     */
    @FXML
    private void handleTimKH() {
        String maKH = txtMaKH.getText().trim();
        if (maKH.isEmpty()) {
            showWarning("Thiếu thông tin", "Vui lòng nhập mã khách hàng");
            return;
        }

        KhachHang kh = khachHangDAO.getById(maKH);
        if (kh == null) {
            showWarning("Không tìm thấy", "Không tìm thấy khách hàng với mã: " + maKH);
            clearThongTinKH();
            return;
        }

        // Hiển thị thông tin KH
        khachHangHienTai = kh;
        lblTenKH.setText(kh.getHo() + " " + kh.getTen());
        lblSoDu.setText(df.format(kh.getSodu()) + " đ");
        lblSDT.setText(kh.getSodienthoai() != null ? kh.getSodienthoai() : "—");

        // Load gói đã mua
        loadGoiKhachHang(maKH);

        // Cập nhật số dư sau mua nếu đã chọn gói
        if (goiDuocChon != null) {
            capNhatSoDuSauMua();
        }

        setTrangThai("Đã tìm thấy KH: " + kh.getHo() + " " + kh.getTen());
    }

    /**
     * Xử lý nút Mua gói
     */
    @FXML
    private void handleMuaGoi() {
        // Kiểm tra đã chọn KH chưa
        if (khachHangHienTai == null) {
            showWarning("Chưa chọn khách hàng", "Vui lòng tìm và chọn khách hàng trước khi mua gói");
            return;
        }

        // Kiểm tra đã chọn gói chưa
        if (goiDuocChon == null) {
            showWarning("Chưa chọn gói", "Vui lòng chọn một gói dịch vụ trên bảng");
            return;
        }

        // Xác nhận mua
        String confirmMsg = String.format(
                "Mua gói: %s\nCho KH: %s %s\nGiá: %s đ\nSố dư hiện tại: %s đ",
                goiDuocChon.getTenGoi(),
                khachHangHienTai.getHo(), khachHangHienTai.getTen(),
                df.format(goiDuocChon.getGiaGoi()),
                df.format(khachHangHienTai.getSodu()));

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận mua gói");
        confirm.setHeaderText("Xác nhận mua gói dịch vụ");
        confirm.setContentText(confirmMsg);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean result = goiDichVuKhachHangBUS.muaGoi(
                            khachHangHienTai.getMakh(),
                            goiDuocChon.getMaGoi());

                    if (result) {
                        showInfo("Thành công",
                                "Đã mua gói " + goiDuocChon.getTenGoi() +
                                        " cho KH " + khachHangHienTai.getHo() + " " + khachHangHienTai.getTen());

                        // Refresh lại thông tin KH (số dư đã thay đổi)
                        refreshThongTinKH();
                        // Refresh gói đã mua
                        loadGoiKhachHang(khachHangHienTai.getMakh());

                        setTrangThai("Mua gói thành công!");
                    } else {
                        showError("Thất bại", "Không thể mua gói dịch vụ");
                    }
                } catch (Exception e) {
                    showError("Lỗi mua gói", e.getMessage());
                }
            }
        });
    }

    // ============== HIỂN THỊ HELPERS ==============

    /**
     * Hiển thị chi tiết gói được chọn
     */
    private void hienThiChiTietGoi(GoiDichVu g) {
        lblMaGoi.setText(g.getMaGoi());
        lblTenGoi.setText(g.getTenGoi());
        lblLoaiGoi.setText(g.getLoaiGoi());
        lblSoGio.setText(g.getSoGio() + " giờ");
        lblHieuLuc.setText(g.getSoNgayHieuLuc() + " ngày");
        lblGiaGoc.setText(df.format(g.getGiaGoc()) + " đ");
        lblGiaGoi.setText(df.format(g.getGiaGoi()) + " đ");

        capNhatSoDuSauMua();
    }

    /**
     * Tính toán và hiển thị số dư sau khi mua
     */
    private void capNhatSoDuSauMua() {
        if (khachHangHienTai != null && goiDuocChon != null) {
            double soDuSau = khachHangHienTai.getSodu() - goiDuocChon.getGiaGoi();
            if (soDuSau >= 0) {
                lblSoDuSauMua.setText(df.format(soDuSau) + " đ");
                lblSoDuSauMua.setStyle("-fx-font-weight: bold; -fx-text-fill: #4CAF50;");
            } else {
                lblSoDuSauMua.setText(df.format(soDuSau) + " đ (KHÔNG ĐỦ)");
                lblSoDuSauMua.setStyle("-fx-font-weight: bold; -fx-text-fill: #F44336;");
            }
        } else {
            lblSoDuSauMua.setText("—");
        }
    }

    /**
     * Xóa chi tiết gói
     */
    private void clearChiTietGoi() {
        lblMaGoi.setText("—");
        lblTenGoi.setText("—");
        lblLoaiGoi.setText("—");
        lblSoGio.setText("—");
        lblHieuLuc.setText("—");
        lblGiaGoc.setText("—");
        lblGiaGoi.setText("—");
        lblSoDuSauMua.setText("—");
    }

    /**
     * Xóa thông tin KH
     */
    private void clearThongTinKH() {
        khachHangHienTai = null;
        lblTenKH.setText("—");
        lblSoDu.setText("—");
        lblSDT.setText("—");
        danhSachGoiKH.clear();
        capNhatSoDuSauMua();
    }

    /**
     * Refresh thông tin KH sau khi mua (vì số dư đã thay đổi)
     */
    private void refreshThongTinKH() {
        if (khachHangHienTai != null) {
            KhachHang kh = khachHangDAO.getById(khachHangHienTai.getMakh());
            if (kh != null) {
                khachHangHienTai = kh;
                lblTenKH.setText(kh.getHo() + " " + kh.getTen());
                lblSoDu.setText(df.format(kh.getSodu()) + " đ");
                capNhatSoDuSauMua();
            }
        }
    }

    /**
     * Cập nhật label trạng thái
     */
    private void setTrangThai(String msg) {
        if (lblTrangThai != null) {
            lblTrangThai.setText(msg);
        }
    }

    // ============== DIALOG HELPERS ==============

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
        setTrangThai("Lỗi: " + content);
    }
}
