package bus;

import dao.DichVuDAO;
import entity.DichVu;
import entity.NhanVien;
import untils.SessionManager;

import java.util.List;

public class DichVuBUS {

    private final DichVuDAO dichVuDAO = new DichVuDAO();

    // Lấy danh sách dịch vụ
    public List<DichVu> getDanhSachDV() throws Exception {
        NhanVien currentUser = SessionManager.getCurrentNhanVien();
        if (currentUser == null)
            throw new Exception("Chưa đăng nhập");
        if (!SessionManager.isNhanVien())
            throw new Exception("Không có quyền thực hiện");

        return dichVuDAO.getAll();
    }

    // Thêm dịch vụ mới + validate
    public boolean themDichVu(DichVu dv) throws Exception {
        NhanVien currentUser = SessionManager.getCurrentNhanVien();
        if (currentUser == null)
            throw new Exception("Chưa đăng nhập");
        if (!SessionManager.isQuanLy())
            throw new Exception("Không có quyền thực hiện");

        // Validate
        if (dv.getTenDV() == null || dv.getTenDV().trim().isEmpty())
            throw new Exception("Tên dịch vụ không được để trống");
        if (dv.getDonGia() <= 0)
            throw new Exception("Đơn giá phải lớn hơn 0");
        if (dv.getSoLuongTon() < 0)
            throw new Exception("Số lượng tồn không được âm");
        if (dv.getLoaiDV() == null || dv.getLoaiDV().trim().isEmpty())
            throw new Exception("Loại dịch vụ không được để trống");
        if (dv.getDonViTinh() == null || dv.getDonViTinh().trim().isEmpty())
            throw new Exception("Đơn vị tính không được để trống");

        // Tạo mã tự động
        dv.setMaDV(dichVuDAO.generateId());

        return dichVuDAO.insert(dv);
    }

    // Sửa dịch vụ + validate
    public boolean suaDichVu(DichVu dv) throws Exception {
        NhanVien currentUser = SessionManager.getCurrentNhanVien();
        if (currentUser == null)
            throw new Exception("Chưa đăng nhập");
        if (!SessionManager.isQuanLy())
            throw new Exception("Không có quyền thực hiện");

        // Validate
        if (dv.getTenDV() == null || dv.getTenDV().trim().isEmpty())
            throw new Exception("Tên dịch vụ không được để trống");
        if (dv.getDonGia() <= 0)
            throw new Exception("Đơn giá phải lớn hơn 0");
        if (dv.getSoLuongTon() < 0)
            throw new Exception("Số lượng tồn không được âm");

        return dichVuDAO.update(dv);
    }

    // Xóa dịch vụ
    public boolean xoaDichVu(String maDV) throws Exception {
        NhanVien currentUser = SessionManager.getCurrentNhanVien();
        if (currentUser == null)
            throw new Exception("Chưa đăng nhập");
        if (!SessionManager.isQuanLy())
            throw new Exception("Không có quyền thực hiện");

        return dichVuDAO.delete(maDV);
    }

    // Kiểm tra tồn kho đủ không
    public boolean kiemTraTonKho(String maDV, int soLuong) throws Exception {
        NhanVien currentUser = SessionManager.getCurrentNhanVien();
        if (currentUser == null)
            throw new Exception("Chưa đăng nhập");
        if (!SessionManager.isNhanVien())
            throw new Exception("Không có quyền thực hiện");

        DichVu dv = dichVuDAO.getById(maDV);
        if (dv == null)
            throw new Exception("Dịch vụ không tồn tại");

        return dv.getSoLuongTon() >= soLuong;
    }
}
