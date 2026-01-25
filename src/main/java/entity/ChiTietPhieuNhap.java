package entity;

public class ChiTietPhieuNhap {
    private String maPhieu;
    private String maDV;
    private int soLuong;
    private double donGiaNhap;
    private double thanhTien;

    public ChiTietPhieuNhap() {}

    public ChiTietPhieuNhap(String maPhieu, String maDV, int soLuong, double donGiaNhap) {
        this.maPhieu = maPhieu;
        this.maDV = maDV;
        this.soLuong = soLuong;
        this.donGiaNhap = donGiaNhap;
        this.thanhTien = soLuong * donGiaNhap;
    }

    public String getMaPhieu() {
        return maPhieu;
    }

    public String getMaDV() {
        return maDV;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public double getDonGiaNhap() {
        return donGiaNhap;
    }

    public double getThanhtien() {
        return thanhTien;
    }

    public void setMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu;
    }

    public void setMaDV(String maDV) {
        this.maDV = maDV;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
        tinhThanhTien();
    }

    public void setDonGiaNhap(double donGiaNhap) {
        this.donGiaNhap = donGiaNhap;
        tinhThanhTien();
    }

    public void tinhThanhTien() {
        this.thanhTien = this.soLuong * this.donGiaNhap;
    }

    @Override
    public String toString() {
        return "ChiTietPhieuNhap{" +
                "maPhieu=" + maPhieu +
                ", maDV=" + maDV +
                ", soLuong=" + soLuong +
                ", donGiaNhap=" + donGiaNhap +
                ", thanhTien=" + thanhTien +
                '}';
    }
}