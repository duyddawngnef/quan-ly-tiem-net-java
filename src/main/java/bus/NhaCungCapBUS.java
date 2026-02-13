package bus;

import dao.NhaCungCapDAO;
import dao.PhieuNhapHangDAO;
import entity.NhaCungCap;

import java.util.ArrayList;

public class NhaCungCapBUS {

    private NhaCungCapDAO nccDAO = new NhaCungCapDAO();
    private PhieuNhapHangDAO phieuNhapHangDAO = new PhieuNhapHangDAO();

    // lấy tất cả NCC
    public ArrayList<NhaCungCap> getAllNhaCungCap() {
        return nccDAO.getAll();
    }

    // lấy NCC đang hoạt động
    public ArrayList<NhaCungCap> getNhaCungCapHoatDong() {
        ArrayList<NhaCungCap> list = nccDAO.getAll();
        ArrayList<NhaCungCap> result = new ArrayList<>();

        for (NhaCungCap ncc : list) {
            if (ncc.getTrangThai().equalsIgnoreCase("HOATDONG")) {
                result.add(ncc);
            }
        }

        return result;
    }

    // thêm NCC
    public boolean themNhaCungCap(NhaCungCap ncc) throws Exception {

        if (ncc.getMaNCC() == null || ncc.getMaNCC().trim().isEmpty()) {
            throw new Exception("Mã NCC không được để trống");
        }

        if (nccDAO.getByID(ncc.getMaNCC()) != null) {
            throw new Exception("Mã NCC đã tồn tại");
        }

        ncc.setTrangThai("HOATDONG");

        return nccDAO.insert(ncc);
    }

    // sửa NCC
    public boolean suaNhaCungCap(NhaCungCap ncc) throws Exception {

        NhaCungCap old = nccDAO.getByID(ncc.getMaNCC());

        if (old == null) {
            throw new Exception("Nhà cung cấp không tồn tại");
        }

        return nccDAO.update(ncc);
    }

    // xóa NCC (soft delete)
    public boolean xoaNhaCungCap(String maNCC) throws Exception {

        NhaCungCap ncc = nccDAO.getByID(maNCC);

        if (ncc == null) {
            throw new Exception("Nhà cung cấp không tồn tại");
        }

        // kiểm tra phiếu nhập CHODUYET
        if (phieuNhapHangDAO.phieuNhapChoDuyet(maNCC)) {
            throw new Exception("NCC có phiếu nhập đang chờ duyệt");
        }

        // soft delete = chuyển sang NGUNG
        ncc.setTrangThai("NGUNG");

        return nccDAO.update(ncc);
    }

}