package dao;

import entity.PhieuNhapHang;

import java.sql.*;
import java.util.ArrayList;

public class PhieuNhapHangDAO {

    // phương thức getAll
    public ArrayList<PhieuNhapHang> getAll() {
        ArrayList<PhieuNhapHang> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM PhieuNhapHang";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                PhieuNhapHang pnh = new PhieuNhapHang(
                        rs.getString("maPhieuNhap"),
                        rs.getString("maNCC"),
                        rs.getString("maNV"),
                        rs.getDate("ngayNhap").toLocalDate(),
                        rs.getDouble("tongTien"),
                        rs.getString("trangThai"));
                danhSach.add(pnh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // phương thức getByID
    public PhieuNhapHang getByID(String maPhieuNhap) {
        PhieuNhapHang pnh = null;
        String sql = "SELECT * FROM PhieuNhapHang WHERE maPhieuNhap = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maPhieuNhap);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                pnh = new PhieuNhapHang(
                        rs.getString("maPhieuNhap"),
                        rs.getString("maNCC"),
                        rs.getString("maNV"),
                        rs.getDate("ngayNhap").toLocalDate(),
                        rs.getDouble("tongTien"),
                        rs.getString("trangThai"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pnh;
    }

    // phương thức getByNCC
    public ArrayList<PhieuNhapHang> getByNCC(String maNCC) {
        ArrayList<PhieuNhapHang> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM PhieuNhapHang WHERE maNCC = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maNCC);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                PhieuNhapHang pnh = new PhieuNhapHang(
                        rs.getString("maPhieuNhap"),
                        rs.getString("maNCC"),
                        rs.getString("maNV"),
                        rs.getDate("ngayNhap").toLocalDate(),
                        rs.getDouble("tongTien"),
                        rs.getString("trangThai"));
                danhSach.add(pnh);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // phương thức insert
    public boolean insert(PhieuNhapHang pnh) {
        String sql = "INSERT INTO PhieuNhapHang (maPhieuNhap, maNCC, maNV, ngayNhap, tongTien, trangThai) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pnh.getMaPhieuNhap());
            pstmt.setString(2, pnh.getMaNCC());
            pstmt.setString(3, pnh.getMaNV());
            pstmt.setDate(4, Date.valueOf(pnh.getNgayNhap()));
            pstmt.setDouble(5, pnh.getTongTien());
            pstmt.setString(6, pnh.getTrangThai());

            int rows = pstmt.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Kiểm tra phiếu nhập chờ duyệt
    public boolean phieuNhapChoDuyet(String maNCC) {
        String sql = "SELECT COUNT(*) FROM PhieuNhapHang WHERE maNCC = ? AND trangThai = 'CHODUYET'";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maNCC);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Tạo mã pnh tự động
    public String generateMaPhieuNhap() {
        String sql = "SELECT maPhieuNhap FROM PhieuNhapHang ORDER BY maPhieuNhap DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString("maPhieuNhap");
                int num = Integer.parseInt(lastId.replace("PN", "")) + 1;
                return String.format("PN%03d", num);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "PN001";
    }

    // Cập nhật trạng thái phiếu nhập
    public boolean updateTrangThai(String maPhieuNhap, String trangThai) {
        String sql = "UPDATE PhieuNhapHang SET trangThai = ? WHERE maPhieuNhap = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, trangThai);
            pstmt.setString(2, maPhieuNhap);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa phiếu nhập (dùng khi rollback)
    public boolean delete(String maPhieuNhap) {
        String sql = "DELETE FROM PhieuNhapHang WHERE maPhieuNhap = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maPhieuNhap);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}