package bus;

import dao.MayTinhDAO;
import entity.KhuMay;
import entity.MayTinh;
import entity.NhanVien;
import untils.SessionManager;

import java.util.List;

public class MayTinhBUS
{
    final MayTinhDAO maytinhDAO = new MayTinhDAO();

    public List<MayTinh> getAllMayTinh() throws Exception
    {
        NhanVien currentUser = SessionManager.getCurrentNhanVien();
        if (currentUser == null)
            throw new Exception("Chưa đang nhập");
        if (!SessionManager.isNhanVien())
            throw new Exception("Không có quyền thực hiện");

        return maytinhDAO.getAll();
    }

    public List<MayTinh> getMayTrong() throws Exception
    {
        NhanVien currentUser = SessionManager.getCurrentNhanVien();
        if (currentUser == null)
            throw new Exception("Chưa đang nhập");
        if (!SessionManager.isNhanVien())
            throw new Exception("Không có quyền thực hiện");

        return maytinhDAO.getMayTrong();
    }

    public List<MayTinh> getMayTheoKhu(String MaKhu) throws Exception
    {
        NhanVien currentUser = SessionManager.getCurrentNhanVien();
        if (currentUser == null)
            throw new Exception("Chưa đang nhập");
        if (!SessionManager.isNhanVien())
            throw new Exception("Không có quyền thực hiện");

        return maytinhDAO.getByKhu(MaKhu);
    }

    public boolean themMayTinh(MayTinh mt) throws Exception
    {
        NhanVien currentUser = SessionManager.getCurrentNhanVien();
        if (currentUser == null)
            throw new Exception("Chưa đang nhập");
        if (!SessionManager.isQuanLy())
            throw new Exception("Không có quyền thực hiện");

        return maytinhDAO.insert(mt);
    }

    public boolean suaMayTinh(MayTinh mt) throws Exception
    {
        NhanVien currentUser = SessionManager.getCurrentNhanVien();
        if (currentUser == null)
            throw new Exception("Chưa đang nhập");
        if (!SessionManager.isQuanLy())
            throw new Exception("Không có quyền thực hiện");

        return maytinhDAO.update(mt);
    }

    public boolean xoaMayTinh(String MaMay) throws Exception
    {
        NhanVien currentUser = SessionManager.getCurrentNhanVien();
        if (currentUser == null)
            throw new Exception("Chưa đang nhập");
        if (!SessionManager.isQuanLy())
            throw new Exception("Không có quyền thực hiện");

        maytinhDAO.delete(MaMay);
        return true;
    }

    public boolean chuyenTrangThai(String MaMay, String ttMoi) throws Exception
    {
        NhanVien currentUser = SessionManager.getCurrentNhanVien();
        if (currentUser == null)
            throw new Exception("Chưa đang nhập");
        if (!SessionManager.isNhanVien())
            throw new Exception("Không có quyền thực hiện");

        MayTinh mt = maytinhDAO.getByID(MaMay);
        String ttHienTai = mt.getTrangthai();

        if (ttHienTai.equals(ttMoi))
            return true;

        if (ttMoi.equals("DANGDUNG")) {
            throw new RuntimeException("Chỉ được chuyển sang DANGDUNG thông qua chức năng 'Mở phiên chơi'!");
        }

        if (ttHienTai.equals("DANGDUNG")) {
            throw new RuntimeException("Máy đang có khách sử dụng, chỉ được chuyển trạng thái thông qua 'Kết thúc phiên'!");
        }

        if ((ttHienTai.equals("TRONG") && ttMoi.equals("BAOTRI")) ||
                (ttHienTai.equals("BAOTRI") && ttMoi.equals("TRONG"))) {

            // Gọi hàm updateTrangThai từ DAO đã cung cấp
            return maytinhDAO.updateTrangThai(MaMay, ttHienTai, ttMoi);
        }

        return true;
    }
}