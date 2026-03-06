package test.bus;

import bus.SuDungDichVuBUS;
import entity.NhanVien;
import entity.SuDungDichVu;
import untils.SessionManager;

import java.util.List;

public class TestSuDungDichVuBUS {
    public static void main(String[] args) {
        // Setup: Giả lập đăng nhập quản lý
        NhanVien admin = new NhanVien();
        admin.setManv("NV001");
        admin.setTen("Admin");
        admin.setChucvu("QUANLY");
        SessionManager.setCurrentUser(admin);

        SuDungDichVuBUS bus = new SuDungDichVuBUS();

        System.out.println("═══════════════════════════════════════");
        System.out.println("     TEST SỬ DỤNG DỊCH VỤ BUS");
        System.out.println("═══════════════════════════════════════");

        // ⚠️ LƯU Ý: Cần có dữ liệu sẵn trong DB
        // - Bảng phiensudung phải có MaPhien hợp lệ
        // - Bảng dichvu phải có dịch vụ với SoLuongTon > 0
        // Thay đổi các giá trị bên dưới cho phù hợp với DB
        String maPhienTest = "PS001"; // Đổi theo DB
        String maDVTest = "DV001"; // Đổi theo DB
        String maSDTest = null;

        try {
            // 1. Test orderDichVu
            System.out.println("\n--- 1. Test orderDichVu() ---");
            boolean ordered = bus.orderDichVu(maPhienTest, maDVTest, 2);
            System.out
                    .println(">> Order dịch vụ " + maDVTest + " (SL: 2): " + (ordered ? "Thành công ✓" : "Thất bại ✗"));

            // 2. Test getDVDaOrderTheoPhien
            System.out.println("\n--- 2. Test getDVDaOrderTheoPhien() ---");
            List<SuDungDichVu> listSD = bus.getDVDaOrderTheoPhien(maPhienTest);
            System.out.println(">> Phiên " + maPhienTest + " có " + listSD.size() + " dịch vụ đã order.");
            for (SuDungDichVu sd : listSD) {
                System.out.printf("   %s | DV: %s | SL: %d | Thành tiền: %.0f\n",
                        sd.getMaSD(), sd.getMaDV(), sd.getSoLuong(), sd.getThanhTien());
                maSDTest = sd.getMaSD(); // Lấy mã cuối cùng để test hủy
            }

            // 3. Test orderDichVu (tồn kho không đủ)
            System.out.println("\n--- 3. Test orderDichVu (tồn kho không đủ) ---");
            try {
                bus.orderDichVu(maPhienTest, maDVTest, 99999);
                System.out.println(">> Validate tồn kho: Thất bại ✗ (không bắt lỗi)");
            } catch (Exception e) {
                System.out.println(">> Validate tồn kho: Thành công ✓ (Bắt lỗi: " + e.getMessage() + ")");
            }

            // 4. Test huyOrder
            System.out.println("\n--- 4. Test huyOrder() ---");
            if (maSDTest != null) {
                boolean cancelled = bus.huyOrder(maSDTest);
                System.out.println(">> Hủy order " + maSDTest + ": " + (cancelled ? "Thành công ✓" : "Thất bại ✗"));
            } else {
                System.out.println(">> Bỏ qua (không có order để hủy)");
            }

            // 5. Test huyOrder (mã không tồn tại)
            System.out.println("\n--- 5. Test huyOrder (mã không tồn tại) ---");
            try {
                bus.huyOrder("SD_KHONG_TON_TAI");
                System.out.println(">> Validate: Thất bại ✗ (không bắt lỗi)");
            } catch (Exception e) {
                System.out.println(">> Validate: Thành công ✓ (Bắt lỗi: " + e.getMessage() + ")");
            }

        } catch (Exception e) {
            System.err.println("LỖI: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n═══════════════════════════════════════");
        System.out.println("     KẾT THÚC TEST SỬ DỤNG DV BUS");
        System.out.println("═══════════════════════════════════════");
    }
}
