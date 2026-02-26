package test.dao;

import dao.GoiDichVuKhachHangDAO;
import entity.GoiDichVuKhachHang;
import java.time.LocalDateTime;
import java.util.List;

public class TestGoiDichVuKhachHangDAO {
    public static void main(String[] args) {
        GoiDichVuKhachHangDAO dao = new GoiDichVuKhachHangDAO();

        System.out.println("--- BẮT ĐẦU TEST GOIDICHVUKHACHHANGDAO ---");

        // 1. Test generateId (Tạo mã tự động)
        String testMaGoiKH = null;
        try {
            System.out.println("\n--- Test Tạo mã tự động ---");
            testMaGoiKH = dao.generateId();
            System.out.println("=> Mã tự động mới: " + testMaGoiKH);
        } catch (Exception e) {
            System.err.println("=> Lỗi generateId: " + e.getMessage());
        }

        // 2. Test insert (Đăng ký gói cho khách)
        try {
            System.out.println("\n--- Test Đăng ký gói cho khách ---");
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime hetHan = now.plusDays(30); // Hết hạn sau 30 ngày

            GoiDichVuKhachHang goiKH = new GoiDichVuKhachHang(
                    testMaGoiKH,
                    "KH001", // Mã khách hàng - thay bằng mã KH có thật trong DB
                    "GOI001", // Mã gói dịch vụ - thay bằng mã gói có thật trong DB
                    "NV001", // Mã nhân viên - thay bằng mã NV có thật trong DB
                    10.0, // Số giờ ban đầu
                    10.0, // Số giờ còn lại
                    now, // Ngày mua
                    hetHan, // Ngày hết hạn
                    150000, // Giá mua
                    "CONHAN" // Trạng thái
            );
            boolean isInserted = dao.insert(goiKH);
            System.out.println("=> insert(): " + (isInserted ? "Thành công" : "Thất bại"));
        } catch (Exception e) {
            System.err.println("=> Lỗi insert: " + e.getMessage());
        }

        // 3. Test getConHieuLuc (Lọc gói còn hạn)
        try {
            System.out.println("\n--- Test Lọc gói còn hiệu lực của KH001 ---");
            List<GoiDichVuKhachHang> listConHan = dao.getConHieuLuc("KH001");
            System.out.println("=> Số gói còn hiệu lực: " + listConHan.size());
            for (GoiDichVuKhachHang g : listConHan) {
                System.out.println("   " + g.getMaGoiKH()
                        + " | MaGoi: " + g.getMaGoi()
                        + " | GioConLai: " + g.getSoGioConLai()
                        + " | HetHan: " + g.getNgayHetHan()
                        + " | TrangThai: " + g.getTrangThai());
            }
        } catch (Exception e) {
            System.err.println("=> Lỗi getConHieuLuc: " + e.getMessage());
        }

        // 4. Test getByKhachHang (Lấy tất cả gói của khách hàng)
        try {
            System.out.println("\n--- Test Lấy tất cả gói của KH001 ---");
            List<GoiDichVuKhachHang> listAll = dao.getByKhachHang("KH001");
            System.out.println("=> Tổng số gói: " + listAll.size());
            listAll.forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("=> Lỗi getByKhachHang: " + e.getMessage());
        }

        // 5. Test getByMaGoiKhachHang (Lấy theo mã gói khách hàng)
        try {
            System.out.println("\n--- Test Lấy gói theo mã: " + testMaGoiKH + " ---");
            GoiDichVuKhachHang goiKH = dao.getByMaGoiKhachHang(testMaGoiKH);
            if (goiKH != null && goiKH.getMaGoiKH() != null) {
                System.out.println("=> Thành công: " + goiKH);
            } else {
                System.out.println("=> Không tìm thấy gói với mã: " + testMaGoiKH);
            }
        } catch (Exception e) {
            System.err.println("=> Lỗi getByMaGoiKhachHang: " + e.getMessage());
        }

        // 6. Test update (Cập nhật số giờ còn lại và trạng thái)
        try {
            System.out.println("\n--- Test Cập nhật gói dịch vụ khách hàng ---");
            GoiDichVuKhachHang goiUpdate = new GoiDichVuKhachHang();
            goiUpdate.setMaGoiKH(testMaGoiKH);
            goiUpdate.setSoGioConLai(5.0); // Giảm còn 5 giờ
            goiUpdate.setTrangThai("CONHAN");

            boolean isUpdated = dao.update(goiUpdate);
            System.out.println("=> update(): " + (isUpdated ? "Thành công" : "Thất bại"));

            // Kiểm tra lại sau cập nhật
            GoiDichVuKhachHang check = dao.getByMaGoiKhachHang(testMaGoiKH);
            if (check != null && check.getMaGoiKH() != null) {
                System.out.println("=> Sau cập nhật - GioConLai: " + check.getSoGioConLai()
                        + " | TrangThai: " + check.getTrangThai());
            }
        } catch (Exception e) {
            System.err.println("=> Lỗi update: " + e.getMessage());
        }

        System.out.println("\n--- KẾT THÚC TEST GOIDICHVUKHACHHANGDAO ---");
    }
}
