package entity;

import java.math.BigDecimal;

public class MayTinh
{
    String mamay;
    String tenmay;
    String makhu;
    String cauhinh;
    double giamoigio;
    String trangthai;

    public MayTinh() {}

    public MayTinh(String mamay, String tenmay, String makhu, String cauhinh, double giamoigio, String trangthai)
    {
        this.mamay = mamay;
        this.tenmay = tenmay;
        this.makhu = makhu;
        this.cauhinh = cauhinh;
        this.giamoigio = giamoigio;
        this.trangthai = trangthai;
    }

    public String getMamay() {
        return mamay;
    }

    public void setMamay(String mamay) {
        this.mamay = mamay;
    }

    public String getTenmay() {
        return tenmay;
    }

    public void setTenmay(String tenmay) {
        this.tenmay = tenmay;
    }

    public String geMakhu() {
        return makhu;
    }

    public void setMakhu(String makhu) {
        this.makhu = makhu;
    }

    public String getCauhinh() {
        return cauhinh;
    }

    public void setCauhinh(String cauhinh) {
        this.cauhinh = cauhinh;
    }

    public double getGiamoigio() {
        return giamoigio;
    }

    public void setGiamoigio(double giamoigio) {
        this.giamoigio = giamoigio;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void settrangThai(String trangthai) {
        this.trangthai = trangthai;
    }

    public String getTrangThaiText()
    {
        switch (trangthai)
        {
            case "TRONG":
                return "Trống";
            case "DANGDUNG":
                return "Đang dùng";
            case "BAOTRI":
                return "Bảo trì";
            default:
                return "Chưa xác định";
        }
    }
}