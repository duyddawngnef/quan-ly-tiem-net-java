package entity;

public class ThongKe {
    private String thoiGian;
    private double tongDoanhThu;
    private double tongNhapHang;
    private double loiNhuan;
    private double thu;
    private double chi;
    private double loiNhuanSummary;
    private int soPhien;

    public ThongKe() {
    }

    public ThongKe(String thoiGian, double tongDoanhThu, double tongNhapHang) {
        this.thoiGian = thoiGian;
        this.tongDoanhThu = tongDoanhThu;
        this.tongNhapHang = tongNhapHang;
        this.loiNhuan = tongDoanhThu - tongNhapHang;
    }

    public ThongKe(double thu, double chi, int soPhien) {
        this.thu = thu;
        this.chi = chi;
        this.soPhien = soPhien;
        this.loiNhuanSummary = thu - chi;
    }

    public String getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(String thoiGian) {
        this.thoiGian = thoiGian;
    }

    public double getTongDoanhThu() {
        return tongDoanhThu;
    }

    public void setTongDoanhThu(double tongDoanhThu) {
        this.tongDoanhThu = tongDoanhThu;
        this.loiNhuan = this.tongDoanhThu - this.tongNhapHang;
    }

    public double getTongNhapHang() {
        return tongNhapHang;
    }

    public void setTongNhapHang(double tongNhapHang) {
        this.tongNhapHang = tongNhapHang;
        this.loiNhuan = this.tongDoanhThu - this.tongNhapHang;
    }

    public double getLoiNhuan() {
        return loiNhuan;
    }

    public void setLoiNhuan(double loiNhuan) {
        this.loiNhuan = loiNhuan;
    }

    public double getThu() {
        return thu;
    }

    public void setThu(double thu) {
        this.thu = thu;
        this.loiNhuanSummary = this.thu - this.chi;
    }

    public double getChi() {
        return chi;
    }

    public void setChi(double chi) {
        this.chi = chi;
        this.loiNhuanSummary = this.thu - this.chi;
    }

    public double getLoiNhuanSummary() {
        return loiNhuanSummary;
    }

    public void setLoiNhuanSummary(double loiNhuanSummary) {
        this.loiNhuanSummary = loiNhuanSummary;
    }

    public int getSoPhien() {
        return soPhien;
    }

    public void setSoPhien(int soPhien) {
        this.soPhien = soPhien;
    }
}