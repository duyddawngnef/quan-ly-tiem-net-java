package bus;

import dao.DichVuDAO;
import dao.SuDungDichVuDAO;
import entity.DichVu;
import entity.NhanVien;
import entity.SuDungDichVu;
import untils.SessionManager;

import java.time.LocalDateTime;
import java.util.List;

public class SuDungDichVuBUS {

    private final SuDungDichVuDAO suDungDichVuDAO = new SuDungDichVuDAO();
    private final DichVuDAO dichVuDAO = new DichVuDAO();

    /**
     * Order dịch vụ cho phiên sử dụng
     * 1. Kiểm tra tồn kho đủ không
     * 2. Tạo SuDungDichVu
     * 3. Trừ soLuongTon của DichVu
     * 4. Insert vào DB
     */
    public boolean orderDichVu(String maPhien, String maDV, int soLuong) throws Exception {
        NhanVien currentUser = SessionManager.getCurrentNhanVien();
        if (currentUser == null)
            throw new Exception("Chưa đăng nhập");
        if (!SessionManager.isNhanVien())
            throw new Exception("Không có quyền thực hiện");

        if (soLuong <= 0)
            throw new Exception("Số lượng phải lớn hơn 0");

        // 1. Kiểm tra tồn kho
        DichVu dv = dichVuDAO.getById(maDV);
        if (dv == null)
            throw new Exception("Dịch vụ không tồn tại");
        if (dv.getSoLuongTon() < soLuong)
            throw new Exception("Tồn kho không đủ! Còn lại: " + dv.getSoLuongTon());

        // 2. Tạo SuDungDichVu
        SuDungDichVu sd = new SuDungDichVu();
        sd.setMaSD("SD" + System.currentTimeMillis()); // Tạo mã tạm
        sd.setMaPhien(maPhien);
        sd.setMaDV(maDV);
        sd.setSoLuong(soLuong);
        sd.setDonGia(dv.getDonGia());
        sd.setThanhTien(dv.getDonGia() * soLuong);
        sd.setThoiGian(LocalDateTime.now());

        // 3. Trừ soLuongTon
        boolean truKho = dichVuDAO.updateSoLuong(maDV, -soLuong);
        if (!truKho)
            throw new Exception("Lỗi khi trừ tồn kho");

        // 4. Insert vào DB
        boolean inserted = suDungDichVuDAO.insert(sd);
        if (!inserted) {
            // Rollback: hoàn lại số lượng nếu insert thất bại
            dichVuDAO.updateSoLuong(maDV, soLuong);
            throw new Exception("Lỗi khi tạo order dịch vụ");
        }

        return true;
    }

    /**
     * Hủy order dịch vụ
     * 1. Hoàn lại số lượng vào tồn kho
     * 2. Xóa khỏi DB
     */
    public boolean huyOrder(String maSuDung) throws Exception {
        NhanVien currentUser = SessionManager.getCurrentNhanVien();
        if (currentUser == null)
            throw new Exception("Chưa đăng nhập");
        if (!SessionManager.isNhanVien())
            throw new Exception("Không có quyền thực hiện");

        // Lấy thông tin order
        SuDungDichVu sd = suDungDichVuDAO.getById(maSuDung);
        if (sd == null)
            throw new Exception("Không tìm thấy order dịch vụ");

        // 1. Hoàn lại số lượng vào tồn kho
        boolean hoanKho = dichVuDAO.updateSoLuong(sd.getMaDV(), sd.getSoLuong());
        if (!hoanKho)
            throw new Exception("Lỗi khi hoàn lại tồn kho");

        // 2. Xóa khỏi DB
        boolean deleted = suDungDichVuDAO.delete(maSuDung);
        if (!deleted) {
            // Rollback: trừ lại nếu xóa thất bại
            dichVuDAO.updateSoLuong(sd.getMaDV(), -sd.getSoLuong());
            throw new Exception("Lỗi khi xóa order dịch vụ");
        }

        return true;
    }

    /**
     * Lấy danh sách dịch vụ đã order theo phiên
     */
    public List<SuDungDichVu> getDVDaOrderTheoPhien(String maPhien) throws Exception {
        NhanVien currentUser = SessionManager.getCurrentNhanVien();
        if (currentUser == null)
            throw new Exception("Chưa đăng nhập");
        if (!SessionManager.isNhanVien())
            throw new Exception("Không có quyền thực hiện");

        return suDungDichVuDAO.getByPhien(maPhien);
    }
}
