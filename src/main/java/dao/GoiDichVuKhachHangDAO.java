package dao;

import entity.GoiDichVuKhachHang;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyPermission;

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
    public GoiDichVuKhachHang getByMaGoiKhachHang(String maGoiKH) {
        GoiDichVuKhachHang goiKH = new GoiDichVuKhachHang();
        String sql = "SELECT MaGoiKH, MaKH, MaGoi, MaNV, SoGioBanDau, SoGioConLai, NgayMua, " +
                "NgayHetHan, GiaMua, TrangThai " + "FROM goidichvu_khachhang WHERE MaGoiKH = ?";

        try {
           Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, maGoiKH);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String magoikh = rs.getString("MaGoiKH");
                String makh = rs.getString("MaKH");
                String magoi = rs.getString("MaGoi");
                String manv = rs.getString("MaNV");
                double sogiobandau = rs.getDouble("SoGioBanDau");
                double sogioconlai = rs.getDouble("SoGioConLai");
                LocalDateTime ngaymua = rs.getTimestamp("NgayMua").toLocalDateTime();
                LocalDateTime ngayhethan = rs.getTimestamp("NgayHetHan").toLocalDateTime();
                double giamua = rs.getDouble("GiaMua");
                String trangthai = rs.getString("TrangThai");

                GoiDichVuKhachHang gdvkh = new GoiDichVuKhachHang(magoikh, makh, magoi, manv,
                        sogiobandau, sogioconlai, ngaymua, ngayhethan, giamua, trangthai);
            }

        } catch (SQLException e) {
            System.err.println("[LỖI GETBYMAGOIKHACHHANG - GoiDichVuKhachHangDAO]: " + e.getMessage());
            return null;
        } finally {
            DBConnection.closeConnection();
        }

        return goiKH;
    }
    public boolean update(GoiDichVuKhachHang updateGDVKH) {
        String sql = "UPDATE goidichvu_khachhang SET SoGioConLai = ?" +
                ", TrangThai = ? WHERE MaGoiKH = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setDouble(1, updateGDVKH.getSoGioConLai());
            ps.setString(2, updateGDVKH.getTrangThai());
            ps.setString(3, updateGDVKH.getMaGoiKH());

            int rowAffected = ps.executeUpdate();

            return rowAffected > 0;
        } catch (SQLException e) {
            System.err.println("[LỖI UPDATE - GoiDichVuKhachHangDAO]: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeConnection();
        }
    }

}
