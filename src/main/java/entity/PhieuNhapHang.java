package entity;

import java.time.LocalDate;

public class PhieuNhapHang {
    private String maPhieuNhap;
    private String maNCC;
    private String maNV;
    private LocalDate ngayNhap;
    private double tongTien;
    private String trangThai;

    public PhieuNhapHang() {}

    public PhieuNhapHang(String maPhieuNhap, String maNCC, String maNV, LocalDate ngayNhap, double tongTien,
            String trangThai) {
        this.maPhieuNhap = maPhieuNhap;
        this.maNCC = maNCC;
        this.maNV = maNV;
        this.ngayNhap = ngayNhap;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
    }

    public String getMaPhieuNhap() {
        return maPhieuNhap;
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

    public String getTrangThai() {
        return trangThai;
    }

    public void setMaPhieuNhap(String maPhieuNhap) {
        this.maPhieuNhap = maPhieuNhap;
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

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "PhieuNhapHang{" +
                "maPhieuNhap='" + maPhieuNhap +
                ", maNCC='" + maNCC +
                ", maNV='" + maNV +
                ", ngayNhap=" + ngayNhap +
                ", tongTien=" + tongTien +
                ", trangThai='" + trangThai +
                '}';
    }

    public boolean isChoDuyet() {
        return "CHODUYET".equals(trangThai);
    }

    public boolean isDaNhap() {
        return "DANHAP".equals(trangThai);
    }

    public boolean isDaHuy() {
        return "DAHUY".equals(trangThai);
    }
}