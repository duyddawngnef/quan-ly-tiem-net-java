package dao;

import entity.DichVu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DichVuDAO {

    public List<DichVu> getAll() {
        List<DichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM dichvu";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DichVu dv = new DichVu(
                        rs.getString("MaDV"),
                        rs.getString("TenDV"),
                        rs.getString("LoaiDV"),
                        rs.getDouble("DonGia"),
                        rs.getString("DonViTinh"),
                        rs.getInt("SoLuongTon"),
                        rs.getString("TrangThai")
                );
                list.add(dv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Tạo mã tự động: DV001, DV002, ...
    public String generateId() {
        String sql = "SELECT MaDV FROM dichvu ORDER BY MaDV DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString("MaDV");
                int num = Integer.parseInt(lastId.replace("DV", "")) + 1;
                return String.format("DV%03d", num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "DV001";
    }

    // Lấy theo mã
    public DichVu getById(String maDV) {
        String sql = "SELECT * FROM dichvu WHERE MaDV = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDV);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new DichVu(
                        rs.getString("MaDV"),
                        rs.getString("TenDV"),
                        rs.getString("LoaiDV"),
                        rs.getDouble("DonGia"),
                        rs.getString("DonViTinh"),
                        rs.getInt("SoLuongTon"),
                        rs.getString("TrangThai")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(DichVu dv) {
        String sql = """
            INSERT INTO dichvu(MaDV, TenDV, LoaiDV, DonGia, DonViTinh, SoLuongTon, TrangThai)
            VALUES (?,?,?,?,?,?,?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dv.getMaDV());
            ps.setString(2, dv.getTenDV());
            ps.setString(3, dv.getLoaiDV());
            ps.setDouble(4, dv.getDonGia());
            ps.setString(5, dv.getDonViTinh());
            ps.setInt(6, dv.getSoLuongTon());
            ps.setString(7, dv.getTrangThai());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật dịch vụ
    public boolean update(DichVu dv) {
        String sql = """
            UPDATE dichvu
            SET TenDV = ?, LoaiDV = ?, DonGia = ?, DonViTinh = ?, SoLuongTon = ?, TrangThai = ?
            WHERE MaDV = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dv.getTenDV());
            ps.setString(2, dv.getLoaiDV());
            ps.setDouble(3, dv.getDonGia());
            ps.setString(4, dv.getDonViTinh());
            ps.setInt(5, dv.getSoLuongTon());
            ps.setString(6, dv.getTrangThai());
            ps.setString(7, dv.getMaDV());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa dịch vụ
    public boolean delete(String maDV) {
        String sql = "DELETE FROM dichvu WHERE MaDV = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maDV);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật số lượng tồn
    public boolean updateSoLuong(String maDV, int soLuongThayDoi) {
        String sql = "UPDATE dichvu SET SoLuongTon = SoLuongTon + ? WHERE MaDV = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, soLuongThayDoi);
            ps.setString(2, maDV);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
