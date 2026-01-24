package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import entity.HoaDon;

public class HoaDonDAO {
    public List<HoaDon> getAll() {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon";

        try (
            Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)
        ) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public HoaDon getById(int maHD) {
        String sql = "SELECT * FROM HoaDon WHERE maHD = ?";
        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
                
            ps.setInt(1, maHD);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int insert (HoaDon hd) {
        String sql = "INSERT INTO HoaDon (maPhien, tongTien, ngayLap) VALUES (?, ?, ?)";
        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            ps.setInt(1, hd.getMaPhien());
            ps.setDouble(2, hd.getTongTien());
            ps.setDate(3, Date.valueOf(hd.getNgayLap()));
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return -1;
    }

    public List<HoaDon> getByDateRange(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon WHERE ngayLap BETWEEN ? AND ?";
        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setTimestamp(1, Timestamp.valueOf(fromDateTime));
            ps.setTimestamp(2, Timestamp.valueOf(toDateTime));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private HoaDon map(ResultSet rs) throws SQLException {
        HoaDon hd = new HoaDon();
        hd.setMaHD(rs.getInt("maHD"));
        hd.setMaPhien(rs.getInt("maPhien"));
        hd.setTongTien(rs.getDouble("tongTien"));
        hd.setNgayLap(rs.getDate("ngayLap").toLocalDate());
        return hd;
    }
}