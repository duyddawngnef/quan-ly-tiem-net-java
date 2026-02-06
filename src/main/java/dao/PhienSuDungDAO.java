package dao;

import quanlytiemnet.java.entity.PhienSuDung;
import utils.DBConnection;

import java.sql.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PhienSuDungDAO {

    public List<PhienSuDung> getAll() {
        List<PhienSuDung> list = new ArrayList<>();
        String sql = "SELECT * FROM PhienSuDung";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                PhienSuDung p = new PhienSuDung();
                p.setMaPhien(rs.getInt("maPhien"));
                p.setMaMay(rs.getInt("maMay"));
                p.setThoiGianBatDau(rs.getTimestamp("batDau").toLocalDateTime());
                p.setThoiGianKetThuc(rs.getTimestamp("ketThuc").toLocalDateTime());

                list.add(p);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

>>>>>>>HEAD
    public boolean insert(PhienSuDung p) {
        String sql = "INSERT INTO PhienSuDung(maMay, batDau) VALUES(?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, p.getMaMay());
            ps.setTimestamp(2, Timestamp.valueOf(p.getThoiGianBatDau()));
            ps.setTimestamp(3, Timestamp.valueOf(p.getThoiGianKetThuc()));

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean closeSession(int maPhien, Timestamp ketThuc) {
        String sql = "UPDATE PhienSuDung SET ketThuc=? WHERE maPhien=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, ketThuc);
            ps.setInt(2, maPhien);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}

    /**
     * Lấy phiên sử dụng theo mã phiên
     *
     * @param maPhien Mã phiên cần tìm
     * @return PhienSuDung hoặc null nếu không tìm thấy
     * @throws SQLException nếu có lỗi database
     */
    public PhienSuDung getByMaPhien(String maPhien) throws SQLException {
        String sql = "SELECT * FROM phiensudung WHERE maPhien = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

        pst.setString(1, maPhien);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPhienSuDung(rs);
                }
            }
        }

        return null;
    }

    /**
     * Thêm phiên sử dụng mới
     *
     * @param phien PhienSuDung cần thêm
     * @return true nếu thêm thành công
     * @throws SQLException nếu có lỗi database
     */
    public boolean insert(PhienSuDung phien) throws SQLException {
        String sql = "INSERT INTO phiensudung (maPhien, maKH, maMay, maNV, maGoiKH, " +
                "gioBatDau, gioKetThuc, tongGio, gioSuDungTuGoi, gioSuDungTuTaiKhoan, " +
                "giaMoiGio, tienGioChoi, loaiThanhToan, trangThai) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            setPhienSuDungParameters(pst, phien);

            return pst.executeUpdate() > 0;
        }
    }

    /**
