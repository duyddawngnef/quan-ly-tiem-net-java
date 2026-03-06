package test.bus;

import bus.GoiDichVuKhachHangBUS;
import entity.NhanVien;
import untils.SessionManager;

public class TestGoiDichVuKhachHangBUS {
    public static void main(String[] args) {
        // Setup: Giả lập đăng nhập quản lý
        NhanVien admin = new NhanVien();
        admin.setManv("NV001");
        admin.setTen("Admin");
        admin.setChucvu("QUANLY");
        SessionManager.setCurrentUser(admin);

        GoiDichVuKhachHangBUS bus = new GoiDichVuKhachHangBUS();

        System.out.println("═══════════════════════════════════════");
        System.out.println("   TEST GÓI DỊCH VỤ KHÁCH HÀNG BUS");
        System.out.println("═══════════════════════════════════════");

        // ⚠️ LƯU Ý: Cần có dữ liệu sẵn trong DB
        // - Bảng khachhang phải có KH với số dư đủ
        // - Bảng goidichvu phải có gói đang hoạt động
        // Thay đổi các giá trị bên dưới cho phù hợp với DB
        String maKHTest = "KH001"; // Đổi theo DB
        String maGoiTest = "GOI001"; // Đổi theo DB

        try {
            // 1. Test muaGoi
            System.out.println("\n--- 1. Test muaGoi() ---");
            boolean muaOk = bus.muaGoi(maKHTest, maGoiTest);
            System.out.println(">> Mua gói " + maGoiTest + " cho KH " + maKHTest + ": "
                    + (muaOk ? "Thành công ✓" : "Thất bại ✗"));

            // 2. Test muaGoi (KH không tồn tại)
            System.out.println("\n--- 2. Test muaGoi (KH không tồn tại) ---");
            try {
                bus.muaGoi("KH_KHONG_TON_TAI", maGoiTest);
                System.out.println(">> Validate KH: Thất bại ✗ (không bắt lỗi)");
            } catch (Exception e) {
                System.out.println(">> Validate KH: Thành công ✓ (" + e.getMessage() + ")");
            }

            // 3. Test muaGoi (Gói không tồn tại)
            System.out.println("\n--- 3. Test muaGoi (Gói không tồn tại) ---");
            try {
                bus.muaGoi(maKHTest, "GOI_KHONG_TON_TAI");
                System.out.println(">> Validate Gói: Thất bại ✗ (không bắt lỗi)");
            } catch (Exception e) {
                System.out.println(">> Validate Gói: Thành công ✓ (" + e.getMessage() + ")");
            }

            // 4. Test truGioSuDung
            System.out.println("\n--- 4. Test truGioSuDung() ---");
            double gioThua = bus.truGioSuDung(maKHTest, 1.5);
            if (gioThua == 0) {
                System.out.println(">> Trừ 1.5 giờ: Thành công ✓ (Đã trừ hết, không thừa giờ)");
            } else {
                System.out.println(">> Trừ 1.5 giờ: Còn thừa " + gioThua + " giờ chưa trừ được");
            }

            // 5. Test truGioSuDung (số giờ âm)
            System.out.println("\n--- 5. Test truGioSuDung (số giờ <= 0) ---");
            try {
                bus.truGioSuDung(maKHTest, -5);
                System.out.println(">> Validate giờ âm: Thất bại ✗ (không bắt lỗi)");
            } catch (Exception e) {
                System.out.println(">> Validate giờ âm: Thành công ✓ (" + e.getMessage() + ")");
            }

            // 6. Test truGioSuDung (KH không có gói)
            System.out.println("\n--- 6. Test truGioSuDung (KH không có gói) ---");
            try {
                bus.truGioSuDung("KH_KHONG_CO_GOI", 1.0);
                System.out.println(">> Validate không gói: Thất bại ✗ (không bắt lỗi)");
            } catch (Exception e) {
                System.out.println(">> Validate không gói: Thành công ✓ (" + e.getMessage() + ")");
            }

        } catch (Exception e) {
            System.err.println("LỖI: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n═══════════════════════════════════════");
        System.out.println("   KẾT THÚC TEST GÓI DV KH BUS");
        System.out.println("═══════════════════════════════════════");
    }
}
