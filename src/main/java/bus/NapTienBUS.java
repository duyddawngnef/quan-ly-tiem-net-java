package bus;

import dao.*;
import entity.*;
import utils.PermissionHelper;

import java.sql.Date;
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

    public ArrayList<LichSuNapTien> getAllLichSu() throws Exception {
        PermissionHelper.requireNhanVien();
        return lichSuNapTienDAO.getAll();
    }

    public ArrayList<LichSuNapTien> getByDateRange(LocalDate tuNgay, LocalDate denNgay) throws Exception {
        PermissionHelper.requireNhanVien();
        if (tuNgay == null || denNgay == null) {
            throw new Exception("Ngày bắt đầu và ngày kết thúc không được để trống!");
        }
        if (tuNgay.isAfter(denNgay)) {
            throw new Exception("Ngày bắt đầu không được sau ngày kết thúc!");
        }
        Date sqlTuNgay = Date.valueOf(tuNgay);
        Date sqlDenNgay = Date.valueOf(denNgay);
        return lichSuNapTienDAO.getByDateRange(sqlTuNgay, sqlDenNgay);
    }

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
            LocalDate ngayHienTai = LocalDate.now();
            if (!"HOATDONG".equals(ctkm.getTrangThai())
                    || ngayHienTai.isBefore(ctkm.getNgayBatDau())
                    || ngayHienTai.isAfter(ctkm.getNgayKetThuc())) {
                throw new Exception("Chương trình khuyến mãi không còn hiệu lực!");
            }
            if (soTienNap < ctkm.getDieuKienToiThieu()) {
                throw new Exception("Không đủ điều kiện áp dụng khuyến mãi!");
            }
            loaiKM = ctkm.getLoaiKM();
            switch (loaiKM){
                case "PHAMTRAM":
                    khuyenMai = soTienNap * ctkm.getGiaTriKM() / 100.0;
                    break;
                case "SOTIEN":
                    khuyenMai = ctkm.getGiaTriKM();
                    break;
                case "TANGGIO":
                    khuyenMai = 0;
                    break;
                default:
                    throw new Exception("Loại khuyến mãi không hợp lệ!");
            }
        }

        double tongTienCong = soTienNap + khuyenMai;
        double soDuSau = soDuTruoc + tongTienCong;
        String maNap = lichSuNapTienDAO.generateMaNap();

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
        if ("TANGGIO".equals(loaiKM) && ctkm != null) {}
        return lsnt;
    }

    public ArrayList<LichSuNapTien> getLichSuNapTien(String maKH) throws Exception {
        PermissionHelper.requireNhanVien();
        if (maKH == null || maKH.trim().isEmpty()) {
            throw new Exception("Mã khách hàng không được để trống!");
        }
        return lichSuNapTienDAO.getByKhachHang(maKH);
    }

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
        LocalDate ngayHienTai = LocalDate.now();
        if (!"HOATDONG".equals(ctkm.getTrangThai())
                || ngayHienTai.isBefore(ctkm.getNgayBatDau())
                || ngayHienTai.isAfter(ctkm.getNgayKetThuc())) {
            throw new Exception("Chương trình khuyến mãi không còn hiệu lực!");
        }
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