package test.bus;

import bus.GoiDichVuBUS;
import entity.GoiDichVu;
import entity.NhanVien;
import untils.SessionManager;

import java.util.List;

public class TestGoiDichVuBUS {
    public static void main(String[] args) {
        // Setup: Giả lập đăng nhập quản lý
        NhanVien admin = new NhanVien();
        admin.setManv("NV001");
        admin.setTen("Admin");
        admin.setChucvu("QUANLY");
        SessionManager.setCurrentUser(admin);

        GoiDichVuBUS bus = new GoiDichVuBUS();

        System.out.println("═══════════════════════════════════════");
        System.out.println("     TEST GÓI DỊCH VỤ BUS");
        System.out.println("═══════════════════════════════════════");

        String maGoiMoi = null;

        try {
            // 1. Test getDanhSachGoiDV
            System.out.println("\n--- 1. Test getDanhSachGoiDV() ---");
            List<GoiDichVu> list = bus.getDanhSachGoiDV();
            System.out.println(">> Tìm thấy " + list.size() + " gói dịch vụ.");
            for (GoiDichVu g : list) {
                System.out.printf("   %s | %s | %.1f giờ | Giá: %.0f | %s\n",
                        g.getMaGoi(), g.getTenGoi(), g.getSoGio(),
                        g.getGiaGoi(), g.getTrangThai());
            }

            // 2. Test themGoiDichVu
            System.out.println("\n--- 2. Test themGoiDichVu() ---");
            GoiDichVu goiMoi = new GoiDichVu();
            goiMoi.setTenGoi("Gói Test 10 Giờ");
            goiMoi.setLoaiGoi("GIOCHOI");
            goiMoi.setSoGio(10);
            goiMoi.setSoNgayHieuLuc(30);
            goiMoi.setGiaGoc(100000);
            goiMoi.setGiaGoi(80000);
            goiMoi.setApDungChoKhu("TATCA");
            goiMoi.setTrangThai("HOATDONG");

            boolean inserted = bus.themGoiDichVu(goiMoi);
            maGoiMoi = goiMoi.getMaGoi();
            System.out.println(">> Thêm gói: " + (inserted ? "Thành công ✓" : "Thất bại ✗"));
            if (inserted) {
                System.out.println("   Mã tự động: " + maGoiMoi);
            }

            // 3. Test suaGoiDichVu
            System.out.println("\n--- 3. Test suaGoiDichVu() ---");
            if (maGoiMoi != null) {
                goiMoi.setTenGoi("Gói Test 10 Giờ (Đã sửa)");
                goiMoi.setGiaGoi(75000);
                boolean updated = bus.suaGoiDichVu(goiMoi);
                System.out.println(">> Sửa gói " + maGoiMoi + ": " + (updated ? "Thành công ✓" : "Thất bại ✗"));
            }

            // 4. Test validate (thêm gói không hợp lệ)
            System.out.println("\n--- 4. Test Validate ---");
            // 4a. Tên rỗng
            try {
                GoiDichVu goiLoi = new GoiDichVu();
                goiLoi.setTenGoi("");
                goiLoi.setLoaiGoi("TEST");
                goiLoi.setSoGio(5);
                goiLoi.setSoNgayHieuLuc(7);
                goiLoi.setGiaGoc(50000);
                goiLoi.setGiaGoi(40000);
                bus.themGoiDichVu(goiLoi);
                System.out.println(">> Validate tên rỗng: Thất bại ✗");
            } catch (Exception e) {
                System.out.println(">> Validate tên rỗng: Thành công ✓ (" + e.getMessage() + ")");
            }

            // 4b. Giá gói > giá gốc
            try {
                GoiDichVu goiLoi2 = new GoiDichVu();
                goiLoi2.setTenGoi("Gói Lỗi");
                goiLoi2.setLoaiGoi("TEST");
                goiLoi2.setSoGio(5);
                goiLoi2.setSoNgayHieuLuc(7);
                goiLoi2.setGiaGoc(50000);
                goiLoi2.setGiaGoi(999999); // Giá gói > giá gốc
                bus.themGoiDichVu(goiLoi2);
                System.out.println(">> Validate giá gói > giá gốc: Thất bại ✗");
            } catch (Exception e) {
                System.out.println(">> Validate giá gói > giá gốc: Thành công ✓ (" + e.getMessage() + ")");
            }

            // 5. Test xoaGoiDichVu
            System.out.println("\n--- 5. Test xoaGoiDichVu() ---");
            if (maGoiMoi != null) {
                boolean deleted = bus.xoaGoiDichVu(maGoiMoi);
                System.out.println(">> Xóa gói " + maGoiMoi + ": " + (deleted ? "Thành công ✓" : "Thất bại ✗"));
            }

            // 6. Test phân quyền (nhân viên thường không được thêm/sửa/xóa)
            System.out.println("\n--- 6. Test Phân Quyền ---");
            NhanVien nhanVien = new NhanVien();
            nhanVien.setManv("NV002");
            nhanVien.setTen("Nhân Viên");
            nhanVien.setChucvu("NHANVIEN");
            SessionManager.setCurrentUser(nhanVien);

            try {
                bus.themGoiDichVu(new GoiDichVu());
                System.out.println(">> Phân quyền (NV thêm): Thất bại ✗");
            } catch (Exception e) {
                System.out.println(">> Phân quyền (NV thêm): Thành công ✓ (" + e.getMessage() + ")");
            }

            // Đọc vẫn được
            List<GoiDichVu> listNV = bus.getDanhSachGoiDV();
            System.out.println(">> Phân quyền (NV đọc): Thành công ✓ (" + listNV.size() + " gói)");

        } catch (Exception e) {
            System.err.println("LỖI: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n═══════════════════════════════════════");
        System.out.println("     KẾT THÚC TEST GÓI DV BUS");
        System.out.println("═══════════════════════════════════════");
    }
}
