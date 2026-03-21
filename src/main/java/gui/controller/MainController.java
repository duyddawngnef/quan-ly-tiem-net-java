package gui.controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import bus.PhienSuDungBUS;
import dao.GoiDichVuKhachHangDAO;
import dao.HoaDonDAO;
import dao.KhachHangDAO;
import dao.MayTinhDAO;
import dao.PhienSuDungDAO;
import dao.SuDungDichVuDAO;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhienSuDung;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import utils.SessionManager;

public class MainController implements Initializable {

    // ================================================================
    //  PHẦN 1: FXML COMPONENTS
    // ================================================================
    @FXML
    private Label lblAvatarChar;
    @FXML
    private Label lblUserName;
    @FXML
    private Label lblUserRole;
    @FXML
    private Label lblPageTitle;
    @FXML
    private Label lblCurrentTime;
    @FXML
    private StackPane contentPane;

    // --- Menu nhân viên / quản lý ---
    @FXML
    private Label lblSectionTongQuan, lblSectionVanHanh;
    @FXML
    private Label lblSectionDichVuNV, lblSectionQuanLy, lblSectionBaoCao;
    @FXML
    private Button btnSoDoMay, btnPhienSuDung, btnNapTien, btnHoaDon;
    @FXML
    private Button btnDichVu, btnGoiDichVu, btnGoiDichVuKH, btnKhuyenMai, btnNhapHang;
    @FXML
    private Button btnKhachHang, btnNhanVien, btnMayTinh, btnKhuMay, btnThongKe;

    // --- Menu khách hàng ---
    @FXML
    private Label lblSectionTaiKhoan, lblSectionSuDung, lblSectionOrderKH;
    @FXML
    private Button btnThongTinKH, btnPhienCuaToi, btnGoiCuaToi, btnDatDichVu;

    // ================================================================
    //  PHẦN 2: BUS
    // ================================================================
    private final PhienSuDungBUS phienBUS = new PhienSuDungBUS(
            new PhienSuDungDAO(), new MayTinhDAO(), new KhachHangDAO(),
            new GoiDichVuKhachHangDAO(), new SuDungDichVuDAO(), new HoaDonDAO());

    /**
     * Timer chạy định kỳ để tự kết thúc phiên khi KH hết tiền/gói
     */
    private Timeline phienCheckTimer;

    // ================================================================
    //  PHẦN 3: KHỞI TẠO
    // ================================================================
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            updateHeader();
            setupMenuByRole();

            // Đăng ký hook đóng cửa sổ (auto-kết thúc phiên cho KH)
            Platform.runLater(this::dangKyDongCuaSo);

            // Bắt đầu timer kiểm tra phiên quá hạn định kỳ (mỗi 2 phút)
            batDauTimerKiemTraPhien();

            // Trang mặc định tuỳ theo role
            if (SessionManager.isKhachHang()) {
                // Auto mở phiên ngay khi KH đăng nhập thành công
                autoMoPhienKhachHang();
                loadThongTinKhachHang();
            } else {
                loadSoDoMay();
                kiemTraPhienQuaHanSauDangNhap();
            }
        } catch (Exception e) {
            System.err.println("Lỗi khởi tạo màn hình Main: " + e.getMessage());
        }
    }

    /**
     * Mở phiên tự động cho khách hàng vừa đăng nhập
     */
    private void autoMoPhienKhachHang() {
        String maKH = SessionManager.getCurrentMaKH();
        if (maKH == null) {
            return;
        }
        try {
            PhienSuDung phien = phienBUS.moPhienTuDongKhiDangNhap(maKH);
            if (phien != null) {
                System.out.println("[Main] Auto-mở phiên: " + phien.getMaPhien()
                        + " tại máy " + phien.getMaMay());
            } else {
                System.out.println("[Main] Không thể auto-mở phiên (không đủ điều kiện hoặc không có máy trống).");
            }
        } catch (Exception e) {
            System.err.println("[Main] Lỗi auto-mở phiên: " + e.getMessage());
        }
    }

    /**
     * Đăng ký sự kiện đóng cửa sổ — logout sạch session, phiên vẫn tiếp tục
     * chạy
     */
    private void dangKyDongCuaSo() {
        if (contentPane == null || contentPane.getScene() == null) {
            return;
        }
        Stage stage = (Stage) contentPane.getScene().getWindow();
        if (stage == null) {
            return;
        }
        stage.setOnCloseRequest((WindowEvent event) -> {
            // Phiên tiếp tục chạy, chỉ logout session
            SessionManager.logout();
        });
    }

    private void kiemTraPhienQuaHanSauDangNhap() {
        try {
            List<PhienSuDung> danhSachDaKetThuc = phienBUS.kiemTraVaKetThucPhienQuaHan();
            if (!danhSachDaKetThuc.isEmpty()) {
                System.out.printf("[Đăng nhập] Đã tự động kết thúc %d phiên quá hạn.%n",
                        danhSachDaKetThuc.size());
            } else {
                System.out.println("[Đăng nhập] Không có phiên nào quá hạn.");
            }
        } catch (Exception e) {
            System.err.println("[Đăng nhập] Lỗi kiểm tra phiên: " + e.getMessage());
        }
    }

    /**
     * Bắt đầu timer định kỳ kiểm tra phiên quá hạn (mỗi 2 phút). Nếu phát hiện
     * KH hiện tại bị kết thúc, hiện thông báo và buộc logout.
     */
    private void batDauTimerKiemTraPhien() {
        phienCheckTimer = new Timeline(
                new KeyFrame(Duration.minutes(2), event -> {
                    try {
                        List<PhienSuDung> ketThuc = phienBUS.kiemTraVaKetThucPhienQuaHan();
                        if (!ketThuc.isEmpty() && SessionManager.isKhachHang()) {
                            // Kiểm tra xem KH hiện tại có nằm trong danh sách bị kết thúc không
                            String maKHHienTai = SessionManager.getCurrentMaKH();
                            boolean khBiKetThuc = ketThuc.stream()
                                    .anyMatch(p -> maKHHienTai != null && maKHHienTai.equals(p.getMaKH()));
                            if (khBiKetThuc) {
                                Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                    alert.setTitle("Phìiên đã kết thúc");
                                    alert.setHeaderText("Tài khoản đã hết tiền và hết giờ gói!");
                                    alert.setContentText("Ð phiên sử dụng của bạn đã bị tự động kết thúc \n"
                                            + "vì tài khoản hết số dư và không còn giờ gói.\n"
                                            + "Vui lòng nạp tiền hoặc mua gói để tiếp tục.");
                                    alert.showAndWait();
                                });
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("[Timer] Lỗi kiểm tra phiên: " + e.getMessage());
                    }
                })
        );
        phienCheckTimer.setCycleCount(Timeline.INDEFINITE);
        phienCheckTimer.play();
        System.out.println("[Timer] Đã bắt đầu timer kiểm tra phiên (mỗi 2 phút).");
    }

    // ================================================================
    //  PHẦN 4: CẬP NHẬT HEADER & PHÂN QUYỀN MENU
    // ================================================================
    private void updateHeader() {
        if (SessionManager.isNhanVien()) {
            NhanVien nv = SessionManager.getCurrentNhanVien();
            if (nv != null) {
                if (lblUserName != null) {
                    lblUserName.setText(nv.getHo() + " " + nv.getTen());
                }
                if (lblUserRole != null) {
                    lblUserRole.setText(
                            "QUANLY".equals(nv.getChucvu()) ? "Quản lý" : "Nhân viên");
                }
                if (lblAvatarChar != null && nv.getTen() != null && !nv.getTen().isEmpty()) {
                    lblAvatarChar.setText(String.valueOf(nv.getTen().charAt(0)).toUpperCase());
                }
            }
        } else if (SessionManager.isKhachHang()) {
            KhachHang kh = SessionManager.getCurrentKhachHang();
            if (kh != null) {
                if (lblUserName != null) {
                    lblUserName.setText(kh.getHo() + " " + kh.getTen());
                }
                if (lblUserRole != null) {
                    lblUserRole.setText("Khách hàng");
                }
                if (lblAvatarChar != null && kh.getTen() != null && !kh.getTen().isEmpty()) {
                    lblAvatarChar.setText(String.valueOf(kh.getTen().charAt(0)).toUpperCase());
                }
            }
        }
        if (lblCurrentTime != null) {
            lblCurrentTime.setText(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(LocalDateTime.now()));
        }
    }

    private void setupMenuByRole() {
        if (SessionManager.isKhachHang()) {
            // ==== KHÁCH HÀNG: ẩn toàn bộ NhanVien menu, hiện menu KH ====
            hideLabel(lblSectionTongQuan);
            hideLabel(lblSectionVanHanh);
            hideLabel(lblSectionDichVuNV);
            hideLabel(lblSectionQuanLy);
            hideLabel(lblSectionBaoCao);
            hide(btnSoDoMay);
            hide(btnPhienSuDung);
            hide(btnNapTien);
            hide(btnHoaDon);
            hide(btnDichVu);
            hide(btnGoiDichVu);
            hide(btnGoiDichVuKH);
            hide(btnKhuyenMai);
            hide(btnNhapHang);
            hide(btnKhachHang);
            hide(btnNhanVien);
            hide(btnMayTinh);
            hide(btnKhuMay);
            hide(btnThongKe);

            // Hiện menu khách hàng
            showLabel(lblSectionTaiKhoan);
            showLabel(lblSectionSuDung);
            showLabel(lblSectionOrderKH);
            show(btnThongTinKH);
            show(btnPhienCuaToi);
            show(btnGoiCuaToi);
            show(btnDatDichVu);

        } else if (!SessionManager.isQuanLy()) {
            // ==== NHÂN VIÊN thường: ẩn 4 menu admin + ẩn menu KH ====
            hide(btnNhanVien);
            hide(btnThongKe);
            hide(btnNhapHang);
            hide(btnKhuyenMai);
            // Labels khách hàng đã ẩn mặc định trong FXML, không cần làm gì thêm
        }
        // QUANLY: giữ nguyên tất cả (menu KH đã ẩn mặc định trong FXML)
    }

    /**
     * Ẩn một nút và không chiếm layout
     */
    private void hide(Button btn) {
        if (btn != null) {
            btn.setVisible(false);
            btn.setManaged(false);
        }
    }

    /**
     * Hiện một nút
     */
    private void show(Button btn) {
        if (btn != null) {
            btn.setVisible(true);
            btn.setManaged(true);
        }
    }

    /**
     * Ẩn một label section
     */
    private void hideLabel(Label lbl) {
        if (lbl != null) {
            lbl.setVisible(false);
            lbl.setManaged(false);
        }
    }

    /**
     * Hiện một label section
     */
    private void showLabel(Label lbl) {
        if (lbl != null) {
            lbl.setVisible(true);
            lbl.setManaged(true);
        }
    }

    // ================================================================
    //  PHẦN 5: LOAD VIEW HELPER
    // ================================================================
    private void loadView(String fxmlPath, String title) {
        try {
            if (contentPane == null) {
                return;
            }
            var fileUrl = getClass().getResource("/fxml/" + fxmlPath);
            if (fileUrl == null) {
                System.err.println(" Không tìm thấy file /fxml/" + fxmlPath);
                return;
            }
            FXMLLoader loader = new FXMLLoader(fileUrl);
            Node node = loader.load();
            contentPane.getChildren().setAll(node);
            if (lblPageTitle != null) {
                lblPageTitle.setText(title.toUpperCase());
            }
        } catch (Exception e) {
            System.err.println(" Lỗi load " + fxmlPath + ": " + e.getMessage());
        }
    }

    // ================================================================
    //  PHẦN 6: MENU ACTIONS - Nhân viên / Quản lý
    // ================================================================
    @FXML
    public void loadSoDoMay() {
        loadView("sodoMay.fxml", "Sơ đồ máy");
    }

    @FXML
    public void loadPhienSuDung() {
        loadView("phienSuDung.fxml", "Quản lý phiên sử dụng");
    }

    @FXML
    public void loadNapTien() {
        loadView("napTien.fxml", "Nạp tiền");
    }

    @FXML
    public void loadHoaDon() {
        loadView("hoaDon.fxml", "Quản lý hóa đơn");
    }

    @FXML
    public void loadDichVu() {
        loadView("dichVu.fxml", "Quản lý dịch vụ");
    }

    @FXML
    public void loadGoiDichVu() {
        loadView("goiDichVu.fxml", "Quản lý gói dịch vụ");
    }

    @FXML
    public void loadGoiDichVuKH() {
        loadView("GoiDichVuKhachHang.fxml", "Quản lý gói dịch vụ khách hàng");
    }

    @FXML
    public void loadKhuyenMai() {
        loadView("khuyenMai.fxml", "Chương trình khuyến mãi");
    }

    @FXML
    public void loadNhapHang() {
        loadView("nhapHang.fxml", "Nhập hàng");
    }

    @FXML
    public void loadKhachHang() {
        loadView("khachHang.fxml", "Quản lý khách hàng");
    }

    @FXML
    public void loadNhanVien() {
        loadView("nhanVien.fxml", "Quản lý nhân viên");
    }

    @FXML
    public void loadMayTinh() {
        loadView("mayTinh.fxml", "Quản lý máy tính");
    }

    @FXML
    public void loadKhuMay() {
        loadView("khuMay.fxml", "Quản lý khu máy");
    }

    @FXML
    public void loadThongKe() {
        loadView("thongKe.fxml", "Báo cáo thống kê");
    }

    // ================================================================
    //  PHẦN 7: MENU ACTIONS - Khách hàng
    // ================================================================
    @FXML
    public void loadThongTinKhachHang() {
        loadView("thongTinKhachHang.fxml", "Thông tin tài khoản");
    }

    @FXML
    public void loadPhienCuaToi() {
        loadView("phienCuaToi.fxml", "Phiên của tôi");
    }

    @FXML
    public void loadGoiCuaToi() {
        loadView("goiCuaToi.fxml", "Gói dịch vụ của tôi");
    }

    @FXML
    public void loadDatDichVu() {
        loadView("datDichVu.fxml", "Đặt dịch vụ");
    }

    // ================================================================
    //  PHẦN 8: LOGOUT
    // ================================================================
    @FXML
    private void handleLogout() {
        // Dừng timer kiểm tra phiên (nếu đang chạy)
        if (phienCheckTimer != null) {
            phienCheckTimer.stop();
            phienCheckTimer = null;
        }
        // Phiên vẫn tiếp tục chạy, KH có nút riêng để kết thúc phiên
        SessionManager.logout();
        try {
            // Lấy stage từ bất kỳ node nào còn hiện hữu
            Stage currentStage = (Stage) contentPane.getScene().getWindow();
            // Huỷ close hook để không chạy 2 lần
            currentStage.setOnCloseRequest(null);
            currentStage.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Đăng nhập");
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
