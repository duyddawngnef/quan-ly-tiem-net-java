package dao;

import entity.GoiDichVuKhachHang;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GoiDichVuKhachHangDAO {

    public boolean insert(GoiDichVuKhachHang gkh) {
        String sql = "INSERT INTO goidichvukhachhang(maKhachHang, maGoi, soGioConLai, ngayMua) VALUES(?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, gkh.getMaKhachHang());
            ps.setInt(2, gkh.getMaGoi());
            ps.setInt(3, gkh.getSoGioConLai());
            ps.setTimestamp(4, Timestamp.valueOf(gkh.getNgayMua()));

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<GoiDichVuKhachHang> getByKhachHang(int maKhachHang) {
        List<GoiDichVuKhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM goidichvukhachhang WHERE maKhachHang=? ORDER BY ngayMua DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maKhachHang);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime time = rs.getTimestamp("ngayMua").toLocalDateTime();

                    GoiDichVuKhachHang gkh = new GoiDichVuKhachHang(
                            rs.getInt("maGoiKhachHang"),
                            rs.getInt("maKhachHang"),
                            rs.getInt("maGoi"),
                            rs.getInt("soGioConLai"),
                            time
                    );
                    list.add(gkh);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean truGio(int maGoiKhachHang, int soGioTru) {
        String sql = "UPDATE goidichvukhachhang SET soGioConLai = soGioConLai - ? WHERE maGoiKhachHang=? AND soGioConLai >= ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, soGioTru);
            ps.setInt(2, maGoiKhachHang);
            ps.setInt(3, soGioTru);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
