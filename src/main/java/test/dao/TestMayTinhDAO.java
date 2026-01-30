package test;

import dao.MayTinhDAO;
import entity.MayTinh;
import java.util.List;

public class TestMayTinhDAO {
    public static void main(String[] args) {
        MayTinhDAO dao = new MayTinhDAO();

        System.out.println("--- BẮT ĐẦU KIỂM TRA MAYTINHDAO ---");

        // 1. Kiểm tra INSERT (Thêm máy mới)
        // Lưu ý: MaKhu phải tồn tại trong bảng khumay và đang HOATDONG
        System.out.println("\n1. Kiểm tra Insert:");
        MayTinh mtMoi = new MayTinh();
        mtMoi.setTenmay("Máy Trạm 99");
        mtMoi.setMakhu("KHU001"); // Đảm bảo mã khu này có trong DB của bạn
        mtMoi.setCauhinh("Core i9, RTX 4090, 64GB RAM");
        mtMoi.setGiamoigio(20000);

        try {
            boolean isInserted = dao.insert(mtMoi);
            if (isInserted) {
                System.out.println(">> Thêm máy mới thành công!");
            }
        } catch (Exception e) {
            System.err.println(">> Lỗi Insert: " + e.getMessage());
        }

        // 2. Kiểm tra GET ALL
        System.out.println("\n2. Danh sách tất cả máy tính:");
        List<MayTinh> list = dao.getAll();
        for (MayTinh mt : list) {
            System.out.printf("ID: %s | Tên: %s | Khu: %s | Giá: %.0f | Trạng thái: %s\n",
                    mt.getMamay(), mt.getTenmay(), mt.getMakhu(), mt.getGiamoigio(), mt.getTrangthai());
        }

        // 3. Kiểm tra UPDATE
        if (!list.isEmpty()) {
            MayTinh mtUpdate = list.get(0);
            System.out.println("\n3. Kiểm tra Update cho mã: " + mtUpdate.getMamay());
            mtUpdate.setTenmay(mtUpdate.getTenmay() + " (VIP)");
            mtUpdate.setGiamoigio(25000);

            try {
                dao.update(mtUpdate);
                System.out.println(">> Cập nhật máy tính thành công!");
            } catch (Exception e) {
                System.err.println(">> Lỗi Update: " + e.getMessage());
            }
        }

        // 4. Kiểm tra DELETE (Xóa mềm - Chuyển sang NGUNG)
        if (!list.isEmpty()) {
            String idXoa = list.get(list.size() - 1).getMamay();
            System.out.println("\n4. Kiểm tra Delete cho mã: " + idXoa);
            try {
                boolean isDeleted = dao.delete(idXoa);
                if (isDeleted) {
                    System.out.println(">> Đã ngưng hoạt động máy: " + idXoa);
                }
            } catch (Exception e) {
                System.err.println(">> Lỗi Delete: " + e.getMessage());
            }
        }

        System.out.println("\n--- KẾT THÚC KIỂM TRA ---");
    }
}