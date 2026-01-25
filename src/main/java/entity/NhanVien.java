package entity;

import dao.NhanVienDAO;

public class NhanVien{
    String manv;
    String ho;
    String ten;
    String chucvu;
    String tendangnhap;
    String matkhau;
    String trangthai;


    public NhanVien(){

    }
    public NhanVien(String manv, String ho, String ten, String chucvu, String tendangnhap, String matkhau, String trangthai) {
        this.manv = manv;
        this.ho = ho;
        this.ten = ten;
        this.chucvu = chucvu;
        this.tendangnhap = tendangnhap;
        this.matkhau = matkhau;
        this.trangthai = trangthai;
    }

    public String getManv() {
        return manv;
    }

    public void setManv(String manv) {
        this.manv = manv;
    }

    public String getHo() {
        return ho;
    }

    public void setHo(String ho) {
        this.ho = ho;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getChucvu() {
        return chucvu;
    }

    public void setChucvu(String chucvu) {
        this.chucvu = chucvu;
    }

    public String getTendangnhap() {
        return tendangnhap;
    }

    public void setTendangnhap(String tendangnhap) {
        this.tendangnhap = tendangnhap;
    }

    public String getMatkhau() {
        return matkhau;
    }

    public void setMatkhau(String matkhau) {
        this.matkhau = matkhau;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }
    public boolean isDangLamViec() {
        return "DANGLAMVIEC".equals(trangthai);
    }

    public boolean isNghiViec() {
        return "NGHIVIEC".equals(trangthai);
    }


    public boolean isQuanLy() {
        return "QUANLY".equals(chucvu);
    }

    public boolean isNhanVien() {
        return "NHANVIEN".equals(chucvu);
    }

    public boolean isThuNgan() {
        return "THUNGAN".equals(chucvu);
    }

}