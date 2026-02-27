package bus;

import dao.ChiTietHoaDonDAO;
import dao.HoaDonDAO;
import entity.HoaDon;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDonBUS {

    private HoaDonDAO HoaDonDAO;
    private ChiTietHoaDonDAO ChiTietDAO;

    private static final double DON_GIA_GIO = 10000;

    public HoaDonBUS() {
        HoaDonDAO = new HoaDonDAO();
        ChiTietDAO = new ChiTietHoaDonDAO();
    }
}
// Tao hoa don moi
public boolean taoHoaDon(HoaDon hd) {

    if (hd == null) {
        System.out.println("Hoa don khong hop le.");
        return false;
    }

    if (hd.getMaPhien() == null || hd.getMaPhien().isEmpty()) {
        System.out.println("Ma phien khong duoc rong.");
        return false;
    }

    if (hd.getMaNV() == null || hd.getMaNV().isEmpty()) {
        System.out.println("Ma nhan vien khong duoc rong.");
        return false;
    }

    hd.setNgayLap(LocalDateTime.now());
    hd.setTrangThai("CHUATHANHTOAN");

    hd.tinhToanTuDong();

    return HoaDonDAO.insert(hd);
}
// Tinh tien gio + dich vu
public void capNhatTien(HoaDon hd, double tienGio, double tienDV) {

    if (hd == null) return;

    if (tienGio < 0 || tienDV < 0) {
        System.out.println("Tien khong hop le.");
        return;
    }

    hd.setTienGioChoi(tienGio);
    hd.setTienDichVu(tienDV);

    hd.tinhToanTuDong();
}
// Ap dung giam gia
public void apDungGiamGia(HoaDon hd, double phanTram) {

    if (hd == null) return;

    if (phanTram < 0 || phanTram > 100) {
        System.out.println("Giam gia phai tu 0-100%");
        return;
    }

    double giam = hd.getTongTien() * (phanTram/100);
    hd.setGiamGia(giam);

    hd.tinhThanhToan();
}
// Thanh toan hoa don
public boolean thanhToan(HoaDon hd, String phuongThuc) {

    if (hd == null) return false;

    if (hd.isDaThanhToan()) {
        System.out.println("Hoa don da thanh toan roi.");
        return false;
    }

    if (!kiemTraPhuongThuc(phuongThuc)) {
        System.out.println("Phuong thuc thanh toan khong hop le");
        return false;
    }

    hd.setPhuongThucTT(phuongThuc);
    hd.setTrangThai("DATHANHTOAN");

    hd.tinhToanTuDong();

    return HoaDonDAO.update(hd);
}
// Huy hoa don (neu chua thanh toan)
public boolean huyHoaDon(HoaDon hd) {

    if (hd == null) return false;

    if (hd.isDaThanhToan()) {
        System.out.println("Khong the huy hoa don da thanh toan.");
        return false;
    }

    return HoaDonDAO.delete(hd.getMaHD());
}
// Lay danh sach hoa don
public List<HoaDon> getDanhSachHoaDon() {
    return HoaDonDAO.getAll();
}
// Kiem tra phuong thuc thanh toan hop le
private boolean kiemTraPhuongThuc(String pt) {

    return pt.equals("TIENMAT") ||
           pt.equals("CHUYENKHOAN") ||
           pt.equals("MOMO") ||
           pt.equals("VNPAY") ||
           pt.equals("TAIKHOAN");
}