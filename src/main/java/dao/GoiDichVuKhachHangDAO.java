package dao;

import entity.GoiDichVuKhachHang;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GoiDichVuKhachHangDAO {

    public List<GoiDichVuKhachHang> getByKhachHang(String maKH) {
        List<GoiDichVuKhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM goidichvu_khachhang WHERE MaKH = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maKH);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                GoiDichVuKhachHang g = new GoiDichVuKhachHang(
                        rs.getString("MaGoiKH"),
                        rs.getString("MaKH"),
                        rs.getString("MaGoi"),
                        rs.getString("MaNV"),
                        rs.getDouble("SoGioBanDau"),
                        rs.getDouble("SoGioConLai"),
                        rs.getTimestamp("NgayMua").toLocalDateTime(),
                        rs.getTimestamp("NgayHetHan").toLocalDateTime(),
                        rs.getDouble("GiaMua"),
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
