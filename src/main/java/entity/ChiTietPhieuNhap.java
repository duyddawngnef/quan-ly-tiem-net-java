package entity;

public class ChiTietPhieuNhap {
    private String maCTPN;
    private String maPhieuNhap;
    private String maDV;
    private int soLuong;
    private double giaNhap;
    private double thanhTien;

    public ChiTietPhieuNhap() {
    }

    public ChiTietPhieuNhap(String maCTPN, String maPhieuNhap, String maDV, int soLuong, double giaNhap,
            double thanhTien) {
        this.maCTPN = maCTPN;
        this.maPhieuNhap = maPhieuNhap;
        this.maDV = maDV;
        this.soLuong = soLuong;
        this.giaNhap = giaNhap;
        this.thanhTien = soLuong * giaNhap;
    }

    public String getMaCTPN() {
        return maCTPN;
    }

    public String getMaPhieuNhap() {
        return maPhieuNhap;
    }

    public String getMaDV() {
        return maDV;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public double getGiaNhap() {
        return giaNhap;
    }

    public double getThanhTien() {
        return thanhTien;
    }

    public void setMaCTPN(String maCTPN) {
        this.maCTPN = maCTPN;
    }

    public void setMaPhieu(String maPhieuNhap) {
        this.maPhieuNhap = maPhieuNhap;
    }

    public void setMaDV(String maDV) {
        this.maDV = maDV;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
        tinhThanhTien();
    }

    public void setGiaNhap(double giaNhap) {
        this.giaNhap = giaNhap;
        tinhThanhTien();
    }

    public void setThanhTien(double thanhTien) {
        this.thanhTien = thanhTien;
    }

    public void tinhThanhTien() {
        this.thanhTien = this.soLuong * this.giaNhap;
    }

    @Override
    public String toString() {
        return "ChiTietPhieuNhap{" +
                "maCTPN=" + maCTPN +
                ", maPhieuNhap=" + maPhieuNhap +
                ", maDV=" + maDV +
                ", soLuong=" + soLuong +
                ", giaNhap=" + giaNhap +
                ", thanhTien=" + thanhTien +
                '}';
    }

}