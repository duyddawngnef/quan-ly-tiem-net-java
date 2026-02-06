package entity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Entity class đại diện cho bảng phiensudung
 */
public class PhienSuDung {
    private String maPhien;
    private String maKH;
    private String maMay;
    private String maNV;  // Nullable - có thể null nếu tự động
    private String maGoiKH;  // Nullable - có thể không dùng gói
    private LocalDateTime gioBatDau;
    private LocalDateTime gioKetThuc;
    private double tongGio;
    private double gioSuDungTuGoi;
    private double gioSuDungTuTaiKhoan;
    private double giaMoiGio;
    private double tienGioChoi;
    private String loaiThanhToan;  // ENUM: TAIKHOAN, GOI, KETHOP
    private String trangThai;      // ENUM: DANGCHOI, DAKETTHUC

    // Constructors
    public PhienSuDung() {
        this.gioBatDau = LocalDateTime.now();
        this.tongGio = 0.0;
        this.gioSuDungTuGoi = 0.0;
        this.gioSuDungTuTaiKhoan = 0.0;
        this.tienGioChoi = 0.0;
        this.loaiThanhToan = "TAIKHOAN";
        this.trangThai = "DANGCHOI";
    }

    public PhienSuDung(String maKH, String maMay, double giaMoiGio) {
        this();
        this.maKH = maKH;
        this.maMay = maMay;
        this.giaMoiGio = giaMoiGio;
    }

    public PhienSuDung(String maPhien, String maKH, String maMay, String maNV, String maGoiKH,
                       LocalDateTime gioBatDau, LocalDateTime gioKetThuc, double tongGio,
                       double gioSuDungTuGoi, double gioSuDungTuTaiKhoan, double giaMoiGio,
                       double tienGioChoi, String loaiThanhToan, String trangThai) {
        this.maPhien = maPhien;
        this.maKH = maKH;
        this.maMay = maMay;
        this.maNV = maNV;
        this.maGoiKH = maGoiKH;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.tongGio = tongGio;
        this.gioSuDungTuGoi = gioSuDungTuGoi;
        this.gioSuDungTuTaiKhoan = gioSuDungTuTaiKhoan;
        this.giaMoiGio = giaMoiGio;
        this.tienGioChoi = tienGioChoi;
        this.loaiThanhToan = loaiThanhToan;
        this.trangThai = trangThai;
    }

    // Getters and Setters
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

    public String getMaMay() {
        return maMay;
    }

    public void setMaMay(String maMay) {
        this.maMay = maMay;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getMaGoiKH() {
        return maGoiKH;
    }

    public void setMaGoiKH(String maGoiKH) {
        this.maGoiKH = maGoiKH;
    }

    public LocalDateTime getGioBatDau() {
        return gioBatDau;
    }

    public void setGioBatDau(LocalDateTime gioBatDau) {
        this.gioBatDau = gioBatDau;
    }

    public LocalDateTime getGioKetThuc() {
        return gioKetThuc;
    }

    public void setGioKetThuc(LocalDateTime gioKetThuc) {
        this.gioKetThuc = gioKetThuc;
    }

    public double getTongGio() {
        return tongGio;
    }

    public void setTongGio(double tongGio) {
        this.tongGio = tongGio;
    }

    public double getGioSuDungTuGoi() {
        return gioSuDungTuGoi;
    }

    public void setGioSuDungTuGoi(double gioSuDungTuGoi) {
        this.gioSuDungTuGoi = gioSuDungTuGoi;
    }

    public double getGioSuDungTuTaiKhoan() {
        return gioSuDungTuTaiKhoan;
    }

    public void setGioSuDungTuTaiKhoan(double gioSuDungTuTaiKhoan) {
        this.gioSuDungTuTaiKhoan = gioSuDungTuTaiKhoan;
    }

    public double getGiaMoiGio() {
        return giaMoiGio;
    }

    public void setGiaMoiGio(double giaMoiGio) {
        this.giaMoiGio = giaMoiGio;
    }

    public double getTienGioChoi() {
        return tienGioChoi;
    }

    public void setTienGioChoi(double tienGioChoi) {
        this.tienGioChoi = tienGioChoi;
    }

    public String getLoaiThanhToan() {
        return loaiThanhToan;
    }

    public void setLoaiThanhToan(String loaiThanhToan) {
        this.loaiThanhToan = loaiThanhToan;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    // Business methods

    /**
     * Tính tổng giờ chơi từ giờ bắt đầu đến hiện tại (hoặc giờ kết thúc)
     */
    public void tinhTongGio() {
        if (gioBatDau != null) {
            LocalDateTime gioKT = (gioKetThuc != null) ? gioKetThuc : LocalDateTime.now();
            long phut = ChronoUnit.MINUTES.between(gioBatDau, gioKT);
            this.tongGio = phut / 60.0;
        }
    }

    /**
     * Tính tiền giờ chơi dựa trên giờ sử dụng từ tài khoản
     */
    public void tinhTienGioChoi() {
        this.tienGioChoi = this.gioSuDungTuTaiKhoan * this.giaMoiGio;
    }

    /**
     * Kiểm tra phiên đang chơi
     */
    public boolean isDangChoi() {
        return "DANGCHOI".equals(this.trangThai);
    }

    /**
     * Kiểm tra phiên đã kết thúc
     */
    public boolean isDaKetThuc() {
        return "DAKETTHUC".equals(this.trangThai);
    }

    /**
     * Kiểm tra có sử dụng gói không
     */
    public boolean isSuDungGoi() {
        return this.maGoiKH != null && this.gioSuDungTuGoi > 0;
    }

    /**
     * Lấy tỷ lệ phần trăm sử dụng từ gói
     */
    public double getTyLeGoi() {
        if (tongGio > 0) {
            return (gioSuDungTuGoi / tongGio) * 100;
        }
        return 0;
    }

    /**
     * Lấy thời gian đã chơi (dạng text)
     */
    public String getThoiGianChoiText() {
        int gio = (int) tongGio;
        int phut = (int) ((tongGio - gio) * 60);
        return String.format("%d giờ %d phút", gio, phut);
    }

    @Override
    public String toString() {
        return "PhienSuDung{" +
                "maPhien='" + maPhien + '\'' +
                ", maKH='" + maKH + '\'' +
                ", maMay='" + maMay + '\'' +
                ", maNV='" + maNV + '\'' +
                ", maGoiKH='" + maGoiKH + '\'' +
                ", gioBatDau=" + gioBatDau +
                ", gioKetThuc=" + gioKetThuc +
                ", tongGio=" + tongGio +
                ", gioSuDungTuGoi=" + gioSuDungTuGoi +
                ", gioSuDungTuTaiKhoan=" + gioSuDungTuTaiKhoan +
                ", giaMoiGio=" + giaMoiGio +
                ", tienGioChoi=" + tienGioChoi +
                ", loaiThanhToan='" + loaiThanhToan + '\'' +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhienSuDung that = (PhienSuDung) o;
        return maPhien != null && maPhien.equals(that.maPhien);
    }

    @Override
    public int hashCode() {
        return maPhien != null ? maPhien.hashCode() : 0;
    }
}