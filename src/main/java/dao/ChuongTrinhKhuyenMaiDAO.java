package dao;

import entity.ChuongTrinhKhuyenMai;

import java.sql.*;
import java.util.ArrayList;

public class ChuongTrinhKhuyenMaiDAO {
    public ArrayList<ChuongTrinhKhuyenMai> getAll() {
        ArrayList<ChuongTrinhKhuyenMai> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM ChuongTrinhKhuyenMai";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ChuongTrinhKhuyenMai ctkm = new ChuongTrinhKhuyenMai(
                        rs.getString("maCTKM"),
                        rs.getString("tenCT"),
                        rs.getString("loaiKM"),
                        rs.getDouble("giaTriKM"),
                        rs.getDouble("dieuKienToiThieu"),
                        rs.getDate("ngayBatDau").toLocalDate(),
                        rs.getDate("ngayKetThuc").toLocalDate(),
                        rs.getString("trangThai"));
                danhSach.add(ctkm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public ChuongTrinhKhuyenMai getByID(String maCTKM) {
        ChuongTrinhKhuyenMai ctkm = null;
        String sql = "SELECT * FROM ChuongTrinhKhuyenMai WHERE maCTKM = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maCTKM);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                ctkm = new ChuongTrinhKhuyenMai(
                        rs.getString("maCTKM"),
                        rs.getString("tenCT"),
                        rs.getString("loaiKM"),
                        rs.getDouble("giaTriKM"),
                        rs.getDouble("dieuKienToiThieu"),
                        rs.getDate("ngayBatDau").toLocalDate(),
                        rs.getDate("ngayKetThuc").toLocalDate(),
                        rs.getString("trangThai"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ctkm;
    }

    public boolean insert(ChuongTrinhKhuyenMai ctkm) {
        String sql = "INSERT INTO ChuongTrinhKhuyenMai (maCTKM, tenCT, loaiKM, giaTriKM, dieuKienToiThieu, ngayBatDau, ngayKetThuc, trangThai) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ctkm.getMaCTKM());
            pstmt.setString(2, ctkm.getTenCT());
            pstmt.setString(3, ctkm.getLoaiKM());
            pstmt.setDouble(4, ctkm.getGiaTriKM());
            pstmt.setDouble(5, ctkm.getDieuKienToiThieu());
            pstmt.setDate(6, Date.valueOf(ctkm.getNgayBatDau()));
            pstmt.setDate(7, Date.valueOf(ctkm.getNgayKetThuc()));
            pstmt.setString(8, ctkm.getTrangThai());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(ChuongTrinhKhuyenMai ctkm) {
        String sql = "UPDATE ChuongTrinhKhuyenMai "
                + "SET tenCT = ?, loaiKM = ?, giaTriKM = ?, dieuKienToiThieu = ?, ngayBatDau = ?, ngayKetThuc = ?, trangThai = ? "
                + "WHERE maCTKM = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ctkm.getTenCT());
            pstmt.setString(2, ctkm.getLoaiKM());
            pstmt.setDouble(3, ctkm.getGiaTriKM());
            pstmt.setDouble(4, ctkm.getDieuKienToiThieu());
            pstmt.setDate(5, Date.valueOf(ctkm.getNgayBatDau()));
            pstmt.setDate(6, Date.valueOf(ctkm.getNgayKetThuc()));
            pstmt.setString(7, ctkm.getTrangThai());
            pstmt.setString(8, ctkm.getMaCTKM());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maCTKM) {
        String sql = "DELETE FROM ChuongTrinhKhuyenMai WHERE maCTKM = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maCTKM);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<ChuongTrinhKhuyenMai> getConHieuLuc() {
        ArrayList<ChuongTrinhKhuyenMai> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM ChuongTrinhKhuyenMai "
                + "WHERE trangThai = 'Hoạt động' "
                + "AND CURDATE() BETWEEN ngayBatDau AND ngayKetThuc";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ChuongTrinhKhuyenMai ctkm = new ChuongTrinhKhuyenMai(
                        rs.getString("maCTKM"),
                        rs.getString("tenCT"),
                        rs.getString("loaiKM"),
                        rs.getDouble("giaTriKM"),
                        rs.getDouble("dieuKienToiThieu"),
                        rs.getDate("ngayBatDau").toLocalDate(),
                        rs.getDate("ngayKetThuc").toLocalDate(),
                        rs.getString("trangThai"));
                danhSach.add(ctkm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public String generateMaCTKM() {
        String sql = "SELECT maCTKM FROM ChuongTrinhKhuyenMai ORDER BY maCTKM DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                String lastId = rs.getString("maCTKM");
                int num = Integer.parseInt(lastId.replace("KM", "")) + 1;
                return String.format("KM%03d", num);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "KM001";
    }

    public void tuDongCapNhatHetHan() {
        String sql = "UPDATE ChuongTrinhKhuyenMai SET trangThai='HETHAN' " +
                "WHERE ngayKetThuc < CURDATE() AND trangThai != 'HETHAN'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}