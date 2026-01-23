package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import entity.ChiTietHoaDon;
import utils.DBConnection;

public class ChiTietHoaDonDAO {

    public boolean insert(ChiTietHoaDon ct) {
        String sql = "INSERT INTO ChiTietHoaDon (maHD, maDV, soLuong, donGia) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)
            ) {

            ps.setInt(1, ct.getMaHD());
            ps.setInt(2, ct.getMaDV());
            ps.setInt(3, ct.getSoLuong());
            ps.setDouble(4, ct.getDonGia());
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
                ct.setMaHD(rs.getInt("maHD"));
                ct.setMaDV(rs.getInt("maDV"));
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