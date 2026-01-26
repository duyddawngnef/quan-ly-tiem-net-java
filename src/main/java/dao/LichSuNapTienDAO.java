package dao;

import entity.LichSuNapTien;

import java.sql.*;
import java.util.ArrayList;

public class LichSuNapTienDAO {

    // phương thức getByKhachHang
    public LichSuNapTien getByKhachHang(String maKH){
        LichSuNapTien lsnt = null;
        String sql = "SELECT * FROM LichSuNapTien WHERE maKH = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, maKH);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                lsnt = new LichSuNapTien(
                        rs.getString("maNap"),
                        rs.getString("maKH"),
                        rs.getString("maNV"),
                        rs.getString("maCTKM"),
                        rs.getDouble("soTienNap"),
                        rs.getDouble("tienKhuyenMai"),
                        rs.getDate("ngayNap").toLocalDate()
                );
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lsnt;
    }

    // phương thức insert
    public boolean insert(LichSuNapTien lsnt){
        String sql = "INSERT INTO LichSuNapTien (maNap, maKH, maNV, maCTKM, soTienNap, tienKhuyenMai, tongTien, ngayNap) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){


            pstmt.setString(1, lsnt.getMaNap());
            pstmt.setString(2, lsnt.getMaKH());
            pstmt.setString(3, lsnt.getMaNV());
            pstmt.setString(4, lsnt.getMaCTKM());
            pstmt.setDouble(5, lsnt.getSoTienNap());
            pstmt.setDouble(6, lsnt.getTienKhuyenMai());
            pstmt.setDouble(7, lsnt.getTongTien());
            pstmt.setDate(8, Date.valueOf(lsnt.getNgayNap()));

            int rows = pstmt.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // phương thức getByDateRange
    public ArrayList<LichSuNapTien> getByDateRange(Date fromDate, Date toDate){
        ArrayList<LichSuNapTien> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM LichSuNapTien WHERE ngayNap BETWEEN ? AND ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);) {


            pstmt.setDate(1, fromDate);
            pstmt.setDate(2, toDate);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                LichSuNapTien lsnt = new LichSuNapTien(
                        rs.getString("maNap"),
                        rs.getString("maKH"),
                        rs.getString("maNV"),
                        rs.getString("maCTKM"),
                        rs.getDouble("soTienNap"),
                        rs.getDouble("tienKhuyenMai"),
                        rs.getDate("ngayNap").toLocalDate()
                );
                danhSach.add(lsnt);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }
}