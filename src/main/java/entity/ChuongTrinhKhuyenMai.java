package entity;

import java.time.LocalDate;

public class ChuongTrinhKhuyenMai {
    private String maCTKM;
    private String tenCTKM;
    private String loaiKM;
    private double giaTri;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private String trangThai;

    public ChuongTrinhKhuyenMai() {}

    public ChuongTrinhKhuyenMai(String maCTKM, String tenCTKM, String loaiKM, double giaTri, LocalDate ngayBatDau, LocalDate ngayKetThuc, String trangThai) {
        this.maCTKM = maCTKM;
        this.tenCTKM = tenCTKM;
        this.loaiKM = loaiKM;
        this.giaTri = giaTri;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.trangThai = trangThai;
    }

    public String getMaCTKM() {
        return maCTKM;
    }

    public String getTenCTKM() {
        return tenCTKM;
    }

    public String getLoaiKM() {
        return loaiKM;
    }

    public double getGiaTri() {
        return giaTri;
    }

    public LocalDate getNgaybBatDau() {
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

    public void setTenCTKM(String tenCTKM) {
        this.tenCTKM = tenCTKM;
    }

    public void setLoaiKM(String loaiKM) {
        this.loaiKM = loaiKM;
    }

    public void setGiaTri(double giaTri) {
        this.giaTri = giaTri;
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
                ", tenCTKM=" + tenCTKM +
                ", loaiKM=" + loaiKM +
                ", giaTri=" + giaTri +
                ", ngayBatDau=" + ngayBatDau +
                ", ngayKetThuc=" + ngayKetThuc +
                ", trangThai=" + trangThai +
                '}';
    }
}