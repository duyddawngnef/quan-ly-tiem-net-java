package entity;

import java.time.LocalDate;

public class PhieuNhapHang {
    private String maPhieu;
    private String maNCC;
    private String maNV;
    private LocalDate ngayNhap;
    private double tongTien;
    private String ghiChu;

    public PhieuNhapHang() {}

    public PhieuNhapHang(String maPhieu, String maNCC, String maNV, LocalDate ngayNhap, double tongTien, String ghiChu) {
        this.maPhieu = maPhieu;
        this.maNCC = maNCC;
        this.maNV = maNV;
        this.ngayNhap = ngayNhap;
        this.tongTien = tongTien;
        this.ghiChu = ghiChu;
    }

    public String getMaPhieu() {
        return maPhieu;
    }

    public String getMaNCC() {
        return maNCC;
    }

    public String getMaNV() {
        return maNV;
    }

    public LocalDate getNgayNhap() {
        return ngayNhap;
    }

    public double getTongTien() {
        return tongTien;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu;
    }

    public void setMaNCC(String maNCC) {
        this.maNCC = maNCC;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public void setNgayNhap(LocalDate ngayNhap) {
        this.ngayNhap = ngayNhap;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    @Override
    public String toString() {
        return "PhieuNhapHang{" +
                "maPhieu=" + maPhieu +
                ", maNCC=" + maNCC +
                ", maNV=" + maNV +
                ", ngayNhap=" + ngayNhap +
                ", tongTien=" + tongTien +
                ", ghiChu=" + ghiChu +
                '}';
    }
}