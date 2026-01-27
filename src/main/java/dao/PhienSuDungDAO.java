package dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import entity.PhienSuDung;

/**
 * DAO class để quản lý phiên sử dụng
 * Các phương thức: getAll, getById, getByMay, insert, update, ketThucPhien
 */
public class PhienSuDungDAO {

    /**
     * Lấy tất cả phiên sử dụng
     * @return List<PhienSuDung> danh sách tất cả phiên
     */
    public List<PhienSuDung> getAll() {
        List<PhienSuDung> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM phiensudung ORDER BY GioBatDau DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                PhienSuDung phien = mapResultSetToPhienSuDung(rs);
                danhSach.add(phien);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSach;
    }

    /**
     * Lấy phiên sử dụng theo mã
     * @param maPhien mã phiên cần tìm
     * @return PhienSuDung hoặc null nếu không tìm thấy
     */
    public PhienSuDung getById(String maPhien) {
        String sql = "SELECT * FROM phiensudung WHERE MaPhien = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maPhien);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPhienSuDung(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Lấy phiên sử dụng theo mã máy
     * @param maMay mã máy
     * @return List<PhienSuDung> danh sách phiên của máy
     */
    public List<PhienSuDung> getByMay(String maMay) {
        List<PhienSuDung> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM phiensudung WHERE MaMay = ? ORDER BY GioBatDau DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maMay);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PhienSuDung phien = mapResultSetToPhienSuDung(rs);
                    danhSach.add(phien);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSach;
    }

    /**
     * Lấy phiên đang chơi của máy
     * @param maMay mã máy
     * @return PhienSuDung đang chơi hoặc null
     */
    public PhienSuDung getPhienDangChoiByMay(String maMay) {
        String sql = "SELECT * FROM phiensudung WHERE MaMay = ? AND TrangThai = 'DANGCHOI'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maMay);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPhienSuDung(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Lấy phiên đang chơi của khách hàng
     * @param maKH mã khách hàng
     * @return PhienSuDung đang chơi hoặc null
     */
    public PhienSuDung getPhienDangChoiByKH(String maKH) {
        String sql = "SELECT * FROM phiensudung WHERE MaKH = ? AND TrangThai = 'DANGCHOI'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maKH);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPhienSuDung(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Mở phiên chơi mới (insert)
     * Logic xử lý:
     * 1. Validate tất cả các điều kiện
     * 2. Kiểm tra máy phải đang TRONG (không ai dùng)
     * 3. Kiểm tra khách hàng không có phiên DANGCHOI khác
     * 4. Kiểm tra khách hàng có đủ tiền hoặc gói
     * 5. Lấy giá máy tại thời điểm hiện tại (snapshot)
     * 6. Xác định loại thanh toán (GOI/TAIKHOAN/KETHOP)
     * 7. INSERT phiên mới
     * 8. UPDATE trạng thái máy sang DANGDUNG
     *
     * @param phien đối tượng phiên sử dụng
     * @return true nếu mở phiên thành công
     */
    public boolean insert(PhienSuDung phien) {
        Connection con = null;
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtUpdateMay = null;

        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            // 1. Validate cơ bản
            if (!validatePhienSuDung(phien)) {
                con.rollback();
                return false;
            }

            // 2. Kiểm tra máy có trống không
            if (!isMayTrong(con, phien.getMaMay())) {
                System.err.println("Máy đang được sử dụng!");
                con.rollback();
                return false;
            }

            // 3. Kiểm tra khách hàng có phiên đang chơi không
            if (hasPhienDangChoi(con, phien.getMaKH())) {
                System.err.println("Khách hàng đang có phiên chơi khác!");
                con.rollback();
                return false;
            }

            // 4. Lấy giá máy hiện tại (snapshot)
            double giaMay = getGiaMay(con, phien.getMaMay());
            if (giaMay < 0) {
                System.err.println("Không lấy được giá máy!");
                con.rollback();
                return false;
            }
            phien.setGiaMoiGio(giaMay);

            // 5. Xác định loại thanh toán và kiểm tra điều kiện
            String loaiTT = xacDinhLoaiThanhToan(con, phien);
            if (loaiTT == null) {
                con.rollback();
                return false;
            }
            phien.setLoaiThanhToan(loaiTT);

            // 6. Insert phiên mới
            String sqlInsert = "INSERT INTO phiensudung " +
                    "(MaKH, MaMay, MaNV, MaGoiKH, GioBatDau, TongGio, " +
                    "GioSuDungTuGoi, GioSuDungTuTaiKhoan, GiaMoiGio, TienGioChoi, " +
                    "LoaiThanhToan, TrangThai) " +
                    "VALUES (?, ?, ?, ?, ?, 0, 0, 0, ?, 0, ?, 'DANGCHOI')";

            pstmtInsert = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            pstmtInsert.setString(1, phien.getMaKH());
            pstmtInsert.setString(2, phien.getMaMay());

            if (phien.getMaNV() != null) {
                pstmtInsert.setString(3, phien.getMaNV());
            } else {
                pstmtInsert.setNull(3, Types.VARCHAR);
            }

            if (phien.getMaGoiKH() != null) {
                pstmtInsert.setString(4, phien.getMaGoiKH());
            } else {
                pstmtInsert.setNull(4, Types.VARCHAR);
            }

            pstmtInsert.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            pstmtInsert.setDouble(6, phien.getGiaMoiGio());
            pstmtInsert.setString(7, phien.getLoaiThanhToan());

            int affectedRows = pstmtInsert.executeUpdate();

            if (affectedRows > 0) {
                // Lấy MaPhien vừa tạo
                try (ResultSet generatedKeys = pstmtInsert.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        phien.setMaPhien(generatedKeys.getString(1));
                    }
                }

                // 7. Cập nhật trạng thái máy
                String sqlUpdateMay = "UPDATE maytinh SET TrangThai = 'DANGDUNG' WHERE MaMay = ?";
                pstmtUpdateMay = con.prepareStatement(sqlUpdateMay);
                pstmtUpdateMay.setString(1, phien.getMaMay());
                pstmtUpdateMay.executeUpdate();

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
                if (pstmtInsert != null) pstmtInsert.close();
                if (pstmtUpdateMay != null) pstmtUpdateMay.close();
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
     * Kết thúc phiên chơi
     * Logic xử lý:
     * 1. Kiểm tra phiên có tồn tại và đang DANGCHOI
     * 2. Tính tổng giờ đã chơi
     * 3. Phân bổ giờ: ưu tiên dùng gói trước (nếu có), sau đó dùng tiền
     * 4. Tính tiền giờ chơi = giờ dùng từ tài khoản * giá
     * 5. Cập nhật số dư khách hàng (trừ tiền)
     * 6. Cập nhật số giờ còn lại trong gói (nếu có dùng)
     * 7. UPDATE phiên: trạng thái, giờ kết thúc, các trường tính toán
     * 8. UPDATE máy: trạng thái về TRONG
     * 9. Tạo hóa đơn tự động
     *
     * @param maPhien mã phiên cần kết thúc
     * @return true nếu kết thúc thành công
     */
    public boolean ketThucPhien(String maPhien) {
        Connection con = null;
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtUpdateMay = null;
        PreparedStatement pstmtUpdateKH = null;
        PreparedStatement pstmtUpdateGoi = null;

        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            // 1. Lấy thông tin phiên
            PhienSuDung phien = getByIdWithConnection(con, maPhien);
            if (phien == null) {
                System.err.println("Không tìm thấy phiên!");
                con.rollback();
                return false;
            }

            if (!phien.isDangChoi()) {
                System.err.println("Phiên không trong trạng thái DANGCHOI!");
                con.rollback();
                return false;
            }

            // 2. Tính tổng giờ đã chơi
            LocalDateTime gioKetThuc = LocalDateTime.now();
            long phut = ChronoUnit.MINUTES.between(phien.getGioBatDau(), gioKetThuc);
            double tongGio = phut / 60.0;

            // 3. Phân bổ giờ: ưu tiên dùng gói
            double gioTuGoi = 0;
            double gioTuTaiKhoan = 0;

            if (phien.getMaGoiKH() != null) {
                double gioConLaiTrongGoi = getSoGioConLaiTrongGoi(con, phien.getMaGoiKH());
                gioTuGoi = Math.min(tongGio, gioConLaiTrongGoi);
                gioTuTaiKhoan = tongGio - gioTuGoi;
            } else {
                gioTuTaiKhoan = tongGio;
            }

            // 4. Tính tiền giờ chơi
            double tienGioChoi = gioTuTaiKhoan * phien.getGiaMoiGio();

            // 5. Kiểm tra số dư khách hàng
            double soDuKH = getSoDuKhachHang(con, phien.getMaKH());
            if (soDuKH < tienGioChoi) {
                System.err.println("Số dư không đủ! Cần: " + tienGioChoi + ", Có: " + soDuKH);
                con.rollback();
                return false;
            }

            // 6. Cập nhật số dư khách hàng
            if (tienGioChoi > 0) {
                String sqlUpdateKH = "UPDATE khachhang SET SoDu = SoDu - ? WHERE MaKH = ?";
                pstmtUpdateKH = con.prepareStatement(sqlUpdateKH);
                pstmtUpdateKH.setDouble(1, tienGioChoi);
                pstmtUpdateKH.setString(2, phien.getMaKH());
                pstmtUpdateKH.executeUpdate();
            }

            // 7. Cập nhật gói (nếu có dùng)
            if (gioTuGoi > 0 && phien.getMaGoiKH() != null) {
                String sqlUpdateGoi = "UPDATE goidichvu_khachhang " +
                        "SET SoGioConLai = SoGioConLai - ? WHERE MaGoiKH = ?";
                pstmtUpdateGoi = con.prepareStatement(sqlUpdateGoi);
                pstmtUpdateGoi.setDouble(1, gioTuGoi);
                pstmtUpdateGoi.setString(2, phien.getMaGoiKH());
                pstmtUpdateGoi.executeUpdate();

                // Cập nhật trạng thái gói nếu hết giờ
                capNhatTrangThaiGoi(con, phien.getMaGoiKH());
            }

            // 8. Cập nhật phiên
            String sqlUpdate = "UPDATE phiensudung SET " +
                    "GioKetThuc = ?, TongGio = ?, GioSuDungTuGoi = ?, " +
                    "GioSuDungTuTaiKhoan = ?, TienGioChoi = ?, TrangThai = 'DAKETTHUC' " +
                    "WHERE MaPhien = ?";

            pstmtUpdate = con.prepareStatement(sqlUpdate);
            pstmtUpdate.setTimestamp(1, Timestamp.valueOf(gioKetThuc));
            pstmtUpdate.setDouble(2, tongGio);
            pstmtUpdate.setDouble(3, gioTuGoi);
            pstmtUpdate.setDouble(4, gioTuTaiKhoan);
            pstmtUpdate.setDouble(5, tienGioChoi);
            pstmtUpdate.setString(6, maPhien);

            pstmtUpdate.executeUpdate();

            // 9. Cập nhật trạng thái máy
            String sqlUpdateMay = "UPDATE maytinh SET TrangThai = 'TRONG' WHERE MaMay = ?";
            pstmtUpdateMay = con.prepareStatement(sqlUpdateMay);
            pstmtUpdateMay.setString(1, phien.getMaMay());
            pstmtUpdateMay.executeUpdate();

            con.commit();
            return true;

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
                if (pstmtUpdate != null) pstmtUpdate.close();
                if (pstmtUpdateMay != null) pstmtUpdateMay.close();
                if (pstmtUpdateKH != null) pstmtUpdateKH.close();
                if (pstmtUpdateGoi != null) pstmtUpdateGoi.close();
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
     * Lấy danh sách phiên đã kết thúc theo khoảng thời gian
     */
    public List<PhienSuDung> getByDateRange(LocalDateTime tuNgay, LocalDateTime denNgay) {
        List<PhienSuDung> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM phiensudung " +
                "WHERE GioBatDau BETWEEN ? AND ? ORDER BY GioBatDau DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(tuNgay));
            pstmt.setTimestamp(2, Timestamp.valueOf(denNgay));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PhienSuDung phien = mapResultSetToPhienSuDung(rs);
                    danhSach.add(phien);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSach;
    }

    /**
     * Lấy lịch sử phiên của khách hàng
     */
    public List<PhienSuDung> getLichSuPhienByKH(String maKH) {
        List<PhienSuDung> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM phiensudung WHERE MaKH = ? ORDER BY GioBatDau DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maKH);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PhienSuDung phien = mapResultSetToPhienSuDung(rs);
                    danhSach.add(phien);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSach;
    }

    // ==================== HELPER METHODS ====================

    /**
     * Map ResultSet sang PhienSuDung
     */
    private PhienSuDung mapResultSetToPhienSuDung(ResultSet rs) throws SQLException {
        PhienSuDung phien = new PhienSuDung();

        phien.setMaPhien(rs.getString("MaPhien"));
        phien.setMaKH(rs.getString("MaKH"));
        phien.setMaMay(rs.getString("MaMay"));

        String maNV = rs.getString("MaNV");
        phien.setMaNV(rs.wasNull() ? null : maNV);

        String maGoiKH = rs.getString("MaGoiKH");
        phien.setMaGoiKH(rs.wasNull() ? null : maGoiKH);

        Timestamp gioBD = rs.getTimestamp("GioBatDau");
        if (gioBD != null) {
            phien.setGioBatDau(gioBD.toLocalDateTime());
        }

        Timestamp gioKT = rs.getTimestamp("GioKetThuc");
        if (gioKT != null) {
            phien.setGioKetThuc(gioKT.toLocalDateTime());
        }

        phien.setTongGio(rs.getDouble("TongGio"));
        phien.setGioSuDungTuGoi(rs.getDouble("GioSuDungTuGoi"));
        phien.setGioSuDungTuTaiKhoan(rs.getDouble("GioSuDungTuTaiKhoan"));
        phien.setGiaMoiGio(rs.getDouble("GiaMoiGio"));
        phien.setTienGioChoi(rs.getDouble("TienGioChoi"));
        phien.setLoaiThanhToan(rs.getString("LoaiThanhToan"));
        phien.setTrangThai(rs.getString("TrangThai"));

        return phien;
    }

    /**
     * Lấy phiên theo ID với connection có sẵn (dùng trong transaction)
     */
    private PhienSuDung getByIdWithConnection(Connection con, String maPhien) throws SQLException {
        String sql = "SELECT * FROM phiensudung WHERE MaPhien = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, maPhien);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPhienSuDung(rs);
                }
            }
        }

        return null;
    }

    /**
     * Validate phiên sử dụng
     */
    private boolean validatePhienSuDung(PhienSuDung phien) {
        if (phien.getMaKH() == null || phien.getMaKH().trim().isEmpty()) {
            System.err.println("Mã khách hàng không hợp lệ!");
            return false;
        }

        if (phien.getMaMay() == null || phien.getMaMay().trim().isEmpty()) {
            System.err.println("Mã máy không hợp lệ!");
            return false;
        }

        return true;
    }

    /**
     * Kiểm tra máy có trống không
     */
    private boolean isMayTrong(Connection con, String maMay) throws SQLException {
        String sql = "SELECT TrangThai FROM maytinh WHERE MaMay = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, maMay);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String trangThai = rs.getString("TrangThai");
                    return "TRONG".equals(trangThai);
                }
            }
        }

        return false;
    }

    /**
     * Kiểm tra khách hàng có phiên đang chơi không
     */
    private boolean hasPhienDangChoi(Connection con, String maKH) throws SQLException {
        String sql = "SELECT COUNT(*) FROM phiensudung WHERE MaKH = ? AND TrangThai = 'DANGCHOI'";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, maKH);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    /**
     * Lấy giá máy hiện tại
     */
    private double getGiaMay(Connection con, String maMay) throws SQLException {
        String sql = "SELECT GiaMoiGio FROM maytinh WHERE MaMay = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, maMay);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("GiaMoiGio");
                }
            }
        }

        return -1;
    }

    /**
     * Xác định loại thanh toán và kiểm tra điều kiện
     */
    private String xacDinhLoaiThanhToan(Connection con, PhienSuDung phien) throws SQLException {
        // Kiểm tra có gói không
        boolean coGoi = false;
        if (phien.getMaGoiKH() != null) {
            double gioConLai = getSoGioConLaiTrongGoi(con, phien.getMaGoiKH());
            coGoi = gioConLai > 0;
        }

        // Kiểm tra số dư
        double soDu = getSoDuKhachHang(con, phien.getMaKH());

        if (coGoi && soDu > 0) {
            return "KETHOP";  // Có cả gói và tiền
        } else if (coGoi) {
            return "GOI";  // Chỉ có gói
        } else if (soDu > 0) {
            return "TAIKHOAN";  // Chỉ có tiền
        } else {
            System.err.println("Khách hàng không có gói và số dư = 0!");
            return null;
        }
    }

    /**
     * Lấy số giờ còn lại trong gói
     */
    private double getSoGioConLaiTrongGoi(Connection con, String maGoiKH) throws SQLException {
        String sql = "SELECT SoGioConLai FROM goidichvu_khachhang " +
                "WHERE MaGoiKH = ? AND TrangThai = 'CONHAN'";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, maGoiKH);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("SoGioConLai");
                }
            }
        }

        return 0;
    }

    /**
     * Lấy số dư khách hàng
     */
    private double getSoDuKhachHang(Connection con, String maKH) throws SQLException {
        String sql = "SELECT SoDu FROM khachhang WHERE MaKH = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, maKH);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("SoDu");
                }
            }
        }

        return 0;
    }

    /**
     * Cập nhật trạng thái gói nếu hết giờ
     */
    private void capNhatTrangThaiGoi(Connection con, String maGoiKH) throws SQLException {
        String sql = "UPDATE goidichvu_khachhang SET TrangThai = 'DAHETGIO' " +
                "WHERE MaGoiKH = ? AND SoGioConLai <= 0";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, maGoiKH);
            pstmt.executeUpdate();
        }
    }
}