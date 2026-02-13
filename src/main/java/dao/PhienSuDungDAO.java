package dao;

import entity.PhienSuDung;
import utils.DBConnection;

import java.sql.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PhienSuDungDAO {

    public List<PhienSuDung> getAll() {
        List<PhienSuDung> list = new ArrayList<>();
        String sql = "SELECT * FROM PhienSuDung";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                PhienSuDung p = new PhienSuDung();
                p.setMaPhien(rs.getString("maPhien"));
                p.setMaMay(rs.getString("maMay"));
                p.setGioBatDau(rs.getTimestamp("batDau").toLocalDateTime());
                p.setGioKetThuc(rs.getTimestamp("ketThuc").toLocalDateTime());

                list.add(p);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insert(PhienSuDung p) {
        String sql = "INSERT INTO PhienSuDung(maMay, batDau) VALUES(?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getMaMay());
            ps.setTimestamp(2, Timestamp.valueOf(p.getGioBatDau()));
            ps.setTimestamp(3, Timestamp.valueOf(p.getGioKetThuc()));

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean closeSession(int maPhien, Timestamp ketThuc) {
        String sql = "UPDATE PhienSuDung SET ketThuc=? WHERE maPhien=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, ketThuc);
            ps.setInt(2, maPhien);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
