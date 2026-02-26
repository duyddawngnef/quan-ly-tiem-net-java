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

        KhachHang kh = khachHangDAO.login(tenDN, matKhau);

        return kh;
    }

    public boolean dangKy(KhachHang kh) {
        if (kh == null)
            return false;
        String hoTen = (kh.getHo() != null ? kh.getHo() : "") + " " + (kh.getTen() != null ? kh.getTen() : "");
        if (hoTen.trim().isEmpty())
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
        return sdt != null && sdt.matches("^0\\d{9}$");
    }

    private String hashPassword(String password) {
        return password;
    }
}