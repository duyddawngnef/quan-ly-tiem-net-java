package entity;

import java.time.LocalDate;

public class PhieuNhapHang {
    private String maphieu;
    private String mancc;
    private String manv;
    private LocalDate ngaynhap;
    private double tongtien;
    private String ghichu;

    public PhieuNhapHang() {}

    public PhieuNhapHang(String maphieu, String mancc, String manv, LocalDate ngaynhap, double tongtien, String ghichu) {
        this.maphieu = maphieu;
        this.mancc = mancc;
        this.manv = manv;
        this.ngaynhap = ngaynhap;
        this.tongtien = tongtien;
        this.ghichu = ghichu;
    }

    public String getMaphieu() {
        return maphieu;
    }

    public String getMancc() {
        return mancc;
    }

    public String getManv() {
        return manv;
    }

    public LocalDate getNgaynhap() {
        return ngaynhap;
    }

    public double getTongtien() {
        return tongtien;
    }

    public String getGhichu() {
        return ghichu;
    }

    public void setMaphieu(String maphieu) {
        this.maphieu = maphieu;
    }

    public void setMancc(String mancc) {
        this.mancc = mancc;
    }

    public void setManv(String manv) {
        this.manv = manv;
    }

    public void setNgaynhap(LocalDate ngaynhap) {
        this.ngaynhap = ngaynhap;
    }

    public void setTongtien(double tongtien) {
        this.tongtien = tongtien;
    }

    public void setGhichu(String ghichu) {
        this.ghichu = ghichu;
    }
}