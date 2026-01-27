package test.dao;

import dao.NhanVienDAO;
import entity.NhanVien;
import java.util.List;

public class NhanVienDAOTest {
    public static void main(String[] args) {
        NhanVienDAO dao = new NhanVienDAO();

        try {
            System.out.println("--- BẮT ĐẦU KIỂM TRA DAO ---");

            // 1. Test Lấy danh sách
            List<NhanVien> list = dao.getAll();
            System.out.println("1. getAll(): Tìm thấy " + list.size() + " nhân viên.");

            // 2. Tạo đối tượng admin giả lập để thực hiện lệnh
            NhanVien admin = new NhanVien();
            admin.setManv("NV001");
            admin.setChucvu("QUANLY");

            // 3. Test Thêm nhân viên
            NhanVien nvMoi = new NhanVien();
            nvMoi.setHo("Trần");
            nvMoi.setTen("A");
            nvMoi.setChucvu("NHANVIEN");
            nvMoi.setTendangnhap("trana_test");
            nvMoi.setMatkhau("654321");

            if(!dao.isTenDangNhapExists("trana_test")) {
                boolean isInserted = dao.insert(nvMoi, admin);
                System.out.println("2. insert(): " + (isInserted ? "Thành công" : "Thất bại"));
            } else {
                System.out.println("2. insert(): Bỏ qua vì user đã tồn tại.");
            }

            // 4. Test Đăng nhập
            NhanVien loggedIn = dao.login("admin", "123456");
            if (loggedIn != null) {
                System.out.println("3. login(): Thành công. Chào " + loggedIn.getTen());
            }

            // 5. Test Tìm kiếm
            List<NhanVien> searchResult = dao.search("Trần");
            System.out.println("4. search('Trần'): Tìm thấy " + searchResult.size() + " kết quả.");

            System.out.println("--- KIỂM TRA HOÀN TẤT ---");

        } catch (Exception e) {
            System.err.println("LỖI KHI TEST: " + e.getMessage());
            e.printStackTrace();
        }
    }
}