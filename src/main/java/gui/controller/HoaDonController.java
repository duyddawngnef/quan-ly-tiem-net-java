package controller;

import bus.HoaDonBUS;
import entity.HoaDon;
import entity.ChiTietHoaDon;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.util.List;

public class HoaDonController {
    @FXML
    private Label lblMaHD;
    @FXML
    private Label lblTongTien;
    @FXML
    private Label lblThanhToan;

    @FXML
    private TableView<ChiTietHoaDon> tableChiTiet;

    private HoaDonBUS hoaDonBUS = new hoaDonBUS();
    private HoaDon hoaDon;

    public void loadHoaDon(String maHD) {

        hoaDon = hoaDonBUS.getHoaDonById(maHD);

        lblMaHD.setText(hoaDon.getMaHD());
        lblTongTien.setText(String.format("%.Of", hoaDon.getTongTien()));
        lblThanhToan.setText(String.format("%.Of", hoaDon.getThanhToan()));

        List<ChiTietHoaDon> ds = hoaDonBUS.getChiTietHoaDon(maHD);
        tableChiTiet.getItems().setAll(ds);
    }

    @FXML
    public void thanhToanTienMat() {
        hoaDonBUS.thanhToan(hoaDon, "TIENMAT");
    }

    @FXML
    public void inHoaDon() {
        hoaDonBUS.inHoaDon(hoaDon.getMaHD());
    }
}