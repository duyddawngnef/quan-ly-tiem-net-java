package test.dao;

import dao.DichVuDAO;
import entity.DichVu;
import java.util.List;

public class TestDichVuDAO {
    public static void main(String[] args) {
        DichVuDAO dao = new DichVuDAO();

        System.out.println("--- BẮT ĐẦU TEST DICHVUDAO ---");

        // 1. Test generateId (Tạo mã tự động)
        try {
            System.out.println("\n--- Test Tạo mã tự động ---");
            String newId = dao.generateId();
            System.out.println("=> Mã tự động mới: " + newId);
        } catch (Exception e) {
            System.err.println("=> Lỗi generateId: " + e.getMessage());
        }

        // 2. Test getAll (Lấy danh sách tất cả dịch vụ)
        try {
            System.out.println("\n--- Test Lấy tất cả dịch vụ ---");
            List<DichVu> list = dao.getAll();
            System.out.println("=> Tổng số dịch vụ: " + list.size());
            list.forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("=> Lỗi getAll: " + e.getMessage());
        }

        // 3. Test insert (Thêm mới dịch vụ)
        String testMaDV = dao.generateId();
        try {
            System.out.println("\n--- Test Thêm dịch vụ ---");
            DichVu dvMoi = new DichVu(
                    testMaDV,
                    "Nước ngọt test",
                    "DOUONG",
                    15000,
                    "Lon",
                    100,
                    "CONHANG");
            boolean isInserted = dao.insert(dvMoi);
            System.out.println("=> insert(): " + (isInserted ? "Thành công" : "Thất bại"));
        } catch (Exception e) {
            System.err.println("=> Lỗi insert: " + e.getMessage());
        }

        // 4. Test getById (Lấy theo mã)
        try {
            System.out.println("\n--- Test Lấy dịch vụ theo mã: " + testMaDV + " ---");
            DichVu dv = dao.getById(testMaDV);
            if (dv != null) {
                System.out.println("=> Thành công: " + dv);
            } else {
                System.out.println("=> Không tìm thấy dịch vụ với mã: " + testMaDV);
            }
        } catch (Exception e) {
            System.err.println("=> Lỗi getById: " + e.getMessage());
        }

        // 5. Test update (Cập nhật dịch vụ)
        try {
            System.out.println("\n--- Test Cập nhật dịch vụ ---");
            DichVu dvUpdate = new DichVu(
                    testMaDV,
                    "Nước ngọt test (đã cập nhật)",
                    "DOUONG",
                    18000,
                    "Lon",
                    80,
                    "CONHANG");
            boolean isUpdated = dao.update(dvUpdate);
            System.out.println("=> update(): " + (isUpdated ? "Thành công" : "Thất bại"));

            // Kiểm tra lại sau khi cập nhật
            DichVu dvCheck = dao.getById(testMaDV);
            if (dvCheck != null) {
                System.out.println("=> Sau cập nhật: " + dvCheck.getTenDV() + " | Giá: " + dvCheck.getDonGia());
            }
        } catch (Exception e) {
            System.err.println("=> Lỗi update: " + e.getMessage());
        }

        // 6. Test updateSoLuong (Cập nhật số lượng tồn)
        try {
            System.out.println("\n--- Test Cập nhật số lượng tồn ---");
            // Giảm 10
            boolean isUpdatedSL = dao.updateSoLuong(testMaDV, -10);
            System.out.println("=> updateSoLuong(-10): " + (isUpdatedSL ? "Thành công" : "Thất bại"));

            DichVu dvCheck = dao.getById(testMaDV);
            if (dvCheck != null) {
                System.out.println("=> Số lượng tồn sau khi giảm 10: " + dvCheck.getSoLuongTon());
            }
        } catch (Exception e) {
            System.err.println("=> Lỗi updateSoLuong: " + e.getMessage());
        }

        // 7. Test delete (Xóa dịch vụ)
        try {
            System.out.println("\n--- Test Xóa dịch vụ: " + testMaDV + " ---");
            boolean isDeleted = dao.delete(testMaDV);
            System.out.println("=> delete(): " + (isDeleted ? "Thành công" : "Thất bại"));

            // Kiểm tra lại sau khi xóa
            DichVu dvCheck = dao.getById(testMaDV);
            System.out.println("=> Kiểm tra sau xóa: " + (dvCheck == null ? "Đã xóa thành công" : "Vẫn còn tồn tại!"));
        } catch (Exception e) {
            System.err.println("=> Lỗi delete: " + e.getMessage());
        }

        System.out.println("\n--- KẾT THÚC TEST DICHVUDAO ---");
    }
}
