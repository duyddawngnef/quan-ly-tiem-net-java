package bus;

import dao.ChuongTrinhKhuyenMaiDAO;
import entity.ChuongTrinhKhuyenMai;
import java.time.LocalDateTime;
import java.util.List;

/**Chức năng chính:
 * - Thêm/sửa/xóa chương trình khuyến mãi
 * - Tìm chương trình khuyến mãi phù hợp
 * - Tính giá trị khuyến mãi
 * - Kiểm tra điều kiện áp dụng
 * - Cập nhật trạng thái tự động
 */
public class KhuyenMaiBUS {
    private ChuongTrinhKhuyenMaiDAO khuyenMaiDAO;

    public KhuyenMaiBUS() {
        this.khuyenMaiDAO = new ChuongTrinhKhuyenMaiDAO();
    }

    // ===== 1. THÊM CHƯƠNG TRÌNH KHUYẾN MÃI =====
    public boolean themChuongTrinh(ChuongTrinhKhuyenMai ctkm) throws Exception {
        // 1. Validate cơ bản
        validateChuongTrinhKhuyenMai(ctkm, true);

        // 2. Kiểm tra ngày bắt đầu < ngày kết thúc
        if (ctkm.getNgayBatDau().isAfter(ctkm.getNgayKetThuc())) {
            throw new Exception("Ngày bắt đầu phải trước ngày kết thúc");
        }

        // 3. Kiểm tra ngày bắt đầu không quá khứ quá xa (warning)
        LocalDateTime now = LocalDateTime.now();
        if (ctkm.getNgayBatDau().isBefore(now.minusDays(1))) {
            throw new Exception("Ngày bắt đầu không được trong quá khứ");
        }

        // 4. Tạo mã tự động
        String maCTKM = khuyenMaiDAO.taoMaChuongTrinhTuDong();
        ctkm.setMaCTKM(maCTKM);

        // 5. Set trạng thái mặc định
        if (ctkm.getTrangThai() == null || ctkm.getTrangThai().trim().isEmpty()) {
            // Nếu ngày bắt đầu là hôm nay hoặc tương lai -> HOATDONG
            // Nếu đã quá ngày kết thúc -> HETHAN
            if (now.isAfter(ctkm.getNgayKetThuc())) {
                ctkm.setTrangThai("HETHAN");
            } else if (now.isBefore(ctkm.getNgayBatDau())) {
                ctkm.setTrangThai("HOATDONG"); // Sẽ hoạt động khi đến ngày
            } else {
                ctkm.setTrangThai("HOATDONG");
            }
        }

        // 6. Thêm vào database
        boolean success = khuyenMaiDAO.them(ctkm);
        if (!success) {
            throw new Exception("Không thể thêm chương trình khuyến mãi");
        }
        return true;
    }
     //Thêm chương trình khuyến mãi
    public boolean themKhuyenMai(ChuongTrinhKhuyenMai km) throws Exception {
        return themChuongTrinh(km);
    }

     //Lấy tất cả chương trình khuyến mãi
    public List<ChuongTrinhKhuyenMai> getAllKhuyenMai() {
        return khuyenMaiDAO.layTatCa();
    }

    //Lấy danh sách chương trình khuyến mãi còn hiệu lực
    public List<ChuongTrinhKhuyenMai> getKhuyenMaiConHieuLuc() {
        return khuyenMaiDAO.layChuongTrinhDangHoatDong();
    }

    // ===== 2. CẬP NHẬT CHƯƠNG TRÌNH =====
    public boolean capNhatChuongTrinh(ChuongTrinhKhuyenMai ctkm) throws Exception {
        // 1. Validate
        validateChuongTrinhKhuyenMai(ctkm, false);

        // 2. Kiểm tra tồn tại
        ChuongTrinhKhuyenMai existing = khuyenMaiDAO.timTheoMa(ctkm.getMaCTKM());
        if (existing == null) {
            throw new Exception("Không tìm thấy chương trình khuyến mãi");
        }

        // 3. Kiểm tra ngày tháng hợp lệ
        if (ctkm.getNgayBatDau().isAfter(ctkm.getNgayKetThuc())) {
            throw new Exception("Ngày bắt đầu phải trước ngày kết thúc");
        }

        // 4. Nếu chương trình đã hết hạn, không cho cập nhật
        if ("HETHAN".equals(existing.getTrangThai()) &&
                !"HETHAN".equals(ctkm.getTrangThai())) {
            throw new Exception("Không thể kích hoạt lại chương trình đã hết hạn");
        }

        // 5. Cập nhật
        boolean success = khuyenMaiDAO.capNhat(ctkm);
        if (!success) {
            throw new Exception("Không thể cập nhật chương trình khuyến mãi");
        }
        return true;
    }

     //Sửa chương trình khuyến mãi
    public boolean suaKhuyenMai(ChuongTrinhKhuyenMai km) throws Exception {
        return capNhatChuongTrinh(km);
    }

    // ===== 3. XÓA CHƯƠNG TRÌNH =====
    //Không xóa vật lý, chỉ chuyển trạng thái NGUNG
    public boolean xoaChuongTrinh(String maCTKM) throws Exception {
        // 1. Validate
        if (maCTKM == null || maCTKM.trim().isEmpty()) {
            throw new Exception("Mã chương trình không hợp lệ");
        }

        // 2. Kiểm tra tồn tại
        ChuongTrinhKhuyenMai ctkm = khuyenMaiDAO.timTheoMa(maCTKM);
        if (ctkm == null) {
            throw new Exception("Không tìm thấy chương trình khuyến mãi");
        }

        // 3. Kiểm tra có đang được sử dụng không
        int soLuotSuDung = khuyenMaiDAO.demSoLuotSuDung(maCTKM);
        if (soLuotSuDung > 0) {
            // Cảnh báo nhưng vẫn cho phép ngừng
            System.out.println("Cảnh báo: Chương trình đã được sử dụng " + soLuotSuDung + " lần");
        }

        // 4. Chuyển trạng thái NGUNG thay vì xóa
        boolean success = khuyenMaiDAO.tatChuongTrinh(maCTKM);
        if (!success) {
            throw new Exception("Không thể ngừng chương trình khuyến mãi");
        }
        return true;
    }
    public boolean xoaKhuyenMai(String maCTKM) throws Exception {
        return xoaChuongTrinh(maCTKM);
    }

    // ===== 4. TÌM CHƯƠNG TRÌNH PHÙ HỢP =====
    //Tìm chương trình khuyến mãi phù hợp với số tiền nạp
    public List<ChuongTrinhKhuyenMai> timChuongTrinhPhuHop(double soTienNap) throws Exception {
        if (soTienNap <= 0) {
            throw new Exception("Số tiền nạp phải lớn hơn 0");
        }
        return khuyenMaiDAO.timChuongTrinhPhuHop(soTienNap);
    }

    //Tìm chương trình khuyến mãi tốt nhất cho số tiền nạp
    public ChuongTrinhKhuyenMai timChuongTrinhTotNhat(double soTienNap) throws Exception {
        if (soTienNap <= 0) {
            throw new Exception("Số tiền nạp phải lớn hơn 0");
        }

        return khuyenMaiDAO.timChuongTrinhTotNhat(soTienNap);
    }

    // ===== 5. TÍNH GIÁ TRỊ KHUYẾN MÃI =====
    public double tinhGiaTriKhuyenMai(String maCTKM, double soTienNap) throws Exception {
        // 1. Validate
        if (maCTKM == null || maCTKM.trim().isEmpty()) {
            throw new Exception("Mã chương trình không hợp lệ");
        }

        if (soTienNap <= 0) {
            throw new Exception("Số tiền nạp phải lớn hơn 0");
        }

        // 2. Kiểm tra chương trình còn hiệu lực
        boolean conHieuLuc = khuyenMaiDAO.kiemTraConHieuLuc(maCTKM);
        if (!conHieuLuc) {
            throw new Exception("Chương trình khuyến mãi không còn hiệu lực hoặc đã ngừng");
        }

        // 3. Kiểm tra điều kiện tối thiểu
        boolean duDieuKien = khuyenMaiDAO.kiemTraDieuKien(maCTKM, soTienNap);
        if (!duDieuKien) {
            ChuongTrinhKhuyenMai ctkm = khuyenMaiDAO.timTheoMa(maCTKM);
            throw new Exception(String.format(
                    "Số tiền nạp chưa đủ điều kiện. Tối thiểu: %,.0f VND",
                    ctkm.getDieuKienToiThieu()
            ));
        }
        return khuyenMaiDAO.tinhGiaTriKhuyenMai(maCTKM, soTienNap);
    }

    // ===== 6. KIỂM TRA ĐIỀU KIỆN =====
    //Kiểm tra chương trình còn hiệu lực
    public boolean kiemTraConHieuLuc(String maCTKM) throws Exception {
        if (maCTKM == null || maCTKM.trim().isEmpty()) {
            throw new Exception("Mã chương trình không hợp lệ");
        }
        return khuyenMaiDAO.kiemTraConHieuLuc(maCTKM);
    }
    //Kiểm tra đủ điều kiện áp dụng
    public boolean kiemTraDieuKien(String maCTKM, double soTienNap) throws Exception {
        if (maCTKM == null || maCTKM.trim().isEmpty()) {
            throw new Exception("Mã chương trình không hợp lệ");
        }
        if (soTienNap <= 0) {
            throw new Exception("Số tiền nạp phải lớn hơn 0");
        }
        return khuyenMaiDAO.kiemTraDieuKien(maCTKM, soTienNap);
    }

    // ===== 7. QUẢN LÝ TRẠNG THÁI =====
    //Tắt chương trình (chuyển trạng thái NGUNG)
    public boolean tatChuongTrinh(String maCTKM) throws Exception {
        if (maCTKM == null || maCTKM.trim().isEmpty()) {
            throw new Exception("Mã chương trình không hợp lệ");
        }
        ChuongTrinhKhuyenMai ctkm = khuyenMaiDAO.timTheoMa(maCTKM);
        if (ctkm == null) {
            throw new Exception("Không tìm thấy chương trình");
        }
        return khuyenMaiDAO.tatChuongTrinh(maCTKM);
    }
    //Bật chương trình (chuyển trạng thái HOATDONG)
    public boolean batChuongTrinh(String maCTKM) throws Exception {
        if (maCTKM == null || maCTKM.trim().isEmpty()) {
            throw new Exception("Mã chương trình không hợp lệ");
        }
        ChuongTrinhKhuyenMai ctkm = khuyenMaiDAO.timTheoMa(maCTKM);
        if (ctkm == null) {
            throw new Exception("Không tìm thấy chương trình");
        }
        // Kiểm tra chưa hết hạn
        if (LocalDateTime.now().isAfter(ctkm.getNgayKetThuc())) {
            throw new Exception("Không thể bật chương trình đã hết hạn");
        }
        return khuyenMaiDAO.batChuongTrinh(maCTKM);
    }
    //Cập nhật trạng thái hết hạn tự động
    public int capNhatChuongTrinhHetHan() {
        return khuyenMaiDAO.capNhatChuongTrinhHetHan();
    }

    // ===== 8. THỐNG KÊ =====
    //Thống kê chương trình theo thời gian
    public List<Object[]> thongKeChuongTrinh(LocalDateTime tuNgay, LocalDateTime denNgay)
            throws Exception {
        if (tuNgay == null || denNgay == null) {
            throw new Exception("Khoảng thời gian không hợp lệ");
        }
        if (tuNgay.isAfter(denNgay)) {
            throw new Exception("Ngày bắt đầu phải trước ngày kết thúc");
        }
        return khuyenMaiDAO.thongKeChuongTrinh(tuNgay, denNgay);
    }
    //Đếm số lượt sử dụng chương trình
    public int demSoLuotSuDung(String maCTKM) throws Exception {
        if (maCTKM == null || maCTKM.trim().isEmpty()) {
            throw new Exception("Mã chương trình không hợp lệ");
        }
        return khuyenMaiDAO.demSoLuotSuDung(maCTKM);
    }

     //Tổng khuyến mãi đã tặng
    public double tongKhuyenMaiDaTang(String maCTKM) throws Exception {
        if (maCTKM == null || maCTKM.trim().isEmpty()) {
            throw new Exception("Mã chương trình không hợp lệ");
        }
        return khuyenMaiDAO.tongKhuyenMaiDaTang(maCTKM);
    }

    // ===== 9. TÌM KIẾM VÀ LẤY DỮ LIỆU =====
    //Tìm kiếm chương trình khuyến mãi theo mã
    public ChuongTrinhKhuyenMai timTheoMa(String maCTKM) throws Exception {
        if (maCTKM == null || maCTKM.trim().isEmpty()) {
            throw new Exception("Mã chương trình không hợp lệ");
        }
        ChuongTrinhKhuyenMai ctkm = khuyenMaiDAO.timTheoMa(maCTKM);
        if (ctkm == null) {
            throw new Exception("Không tìm thấy chương trình khuyến mãi");
        }
        return ctkm;
    }

    //Tìm theo tên
    public List<ChuongTrinhKhuyenMai> timTheoTen(String tenCT) throws Exception {
        if (tenCT == null || tenCT.trim().isEmpty()) {
            return layChuongTrinhDangHoatDong();
        }
        return khuyenMaiDAO.timTheoTen(tenCT);
    }

    //Lấy chương trình đang hoạt động
    public List<ChuongTrinhKhuyenMai> layChuongTrinhDangHoatDong() {
        return khuyenMaiDAO.layChuongTrinhDangHoatDong();
    }

    //Lấy theo trạng thái
    public List<ChuongTrinhKhuyenMai> layTheoTrangThai(String trangThai) throws Exception {
        if (trangThai == null || trangThai.trim().isEmpty()) {
            throw new Exception("Trạng thái không hợp lệ");
        }
        return khuyenMaiDAO.timTheoTrangThai(trangThai);
    }

     //Lấy theo loại
    public List<ChuongTrinhKhuyenMai> layTheoLoai(String loaiKM) throws Exception {
        if (loaiKM == null || loaiKM.trim().isEmpty()) {
            throw new Exception("Loại khuyến mãi không hợp lệ");
        }
        return khuyenMaiDAO.timTheoLoai(loaiKM);
    }

    //Lấy tất cả
    public List<ChuongTrinhKhuyenMai> layTatCa() {
        return khuyenMaiDAO.layTatCa();
    }

    // ===== VALIDATION =====
    private void validateChuongTrinhKhuyenMai(ChuongTrinhKhuyenMai ctkm, boolean isInsert)
            throws Exception {
        // Kiểm tra null
        if (ctkm == null) {
            throw new Exception("Chương trình khuyến mãi không hợp lệ");
        }

        // Kiểm tra tên
        if (ctkm.getTenCT() == null || ctkm.getTenCT().trim().isEmpty()) {
            throw new Exception("Tên chương trình không được để trống");
        }

        if (ctkm.getTenCT().length() > 100) {
            throw new Exception("Tên chương trình không được vượt quá 100 ký tự");
        }

        // Kiểm tra loại khuyến mãi
        if (ctkm.getLoaiKM() == null || ctkm.getLoaiKM().trim().isEmpty()) {
            throw new Exception("Loại khuyến mãi không được để trống");
        }

        String loaiKM = ctkm.getLoaiKM().toUpperCase();
        if (!loaiKM.equals("PHANTRAM") && !loaiKM.equals("SOTIEN") && !loaiKM.equals("TANGGIO")) {
            throw new Exception("Loại khuyến mãi phải là PHANTRAM, SOTIEN hoặc TANGGIO");
        }

        // Kiểm tra giá trị khuyến mãi
        if (ctkm.getGiaTriKM() <= 0) {
            throw new Exception("Giá trị khuyến mãi phải lớn hơn 0");
        }

        // Kiểm tra phần trăm không vượt quá 100%
        if ("PHANTRAM".equals(loaiKM) && ctkm.getGiaTriKM() > 100) {
            throw new Exception("Giá trị khuyến mãi phần trăm không được vượt quá 100%");
        }

        // Kiểm tra điều kiện tối thiểu
        if (ctkm.getDieuKienToiThieu() < 0) {
            throw new Exception("Điều kiện tối thiểu không được âm");
        }

        // Kiểm tra ngày tháng
        if (ctkm.getNgayBatDau() == null) {
            throw new Exception("Ngày bắt đầu không được để trống");
        }

        if (ctkm.getNgayKetThuc() == null) {
            throw new Exception("Ngày kết thúc không được để trống");
        }
    }
}