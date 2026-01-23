package dao;

import entity.KhachHang;
import entity.NhanVien;

import java.net.ConnectException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {
    public List<KhachHang> getAll(){
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM khachhang ORDER BY MaKH DESC";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                KhachHang kh = mapResultSetToEntity(rs);
                list.add(kh);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Lỗi getALL KhachHang: "+ e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    public  KhachHang login(String tendangnhap,String matkhau){
        KhachHang kh = null;
        String sql = "SELECT * FROM khachhang "+
                    "WHERE TenDangNhap = ? AND MatKhau = ?";
        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1,tendangnhap);
            pstmt.setString(2,matkhau);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                kh = mapResultSetToEntity(rs);
                System.out.println("Đăng nhập thành công");

            }
            rs.close();
            pstmt.close();
        }
        catch (SQLException e){
            System.err.println("Lỗi login: " + e.getMessage());
            e.printStackTrace();
        }
        return kh;
    }
    public KhachHang getByTenDangNhap(String tenDN ){
        KhachHang kh = null;
        String sql = "SELECT * FROM khachhang"+
                    "WHERE TenDangNhap =?;";
        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1,tenDN);

            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                kh = mapResultSetToEntity(rs);
                System.out.println("Tìm thấy khách hàng : "+tenDN);
            }
            conn.close();
            pstmt.close();

        }catch (SQLException e){
            System.err.println("Lỗi tìm tên đăng nhập : "+e.getMessage());
            e.printStackTrace();
        }
        return kh;
    }
    public KhachHang getById(int maKH){
        KhachHang kh =null;
        String sql = "SELECT * FROM khachhang WHERE MaKH = ?";
        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                kh = mapResultSetToEntity(rs);
            }
            rs.close();
            pstmt.close();

        }catch (SQLException e){
            System.err.println("Lỗi getById KhachHang : " + e.getMessage());
            e.printStackTrace();
        }


        return kh;
    }
    public boolean insert(KhachHang kh ){
        String sql = "INSERT INTO khachhang (MaKH,Ho,Ten,SoDienThoai,TenDangNhap,MatKhau,SoDu) " +
                    "VALUE (?,?,?,?,?,?,?)";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, kh.getMakh());
            pstmt.setString(2,kh.getHo());
            pstmt.setString(3,kh.getTen());
            pstmt.setString(4,kh.getSodienthoai());
            pstmt.setString(5,kh.getTendangnhap());
            pstmt.setString(6,kh.getMatkhau());
            pstmt.setDouble(7,kh.getSodu());

            int rowUpdate = pstmt.executeUpdate();

            conn.close();
            pstmt.close();
            return rowUpdate > 0 ;
        }catch (SQLException e){
            System.err.println("Lỗi insert KhachHang : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public  String generateMaKH(){
        String sql = "SELECT MaKH FROM khachhang "+
                "ORDER BY MaKH LIMIT 1";
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if(rs.next()){
                String maKH = rs.getString("MaKH ");
                //LẤY TỪ VỊ TRÍ THỨ 2
                int num = Integer.parseInt(maKH.substring(2));
                //FORMAT CHO MÃ KHÁCH HÀNG
                return String.format("KH%03d" ,num + 1);
            }

            conn.close();
            stmt.close();
        }catch (SQLException e){
            System.err.println("Lỗi generateMaKH" + e.getMessage());
            e.printStackTrace();
        }
        //CHƯA CÓ DATABASE
        return  "KH001";
    }
    private KhachHang mapResultSetToEntity(ResultSet rs) throws SQLException {
        KhachHang kh = new KhachHang();
        kh.setMakh(rs.getString("MaKH"));
        kh.setHo(rs.getString("Ho"));
        kh.setTen(rs.getString("Ten"));
        kh.setSodienthoai(rs.getString("TenDangNhap"));
        kh.setMatkhau(rs.getString("MatKhau"));
        kh.setSodu(rs.getDouble("SoDu"));
        return kh;
    }

}