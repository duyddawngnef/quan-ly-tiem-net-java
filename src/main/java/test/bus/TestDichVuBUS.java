package test.bus;

import bus.DichVuBUS;
import entity.DichVu;
import entity.NhanVien;
import untils.SessionManager;

import java.util.List;

public class TestDichVuBUS {
    public static void main(String[] args) {
        // Setup: Giả lập đăng nhập quản lý
        NhanVien admin = new NhanVien();
        admin.setManv("NV001");
        admin.setTen("Admin");
        admin.setChucvu("QUANLY");
        SessionManager.setCurrentUser(admin);

        DichVuBUS bus = new DichVuBUS();

        System.out.println("═══════════════════════════════════════");
        System.out.println("     TEST DỊCH VỤ BUS");
        System.out.println("═══════════════════════════════════════");

        try {
            // 1. Test getDanhSachDV
            System.out.println("\n--- 1. Test getDanhSachDV() ---");
            List<DichVu> list = bus.getDanhSachDV();
            System.out.println(">> Tìm thấy " + list.size() + " dịch vụ.");
            for (DichVu dv : list) {
                System.out.printf("   %s | %s | Giá: %.0f | Tồn: %d | %s\n",
                        dv.getMaDV(), dv.getTenDV(), dv.getDonGia(),
                        dv.getSoLuongTon(), dv.getTrangThai());
            }

            // 2. Test themDichVu
            System.out.println("\n--- 2. Test themDichVu() ---");
            DichVu dvMoi = new DichVu();
            dvMoi.setTenDV("Mì Tôm Hảo Hảo");
            dvMoi.setLoaiDV("DOUONG");
            dvMoi.setDonGia(10000);
            dvMoi.setDonViTinh("Gói");
            dvMoi.setSoLuongTon(50);
            dvMoi.setTrangThai("HOATDONG");

            boolean inserted = bus.themDichVu(dvMoi);
            System.out.println(">> Thêm dịch vụ: " + (inserted ? "Thành công ✓" : "Thất bại ✗"));
            if (inserted) {
                System.out.println("   Mã tự động: " + dvMoi.getMaDV());
            }

            // 3. Test suaDichVu
            System.out.println("\n--- 3. Test suaDichVu() ---");
            if (inserted && dvMoi.getMaDV() != null) {
                dvMoi.setTenDV("Mì Tôm Hảo Hảo (Đã sửa)");
                dvMoi.setDonGia(12000);
                boolean updated = bus.suaDichVu(dvMoi);
                System.out.println(
                        ">> Sửa dịch vụ " + dvMoi.getMaDV() + ": " + (updated ? "Thành công ✓" : "Thất bại ✗"));
            }

            // 4. Test kiemTraTonKho
            System.out.println("\n--- 4. Test kiemTraTonKho() ---");
            if (dvMoi.getMaDV() != null) {
                boolean du = bus.kiemTraTonKho(dvMoi.getMaDV(), 10);
                System.out.println(">> Kiểm tra tồn kho (cần 10): " + (du ? "Đủ ✓" : "Không đủ ✗"));

                boolean khongDu = bus.kiemTraTonKho(dvMoi.getMaDV(), 999);
                System.out.println(">> Kiểm tra tồn kho (cần 999): " + (khongDu ? "Đủ ✓" : "Không đủ ✗"));
            }

            // 5. Test validate (thử thêm dịch vụ không hợp lệ)
            System.out.println("\n--- 5. Test Validate ---");
            try {
                DichVu dvLoi = new DichVu();
                dvLoi.setTenDV(""); // Tên rỗng
                dvLoi.setDonGia(-5000); // Giá âm
                bus.themDichVu(dvLoi);
                System.out.println(">> Validate: Thất bại ✗ (không bắt lỗi)");
            } catch (Exception e) {
                System.out.println(">> Validate: Thành công ✓ (Bắt lỗi: " + e.getMessage() + ")");
            }

            // 6. Test xoaDichVu
            System.out.println("\n--- 6. Test xoaDichVu() ---");
            if (dvMoi.getMaDV() != null) {
                boolean deleted = bus.xoaDichVu(dvMoi.getMaDV());
                System.out.println(
                        ">> Xóa dịch vụ " + dvMoi.getMaDV() + ": " + (deleted ? "Thành công ✓" : "Thất bại ✗"));
            }

        } catch (Exception e) {
            System.err.println("LỖI: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n═══════════════════════════════════════");
        System.out.println("     KẾT THÚC TEST DỊCH VỤ BUS");
        System.out.println("═══════════════════════════════════════");
    }
}
