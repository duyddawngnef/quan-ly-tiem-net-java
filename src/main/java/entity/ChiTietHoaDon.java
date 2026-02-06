package entity;

/**
 * Entity class đại diện cho bảng chitiethoadon
 */
public class ChiTietHoaDon {
    private String maCTHD;
    private String maHD;
    private String loaiChiTiet; // ENUM: GIOCHOI, DICHVU
    private String moTa;
    private double soLuong;
    private double donGia;
    private double thanhTien;

    // Constructors
    public ChiTietHoaDon() {
        this.soLuong = 1.0;
    }

    public ChiTietHoaDon(String maHD, String loaiChiTiet, String moTa, double soLuong, double donGia) {
        this.maHD = maHD;
        this.loaiChiTiet = loaiChiTiet;
        this.moTa = moTa;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = soLuong * donGia;
    }

    public ChiTietHoaDon(String maCTHD, String maHD, String loaiChiTiet, String moTa,
                         double soLuong, double donGia, double thanhTien) {
        this.maCTHD = maCTHD;
        this.maHD = maHD;
        this.loaiChiTiet = loaiChiTiet;
        this.moTa = moTa;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
    }

    // Getters and Setters
    public String getMaCTHD() {
        return maCTHD;
    }

    public void setMaCTHD(String maCTHD) {
        this.maCTHD = maCTHD;
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public String getLoaiChiTiet() {
        return loaiChiTiet;
    }

    public void setLoaiChiTiet(String loaiChiTiet) {
        this.loaiChiTiet = loaiChiTiet;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public double getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(double soLuong) {
        this.soLuong = soLuong;
        this.tinhThanhTien(); // Tự động tính lại thành tiền
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
        this.tinhThanhTien(); // Tự động tính lại thành tiền
    }

    public double getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(double thanhTien) {
        this.thanhTien = thanhTien;
    }

    // Business methods

    /**
     * Tính thành tiền = số lượng * đơn giá
     */
    public void tinhThanhTien() {
        this.thanhTien = this.soLuong * this.donGia;
    }

    /**
     * Kiểm tra có phải chi tiết giờ chơi không
     */
    public boolean isGioChoi() {
        return "GIOCHOI".equals(this.loaiChiTiet);
    }

    /**
     * Kiểm tra có phải chi tiết dịch vụ không
     */
    public boolean isDichVu() {
        return "DICHVU".equals(this.loaiChiTiet);
    }

    @Override
    public String toString() {
        return "ChiTietHoaDon{" +
                "maCTHD='" + maCTHD + '\'' +
                ", maHD='" + maHD + '\'' +
                ", loaiChiTiet='" + loaiChiTiet + '\'' +
                ", moTa='" + moTa + '\'' +
                ", soLuong=" + soLuong +
                ", donGia=" + donGia +
                ", thanhTien=" + thanhTien +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietHoaDon that = (ChiTietHoaDon) o;
        return maCTHD != null && maCTHD.equals(that.maCTHD);
    }

    @Override
    public int hashCode() {
        return maCTHD != null ? maCTHD.hashCode() : 0;
    }
}