package test.dao;

import dao.SuDungDichVuDAO;
import entity.SuDungDichVu;
import java.time.LocalDateTime;
import java.util.List;

public class TestSuDungDichVuDAO {
    public static void main(String[] args) {
        SuDungDichVuDAO dao = new SuDungDichVuDAO();

        System.out.println("--- BẮT ĐẦU TEST SUDUNGDICHVUDAO ---");

        // 1. Test generateId (Tạo mã tự động)
        String testMaSD = null;
        try {
            System.out.println("\n--- Test Tạo mã tự động ---");
            testMaSD = dao.generateId();
            System.out.println("=> Mã tự động mới: " + testMaSD);
        } catch (Exception e) {
            System.err.println("=> Lỗi generateId: " + e.getMessage());
        }

        // 2. Test insert (Thêm mới sử dụng dịch vụ)
        try {
            System.out.println("\n--- Test Thêm sử dụng dịch vụ ---");
            SuDungDichVu sd = new SuDungDichVu(
                    testMaSD,
                    "PS001", // Mã phiên - thay bằng mã phiên có thật trong DB
                    "DV001", // Mã dịch vụ - thay bằng mã DV có thật trong DB
                    2, // Số lượng
                    15000, // Đơn giá
                    30000, // Thành tiền
                    LocalDateTime.now());
            boolean isInserted = dao.insert(sd);
            System.out.println("=> insert(): " + (isInserted ? "Thành công" : "Thất bại"));
        } catch (Exception e) {
            System.err.println("=> Lỗi insert: " + e.getMessage());
        }

        // 3. Test getById (Lấy theo mã)
        try {
            System.out.println("\n--- Test Lấy theo mã: " + testMaSD + " ---");
            SuDungDichVu sd = dao.getById(testMaSD);
            if (sd != null) {
                System.out.println("=> Thành công: " + sd);
                System.out.println("   MaPhien: " + sd.getMaPhien()
                        + " | MaDV: " + sd.getMaDV()
                        + " | SoLuong: " + sd.getSoLuong()
                        + " | ThanhTien: " + sd.getThanhTien());
            } else {
                System.out.println("=> Không tìm thấy với mã: " + testMaSD);
            }
        } catch (Exception e) {
            System.err.println("=> Lỗi getById: " + e.getMessage());
        }

        // 4. Test getByPhien (Lấy danh sách theo phiên)
        try {
            System.out.println("\n--- Test Lấy danh sách theo phiên PS001 ---");
            List<SuDungDichVu> list = dao.getByPhien("PS001");
            System.out.println("=> Tổng số: " + list.size());
            list.forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("=> Lỗi getByPhien: " + e.getMessage());
        }

        // 5. Test delete (Xóa dịch vụ đã gọi)
        try {
            System.out.println("\n--- Test Xóa sử dụng dịch vụ: " + testMaSD + " ---");
            boolean isDeleted = dao.delete(testMaSD);
            System.out.println("=> delete(): " + (isDeleted ? "Thành công" : "Thất bại"));

            // Kiểm tra lại sau khi xóa
            SuDungDichVu sdCheck = dao.getById(testMaSD);
            System.out.println("=> Kiểm tra sau xóa: " + (sdCheck == null ? "Đã xóa thành công" : "Vẫn còn tồn tại!"));
        } catch (Exception e) {
            System.err.println("=> Lỗi delete: " + e.getMessage());
        }

        System.out.println("\n--- KẾT THÚC TEST SUDUNGDICHVUDAO ---");
    }
}
