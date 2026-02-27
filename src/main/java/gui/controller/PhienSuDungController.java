package gui.controller;

import bus.PhienSuDungBUS;
import bus.HoaDonBUS;
import bus.KhachHangBUS;
import bus.MayBUS;
import entity.*;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.time.LocalDateTime;

public class PhienSuDungController {
    // ================== UI =====================

    @FXML private Label lblMaMay;
    @FXML private ComboBox<KhachHang> cbKhachHang;
    @FXML private Label lblSoDu;

    @FXML private Label lblGioBatDau;
    @FXML private Label lblThoiGian;
    @FXML private Label lblTienTamTinh;

    @FXML private Button btnBatDau;
    @FXML private Button btnKetThuc;

    // =================== BUS =====================
    private PhienSuDungBUS phienBUS = new PhienSuDungBUS();
    private HoaDonBUS hoaDonBUS = new HoaDonBUS();
    private KhachHangBUS khBUS = new KhachHangBUS();
    private MayBUS mayBUS = new mayBUS();

    private PhienSuDung phienHienTai;
    private Timeline timeline;

    private static final double DON_GIA_GIO = 10000;

    // ================== INIT ======================

    @FXML
    public void initialize() {

        cbKhachHang.getItems().add(null);
        cbKhachHang.getItems().addAll(khBUS.getAll());

        setTrangThaiChuaMo();
    }

    public void setLblMaMay (String maMay) {
        LblMaMay.setText(maMay);
    }

    // ================= CHON KH ======================

    @FXML
    public void chonKhachHang() {
        KhachHang kh = cbKhachHang.getValue();

        if (kh != null) {
            lblSoDu.setText(String.valueOf(kh.getSodu()));
        } else {
            lblSoDu.setText("Khach vang lai");
        }
    }

    // ================= MO PHIEN =====================

    @FXML
    public void batDauPhien() {

        if (phienHienTai != null) return;

        KhachHang kh = cbKhachHang.getValue();
        String maKH = (kh != null) ? kh.getMakh() : null;

        phienHienTai = new PhienSuDung();
        phienHienTai.setMaMay(lblMaMay.getText());
        phienHienTai.setMaKH(maKH);
        phienHienTai.setGioBatDau(LocalDateTime.now());

        phienBUS.moPhien(phienHienTai);
        mayBUS.capNhatTrangThai(lblMaMay.getText(), "DANG_SU_DUNG");

        lblGioBatDau.setText(phienHienTai.getGioBatDau().toString());

        batDauRealtime();
        setTrangThaiDangMo();
    }

    // ==================== REALTIME ================

    private void batDauRealtime() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {

            long soPhut = java.time.Duration.between(phienHienTai.getGioBatDau(), LocalDateTime.now()).toMinutes();

            double tien = (soPhut / 60.0) * DON_GIA_GIO;

            lblThoiGian.setText(soPhut + " phut");
            lblTienTamTinh.setText(String.format("%.Of", tien));
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    // =================== DONG PHIEN =================

    @FXML
    public void ketThucPhien() {

        if (phienHienTai == null) return;

        timeline.stop();

        phienHienTai.setGioKetThuc(LocalDateTime.now());
        phienBUS.ketThucPhien(phienHienTai);

        HoaDon hd = hoaDonBUS.taoHoaDon(
                phienHienTai,
                phienBUS.getDichVuTrongPhien(phienHienTai.getMaPhien())
        );

        mayBUS.capNhatTrangThai(lblMaMay.getText(), "TRONG");

        phienHienTai = null;
        setTrangThaiChuaMo();

        System.out.println("Da tao hoa don: " + hd.getMaHD());
    }
    // ===================== TRANG THAI UI ====================

    private void setTrangThaiChuaMo() {

        btnBatDau.setDisable(false);
        btnKetThuc.setDisable(true);

        lblGioBatDau.setText("-");
        lblThoiGian.setText("-");
        lblTienTamTinh.setText("-");
    }

    private void setTrangThaiDangMo() {

        btnBatDau.setDisable(true);
        btnKetThuc.setDisable(false);
    }
}