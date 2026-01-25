package entity;

public class ChiTietPhieuNhap {
    private String maphieu;
    private String madv;
    private int soluong;
    private double dongianhap;
    private double thanhtien;

    public ChiTietPhieuNhap() {}

    public ChiTietPhieuNhap(String maphieu, String madv, int soluong, double dongianhap, double thanhtien) {
        this.maphieu = maphieu;
        this.madv = madv;
        this.soluong = soluong;
        this.dongianhap = dongianhap;
        this.thanhtien = thanhtien;
    }

    public String getMaphieu() {
        return maphieu;
    }

    public String getMadv() {
        return madv;
    }

    public int getSoluong() {
        return soluong;
    }

    public double getDongianhap() {
        return dongianhap;
    }

    public double getThanhtien() {
        return thanhtien;
    }

    public void setMaphieu(String maphieu) {
        this.maphieu = maphieu;
    }

    public void setMadv(String madv) {
        this.madv = madv;
    }

    public void setSoluong(int soluong) {
        this.soluong = soluong;
    }

    public void setDongianhap(double dongianhap) {
        this.dongianhap = dongianhap;
    }

    public void setThanhtien(double thanhtien) {
        this.thanhtien = thanhtien;
    }
}