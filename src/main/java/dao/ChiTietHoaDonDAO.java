package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import quanlytiemnet.java.entity.ChiTietHoaDon;
import utils.DBConnection;

/**
 * DAO class để quản lý chi tiết hóa đơn
 * Các phương thức: getByHoaDon, insert
 */
public class ChiTietHoaDonDAO {

    public boolean insert(ChiTietHoaDon ct) {
        String sql = "INSERT INTO ChiTietHoaDon (maChiTietHoaDon, maHoaDon, tenDichVu, soLuong, donGia, thanhTien) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)
            ) {

            ps.setInt(1, ct.getMaChiTietHoaDon());
            ps.setInt(2, ct.getMaHoaDon());
            ps.setString(3, ct.getTenDichVu());
            ps.setInt(4, ct.getSoLuong());
            ps.setDouble(5, ct.getDonGia());
            ps.setDouble(6, ct.getThanhTien());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    public List<ChiTietHoaDon> getByHoaDon(int maHD) {
        List<ChiTietHoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietHoaDon WHERE maHD = ?";
        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, maHD);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ChiTietHoaDon ct = new ChiTietHoaDon();
                ct.setMaChiTietHoaDon(rs.getInt("maChiTietHoaDon"));
                ct.setMaHoaDon(rs.getInt("maHoaDon"));
                ct.setSoLuong(rs.getInt("soLuong"));
                ct.setDonGia(rs.getDouble("donGia"));
                list.add(ct);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}