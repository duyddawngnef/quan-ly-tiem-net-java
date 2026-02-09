package bus;

import dao.*;
import entity.*;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NhapHangBUS {

    private PhieuNhapHangDAO phieuDAO = new PhieuNhapHangDAO();
    private ChiTietPhieuNhapDAO ctDAO = new ChiTietPhieuNhapDAO();
    private NhaCungCapDAO nccDAO = new NhaCungCapDAO();
    private DichVuDAO dvDAO = new DichVuDAO();

    // tạo phiếu nhập
    public PhieuNhapHang taoPhieuNhap(String maNCC, String maNV, List<ChiTietPhieuNhap> chiTietList) throws Exception {

        if (chiTietList == null || chiTietList.isEmpty()) {
            throw new Exception("Chi tiết phiếu nhập không được rỗng");
        }

        NhaCungCap ncc = nccDAO.getByID(maNCC);

        if (ncc == null || !ncc.getTrangThai().equals("HOATDONG")) {
            throw new Exception("Nhà cung cấp không hợp lệ");
        }

        double tongTien = 0;

        for (ChiTietPhieuNhap ct : chiTietList) {

            if (ct.getSoLuong() <= 0 || ct.getGiaNhap() <= 0) {
                throw new Exception("Số lượng và giá nhập phải > 0");
            }

            tongTien += ct.getSoLuong() * ct.getGiaNhap();
        }

        Connection conn = DBConnection.getConnection();

        try {
            conn.setAutoCommit(false);

            String maPhieu = generateId("PN");

            PhieuNhapHang pnh = new PhieuNhapHang(
                    maPhieu,
                    maNCC,
                    maNV,
                    LocalDate.now(),
                    tongTien,
                    "CHODUYET");

            // insert phiếu
            String sql = "INSERT INTO PhieuNhapHang VALUES (?, ?, ?, ?, ?, ?)";
            var pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, pnh.getMaPhieuNhap());
            pstmt.setString(2, pnh.getMaNCC());
            pstmt.setString(3, pnh.getMaNV());
            pstmt.setDate(4, java.sql.Date.valueOf(pnh.getNgayNhap()));
            pstmt.setDouble(5, pnh.getTongTien());
            pstmt.setString(6, pnh.getTrangThai());

            pstmt.executeUpdate();

            // insert chi tiết
            for (ChiTietPhieuNhap ct : chiTietList) {

                ct.setMaCTPN(generateId("CTPN"));
                ct.setMaPhieu(maPhieu);
                ct.tinhThanhTien();

                ctDAO.insert(ct);
            }

            conn.commit();

            return pnh;

        } catch (Exception e) {

            conn.rollback();
            throw e;

        } finally {
            conn.close();
        }
    }

    public boolean duyetPhieu(String maPhieu) throws Exception {

        Connection conn = DBConnection.getConnection();

        try {
            conn.setAutoCommit(false);

            PhieuNhapHang phieu = phieuDAO.getByID(maPhieu);

            if (phieu == null) {
                throw new Exception("Phiếu không tồn tại");
            }

            if (!phieu.getTrangThai().equals("CHODUYET")) {
                throw new Exception("Chỉ duyệt phiếu đang chờ");
            }

            ArrayList<ChiTietPhieuNhap> list = ctDAO.getByPhieu(maPhieu);

            for (ChiTietPhieuNhap ct : list) {
                dvDAO.updateSoLuongTon(conn, ct.getMaDV(), ct.getSoLuong());
            }

            String sql = "UPDATE PhieuNhapHang SET trangThai='DANHAP' WHERE maPhieuNhap=?";
            var pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, maPhieu);
            pstmt.executeUpdate();

            conn.commit();
            return true;

        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
    }

    // hủy phiếu
    public boolean huyPhieu(String maPhieu) throws Exception {

        Connection conn = DBConnection.getConnection();

        try {
            conn.setAutoCommit(false);

            PhieuNhapHang phieu = phieuDAO.getByID(maPhieu);

            if (phieu == null) {
                throw new Exception("Phiếu không tồn tại");
            }

            if (phieu.getTrangThai().equals("DAHUY")) {
                throw new Exception("Phiếu đã hủy");
            }

            ArrayList<ChiTietPhieuNhap> list = ctDAO.getByPhieu(maPhieu);

            // nếu đã nhập → trừ tồn
            if (phieu.getTrangThai().equals("DANHAP")) {
                for (ChiTietPhieuNhap ct : list) {
                    dvDAO.updateSoLuongTon(conn, ct.getMaDV(), -ct.getSoLuong());
                }
            }

            String sql = "UPDATE PhieuNhapHang SET trangThai='DAHUY' WHERE maPhieuNhap=?";
            var pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, maPhieu);
            pstmt.executeUpdate();

            conn.commit();
            return true;

        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
    }

    public ArrayList<PhieuNhapHang> getAllPhieuNhap() {
        return phieuDAO.getAll();
    }

    private String generateId(String prefix) {
        return prefix + System.currentTimeMillis();
    }
}