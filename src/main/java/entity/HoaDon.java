package entity;

import java.time.LocalDateTime;

/**
 * Entity class đại diện cho bảng hoadon
 */
public class HoaDon {
    private String maHD;
    private String maPhien;
    private String maKH;
    private String maNV;
    private LocalDateTime ngayLap;
    private double tienGioChoi;
    private double tienDichVu;
    private double tongTien;
    private double giamGia;
    private double thanhToan;
    private String phuongThucTT; // ENUM: TIENMAT, CHUYENKHOAN, MOMO, VNPAY, TAIKHOAN
    private String trangThai;    // ENUM: CHUATHANHTOAN, DATHANHTOAN

    // Constructors
    public HoaDon() {
        this.ngayLap = LocalDateTime.now();
        this.tienGioChoi = 0.0;
        this.tienDichVu = 0.0;
        this.giamGia = 0.0;
        this.phuongThucTT = "TAIKHOAN";
        this.trangThai = "CHUATHANHTOAN";
    }

    public HoaDon(String maPhien, String maKH, String maNV) {
        this();
        this.maPhien = maPhien;
        this.maKH = maKH;
        this.maNV = maNV;
    }

    public HoaDon(String maHD, String maPhien, String maKH, String maNV, LocalDateTime ngayLap,
                  double tienGioChoi, double tienDichVu, double tongTien, double giamGia,
                  double thanhToan, String phuongThucTT, String trangThai) {
        this.maHD = maHD;
        this.maPhien = maPhien;
        this.maKH = maKH;
        this.maNV = maNV;
        this.ngayLap = ngayLap;
        this.tienGioChoi = tienGioChoi;
        this.tienDichVu = tienDichVu;
        this.tongTien = tongTien;
        this.giamGia = giamGia;
        this.thanhToan = thanhToan;
        this.phuongThucTT = phuongThucTT;
        this.trangThai = trangThai;
    }

    // Getters and Setters
    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public String getMaPhien() {
        return maPhien;
    }

    public void setMaPhien(String maPhien) {
        this.maPhien = maPhien;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        this.ngayLap = ngayLap;
    }

    public double getTienGioChoi() {
        return tienGioChoi;
    }

    public void setTienGioChoi(double tienGioChoi) {
        this.tienGioChoi = tienGioChoi;
    }

    public double getTienDichVu() {
        return tienDichVu;
    }

    public void setTienDichVu(double tienDichVu) {
        this.tienDichVu = tienDichVu;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public double getGiamGia() {
        return giamGia;
    }

    public void setGiamGia(double giamGia) {
        this.giamGia = giamGia;
    }

    public double getThanhToan() {
        return thanhToan;
    }

    public void setThanhToan(double thanhToan) {
        this.thanhToan = thanhToan;
    }

    public String getPhuongThucTT() {
        return phuongThucTT;
    }

    public void setPhuongThucTT(String phuongThucTT) {
        this.phuongThucTT = phuongThucTT;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    // Business methods

    /**
     * Tính tổng tiền từ tiền giờ chơi và tiền dịch vụ
     */
    public void tinhTongTien() {
        this.tongTien = this.tienGioChoi + this.tienDichVu;
    }

    /**
     * Tính số tiền thanh toán sau khi trừ giảm giá
     */
    public void tinhThanhToan() {
        this.thanhToan = this.tongTien - this.giamGia;
    }

    /**
     * Tự động tính toán tổng tiền và thanh toán
     */
    public void tinhToanTuDong() {
        tinhTongTien();
        tinhThanhToan();
    }

    /**
     * Kiểm tra hóa đơn đã thanh toán chưa
     */
    public boolean isDaThanhToan() {
        return "DATHANHTOAN".equals(this.trangThai);
    }

    @Override
    public String toString() {
        return "HoaDon{" +
                "maHD='" + maHD + '\'' +
                ", maPhien='" + maPhien + '\'' +
                ", maKH='" + maKH + '\'' +
                ", maNV='" + maNV + '\'' +
                ", ngayLap=" + ngayLap +
                ", tienGioChoi=" + tienGioChoi +
                ", tienDichVu=" + tienDichVu +
                ", tongTien=" + tongTien +
                ", giamGia=" + giamGia +
                ", thanhToan=" + thanhToan +
                ", phuongThucTT='" + phuongThucTT + '\'' +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HoaDon hoaDon = (HoaDon) o;
        return maHD != null && maHD.equals(hoaDon.maHD);
    }

    @Override
    public int hashCode() {
        return maHD != null ? maHD.hashCode() : 0;
    }
}