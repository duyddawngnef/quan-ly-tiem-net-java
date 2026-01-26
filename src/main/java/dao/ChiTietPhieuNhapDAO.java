package dao;

import entity.ChiTietPhieuNhap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ChiTietPhieuNhapDAO {

    // phương thức getByPhieu
    public ArrayList<ChiTietPhieuNhap> getByPhieu(String maPhieu){
        ArrayList<ChiTietPhieuNhap> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietPhieuNhap WHERE maPhieu = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maPhieu);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                ChiTietPhieuNhap ctpn = new ChiTietPhieuNhap(
                        rs.getString("maPhieu"),
                        rs.getString("maDV"),
                        rs.getInt("soLuong"),
                        rs.getDouble("donGiaNhap")
                );
                danhSach.add(ctpn);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // phương thức insert
    public boolean insert(ChiTietPhieuNhap ctpn){
        String sql = "INSERT INTO ChiTietPhieuNhap (maPhieu, maDV, soLuong, donGiaNhap, thanhTien) "
                + "VALUES (?, ?, ?, ?, ?)";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ctpn.getMaPhieu());
            pstmt.setString(2, ctpn.getMaDV());
            pstmt.setInt(3, ctpn.getSoLuong());
            pstmt.setDouble(4, ctpn.getDonGiaNhap());
            pstmt.setDouble(5, ctpn.getThanhTien());

            int rows = pstmt.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}