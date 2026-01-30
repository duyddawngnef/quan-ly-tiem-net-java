package dao;

import entity.GoiDichVu;

import java.sql.*;
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
                        rs.getInt("maGoi"),
                        rs.getString("tenGoi"),
                        rs.getInt("soGio"),
                        rs.getInt("soNgay"),
                        rs.getInt("soTuan"),
                        rs.getInt("soThang"),
                        rs.getDouble("giaTien")
                );
                list.add(g);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insert(GoiDichVu g) {
        String sql = "INSERT INTO goidichvu(tenGoi, soGio, soNgay, soTuan, soThang, giaTien) VALUES(?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, g.getTenGoi());
            ps.setInt(2, g.getSoGio());
            ps.setInt(3, g.getSoNgay());
            ps.setInt(4, g.getSoTuan());
            ps.setInt(5, g.getSoThang());
            ps.setDouble(6, g.getGiaTien());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(GoiDichVu g) {
        String sql = "UPDATE goidichvu SET tenGoi=?, soGio=?, soNgay=?, soTuan=?, soThang=?, giaTien=? WHERE maGoi=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, g.getTenGoi());
            ps.setInt(2, g.getSoGio());
            ps.setInt(3, g.getSoNgay());
            ps.setInt(4, g.getSoTuan());
            ps.setInt(5, g.getSoThang());
            ps.setDouble(6, g.getGiaTien());
            ps.setInt(7, g.getMaGoi());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int maGoi) {
        String sql = "DELETE FROM goidichvu WHERE maGoi=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maGoi);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
