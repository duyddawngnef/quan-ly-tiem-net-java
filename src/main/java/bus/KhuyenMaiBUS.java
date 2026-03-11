package bus;

import dao.ChuongTrinhKhuyenMaiDAO;
import entity.ChuongTrinhKhuyenMai;

import java.util.ArrayList;

public class KhuyenMaiBUS {
    private ChuongTrinhKhuyenMaiDAO dao = new ChuongTrinhKhuyenMaiDAO();

    public ArrayList<ChuongTrinhKhuyenMai> getAllKhuyenMai() {
        return dao.getAll();
    }

    public ArrayList<ChuongTrinhKhuyenMai> getKhuyenMaiConHieuLuc() {
        return dao.getConHieuLuc();
    }

    public String generateMaCTKM() {
        return dao.generateMaCTKM();
    }

    public boolean themKhuyenMai(ChuongTrinhKhuyenMai km) throws Exception {
        validate(km);
        return dao.insert(km);
    }

    public boolean suaKhuyenMai(ChuongTrinhKhuyenMai km) throws Exception {
        validate(km);
        return dao.update(km);
    }

    public boolean xoaKhuyenMai(String maCTKM) {
        return dao.delete(maCTKM);
    }

    private void validate(ChuongTrinhKhuyenMai km) throws Exception {
        if (km.getMaCTKM() == null || km.getMaCTKM().trim().isEmpty())
            throw new Exception("Mã CTKM không được để trống");

        if (km.getTenCT() == null || km.getTenCT().trim().isEmpty())
            throw new Exception("Tên chương trình không được để trống");

        if (km.getLoaiKM() == null ||
                (!km.getLoaiKM().equals("PHANTRAM") &&
                        !km.getLoaiKM().equals("SOTIEN")   &&
                        !km.getLoaiKM().equals("TANGGIO")))
            throw new Exception("LoaiKM phải là PHANTRAM, SOTIEN hoặc TANGGIO");

        if (km.getGiaTriKM() <= 0)
            throw new Exception("GiaTriKM phải lớn hơn 0");

        if (km.getDieuKienToiThieu() < 0)
            throw new Exception("DieuKienToiThieu phải >= 0");

        if (km.getNgayBatDau() == null || km.getNgayKetThuc() == null)
            throw new Exception("Ngày bắt đầu và ngày kết thúc không được để trống");

        if (!km.getNgayKetThuc().isAfter(km.getNgayBatDau()))
            throw new Exception("NgayKetThuc phải sau NgayBatDau");
    }

    public void tuDongCapNhatHetHan() {
        dao.tuDongCapNhatHetHan();
    }
}