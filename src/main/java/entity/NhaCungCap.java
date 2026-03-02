package entity;

public class NhaCungCap {
    private String maNCC;
    private String tenNCC;
    private String soDienThoai;
    private String email;
    private String diaChi;
    private String nguoiLienHe;
    private String trangThai;

    public NhaCungCap() {
    }

    public NhaCungCap(String maNCC, String tenNCC, String soDienThoai, String email, String diaChi, String nguoiLienHe,
            String trangThai) {
        this.maNCC = maNCC;
        this.tenNCC = tenNCC;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.diaChi = diaChi;
        this.nguoiLienHe = nguoiLienHe;
        this.trangThai = trangThai;
    }

    public String getMaNCC() {
        return maNCC;
    }

    public String getTenNCC() {
        return tenNCC;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public String getNguoiLienHe() {
        return nguoiLienHe;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setMaNCC(String maNCC) {
        this.maNCC = maNCC;
    }

    public void setTenNCC(String tenNCC) {
        this.tenNCC = tenNCC;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public void setNguoiLienHe(String nguoiLienHe) {
        this.nguoiLienHe = nguoiLienHe;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "NhaCungCap{" +
                "maNCC=" + maNCC +
                ", tenNCC=" + tenNCC +
                ", soDienThoai=" + soDienThoai +
                ", email=" + email +
                ", diaChi=" + diaChi +
                ", nguoiLienHe=" + nguoiLienHe +
                ", trangThai=" + trangThai +
                '}';
    }

    public boolean isHoatDong() {
        return "HOATDONG".equals(trangThai);
    }

    public boolean isNgung() {
        return "NGUNG".equals(trangThai);
    }

}