package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import entity.ChiTietHoaDon;

/**
 * DAO class để quản lý chi tiết hóa đơn
 * Các phương thức: getByHoaDon, insert
 */
public class ChiTietHoaDonDAO {

    /**
     * Lấy danh sách chi tiết hóa đơn theo mã hóa đơn
     * @param maHD mã hóa đơn
     * @return List<ChiTietHoaDon> danh sách chi tiết của hóa đơn
     */
    public List<ChiTietHoaDon> getByHoaDon(String maHD) {
        List<ChiTietHoaDon> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM chitiethoadon WHERE MaHD = ? ORDER BY MaCTHD";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maHD);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ChiTietHoaDon cthd = mapResultSetToChiTietHoaDon(rs);
                    danhSach.add(cthd);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSach;
    }

    /**
     * Thêm chi tiết hóa đơn mới
     * Logic xử lý:
     * 1. Validate tất cả các trường
     * 2. Kiểm tra hóa đơn phải tồn tại
     * 3. Kiểm tra LoaiChiTiet phải hợp lệ (GIOCHOI hoặc DICHVU)
     * 4. Tính ThanhTien = SoLuong * DonGia
     * 5. INSERT vào database
     * 6. Cập nhật lại tổng tiền trong bảng hoadon
     *
     * @param chiTiet đối tượng chi tiết hóa đơn cần thêm
     * @return true nếu thêm thành công, false nếu thất bại
     */
    public boolean insert(ChiTietHoaDon chiTiet) {
        // Validation
        if (!validateChiTietHoaDon(chiTiet)) {
            return false;
        }

        Connection con = null;
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtUpdate = null;

        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false); // Bắt đầu transaction

            // 1. Insert chi tiết hóa đơn
            String sqlInsert = "INSERT INTO chitiethoadon (MaHD, LoaiChiTiet, MoTa, " +
                    "SoLuong, DonGia, ThanhTien) VALUES (?, ?, ?, ?, ?, ?)";

            pstmtInsert = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);

            // Tính thành tiền
            double thanhTien = chiTiet.getSoLuong() * chiTiet.getDonGia();

            pstmtInsert.setString(1, chiTiet.getMaHD());
            pstmtInsert.setString(2, chiTiet.getLoaiChiTiet());
            pstmtInsert.setString(3, chiTiet.getMoTa());
            pstmtInsert.setDouble(4, chiTiet.getSoLuong());
            pstmtInsert.setDouble(5, chiTiet.getDonGia());
            pstmtInsert.setDouble(6, thanhTien);

            int affectedRows = pstmtInsert.executeUpdate();

            if (affectedRows > 0) {
                // Lấy MaCTHD vừa được tạo
                try (ResultSet generatedKeys = pstmtInsert.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        chiTiet.setMaCTHD(generatedKeys.getString(1));
                    }
                }

                // 2. Cập nhật tổng tiền trong hóa đơn
                String sqlUpdate = "UPDATE hoadon SET " +
                        "TienGioChoi = (SELECT IFNULL(SUM(ThanhTien), 0) " +
                        "               FROM chitiethoadon " +
                        "               WHERE MaHD = ? AND LoaiChiTiet = 'GIOCHOI'), " +
                        "TienDichVu = (SELECT IFNULL(SUM(ThanhTien), 0) " +
                        "              FROM chitiethoadon " +
                        "              WHERE MaHD = ? AND LoaiChiTiet = 'DICHVU'), " +
                        "TongTien = TienGioChoi + TienDichVu, " +
                        "ThanhToan = TongTien - GiamGia " +
                        "WHERE MaHD = ?";

                pstmtUpdate = con.prepareStatement(sqlUpdate);
                pstmtUpdate.setString(1, chiTiet.getMaHD());
                pstmtUpdate.setString(2, chiTiet.getMaHD());
                pstmtUpdate.setString(3, chiTiet.getMaHD());

                pstmtUpdate.executeUpdate();

                con.commit(); // Commit transaction
                return true;
            }

            con.rollback();
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            try {
                if (pstmtInsert != null) pstmtInsert.close();
                if (pstmtUpdate != null) pstmtUpdate.close();
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Lấy tất cả chi tiết hóa đơn
     * @return List<ChiTietHoaDon> danh sách tất cả chi tiết hóa đơn
     */
    public List<ChiTietHoaDon> getAll() {
        List<ChiTietHoaDon> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM chitiethoadon ORDER BY MaHD, MaCTHD";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                ChiTietHoaDon cthd = mapResultSetToChiTietHoaDon(rs);
                danhSach.add(cthd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSach;
    }

    /**
     * Lấy chi tiết hóa đơn theo mã
     * @param maCTHD mã chi tiết hóa đơn
     * @return ChiTietHoaDon hoặc null nếu không tìm thấy
     */
    public ChiTietHoaDon getById(String maCTHD) {
        String sql = "SELECT * FROM chitiethoadon WHERE MaCTHD = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maCTHD);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToChiTietHoaDon(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Lấy danh sách chi tiết theo loại (GIOCHOI hoặc DICHVU)
     * @param maHD mã hóa đơn
     * @param loaiChiTiet loại chi tiết (GIOCHOI/DICHVU)
     * @return List<ChiTietHoaDon> danh sách chi tiết theo loại
     */
    public List<ChiTietHoaDon> getByLoaiChiTiet(String maHD, String loaiChiTiet) {
        List<ChiTietHoaDon> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM chitiethoadon WHERE MaHD = ? AND LoaiChiTiet = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maHD);
            pstmt.setString(2, loaiChiTiet);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ChiTietHoaDon cthd = mapResultSetToChiTietHoaDon(rs);
                    danhSach.add(cthd);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSach;
    }

    /**
     * Xóa chi tiết hóa đơn
     * Chỉ cho phép xóa khi hóa đơn chưa thanh toán
     * @param maCTHD mã chi tiết hóa đơn cần xóa
     * @return true nếu xóa thành công
     */
    public boolean delete(String maCTHD) {
        Connection con = null;
        PreparedStatement pstmtCheck = null;
        PreparedStatement pstmtDelete = null;
        PreparedStatement pstmtUpdate = null;

        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            // 1. Kiểm tra hóa đơn chưa thanh toán
            String sqlCheck = "SELECT h.TrangThai, c.MaHD " +
                    "FROM chitiethoadon c " +
                    "JOIN hoadon h ON c.MaHD = h.MaHD " +
                    "WHERE c.MaCTHD = ?";

            pstmtCheck = con.prepareStatement(sqlCheck);
            pstmtCheck.setString(1, maCTHD);

            String maHD = null;
            try (ResultSet rs = pstmtCheck.executeQuery()) {
                if (rs.next()) {
                    String trangThai = rs.getString("TrangThai");
                    if ("DATHANHTOAN".equals(trangThai)) {
                        System.err.println("Không thể xóa chi tiết của hóa đơn đã thanh toán!");
                        con.rollback();
                        return false;
                    }
                    maHD = rs.getString("MaHD");
                } else {
                    System.err.println("Không tìm thấy chi tiết hóa đơn!");
                    con.rollback();
                    return false;
                }
            }

            // 2. Xóa chi tiết
            String sqlDelete = "DELETE FROM chitiethoadon WHERE MaCTHD = ?";
            pstmtDelete = con.prepareStatement(sqlDelete);
            pstmtDelete.setString(1, maCTHD);

            int affectedRows = pstmtDelete.executeUpdate();

            if (affectedRows > 0) {
                // 3. Cập nhật lại tổng tiền trong hóa đơn
                String sqlUpdate = "UPDATE hoadon SET " +
                        "TienGioChoi = (SELECT IFNULL(SUM(ThanhTien), 0) " +
                        "               FROM chitiethoadon " +
                        "               WHERE MaHD = ? AND LoaiChiTiet = 'GIOCHOI'), " +
                        "TienDichVu = (SELECT IFNULL(SUM(ThanhTien), 0) " +
                        "              FROM chitiethoadon " +
                        "              WHERE MaHD = ? AND LoaiChiTiet = 'DICHVU'), " +
                        "TongTien = TienGioChoi + TienDichVu, " +
                        "ThanhToan = TongTien - GiamGia " +
                        "WHERE MaHD = ?";

                pstmtUpdate = con.prepareStatement(sqlUpdate);
                pstmtUpdate.setString(1, maHD);
                pstmtUpdate.setString(2, maHD);
                pstmtUpdate.setString(3, maHD);

                pstmtUpdate.executeUpdate();

                con.commit();
                return true;
            }

            con.rollback();
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            try {
                if (pstmtCheck != null) pstmtCheck.close();
                if (pstmtDelete != null) pstmtDelete.close();
                if (pstmtUpdate != null) pstmtUpdate.close();
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Tính tổng tiền chi tiết theo hóa đơn
     * @param maHD mã hóa đơn
     * @return tổng tiền của tất cả chi tiết
     */
    public double getTongTienChiTiet(String maHD) {
        String sql = "SELECT SUM(ThanhTien) as TongTien FROM chitiethoadon WHERE MaHD = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maHD);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("TongTien");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // ==================== HELPER METHODS ====================

    /**
     * Map ResultSet sang đối tượng ChiTietHoaDon
     */
    private ChiTietHoaDon mapResultSetToChiTietHoaDon(ResultSet rs) throws SQLException {
        ChiTietHoaDon cthd = new ChiTietHoaDon();

        cthd.setMaCTHD(rs.getString("MaCTHD"));
        cthd.setMaHD(rs.getString("MaHD"));
        cthd.setLoaiChiTiet(rs.getString("LoaiChiTiet"));
        cthd.setMoTa(rs.getString("MoTa"));
        cthd.setSoLuong(rs.getDouble("SoLuong"));
        cthd.setDonGia(rs.getDouble("DonGia"));
        cthd.setThanhTien(rs.getDouble("ThanhTien"));

        return cthd;
    }

    /**
     * Validate chi tiết hóa đơn trước khi thêm
     */
    private boolean validateChiTietHoaDon(ChiTietHoaDon chiTiet) {
        // Kiểm tra mã hóa đơn
        if (chiTiet.getMaHD() == null || chiTiet.getMaHD().trim().isEmpty()) {
            System.err.println("Mã hóa đơn không hợp lệ!");
            return false;
        }

        // Kiểm tra loại chi tiết
        if (!"GIOCHOI".equals(chiTiet.getLoaiChiTiet()) &&
                !"DICHVU".equals(chiTiet.getLoaiChiTiet())) {
            System.err.println("Loại chi tiết phải là GIOCHOI hoặc DICHVU!");
            return false;
        }

        // Kiểm tra mô tả
        if (chiTiet.getMoTa() == null || chiTiet.getMoTa().trim().isEmpty()) {
            System.err.println("Mô tả không được rỗng!");
            return false;
        }

        // Kiểm tra số lượng
        if (chiTiet.getSoLuong() <= 0) {
            System.err.println("Số lượng phải lớn hơn 0!");
            return false;
        }

        // Kiểm tra đơn giá
        if (chiTiet.getDonGia() < 0) {
            System.err.println("Đơn giá không được âm!");
            return false;
        }

        // Kiểm tra hóa đơn có tồn tại và chưa thanh toán
        if (!isHoaDonValid(chiTiet.getMaHD())) {
            System.err.println("Hóa đơn không tồn tại hoặc đã thanh toán!");
            return false;
        }

        return true;
    }

    /**
     * Kiểm tra hóa đơn có tồn tại và chưa thanh toán
     */
    private boolean isHoaDonValid(String maHD) {
        String sql = "SELECT TrangThai FROM hoadon WHERE MaHD = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maHD);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String trangThai = rs.getString("TrangThai");
                    // Chỉ cho phép thêm chi tiết cho hóa đơn chưa thanh toán
                    return "CHUATHANHTOAN".equals(trangThai);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}