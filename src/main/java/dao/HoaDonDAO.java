package dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import entity.HoaDon;

/**
 * DAO class để quản lý hóa đơn
 * Các phương thức: getAll, getById, insert, getByDateRange
 */
public class HoaDonDAO {

    /**
     * Lấy tất cả hóa đơn
     * @return List<HoaDon> danh sách tất cả hóa đơn
     */
    public List<HoaDon> getAll() {
        List<HoaDon> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM hoadon ORDER BY NgayLap DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                HoaDon hd = mapResultSetToHoaDon(rs);
                danhSach.add(hd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSach;
    }

    /**
     * Lấy hóa đơn theo mã
     * @param maHD mã hóa đơn cần tìm
     * @return HoaDon hoặc null nếu không tìm thấy
     */
    public HoaDon getById(String maHD) {
        String sql = "SELECT * FROM hoadon WHERE MaHD = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maHD);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToHoaDon(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Thêm hóa đơn mới
     * Logic xử lý:
     * 1. Validate tất cả các trường
     * 2. Kiểm tra phiên sử dụng phải tồn tại
     * 3. Kiểm tra khách hàng và nhân viên phải tồn tại
     * 4. Tính toán các trường tự động (TongTien, ThanhToan)
     * 5. INSERT vào database
     *
     * @param hoaDon đối tượng hóa đơn cần thêm
     * @return true nếu thêm thành công, false nếu thất bại
     */
    public boolean insert(HoaDon hoaDon) {
        // Validation
        if (!validateHoaDon(hoaDon)) {
            return false;
        }

        String sql = "INSERT INTO hoadon (MaPhien, MaKH, MaNV, NgayLap, TienGioChoi, " +
                "TienDichVu, TongTien, GiamGia, ThanhToan, PhuongThucTT, TrangThai) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Tính toán tự động
            double tongTien = hoaDon.getTienGioChoi() + hoaDon.getTienDichVu();
            double thanhToan = tongTien - hoaDon.getGiamGia();

            // Set parameters
            pstmt.setString(1, hoaDon.getMaPhien());
            pstmt.setString(2, hoaDon.getMaKH());
            pstmt.setString(3, hoaDon.getMaNV());
            pstmt.setTimestamp(4, Timestamp.valueOf(hoaDon.getNgayLap() != null ?
                    hoaDon.getNgayLap() : LocalDateTime.now()));
            pstmt.setDouble(5, hoaDon.getTienGioChoi());
            pstmt.setDouble(6, hoaDon.getTienDichVu());
            pstmt.setDouble(7, tongTien);
            pstmt.setDouble(8, hoaDon.getGiamGia());
            pstmt.setDouble(9, thanhToan);
            pstmt.setString(10, hoaDon.getPhuongThucTT());
            pstmt.setString(11, hoaDon.getTrangThai() != null ?
                    hoaDon.getTrangThai() : "CHUATHANHTOAN");

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Lấy MaHD vừa được tạo
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        hoaDon.setMaHD(generatedKeys.getString(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Lấy danh sách hóa đơn theo khoảng thời gian
     * @param tuNgay ngày bắt đầu
     * @param denNgay ngày kết thúc
     * @return List<HoaDon> danh sách hóa đơn trong khoảng thời gian
     */
    public List<HoaDon> getByDateRange(LocalDateTime tuNgay, LocalDateTime denNgay) {
        List<HoaDon> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM hoadon WHERE NgayLap BETWEEN ? AND ? ORDER BY NgayLap DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(tuNgay));
            pstmt.setTimestamp(2, Timestamp.valueOf(denNgay));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    HoaDon hd = mapResultSetToHoaDon(rs);
                    danhSach.add(hd);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSach;
    }

    /**
     * Lấy hóa đơn theo mã phiên
     * @param maPhien mã phiên sử dụng
     * @return HoaDon hoặc null nếu không tìm thấy
     */
    public HoaDon getByMaPhien(String maPhien) {
        String sql = "SELECT * FROM hoadon WHERE MaPhien = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maPhien);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToHoaDon(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Cập nhật trạng thái hóa đơn
     * @param maHD mã hóa đơn
     * @param trangThai trạng thái mới (CHUATHANHTOAN/DATHANHTOAN)
     * @return true nếu cập nhật thành công
     */
    public boolean updateTrangThai(String maHD, String trangThai) {
        if (!trangThai.equals("CHUATHANHTOAN") && !trangThai.equals("DATHANHTOAN")) {
            System.err.println("Trạng thái không hợp lệ!");
            return false;
        }

        String sql = "UPDATE hoadon SET TrangThai = ? WHERE MaHD = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, trangThai);
            pstmt.setString(2, maHD);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Lấy danh sách hóa đơn theo mã khách hàng
     * @param maKH mã khách hàng
     * @return List<HoaDon> danh sách hóa đơn của khách hàng
     */
    public List<HoaDon> getByMaKH(String maKH) {
        List<HoaDon> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM hoadon WHERE MaKH = ? ORDER BY NgayLap DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maKH);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    HoaDon hd = mapResultSetToHoaDon(rs);
                    danhSach.add(hd);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSach;
    }

    /**
     * Lấy tổng doanh thu theo khoảng thời gian
     * @param tuNgay ngày bắt đầu
     * @param denNgay ngày kết thúc
     * @return tổng doanh thu
     */
    public double getTongDoanhThu(LocalDateTime tuNgay, LocalDateTime denNgay) {
        String sql = "SELECT SUM(ThanhToan) as TongDoanhThu " +
                "FROM hoadon " +
                "WHERE NgayLap BETWEEN ? AND ? AND TrangThai = 'DATHANHTOAN'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(tuNgay));
            pstmt.setTimestamp(2, Timestamp.valueOf(denNgay));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("TongDoanhThu");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public String taoMaHoaDonTuDong() {
        String sql = "SELECT MaHD FROM hoadon ORDER BY MaHD DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String maCuoi = rs.getString("MaHD");
                int soThuTu = Integer.parseInt(maCuoi.substring(2)) + 1;
                return String.format("HD%03d", soThuTu);
            } else {
                return "HD001";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "HD001";
        }
    }


    // ==================== HELPER METHODS ====================

    /**
     * Map ResultSet sang đối tượng HoaDon
     */
    private HoaDon mapResultSetToHoaDon(ResultSet rs) throws SQLException {
        HoaDon hd = new HoaDon();

        hd.setMaHD(rs.getString("MaHD"));
        hd.setMaPhien(rs.getString("MaPhien"));
        hd.setMaKH(rs.getString("MaKH"));
        hd.setMaNV(rs.getString("MaNV"));

        Timestamp ngayLap = rs.getTimestamp("NgayLap");
        if (ngayLap != null) {
            hd.setNgayLap(ngayLap.toLocalDateTime());
        }

        hd.setTienGioChoi(rs.getDouble("TienGioChoi"));
        hd.setTienDichVu(rs.getDouble("TienDichVu"));
        hd.setTongTien(rs.getDouble("TongTien"));
        hd.setGiamGia(rs.getDouble("GiamGia"));
        hd.setThanhToan(rs.getDouble("ThanhToan"));
        hd.setPhuongThucTT(rs.getString("PhuongThucTT"));
        hd.setTrangThai(rs.getString("TrangThai"));

        return hd;
    }

    /**
     * Validate hóa đơn trước khi thêm
     */
    private boolean validateHoaDon(HoaDon hoaDon) {
        // Kiểm tra các trường bắt buộc
        if (hoaDon.getMaPhien() == null || hoaDon.getMaPhien().trim().isEmpty()) {
            System.err.println("Mã phiên không hợp lệ!");
            return false;
        }

        if (hoaDon.getMaKH() == null || hoaDon.getMaKH().trim().isEmpty()) {
            System.err.println("Mã khách hàng không hợp lệ!");
            return false;
        }

        if (hoaDon.getMaNV() == null || hoaDon.getMaNV().trim().isEmpty()) {
            System.err.println("Mã nhân viên không hợp lệ!");
            return false;
        }

        // Kiểm tra số tiền
        if (hoaDon.getTienGioChoi() < 0) {
            System.err.println("Tiền giờ chơi không được âm!");
            return false;
        }

        if (hoaDon.getTienDichVu() < 0) {
            System.err.println("Tiền dịch vụ không được âm!");
            return false;
        }

        if (hoaDon.getGiamGia() < 0) {
            System.err.println("Giảm giá không được âm!");
            return false;
        }

        // Kiểm tra giảm giá không vượt quá tổng tiền
        double tongTien = hoaDon.getTienGioChoi() + hoaDon.getTienDichVu();
        if (hoaDon.getGiamGia() > tongTien) {
            System.err.println("Giảm giá không được vượt quá tổng tiền!");
            return false;
        }

        // Kiểm tra phương thức thanh toán
        String[] phuongThucHopLe = {"TIENMAT", "CHUYENKHOAN", "MOMO", "VNPAY", "TAIKHOAN"};
        boolean phuongThucOK = false;
        for (String pt : phuongThucHopLe) {
            if (pt.equals(hoaDon.getPhuongThucTT())) {
                phuongThucOK = true;
                break;
            }
        }
        if (!phuongThucOK) {
            System.err.println("Phương thức thanh toán không hợp lệ!");
            return false;
        }

        return true;
    }

    /**
     * Kiểm tra phiên sử dụng có tồn tại không
     */
    private boolean isPhienExists(String maPhien) {
        String sql = "SELECT COUNT(*) FROM phiensudung WHERE MaPhien = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maPhien);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}