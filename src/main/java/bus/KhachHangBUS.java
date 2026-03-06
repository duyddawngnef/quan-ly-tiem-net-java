package bus;

import dao.KhachHangDAO;
import entity.KhachHang;

public class KhachHangBUS {
    private KhachHangDAO khachHangDAO = new KhachHangDAO();

    public KhachHang dangNhap(String tenDN, String matKhau) {
        if (tenDN == null || tenDN.isEmpty())
            return null;
        if (matKhau == null || matKhau.isEmpty())
            return null;

        return khachHangDAO.login(tenDN, matKhau);
    }

    public boolean dangKy(KhachHang kh) {
        if (kh == null)
            return false;
        if (kh.getHo() == null || kh.getHo().isEmpty())
            return false;
        if (kh.getTen() == null || kh.getTen().isEmpty())
            return false;
        if (!isValidSDT(kh.getSodienthoai()))
            return false;

        if (khachHangDAO.isTenDangNhapExists(kh.getTendangnhap())) {
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