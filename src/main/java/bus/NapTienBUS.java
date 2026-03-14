package bus;

import dao.*;
import entity.*;
import java.time.LocalDateTime;
import java.util.List;

/**Chức năng chính:
 * - Nạp tiền vào tài khoản khách hàng
 * - Áp dụng khuyến mãi tự động
 * - Xem lịch sử nạp tiền
 * - Thống kê nạp tiền
 */
public class NapTienBUS {
    private LichSuNapTienDAO lichSuNapTienDAO;
    private KhachHangDAO khachHangDAO;
    private ChuongTrinhKhuyenMaiDAO khuyenMaiDAO;
    private NhanVienDAO nhanVienDAO;

    public NapTienBUS() {
        this.lichSuNapTienDAO = new LichSuNapTienDAO();
        this.khachHangDAO = new KhachHangDAO();
        this.khuyenMaiDAO = new ChuongTrinhKhuyenMaiDAO();
        this.nhanVienDAO = new NhanVienDAO();
    }

    // ===== 1. NẠP TIỀN =====
    /**Logic:
     * 1. Validate đầu vào
     * 2. Kiểm tra khách hàng tồn tại và đang hoạt động
     * 3. Kiểm tra nhân viên tồn tại (nếu có)
     * 4. Tìm và áp dụng chương trình khuyến mãi tốt nhất (nếu có)
     * 5. Tính toán: KhuyenMai, TongTienCong
     * 6. Lưu số dư trước
     * 7. Cập nhật số dư khách hàng
     * 8. Tạo lịch sử nạp tiền
     */
    public LichSuNapTien napTien(String maKH, double soTienNap, String maCTKM,
                                 String maNV, String phuongThuc, String maGiaoDich) throws Exception {
        // 1. Validate đầu vào
        if (maKH == null || maKH.trim().isEmpty()) {
            throw new Exception("Mã khách hàng không được để trống");
        }

        if (soTienNap <= 0) {
            throw new Exception("Số tiền nạp phải lớn hơn 0");
        }

        if (phuongThuc == null || phuongThuc.trim().isEmpty()) {
            throw new Exception("Phương thức thanh toán không được để trống");
        }

        // Validate phương thức thanh toán
        String[] phuongThucHopLe = {"TIENMAT", "MOMO", "CHUYENKHOAN", "VNPAY", "THE"};
        boolean hopLe = false;
        for (String pt : phuongThucHopLe) {
            if (pt.equals(phuongThuc)) {
                hopLe = true;
                break;
            }
        }
        if (!hopLe) {
            throw new Exception("Phương thức thanh toán không hợp lệ. Chỉ chấp nhận: TIENMAT, MOMO, CHUYENKHOAN, VNPAY, THE");
        }

        // 2. Kiểm tra khách hàng
        KhachHang khachHang = khachHangDAO.getById(maKH);
        if (khachHang == null) {
            throw new Exception("Không tìm thấy khách hàng với mã: " + maKH);
        }
        if (khachHang.isNgung()) {
            throw new Exception("Khách hàng đã bị khóa, không thể nạp tiền");
        }

        // 3. Kiểm tra nhân viên (nếu có)
        if (maNV != null && !maNV.trim().isEmpty()) {
            NhanVien nhanVien = nhanVienDAO.getById(maNV);
            if (nhanVien == null) {
                throw new Exception("Không tìm thấy nhân viên với mã: " + maNV);
            }
            if (nhanVien.isNghiViec()) {
                throw new Exception("Nhân viên đã nghỉ việc, không thể thực hiện giao dịch");
            }
        }

        // 4. Tìm và áp dụng khuyến mãi
        double khuyenMai = 0;
        String maCTKMApDung = null;

        // Nếu có mã khuyến mãi được chỉ định
        if (maCTKM != null && !maCTKM.trim().isEmpty()) {
            // Kiểm tra chương trình còn hiệu lực
            if (!khuyenMaiDAO.kiemTraConHieuLuc(maCTKM)) {
                throw new Exception("Chương trình khuyến mãi không còn hiệu lực hoặc không tồn tại");
            }
            // Kiểm tra điều kiện áp dụng
            if (!khuyenMaiDAO.kiemTraDieuKien(maCTKM, soTienNap)) {
                ChuongTrinhKhuyenMai ctkm = khuyenMaiDAO.timTheoMa(maCTKM);
                throw new Exception(String.format(
                        "Số tiền nạp không đủ điều kiện áp dụng khuyến mãi. Yêu cầu tối thiểu: %,.0f VND",
                        ctkm.getDieuKienToiThieu()
                ));
            }
            // Tính khuyến mãi
            khuyenMai = khuyenMaiDAO.tinhGiaTriKhuyenMai(maCTKM, soTienNap);
            maCTKMApDung = maCTKM;
        } else {
            // Tự động tìm chương trình tốt nhất
            ChuongTrinhKhuyenMai chuongTrinhTotNhat = khuyenMaiDAO.timChuongTrinhTotNhat(soTienNap);
            if (chuongTrinhTotNhat != null) {
                khuyenMai = chuongTrinhTotNhat.tinhKhuyenMai(soTienNap);
                maCTKMApDung = chuongTrinhTotNhat.getMaCTKM();
            }
        }

        // 5. Tính toán
        double tongTienCong = soTienNap + khuyenMai;
        double soDuTruoc = khachHang.getSodu();
        double soDuSau = soDuTruoc + tongTienCong;

        // 6. Cập nhật số dư khách hàng
        khachHang.setSodu(soDuSau);

        // 7. Tạo lịch sử nạp tiền
        LichSuNapTien lichSu = new LichSuNapTien();
        lichSu.setMaNap(lichSuNapTienDAO.taoMaNapTuDong());
        lichSu.setMaKH(maKH);
        lichSu.setMaNV(maNV);
        lichSu.setMaCTKM(maCTKMApDung);
        lichSu.setSoTienNap(soTienNap);
        lichSu.setKhuyenMai(khuyenMai);
        lichSu.setTongTienCong(tongTienCong);
        lichSu.setSoDuTruoc(soDuTruoc);
        lichSu.setSoDuSau(soDuSau);
        lichSu.setPhuongThuc(phuongThuc);
        lichSu.setMaGiaoDich(maGiaoDich);
        lichSu.setNgayNap(LocalDateTime.now());

        // 8. Lưu vào database
        // Sử dụng cách tiếp cận: cập nhật số dư trước, sau đó tạo lịch sử
        boolean capNhatSoDu = capNhatSoDuKhachHang(maKH, soDuSau);
        if (!capNhatSoDu) {
            throw new Exception("Không thể cập nhật số dư khách hàng");
        }

        boolean luuLichSu = lichSuNapTienDAO.them(lichSu);
        if (!luuLichSu) {
            // Rollback: trả lại số dư cũ
            capNhatSoDuKhachHang(maKH, soDuTruoc);
            throw new Exception("Không thể lưu lịch sử nạp tiền");
        }
        return lichSu;
    }

    //Phương thức hỗ trợ cập nhật số dư khách hàng
    private boolean capNhatSoDuKhachHang(String maKH, double soDuMoi) {
        try {
            // Sử dụng SQL trực tiếp để cập nhật số dư
            java.sql.Connection conn = dao.DBConnection.getConnection();
            String sql = "UPDATE khachhang SET SoDu = ? WHERE MaKH = ?";
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, soDuMoi);
            pstmt.setString(2, maKH);
            int rows = pstmt.executeUpdate();
            pstmt.close();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== 2. XEM LỊCH SỬ NẠP TIỀN =====
    public List<LichSuNapTien> layLichSuNap(String maKH) throws Exception {
        if (maKH == null || maKH.trim().isEmpty()) {
            throw new Exception("Mã khách hàng không được để trống");
        }
        return lichSuNapTienDAO.timTheoKhachHang(maKH);
    }

    public List<LichSuNapTien> getLichSuNapTien(String maKH) throws Exception {
        return layLichSuNap(maKH);
    }

    //Tính tiền khuyến mãi
    public double tinhKhuyenMai(double soTien, String maCTKM) throws Exception {
        return tinhTruocKhuyenMai(soTien, maCTKM);
    }

    //Lấy lịch sử nạp tiền theo khoảng thời gian
    public List<LichSuNapTien> layLichSuNapTheoThoiGian(LocalDateTime tuNgay, LocalDateTime denNgay) throws Exception {
        if (tuNgay == null || denNgay == null) {
            throw new Exception("Thời gian không được để trống");
        }

        if (tuNgay.isAfter(denNgay)) {
            throw new Exception("Thời gian bắt đầu phải trước thời gian kết thúc");
        }
        return lichSuNapTienDAO.timTheoKhoangThoiGian(tuNgay, denNgay);
    }

    //Lấy giao dịch nạp gần nhất của khách hàng
    public LichSuNapTien layGiaoDichGanNhat(String maKH) throws Exception {
        if (maKH == null || maKH.trim().isEmpty()) {
            throw new Exception("Mã khách hàng không được để trống");
        }

        return lichSuNapTienDAO.layGiaoDichGanNhat(maKH);
    }

    // ===== 3. THỐNG KÊ NẠP TIỀN =====
    //Thống kê tổng tiền nạp theo thời gian
    public double thongKeTongTienNap(LocalDateTime tuNgay, LocalDateTime denNgay) throws Exception {
        if (tuNgay == null || denNgay == null) {
            throw new Exception("Thời gian không được để trống");
        }

        if (tuNgay.isAfter(denNgay)) {
            throw new Exception("Thời gian bắt đầu phải trước thời gian kết thúc");
        }
        return lichSuNapTienDAO.tongTienNapTheoThoiGian(tuNgay, denNgay);
    }

    //Thống kê tổng khuyến mãi đã tặng theo thời gian
    public double thongKeTongKhuyenMai(LocalDateTime tuNgay, LocalDateTime denNgay) throws Exception {
        if (tuNgay == null || denNgay == null) {
            throw new Exception("Thời gian không được để trống");
        }

        if (tuNgay.isAfter(denNgay)) {
            throw new Exception("Thời gian bắt đầu phải trước thời gian kết thúc");
        }
        return lichSuNapTienDAO.tongKhuyenMaiDaTang(tuNgay, denNgay);
    }

   //Thống kê tổng tiền nạp của một khách hàng
    public double thongKeTongTienNapTheoKhachHang(String maKH) throws Exception {
        if (maKH == null || maKH.trim().isEmpty()) {
            throw new Exception("Mã khách hàng không được để trống");
        }
        return lichSuNapTienDAO.tongTienNapTheoKhachHang(maKH);
    }

    //Lấy danh sách top khách hàng nạp nhiều nhất
    public List<Object[]> topKhachHangNapNhieu(int soLuong, LocalDateTime tuNgay, LocalDateTime denNgay) throws Exception {
        if (soLuong <= 0) {
            throw new Exception("Số lượng phải lớn hơn 0");
        }

        if (tuNgay == null || denNgay == null) {
            throw new Exception("Thời gian không được để trống");
        }

        if (tuNgay.isAfter(denNgay)) {
            throw new Exception("Thời gian bắt đầu phải trước thời gian kết thúc");
        }
        return lichSuNapTienDAO.topKhachHangNapNhieu(soLuong, tuNgay, denNgay);
    }

    //Đếm số lượt nạp theo phương thức
    public int demSoLuotNapTheoPhuongThuc(String phuongThuc, LocalDateTime tuNgay, LocalDateTime denNgay) throws Exception {
        if (phuongThuc == null || phuongThuc.trim().isEmpty()) {
            throw new Exception("Phương thức thanh toán không được để trống");
        }

        if (tuNgay == null || denNgay == null) {
            throw new Exception("Thời gian không được để trống");
        }

        if (tuNgay.isAfter(denNgay)) {
            throw new Exception("Thời gian bắt đầu phải trước thời gian kết thúc");
        }
        return lichSuNapTienDAO.demSoLuotNapTheoPhuongThuc(phuongThuc, tuNgay, denNgay);
    }

    // ===== 4. VALIDATE CHƯƠNG TRÌNH KHUYẾN MÃI =====
   // Lấy danh sách chương trình khuyến mãi phù hợp với số tiền nạp
    public List<ChuongTrinhKhuyenMai> layChuongTrinhPhuHop(double soTienNap) throws Exception {
        if (soTienNap <= 0) {
            throw new Exception("Số tiền nạp phải lớn hơn 0");
        }
        return khuyenMaiDAO.timChuongTrinhPhuHop(soTienNap);
    }

   //Tính trước số tiền khuyến mãi sẽ nhận được
    public double tinhTruocKhuyenMai(double soTienNap, String maCTKM) throws Exception {
        if (soTienNap <= 0) {
            throw new Exception("Số tiền nạp phải lớn hơn 0");
        }

        if (maCTKM != null && !maCTKM.trim().isEmpty()) {
            // Kiểm tra chương trình còn hiệu lực
            if (!khuyenMaiDAO.kiemTraConHieuLuc(maCTKM)) {
                throw new Exception("Chương trình khuyến mãi không còn hiệu lực");
            }

            // Kiểm tra điều kiện
            if (!khuyenMaiDAO.kiemTraDieuKien(maCTKM, soTienNap)) {
                return 0;
            }
            return khuyenMaiDAO.tinhGiaTriKhuyenMai(maCTKM, soTienNap);
        } else {
            // Tìm chương trình tốt nhất
            ChuongTrinhKhuyenMai chuongTrinh = khuyenMaiDAO.timChuongTrinhTotNhat(soTienNap);
            if (chuongTrinh != null) {
                return chuongTrinh.tinhKhuyenMai(soTienNap);
            }
            return 0;
        }
    }
    //Lấy chương trình khuyến mãi tốt nhất cho số tiền nạp
    public ChuongTrinhKhuyenMai layChuongTrinhTotNhat(double soTienNap) throws Exception {
        if (soTienNap <= 0) {
            throw new Exception("Số tiền nạp phải lớn hơn 0");
        }
        return khuyenMaiDAO.timChuongTrinhTotNhat(soTienNap);
    }
}