package gui.controller;

import bus.KhachHangBUS;
import bus.MayTinhBUS;
import bus.KhuMayBUS;
import bus.PhienSuDungBUS;
import dao.*;
import entity.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Rectangle;
import utils.ThongBaoDialogHelper;
import dao.SuDungDichVuDAO;
import dao.KhachHangDAO;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SoDoMayController implements Initializable {

    @FXML private VBox   vboxKhuMay;
    @FXML private ComboBox<String> cboKhu;
    @FXML private Label  lblMayTrong, lblMayDangDung, lblMayBaoTri, lblTongMay;
    @FXML private Label  lblSelectedMay, lblAutoRefresh;
    @FXML private Button btnMoPhien, btnKetThuc;

    private final MayTinhBUS    mayTinhBUS   = new MayTinhBUS();
    private final KhuMayBUS     khuMayBUS    = new KhuMayBUS();
    private final KhachHangBUS  khachHangBUS = new KhachHangBUS();
    private final PhienSuDungBUS phienBUS = new PhienSuDungBUS(
            new PhienSuDungDAO(), new MayTinhDAO(), new KhachHangDAO(),
            new GoiDichVuKhachHangDAO(), new SuDungDichVuDAO(), new HoaDonDAO());

    private final SuDungDichVuDAO suDungDichVuDAO = new SuDungDichVuDAO();
    private final KhachHangDAO    khachHangDAO    = new KhachHangDAO();
    private MayTinh  selectedMay = null;
    private Timeline autoRefresh;
    private int      refreshCountdown = 30;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadKhuCombo();
        loadSoDo();
        startAutoRefresh();
    }

    // ================================================================
    //  LOAD COMBO KHU
    // ================================================================
    private void loadKhuCombo() {
        try {
            List<KhuMay> khuList = khuMayBUS.getAllKhuMay();
            cboKhu.getItems().clear();
            cboKhu.getItems().add("Tất cả");
            khuList.forEach(k -> cboKhu.getItems().add(k.getTenkhu()));
            cboKhu.setValue("Tất cả");
        } catch (Exception e) {
            cboKhu.getItems().setAll("Tất cả");
        }
    }

    // ================================================================
    //  SƠ ĐỒ MÁY
    // ================================================================
    public void loadSoDo() {
        try {
            List<MayTinh> allMay = mayTinhBUS.getAllMayTinh();
            List<KhuMay>  allKhu = khuMayBUS.getAllKhuMay();
            updateStats(allMay);
            renderSoDo(allMay, allKhu);
        } catch (Exception e) {
            vboxKhuMay.getChildren().clear();
            Label errLbl = new Label("⚠  Lỗi tải dữ liệu: " + e.getMessage());
            errLbl.setStyle("-fx-text-fill:#C62828;");
            vboxKhuMay.getChildren().add(errLbl);
        }
    }

    private void updateStats(List<MayTinh> list) {
        long trong    = list.stream().filter(m -> "TRONG".equals(m.getTrangthai())).count();
        long dangDung = list.stream().filter(m -> "DANGDUNG".equals(m.getTrangthai())).count();
        long baoTri   = list.stream().filter(m -> "BAOTRI".equals(m.getTrangthai())).count();
        if (lblMayTrong   != null) lblMayTrong.setText(String.valueOf(trong));
        if (lblMayDangDung != null) lblMayDangDung.setText(String.valueOf(dangDung));
        if (lblMayBaoTri  != null) lblMayBaoTri.setText(String.valueOf(baoTri));
        if (lblTongMay    != null) lblTongMay.setText(String.valueOf(list.size()));
    }

    private void renderSoDo(List<MayTinh> allMay, List<KhuMay> allKhu) {
        String filterKhu = cboKhu.getValue();
        vboxKhuMay.getChildren().clear();

        Map<String, List<MayTinh>> grouped = allMay.stream()
                .collect(Collectors.groupingBy(m -> m.getMakhu() == null ? "none" : m.getMakhu()));

        for (KhuMay khu : allKhu) {
            if (!"Tất cả".equals(filterKhu) && !khu.getTenkhu().equals(filterKhu)) continue;

            List<MayTinh> mayInKhu = grouped.getOrDefault(khu.getMakhu(), List.of());

            VBox khuBox = new VBox(10);
            khuBox.setStyle("-fx-background-color:#F8FAFF; -fx-background-radius:10; -fx-padding:16;");

            Label khuLabel = new Label("► " + khu.getTenkhu() + "  (" + mayInKhu.size() + " máy)");
            khuLabel.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:#1565C0;");
            khuBox.getChildren().add(khuLabel);

            FlowPane flowPane = new FlowPane(12, 12);
            flowPane.setPadding(new Insets(8, 0, 0, 0));
            for (MayTinh may : mayInKhu) {
                flowPane.getChildren().add(buildMachineCard(may));
            }
            khuBox.getChildren().add(flowPane);
            vboxKhuMay.getChildren().add(khuBox);
        }
    }

    private VBox buildMachineCard(MayTinh may) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(115); card.setMaxWidth(115);
        card.setPrefHeight(95);
        card.setCursor(javafx.scene.Cursor.HAND);

        String bgColor = switch (may.getTrangthai()) {
            case "TRONG"    -> "#E8F5E9";
            case "DANGDUNG" -> "#E3F2FD";
            case "BAOTRI"   -> "#FFF3E0";
            default          -> "#F5F5F5";
        };
        card.setStyle("-fx-background-color:" + bgColor
                + "; -fx-background-radius:8; -fx-border-color:transparent; -fx-border-width:0;");

        // Giữ icon tim theo yêu cầu
        String icon = switch (may.getTrangthai()) {
            case "TRONG"    -> "○";
            case "DANGDUNG" -> "●";
            case "BAOTRI"   -> "✦";
            default          -> "◆";
        };
        String statusText = switch (may.getTrangthai()) {
            case "TRONG"    -> "Trống";
            case "DANGDUNG" -> "Đang dùng";
            case "BAOTRI"   -> "Bảo trì";
            default          -> may.getTrangthai();
        };

        Label iconLbl   = new Label(icon);
        iconLbl.setStyle("-fx-font-size:22px;");
        Label nameLbl   = new Label(may.getTenmay());
        nameLbl.setStyle("-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:#212121;");
        Label statusLbl = new Label(statusText);
        statusLbl.setStyle("-fx-font-size:10px; -fx-text-fill:#555555;");

        card.getChildren().addAll(iconLbl, nameLbl, statusLbl);
        card.setOnMouseClicked(e -> handleMachineClick(may, card));
        return card;
    }

    private void handleMachineClick(MayTinh may, VBox card) {
        selectedMay = may;
        if (lblSelectedMay != null)
            lblSelectedMay.setText("Đã chọn: " + may.getTenmay() + " | " + may.getTrangthai());
        boolean isTrong    = "TRONG".equals(may.getTrangthai());
        boolean isDangDung = "DANGDUNG".equals(may.getTrangthai());
        if (btnMoPhien != null) btnMoPhien.setDisable(!isTrong);
        if (btnKetThuc != null) btnKetThuc.setDisable(!isDangDung);
    }

    @FXML public void handleFilterKhu() { loadSoDo(); }

    @FXML
    public void handleRefresh() {
        selectedMay = null;
        if (lblSelectedMay != null) lblSelectedMay.setText("Chưa chọn máy");
        if (btnMoPhien != null) btnMoPhien.setDisable(true);
        if (btnKetThuc != null) btnKetThuc.setDisable(true);
        loadSoDo();
    }

    // ================================================================
    //  MỞ PHIÊN (code gốc)
    // ================================================================
    @FXML
    public void handleMoPhien() {
        if (selectedMay == null) return;

        List<KhachHang> dsKH;
        try {
            dsKH = khachHangBUS.getAllKhachHang().stream()
                    .filter(k -> "HOATDONG".equals(k.getTrangthai()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(vboxKhuMay.getScene(), "Lỗi tải dữ liệu: " + e.getMessage());
            return;
        }
        if (dsKH.isEmpty()) {
            ThongBaoDialogHelper.showError(vboxKhuMay.getScene(), "Không có khách hàng nào đang hoạt động.");
            return;
        }

        Stage dialog = makeDialog();

        ComboBox<KhachHang> cboKH = new ComboBox<>();
        cboKH.getItems().setAll(dsKH);
        cboKH.setMaxWidth(Double.MAX_VALUE);
        cboKH.setPromptText("-- Chọn khách hàng --");
        cboKH.setCellFactory(lv -> khachHangCell());
        cboKH.setButtonCell(khachHangCell());

        Label lblSoDu = new Label("");
        lblSoDu.setStyle("-fx-text-fill:#388E3C; -fx-font-size:12px;");
        cboKH.setOnAction(e -> {
            KhachHang kh = cboKH.getValue();
            lblSoDu.setText(kh != null ? "Số dư: " + String.format("%,.0f ₫", kh.getSodu()) : "");
        });

        Label  lblLoi = errLabel();
        Button btnOk  = primaryBtn("▶  Mở phiên", "#1565C0");
        Button btnHuy = secondaryBtn();
        btnHuy.setOnAction(e -> dialog.close());

        VBox body = new VBox(10, boldLabel("Khách hàng:"), cboKH, lblSoDu, lblLoi);

        btnOk.setOnAction(e -> {
            KhachHang kh = cboKH.getValue();
            if (kh == null) { lblLoi.setText("⚠ Vui lòng chọn khách hàng!"); return; }
            try {
                PhienSuDung phien = phienBUS.moPhienMoi(kh.getMakh(), selectedMay.getMamay());
                dialog.close();
                ThongBaoDialogHelper.showSuccess(vboxKhuMay.getScene(),
                        "✔ Đã mở phiên " + phien.getMaPhien()
                                + "\nKhách: " + kh.getHo() + " " + kh.getTen()
                                + "  |  Máy: " + selectedMay.getTenmay());
                selectedMay = null;
                if (btnMoPhien != null) btnMoPhien.setDisable(true);
                if (btnKetThuc != null) btnKetThuc.setDisable(true);
                if (lblSelectedMay != null) lblSelectedMay.setText("Chưa chọn máy");
                loadSoDo();
            } catch (Exception ex) {
                lblLoi.setText("⚠ " + ex.getMessage());
            }
        });

        dialog.setScene(new Scene(buildRoot("▶  Mở Phiên Mới", "#1565C0", body, btnOk, btnHuy)));
        dialog.showAndWait();
    }

    // ================================================================
    //  KẾT THÚC PHIÊN — hiện dialog thanh toán
    // ================================================================
    @FXML
    public void handleKetThucPhien() {
        if (selectedMay == null) return;
        try {
            PhienSuDung phien = phienBUS.getPhienDangChoiByMay(selectedMay.getMamay());
            if (phien == null) {
                ThongBaoDialogHelper.showError(vboxKhuMay.getScene(),
                        "Máy " + selectedMay.getTenmay() + " không có phiên đang chơi.");
                return;
            }
            showThanhToanDialog(phien);
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(vboxKhuMay.getScene(), "Lỗi: " + e.getMessage());
        }
    }

    private void showThanhToanDialog(PhienSuDung phien) {
        long phut = phien.getGioBatDau() != null
                ? java.time.temporal.ChronoUnit.MINUTES.between(phien.getGioBatDau(), java.time.LocalDateTime.now()) : 0;
        double tienGio = (phut / 60.0) * phien.getGiaMoiGio();

        // Lấy tên KH và số dư
        String tenKH = phien.getMaKH();
        double soDuKH = 0;
        try {
            entity.KhachHang kh = khachHangDAO.getById(phien.getMaKH());
            if (kh != null) {
                tenKH = kh.getHo() + " " + kh.getTen();
                soDuKH = kh.getSodu();
            }
        } catch (Exception ignored) {}

        // Dịch vụ đã dùng
        java.util.List<entity.SuDungDichVu> dsDichVu = new java.util.ArrayList<>();
        double tienDichVu = 0;
        try {
            dsDichVu = suDungDichVuDAO.geyByPhien(phien.getMaPhien());
            if (dsDichVu == null) dsDichVu = new java.util.ArrayList<>();
            for (entity.SuDungDichVu sv : dsDichVu) tienDichVu += sv.getThanhtien();
        } catch (Exception ignored) {}

        double tongCong; // tính sau khi biết tienGio chính xác
        java.time.format.DateTimeFormatter FMT = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        Stage dialog = makeDialog();

        // ── Thông tin phiên ──
        VBox secPhien = sectionBox("▣  Thông tin phiên");
        GridPane gridPhien = infoGrid();
        addInfoRow(gridPhien, 0, "Mã phiên:",    phien.getMaPhien());
        addInfoRow(gridPhien, 1, "Khách hàng:",  phien.getMaKH() + " - " + tenKH.trim());
        addInfoRow(gridPhien, 2, "Máy:",         phien.getMaMay());
        addInfoRow(gridPhien, 3, "Giờ bắt đầu:", phien.getGioBatDau() != null ? phien.getGioBatDau().format(FMT) : "--");
        addInfoRow(gridPhien, 4, "Thời gian:",   phut / 60 + "h " + String.format("%02dm", phut % 60));
        // Tính giờ từ gói và giờ từ máy
        double gioConLaiGoi = 0;
        if (phien.getMaGoiKH() != null) {
            try {
                entity.GoiDichVuKhachHang goiHT = new dao.GoiDichVuKhachHangDAO().getByID(phien.getMaGoiKH());
                if (goiHT != null) gioConLaiGoi = goiHT.getSogioconlai();
            } catch (Exception ignored) {}
        }
        double gioTuGoi2   = Math.min(phut / 60.0, gioConLaiGoi);
        double gioTuMay2   = Math.max(0, phut / 60.0 - gioTuGoi2);
        double tienMayTam2 = gioTuMay2 * phien.getGiaMoiGio();

        if (phien.getMaGoiKH() != null) {
            addInfoRow(gridPhien, 5, "Goi su dung:", phien.getMaGoiKH());
            long gG=(long)gioTuGoi2; long pG=(long)Math.round((gioTuGoi2-gG)*60);
            addInfoRow(gridPhien, 6, "Gio tu goi:", gG+"h"+String.format("%02d",pG)+"m  (mien phi)");
            double conDu2=Math.max(0,gioConLaiGoi-gioTuGoi2);
            long gD=(long)conDu2; long pD=(long)Math.round((conDu2-gD)*60);
            addInfoRow(gridPhien, 7, "Gio con du trong goi:", gD+"h"+String.format("%02d",pD)+"m");
            long gM=(long)gioTuMay2; long pM=(long)Math.round((gioTuMay2-gM)*60);
            addInfoRow(gridPhien, 8, "Gio tinh theo may:", gM+"h"+String.format("%02d",pM)+"m"
                    +"  @ "+String.format("%,.0f d/gio",phien.getGiaMoiGio()));
            addInfoRow(gridPhien, 9, "Tien theo may:", gioTuMay2>0
                    ? String.format("%,.0f d",tienMayTam2) : "0 d");
            tienGio = tienMayTam2;
        } else {
            addInfoRow(gridPhien, 5, "Don gia:", String.format("%,.0f d/gio",phien.getGiaMoiGio()));
            addInfoRow(gridPhien, 6, "Gio tu goi:", "0h00m");
            addInfoRow(gridPhien, 7, "Gio tinh theo may:", phut/60+"h"+String.format("%02dm",phut%60));
            addInfoRow(gridPhien, 8, "Tien gio:", String.format("%,.0f d",tienGio));
        }
        secPhien.getChildren().add(gridPhien);
        tongCong = tienGio + tienDichVu;

        // ── Dịch vụ ──
        VBox secDV = sectionBox("☆  Dịch vụ sử dụng");
        if (dsDichVu.isEmpty()) {
            Label lblNoDV = new Label("Không có sử dụng dịch vụ");
            lblNoDV.setStyle("-fx-text-fill:#888; -fx-font-style:italic; -fx-font-size:12px;");
            secDV.getChildren().add(lblNoDV);
        } else {
            GridPane gridDV = infoGrid();
            addHeaderRow(gridDV, "Tên dịch vụ", "SL", "Đơn giá", "Thành tiền");
            int rowDV = 1;
            for (entity.SuDungDichVu sv : dsDichVu) addDichVuRow(gridDV, rowDV++, sv);
            Label lblTienDV = new Label("Tổng dịch vụ: " + String.format("%,.0f ₫", tienDichVu));
            lblTienDV.setStyle("-fx-font-weight:bold; -fx-font-size:13px; -fx-text-fill:#e65100;");
            lblTienDV.setMaxWidth(Double.MAX_VALUE); lblTienDV.setAlignment(Pos.CENTER_RIGHT);
            secDV.getChildren().addAll(gridDV, lblTienDV);
        }

        // ── Tổng cộng ──
        Label lblTong = new Label(String.format("TỔNG CỘNG:  %,.0f ₫", tongCong));
        lblTong.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:white; "
                + "-fx-background-color:#1565C0; -fx-padding:12 16; -fx-background-radius:8;");
        lblTong.setMaxWidth(Double.MAX_VALUE);

        // ── Thanh toán (5 toggle card) ──
        VBox secTT = sectionBox("▸  Phương thức thanh toán");
        final double tongF = tongCong, soDuF = soDuKH;
        final String tenKHFinal = tenKH;
        // Tính số tiền thiếu sau khi dùng hết tài khoản
        final double tienTruTK = Math.min(soDuF, tongF); // phần trừ từ TK
        final double tienThieu = Math.max(0, tongF - soDuF); // phần còn thiếu

        String[][] opts = {
                {"TAIKHOAN",    "▣ Tài khoản",   "#E8F5E9", "#2e7d32"},
                {"TIENMAT",     "○ Tiền mặt",     "#FFF8E1", "#f57f17"},
                {"CHUYENKHOAN", "□ Chuyển khoản", "#E3F2FD", "#1565C0"},
                {"MOMO",        "◆ MoMo",          "#FCE4EC", "#880e4f"},
                {"VNPAY",       "■ VNPay",         "#E8EAF6", "#1a237e"},
        };
        ToggleGroup grp = new ToggleGroup();
        HBox hboxPT = new HBox(8); hboxPT.setAlignment(Pos.CENTER_LEFT); hboxPT.setStyle("-fx-padding:4 0;");
        ToggleButton[] tbs = new ToggleButton[opts.length];
        for (int idx = 0; idx < opts.length; idx++) {
            String code=opts[idx][0], lbl=opts[idx][1], bg=opts[idx][2], fg=opts[idx][3];
            ToggleButton tb = new ToggleButton(lbl);
            tb.setToggleGroup(grp); tb.setUserData(code);
            tb.setStyle("-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:"+fg
                    +"; -fx-background-color:"+bg+"; -fx-border-color:"+fg
                    +"; -fx-border-width:1.5; -fx-border-radius:8; -fx-background-radius:8; -fx-padding:8 12; -fx-cursor:hand;");
            tb.selectedProperty().addListener((obs2, was2, now2) -> {
                if (now2) tb.setStyle("-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:white"
                        +"; -fx-background-color:"+fg+"; -fx-border-color:"+fg
                        +"; -fx-border-width:1.5; -fx-border-radius:8; -fx-background-radius:8; -fx-padding:8 12; -fx-cursor:hand;");
                else tb.setStyle("-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:"+fg
                        +"; -fx-background-color:"+bg+"; -fx-border-color:"+fg
                        +"; -fx-border-width:1.5; -fx-border-radius:8; -fx-background-radius:8; -fx-padding:8 12; -fx-cursor:hand;");
            });
            tbs[idx] = tb; hboxPT.getChildren().add(tb);
        }
        tbs[0].setSelected(true);

        // Panel Tài khoản
        VBox panelTK = new VBox(6);
        panelTK.setStyle("-fx-background-color:#E8F5E9; -fx-padding:12; -fx-background-radius:8; -fx-border-color:#a5d6a7; -fx-border-radius:8; -fx-border-width:1;");
        Label lblSD2 = new Label("Số dư hiện tại: " + String.format("%,.0f ₫", soDuF));
        lblSD2.setStyle("-fx-font-size:13px; -fx-text-fill:#2e7d32; -fx-font-weight:bold;");
        if (soDuF >= tongF) {
            double cl2 = soDuF - tongF;
            Label lblCL2 = new Label("Sau thanh toán còn: " + String.format("%,.0f ₫", cl2));
            lblCL2.setStyle("-fx-font-size:12px; -fx-text-fill:#388E3C;");
            panelTK.getChildren().addAll(lblSD2, lblCL2);
        } else {
            Label lblTruTK2 = new Label("Sẽ trừ từ tài khoản: " + String.format("%,.0f ₫", tienTruTK));
            lblTruTK2.setStyle("-fx-font-size:12px; -fx-text-fill:#388E3C;");
            Label lblThieu2 = new Label("⚠ Còn thiếu: " + String.format("%,.0f ₫", tienThieu)
                    + " - vui lòng chọn phương thức khác để trả phần thiếu");
            lblThieu2.setStyle("-fx-font-size:12px; -fx-text-fill:#e65100; -fx-font-weight:bold; -fx-wrap-text:true;");
            lblThieu2.setMaxWidth(Double.MAX_VALUE);
            panelTK.getChildren().addAll(lblSD2, lblTruTK2, lblThieu2);
        }

        // Panel Tiền mặt
        VBox panelTM2 = new VBox(6);
        panelTM2.setStyle("-fx-background-color:#FFF8E1; -fx-padding:12; -fx-background-radius:8; -fx-border-color:#ffe082; -fx-border-radius:8; -fx-border-width:1;");
        double tienMatCanTra = tienThieu > 0 ? tienThieu : tongF;
        Label lblTM2 = new Label("○  Thanh toán tiền mặt: " + String.format("%,.0f ₫", tienMatCanTra));
        lblTM2.setStyle("-fx-font-size:13px; -fx-text-fill:#f57f17; -fx-font-weight:bold;");
        Label lblTMNote2;
        if (tienThieu > 0 && tienTruTK > 0) {
            lblTMNote2 = new Label("Đã trừ tài khoản: " + String.format("%,.0f ₫", tienTruTK)
                    + ". Nhân viên thu tiền mặt phần còn thiếu và xác nhận.");
        } else {
            lblTMNote2 = new Label("Nhân viên thu tiền mặt và xác nhận.");
        }
        lblTMNote2.setStyle("-fx-font-size:12px; -fx-text-fill:#795548; -fx-wrap-text:true;");
        lblTMNote2.setMaxWidth(Double.MAX_VALUE);
        panelTM2.getChildren().addAll(lblTM2, lblTMNote2);
        panelTM2.setVisible(false); panelTM2.setManaged(false);

        // Panel QR
        VBox panelQR2 = new VBox(12); panelQR2.setAlignment(Pos.CENTER); panelQR2.setPadding(new Insets(16));
        panelQR2.setStyle("-fx-background-color:#F3E5F5; -fx-background-radius:8; -fx-border-color:#ce93d8; -fx-border-radius:8; -fx-border-width:1;");
        panelQR2.setVisible(false); panelQR2.setManaged(false);
        StackPane qrBox2 = buildFakeQR(260);
        Label lblQRTitle2  = new Label(""); lblQRTitle2.setStyle("-fx-font-size:15px; -fx-font-weight:bold; -fx-text-fill:#333;");
        double tienQR = tienThieu > 0 ? tienThieu : tongF;
        Label lblQRAmount2 = new Label(String.format("Số tiền: %,.0f ₫", tienQR)); lblQRAmount2.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:#1565C0;");
        Label lblQRContent2 = new Label("Nội dung: TT-" + phien.getMaPhien()); lblQRContent2.setStyle("-fx-font-size:12px; -fx-text-fill:#555;");
        Label lblQRNote2 = new Label("Khách quét mã và thanh toán, sau đó xác nhận."); lblQRNote2.setStyle("-fx-font-size:12px; -fx-text-fill:#666;");
        panelQR2.getChildren().addAll(lblQRTitle2, qrBox2, lblQRAmount2, lblQRContent2, lblQRNote2);

        secTT.getChildren().addAll(hboxPT, panelTK, panelTM2, panelQR2);

        Label  lblLoi3 = new Label(""); lblLoi3.setStyle("-fx-text-fill:#C62828; -fx-font-size:12px; -fx-wrap-text:true;"); lblLoi3.setMaxWidth(Double.MAX_VALUE);
        Button btnXN  = primaryBtn("✔  Xác nhận thanh toán", "#1b5e20");
        Button btnHuy = secondaryBtn();
        btnHuy.setOnAction(e -> dialog.close());
        final Timeline[] autoTimer = {null};

        grp.selectedToggleProperty().addListener((obs3, o3, n3) -> {
            if (autoTimer[0] != null) { autoTimer[0].stop(); autoTimer[0] = null; }
            panelTK.setVisible(false); panelTK.setManaged(false);
            panelTM2.setVisible(false); panelTM2.setManaged(false);
            panelQR2.setVisible(false); panelQR2.setManaged(false);
            lblLoi3.setText("");
            btnXN.setDisable(false);
            btnXN.setText("✔  Xác nhận thanh toán");
            btnXN.setStyle("-fx-background-color:#1b5e20; -fx-text-fill:white; -fx-font-weight:bold; -fx-pref-height:36px; -fx-pref-width:210px; -fx-background-radius:6;");
            btnXN.setOnAction(e3 -> doKetThuc(phien, dialog, lblLoi3));
            if (n3 == null) return;
            String code3 = (String) n3.getUserData();
            switch (code3) {
                case "TAIKHOAN" -> {
                    panelTK.setVisible(true); panelTK.setManaged(true);
                    if (soDuF >= tongF) {
                        lblLoi3.setText("");
                        btnXN.setDisable(false);
                        btnXN.setStyle("-fx-background-color:#1b5e20; -fx-text-fill:white; -fx-font-weight:bold; -fx-pref-height:36px; -fx-pref-width:210px; -fx-background-radius:6;");
                    } else {
                        lblLoi3.setText("⚠ Số dư không đủ! Vui lòng chọn Tiền mặt / Chuyển khoản / MoMo / VNPay để trả phần thiếu "
                                + String.format("%,.0f ₫", tienThieu) + " (đã tự động trừ TK " + String.format("%,.0f ₫", tienTruTK) + ")");
                        lblLoi3.setStyle("-fx-text-fill:#C62828; -fx-font-size:12px; -fx-font-weight:bold; -fx-wrap-text:true;");
                        btnXN.setDisable(true);
                        btnXN.setStyle("-fx-background-color:#BDBDBD; -fx-text-fill:#757575; "
                                + "-fx-font-weight:bold; -fx-pref-height:36px; -fx-pref-width:210px; -fx-background-radius:6;");
                    }
                }
                case "TIENMAT"  -> { panelTM2.setVisible(true); panelTM2.setManaged(true); }
                case "CHUYENKHOAN" -> {
                    panelQR2.setVisible(true); panelQR2.setManaged(true);
                    panelQR2.setStyle("-fx-background-color:#E3F2FD; -fx-background-radius:8; -fx-border-color:#90caf9; -fx-border-radius:8; -fx-border-width:1;");
                    lblQRTitle2.setText("□  Quét mã để chuyển khoản");
                    if (tienThieu > 0 && tienTruTK > 0) {
                        lblQRNote2.setText("Đã trừ TK: " + String.format("%,.0f ₫", tienTruTK)
                                + ". Khách quét mã chuyển khoản phần còn thiếu.");
                    } else {
                        lblQRNote2.setText("Khách quét mã và chuyển khoản, sau đó nhấn xác nhận.");
                    }
                    setupQRButton(btnXN, btnHuy, autoTimer, phien, dialog, tienQR, "Chuyển khoản", tenKHFinal);
                }
                case "MOMO" -> {
                    panelQR2.setVisible(true); panelQR2.setManaged(true);
                    panelQR2.setStyle("-fx-background-color:#FCE4EC; -fx-background-radius:8; -fx-border-color:#f48fb1; -fx-border-radius:8; -fx-border-width:1;");
                    lblQRTitle2.setText("◆  Quét mã MoMo");
                    if (tienThieu > 0 && tienTruTK > 0) {
                        lblQRNote2.setText("Đã trừ TK: " + String.format("%,.0f ₫", tienTruTK)
                                + ". Khách quét mã MoMo trả phần còn thiếu.");
                    } else {
                        lblQRNote2.setText("Khách quét mã MoMo và chuyển tiền, sau đó nhấn xác nhận.");
                    }
                    setupQRButton(btnXN, btnHuy, autoTimer, phien, dialog, tienQR, "MoMo", tenKHFinal);
                }
                case "VNPAY" -> {
                    panelQR2.setVisible(true); panelQR2.setManaged(true);
                    panelQR2.setStyle("-fx-background-color:#E8EAF6; -fx-background-radius:8; -fx-border-color:#9fa8da; -fx-border-radius:8; -fx-border-width:1;");
                    lblQRTitle2.setText("■  Quét mã VNPay");
                    if (tienThieu > 0 && tienTruTK > 0) {
                        lblQRNote2.setText("Đã trừ TK: " + String.format("%,.0f ₫", tienTruTK)
                                + ". Khách quét mã VNPay trả phần còn thiếu.");
                    } else {
                        lblQRNote2.setText("Khách quét mã VNPay và thanh toán, sau đó nhấn xác nhận.");
                    }
                    setupQRButton(btnXN, btnHuy, autoTimer, phien, dialog, tienQR, "VNPay", tenKHFinal);
                }
            }
        });
        btnXN.setOnAction(e -> doKetThuc(phien, dialog, lblLoi3));

        VBox body = new VBox(14, secPhien, new Separator(), secDV, new Separator(), lblTong, new Separator(), secTT, lblLoi3);
        ScrollPane scroll = new ScrollPane(body);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background-color:transparent; -fx-background:transparent;");
        scroll.setPrefHeight(480);

        VBox root = new VBox(14); root.setPadding(new Insets(24)); root.setPrefWidth(580);
        root.setStyle("-fx-background-color:white; -fx-border-color:#E0E0E0; -fx-border-width:1; -fx-border-radius:10; -fx-background-radius:10;");
        Label titleLbl = new Label("■  Kết Thúc & Thanh Toán");
        titleLbl.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#C62828;");
        HBox btnRow = new HBox(10, btnXN, btnHuy); btnRow.setAlignment(Pos.CENTER_RIGHT);
        root.getChildren().addAll(titleLbl, new Separator(), scroll, new Separator(), btnRow);

        dialog.setScene(new Scene(root));
        dialog.showAndWait();
    }


    // ── Dialog helpers (dùng cho showThanhToanDialog) ────────────────
    private VBox sectionBox(String title) {
        VBox box = new VBox(8);
        Label lbl = new Label(title);
        lbl.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:#1565C0;");
        box.getChildren().add(lbl);
        return box;
    }

    private GridPane infoGrid() {
        GridPane g = new GridPane();
        g.setHgap(16); g.setVgap(8);
        g.setPadding(new Insets(10));
        g.setStyle("-fx-background-color:#f5f7fa; -fx-background-radius:6; -fx-border-color:#e0e0e0; -fx-border-radius:6; -fx-border-width:1;");
        ColumnConstraints c1 = new ColumnConstraints(150);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setHgrow(Priority.ALWAYS);
        g.getColumnConstraints().addAll(c1, c2, c2, c2);
        return g;
    }

    private void addInfoRow(GridPane g, int row, String label, String value) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill:#616161; -fx-font-size:12px; -fx-min-width:140;");
        Label val = new Label(value != null ? value : "--");
        val.setStyle("-fx-font-weight:bold; -fx-font-size:13px; -fx-text-fill:#212121;");
        val.setWrapText(true);
        g.add(lbl, 0, row); g.add(val, 1, row);
        GridPane.setColumnSpan(val, 3);
    }

    private void addHeaderRow(GridPane g, String c1, String c2, String c3, String c4) {
        String style = "-fx-font-weight:bold; -fx-font-size:12px; -fx-text-fill:#333;";
        Label l1 = new Label(c1); l1.setStyle(style);
        Label l2 = new Label(c2); l2.setStyle(style);
        Label l3 = new Label(c3); l3.setStyle(style);
        Label l4 = new Label(c4); l4.setStyle(style);
        g.add(l1, 0, 0); g.add(l2, 1, 0); g.add(l3, 2, 0); g.add(l4, 3, 0);
    }

    private void addDichVuRow(GridPane g, int row, entity.SuDungDichVu sv) {
        String style = "-fx-font-size:12px; -fx-text-fill:#212121;";
        Label lTen = new Label(sv.getMadv() != null ? sv.getMadv() : "--");  lTen.setStyle(style);
        Label lSL  = new Label(String.valueOf(sv.getSoluong()));              lSL.setStyle(style);
        Label lGia = new Label(String.format("%,.0f ₫", sv.getDongia())); lGia.setStyle(style);
        Label lTT  = new Label(String.format("%,.0f ₫", sv.getThanhtien())); lTT.setStyle("-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:#e65100;");
        g.add(lTen, 0, row); g.add(lSL, 1, row); g.add(lGia, 2, row); g.add(lTT, 3, row);
    }

    private void setupQRButton(Button btn, Button btnHuyRef, Timeline[] timerRef,
                               PhienSuDung phien, Stage dialog,
                               double tongTien, String phuongThuc, String tenKH) {
        if (timerRef[0] != null) { timerRef[0].stop(); timerRef[0] = null; }
        if (btnHuyRef != null) {
            btnHuyRef.setText("Hủy");
            btnHuyRef.setStyle("-fx-background-color:#E0E0E0; -fx-pref-height:36px; -fx-pref-width:90px; -fx-background-radius:6;");
            btnHuyRef.setOnAction(e -> dialog.close());
        }
        btn.setDisable(false);
        btn.setText("💳  Chọn cách thanh toán này");
        btn.setStyle("-fx-background-color:#1565C0; -fx-text-fill:white; "
                + "-fx-font-weight:bold; -fx-pref-height:36px; -fx-pref-width:230px; -fx-background-radius:6;");
        btn.setOnAction(ev -> {
            btn.setDisable(true);
            btn.setText("⏳  Đang chờ chuyển khoản...");
            btn.setStyle("-fx-background-color:#78909C; -fx-text-fill:white; "
                    + "-fx-font-weight:bold; -fx-pref-height:36px; -fx-pref-width:230px; -fx-background-radius:6;");
            final int[] cd = {5};
            timerRef[0] = new Timeline(new KeyFrame(Duration.seconds(1), e2 -> {
                cd[0]--;
                if (cd[0] > 0) {
                    btn.setText("⏳  Đang chờ... " + cd[0] + "s");
                } else {
                    timerRef[0].stop();
                    // Kết thúc phiên ngay khi nhận tiền
                    try { phienBUS.ketThucPhien(phien.getMaPhien()); }
                    catch (Exception ex) { System.err.println("[QR] Lỗi: " + ex.getMessage()); }
                    showPaymentToast(dialog, phien.getMaPhien(), tongTien, phuongThuc, tenKH);
                    btn.setDisable(false);
                    btn.setText("✅  Đã nhận tiền thành công");
                    btn.setStyle("-fx-background-color:#2e7d32; -fx-text-fill:white; "
                            + "-fx-font-weight:bold; -fx-pref-height:36px; -fx-pref-width:230px; -fx-background-radius:6;");
                    btn.setOnAction(e3 -> { dialog.close(); selectedMay = null;
                        if (btnMoPhien!=null) btnMoPhien.setDisable(true);
                        if (btnKetThuc!=null) btnKetThuc.setDisable(true);
                        if (lblSelectedMay!=null) lblSelectedMay.setText("Chưa chọn máy");
                        loadSoDo(); });
                    if (btnHuyRef != null) {
                        btnHuyRef.setText("🚪  Thoát");
                        btnHuyRef.setStyle("-fx-background-color:#455A64; -fx-text-fill:white; "
                                + "-fx-font-weight:bold; -fx-pref-height:36px; -fx-pref-width:90px; -fx-background-radius:6;");
                        btnHuyRef.setOnAction(e3 -> { dialog.close(); selectedMay = null;
                            if (btnMoPhien!=null) btnMoPhien.setDisable(true);
                            if (btnKetThuc!=null) btnKetThuc.setDisable(true);
                            if (lblSelectedMay!=null) lblSelectedMay.setText("Chưa chọn máy");
                            loadSoDo(); });
                    }
                }
            }));
            timerRef[0].setCycleCount(5);
            timerRef[0].play();
        });
    }

    private void showPaymentToast(Stage owner, String maPhien, double soTien,
                                  String phuongThuc, String tenKH) {
        Stage toast = new Stage(javafx.stage.StageStyle.UNDECORATED);
        toast.initOwner(owner);
        VBox box = new VBox(6);
        box.setPadding(new Insets(14, 20, 14, 20));
        box.setStyle("-fx-background-color:#1b5e20; -fx-background-radius:10;"
                + " -fx-border-color:#a5d6a7; -fx-border-radius:10; -fx-border-width:1;");
        box.setAlignment(Pos.CENTER_LEFT);
        Label l1 = new Label("Da nhan thanh toan");
        l1.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:white;");
        Label l2 = new Label("Khach hang: " + (tenKH != null ? tenKH.trim() : "--"));
        l2.setStyle("-fx-font-size:13px; -fx-text-fill:#c8e6c9; -fx-font-weight:bold;");
        Label l3 = new Label("So tien: " + String.format("%,.0f d", soTien));
        l3.setStyle("-fx-font-size:13px; -fx-text-fill:#c8e6c9;");
        Label l4 = new Label("Noi dung: Thanh toan hoa don " + maPhien);
        l4.setStyle("-fx-font-size:12px; -fx-text-fill:#a5d6a7;");
        Label l5 = new Label("Phuong thuc: " + phuongThuc);
        l5.setStyle("-fx-font-size:12px; -fx-text-fill:#a5d6a7;");
        box.getChildren().addAll(l1, l2, l3, l4, l5);
        toast.setScene(new Scene(box));
        toast.setOpacity(1.0);
        javafx.geometry.Rectangle2D screen = javafx.stage.Screen.getPrimary().getVisualBounds();
        toast.setX(screen.getMaxX() - 380);
        toast.setY(screen.getMaxY() - 200);
        toast.show();
        Timeline fadeTimer = new Timeline(
                new KeyFrame(Duration.seconds(3.5)),
                new KeyFrame(Duration.seconds(3.8), ev -> toast.setOpacity(0.6)),
                new KeyFrame(Duration.seconds(4.1), ev -> toast.setOpacity(0.2)),
                new KeyFrame(Duration.seconds(4.4), ev -> toast.close())
        );
        fadeTimer.play();
    }

    private void doKetThuc(PhienSuDung phien, Stage dialog, Label lblLoi) {
        try {
            phienBUS.ketThucPhien(phien.getMaPhien());
            dialog.close();
            selectedMay = null;
            if (btnMoPhien  != null) btnMoPhien.setDisable(true);
            if (btnKetThuc  != null) btnKetThuc.setDisable(true);
            if (lblSelectedMay != null) lblSelectedMay.setText("Chưa chọn máy");
            loadSoDo();
        } catch (Exception ex) {
            if (lblLoi != null) lblLoi.setText("Lỗi: " + ex.getMessage());
        }
    }

    private StackPane buildFakeQR(double size) {
        StackPane pane = new StackPane();
        pane.setPrefSize(size, size); pane.setMaxSize(size, size);
        pane.setStyle("-fx-background-color:white; -fx-border-color:#444; -fx-border-width:4;"
                + " -fx-background-radius:8; -fx-border-radius:8;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);");
        int cells = 25;
        double cs = (size - 8) / cells;
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setTranslateX(4); grid.setTranslateY(4);
        java.util.Random rnd = new java.util.Random(77);
        for (int r = 0; r < cells; r++) {
            for (int c = 0; c < cells; c++) {
                Rectangle rect = new Rectangle(cs - 0.5, cs - 0.5);
                rect.setArcWidth(1); rect.setArcHeight(1);
                boolean inTL = r < 7 && c < 7;
                boolean inTR = r < 7 && c >= cells - 7;
                boolean inBL = r >= cells - 7 && c < 7;
                boolean inCorner = inTL || inTR || inBL;
                if (inCorner) {
                    int lr = inTL ? r : (inTR ? r : r - (cells - 7));
                    int lc = inTL ? c : (inTR ? c - (cells - 7) : c);
                    boolean outerBorder = lr == 0 || lr == 6 || lc == 0 || lc == 6;
                    boolean innerBlock  = lr >= 2 && lr <= 4 && lc >= 2 && lc <= 4;
                    rect.setFill(outerBorder || innerBlock ? Color.BLACK : Color.WHITE);
                } else {
                    rect.setFill(rnd.nextInt(10) < 6 ? Color.BLACK : Color.WHITE);
                }
                grid.add(rect, c, r);
            }
        }
        double logoSize = size * 0.18;
        StackPane logo = new StackPane();
        javafx.scene.shape.Circle circle = new javafx.scene.shape.Circle(logoSize / 2);
        circle.setFill(Color.web("#1565C0")); circle.setStroke(Color.WHITE); circle.setStrokeWidth(2);
        Label logoLbl = new Label("QR");
        logoLbl.setStyle("-fx-font-size:" + (int)(logoSize * 0.38) + "px; -fx-font-weight:bold; -fx-text-fill:white;");
        logo.getChildren().addAll(circle, logoLbl);
        pane.getChildren().addAll(grid, logo);
        return pane;
    }

    // ================================================================
    //  AUTO REFRESH
    // ================================================================
    private void startAutoRefresh() {
        autoRefresh = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            refreshCountdown--;
            if (lblAutoRefresh != null)
                lblAutoRefresh.setText("Tự động cập nhật: " + refreshCountdown + "s");
            if (refreshCountdown <= 0) {
                refreshCountdown = 30;
                loadSoDo();
            }
        }));
        autoRefresh.setCycleCount(Timeline.INDEFINITE);
        autoRefresh.play();
    }

    // ================================================================
    //  UI HELPERS
    // ================================================================
    private Stage makeDialog() {
        Stage s = new Stage(StageStyle.UNDECORATED);
        s.initModality(Modality.APPLICATION_MODAL);
        s.initOwner(vboxKhuMay.getScene().getWindow());
        return s;
    }

    private VBox buildRoot(String title, String color, VBox body, Button btnOk, Button btnHuy) {
        VBox root = new VBox(14);
        root.setPadding(new Insets(24));
        root.setPrefWidth(500);
        root.setStyle("-fx-background-color:white; -fx-border-color:#E0E0E0; "
                + "-fx-border-width:1; -fx-border-radius:8; -fx-background-radius:8;");
        Label lbl = new Label(title);
        lbl.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:" + color + ";");
        HBox btnRow = new HBox(10, btnOk, btnHuy);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        root.getChildren().addAll(lbl, new Separator(), body, new Separator(), btnRow);
        return root;
    }

    private Button primaryBtn(String text, String color) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:" + color + "; -fx-text-fill:white; "
                + "-fx-font-weight:bold; -fx-pref-height:36px; -fx-pref-width:190px; -fx-background-radius:6;");
        return b;
    }

    private Button secondaryBtn() {
        Button b = new Button("Hủy");
        b.setStyle("-fx-background-color:#E0E0E0; -fx-pref-height:36px; "
                + "-fx-pref-width:90px; -fx-background-radius:6;");
        return b;
    }

    private Label errLabel() {
        Label l = new Label("");
        l.setStyle("-fx-text-fill:#C62828; -fx-font-size:12px; -fx-wrap-text:true;");
        l.setMaxWidth(Double.MAX_VALUE);
        return l;
    }

    private Label boldLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight:bold; -fx-font-size:13px;");
        return l;
    }

    private ListCell<KhachHang> khachHangCell() {
        return new ListCell<>() {
            @Override protected void updateItem(KhachHang kh, boolean empty) {
                super.updateItem(kh, empty);
                if (empty || kh == null) { setText(null); return; }
                setText(String.format("%s  |  %s %s  |  %,.0f ₫",
                        kh.getMakh(), kh.getHo(), kh.getTen(), kh.getSodu()));
            }
        };
    }
}