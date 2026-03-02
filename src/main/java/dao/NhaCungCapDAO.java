package dao;

import entity.NhaCungCap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class NhaCungCapDAO {

    // phương thức getAll
    public ArrayList<NhaCungCap> getAll() {
        ArrayList<NhaCungCap> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM NhaCungCap";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                NhaCungCap ncc = new NhaCungCap(
                        rs.getString("maNCC"),
                        rs.getString("tenNCC"),
                        rs.getString("soDienThoai"),
                        rs.getString("email"),
                        rs.getString("diaChi"),
                        rs.getString("nguoiLienHe"),
                        rs.getString("trangThai"));
                danhSach.add(ncc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // phương thức getByID
    public NhaCungCap getByID(String maNCC) {
        NhaCungCap ncc = null;
        String sql = "SELECT * FROM NhaCungCap WHERE maNCC = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maNCC);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                ncc = new NhaCungCap(
                        rs.getString("maNCC"),
                        rs.getString("tenNCC"),
                        rs.getString("soDienThoai"),
                        rs.getString("email"),
                        rs.getString("diaChi"),
                        rs.getString("nguoiLienHe"),
                        rs.getString("trangThai"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ncc;
    }

    // phương thức insert
    public boolean insert(NhaCungCap ncc) {
        String sql = "INSERT INTO NhaCungCap (maNCC, tenNCC, soDienThoai, email, diaChi, nguoiLienHe, trangThai) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ncc.getMaNCC());
            pstmt.setString(2, ncc.getTenNCC());
            pstmt.setString(3, ncc.getSoDienThoai());
            pstmt.setString(4, ncc.getEmail());
            pstmt.setString(5, ncc.getDiaChi());
            pstmt.setString(6, ncc.getNguoiLienHe());
            pstmt.setString(7, ncc.getTrangThai());

            int rows = pstmt.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // phương thức update
    public boolean update(NhaCungCap ncc) {
        String sql = "UPDATE NhaCungCap "
                + "SET tenNCC = ?, soDienThoai = ?, email = ?, diaChi = ?, nguoiLienHe = ?, trangThai = ? "
                + "WHERE maNCC = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ncc.getTenNCC());
            pstmt.setString(2, ncc.getSoDienThoai());
            pstmt.setString(3, ncc.getEmail());
            pstmt.setString(4, ncc.getDiaChi());
            pstmt.setString(5, ncc.getNguoiLienHe());
            pstmt.setString(6, ncc.getTrangThai());
            pstmt.setString(7, ncc.getMaNCC());

            int rows = pstmt.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // phương thức delete
    public boolean delete(String maNCC) {
        String sql = "DELETE FROM NhaCungCap WHERE maNCC = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maNCC);

            int rows = pstmt.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tạo mã ncc tự động
    public String generateMaNCC() {
        String sql = "SELECT maNCC FROM NhaCungCap ORDER BY maNCC DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString("maNCC");
                int num = Integer.parseInt(lastId.replace("NCC", "")) + 1;
                return String.format("NCC%03d", num);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "NCC001";
    }
}