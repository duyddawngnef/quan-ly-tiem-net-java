package dao;

import entity.KhuMay;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhuMayDAO
{
    public List<KhuMay> getAll()
    {
        List<KhuMay> list = new ArrayList<>();
        String sql = "SELECT * FROM khumay ORDER BY MaKhu DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next())
            {
                KhuMay km = mapResultSetToEntity(rs);
                list.add(km);
            }
            rs.close();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getAll KhuMay: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(KhuMay km)
    {
        validateKhuMay(km);

        String sql = "INSERT INTO khumay (MaKhu, TenKhu, GiaCoSo, SoMayToiDa, TrangThai) " +
                     "VALUES (?, ?, ?, ?, ?)";

        // Tạo mã khu tự động
        String makhu = generateMaKhu();
        km.setMaKhu(makhu);

        // Set trạng thái = Hoạt Động
        km.setTrangthai("HOATDONG");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, km.getMaKhu());
            pstmt.setString(2, km.getTenKhu());
            pstmt.setDouble(3, km.getGiacoso());
            pstmt.setInt(4, km.getSomaytoida());
            pstmt.setString(5, km.getTrangthai());

            int rowUpdate = pstmt.executeUpdate();
            return rowUpdate > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi insert KhuMay: " + e.getMessage());
        }
    }

    public boolean update(KhuMay km)
    {
        KhuMay existing = getByID(km.getMaKhu());

        //kiểm tra khu máy tồn tại
        if (existing == null){
            throw new RuntimeException("Lỗi khu máy không tồn tại !");
        }

        //khu máy đã bị xóa trước đó
        if (existing.getTrangthai().equals("NGUNG")) {
            throw new RuntimeException("Khu máy đã bị xóa !");
        }

        //kiểm tra Valid
        validateKhuMay(km);

        String sql = "UPDATE khumay SET TenKhu = ?, GiaCoSo = ?, SoMayToiDa = ? " +
                     "WHERE MaKhu = ? AND TrangThai = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, km.getTenKhu());
            pstmt.setDouble(2, km.getGiacoso());
            pstmt.setInt(3, km.getSomaytoida());
            pstmt.setString(4, km.getMaKhu());
            pstmt.setString(5, "HOATDONG");
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi update KhuMay: " + e.getMessage());
        }
        return true;
    }

    public boolean delete(String MaKhu)
    {
        KhuMay km = getByID(MaKhu);

        //kiểm tra khu máy tồn tại
        if (km == null) {
            throw new RuntimeException("Lỗi khu máy không tồn tại !");
        }

        //khu máy đã bị xóa trước đó
        if (km.getTrangthai().equals("NGUNG")) {
            throw new RuntimeException("Khu máy đã bị xóa !");
        }

        if (hasActiveComputer(MaKhu)) {
            throw new RuntimeException("Không thể xóa khu máy có máy đang sử dụng !");
        } else {
            updateMaKhuNull(MaKhu);
        }

        String sql = "UPDATE khumay SET TrangThai = 'NGUNG' WHERE MaKhu = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, MaKhu);
            int row = pstmt.executeUpdate();
            return row > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi delete KhuMay: " + e.getMessage());
        }
    }

    //Tạo mã tự động
    public String generateMaKhu()
    {
        String sql = "SELECT MaKhu FROM khumay "+
                     "ORDER BY MaKhu DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            ResultSet rs = pstmt.executeQuery(sql);

            if(rs.next())
            {
                String makhu = rs.getString("MaKhu");
                int num = Integer.parseInt(makhu.substring(3));
                return String.format("KHU%03d", num + 1);
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi generateMaKhu" + e.getMessage());
        }
        //CHƯA CÓ DATABASE
        return "KHU001";
    }

    // Tìm khu theo mã
    public KhuMay getByID(String makhu)
    {
        KhuMay km = null;
        String sql = "SELECT * FROM khumay WHERE MaKhu = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, makhu);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
                km = mapResultSetToEntity(rs);
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getByID KhuMay: " + e.getMessage());
        }
        return km;
    }

    /*
    ======================VALIDATION=============
     */
    private void validateKhuMay(KhuMay km) {
        // Validate TenKhu
        if (km.getTenKhu() == null || km.getTenKhu().trim().isEmpty()) {
            throw new RuntimeException("Tên khu kmông được để trống");
        }
        if (km.getTenKhu().trim().length() > 50) {
            throw new RuntimeException("Tên khu kmông được vượt quá 50 ký tự");
        }
        if (isTenKhuExists(km.getTenKhu())) {
            throw new RuntimeException("Tên khu đã tồn tại !");
        }

        // Validate GiaCoSo
        if (km.getGiacoso() <= 0) {
            throw new RuntimeException("Giá cơ sở không được nhỏ hơn hoặc bằng 0");
        }

        // Validate SoMayToiDa
        if (km.getSomaytoida() < 0) {
            throw new RuntimeException("Số máy tối đa không được nhỏ hơn 0");
        }
    }

    public boolean isTenKhuExists(String tenkhu){

        if (tenkhu == null || tenkhu.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM khumay WHERE TenKhu = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, tenkhu);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next())
                return rs.getInt(1) > 0;
            rs.close();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi isTenKhuExists KhachHang " + e.getMessage());
        }
        return false;
    }

    // Đếm số lượng máy đang dùng ở trong khu
    public boolean hasActiveComputer (String MaKhu) {
        String sql = "SELECT COUNT(*) FROM maytinh WHERE MaKhu = ? AND TrangThai = 'DANGDUNG'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, MaKhu);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next())
                return rs.getInt(1) > 0 ;
            rs.close();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi hasActiveComputer KhuMay " + e.getMessage());
        }
        return false;
    }

    public int countMayTinhByKhu (String MaKhu)
    {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM maytinh WHERE MaKhu = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, MaKhu);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
                count = rs.getInt(1);
            rs.close();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi countMayTinhByKhu KhuMay " + e.getMessage());
        }
        return count;
    }

    public boolean updateMaKhuNull(String MaKhu)
    {
        String sql = "UPDATE maytinh SET MaKhu = NULL WHERE MaKhu = ? ";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, MaKhu);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi updateMaKhuNull KhuMay " + e.getMessage());
        }
        return true;
    }

    public KhuMay mapResultSetToEntity(ResultSet rs) throws SQLException
    {
        KhuMay km = new KhuMay();
        km.setMaKhu(rs.getString("MaKhu"));
        km.setTenKhu(rs.getString("TenKhu"));
        km.setGiacoso(rs.getDouble("GiaCoSo"));
        km.setSomaytoida(rs.getInt("SoMayToiDa"));

        try {
            km.setTrangthai(rs.getString("TrangThai"));
        } catch (SQLException e) {
            km.setTrangthai("HOATDONG");
        }
        return km;
    }
}