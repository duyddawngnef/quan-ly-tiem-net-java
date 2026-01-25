package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import entity.PhienSuDung;

public class PhienSuDungDAO {
    public List<PhienSuDung> getAll() {
        List<PhienSuDung> list = new ArrayList<>();
        String sql = "SELECT * FROM PhienSuDung";

        try (Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(map(rs));
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    private PhienSuDung map(ResultSet rs) {
        throw new UnsupportedOperationException("Unimplemented method 'map'");
    }


    public PhienSuDung getById(int maPhien) {
        String sql = "SELECT * FROM PhienSuDung WHERE maPhien = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
                
            ps.setInt(1, maPhien);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}