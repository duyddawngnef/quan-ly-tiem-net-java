package dao;

import entity.PhieuNhapHang;

import java.sql.*;
import java.util.ArrayList;

public class PhieuNhapHangDAO {

    //phương thức getAll
    public ArrayList<PhieuNhapHang> getAll() {
        ArrayList<PhieuNhapHang> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM PhieuNhapHang";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()){


            while (rs.next()) {
                PhieuNhapHang pnh = new PhieuNhapHang(
                        rs.getString("maPhieu"),
                        rs.getString("maNCC"),
                        rs.getString("maNV"),
                        rs.getDate("ngayNhap").toLocalDate(),
                        rs.getDouble("tongTien"),
                        rs.getString("ghiChu")
                );
                danhSach.add(pnh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // phương thức getByID
    public PhieuNhapHang getByID(String maPhieu){
        PhieuNhapHang pnh = null;
        String sql = "SELECT * FROM PhieuNhapHang WHERE maPhieu = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, maPhieu);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                pnh = new PhieuNhapHang(
                        rs.getString("maPhieu"),
                        rs.getString("maNCC"),
                        rs.getString("maNV"),
                        rs.getDate("ngayNhap").toLocalDate(),
                        rs.getDouble("tongTien"),
                        rs.getString("ghiChu")
                );
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pnh;
    }

    // phương thức getByNCC
    public ArrayList<PhieuNhapHang> getByNCC(String maNCC){
        ArrayList<PhieuNhapHang> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM PhieuNhapHang WHERE maNCC = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, maNCC);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                PhieuNhapHang pnh = new PhieuNhapHang(
                        rs.getString("maPhieu"),
                        rs.getString("maNCC"),
                        rs.getString("maNV"),
                        rs.getDate("ngayNhap").toLocalDate(),
                        rs.getDouble("tongTien"),
                        rs.getString("ghiChu")
                );
                danhSach.add(pnh);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // phương thức insert
    public boolean insert(PhieuNhapHang pnh){
        String sql = "INSERT INTO PhieuNhapHang (maPhieu, maNCC, maNV, ngayNhap, tongTien, ghiChu) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, pnh.getMaPhieu());
            pstmt.setString(2, pnh.getMaNCC());
            pstmt.setString(3, pnh.getMaNV());
            pstmt.setDate(4, Date.valueOf(pnh.getNgayNhap()));
            pstmt.setDouble(5, pnh.getTongTien());
            pstmt.setString(6, pnh.getGhiChu());

            int rows = pstmt.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}