package dao;

import entity.ChiTietPhieuNhap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ChiTietPhieuNhapDAO {

    // phương thức getByPhieu
    public ArrayList<ChiTietPhieuNhap> getByPhieu(String maPhieuNhap) {
        ArrayList<ChiTietPhieuNhap> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietPhieuNhap WHERE maPhieuNhap = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maPhieuNhap);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ChiTietPhieuNhap ctpn = new ChiTietPhieuNhap(
                        rs.getString("maCTPN"),
                        rs.getString("maPhieuNhap"),
                        rs.getString("maDV"),
                        rs.getInt("soLuong"),
                        rs.getDouble("giaNhap"),
                        rs.getDouble("thanhTien"));
                danhSach.add(ctpn);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // phương thức insert
    public boolean insert(ChiTietPhieuNhap ctpn) {
        String sql = "INSERT INTO ChiTietPhieuNhap (maCTPN, maPhieuNhap, maDV, soLuong, giaNhap, thanhTien) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ctpn.getMaCTPN());
            pstmt.setString(2, ctpn.getMaPhieuNhap());
            pstmt.setString(3, ctpn.getMaDV());
            pstmt.setInt(4, ctpn.getSoLuong());
            pstmt.setDouble(5, ctpn.getGiaNhap());
            pstmt.setDouble(6, ctpn.getThanhTien());

            int rows = pstmt.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // cập nhật số lượng tồn
    public boolean updateSoLuongTon(Connection conn, String maDV, int soLuong) throws SQLException {

        String sql = "UPDATE DichVu SET soLuongTon = soLuongTon + ?, trangThai = 'CONHANG' WHERE maDV = ?";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, soLuong);
        pstmt.setString(2, maDV);

        return pstmt.executeUpdate() > 0;
    }
}