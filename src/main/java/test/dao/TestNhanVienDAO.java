package test.dao;

import dao.NhanVienDAO;
import entity.NhanVien;
import java.util.List;

public class TestNhanVienDAO {
    public static void main(String[] args) {
        NhanVienDAO dao = new NhanVienDAO();
        System.out.println("--- BẮT ĐẦU TEST TOÀN DIỆN NHANVIENDAO ---");

        // Giả lập một nhân viên đang đăng nhập là QUAN LY để có quyền Thêm/Sửa/Xóa
        NhanVien adminUser = new NhanVien();
        adminUser.setManv("NV001");
        adminUser.setHo("Admin");
        adminUser.setTen("System");
        adminUser.setChucvu("QUANLY"); // Quan trọng: Phải là QUANLY mới qua được validate
        adminUser.setTrangthai("DANGLAMVIEC");

        String maNVMoi = ""; // Biến lưu mã để dùng xuyên suốt

        // ==========================================
        // 1. TEST INSERT (THÊM MỚI)
        // ==========================================
        try {
            System.out.println("\n[1] Test Thêm Nhân Viên Mới:");

            NhanVien newNV = new NhanVien();
            newNV.setHo("Lê");
            newNV.setTen("Thử Nghiệm");
            newNV.setChucvu("NHANVIEN");
            // Tạo user ngẫu nhiên để không trùng
            newNV.setTendangnhap("staff_" + System.currentTimeMillis());
            newNV.setMatkhau("123456");

            // Gọi hàm insert với quyền Admin
            if (dao.insert(newNV, adminUser)) {
                maNVMoi = newNV.getManv(); // Lấy mã vừa sinh (VD: NV006)
                System.out.println("=> THÀNH CÔNG: Đã thêm nhân viên mã: " + maNVMoi);
                System.out.println("   Tên đăng nhập: " + newNV.getTendangnhap());
            }
        } catch (Exception e) {
            System.err.println("=> THẤT BẠI: " + e.getMessage());
        }

        // ==========================================
        // 2. TEST UPDATE (CẬP NHẬT)
        // ==========================================
        if (!maNVMoi.isEmpty()) {
            try {
                System.out.println("\n[2] Test Cập Nhật Thông Tin:");

                NhanVien nvUpdate = new NhanVien();
                nvUpdate.setManv(maNVMoi); // Mã của nhân viên vừa tạo
                nvUpdate.setHo("Lê (Đã Sửa)");
                nvUpdate.setTen("Thử Nghiệm Update");
                nvUpdate.setChucvu("NHANVIEN");
                nvUpdate.setMatkhau("654321"); // Đổi mật khẩu

                // Gọi hàm update với quyền Admin
                if (dao.update(nvUpdate, adminUser)) {
                    System.out.println("=> THÀNH CÔNG: Đã cập nhật cho " + maNVMoi);

                    // Kiểm tra lại
                    NhanVien check = dao.getById(maNVMoi);
                    System.out.println("   Tên mới: " + check.getHo() + " " + check.getTen());
                    System.out.println("   Mật khẩu mới: " + check.getMatkhau());
                }
            } catch (Exception e) {
                System.err.println("=> THẤT BẠI: " + e.getMessage());
            }
        }

        // ==========================================
        // 3. TEST LOGIN (ĐĂNG NHẬP)
        // ==========================================
        if (!maNVMoi.isEmpty()) {
            try {
                System.out.println("\n[3] Test Đăng Nhập (Login):");

                // Lấy tên đăng nhập của nhân viên vừa tạo để test
                NhanVien createdNV = dao.getById(maNVMoi);
                String username = createdNV.getTendangnhap();
                String password = createdNV.getMatkhau(); // Mật khẩu mới (654321)

                NhanVien loginResult = dao.login(username, password);
                if (loginResult != null) {
                    System.out.println("=> THÀNH CÔNG: Đăng nhập được với user: " + username);
                    System.out.println("   Chức vụ: " + loginResult.getChucvu());
                }
            } catch (Exception e) {
                System.err.println("=> THẤT BẠI: " + e.getMessage());
            }
        }

        // ==========================================
        // 4. TEST GET ALL & SEARCH
        // ==========================================
        try {
            System.out.println("\n[4] Test Lấy Danh Sách & Tìm Kiếm:");

            List<NhanVien> list = dao.getAllDangLamViec();
            System.out.println("- Tổng số NV đang làm việc: " + list.size());

            // Test tìm kiếm
            String keyword = "Thử Nghiệm";
            List<NhanVien> searchList = dao.search(keyword);
            System.out.println("- Tìm thấy " + searchList.size() + " nhân viên với từ khóa '" + keyword + "'");
        } catch (Exception e) {
            System.err.println("=> THẤT BẠI: " + e.getMessage());
        }

        // ==========================================
        // 5. TEST SOFT DELETE (CHO NGHỈ VIỆC)
        // ==========================================
        if (!maNVMoi.isEmpty()) {
            try {
                System.out.println("\n[5] Test Cho Nghỉ Việc (Soft Delete):");

                // Xem cảnh báo
                String warning = dao.getDeleteWarning(maNVMoi, adminUser);
                if(warning != null) System.out.println("   Cảnh báo: " + warning);

                if (dao.delete(maNVMoi, adminUser)) {
                    System.out.println("=> THÀNH CÔNG: Đã chuyển trạng thái " + maNVMoi + " sang NGHIVIEC");
                }
            } catch (Exception e) {
                System.err.println("=> THẤT BẠI: " + e.getMessage());
            }
        }

        // ==========================================
        // 6. TEST RESTORE (KHÔI PHỤC ĐI LÀM)
        // ==========================================
        if (!maNVMoi.isEmpty()) {
            try {
                System.out.println("\n[6] Test Khôi Phục (Restore):");

                if (dao.restore(maNVMoi, adminUser)) {
                    System.out.println("=> THÀNH CÔNG: Đã khôi phục " + maNVMoi + " sang DANGLAMVIEC");
                }
            } catch (Exception e) {
                System.err.println("=> THẤT BẠI: " + e.getMessage());
            }
        }

        System.out.println("\n--- KẾT THÚC TEST ---");
    }
}