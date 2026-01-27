package entity;

public class DichVu {
    private int maDichVu;
    private String tenDichVu;
    private double donGia;
    private int soLuongTon;
    private String loai;

    public DichVu() {}

    public DichVu(int maDichVu, String tenDichVu, double donGia, int soLuongTon, String loai) {
        this.maDichVu = maDichVu;
        this.tenDichVu = tenDichVu;
        this.donGia = donGia;
        this.soLuongTon = soLuongTon;
        this.loai = loai;
    }

    public int getMaDichVu() { return maDichVu; }
    public void setMaDichVu(int maDichVu) { this.maDichVu = maDichVu; }

    public String getTenDichVu() { return tenDichVu; }
    public void setTenDichVu(String tenDichVu) { this.tenDichVu = tenDichVu; }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }

    public int getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(int soLuongTon) { this.soLuongTon = soLuongTon; }

    public String getLoai() { return loai; }
    public void setLoai(String loai) { this.loai = loai; }

    @Override
    public String toString() {
        return "DichVu{" +
                "maDichVu=" + maDichVu +
                ", tenDichVu='" + tenDichVu + '\'' +
                ", donGia=" + donGia +
                ", soLuongTon=" + soLuongTon +
                ", loai='" + loai + '\'' +
                '}';
    }
}
