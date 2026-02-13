package bus;

import dao.KhuMayDAO;
import dao.MayTinhDAO;
import entity.KhuMay;
import entity.NhanVien;
import untils.SessionManager;

import java.util.List;

public class KhuMayBUS
{
    final KhuMayDAO khumayDAO = new KhuMayDAO();

    public List<KhuMay> getAllKhuMay() throws Exception
    {
        NhanVien currentUser = SessionManager.getCurrentNhanVien();
        if (currentUser == null)
            throw new Exception("Chưa đang nhập");
        if (!SessionManager.isNhanVien())
            throw new Exception("Không có quyền thực hiện");

        return khumayDAO.getAll();
    }

    public boolean themKhuMay(KhuMay km) throws Exception
    {
        NhanVien currentUser = SessionManager.getCurrentNhanVien();
        if (currentUser == null)
            throw new Exception("Chưa đang nhập");
        if (!SessionManager.isQuanLy())
            throw new Exception("Không có quyền thực hiện");

        return khumayDAO.insert(km);
    }

    public boolean suaKhuMay(KhuMay km) throws Exception
    {
        NhanVien currentUser = SessionManager.getCurrentNhanVien();
        if (currentUser == null)
            throw new Exception("Chưa đang nhập");
        if (!SessionManager.isQuanLy())
            throw new Exception("Không có quyền thực hiện");

        return khumayDAO.update(km);
    }

    public boolean xoaKhuMay(String MaKhu) throws Exception
    {
        NhanVien currentUser = SessionManager.getCurrentNhanVien();
        if (currentUser == null)
            throw new Exception("Chưa đang nhập");
        if (!SessionManager.isQuanLy())
            throw new Exception("Không có quyền thực hiện");

        if (khumayDAO.hasActiveComputer(MaKhu))
            throw new RuntimeException("Không thể xóa khu máy có máy đang sử dụng !");
        else
            khumayDAO.updateMaKhuNull(MaKhu);

        khumayDAO.delete(MaKhu);
        return true;
    }

    public int demSoMayTrongKhu(String MaKhu) throws Exception
    {
        NhanVien currentUser = SessionManager.getCurrentNhanVien();
        if (currentUser == null)
            throw new Exception("Chưa đang nhập");
        if (!SessionManager.isNhanVien())
            throw new Exception("Không có quyền thực hiện");

        return khumayDAO.countMayTinhByKhu(MaKhu);
    }
}