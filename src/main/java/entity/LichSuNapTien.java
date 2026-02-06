package entity;

import java.time.LocalDate;

public class LichSuNapTien {
    private String maNap;
    private String maKH;
    private String maNV;
    private String maCTKM;
    private double soTienNap;
    private double khuyenMai;
    private double tongTienCong;
    private double soDuTruoc;
    private double soDuSau;
    private String phuongThuc;
    private String maGiaoDich;
    private LocalDate ngayNap;

    public LichSuNapTien() {}

    public LichSuNapTien(String maNap, String maKH, String maNV, String maCTKM, double soTienNap, double khuyenMai, double tongTienCong, double soDuTruoc, double soDuSau, String phuongThuc, String maGiaoDich, LocalDate ngayNap) {
        this.maNap = maNap;
        this.maKH = maKH;
        this.maNV = maNV;
        this.maCTKM = maCTKM;
        this.soTienNap = soTienNap;
        this.khuyenMai = khuyenMai;
        this.tongTienCong = soTienNap + khuyenMai;
        this.soDuTruoc = soDuTruoc;
        this.soDuSau = soDuSau;
        this.phuongThuc= phuongThuc;
        this.maGiaoDich = maGiaoDich;
        this.ngayNap = ngayNap;

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

    public double getKhuyenMai() {
        return khuyenMai;
    }

    public double getTongTienCong() {
        return tongTienCong;
    }

    public double getSoDuTruoc() {
        return soDuTruoc;
    }

    public double getSoDuSau() {
        return soDuSau;
    }

    public String getPhuongThuc() {
        return phuongThuc;
    }

    public String getMaGiaoDich() {
        return maGiaoDich;
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

    public void setKhuyenMai(double khuyenMai) {
        this.khuyenMai = khuyenMai;
        tinhTongTien();
    }

    public void setTongTienCong(double tongTienCong) {
        this.tongTienCong = tongTienCong;
    }

    public void setSoDuTruoc(double soDuTruoc) {
        this.soDuTruoc = soDuTruoc;
    }

    public void setSoDuSau(double soDuSau) {
        this.soDuSau = soDuSau;
    }

    public void setPhuongThuc(String phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public void setMaGiaoDich(String maGiaoDich) {
        this.maGiaoDich = maGiaoDich;
    }

    public void setNgayNap(LocalDate ngayNap) {
        this.ngayNap = ngayNap;
    }

    public void tinhTongTien() {
        this.tongTienCong = this.soTienNap + this.khuyenMai;
    }

    @Override
    public String toString() {
        return "LichSuNapTien{" +
                "maNap=" + maNap +
                ", maKH=" + maKH +
                ", maNV=" + maNV +
                ", maCTKM=" + maCTKM +
                ", soTienNap=" + soTienNap +
                ", khuyenMai=" + khuyenMai +
                ", tongTienCong=" + tongTienCong +
                ", soDuTruoc=" + soDuTruoc +
                ", soDuSau=" + soDuSau +
                ", phuongThuc=" + phuongThuc +
                ", maGiaoDich=" + maGiaoDich +
                ", ngayNap=" + ngayNap +
                '}';
    }

}