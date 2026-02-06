package model;

public class May {

    private int maMay;
    private String tenMay;
    private String trangThai;

    public May(int maMay, String tenMay, String trangThai) {
        this.maMay = maMay;
        this.tenMay = tenMay;
        this.trangThai = trangThai;
    }

    public int getMaMay() {
        return maMay;
    }

    public String getTenMay() {
        return tenMay;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
