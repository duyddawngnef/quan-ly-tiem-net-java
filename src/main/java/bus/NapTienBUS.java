package bus;

import dao.*;
import entity.*;
import untils.PermissionHelper;

import java.time.LocalDate;
import java.util.ArrayList;

public class NapTienBUS {

    private KhachHangDAO khachHangDAO;
    private LichSuNapTienDAO lichSuNapTienDAO;
    private ChuongTrinhKhuyenMaiDAO chuongTrinhKhuyenMaiDAO;

    public NapTienBUS() {
        this.khachHangDAO = new KhachHangDAO();
        this.lichSuNapTienDAO = new LichSuNapTienDAO();
        this.chuongTrinhKhuyenMaiDAO = new ChuongTrinhKhuyenMaiDAO();
    }

    // phương thức nạp tiền
    public LichSuNapTien napTien(String maKH, double soTienNap, String maCTKM) throws Exception {

        // Kiểm tra phân quyền
        PermissionHelper.requireNapTien();
        String maNV = PermissionHelper.getCurrentMaNV();

        // Kiểm tra số tiền
        if (soTienNap <= 0) {
            throw new Exception("Số tiền nạp phải lớn hơn 0!");
        }

        // Lấy khách hàng, kiểm tra HOATDONG
        KhachHang kh = khachHangDAO.getById(maKH);
        if (kh == null) {
            throw new Exception("Khách hàng không tồn tại!");
        }
        if (kh.isNgung()) {
            throw new Exception("Khách hàng đã bị ngừng hoạt động!");
        }

        // Số dư trước
        double soDuTruoc = kh.getSodu();

        // Xử lý khuyến mãi
        double khuyenMai = 0;
        String loaiKM = null;
        ChuongTrinhKhuyenMai ctkm = null;

        if (maCTKM != null && !maCTKM.trim().isEmpty()) {

            ctkm = chuongTrinhKhuyenMaiDAO.getByID(maCTKM);
            if (ctkm == null) {
                throw new Exception("Chương trình khuyến mãi không tồn tại!");
            }

            LocalDate today = LocalDate.now();

            if (!"HOATDONG".equals(ctkm.getTrangThai())
                    || today.isBefore(ctkm.getNgayBatDau())
                    || today.isAfter(ctkm.getNgayKetThuc())) {
                throw new Exception("Chương trình khuyến mãi không còn hiệu lực!");
            }

            if (soTienNap < ctkm.getDieuKienToiThieu()) {
                throw new Exception("Không đủ điều kiện áp dụng khuyến mãi!");
            }

            loaiKM = ctkm.getLoaiKM();

            if ("PHANTRAM".equals(loaiKM)) {
                khuyenMai = soTienNap * ctkm.getGiaTriKM() / 100.0;
            } else if ("SOTIEN".equals(loaiKM)) {
                khuyenMai = ctkm.getGiaTriKM();
            } else if ("TANGGIO".equals(loaiKM)) {
                khuyenMai = 0;
            } else {
                throw new Exception("Loại khuyến mãi không hợp lệ!");
            }
        }

        // Tổng tiền cộng
        double tongTienCong = soTienNap + khuyenMai;
        double soDuSau = soDuTruoc + tongTienCong;

        // Sinh mã nạp
        String maNap = lichSuNapTienDAO.generateMaNap();

        // Tạo đối tượng lịch sử
        LichSuNapTien lsnt = new LichSuNapTien(
                maNap,
                maKH,
                maNV,
                maCTKM,
                soTienNap,
                khuyenMai,
                tongTienCong,
                soDuTruoc,
                soDuSau,
                "TIENMAT",
                null,
                LocalDate.now());

        // Cập nhật số dư khách hàng
        boolean updateOk = khachHangDAO.updateSoDu(maKH, soDuSau);
        if (!updateOk) {
            throw new Exception("Cập nhật số dư khách hàng thất bại!");
        }

        // Insert lịch sử — nếu thất bại thì hoàn tiền lại (compensate)
        boolean insertOk = lichSuNapTienDAO.insert(lsnt);
        if (!insertOk) {
            khachHangDAO.updateSoDu(maKH, soDuTruoc); // hoàn số dư về trạng thái ban đầu
            throw new Exception("Lưu lịch sử nạp tiền thất bại, đã hoàn tiền!");
        }

        // Nếu TANGGIO thì tạo gói tặng giờ
        if ("TANGGIO".equals(loaiKM) && ctkm != null) {
        }

        return lsnt;
    }

    // 2. Lấy lịch sử nạp tiền
    public ArrayList<LichSuNapTien> getLichSuNapTien(String maKH) throws Exception {
        PermissionHelper.requireNhanVien();
        if (maKH == null || maKH.trim().isEmpty()) {
            throw new Exception("Mã khách hàng không được để trống!");
        }
        return lichSuNapTienDAO.getByKhachHang(maKH);
    }

    // 3. Tính tiền khuyến mãi
    public double tinhKhuyenMai(double soTien, String maCTKM) throws Exception {
        PermissionHelper.requireNhanVien();

        if (soTien <= 0) {
            throw new Exception("Số tiền phải lớn hơn 0!");
        }

        if (maCTKM == null || maCTKM.trim().isEmpty()) {
            return 0;
        }

        ChuongTrinhKhuyenMai ctkm = chuongTrinhKhuyenMaiDAO.getByID(maCTKM);
        if (ctkm == null) {
            throw new Exception("Chương trình khuyến mãi không tồn tại!");
        }

        // Kiểm tra còn hiệu lực
        LocalDate today = LocalDate.now();
        if (!"HOATDONG".equals(ctkm.getTrangThai())
                || today.isBefore(ctkm.getNgayBatDau())
                || today.isAfter(ctkm.getNgayKetThuc())) {
            throw new Exception("Chương trình khuyến mãi không còn hiệu lực!");
        }

        // Kiểm tra điều kiện tối thiểu
        if (soTien < ctkm.getDieuKienToiThieu()) {
            return 0;
        }

        switch (ctkm.getLoaiKM()) {
            case "PHANTRAM":
                return soTien * ctkm.getGiaTriKM() / 100.0;
            case "SOTIEN":
                return ctkm.getGiaTriKM();
            case "TANGGIO":
                return 0;
            default:
                throw new Exception("Loại khuyến mãi không hợp lệ: " + ctkm.getLoaiKM());
        }
    }
}