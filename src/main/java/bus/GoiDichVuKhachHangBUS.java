package bus;

import dao.GoiDichVuDAO;
import dao.GoiDichVuKhachHangDAO;
import dao.KhachHangDAO;
import entity.GoiDichVu;
import entity.GoiDichVuKhachHang;
import entity.KhachHang;
import entity.NhanVien;
import untils.SessionManager;

import java.time.LocalDateTime;
import java.util.List;

public class GoiDichVuKhachHangBUS {

    private final GoiDichVuKhachHangDAO goiDichVuKhachHangDAO = new GoiDichVuKhachHangDAO();
    private final GoiDichVuDAO goiDichVuDAO = new GoiDichVuDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();

    /**
     * Mua gói dịch vụ cho khách hàng
     * 1. Lấy giá gói
     * 2. Kiểm tra KH đủ tiền
     * 3. Trừ tiền KH
     * 4. Tạo GoiDichVuKhachHang với ngayHetHan
     */
    public boolean muaGoi(String maKH, String maGoi) throws Exception {
        NhanVien currentUser = SessionManager.getCurrentNhanVien();
        if (currentUser == null)
            throw new Exception("Chưa đăng nhập");
        if (!SessionManager.isNhanVien())
            throw new Exception("Không có quyền thực hiện");

        // 1. Lấy thông tin gói dịch vụ
        GoiDichVu goi = null;
        List<GoiDichVu> danhSachGoi = goiDichVuDAO.getAll();
        for (GoiDichVu g : danhSachGoi) {
            if (g.getMaGoi().equals(maGoi)) {
                goi = g;
                break;
            }
        }
        if (goi == null)
            throw new Exception("Gói dịch vụ không tồn tại");
        if (!"CONHAN".equals(goi.getTrangThai()) && !"HOATDONG".equals(goi.getTrangThai()))
            throw new Exception("Gói dịch vụ không còn hoạt động");

        // 2. Kiểm tra KH đủ tiền
        KhachHang kh = khachHangDAO.getById(maKH);
        if (kh == null)
            throw new Exception("Khách hàng không tồn tại");
        if (kh.getSodu() < goi.getGiaGoi())
            throw new Exception("Số dư không đủ! Cần: " + goi.getGiaGoi() + ", Hiện có: " + kh.getSodu());

        // 3. Trừ tiền KH
        double soDuMoi = kh.getSodu() - goi.getGiaGoi();
        boolean truTien = khachHangDAO.updateSoDu(maKH, soDuMoi);
        if (!truTien)
            throw new Exception("Lỗi khi trừ tiền khách hàng");

        // 4. Tạo GoiDichVuKhachHang
        GoiDichVuKhachHang goiKH = new GoiDichVuKhachHang();
        goiKH.setMaGoiKH(goiDichVuKhachHangDAO.generateId());
        goiKH.setMaKH(maKH);
        goiKH.setMaGoi(maGoi);
        goiKH.setMaNV(SessionManager.getCurrentMaNV());
        goiKH.setSoGioBanDau(goi.getSoGio());
        goiKH.setSoGioConLai(goi.getSoGio());
        goiKH.setNgayMua(LocalDateTime.now());
        goiKH.setNgayHetHan(LocalDateTime.now().plusDays(goi.getSoNgayHieuLuc()));
        goiKH.setGiaMua(goi.getGiaGoi());
        goiKH.setTrangThai("CONHAN");

        boolean inserted = goiDichVuKhachHangDAO.insert(goiKH);
        if (!inserted) {
            // Rollback: hoàn tiền nếu insert thất bại
            khachHangDAO.updateSoDu(maKH, kh.getSodu());
            throw new Exception("Lỗi khi đăng ký gói dịch vụ");
        }

        return true;
    }

    /**
     * Trừ giờ sử dụng từ gói của khách hàng
     * 1. Lấy gói còn hiệu lực của KH
     * 2. Trừ giờ, cập nhật soGioConLai
     * 3. Return số giờ còn thừa (nếu gói hết giờ)
     */
    public double truGioSuDung(String maKH, double soGio) throws Exception {
        NhanVien currentUser = SessionManager.getCurrentNhanVien();
        if (currentUser == null)
            throw new Exception("Chưa đăng nhập");
        if (!SessionManager.isNhanVien())
            throw new Exception("Không có quyền thực hiện");

        if (soGio <= 0)
            throw new Exception("Số giờ phải lớn hơn 0");

        // 1. Lấy gói còn hiệu lực
        List<GoiDichVuKhachHang> goiConHan = goiDichVuKhachHangDAO.getConHieuLuc(maKH);
        if (goiConHan == null || goiConHan.isEmpty())
            throw new Exception("Khách hàng không có gói dịch vụ còn hiệu lực");

        double gioCanTru = soGio;

        // 2. Trừ giờ từ từng gói (ưu tiên gói sắp hết hạn trước)
        for (GoiDichVuKhachHang goiKH : goiConHan) {
            if (gioCanTru <= 0)
                break;

            double gioConLai = goiKH.getSoGioConLai();

            if (gioConLai >= gioCanTru) {
                // Gói này đủ giờ
                goiKH.setSoGioConLai(gioConLai - gioCanTru);
                if (goiKH.getSoGioConLai() == 0) {
                    goiKH.setTrangThai("HETGIO");
                }
                goiDichVuKhachHangDAO.update(goiKH);
                gioCanTru = 0;
            } else {
                // Gói này không đủ, trừ hết và chuyển sang gói tiếp theo
                gioCanTru -= gioConLai;
                goiKH.setSoGioConLai(0);
                goiKH.setTrangThai("HETGIO");
                goiDichVuKhachHangDAO.update(goiKH);
            }
        }

        // 3. Return số giờ còn thừa (chưa trừ được)
        return gioCanTru;
    }
}
