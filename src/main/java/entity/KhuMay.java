package entity;

public  class KhuMay
{
    String makhu;
    String tenkhu;
    Double giacoso;
    int somaytoida;
    String trangthai;

    public KhuMay() {}

    public  KhuMay(String makhu, String tenkhu, Double giacoso, int somaytoida, String trangthai)
    {
        this.makhu = makhu;
        this.tenkhu = tenkhu;
        this.giacoso = giacoso;
        this.somaytoida = somaytoida;
        this.trangthai = trangthai;
    }

    public String getMaKhu() {
        return makhu;
    }

    public void setMaKhu(String makhu) {
        this.makhu = makhu;
    }

    public String getTenKhu() {
        return tenkhu;
    }

    public void setTenKhu(String tenkhu) {
        this.tenkhu = tenkhu;
    }

    public Double getGiacoso() {
        return giacoso;
    }

    public void setGiacoso(Double giacoso) {
        this.giacoso = giacoso;
    }

    public int getSomaytoida() {
        return somaytoida;
    }

    public void setSomaytoida(int somaytoida) {
        this.somaytoida = somaytoida;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }
}