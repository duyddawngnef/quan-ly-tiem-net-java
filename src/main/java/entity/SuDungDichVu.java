package entity;

import java.time.LocalDateTime;

public class SuDungDichVu {

    private String maSD;
    private String maPhien;
    private String maDV;
    private int soLuong;
    private double donGia;
    private double thanhTien;
    private LocalDateTime thoiGian;

    public SuDungDichVu() {}

    public SuDungDichVu(String maSD, String maPhien, String maDV,
                        int soLuong, double donGia,
                        double thanhTien, LocalDateTime thoiGian) {
        this.maSD = maSD;
        this.maPhien = maPhien;
        this.maDV = maDV;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
        this.thoiGian = thoiGian;
    }

    public String getMaSD() { return maSD; }
    public void setMaSD(String maSD) { this.maSD = maSD; }

    public String getMaPhien() { return maPhien; }
    public void setMaPhien(String maPhien) { this.maPhien = maPhien; }

    public String getMaDV() { return maDV; }
    public void setMaDV(String maDV) { this.maDV = maDV; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }

    public double getThanhTien() { return thanhTien; }
    public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }

    public LocalDateTime getThoiGian() { return thoiGian; }
    public void setThoiGian(LocalDateTime thoiGian) { this.thoiGian = thoiGian; }

    @Override
    public String toString() {
        return maSD + " | " + maDV + " | " + thanhTien;
    }
}
