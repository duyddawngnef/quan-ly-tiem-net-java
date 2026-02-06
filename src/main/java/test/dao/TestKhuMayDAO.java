package test.dao;

import dao.KhuMayDAO;
import entity.KhuMay;
import java.util.List;

public class TestKhuMayDAO {
    public static void main(String[] args) {
        KhuMayDAO dao = new KhuMayDAO();

        System.out.println("--- BẮT ĐẦU KIỂM TRA KHUMAYDAO ---");

        // 1. Kiểm tra INSERT (Thêm mới)
        System.out.println("\n1. Kiểm tra Insert:");
        KhuMay moi = new KhuMay();
        moi.setTenKhu("Khu Máy Gaming VIP");
        moi.setGiacoso(15000);
        moi.setSomaytoida(20);

        try {
            boolean isInserted = dao.insert(moi);
            if (isInserted) {
                System.out.println(">> Thêm mới thành công!");
            }
        } catch (Exception e) {
            System.err.println(">> Lỗi Insert: " + e.getMessage());
        }

        // 2. Kiểm tra GET ALL (Lấy danh sách)
        System.out.println("\n2. Danh sách khu máy hiện có:");
        List<KhuMay> list = dao.getAll();
        for (KhuMay km : list) {
            System.out.printf("ID: %s | Tên: %s | Giá: %.0f | Trạng thái: %s\n",
                    km.getMaKhu(), km.getTenKhu(), km.getGiacoso(), km.getTrangthai());
        }

        // 3. Kiểm tra GET BY ID & UPDATE
        if (!list.isEmpty()) {
            String targetID = list.get(0).getMaKhu(); // Lấy mã của thằng đầu tiên để test
            System.out.println("\n3. Kiểm tra Update cho mã: " + targetID);

            KhuMay existing = dao.getByID(targetID);
            if (existing != null) {
                existing.setTenKhu(existing.getTenKhu() + " (Đã sửa)");
                existing.setGiacoso(20000);

                try {
                    dao.update(existing);
                    System.out.println(">> Cập nhật thành công!");
                } catch (Exception e) {
                    System.err.println(">> Lỗi Update: " + e.getMessage());
                }
            }
        }

        // 4. Kiểm tra DELETE (Xóa mềm)
        System.out.println("\n4. Kiểm tra Delete:");
        try {
            // Thử tạo một mã không tồn tại để xem nó có throw lỗi không
            // Hoặc dùng mã vừa tạo ở bước 1 để xóa
            String idToDelete = "KHU007";
            boolean isDeleted = dao.delete(idToDelete);
            if (isDeleted) {
                System.out.println(">> Xóa (Ngưng hoạt động) thành công mã: " + idToDelete);
            }
        } catch (Exception e) {
            System.err.println(">> Lỗi khi xóa: " + e.getMessage());
        }

        System.out.println("\n--- KẾT THÚC KIỂM TRA ---");
    }
}