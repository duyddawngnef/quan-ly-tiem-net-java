package entity;

import java.time.LocalDateTime;

public class PhienSuDung {

    private int maPhien;
    private int maMay;
    private Integer maKhachHang;

    private LocalDateTime thoiGianBatDau;
    private LocalDateTime thoiGianKetThuc;

    private double donGiaGio;
    private double tienGio;

    private boolean dangSuDung;

    public PhienSuDung() {
    }

    // Getter & Setter
    public int getMaPhien() {
        return maPhien;
    }

    public void setMaPhien(int maPhien) {
        this.maPhien = maPhien;
    }

    public int getMaMay() {
        return maMay;
    }

    public void setMaMay(int maMay) {
        this.maMay = maMay;
    }

    public Integer getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(Integer maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public LocalDateTime getThoiGianBatDau() {
        return thoiGianBatDau;
    }

    public void setThoiGianBatDau(LocalDateTime thoiGianBatDau) {
        this.thoiGianBatDau = thoiGianBatDau;
    }

    public LocalDateTime getThoiGianKetThuc() {
        return thoiGianKetThuc;
    }

    public void setThoiGianKetThuc(LocalDateTime thoiGianKetThuc) {
        this.thoiGianKetThuc = thoiGianKetThuc;
    }

    public double getDonGiaGio() {
        return donGiaGio;
    }

    public void setDonGiaGio(double donGiaGio) {
        this.donGiaGio = donGiaGio;
    }

    public double getTienGio() {
        return tienGio;
    }

    public void setTienGio(double tienGio) {
        this.tienGio = tienGio;
    }

    public boolean isDangSuDung() {
        return dangSuDung;
    }

    public void setDangSuDung(boolean dangSuDung) {
        this.dangSuDung = dangSuDung;
    }
}
