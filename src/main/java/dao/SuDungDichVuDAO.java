package dao;

import entity.SuDungDichVu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SuDungDichVuDAO {

    public boolean insert(SuDungDichVu sd) {
        String sql = """
            INSERT INTO sudungdichvu
            (MaSD, MaPhien, MaDV, SoLuong, DonGia, ThanhTien, ThoiGian)
            VALUES (?,?,?,?,?,?,?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sd.getMaSD());
            ps.setString(2, sd.getMaPhien());
            ps.setString(3, sd.getMaDV());
            ps.setInt(4, sd.getSoLuong());
            ps.setDouble(5, sd.getDonGia());
            ps.setDouble(6, sd.getThanhTien());
            ps.setTimestamp(7, Timestamp.valueOf(sd.getThoiGian()));

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<SuDungDichVu> getByPhien(String maPhien) {
        List<SuDungDichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM sudungdichvu WHERE MaPhien = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maPhien);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                SuDungDichVu sd = new SuDungDichVu(
                        rs.getString("MaSD"),
                        rs.getString("MaPhien"),
                        rs.getString("MaDV"),
                        rs.getInt("SoLuong"),
                        rs.getDouble("DonGia"),
                        rs.getDouble("ThanhTien"),
                        rs.getTimestamp("ThoiGian").toLocalDateTime()
                );
                list.add(sd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    //tính tống tiền sử dụng dịch vụ
    public  double tinhTongTienKhachHang(String maKH ) throws Exception {
        String sql = "SELECT SUM(DonGia) FROM sudungdichvu" +
                "WHERE MaPhien = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
        ){
            pstmt.setString(1,maKH);

            try (ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    return  rs.getDouble(1);
                }
            }
        }
        catch (SQLException e ){
            throw  new Exception("Lỗi tính tổng tiền khách hàng " + e.getMessage());
        }
        return  0.0;
    }
}
