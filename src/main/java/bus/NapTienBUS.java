package bus;

import dao.*;
import entity.*;
import untils.SessionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class NapTienBUS {

    private LichSuNapTienDAO lichSuNapTienDAO;
    private KhachHangDAO khachHangDAO;
    private ChuongTrinhKhuyenMaiDAO ctkm_DAO;

    public NapTienBUS() {
        this.lichSuNapTienDAO = new LichSuNapTienDAO();
        this.khachHangDAO = new KhachHangDAO();
        this.ctkm_DAO = new ChuongTrinhKhuyenMaiDAO();
    }

    public LichSuNapTien napTien(String maKH, double soTienNap, String maCTKM) throws Exception {
        // Kiểm tra phân quyền
        NhanVien currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new Exception("Chưa đăng nhập!");
        }

        String vaiTro = currentUser.getVaiTro();
        if (!vaiTro.equals("QUANLY") && !vaiTro.equals("NHANVIEN")) {
            throw new Exception("Không có quyền thực hiện chức năng này!");
        }

        // Kiểm tra số tiền nạp > 0
        if (soTienNap <= 0) {
            throw new Exception("Số tiền nạp phải lớn hơn 0!");
        }

        // Lấy khách hàng và kiểm tra trạng thái
        KhachHang kh = khachHangDAO.getByMaKH(maKH);
        if (kh == null) {
            throw new Exception("Không tìm thấy khách hàng!");
        }

        if (!kh.getTrangThai().equals("HOATDONG")) {
            throw new Exception("Khách hàng không hoạt động, không thể nạp tiền!");
        }

        // Lưu số dư trước
        double soDuTruoc = kh.getSoDu();

        // Xử lý khuyến mãi
        double khuyenMai = 0;
        ChuongTrinhKhuyenMai ctkm = null;
        boolean isTangGio = false;

        if (maCTKM != null && !maCTKM.trim().isEmpty()) {
            ctkm = ctkm_DAO.getByMaCTKM(maCTKM);

            if (ctkm == null) {
                throw new Exception("Không tìm thấy chương trình khuyến mãi!");
            }

            // Kiểm tra còn hiệu lực
            LocalDate now = LocalDate.now();
            if (now.isBefore(ctkm.getNgayBatDau()) || now.isAfter(ctkm.getNgayKetThuc())) {
                throw new Exception("Chương trình khuyến mãi không còn hiệu lực!");
            }

            if (!ctkm.getTrangThai().equals("HOATDONG")) {
                throw new Exception("Chương trình khuyến mãi không hoạt động!");
            }

            // Kiểm tra điều kiện tối thiểu
            double dieuKienToiThieu = ctkm.getDieuKienToiThieu();
            if (soTienNap < dieuKienToiThieu) {
                throw new Exception("Số tiền nạp không đủ điều kiện khuyến mãi! Tối thiểu: "
                        + dieuKienToiThieu + " VNĐ");
            }

            // Tính khuyến mãi theo loại
            String loaiKM = ctkm.getLoaiKM();
            double giaTriKM = ctkm.getGiaTriKM();

            switch (loaiKM) {
                case "PHANTRAM":
                    khuyenMai = soTienNap * giaTriKM / 100;
                    break;

                case "SOTIEN":
                    khuyenMai = giaTriKM;
                    break;

                case "TANGGIO":
                    khuyenMai = 0; // Xử lý riêng sau
                    isTangGio = true;
                    break;

                default:
                    throw new Exception("Loại khuyến mãi không hợp lệ!");
            }
        }

        // Tính tổng tiền cộng
        double tongTienCong = soTienNap + khuyenMai;
        double soDuSau = soDuTruoc + tongTienCong;

        // BEGIN TRANSACTION
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Cập nhật số dư khách hàng
            boolean updateSuccess = khachHangDAO.updateSoDu(maKH, tongTienCong);
            if (!updateSuccess) {
                throw new Exception("Cập nhật số dư khách hàng thất bại!");
            }

            // Tạo lịch sử nạp tiền
            String maNap = generateMaNap();

            LichSuNapTien lichSu = new LichSuNapTien(
                    maNap,
                    maKH,
                    currentUser.getMaNV(),
                    maCTKM,
                    soTienNap,
                    khuyenMai,
                    tongTienCong,
                    soDuTruoc,
                    soDuSau,
                    phuongThuc,
                    maGiaoDich,
                    LocalDate.now());

            // Insert lịch sử nạp tiền
            boolean insertSuccess = lichSuNapTienDAO.insert(lichSu);
            if (!insertSuccess) {
                throw new Exception("Lưu lịch sử nạp tiền thất bại!");
            }

            // Nếu là khuyến mãi tặng giờ, tạo gói tặng
            if (isTangGio && ctkm != null) {
                GoiTang goiTang = new GoiTang();
                goiTang.setMaGoi(generateMaGoiTang());
                goiTang.setMaKH(maKH);
                goiTang.setMaCTKM(maCTKM);
                goiTang.setTenGoi("Khuyến mãi nạp tiền - " + ctkm.getTenCTKM());
                goiTang.setSoGioTang((int) ctkm.getGiaTriKM());
                goiTang.setSoGioConLai((int) ctkm.getGiaTriKM());
                goiTang.setNgayBatDau(LocalDate.now());
                goiTang.setNgayHetHan(LocalDate.now().plusDays(30)); // Mặc định 30 ngày
                goiTang.setTrangThai("HOATDONG");
                goiTang.setMaNap(maNap);

                boolean insertGoiTang = goiTangDAO.insert(goiTang);
                if (!insertGoiTang) {
                    throw new Exception("Tạo gói tặng giờ thất bại!");
                }
            }

            // COMMIT
            conn.commit();

            return lichSu;

        } catch (Exception e) {
            // ROLLBACK nếu có lỗi
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ArrayList<LichSuNapTien> getLichSuNapTien(String maKH) throws Exception {
        // Kiểm tra phân quyền
        NhanVien currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new Exception("Chưa đăng nhập!");
        }

        String vaiTro = currentUser.getVaiTro();
        if (!vaiTro.equals("QUANLY") && !vaiTro.equals("NHANVIEN")) {
            throw new Exception("Không có quyền truy cập!");
        }

        return lichSuNapTienDAO.getByKhachHang(maKH);
    }

    public double tinhKhuyenMai(double soTien, String maCTKM) throws Exception {
        // Kiểm tra phân quyền
        NhanVien currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            throw new Exception("Chưa đăng nhập!");
        }

        String vaiTro = currentUser.getVaiTro();
        if (!vaiTro.equals("QUANLY") && !vaiTro.equals("NHANVIEN")) {
            throw new Exception("Không có quyền thực hiện!");
        }

        if (soTien <= 0) {
            throw new Exception("Số tiền phải lớn hơn 0!");
        }

        if (maCTKM == null || maCTKM.trim().isEmpty()) {
            return 0;
        }

        ChuongTrinhKhuyenMai ctkm = ctkm_DAO.getByMaCTKM(maCTKM);
        if (ctkm == null) {
            throw new Exception("Không tìm thấy chương trình khuyến mãi!");
        }

        // Kiểm tra điều kiện
        double dieuKienToiThieu = ctkm.getDieuKienToiThieu();
        if (soTien < dieuKienToiThieu) {
            return 0;
        }

        // Tính khuyến mãi
        String loaiKM = ctkm.getLoaiKM();
        double giaTriKM = ctkm.getGiaTriKM();

        switch (loaiKM) {
            case "PHANTRAM":
                return soTien * giaTriKM / 100;

            case "SOTIEN":
                return giaTriKM;

            case "TANGGIO":
                return 0;

            default:
                throw new Exception("Loại khuyến mãi không hợp lệ!");
        }
    }

    // Max nạp tiền tự động
    private String generateMaNap() {
        return "NAP" + System.currentTimeMillis();
    }

    /**
     * Generate mã gói tặng tự động
     */
    private String generateMaGoiTang() {
        return "GT" + System.currentTimeMillis();
    }
}