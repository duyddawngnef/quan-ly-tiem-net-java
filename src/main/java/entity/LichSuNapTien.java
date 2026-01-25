package entity;

import java.time.LocalDate;

public class LichSuNapTien {
    private String maNap;
    private String maKH;
    private String maNV;
    private String maCTKM;
    private double soTienNap;
    private double tienKhuyenMai;
    private double tongTien;
    private LocalDate ngayNap;

    public LichSuNapTien() {}

    public LichSuNapTien(String maNap, String maKH, String maNV, String maCTKM, double soTienNap, double tienKhuyenMai, LocalDate ngayNap) {
        this.maNap = maNap;
        this.maKH = maKH;
        this.maNV = maNV;
        this.maCTKM = maCTKM;
        this.soTienNap = soTienNap;
        this.tienKhuyenMai = tienKhuyenMai;
        this.ngayNap = ngayNap;
        this.tongTien = soTienNap + tienKhuyenMai;
    }

    public String getMaNap(){
        return maNap;
    }

    public String getMaKH() {
        return maKH;
    }

    public String getMaNV() {
        return maNV;
    }

    public String getMaCTKM() {
        return maCTKM;
    }

    public double getSoTienNap() {
        return soTienNap;
    }

    public double getTienKhuyenMai() {
        return tienKhuyenMai;
    }

    public double getTongTien() {
        return tongTien;
    }

    public LocalDate getNgayNap() {
        return ngayNap;
    }

    public void setMaNap(String maNap) {
        this.maNap = maNap;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public void setMaCTKM(String maCTKM) {
        this.maCTKM = maCTKM;
    }

    public void setSoTienNap(double soTienNap) {
        this.soTienNap = soTienNap;
        tinhTongTien();
    }

    public void setTienKhuyenMai(double tienKhuyenMai) {
        this.tienKhuyenMai = tienKhuyenMai;
        tinhTongTien();
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public void setNgayNap(LocalDate ngayNap) {
        this.ngayNap = ngayNap;
    }

    public void tinhTongTien() {
        this.tongTien = this.soTienNap + this.tienKhuyenMai;
    }

    @Override
    public String toString() {
        return "LichSuNapTien{" +
                "maNap=" + maNap +
                ", maKH=" + maKH +
                ", maNV=" + maNV +
                ", maCTKM=" + maCTKM +
                ", soTienNap=" + soTienNap +
                ", tienKhuyenMai=" + tienKhuyenMai +
                ", tongTien=" + tongTien +
                ", ngayNap=" + ngayNap +
                '}';
    }
}