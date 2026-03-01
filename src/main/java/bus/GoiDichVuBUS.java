package bus;

import dao.GoiDichVuDAO;
import entity.GoiDichVu;
import entity.NhanVien;
import untils.SessionManager;

import java.util.List;

public class GoiDichVuBUS {

    private final GoiDichVuDAO goiDichVuDAO = new GoiDichVuDAO();

    // Lấy danh sách gói dịch vụ
    public List<GoiDichVu> getDanhSachGoiDV() throws Exception {
        NhanVien currentUser = SessionManager.getCurrentNhanVien();
        if (currentUser == null)
            throw new Exception("Chưa đăng nhập");
        if (!SessionManager.isNhanVien())
            throw new Exception("Không có quyền thực hiện");

        return goiDichVuDAO.getAll();
    }

    // Thêm gói dịch vụ + validate
    public boolean themGoiDichVu(GoiDichVu g) throws Exception {
        NhanVien currentUser = SessionManager.getCurrentNhanVien();
        if (currentUser == null)
            throw new Exception("Chưa đăng nhập");
        if (!SessionManager.isQuanLy())
            throw new Exception("Không có quyền thực hiện");

        // Validate
        validateGoiDichVu(g);

        // Tạo mã tự động
        g.setMaGoi(goiDichVuDAO.generateId());

        return goiDichVuDAO.insert(g);
    }

    // Sửa gói dịch vụ + validate
    public boolean suaGoiDichVu(GoiDichVu g) throws Exception {
        NhanVien currentUser = SessionManager.getCurrentNhanVien();
        if (currentUser == null)
            throw new Exception("Chưa đăng nhập");
        if (!SessionManager.isQuanLy())
            throw new Exception("Không có quyền thực hiện");

        // Validate
        validateGoiDichVu(g);

        return goiDichVuDAO.update(g);
    }

    // Xóa gói dịch vụ
    public boolean xoaGoiDichVu(String maGoi) throws Exception {
        NhanVien currentUser = SessionManager.getCurrentNhanVien();
        if (currentUser == null)
            throw new Exception("Chưa đăng nhập");
        if (!SessionManager.isQuanLy())
            throw new Exception("Không có quyền thực hiện");

        return goiDichVuDAO.delete(maGoi);
    }

    // Validate gói dịch vụ
    private void validateGoiDichVu(GoiDichVu g) throws Exception {
        if (g.getTenGoi() == null || g.getTenGoi().trim().isEmpty())
            throw new Exception("Tên gói không được để trống");
        if (g.getLoaiGoi() == null || g.getLoaiGoi().trim().isEmpty())
            throw new Exception("Loại gói không được để trống");
        if (g.getSoGio() <= 0)
            throw new Exception("Số giờ phải lớn hơn 0");
        if (g.getSoNgayHieuLuc() <= 0)
            throw new Exception("Số ngày hiệu lực phải lớn hơn 0");
        if (g.getGiaGoc() <= 0)
            throw new Exception("Giá gốc phải lớn hơn 0");
        if (g.getGiaGoi() <= 0)
            throw new Exception("Giá gói phải lớn hơn 0");
        if (g.getGiaGoi() > g.getGiaGoc())
            throw new Exception("Giá gói không được lớn hơn giá gốc");
    }
}
