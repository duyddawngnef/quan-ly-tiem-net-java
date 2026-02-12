package dao;

import entity.GoiDichVuKhachHang;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GoiDichVuKhachHangDAO {

    // Tạo mã tự động: GOIKH001, GOIKH002, ...
    public String generateId() {
        String sql = "SELECT MaGoiKH FROM goidichvu_khachhang ORDER BY MaGoiKH DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString("MaGoiKH");
                int num = Integer.parseInt(lastId.replace("GOIKH", "")) + 1;
                return String.format("GOIKH%03d", num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "GOIKH001";
    }

    // Đăng ký gói cho khách
    public boolean insert(GoiDichVuKhachHang g) {
        String sql = """
            INSERT INTO goidichvu_khachhang
            (MaGoiKH, MaKH, MaGoi, MaNV, SoGioBanDau, SoGioConLai, NgayMua, NgayHetHan, GiaMua, TrangThai)
            VALUES (?,?,?,?,?,?,?,?,?,?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, g.getMaGoiKH());
            ps.setString(2, g.getMaKH());
            ps.setString(3, g.getMaGoi());
            ps.setString(4, g.getMaNV());
            ps.setDouble(5, g.getSoGioBanDau());
            ps.setDouble(6, g.getSoGioConLai());
            ps.setTimestamp(7, g.getNgayMua() != null ? Timestamp.valueOf(g.getNgayMua()) : null);
            ps.setTimestamp(8, Timestamp.valueOf(g.getNgayHetHan()));
            ps.setDouble(9, g.getGiaMua());
            ps.setString(10, g.getTrangThai());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lọc gói còn hạn (còn hiệu lực + còn giờ)
    public List<GoiDichVuKhachHang> getConHieuLuc(String maKH) {
        List<GoiDichVuKhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM goidichvu_khachhang WHERE MaKH = ? AND TrangThai = 'CONHAN' AND NgayHetHan > NOW() AND SoGioConLai > 0";

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
