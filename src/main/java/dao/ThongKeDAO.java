package dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class ThongKeDAO {

    // thống kê doanh thu
    public Map<String, Object> thongKeDoanhThu(LocalDateTime tu, LocalDateTime den) throws Exception {

        String sql = """
                SELECT 
                    COALESCE(SUM(ThanhToan),0) as TongDoanhThu,
                    COALESCE(SUM(TienGioChoi),0) as TongTienGioChoi,
                    COALESCE(SUM(TienDichVu),0) as TongTienDichVu,
                    COUNT(*) as SoHoaDon
                FROM HoaDon
                WHERE ThoiDiemThanhToan >= ?
                AND ThoiDiemThanhToan < ?
                """;

        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setTimestamp(1, Timestamp.valueOf(tu));
        ps.setTimestamp(2, Timestamp.valueOf(den));

        ResultSet rs = ps.executeQuery();

        Map<String, Object> result = new HashMap<>();

        if (rs.next()) {

            double tongDoanhThu = rs.getDouble("TongDoanhThu");
            double tongTienGioChoi = rs.getDouble("TongTienGioChoi");
            double tongTienDichVu = rs.getDouble("TongTienDichVu");
            int soHoaDon = rs.getInt("SoHoaDon");

            result.put("TongDoanhThu", tongDoanhThu);
            result.put("TongTienGioChoi", tongTienGioChoi);
            result.put("TongTienDichVu", tongTienDichVu);
            result.put("SoHoaDon", soHoaDon);
        }

        rs.close();
        ps.close();
        conn.close();

        return result;
    }


    // top dịch vụ bán chạy
    public List<Map<String, Object>> topDichVu(LocalDateTime tu, LocalDateTime den, int top) throws Exception {

        String sql = """
                SELECT dv.MaDV,
                       dv.TenDV,
                       SUM(sd.SoLuong) as TongSoLuong,
                       SUM(sd.ThanhTien) as TongDoanhThu
                FROM SuDungDichVu sd
                JOIN DichVu dv ON sd.MaDV = dv.MaDV
                JOIN PhienSuDung ps ON sd.MaPhien = ps.MaPhien
                WHERE ps.GioKetThuc >= ?
                AND ps.GioKetThuc < ?
                GROUP BY dv.MaDV, dv.TenDV
                ORDER BY TongSoLuong DESC
                LIMIT ?
                """;

        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setTimestamp(1, Timestamp.valueOf(tu));
        ps.setTimestamp(2, Timestamp.valueOf(den));
        ps.setInt(3, top);

        ResultSet rs = ps.executeQuery();

        List<Map<String, Object>> list = new ArrayList<>();

        while (rs.next()) {

            Map<String, Object> dv = new HashMap<>();

            dv.put("MaDV", rs.getString("MaDV"));
            dv.put("TenDV", rs.getString("TenDV"));
            dv.put("TongSoLuong", rs.getInt("TongSoLuong"));
            dv.put("TongDoanhThu", rs.getDouble("TongDoanhThu"));

            list.add(dv);
        }

        rs.close();
        ps.close();
        conn.close();

        return list;
    }


    // Doanh thu hom nay
    public double doanhThuHomNay() throws Exception {

        String sql = """
                SELECT COALESCE(SUM(ThanhToan),0)
                FROM HoaDon
                WHERE DATE(ThoiDiemThanhToan) = CURRENT_DATE
                """;

        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();

        double doanhThu = 0;

        if (rs.next()) {
            doanhThu = rs.getDouble(1);
        }

        rs.close();
        ps.close();
        conn.close();

        return doanhThu;
    }


    // doanh thu tháng
    public double doanhThuThang(int thang, int nam) throws Exception {

        String sql = """
                SELECT COALESCE(SUM(ThanhToan),0)
                FROM HoaDon
                WHERE MONTH(ThoiDiemThanhToan) = ?
                AND YEAR(ThoiDiemThanhToan) = ?
                """;

        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setInt(1, thang);
        ps.setInt(2, nam);

        ResultSet rs = ps.executeQuery();

        double doanhThu = 0;

        if (rs.next()) {
            doanhThu = rs.getDouble(1);
        }

        rs.close();
        ps.close();
        conn.close();

        return doanhThu;
    }
}