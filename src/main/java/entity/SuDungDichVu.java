package entity;

import java.time.LocalDateTime;

public class SuDungDichVu {
    private int maSuDung;
    private int maPhien;
    private int maDichVu;
    private int soLuong;
    private double thanhTien;
    private LocalDateTime thoiGian;

    public SuDungDichVu() {}

    public SuDungDichVu(int maSuDung, int maPhien, int maDichVu, int soLuong, double thanhTien, LocalDateTime thoiGian) {
        this.maSuDung = maSuDung;
        this.maPhien = maPhien;
        this.maDichVu = maDichVu;
        this.soLuong = soLuong;
        this.thanhTien = thanhTien;
        this.thoiGian = thoiGian;
    }

    public int getMaSuDung() { return maSuDung; }
    public void setMaSuDung(int maSuDung) { this.maSuDung = maSuDung; }

    public int getMaPhien() { return maPhien; }
    public void setMaPhien(int maPhien) { this.maPhien = maPhien; }

    public int getMaDichVu() { return maDichVu; }
    public void setMaDichVu(int maDichVu) { this.maDichVu = maDichVu; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public double getThanhTien() { return thanhTien; }
    public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }

    public LocalDateTime getThoiGian() { return thoiGian; }
    public void setThoiGian(LocalDateTime thoiGian) { this.thoiGian = thoiGian; }
}
