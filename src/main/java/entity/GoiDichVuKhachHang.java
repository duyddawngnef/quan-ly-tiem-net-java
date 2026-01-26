package entity;

import java.time.LocalDateTime;

public class GoiDichVuKhachHang {
    private int maGoiKhachHang;
    private int maKhachHang;
    private int maGoi;
    private int soGioConLai;
    private LocalDateTime ngayMua;

    public GoiDichVuKhachHang() {}

    public GoiDichVuKhachHang(int maGoiKhachHang, int maKhachHang, int maGoi, int soGioConLai, LocalDateTime ngayMua) {
        this.maGoiKhachHang = maGoiKhachHang;
        this.maKhachHang = maKhachHang;
        this.maGoi = maGoi;
        this.soGioConLai = soGioConLai;
        this.ngayMua = ngayMua;
    }

    public int getMaGoiKhachHang() { return maGoiKhachHang; }
    public void setMaGoiKhachHang(int maGoiKhachHang) { this.maGoiKhachHang = maGoiKhachHang; }

    public int getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(int maKhachHang) { this.maKhachHang = maKhachHang; }

    public int getMaGoi() { return maGoi; }
    public void setMaGoi(int maGoi) { this.maGoi = maGoi; }

    public int getSoGioConLai() { return soGioConLai; }
    public void setSoGioConLai(int soGioConLai) { this.soGioConLai = soGioConLai; }

    public LocalDateTime getNgayMua() { return ngayMua; }
    public void setNgayMua(LocalDateTime ngayMua) { this.ngayMua = ngayMua; }
}
