package dao;

import entity.DichVu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
}
