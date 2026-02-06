package bus;

import dao.KhachHangDAO;
import entity.KhachHang;
import java.util.PasswordUtils;
import java.util.SessionManager;

public class KhachHangBUS {
    private KhachHangDAO khachHangDAO = new KhachHangDAO();

    public KhachHang dangNhap(String tenDN, String matKhau) {
        if (tenDN == null || tenDN.isEmpty()) return null;
        if (matKhau == null || matKhau.isEmpty()) return null;

        KhachHang kh = khachHangDAO.login(tenDN, matKhau);

        if (kh != null) {
            SessionManager.setKhachHang(kh);
        }

        return kh;
    }

    public boolean dangKy(KhachHang kh) {
        if (kh == null) return false;
        if (kh.getHoTen() == null || kh.getHoTen().isEmpty()) return false;
        if (!isValidSDT(kh.getSdt())) return false;

        if (khachHangDAO.isTenDangNhapTonTai(kh.getTendangnhap())) {
            return false;
        }

        kh.setMatkhau(hashPassword(kh.getMatkhau()));
        return khachHangDAO.insert(kh);
    }
        private boolean isValidSDT(String sdt) {
            return sdt != null && sdt.matches("^0\\d{10}$");
    }

        private String hashPassword(String password) {
            return password;
        }

}