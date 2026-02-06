package dao;

import entity.ChuongTrinhKhuyenMai;

import java.sql.*;
import java.util.ArrayList;

public class ChuongTrinhKhuyenMaiDAO {

    // phương thức getAll
    public ArrayList<ChuongTrinhKhuyenMai> getAll() {
        ArrayList<ChuongTrinhKhuyenMai> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM ChuongTrinhKhuyenMai";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()){

            while(rs.next()){
                ChuongTrinhKhuyenMai ctkm = new ChuongTrinhKhuyenMai(
                        rs.getString("maCTKM"),
                        rs.getString("tenCT"),
                        rs.getString("loaiKM"),
                        rs.getDouble("giaTriKM"),
                        rs.getDouble("dieuKienToiThieu"),
                        rs.getDate("ngayBatDau").toLocalDate(),
                        rs.getDate("ngayKetThuc").toLocalDate(),
                        rs.getString("trangThai")
                );
                danhSach.add(ctkm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }
    // phương thức getByID
    public ChuongTrinhKhuyenMai getByID(String maCTKM){
        ChuongTrinhKhuyenMai ctkm = null;
        String sql = "SELECT * FROM ChuongTrinhKhuyenMai WHERE maCTKM = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, maCTKM);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                ctkm = new ChuongTrinhKhuyenMai(
                        rs.getString("maCTKM"),
                        rs.getString("tenCT"),
                        rs.getString("loaiKM"),
                        rs.getDouble("giaTriKM"),
                        rs.getDouble("dieuKienToiThieu"),
                        rs.getDate("ngayBatDau").toLocalDate(),
                        rs.getDate("ngayKetThuc").toLocalDate(),
                        rs.getString("trangThai")
                );
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ctkm;
    }

    // phương thức insert
    public boolean insert(ChuongTrinhKhuyenMai ctkm){
        String sql = "INSERT INTO ChuongTrinhKhuyenMai (maCTKM, tenCT, loaiKM, giaTriKM, dieuKienToiThieu, ngayBatDau, ngayKetThuc, trangThai) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, ctkm.getMaCTKM());
            pstmt.setString(2, ctkm.getTenCT());
            pstmt.setString(3, ctkm.getLoaiKM());
            pstmt.setDouble(4, ctkm.getGiaTriKM());
            pstmt.setDouble(5, ctkm.getDieuKienToiThieu());
            pstmt.setDate(6, Date.valueOf(ctkm.getNgayBatDau()));
            pstmt.setDate(7, Date.valueOf(ctkm.getNgayKetThuc()));
            pstmt.setString(8, ctkm.getTrangThai());

            int rows = pstmt.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // phương thức update
    public boolean update(ChuongTrinhKhuyenMai ctkm){
        String sql = "UPDATE ChuongTrinhKhuyenMai "
                + "SET tenCT = ?, loaiKM = ?, giaTriKM = ?, dieuKienToiThieu = ?, ngayBatDau = ?, ngayKetThuc = ?, trangThai = ? "
                + "WHERE maCTKM = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){


            pstmt.setString(1, ctkm.getTenCT());
            pstmt.setString(2, ctkm.getLoaiKM());
            pstmt.setDouble(3, ctkm.getGiaTriKM());
            pstmt.setDouble(4, ctkm.getDieuKienToiThieu());
            pstmt.setDate(5, Date.valueOf(ctkm.getNgayBatDau()));
            pstmt.setDate(6, Date.valueOf(ctkm.getNgayKetThuc()));
            pstmt.setString(7, ctkm.getTrangThai());
            pstmt.setString(8, ctkm.getMaCTKM());
            int rows = pstmt.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //phương thức delete
    public boolean delete(String maCTKM){
        String sql = "DELETE FROM ChuongTrinhKhuyenMai WHERE maCTKM = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, maCTKM);

            int rows = pstmt.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // phương thức getConHieuLuc
    public ArrayList<ChuongTrinhKhuyenMai> getConHieuLuc() {
        ArrayList<ChuongTrinhKhuyenMai> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM ChuongTrinhKhuyenMai "
                + "WHERE trangThai = 'Hoạt động' "
                + "AND CURDATE() BETWEEN ngayBatDau AND ngayKetThuc";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                ChuongTrinhKhuyenMai ctkm = new ChuongTrinhKhuyenMai(
                        rs.getString("maCTKM"),
                        rs.getString("tenCT"),
                        rs.getString("loaiKM"),
                        rs.getDouble("giaTriKM"),
                        rs.getDouble("dieuKienToiThieu"),
                        rs.getDate("ngayBatDau").toLocalDate(),
                        rs.getDate("ngayKetThuc").toLocalDate(),
                        rs.getString("trangThai")
                );
                danhSach.add(ctkm);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }
}