import dao.DBConnection;
import dao.KhachHangDAO;
import entity.KhachHang;

public class test {
//    public static void main(String[] args) {
//        System.out.println("Đang test kết nối database...\n");
//
//        // Kết nối database
//        DBConnection.getConnection();
//
//        // Test kết nối
//        boolean success = DBConnection.testConnection();
//
//        if (success) {
//            System.out.println("\n→ Bạn có thể bắt đầu code các phần khác!");
//        } else {
//            System.out.println("\n→ Kiểm tra lại:");
//            System.out.println("  1. MySQL đã chạy chưa?");
//            System.out.println("  2. Database 'quanlytiemnet_simple' đã tạo chưa?");
//            System.out.println("  3. Username/Password có đúng không?");
//            System.out.println("  4. MySQL Connector đã thêm vào pom.xml chưa?");
//        }
//
//        // Đóng kết nối
//        DBConnection.closeConnection();
//    }
public static void main(String[] args) {
    KhachHangDAO dao = new KhachHangDAO();

    // Test 1: Login đúng
    KhachHang kh1 = dao.login("hoangnam", "123456");
    if (kh1 != null) {
        System.out.println("Đăng nhập thành công: " + kh1.getHo() + " " + kh1.getTen());
        System.out.println("Số dư: " + kh1.getSodu());
    } else {
        System.out.println("Sai tên đăng nhập hoặc mật khẩu");
    }

    // Test 2: Login sai
    KhachHang kh2 = dao.login("hoangnam", "saimatkhau");
    System.out.println("Login sai: " + (kh2 == null ? "null" : "có data"));  // null

    // Test 3: Kiểm tra trùng tên đăng nhập
    KhachHang existing = dao.getByTenDangNhap("hoangnam");
    System.out.println("Tên 'hoangnam' đã tồn tại: " + (existing != null));  // true

    KhachHang notExist = dao.getByTenDangNhap("tenmoihoantooan");
    System.out.println("Tên 'tenmoihoantooan' tồn tại: " + (notExist != null));  // false

    // Test 4: Đăng ký mới
    KhachHang newKH = new KhachHang();
    newKH.setMakh(dao.generateMaKH());  // Tự sinh mã
    newKH.setHo("Lê");
    newKH.setTen("Văn Test");
    newKH.setSodienthoai("0909123456");
    newKH.setTendangnhap("levantest");
    newKH.setMatkhau("123456");
    newKH.setSodu(0);
    newKH.setTrangthai("HOATDONG");

    boolean result = dao.insert(newKH);
    System.out.println("Đăng ký: " + (result ? "Thành công" : "Thất bại"));
}
}