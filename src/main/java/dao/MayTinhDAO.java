package dao;

import entity.KhuMay;
import entity.MayTinh;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MayTinhDAO
{
    public List<MayTinh> getAll()
    {
        List<MayTinh> list = new ArrayList<>();
        String sql = "SELECT * FROM maytinh ORDER BY MaMay DESC";

        try
        {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                MayTinh mt = mapResultSetToEntity(rs);
                list.add(mt);
            }
            rs.close();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getAll MayTinh: " + e.getMessage());
        }
        return list;
    }

    public List<MayTinh> getByKhu(String makhu)
    {
        List<MayTinh> list = new ArrayList<>();
        String sql = "SELECT * FROM maytinh WHERE MaKhu = ? ORDER BY MaKhu DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, makhu);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                MayTinh mt = mapResultSetToEntity(rs);
                list.add(mt);
            }
            rs.close();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getByKhu MayTinh: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(MayTinh mt)
    {
        validateMayTinh(mt);

        String sql = "INSERT INTO maytinh (MaMay, TenMay, MaKhu, CauHinh, GiaMoiGio, TrangThai) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        // Tạo mã khu tự động
        String makhu = generateMaMay();
        mt.setMamay(makhu);

        // Set trạng thái = Hoạt Động
        mt.setTrangthai("TRONG");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, mt.getMamay());
            pstmt.setString(2, mt.getTenmay());
            pstmt.setString(3, mt.getMakhu());
            pstmt.setString(4, mt.getCauhinh());
            pstmt.setDouble(5, mt.getGiamoigio());
            pstmt.setString(6, mt.getTrangthai());

            int rowUpdate = pstmt.executeUpdate();
            return rowUpdate > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi insert MayTinh: " + e.getMessage());
        }
    }

    public boolean update(MayTinh mt)
    {
        MayTinh existing = getByID(mt.getMamay());

        //kiểm tra máy tính tồn tại
        if (existing == null){
            throw new RuntimeException("Lỗi máy tính không tồn tại !");
        }

        //Máy tính đã bị xóa trước đó
        if (existing.getTrangthai().equals("NGUNG")) {
            throw new RuntimeException("Máy tính đã bị xóa !");
        }

        // Kiểm tra GiaMoiGio
        if (existing.getGiamoigio() != mt.getGiamoigio())
            if (existing.getTrangthai().equals("DANGDUNG")) {
                throw new RuntimeException("Máy đang sử dụng. Không thể sửa giá !");
            }

        //kiểm tra Valid
        validateMayTinh(mt);

        String sql = "UPDATE maytinh SET TenMay = ?, MaKhu = ?, CauHinh = ?, GiaMoiGio = ?" +
                     "WHERE MaMay = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, mt.getTenmay());
            pstmt.setString(2, mt.getMakhu());
            pstmt.setString(3, mt.getCauhinh());
            pstmt.setDouble(4, mt.getGiamoigio());
            pstmt.setString(5, mt.getMamay());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi update MayTinh: " + e.getMessage());
        }
        return true;
    }

    public boolean delete(String MaMay)
    {
        MayTinh mt = getByID(MaMay);

        //kiểm tra máy tính tồn tại
        if (mt == null) {
            throw new RuntimeException("Lỗi máy tính không tồn tại !");
        }

        //Máy tính đã bị xóa trước đó
        if (mt.getTrangthai().equals("NGUNG")) {
            throw new RuntimeException("Máy tính đã bị xóa !");
        }

        // Kiểm tra trạng thái
        if (mt.getTrangthai().equals("DANGDUNG")) {
            throw new RuntimeException("Máy đang được sử dụng");
        }

        String sql = "UPDATE maytinh SET TrangThai = 'NGUNG' WHERE MaMay = ? ";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, MaMay);

            int row = pstmt.executeUpdate();
            return row > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi delete MayTinh: " + e.getMessage());
        }
    }

    public boolean updateTrangThai(String MaMay, String TrangThai)
    {
        String sql = "UPDATE maytinh SET TrangThai = ? WHERE MaMay = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, TrangThai);
            pstmt.setString(2, MaMay);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Lỗi updateTrangThai MayTinh: " + e.getMessage());
        }
        return true;
    }

    //Tạo mã tự động
    public  String generateMaMay()
    {
        String sql = "SELECT MaMay FROM maytinh "+
                     "ORDER BY MaMay DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            ResultSet rs = pstmt.executeQuery(sql);

            if(rs.next())
            {
                String mamay = rs.getString("MaMay");
                int num = Integer.parseInt(mamay.substring(3));
                return String.format("MAY%03d", num + 1);
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi generateMaMay MayTinh" + e.getMessage());
        }
        //CHƯA CÓ DATABASE
        return "MAY001";
    }

    // Tìm máy theo mã
    public MayTinh getByID(String MaMay)
    {
        MayTinh mt = null;
        String sql = "SELECT * FROM maytinh WHERE MaMay = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, MaMay);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
                mt = mapResultSetToEntity(rs);
            rs.close();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getByID MayTinh: " + e.getMessage());
        }
        return mt;
    }

    public List<MayTinh> getByTrangThai(String TrangThai) {
        List<MayTinh> list = new ArrayList<>();
        String sql = "SELECT * FROM maytinh WHERE TrangThai = ? ORDER BY TenMay";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, TrangThai);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
            rs.close();

        } catch (SQLException e) {
            System.err.println("Lỗi getByTrangThai MayTinh: " + e.getMessage());
        }
        return list;
    }

    public int countByTrangThai(String TrangThai) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM maytinh WHERE TrangThai = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, TrangThai);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();

        } catch (SQLException e) {
            System.err.println("Lỗi countByTrangThai MayTinh: " + e.getMessage());
        }
        return count;
    }

    /*
    ======================VALIDATION=============
     */
    private void validateMayTinh(MayTinh mt) {
        // Validate TenMay
        if (mt.getTenmay() == null || mt.getTenmay().trim().isEmpty()) {
            throw new RuntimeException("Tên máy kmông được để trống");
        }
        if (isTenMayExists(mt.getTenmay())) {
            throw new RuntimeException("Tên máy đã tồn tại !");
        }

        // Validate GiaMoiGio
        if (mt.getGiamoigio() < 0) {
            throw new RuntimeException("Giá mỗi giờ không được nhỏ hơn 0");
        }

        // Validate MaKhu
        if (mt.getMakhu() == null || getTrangThaiKhuMay(mt.getMamay())) {
            throw new RuntimeException("Khu máy phải tồn tại và đang hoạt động");
        }
    }

    public boolean isTenMayExists(String tenmay){

        if (tenmay == null || tenmay.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM maytinh WHERE TenMay = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, tenmay);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
                return rs.getInt(1) > 0;
            rs.close();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi isTenMayExists MayTinh " + e.getMessage());
        }
        return false;
    }

    public boolean getTrangThaiKhuMay(String MaMay)
    {
        String sql = "SELECT km.TrangThai FROM maytinh mt " +
                     "JOIN khumay km ON km.MaKhu = mt.MaKhu " +
                     "WHERE MaMay = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, MaMay);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next())
                if (!rs.getString("TrangThai").equals("HOATDONG"))
                    return true;
            rs.close();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getTrangThaiKhuMay MayTinh " + e.getMessage());
        }
        return false;
    }

    public MayTinh mapResultSetToEntity(ResultSet rs) throws SQLException
    {
        MayTinh mt = new MayTinh();
        mt.setMamay(rs.getString("MaMay"));
        mt.setTenmay(rs.getString("TenMay"));
        mt.setMakhu(rs.getString("MaKhu"));
        mt.setCauhinh(rs.getString("CauHinh"));
        mt.setGiamoigio(rs.getDouble("GiaMoiGio"));

        try {
            mt.setTrangthai(rs.getString("TrangThai"));
        } catch (SQLException e) {
            mt.setTrangthai("TRONG");
        }
        return mt;
    }
}