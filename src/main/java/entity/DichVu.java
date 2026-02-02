package entity;

public class DichVu {

    private String maDV;
    private String tenDV;
    private String loaiDV;
    private double donGia;
    private String donViTinh;
    private int soLuongTon;
    private String trangThai;

    public DichVu() {}

    public DichVu(String maDV, String tenDV, String loaiDV,
                  double donGia, String donViTinh,
                  int soLuongTon, String trangThai) {
        this.maDV = maDV;
        this.tenDV = tenDV;
        this.loaiDV = loaiDV;
        this.donGia = donGia;
        this.donViTinh = donViTinh;
        this.soLuongTon = soLuongTon;
        this.trangThai = trangThai;
    }

    public String getMaDV() { return maDV; }
    public void setMaDV(String maDV) { this.maDV = maDV; }

    public String getTenDV() { return tenDV; }
    public void setTenDV(String tenDV) { this.tenDV = tenDV; }

    public String getLoaiDV() { return loaiDV; }
    public void setLoaiDV(String loaiDV) { this.loaiDV = loaiDV; }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }

    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }

    public int getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(int soLuongTon) { this.soLuongTon = soLuongTon; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    @Override
    public String toString() {
        return maDV + " | " + tenDV + " | " + donGia + " | " + soLuongTon;
    }
}
