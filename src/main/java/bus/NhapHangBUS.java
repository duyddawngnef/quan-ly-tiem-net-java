package bus;

import dao.*;
import entity.*;
import untils.PermissionHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NhapHangBUS {

    private PhieuNhapHangDAO phieuNhapHangDAO;
    private ChiTietPhieuNhapDAO chiTietPhieuNhapDAO;
    private NhaCungCapDAO nhaCungCapDAO;
    private DichVuDAO dichVuDAO;

    public NhapHangBUS() {
        this.phieuNhapHangDAO = new PhieuNhapHangDAO();
        this.chiTietPhieuNhapDAO = new ChiTietPhieuNhapDAO();
        this.nhaCungCapDAO = new NhaCungCapDAO();
        this.dichVuDAO = new DichVuDAO();
    }

    // 1. Tạo phiếu nhập
    public PhieuNhapHang taoPhieuNhap(String maNCC, List<ChiTietPhieuNhap> chiTietList) throws Exception {

        // Kiểm tra phân quyền
        PermissionHelper.requireQuanLy();

        // Lấy mã nhân viên hiện tại
        String maNV = PermissionHelper.getCurrentMaNV();

        // Kiểm tra NCC tồn tại và HOATDONG
        NhaCungCap ncc = nhaCungCapDAO.getByID(maNCC);
        if (ncc == null) {
            throw new Exception("Nhà cung cấp không tồn tại!");
        }
        if (!ncc.isHoatDong()) {
            throw new Exception("Nhà cung cấp đã ngừng hoạt động!");
        }

        // Kiểm tra danh sách chi tiết không rỗng
        if (chiTietList == null || chiTietList.isEmpty()) {
            throw new Exception("Phiếu nhập phải có ít nhất một mặt hàng!");
        }

        // Validate từng chi tiết
        for (ChiTietPhieuNhap ct : chiTietList) {
            DichVu dv = dichVuDAO.getById(ct.getMaDV());
            if (dv == null) {
                throw new Exception("Dịch vụ/hàng hóa không tồn tại: " + ct.getMaDV());
            }
            if (ct.getSoLuong() <= 0) {
                throw new Exception("Số lượng phải lớn hơn 0: " + ct.getMaDV());
            }
            if (ct.getGiaNhap() <= 0) {
                throw new Exception("Giá nhập phải lớn hơn 0: " + ct.getMaDV());
            }
        }

        // Tính tổng tiền
        double tongTien = 0;
        for (ChiTietPhieuNhap ct : chiTietList) {
            tongTien += ct.getSoLuong() * ct.getGiaNhap();
        }

        // Tạo PhieuNhapHang
        String maPhieuNhap = phieuNhapHangDAO.generateMaPhieuNhap();
        PhieuNhapHang phieu = new PhieuNhapHang(
                maPhieuNhap,
                maNCC,
                maNV,
                LocalDate.now(),
                tongTien,
                "CHODUYET");

        // INSERT phiếu nhập
        boolean insertPhieuOk = phieuNhapHangDAO.insert(phieu);
        if (!insertPhieuOk) {
            throw new Exception("Tạo phiếu nhập thất bại!");
        }

        // INSERT từng chi tiết — compensate nếu lỗi
        List<ChiTietPhieuNhap> inserted = new ArrayList<>();
        try {
            for (ChiTietPhieuNhap ct : chiTietList) {
                String maCTPN = chiTietPhieuNhapDAO.generateMaCTPN();
                ct.setMaCTPN(maCTPN);
                ct.setMaPhieu(maPhieuNhap);
                ct.setThanhTien(ct.getSoLuong() * ct.getGiaNhap());

                boolean insertCtOk = chiTietPhieuNhapDAO.insert(ct);
                if (!insertCtOk) {
                    throw new Exception("Lưu chi tiết phiếu nhập thất bại: " + ct.getMaDV());
                }
                inserted.add(ct);
            }
        } catch (Exception e) {
            // Compensate: xóa các chi tiết đã insert và xóa phiếu
            for (ChiTietPhieuNhap ct : inserted) {
                chiTietPhieuNhapDAO.delete(ct.getMaCTPN());
            }
            phieuNhapHangDAO.delete(maPhieuNhap);
            throw new Exception("Tạo phiếu nhập thất bại: " + e.getMessage());
        }

        // Trả về phiếu vừa tạo
        return phieu;
    }

    // 2. Duyệt phiếu
    public boolean duyetPhieu(String maPhieu) throws Exception {

        // Kiểm tra phân quyền
        PermissionHelper.requireQuanLy();

        // Lấy phiếu nhập từ DB
        PhieuNhapHang phieu = phieuNhapHangDAO.getByID(maPhieu);
        if (phieu == null) {
            throw new Exception("Phiếu nhập không tồn tại!");
        }

        // Kiểm tra trạng thái CHODUYET
        if (!"CHODUYET".equals(phieu.getTrangThai())) {
            throw new Exception("Chỉ có thể duyệt phiếu ở trạng thái CHỜ DUYỆT!");
        }

        // Lấy danh sách chi tiết
        List<ChiTietPhieuNhap> chiTietList = chiTietPhieuNhapDAO.getByPhieu(maPhieu);
        if (chiTietList == null || chiTietList.isEmpty()) {
            throw new Exception("Phiếu nhập không có chi tiết!");
        }

        // Cộng tồn kho từng mặt hàng
        List<String> updated = new ArrayList<>();
        try {
            for (ChiTietPhieuNhap ct : chiTietList) {
                boolean ok = dichVuDAO.updateSoLuong(ct.getMaDV(), ct.getSoLuong());
                if (!ok) {
                    throw new Exception("Cập nhật tồn kho thất bại: " + ct.getMaDV());
                }
                updated.add(ct.getMaDV());

                // Tự động cập nhật trạng thái dịch vụ = CONHANG
                dichVuDAO.updateTrangThai(ct.getMaDV(), "CONHANG");
            }
        } catch (Exception e) {
            // Compensate: trừ lại tồn kho những mặt hàng đã cộng
            for (String maDV : updated) {
                ChiTietPhieuNhap ct = chiTietList.stream()
                        .filter(c -> c.getMaDV().equals(maDV))
                        .findFirst().orElse(null);
                if (ct != null) {
                    dichVuDAO.updateSoLuong(maDV, -ct.getSoLuong());
                }
            }
            throw new Exception("Duyệt phiếu thất bại: " + e.getMessage());
        }

        // Cập nhật trạng thái phiếu = DANHAP
        boolean updateOk = phieuNhapHangDAO.updateTrangThai(maPhieu, "DANHAP");
        if (!updateOk) {
            // Compensate: trừ lại toàn bộ tồn kho vừa cộng
            for (ChiTietPhieuNhap ct : chiTietList) {
                dichVuDAO.updateSoLuong(ct.getMaDV(), -ct.getSoLuong());
            }
            throw new Exception("Cập nhật trạng thái phiếu thất bại!");
        }

        return true;
    }

    // 3. Hủy phiếu
    public boolean huyPhieu(String maPhieu) throws Exception {

        // Kiểm tra phân quyền
        PermissionHelper.requireQuanLy();

        // Lấy phiếu nhập từ DB
        PhieuNhapHang phieu = phieuNhapHangDAO.getByID(maPhieu);
        if (phieu == null) {
            throw new Exception("Phiếu nhập không tồn tại!");
        }

        // Kiểm tra trạng thái hợp lệ để hủy
        String trangThai = phieu.getTrangThai();
        if (!"CHODUYET".equals(trangThai) && !"DANHAP".equals(trangThai)) {
            throw new Exception("Chỉ có thể hủy phiếu ở trạng thái CHỜ DUYỆT hoặc ĐÃ NHẬP!");
        }

        // Nếu đã nhập kho thì trừ lại tồn kho
        if ("DANHAP".equals(trangThai)) {
            List<ChiTietPhieuNhap> chiTietList = chiTietPhieuNhapDAO.getByPhieu(maPhieu);
            if (chiTietList == null || chiTietList.isEmpty()) {
                throw new Exception("Không tìm thấy chi tiết phiếu nhập!");
            }

            List<String> reverted = new ArrayList<>();
            try {
                for (ChiTietPhieuNhap ct : chiTietList) {
                    boolean ok = dichVuDAO.updateSoLuong(ct.getMaDV(), -ct.getSoLuong());
                    if (!ok) {
                        throw new Exception("Trừ tồn kho thất bại: " + ct.getMaDV());
                    }
                    reverted.add(ct.getMaDV());
                }
            } catch (Exception e) {
                // Compensate: cộng lại tồn kho những mặt hàng đã trừ
                for (String maDV : reverted) {
                    ChiTietPhieuNhap ct = chiTietList.stream()
                            .filter(c -> c.getMaDV().equals(maDV))
                            .findFirst().orElse(null);
                    if (ct != null) {
                        dichVuDAO.updateSoLuong(maDV, ct.getSoLuong());
                    }
                }
                throw new Exception("Hủy phiếu thất bại: " + e.getMessage());
            }
        }

        // Cập nhật trạng thái phiếu = DAHUY
        boolean updateOk = phieuNhapHangDAO.updateTrangThai(maPhieu, "DAHUY");
        if (!updateOk) {
            throw new Exception("Cập nhật trạng thái phiếu thất bại!");
        }

        return true;
    }

    // 4. Lấy tất cả phiếu nhập
    public ArrayList<PhieuNhapHang> getAllPhieuNhap() throws Exception {
        PermissionHelper.requireQuanLy();
        return phieuNhapHangDAO.getAll();
    }
}