package entity;

public class GoiDichVu {

    private String maGoi;
    private String tenGoi;
    private String loaiGoi;
    private double soGio;
    private int soNgayHieuLuc;
    private double giaGoc;
    private double giaGoi;
    private String apDungChoKhu;
    private String trangThai;

    public GoiDichVu() {}

    public GoiDichVu(String maGoi, String tenGoi, String loaiGoi,
                     double soGio, int soNgayHieuLuc,
                     double giaGoc, double giaGoi,
                     String apDungChoKhu, String trangThai) {
        this.maGoi = maGoi;
        this.tenGoi = tenGoi;
        this.loaiGoi = loaiGoi;
        this.soGio = soGio;
        this.soNgayHieuLuc = soNgayHieuLuc;
        this.giaGoc = giaGoc;
        this.giaGoi = giaGoi;
        this.apDungChoKhu = apDungChoKhu;
        this.trangThai = trangThai;
    }

    public String getMaGoi() { return maGoi; }
    public void setMaGoi(String maGoi) { this.maGoi = maGoi; }

    public String getTenGoi() { return tenGoi; }
    public void setTenGoi(String tenGoi) { this.tenGoi = tenGoi; }

    public String getLoaiGoi() { return loaiGoi; }
    public void setLoaiGoi(String loaiGoi) { this.loaiGoi = loaiGoi; }

    public double getSoGio() { return soGio; }
    public void setSoGio(double soGio) { this.soGio = soGio; }

    public int getSoNgayHieuLuc() { return soNgayHieuLuc; }
    public void setSoNgayHieuLuc(int soNgayHieuLuc) { this.soNgayHieuLuc = soNgayHieuLuc; }

    public double getGiaGoc() { return giaGoc; }
    public void setGiaGoc(double giaGoc) { this.giaGoc = giaGoc; }

    public double getGiaGoi() { return giaGoi; }
    public void setGiaGoi(double giaGoi) { this.giaGoi = giaGoi; }

    public String getApDungChoKhu() { return apDungChoKhu; }
    public void setApDungChoKhu(String apDungChoKhu) { this.apDungChoKhu = apDungChoKhu; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    @Override
    public String toString() {
        return maGoi + " | " + tenGoi + " | " + giaGoi + " | " + soGio + "h";
    }
}
