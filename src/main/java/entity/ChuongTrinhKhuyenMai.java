package entity;

import java.time.LocalDate;

public class ChuongTrinhKhuyenMai {
    private String mactkm;
    private String tenctkm;
    private String loaikm;
    private double giatri;
    private LocalDate ngaybatdau;
    private LocalDate ngayketthuc;
    private String trangthai;

    public ChuongTrinhKhuyenMai() {}

    public ChuongTrinhKhuyenMai(String mactkm, String tenctkm, String loaikm, double giatri, LocalDate ngaybatdau, LocalDate ngayketthuc, String trangthai) {
        this.mactkm = mactkm;
        this.tenctkm = tenctkm;
        this.loaikm = loaikm;
        this.giatri = giatri;
        this.ngaybatdau = ngaybatdau;
        this.ngayketthuc = ngayketthuc;
        this.trangthai = trangthai;
    }

    public String getMactkm() {
        return mactkm;
    }

    public String getTenctkm() {
        return tenctkm;
    }

    public String getLoaikm() {
        return loaikm;
    }

    public double getGiatri() {
        return giatri;
    }

    public LocalDate getNgaybatdau() {
        return ngaybatdau;
    }

    public LocalDate getNgayketthuc() {
        return ngayketthuc;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setMactkm(String mactkm) {
        this.mactkm = mactkm;
    }

    public void setTenctkm(String tenctkm) {
        this.tenctkm = tenctkm;
    }

    public void setLoaikm(String loaikm) {
        this.loaikm = loaikm;
    }

    public void setGiatri(double giatri) {
        this.giatri = giatri;
    }

    public void setNgaybatdau(LocalDate ngaybatdau) {
        this.ngaybatdau = ngaybatdau;
    }

    public void setNgayketthuc(LocalDate ngayketthuc) {
        this.ngayketthuc = ngayketthuc;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }
}