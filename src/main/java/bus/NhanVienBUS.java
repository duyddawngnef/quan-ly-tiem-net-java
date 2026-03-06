package bus;

import dao.NhanVienDAO;
import entity.NhanVien;
import untils.SessionManager;

import java.util.List;

public class NhanVienBUS {

    private NhanVienDAO nhanVienDAO = new NhanVienDAO();

    public NhanVien dangNhapNhanVien(String tenDN, String matKhau) {
        return nhanVienDAO.login(tenDN, matKhau);
    }

    public List<NhanVien> getDanhSachNhanVien() {
        return nhanVienDAO.getAll();
    }

    public boolean suaNhanVien(NhanVien nv) {
        NhanVien nguoiThucHien = SessionManager.getCurrentNhanVien();
        return nhanVienDAO.update(nv, nguoiThucHien);
    }

    public boolean xoaNhanVien(String maNV) {
        NhanVien nguoiThucHien = SessionManager.getCurrentNhanVien();
        return nhanVienDAO.delete(maNV, nguoiThucHien);
    }
}