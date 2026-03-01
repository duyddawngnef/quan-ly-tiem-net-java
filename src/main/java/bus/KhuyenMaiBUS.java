package bus;

import dao.ChuongTrinhKhuyenMaiDAO;
import entity.ChuongTrinhKhuyenMai;

import java.util.ArrayList;

public class KhuyenMaiBUS {
    private ChuongTrinhKhuyenMaiDAO dao = new ChuongTrinhKhuyenMaiDAO();

    // lấy tất cả CTKM
    public ArrayList<ChuongTrinhKhuyenMai> getAllKhuyenMai() {
        return dao.getAll();
    }

    // Lấy CTKM còn hiệu lực
    public ArrayList<ChuongTrinhKhuyenMai> getKhuyenMaiConHieuLuc() {
        return dao.getConHieuLuc();
    }

    // thêm CTKM mới
    public boolean themKhuyenMai(ChuongTrinhKhuyenMai km) throws Exception {
        validate(km);
        return dao.insert(km);
    }

    // Sửa CTKM
    public boolean suaKhuyenMai(ChuongTrinhKhuyenMai km) throws Exception {
        validate(km);
        return dao.update(km);
    }

    // Xóa CTKM
    public boolean xoaKhuyenMai(String maCTKM) {
        return dao.delete(maCTKM);
    }

    // Validation dữ liệu
    private void validate(ChuongTrinhKhuyenMai km) throws Exception {
        // LoaiKM phải thuộc PHANTRAM | SOTIEN | TANGGIO
        if (!km.getLoaiKM().equals("PHANTRAM") &&
                !km.getLoaiKM().equals("SOTIEN") &&
                !km.getLoaiKM().equals("TANGGIO")) {
            throw new Exception("LoaiKM phải là PHANTRAM, SOTIEN hoặc TANGGIO");
        }

        // GiaTriKM > 0
        if (km.getGiaTriKM() <= 0) {
            throw new Exception("GiaTriKM phải lớn hơn 0");
        }

        // DieuKienToiThieu >= 0
        if (km.getDieuKienToiThieu() < 0) {
            throw new Exception("DieuKienToiThieu phải >= 0");
        }

        // NgayKetThuc > NgayBatDau
        if (!km.getNgayKetThuc().isAfter(km.getNgayBatDau())) {
            throw new Exception("NgayKetThuc phải sau NgayBatDau");
        }
    }
}