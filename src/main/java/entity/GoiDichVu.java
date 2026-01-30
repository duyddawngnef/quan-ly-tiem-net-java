package entity;

public class GoiDichVu {
    private int maGoi;
    private String tenGoi;
    private int soGio;
    private int soNgay;
    private int soTuan;
    private int soThang;
    private double giaTien;

    public GoiDichVu() {}

    public GoiDichVu(int maGoi, String tenGoi, int soGio, int soNgay, int soTuan, int soThang, double giaTien) {
        this.maGoi = maGoi;
        this.tenGoi = tenGoi;
        this.soGio = soGio;
        this.soNgay = soNgay;
        this.soTuan = soTuan;
        this.soThang = soThang;
        this.giaTien = giaTien;
    }

    public int getMaGoi() { return maGoi; }
    public void setMaGoi(int maGoi) { this.maGoi = maGoi; }

    public String getTenGoi() { return tenGoi; }
    public void setTenGoi(String tenGoi) { this.tenGoi = tenGoi; }

    public int getSoGio() { return soGio; }
    public void setSoGio(int soGio) { this.soGio = soGio; }

    public int getSoNgay() { return soNgay; }
    public void setSoNgay(int soNgay) { this.soNgay = soNgay; }

    public int getSoTuan() { return soTuan; }
    public void setSoTuan(int soTuan) { this.soTuan = soTuan; }

    public int getSoThang() { return soThang; }
    public void setSoThang(int soThang) { this.soThang = soThang; }

    public double getGiaTien() { return giaTien; }
    public void setGiaTien(double giaTien) { this.giaTien = giaTien; }
}
