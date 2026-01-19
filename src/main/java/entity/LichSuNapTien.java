package entity;

import java.time.LocalDate;

public class LichSuNapTien {
    private String manap;
    private String makh;
    private String manv;
    private String mactkm;
    private double sotiennap;
    private double tienkhuyenmai;
    private double tongtien;
    private LocalDate ngaynap;

    public LichSuNapTien() {}

    public LichSuNapTien(String manap, String makh, String manv, String mactkm, double sotiennap, double tienkhuyenmai, double tongtien, LocalDate ngaynap) {
        this.manap = manap;
        this.makh = makh;
        this.manv = manv;
        this.mactkm = mactkm;
        this.sotiennap = sotiennap;
        this.tienkhuyenmai = tienkhuyenmai;
        this.tongtien = tongtien;
        this.ngaynap = ngaynap;
    }

    public String getManap(){
        return manap;
    }

    public String getMakh() {
        return makh;
    }

    public String getManv() {
        return manv;
    }

    public String getMactkm() {
        return mactkm;
    }

    public double getSotiennap() {
        return sotiennap;
    }

    public double getTienkhuyenmai() {
        return tienkhuyenmai;
    }

    public double getTongtien() {
        return tongtien;
    }

    public LocalDate getNgaynap() {
        return ngaynap;
    }

    public void setManap(String manap) {
        this.manap = manap;
    }

    public void setMakh(String makh) {
        this.makh = makh;
    }

    public void setManv(String manv) {
        this.manv = manv;
    }

    public void setMactkm(String mactkm) {
        this.mactkm = mactkm;
    }

    public void setSotiennap(double sotiennap) {
        this.sotiennap = sotiennap;
    }

    public void setTienkhuyenmai(double tienkhuyenmai) {
        this.tienkhuyenmai = tienkhuyenmai;
    }

    public void setTongtien(double tongtien) {
        this.tongtien = tongtien;
    }

    public void setNgaynap(LocalDate ngaynap) {
        this.ngaynap = ngaynap;
    }
}