package entity;

import java.time.LocalDateTime;

public class GoiDichVuKhachHang {

    private String maGoiKH;
    private String maKH;
    private String maGoi;
    private String maNV;
    private double soGioBanDau;
    private double soGioConLai;
    private LocalDateTime ngayMua;
    private LocalDateTime ngayHetHan;
    private double giaMua;
    private String trangThai;

    public GoiDichVuKhachHang() {}

    public GoiDichVuKhachHang(String maGoiKH, String maKH, String maGoi,
                              String maNV, double soGioBanDau,
                              double soGioConLai, LocalDateTime ngayMua,
                              LocalDateTime ngayHetHan, double giaMua,
                              String trangThai) {
        this.maGoiKH = maGoiKH;
        this.maKH = maKH;
        this.maGoi = maGoi;
        this.maNV = maNV;
        this.soGioBanDau = soGioBanDau;
        this.soGioConLai = soGioConLai;
        this.ngayMua = ngayMua;
        this.ngayHetHan = ngayHetHan;
        this.giaMua = giaMua;
        this.trangThai = trangThai;
    }

    public String getMaGoiKH() { return maGoiKH; }
    public void setMaGoiKH(String maGoiKH) { this.maGoiKH = maGoiKH; }

    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }

    public String getMaGoi() { return maGoi; }
    public void setMaGoi(String maGoi) { this.maGoi = maGoi; }

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public double getSoGioBanDau() { return soGioBanDau; }
    public void setSoGioBanDau(double soGioBanDau) { this.soGioBanDau = soGioBanDau; }

    public double getSoGioConLai() { return soGioConLai; }
    public void setSoGioConLai(double soGioConLai) { this.soGioConLai = soGioConLai; }

    public LocalDateTime getNgayMua() { return ngayMua; }
    public void setNgayMua(LocalDateTime ngayMua) { this.ngayMua = ngayMua; }

    public LocalDateTime getNgayHetHan() { return ngayHetHan; }
    public void setNgayHetHan(LocalDateTime ngayHetHan) { this.ngayHetHan = ngayHetHan; }

    public double getGiaMua() { return giaMua; }
    public void setGiaMua(double giaMua) { this.giaMua = giaMua; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    @Override
    public String toString() {
        return maGoiKH + " | " + maKH + " | " + soGioConLai;
    }
}
