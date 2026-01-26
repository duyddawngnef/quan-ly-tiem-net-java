package dao;

import entity.SuDungDichVu;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SuDungDichVuDAO {

    public boolean insert(SuDungDichVu sd) {
        String sql = "INSERT INTO sudungdichvu(maPhien, maDichVu, soLuong, thanhTien, thoiGian) VALUES(?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sd.getMaPhien());
            ps.setInt(2, sd.getMaDichVu());
            ps.setInt(3, sd.getSoLuong());
            ps.setDouble(4, sd.getThanhTien());
            ps.setTimestamp(5, Timestamp.valueOf(sd.getThoiGian()));

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<SuDungDichVu> getByPhien(int maPhien) {
        List<SuDungDichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM sudungdichvu WHERE maPhien=? ORDER BY thoiGian DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maPhien);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime time = rs.getTimestamp("thoiGian").toLocalDateTime();

                    SuDungDichVu sd = new SuDungDichVu(
                            rs.getInt("maSuDung"),
                            rs.getInt("maPhien"),
                            rs.getInt("maDichVu"),
                            rs.getInt("soLuong"),
                            rs.getDouble("thanhTien"),
                            time
                    );
                    list.add(sd);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
