package dao;

import entity.GoiDichVu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
}
