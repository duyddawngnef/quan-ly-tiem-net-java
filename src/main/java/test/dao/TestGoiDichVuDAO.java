package test.dao;

import dao.GoiDichVuDAO;
import entity.GoiDichVu;
import java.util.List;

public class TestGoiDichVuDAO {
    public static void main(String[] args) {
        GoiDichVuDAO dao = new GoiDichVuDAO();

        System.out.println("--- BẮT ĐẦU TEST GOIDICHVUDAO ---");

        // 1. Test generateId (Tạo mã tự động)
        String testMaGoi = dao.generateId();
        try {
            System.out.println("\n--- Test Tạo mã tự động ---");
            System.out.println("=> Mã tự động mới: " + testMaGoi);
        } catch (Exception e) {
            System.err.println("=> Lỗi generateId: " + e.getMessage());
        }

        // 2. Test getAll (Lấy danh sách tất cả gói dịch vụ)
        try {
            System.out.println("\n--- Test Lấy tất cả gói dịch vụ ---");
            List<GoiDichVu> list = dao.getAll();
            System.out.println("=> Tổng số gói dịch vụ: " + list.size());
            list.forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("=> Lỗi getAll: " + e.getMessage());
        }

        // 3. Test insert (Thêm mới gói dịch vụ)
        try {
            System.out.println("\n--- Test Thêm gói dịch vụ ---");
            GoiDichVu goiMoi = new GoiDichVu(
                    testMaGoi,
                    "Gói VIP Test",
                    "THEOGIO",
                    10.0, // Số giờ
                    30, // Số ngày hiệu lực
                    200000, // Giá gốc
                    150000, // Giá gói (khuyến mãi)
                    "Khu VIP", // Áp dụng cho khu
                    "HOATDONG");
            boolean isInserted = dao.insert(goiMoi);
            System.out.println("=> insert(): " + (isInserted ? "Thành công" : "Thất bại"));
        } catch (Exception e) {
            System.err.println("=> Lỗi insert: " + e.getMessage());
        }

        // 4. Test update (Cập nhật gói dịch vụ)
        try {
            System.out.println("\n--- Test Cập nhật gói dịch vụ ---");
            GoiDichVu goiUpdate = new GoiDichVu(
                    testMaGoi,
                    "Gói VIP Test (đã cập nhật)",
                    "THEOGIO",
                    15.0, // Tăng số giờ
                    45, // Tăng số ngày hiệu lực
                    250000, // Giá gốc mới
                    180000, // Giá gói mới
                    "Khu VIP",
                    "HOATDONG");
            boolean isUpdated = dao.update(goiUpdate);
            System.out.println("=> update(): " + (isUpdated ? "Thành công" : "Thất bại"));

            // Kiểm tra lại danh sách sau cập nhật
            List<GoiDichVu> listAfter = dao.getAll();
            for (GoiDichVu g : listAfter) {
                if (g.getMaGoi().equals(testMaGoi)) {
                    System.out.println("=> Sau cập nhật: " + g.getTenGoi()
                            + " | SoGio: " + g.getSoGio()
                            + " | GiaGoi: " + g.getGiaGoi());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("=> Lỗi update: " + e.getMessage());
        }

        // 5. Test delete (Xóa gói dịch vụ)
        try {
            System.out.println("\n--- Test Xóa gói dịch vụ: " + testMaGoi + " ---");
            boolean isDeleted = dao.delete(testMaGoi);
            System.out.println("=> delete(): " + (isDeleted ? "Thành công" : "Thất bại"));

            // Kiểm tra lại danh sách sau xóa
            List<GoiDichVu> listAfter = dao.getAll();
            boolean stillExists = listAfter.stream().anyMatch(g -> g.getMaGoi().equals(testMaGoi));
            System.out.println("=> Kiểm tra sau xóa: " + (stillExists ? "Vẫn còn tồn tại!" : "Đã xóa thành công"));
        } catch (Exception e) {
            System.err.println("=> Lỗi delete: " + e.getMessage());
        }

        System.out.println("\n--- KẾT THÚC TEST GOIDICHVUDAO ---");
    }
}
