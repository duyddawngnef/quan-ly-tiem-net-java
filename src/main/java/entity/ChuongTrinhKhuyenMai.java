package entity;

import java.time.LocalDate;

public class ChuongTrinhKhuyenMai {
    private String maCTKM;
    private String tenCT;
    private String loaiKM;
    private double giaTriKM;
    private double dieuKienToiThieu;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private String trangThai;

    public ChuongTrinhKhuyenMai() {}

    public ChuongTrinhKhuyenMai(String maCTKM, String tenCT, String loaiKM, double giaTriKM, double dieuKienToiThieu, LocalDate ngayBatDau, LocalDate ngayKetThuc, String trangThai) {
        this.maCTKM = maCTKM;
        this.tenCT = tenCT;
        this.loaiKM = loaiKM;
        this.giaTriKM = giaTriKM;
        this.dieuKienToiThieu = dieuKienToiThieu;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.trangThai = trangThai;
    }

    public String getMaCTKM() {
        return maCTKM;
    }

    public String getTenCT() {
        return tenCT;
    }

    public String getLoaiKM() {
        return loaiKM;
    }

    public double getGiaTriKM() {
        return giaTriKM;
    }

    public double getDieuKienToiThieu() {
        return dieuKienToiThieu;
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setMaCTKM(String maCTKM) {
        this.maCTKM = maCTKM;
    }

    public void setTenCT(String tenCT) {
        this.tenCT = tenCT;
    }

    public void setLoaiKM(String loaiKM) {
        this.loaiKM = loaiKM;
    }

    public void setGiaTriKM(double giaTriKM) {
        this.giaTriKM = giaTriKM;
    }

    public void setDieuKienToiThieu(double dieuKienToiThieu) {
        this.dieuKienToiThieu = dieuKienToiThieu;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "ChuongTrinhKhuyenMai{" +
                "maCTKM=" + maCTKM +
                ", tenCT=" + tenCT +
                ", loaiKM=" + loaiKM +
                ", giaTriKM=" + giaTriKM +
                ", dieuKienToiThieu=" + dieuKienToiThieu +
                ", ngayBatDau=" + ngayBatDau +
                ", ngayKetThuc=" + ngayKetThuc +
                ", trangThai=" + trangThai +
                '}';
    }
}