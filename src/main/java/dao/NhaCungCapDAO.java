package dao;

import entity.NhaCungCap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class NhaCungCapDAO {

    // phương thức getAll
    public ArrayList<NhaCungCap> getAll(){
        ArrayList<NhaCungCap> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM NhaCungCap";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {


            while(rs.next()) {
                NhaCungCap ncc = new NhaCungCap(
                        rs.getString("maNCC"),
                        rs.getString("tenNCC"),
                        rs.getString("diaChi"),
                        rs.getString("soDienThoai"),
                        rs.getString("email")
                );
                danhSach.add(ncc);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return danhSach;
    }

    // phương thức getByID
    public NhaCungCap getByID(String maNCC){
        NhaCungCap ncc = null;
        String sql = "SELECT * FROM NhaCungCap WHERE maNCC = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maNCC);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                ncc = new NhaCungCap(
                        rs.getString("maNCC"),
                        rs.getString("tenNCC"),
                        rs.getString("diaChi"),
                        rs.getString("soDienThoai"),
                        rs.getString("email")
                );
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ncc;
    }

    // phương thức insert
    public boolean insert(NhaCungCap ncc){
        String sql = "INSERT INTO NhaCungCap (maNCC, tenNCC, diaChi, soDienThoai, email) "
                + "VALUES (?, ?, ?, ?, ?)";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ncc.getMaNCC());
            pstmt.setString(2, ncc.getTenNCC());
            pstmt.setString(3, ncc.getDiaChi());
            pstmt.setString(4, ncc.getSoDienThoai());
            pstmt.setString(5, ncc.getEmail());

            int rows = pstmt.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // phương thức update
    public boolean update(NhaCungCap ncc){
        String sql = "UPDATE NhaCungCap "
                + "SET tenNCC = ?, diaChi = ?, soDienThoai = ?, email = ? "
                + "WHERE maNCC = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ncc.getTenNCC());
            pstmt.setString(2, ncc.getDiaChi());
            pstmt.setString(3, ncc.getSoDienThoai());
            pstmt.setString(4, ncc.getEmail());
            pstmt.setString(5, ncc.getMaNCC());

            int rows = pstmt.executeUpdate();

            return rows > 0;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    // phương thức delete
    public boolean delete(String maNCC){
        String sql = "DELETE FROM NhaCungCap WHERE maNCC = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

           pstmt.setString(1, maNCC);

           int rows = pstmt.executeUpdate();

           return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}