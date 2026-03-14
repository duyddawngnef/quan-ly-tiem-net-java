package test.dao;

import dao.MayTinhDAO;
import entity.MayTinh;

import java.util.List;

public class TestMayTinhDAO {

    public static void main(String[] args) {
        MayTinhDAO dao = new MayTinhDAO();

        // CHÚ Ý: đổi "KHU01" thành MaKhu có thật và đang HOATDONG trong CSDL của bạn
        String maKhuHopLe = "KHU006";

        // 1. Test Insert
        System.out.println("===== TEST INSERT =====");
        MayTinh mt = new MayTinh();
        // MaMay sẽ được generate trong DAO, nên không set ở đây
        mt.setTenmay("MAY_TEST_01");
        mt.setMakhu(maKhuHopLe);
        mt.setCauhinh("Core i5 / 8GB / GTX 1050");
        mt.setGiamoigio(10000.0);
        mt.setTrangthai("TRONG"); // DAO sẽ set lại thành TRONG, nhưng set cho đầy đủ

        try {
            boolean insertOk = dao.Insert(mt);
            System.out.println("Insert result: " + insertOk);
        } catch (Exception e) {
            System.out.println("Lỗi khi insert: " + e.getMessage());
            e.printStackTrace();
            return; // nếu insert lỗi thì dừng test, vì các bước sau phụ thuộc máy này
        }

        // Lưu lại MaMay vừa được generate để dùng cho các test sau.
        // Lưu ý: trong DAO Insert hiện tại bạn set MaMay vào đối tượng mt trước khi insert,
        // nên sau khi Insert xong, mt.getMamay() chính là mã trong DB.
        String maMayTest = mt.getMamay();
        System.out.println("MaMay được generate: " + maMayTest);

        // 2. Test getById
        System.out.println("\n===== TEST getById =====");
        try {
            MayTinh mtById = dao.getById(maMayTest);
            if (mtById != null) {
                System.out.println("Tìm thấy máy: ");
                printMayTinh(mtById);
            } else {
                System.out.println("Không tìm thấy máy với MaMay = " + maMayTest);
            }
        } catch (Exception e) {
            System.out.println("Lỗi khi getById: " + e.getMessage());
            e.printStackTrace();
        }

        // 3. Test getAll
        System.out.println("\n===== TEST getAll =====");
        try {
            List<MayTinh> list = dao.getAll();
            System.out.println("Số lượng máy: " + list.size());
            for (MayTinh item : list) {
                printMayTinh(item);
            }
        } catch (Exception e) {
            System.out.println("Lỗi khi getAll: " + e.getMessage());
            e.printStackTrace();
        }

        // 4. Test UpdateThongTinKhac
        System.out.println("\n===== TEST UpdateThongTinKhac =====");
        try {
            MayTinh mtUpdate = dao.getById(maMayTest);
            if (mtUpdate != null) {
                mtUpdate.setTenmay("MAY_TEST_01_UPDATED");
                mtUpdate.setCauhinh("Core i5 / 16GB / GTX 1660");
                // vẫn giữ MaKhu hợp lệ
                mtUpdate.setMakhu(maKhuHopLe);
                // thử set trạng thái khác, ví dụ TRONG (hoặc BAOTRI, NGUNG... tuỳ bạn)
                mtUpdate.setTrangthai("TRONG");

                boolean updateInfoOk = dao.UpdateThongTinKhac(mtUpdate);
                System.out.println("UpdateThongTinKhac result: " + updateInfoOk);

                MayTinh afterUpdateInfo = dao.getById(maMayTest);
                System.out.println("Sau khi UpdateThongTinKhac:");
                printMayTinh(afterUpdateInfo);
            } else {
                System.out.println("Không tìm thấy máy để update thông tin khác.");
            }
        } catch (Exception e) {
            System.out.println("Lỗi khi UpdateThongTinKhac: " + e.getMessage());
            e.printStackTrace();
        }

        // 5. Test UpdateGiaMoiGio (máy đang không DANGDUNG)
        System.out.println("\n===== TEST UpdateGiaMoiGio (khi không DANGDUNG) =====");
        try {
            boolean updateGiaOk = dao.UpdateGiaMoiGio(maMayTest, 15000.0);
            System.out.println("UpdateGiaMoiGio result: " + updateGiaOk);

            MayTinh afterUpdateGia = dao.getById(maMayTest);
            System.out.println("Sau khi UpdateGiaMoiGio:");
            printMayTinh(afterUpdateGia);
        } catch (Exception e) {
            System.out.println("Lỗi khi UpdateGiaMoiGio: " + e.getMessage());
            e.printStackTrace();
        }

        // 6. Test 4 chức năng Admin
        System.out.println("\n===== TEST Đưa vào bảo trì (TRONG -> BAOTRI) =====");
        try {
            boolean baoTriOk = dao.duaVaoBaoTri(maMayTest);
            System.out.println("duaVaoBaoTri result: " + baoTriOk);
            printMayTinh(dao.getById(maMayTest));
        } catch (Exception e) {
            System.out.println("Lỗi duaVaoBaoTri: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n===== TEST Hoàn tất bảo trì (BAOTRI -> TRONG) =====");
        try {
            boolean hoanTatOk = dao.hoanTatBaoTri(maMayTest);
            System.out.println("hoanTatBaoTri result: " + hoanTatOk);
            printMayTinh(dao.getById(maMayTest));
        } catch (Exception e) {
            System.out.println("Lỗi hoanTatBaoTri: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n===== TEST Ngừng sử dụng (TRONG/BAOTRI -> NGUNG) =====");
        try {
            boolean ngungOk = dao.ngungSuDung(maMayTest);
            System.out.println("ngungSuDung result: " + ngungOk);
            printMayTinh(dao.getById(maMayTest));
        } catch (Exception e) {
            System.out.println("Lỗi ngungSuDung: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n===== TEST Khôi phục (NGUNG -> TRONG) =====");
        try {
            boolean khoiPhucOk = dao.khoiPhuc(maMayTest);
            System.out.println("khoiPhuc result: " + khoiPhucOk);
            printMayTinh(dao.getById(maMayTest));
        } catch (Exception e) {
            System.out.println("Lỗi khoiPhuc: " + e.getMessage());
            e.printStackTrace();
        }

        // 7. Test delete (thực tế là set TrangThai='NGUNG')
        System.out.println("\n===== TEST delete (set NGUNG) =====");
        try {
            boolean deleteOk = dao.delete(maMayTest);
            System.out.println("delete result: " + deleteOk);
            printMayTinh(dao.getById(maMayTest));
        } catch (Exception e) {
            System.out.println("Lỗi khi delete: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n===== KẾT THÚC TEST MayTinhDAO =====");
    }

    // Hàm in thông tin 1 máy cho dễ nhìn
    private static void printMayTinh(MayTinh mt) {
        if (mt == null) {
            System.out.println("MayTinh = null");
            return;
        }
        System.out.println("MaMay    : " + mt.getMamay());
        System.out.println("TenMay   : " + mt.getTenmay());
        System.out.println("MaKhu    : " + mt.getMakhu());
        System.out.println("CauHinh  : " + mt.getCauhinh());
        System.out.println("GiaMoiGio: " + mt.getGiamoigio());
        System.out.println("TrangThai: " + mt.getTrangthai());
        System.out.println("----------------------------------------");
    }
}