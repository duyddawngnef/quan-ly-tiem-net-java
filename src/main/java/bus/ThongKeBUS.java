package bus;

import dao.KhachHangDAO;
import dao.MayTinhDAO;
import dao.PhienSuDungDAO;
import dao.ThongKeDAO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ThongKeBUS {

    private ThongKeDAO thongKeDAO = new ThongKeDAO();
    private MayTinhDAO mayTinhDAO = new MayTinhDAO();
    private KhachHangDAO khachHangDAO = new KhachHangDAO();
    private PhienSuDungDAO phienSuDungDAO = new PhienSuDungDAO();

    // check quyền
    private void checkQuanLy(String vaiTro) throws Exception {
        if (!vaiTro.equals("QUANLY")) {
            throw new Exception("Không có quyền truy cập");
        }
    }

    private void checkQuanLyNhanVien(String vaiTro) throws Exception {
        if (!vaiTro.equals("QUANLY") && !vaiTro.equals("NHANVIEN")) {
            throw new Exception("Không có quyền truy cập");
        }
    }

    // thống kê doanh thu
    public Map<String, Object> thongKeDoanhThu(
            LocalDate tuNgay,
            LocalDate denNgay,
            String vaiTro) throws Exception {

        checkQuanLy(vaiTro);

        if (tuNgay.isAfter(denNgay)) {
            throw new Exception("Từ ngày phải <= đến ngày");
        }

        LocalDateTime tu = tuNgay.atStartOfDay();
        LocalDateTime den = denNgay.plusDays(1).atStartOfDay();

        Map<String, Object> result = thongKeDAO.thongKeDoanhThu(tu, den);

        long soNgay = ChronoUnit.DAYS.between(tuNgay, denNgay) + 1;

        double tongDoanhThu = (double) result.get("TongDoanhThu");

        double doanhThuTrungBinh = soNgay == 0
                ? 0
                : tongDoanhThu / soNgay;

        result.put("DoanhThuTrungBinh", doanhThuTrungBinh);

        return result;
    }

    // thống kê dịch vụ bán chạy
    public List<Map<String, Object>> thongKeDichVuBanChay(
            LocalDate tuNgay,
            LocalDate denNgay,
            int top,
            String vaiTro) throws Exception {

        checkQuanLy(vaiTro);

        LocalDateTime tu = tuNgay.atStartOfDay();
        LocalDateTime den = denNgay.plusDays(1).atStartOfDay();

        return thongKeDAO.topDichVu(tu, den, top);
    }

    //thống kê tổng quan
    public Map<String, Object> thongKeTongQuan(String vaiTro) throws Exception {

        checkQuanLyNhanVien(vaiTro);

        Map<String, Object> result = new HashMap<>();

        int tongMay = mayTinhDAO.count();
        int mayDangDung = mayTinhDAO.countByStatus("DANGDUNG");
        int mayTrong = mayTinhDAO.countByStatus("TRONG");

        int tongKH = khachHangDAO.count();
        int khHoatDong = khachHangDAO.countByStatus("HOATDONG");

        int phienDangChoi = phienSuDungDAO.countDangChoi();

        double doanhThuHomNay = thongKeDAO.doanhThuHomNay();
        double doanhThuThang = thongKeDAO.doanhThuThang(
                LocalDate.now().getMonthValue(),
                LocalDate.now().getYear());

        result.put("TongMay", tongMay);
        result.put("MayDangDung", mayDangDung);
        result.put("MayTrong", mayTrong);

        result.put("TongKhachHang", tongKH);
        result.put("KhachHangHoatDong", khHoatDong);

        result.put("PhienDangChoi", phienDangChoi);

        result.put("DoanhThuHomNay", doanhThuHomNay);
        result.put("DoanhThuThang", doanhThuThang);

        return result;
    }

}