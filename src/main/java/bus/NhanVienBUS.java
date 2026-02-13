package bus;

import dao.NhanVienDAO;
import entity.NhanVien;
import java.util.PasswordUtils;

import java.util.List;

public class NhanVienBUS {

    private NhanVienDAO nhanVienDAO = new NhanVienDAO();

    public NhanVien dangNhapNhanVien(String tenDN, String matKhau) {
        return nhanVienDAO.login(tenDN, matKhau);
    }

    public List<NhanVien> getDanhSachSinhVien() {
        return nhanVienDAO.getAll();
    }

    public boolean suaNhanVien(NhanVien nv) {
        return nhanVienDAO.update(nv);
    }

    public boolean xoaNhanVien(int maNV) {
        return nhanVienDAO.delete(maNV);
    }

    public boolean resetMatKhau(int maNV) {
        return nhanVienDAO.resetPassword(maNV);
    }
}