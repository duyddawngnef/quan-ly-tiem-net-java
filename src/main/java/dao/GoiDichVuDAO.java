package dao;

import entity.GoiDichVu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GoiDichVuDAO {

    public List<GoiDichVu> getAll() {
        List<GoiDichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM goidichvu";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                GoiDichVu g = new GoiDichVu(
                        rs.getString("MaGoi"),
                        rs.getString("TenGoi"),
                        rs.getString("LoaiGoi"),
                        rs.getDouble("SoGio"),
                        rs.getInt("SoNgayHieuLuc"),
                        rs.getDouble("GiaGoc"),
                        rs.getDouble("GiaGoi"),
                        rs.getString("ApDungChoKhu"),
                        rs.getString("TrangThai")
                );
                list.add(g);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Tạo mã tự động: GOI001, GOI002, ...
    public String generateId() {
        String sql = "SELECT MaGoi FROM goidichvu ORDER BY MaGoi DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString("MaGoi");
                int num = Integer.parseInt(lastId.replace("GOI", "")) + 1;
                return String.format("GOI%03d", num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "GOI001";
    }

    // Thêm mới gói dịch vụ
    public boolean insert(GoiDichVu g) {
        String sql = """
            INSERT INTO goidichvu(MaGoi, TenGoi, LoaiGoi, SoGio, SoNgayHieuLuc, GiaGoc, GiaGoi, ApDungChoKhu, TrangThai)
            VALUES (?,?,?,?,?,?,?,?,?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, g.getMaGoi());
            ps.setString(2, g.getTenGoi());
            ps.setString(3, g.getLoaiGoi());
            ps.setDouble(4, g.getSoGio());
            ps.setInt(5, g.getSoNgayHieuLuc());
            ps.setDouble(6, g.getGiaGoc());
            ps.setDouble(7, g.getGiaGoi());
            ps.setString(8, g.getApDungChoKhu());
            ps.setString(9, g.getTrangThai());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật gói dịch vụ
    public boolean update(GoiDichVu g) {
        String sql = """
            UPDATE goidichvu
            SET TenGoi = ?, LoaiGoi = ?, SoGio = ?, SoNgayHieuLuc = ?, GiaGoc = ?, GiaGoi = ?, ApDungChoKhu = ?, TrangThai = ?
            WHERE MaGoi = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, g.getTenGoi());
            ps.setString(2, g.getLoaiGoi());
            ps.setDouble(3, g.getSoGio());
            ps.setInt(4, g.getSoNgayHieuLuc());
            ps.setDouble(5, g.getGiaGoc());
            ps.setDouble(6, g.getGiaGoi());
            ps.setString(7, g.getApDungChoKhu());
            ps.setString(8, g.getTrangThai());
            ps.setString(9, g.getMaGoi());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa gói dịch vụ
    public boolean delete(String maGoi) {
        String sql = "DELETE FROM goidichvu WHERE MaGoi = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maGoi);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
