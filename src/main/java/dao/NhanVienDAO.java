package dao;

import entity.NhanVien;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {
    public List<NhanVien> getAll(){
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM nhanvien ORDER BY MaNV DESC";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                NhanVien nv = mapResultSetToEntity(rs);
                list.add(nv);
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getAll NhanVien: "+ e.getMessage());

        }
        return list;
    }

    //Lấy danh sách nhân viên đang làm việc
    public List<NhanVien> getAllDangLamViec(){
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM nhanvien WHERE TrangThai = 'DANGLAMVIEC' ORDER BY MaNV DESC";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                NhanVien nv = mapResultSetToEntity(rs);
                list.add(nv);
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getAllDangLamViec NhanVien: "+ e.getMessage());

        }
        return list;
    }

    //Lấy danh sách nhân viên đã nghỉ việc
    public List<NhanVien> getAllDaNghiViec(){
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM nhanvien WHERE TrangThai = 'NGHIVIEC' ORDER BY MaNV DESC";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                NhanVien nv = mapResultSetToEntity(rs);
                list.add(nv);
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getAllDaNghiViec NhanVien: "+ e.getMessage());

        }
        return list;
    }

    //Đăng nhập nhân viên
    public NhanVien login(String tendangnhap, String matkhau){
        NhanVien nv = null;

        if(tendangnhap == null || tendangnhap.trim().isEmpty()){
            throw new RuntimeException("Tên đăng nhập không được để trống !");
        }
        if(matkhau == null || matkhau.trim().isEmpty()){
            throw new RuntimeException("Mật khẩu không được để trống !");
        }


        String sql = "SELECT * FROM nhanvien "+
                "WHERE TenDangNhap = ? AND MatKhau = ?";
        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1,tendangnhap);
            pstmt.setString(2,matkhau);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                nv = mapResultSetToEntity(rs);
                if(nv.isNghiViec()){
                    rs.close();
                    pstmt.close();
                    conn.close();
                    throw new RuntimeException("Nhân viên đã nghỉ việc !");
                }
            }
            rs.close();
            pstmt.close();
            conn.close();
        }
        catch (SQLException e){
            throw new RuntimeException("Lỗi login NhanVien: " + e.getMessage());

        }
        return nv;
    }

    //Tìm nhân viên theo tên đăng nhập
    public NhanVien getByTenDangNhap(String tenDN ){
        NhanVien nv = null;
        String sql = "SELECT * FROM nhanvien "+
                "WHERE TenDangNhap = ?";
        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1,tenDN);

            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                nv = mapResultSetToEntity(rs);
            }
            rs.close();
            pstmt.close();
            conn.close();

        }catch (SQLException e){
            throw new RuntimeException("Lỗi getByTenDangNhap NhanVien: "+e.getMessage());

        }
        return nv;
    }

    //Tìm nhân viên theo Id
    public NhanVien getById(String MaNV){
        NhanVien nv = null;
        String sql = "SELECT * FROM nhanvien WHERE MaNV = ?";

        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);


            pstmt.setString(1,MaNV);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                nv = mapResultSetToEntity(rs);
            }

            rs.close();
            pstmt.close();
            conn.close();
        }catch (SQLException e){
            throw new RuntimeException("Lỗi getById NhanVien: " +e.getMessage());

        }
        return nv;
    }

    //Tìm kiếm nhân viên
    public List<NhanVien> search(String keyword){
        List<NhanVien> list = new ArrayList<>();

        if(keyword == null || keyword.trim().isEmpty()){
            return getAllDangLamViec();
        }

        String sql = "SELECT * FROM nhanvien "+
                "WHERE TrangThai = 'DANGLAMVIEC' AND "+
                "(Ho LIKE ? OR Ten LIKE ? OR TenDangNhap LIKE ? OR MaNV LIKE ? OR ChucVu LIKE ?) "+
                "ORDER BY MaNV DESC";
        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            String pattern = "%" + keyword.trim() + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);
            pstmt.setString(4, pattern);
            pstmt.setString(5, pattern);

            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                NhanVien nv = mapResultSetToEntity(rs);
                list.add(nv);
            }
            rs.close();
            pstmt.close();
            conn.close();

        }catch (SQLException e){
            throw new RuntimeException("Lỗi search NhanVien: "+e.getMessage());

        }
        return list;
    }

    /*
    ======================THÊM NHÂN VIÊN=============
    Chỉ QUANLY mới được thêm nhân viên
     */
    public boolean insert(NhanVien nv, NhanVien nguoiThucHien){

        //Kiểm tra quyền: chỉ QUANLY mới được thêm
        if(nguoiThucHien == null || !nguoiThucHien.isQuanLy()){
            throw new RuntimeException("Chỉ Quản lý mới có quyền thêm nhân viên !");
        }

        validateNhanVien(nv,true);

        if(isTenDangNhapExists(nv.getTendangnhap())){
            throw new RuntimeException("Tên đăng nhập đã tồn tại !");
        }


        String sql = "INSERT INTO nhanvien (MaNV,Ho,Ten,ChucVu,TenDangNhap,MatKhau,TrangThai) " +
                "VALUES (?,?,?,?,?,?,?)";


        //tạo mã nhân viên tự động
        String maNV = generateMaNV();
        nv.setManv(maNV);

        //set trạng thái = đang làm việc
        nv.setTrangthai("DANGLAMVIEC");

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, nv.getManv());
            pstmt.setString(2, nv.getHo());
            pstmt.setString(3, nv.getTen());
            pstmt.setString(4, nv.getChucvu());
            pstmt.setString(5, nv.getTendangnhap());
            pstmt.setString(6, nv.getMatkhau());
            pstmt.setString(7, nv.getTrangthai());

            int rowUpdate = pstmt.executeUpdate();

            pstmt.close();
            conn.close();
            return rowUpdate > 0 ;
        }catch (SQLException e){
            throw new RuntimeException("Lỗi insert NhanVien: " + e.getMessage());

        }
    }

    /*
    ======================CẬP NHẬT NHÂN VIÊN=============
    - QUANLY: Sửa được tất cả NV
    - NV khác: Chỉ sửa được thông tin của mình (trừ ChucVu)
     */
    public boolean update(NhanVien nv, NhanVien nguoiThucHien){

        NhanVien existing = getById(nv.getManv());

        //kiểm tra nhân viên tồn tại
        if(existing == null){
            throw new RuntimeException("Nhân viên không tồn tại !");
        }

        //nhân viên đã nghỉ việc
        if(existing.isNghiViec()){
            throw new RuntimeException("Nhân viên đã nghỉ việc, không thể sửa !");
        }

        //Kiểm tra quyền
        if(nguoiThucHien == null){
            throw new RuntimeException("Người thực hiện không hợp lệ !");
        }

        //Nếu không phải QUANLY thì chỉ được sửa thông tin của chính mình
        if(!nguoiThucHien.isQuanLy()){
            if(!nguoiThucHien.getManv().equals(nv.getManv())){
                throw new RuntimeException("Bạn chỉ có thể sửa thông tin của chính mình !");
            }
            //Không cho sửa chức vụ
            if(!existing.getChucvu().equals(nv.getChucvu())){
                throw new RuntimeException("Bạn không có quyền thay đổi chức vụ !");
            }
        }

        //kiểm tra Valid
        validateNhanVien(nv,false);


        String sql = "UPDATE nhanvien SET Ho = ?, Ten = ?, ChucVu = ?, MatKhau = ? WHERE MaNV = ? AND TrangThai = 'DANGLAMVIEC'";

        try{

            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, nv.getHo());
            pstmt.setString(2, nv.getTen());
            pstmt.setString(3, nv.getChucvu());
            pstmt.setString(4, nv.getMatkhau());
            pstmt.setString(5, nv.getManv());

            int row = pstmt.executeUpdate();
            pstmt.close();
            conn.close();

            return row > 0;

        }catch (SQLException e){
            throw new RuntimeException("Lỗi update NhanVien: " + e.getMessage());
        }
    }

    /*
    ======================XÓA NHÂN VIÊN (SOFT DELETE)==============
    - Chỉ QUANLY mới được xóa
    - Không được xóa QUANLY duy nhất còn DANGLAMVIEC
     */
    public boolean delete(String MaNV, NhanVien nguoiThucHien){
        NhanVien nv = getById(MaNV);

        //nhân viên không tồn tại
        if(nv == null){
            throw new RuntimeException("Nhân viên không tồn tại !");
        }

        //đã nghỉ việc trước đó
        if(nv.isNghiViec()){
            throw new RuntimeException("Nhân viên đã nghỉ việc trước đó !");
        }

        //Kiểm tra quyền: chỉ QUANLY mới được xóa
        if(nguoiThucHien == null || !nguoiThucHien.isQuanLy()){
            throw new RuntimeException("Chỉ Quản lý mới có quyền xóa nhân viên !");
        }

        //Không được tự xóa chính mình
        if(nguoiThucHien.getManv().equals(MaNV)){
            throw new RuntimeException("Bạn không thể xóa chính mình !");
        }

        //Kiểm tra nếu NV cần xóa là QUANLY duy nhất còn DANGLAMVIEC
        if(nv.isQuanLy()){
            int soQuanLyConLai = countQuanLyDangLamViec();
            if(soQuanLyConLai <= 1){
                throw new RuntimeException("Không thể xóa quản lý duy nhất còn lại !");
            }
        }

        String sql = "UPDATE nhanvien SET TrangThai = 'NGHIVIEC' WHERE MaNV = ?";
        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, MaNV);

            int row = pstmt.executeUpdate();


            pstmt.close();
            conn.close();


            return row > 0;


        }catch (SQLException e){
            throw new RuntimeException("Lỗi delete NhanVien: " + e.getMessage());
        }

    }

    //Cảnh báo trước khi xóa
    public String getDeleteWarning(String MaNV, NhanVien nguoiThucHien){
        NhanVien nv = getById(MaNV);

        if(nv == null){
            return "Nhân viên không tồn tại !";
        }

        StringBuilder warning = new StringBuilder();

        //Cảnh báo nếu xóa quản lý
        if(nv.isQuanLy()){
            int soQuanLy = countQuanLyDangLamViec();
            if(soQuanLy <= 1){
                return "Không thể xóa quản lý duy nhất còn lại !";
            }
            warning.append("Đây là tài khoản Quản lý.\n");
        }

        if(warning.length() > 0 ) {
            warning.append("Bạn có chắc muốn cho nhân viên này nghỉ việc ?");
            return warning.toString();
        }
        return null;

    }

    /*
    ======================KHÔI PHỤC NHÂN VIÊN=============
    Chỉ QUANLY mới được khôi phục
     */
    public boolean restore(String MaNV, NhanVien nguoiThucHien){
        NhanVien nv = getById(MaNV);

        if(nv == null) {
            throw new RuntimeException("Nhân viên không tồn tại !");
        }

        //chưa nghỉ việc
        if(!nv.isNghiViec()){
            throw new RuntimeException("Nhân viên đang làm việc, không cần khôi phục !");
        }

        //Kiểm tra quyền: chỉ QUANLY mới được khôi phục
        if(nguoiThucHien == null || !nguoiThucHien.isQuanLy()){
            throw new RuntimeException("Chỉ Quản lý mới có quyền khôi phục nhân viên !");
        }

        String sql = "UPDATE nhanvien SET TrangThai = 'DANGLAMVIEC' WHERE MaNV = ?";
        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);


            pstmt.setString(1, MaNV);

            int row = pstmt.executeUpdate();


            pstmt.close();
            conn.close();


            return row > 0;


        }catch (SQLException e){
            throw new RuntimeException("Lỗi restore NhanVien: " + e.getMessage());
        }

    }

    //Tạo mã tự động
    public String generateMaNV(){
        String sql = "SELECT MaNV FROM nhanvien "+
                "ORDER BY MaNV DESC LIMIT 1";
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if(rs.next()){
                String maNV = rs.getString("MaNV");
                //LẤY TỪ VỊ TRÍ THỨ 2
                int num = Integer.parseInt(maNV.substring(2));
                //FORMAT CHO MÃ NHÂN VIÊN

                rs.close();
                stmt.close();
                conn.close();

                return String.format("NV%03d" ,num + 1);
            }

            rs.close();
            stmt.close();
            conn.close();
        }catch (SQLException e){
            throw new RuntimeException("Lỗi generateMaNV: " + e.getMessage());

        }
        //CHƯA CÓ DATABASE
        return "NV001";
    }

    //Đếm số quản lý đang làm việc
    private int countQuanLyDangLamViec(){
        String sql = "SELECT COUNT(*) FROM nhanvien WHERE ChucVu = 'QUANLY' AND TrangThai = 'DANGLAMVIEC'";
        try{
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            int count = 0;
            if(rs.next()){
                count = rs.getInt(1);
            }

            rs.close();
            stmt.close();
            conn.close();

            return count;

        }catch (SQLException e){
            throw new RuntimeException("Lỗi countQuanLyDangLamViec: " + e.getMessage());
        }
    }


    /*
    ======================VALIDATION=============
     */
    private void validateNhanVien(NhanVien nv, boolean isInsert){
        // Validate Ho
        if (nv.getHo() == null || nv.getHo().trim().isEmpty()) {
            throw new RuntimeException("Họ không được để trống");
        }
        if (nv.getHo().trim().length() > 50) {
            throw new RuntimeException("Họ không được vượt quá 50 ký tự");
        }

        // Validate Ten
        if (nv.getTen() == null || nv.getTen().trim().isEmpty()) {
            throw new RuntimeException("Tên không được để trống");
        }
        if (nv.getTen().trim().length() > 50) {
            throw new RuntimeException("Tên không được vượt quá 50 ký tự");
        }

        // Validate ChucVu
        if (nv.getChucvu() == null || nv.getChucvu().trim().isEmpty()) {
            throw new RuntimeException("Chức vụ không được để trống");
        }
        String chucVu = nv.getChucvu().trim().toUpperCase();
        if (!chucVu.equals("QUANLY") && !chucVu.equals("NHANVIEN") && !chucVu.equals("THUNGAN")) {
            throw new RuntimeException("Chức vụ phải là QUANLY, NHANVIEN hoặc THUNGAN");
        }

        // Validate TenDangNhap (chỉ khi Insert)
        if (isInsert) {
            if (nv.getTendangnhap() == null || nv.getTendangnhap().trim().isEmpty()) {
                throw new RuntimeException("Tên đăng nhập không được để trống");
            }
            String tenDN = nv.getTendangnhap().trim();
            if (tenDN.length() < 4) {
                throw new RuntimeException("Tên đăng nhập phải có ít nhất 4 ký tự");
            }
            if (tenDN.length() > 50) {
                throw new RuntimeException("Tên đăng nhập không được vượt quá 50 ký tự");
            }
            if (!tenDN.matches("^[a-zA-Z0-9_]+$")) {
                throw new RuntimeException("Tên đăng nhập chỉ được chứa chữ cái, số và dấu gạch dưới");
            }
        }

        // Validate MatKhau
        if (nv.getMatkhau() == null || nv.getMatkhau().isEmpty()) {
            throw new RuntimeException("Mật khẩu không được để trống");
        }
        if (nv.getMatkhau().length() < 6) {
            throw new RuntimeException("Mật khẩu phải có ít nhất 6 ký tự");
        }


    }

    public boolean isTenDangNhapExists(String tendangnhap){

        if(tendangnhap == null || tendangnhap.trim().isEmpty()){
            return false;
        }


        String sql = "SELECT COUNT(*) FROM nhanvien WHERE TenDangNhap = ?";


        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1,tendangnhap);

            ResultSet rs = pstmt.executeQuery();

            boolean exists = false;
            if(rs.next()){
                exists = rs.getInt(1) > 0;
            }

            rs.close();
            pstmt.close();
            conn.close();

            return exists;

        }catch (SQLException e){
            throw new RuntimeException("Lỗi isTenDangNhapExists NhanVien: " + e.getMessage());
        }

    }

    //Chuyển từ ResultSet -> NhanVien
    private NhanVien mapResultSetToEntity(ResultSet rs) throws SQLException {
        NhanVien nv = new NhanVien();
        nv.setManv(rs.getString("MaNV"));
        nv.setHo(rs.getString("Ho"));
        nv.setTen(rs.getString("Ten"));
        nv.setChucvu(rs.getString("ChucVu"));
        nv.setTendangnhap(rs.getString("TenDangNhap"));
        nv.setMatkhau(rs.getString("MatKhau"));
        nv.setTrangthai(rs.getString("TrangThai"));

        return nv;
    }
}