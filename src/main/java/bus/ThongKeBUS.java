package bus;

import dao.KhachHangDAO;
import dao.MayTinhDAO;
import dao.PhienSuDungDAO;
import dao.ThongKeDAO;
import entity.KhachHang;
import entity.ThongKe;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ThongKeBUS {

    private final ThongKeDAO thongKeDAO = new ThongKeDAO();
    private final MayTinhDAO mayTinhDAO = new MayTinhDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final PhienSuDungDAO phienSuDungDAO = new PhienSuDungDAO();

    // Kiểm tra quyền
    private void checkQuanLy(String vaiTro) throws Exception {
        if (!"QUANLY".equals(vaiTro))
            throw new Exception("Không có quyền truy cập");
    }

    private void checkQuanLyNhanVien(String vaiTro) throws Exception {
        if (!"QUANLY".equals(vaiTro) && !"NHANVIEN".equals(vaiTro))
            throw new Exception("Không có quyền truy cập");
    }

    // Summary Cards (ThongKeController.loadSummaryCards)
    public ThongKe getSummary(LocalDate from, LocalDate to) throws Exception {
        LocalDateTime tu = from.atStartOfDay();
        LocalDateTime den = to.plusDays(1).atStartOfDay();
        return thongKeDAO.getSummary(tu, den);
    }

    // Tab 1 — Doanh thu (handleThongKe)
    public List<ThongKe> thongKe(String loai, LocalDate from, LocalDate to) throws Exception {
        if (from == null || to == null)
            throw new Exception("Vui lòng chọn ngày");
        if (from.isAfter(to))
            throw new Exception("Từ ngày phải ≤ đến ngày");

        LocalDateTime tu = from.atStartOfDay();
        LocalDateTime den = to.plusDays(1).atStartOfDay();

        if ("Theo tháng".equals(loai))
            return thongKeDAO.thongKeTheoThang(tu, den);

        return thongKeDAO.thongKeTheoNgay(tu, den);
    }

    // Tab 2 — Thu Chi (handleThongKeThuChi)
    public List<ThongKe> thongKeThuChi(String period,
                                       LocalDate from,
                                       LocalDate to) throws Exception {
        LocalDate[] range = resolvePeriod(period, from, to);
        LocalDateTime tu = range[0].atStartOfDay();
        LocalDateTime den = range[1].plusDays(1).atStartOfDay();
        return thongKeDAO.thongKeThuChi(tu, den);
    }

    // Tab 3 — Top Khách Hàng (handleThongKeTop)
    public List<Object[]> topKhachHang(int nam, int n) throws Exception {
        if (n <= 0)
            throw new Exception("Số lượng top phải > 0");
        return thongKeDAO.topKhachHang(nam, n);
    }

    // Thống kê doanh thu (có kiểm tra quyền)
    public ThongKe thongKeDoanhThu(LocalDate tuNgay,
                                   LocalDate denNgay,
                                   String vaiTro) throws Exception {
        checkQuanLy(vaiTro);
        if (tuNgay.isAfter(denNgay))
            throw new Exception("Từ ngày phải <= đến ngày");

        LocalDateTime tu = tuNgay.atStartOfDay();
        LocalDateTime den = denNgay.plusDays(1).atStartOfDay();
        return thongKeDAO.getSummary(tu, den);
    }

    // Thống kê dịch vụ bán chạy (có kiểm tra quyền)
    // Object[]: [0]=MaDV, [1]=TenDV, [2]=TongSoLuong, [3]=TongDoanhThu
    public List<Object[]> thongKeDichVuBanChay(LocalDate tuNgay,
            LocalDate denNgay,
            int top,
            String vaiTro) throws Exception {
        checkQuanLy(vaiTro);
        LocalDateTime tu = tuNgay.atStartOfDay();
        LocalDateTime den = denNgay.plusDays(1).atStartOfDay();
        return thongKeDAO.topDichVu(tu, den, top);
    }

    // Thống kê tổng quan (có kiểm tra quyền)
    public ThongKe thongKeTongQuan(String vaiTro) throws Exception {
        checkQuanLyNhanVien(vaiTro);

        int tongMay = mayTinhDAO.countByTrangThai("TRONG")
                + mayTinhDAO.countByTrangThai("DANGDUNG")
                + mayTinhDAO.countByTrangThai("NGUNG");
        int mayDangDung = mayTinhDAO.countByTrangThai("DANGDUNG");
        int mayTrong = mayTinhDAO.countByTrangThai("TRONG");

        List<KhachHang> danhSachKH = khachHangDAO.getAll();
        int tongKH = danhSachKH.size();
        int khHoatDong = 0;
        for (KhachHang kh : danhSachKH) {
            if ("HOATDONG".equals(kh.getTrangthai()))
                khHoatDong++;
        }

        int phienDangChoi = phienSuDungDAO.countByTrangThai("DANGCHOI");
        double doanhThuHomNay = thongKeDAO.doanhThuHomNay();
        double doanhThuThang = thongKeDAO.doanhThuThang(
                LocalDate.now().getMonthValue(),
                LocalDate.now().getYear());

        // Đóng gói vào ThongKeDoanhThu (dùng constructor Summary)
        ThongKe tk = new ThongKe(doanhThuHomNay, doanhThuThang, phienDangChoi);
        tk.setThoiGian("Tổng quan");
        return tk;
    }

    // Helper — period string → [from, to]
    private LocalDate[] resolvePeriod(String period, LocalDate from, LocalDate to) {
        LocalDate today = LocalDate.now();

        if (period == null || "Tùy chỉnh".equals(period)) {
            return new LocalDate[] {
                    from != null ? from : today.withDayOfMonth(1),
                    to != null ? to : today
            };
        }

        return switch (period) {
            case "Hôm nay" -> new LocalDate[] { today, today };
            case "Tuần này" -> new LocalDate[] { today.with(DayOfWeek.MONDAY), today };
            case "Tháng này" -> new LocalDate[] { today.withDayOfMonth(1), today };
            case "Quý này" -> {
                int thangDauQuy = ((today.getMonthValue() - 1) / 3) * 3 + 1;
                yield new LocalDate[] { today.withMonth(thangDauQuy).withDayOfMonth(1), today };
            }
            case "Năm nay" -> new LocalDate[] { today.withDayOfYear(1), today };
            default -> new LocalDate[] {
                    from != null ? from : today.withDayOfMonth(1),
                    to != null ? to : today
            };
        };
    }
}