package quanlytiemnet.java.entity;

import java.time.LocalDate;
import java.util.List;

public class HoaDon {

    private int maHD;
    private int maPhien;
    private int maNV;
    private LocalDate ngayLap;
    private double tongTien;
    private String phuongThucTT;
    private String ghiChu;
    private List<ChiTietHoaDon> chiTietHoaDons;

    public int getMaHD() {
        return maHD;
    }

    public void setMaHD(int maHD) {
        this.maHD = maHD;
    }
    
    public int getMaPhien() {
        return maPhien;
    }
    
    public void setMaPhien(int maPhien) {
        this.maPhien = maPhien;
    }
    
    public int getMaNV() {
        return maNV;
    }
    
    public void setMaNV(int maNV) {
        this.maNV = maNV;
    }

    public LocalDate getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDate ngayLap) {
        this.ngayLap = ngayLap;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }
    
    public String getPhuongThucTT() {
        return phuongThucTT;
    }
    
    public void setPhuongThucTT(String phuongThucTT) {
        this.phuongThucTT = phuongThucTT;
    }
    
    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public List<ChiTietHoaDon> getChiTietHoaDons() {
        return chiTietHoaDons;
    }

    public void setChiTietHoaDons(List<ChiTietHoaDon> chiTietHoaDons) {
        this.chiTietHoaDons = chiTietHoaDons;
    }
}

