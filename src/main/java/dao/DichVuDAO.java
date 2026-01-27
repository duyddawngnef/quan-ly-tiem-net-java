package dao;

import entity.DichVu;

import java.sql.*;
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
                        rs.getInt("maDichVu"),
                        rs.getString("tenDichVu"),
                        rs.getDouble("donGia"),
                        rs.getInt("soLuongTon"),
                        rs.getString("loai")
                );
                list.add(dv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(DichVu dv) {
        String sql = "INSERT INTO dichvu(tenDichVu, donGia, soLuongTon, loai) VALUES(?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dv.getTenDichVu());
            ps.setDouble(2, dv.getDonGia());
            ps.setInt(3, dv.getSoLuongTon());
            ps.setString(4, dv.getLoai());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(DichVu dv) {
        String sql = "UPDATE dichvu SET tenDichVu=?, donGia=?, soLuongTon=?, loai=? WHERE maDichVu=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dv.getTenDichVu());
            ps.setDouble(2, dv.getDonGia());
            ps.setInt(3, dv.getSoLuongTon());
            ps.setString(4, dv.getLoai());
            ps.setInt(5, dv.getMaDichVu());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int maDichVu) {
        String sql = "DELETE FROM dichvu WHERE maDichVu=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maDichVu);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateSoLuong(int maDichVu, int soLuongTru) {
        String sql = "UPDATE dichvu SET soLuongTon = soLuongTon - ? WHERE maDichVu=? AND soLuongTon >= ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, soLuongTru);
            ps.setInt(2, maDichVu);
            ps.setInt(3, soLuongTru);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
