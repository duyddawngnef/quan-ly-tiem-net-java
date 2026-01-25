package entity;

public class NhaCungCap {
    private String mancc;
    private String tenncc;
    private String diachi;
    private String sodienthoai;
    private String email;

    public NhaCungCap() {}

    public NhaCungCap(String mancc, String tenncc, String diachi, String sodienthoai, String email) {
        this.mancc = mancc;
        this.tenncc =  tenncc;
        this.diachi = diachi;
        this.sodienthoai = sodienthoai;
        this.email = email;
    }

    public String getMancc() {
        return mancc;
    }

    public String getTenncc() {
        return tenncc;
    }

    public String getDiachi() {
        return diachi;
    }

    public String getSodienthoai() {
        return sodienthoai;
    }

    public String getEmail() {
        return email;
    }

    public void setMancc(String mancc) {
        this.mancc = mancc;
    }

    public void setTenncc(String tenncc) {
        this.tenncc = tenncc;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public void setSodienthoai(String sodienthoai) {
        this.sodienthoai = sodienthoai;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}