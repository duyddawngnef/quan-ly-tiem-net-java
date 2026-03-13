package dao;

import entity.ThongKe;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ThongKeDAO {

    public ThongKe getSummary(LocalDateTime tu, LocalDateTime den) throws Exception {
        String sql = """
                SELECT
                    COALESCE(SUM(h.ThanhToan), 0)    AS Thu,
                    COALESCE(SUM(n.TongTienNhap), 0) AS Chi,
                    COUNT(DISTINCT h.MaHD)            AS SoPhien
                FROM hoadon h
                LEFT JOIN (
                    SELECT DATE(NgayNhap) AS NgayNhap, SUM(TongTien) AS TongTienNhap
                    FROM PhieuNhapHang
                    GROUP BY DATE(NgayNhap)
                ) n ON DATE(h.NgayLap) = n.NgayNhap
                WHERE h.NgayLap >= ? AND h.NgayLap < ?
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(tu));
            ps.setTimestamp(2, Timestamp.valueOf(den));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ThongKe(
                            rs.getDouble("Thu"),
                            rs.getDouble("Chi"),
                            rs.getInt("SoPhien"));
                }
            }
        }
        return new ThongKe(0, 0, 0);
    }

    // ===== Tab 1: Theo ngày =====
    public List<ThongKe> thongKeTheoNgay(LocalDateTime tu, LocalDateTime den) throws Exception {
        String sql = """
                SELECT
                    DATE(h.NgayLap)                  AS ThoiGian,
                    COALESCE(SUM(h.ThanhToan), 0)    AS Thu,
                    COALESCE(SUM(n.TongTienNhap), 0) AS Chi
                FROM hoadon h
                LEFT JOIN (
                    SELECT DATE(NgayNhap) AS NgayNhap, SUM(TongTien) AS TongTienNhap
                    FROM PhieuNhapHang
                    GROUP BY DATE(NgayNhap)
                ) n ON DATE(h.NgayLap) = n.NgayNhap
                WHERE h.NgayLap >= ? AND h.NgayLap < ?
                GROUP BY DATE(h.NgayLap)
                ORDER BY ThoiGian
                """;
        return queryThongKe(sql, tu, den);
    }

    // ===== Tab 1: Theo tháng =====
    public List<ThongKe> thongKeTheoThang(LocalDateTime tu, LocalDateTime den) throws Exception {
        String sql = """
                SELECT
                    DATE_FORMAT(h.NgayLap, '%Y-%m')  AS ThoiGian,
                    COALESCE(SUM(h.ThanhToan), 0)    AS Thu,
                    COALESCE(SUM(n.TongTienNhap), 0) AS Chi
                FROM hoadon h
                LEFT JOIN (
                    SELECT DATE_FORMAT(NgayNhap, '%Y-%m') AS NgayNhap, SUM(TongTien) AS TongTienNhap
                    FROM PhieuNhapHang
                    GROUP BY DATE_FORMAT(NgayNhap, '%Y-%m')
                ) n ON DATE_FORMAT(h.NgayLap, '%Y-%m') = n.NgayNhap
                WHERE h.NgayLap >= ? AND h.NgayLap < ?
                GROUP BY DATE_FORMAT(h.NgayLap, '%Y-%m')
                ORDER BY ThoiGian
                """;
        return queryThongKe(sql, tu, den);
    }

    // ===== Tab 2: Thu Chi =====
    public List<ThongKe> thongKeThuChi(LocalDateTime tu, LocalDateTime den) throws Exception {
        String sql = """
                SELECT
                    DATE(h.NgayLap)                  AS ThoiGian,
                    COALESCE(SUM(h.ThanhToan), 0)    AS Thu,
                    COALESCE(SUM(n.TongTienNhap), 0) AS Chi
                FROM hoadon h
                LEFT JOIN (
                    SELECT DATE(NgayNhap) AS NgayNhap, SUM(TongTien) AS TongTienNhap
                    FROM PhieuNhapHang
                    GROUP BY DATE(NgayNhap)
                ) n ON DATE(h.NgayLap) = n.NgayNhap
                WHERE h.NgayLap >= ? AND h.NgayLap < ?
                GROUP BY DATE(h.NgayLap)
                ORDER BY ThoiGian
                """;
        return queryThongKe(sql, tu, den);
    }

    // ===== Tab 3: Top Khách Hàng =====
    // Object[]: [0]=MaKH(String), [1]=TenKH(String), [2]=SoLanDen(int), [3]=TongChiTieu(double)
    public List<Object[]> topKhachHang(int nam, int n) throws Exception {
        String sql = """
                SELECT
                    kh.MaKH,
                    kh.TenKH,
                    COUNT(DISTINCT ps.MaPhien)    AS SoLanDen,
                    COALESCE(SUM(h.ThanhToan), 0) AS TongChiTieu
                FROM KhachHang kh
                JOIN PhienSuDung ps ON kh.MaKH    = ps.MaKH
                JOIN hoadon      h  ON ps.MaPhien = h.MaPhien
                WHERE YEAR(h.NgayLap) = ?
                GROUP BY kh.MaKH, kh.TenKH
                ORDER BY TongChiTieu DESC
                LIMIT ?
                """;
        List<Object[]> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nam);
            ps.setInt(2, n);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                            rs.getString("MaKH"),
                            rs.getString("TenKH"),
                            rs.getInt("SoLanDen"),
                            rs.getDouble("TongChiTieu")
                    });
                }
            }
        }
        return list;
    }

    // ===== Top Dịch Vụ =====
    public List<Object[]> topDichVu(LocalDateTime tu, LocalDateTime den, int top) throws Exception {
        String sql = """
                SELECT
                    dv.MaDV,
                    dv.TenDV,
                    SUM(sd.SoLuong)   AS TongSoLuong,
                    SUM(sd.ThanhTien) AS TongDoanhThu
                FROM SuDungDichVu sd
                JOIN DichVu      dv ON sd.MaDV    = dv.MaDV
                JOIN PhienSuDung ps ON sd.MaPhien = ps.MaPhien
                WHERE ps.GioKetThuc >= ? AND ps.GioKetThuc < ?
                GROUP BY dv.MaDV, dv.TenDV
                ORDER BY TongSoLuong DESC
                LIMIT ?
                """;
        List<Object[]> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(tu));
            ps.setTimestamp(2, Timestamp.valueOf(den));
            ps.setInt(3, top);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                            rs.getString("MaDV"),
                            rs.getString("TenDV"),
                            rs.getInt("TongSoLuong"),
                            rs.getDouble("TongDoanhThu")
                    });
                }
            }
        }
        return list;
    }

    // ===== Helper =====
    private List<ThongKe> queryThongKe(String sql,
                                       LocalDateTime tu,
                                       LocalDateTime den) throws Exception {
        List<ThongKe> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(tu));
            ps.setTimestamp(2, Timestamp.valueOf(den));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ThongKe(
                            rs.getString("ThoiGian"),
                            rs.getDouble("Thu"),
                            rs.getDouble("Chi")));
                }
            }
        }
        return list;
    }

    public double doanhThuHomNay() throws Exception {
        String sql = "SELECT COALESCE(SUM(ThanhToan),0) FROM hoadon WHERE DATE(NgayLap) = CURRENT_DATE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0;
        }
    }

    public double doanhThuThang(int thang, int nam) throws Exception {
        String sql = "SELECT COALESCE(SUM(ThanhToan),0) FROM hoadon WHERE MONTH(NgayLap)=? AND YEAR(NgayLap)=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, thang);
            ps.setInt(2, nam);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        }
    }
}