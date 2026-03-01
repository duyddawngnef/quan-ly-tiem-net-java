package dao;

import entity.SuDungDichVu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SuDungDichVuDAO {

    // Tạo mã tự động: SD001, SD002, ...
    public String generateId() {
        String sql = "SELECT MaSD FROM sudungdichvu ORDER BY MaSD DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString("MaSD");
                int num = Integer.parseInt(lastId.replace("SD", "")) + 1;
                return String.format("SD%03d", num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "SD001";
    }

    // Lấy theo mã
    public SuDungDichVu getById(String maSD) {
        String sql = "SELECT * FROM sudungdichvu WHERE MaSD = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maSD);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new SuDungDichVu(
                        rs.getString("MaSD"),
                        rs.getString("MaPhien"),
                        rs.getString("MaDV"),
                        rs.getInt("SoLuong"),
                        rs.getDouble("DonGia"),
                        rs.getDouble("ThanhTien"),
                        rs.getTimestamp("ThoiGian").toLocalDateTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(SuDungDichVu sd) {
        String sql = """
                    INSERT INTO sudungdichvu
                    (MaSD, MaPhien, MaDV, SoLuong, DonGia, ThanhTien, ThoiGian)
                    VALUES (?,?,?,?,?,?,?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sd.getMaSD());
            ps.setString(2, sd.getMaPhien());
            ps.setString(3, sd.getMaDV());
            ps.setInt(4, sd.getSoLuong());
            ps.setDouble(5, sd.getDonGia());
            ps.setDouble(6, sd.getThanhTien());
            ps.setTimestamp(7, Timestamp.valueOf(sd.getThoiGian()));

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<SuDungDichVu> getByPhien(String maPhien) {
        List<SuDungDichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM sudungdichvu WHERE MaPhien = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maPhien);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                SuDungDichVu sd = new SuDungDichVu(
                        rs.getString("MaSD"),
                        rs.getString("MaPhien"),
                        rs.getString("MaDV"),
                        rs.getInt("SoLuong"),
                        rs.getDouble("DonGia"),
                        rs.getDouble("ThanhTien"),
                        rs.getTimestamp("ThoiGian").toLocalDateTime());
                list.add(sd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Xóa dịch vụ đã gọi
    public boolean delete(String maSD) {
        String sql = "DELETE FROM sudungdichvu WHERE MaSD = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maSD);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tính tổng tiền sử dụng dịch vụ theo phiên
    public double tinhTongTienKhachHang(String maPhien) throws Exception {
        String sql = "SELECT SUM(ThanhTien) FROM sudungdichvu WHERE MaPhien = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maPhien);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Lỗi tính tổng tiền khách hàng " + e.getMessage());
        }
        return 0.0;
    }
}
