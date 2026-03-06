package gui.controller;

import bus.GoiDichVuBUS;
import entity.GoiDichVu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GoiDichVuController - Controller quản lý gói dịch vụ
 * 
 * Chức năng:
 * - Hiển thị danh sách gói dịch vụ trong TableView
 * - Thêm, sửa, xóa gói dịch vụ (quyền QUANLY)
 * - Tìm kiếm gói dịch vụ theo tên
 * - Hiển thị chi tiết gói khi chọn trên bảng
 */
public class GoiDichVuController {

    // ============== FXML COMPONENTS ==============

    @FXML
    private TableView<GoiDichVu> tableGoiDV;
    @FXML
    private TableColumn<GoiDichVu, String> colMaGoi;
    @FXML
    private TableColumn<GoiDichVu, String> colTenGoi;
    @FXML
    private TableColumn<GoiDichVu, String> colLoaiGoi;
    @FXML
    private TableColumn<GoiDichVu, Double> colSoGio;
    @FXML
    private TableColumn<GoiDichVu, Integer> colSoNgayHL;
    @FXML
    private TableColumn<GoiDichVu, Double> colGiaGoc;
    @FXML
    private TableColumn<GoiDichVu, Double> colGiaGoi;
    @FXML
    private TableColumn<GoiDichVu, String> colApDungKhu;
    @FXML
    private TableColumn<GoiDichVu, String> colTrangThai;

    @FXML
    private TextField txtTimKiem;
    @FXML
    private TextField txtMaGoi;
    @FXML
    private TextField txtTenGoi;
    @FXML
    private TextField txtSoGio;
    @FXML
    private TextField txtSoNgayHL;
    @FXML
    private TextField txtGiaGoc;
    @FXML
    private TextField txtGiaGoi;

    @FXML
    private ComboBox<String> cboLoaiGoi;
    @FXML
    private ComboBox<String> cboApDungKhu;
    @FXML
    private ComboBox<String> cboTrangThai;

    @FXML
    private Button btnThem;
    @FXML
    private Button btnSua;
    @FXML
    private Button btnXoa;
    @FXML
    private Button btnTimKiem;
    @FXML
    private Button btnLamMoi;

    @FXML
    private Label lblTrangThai;

    // ============== BUSINESS LAYER ==============

    private final GoiDichVuBUS goiDichVuBUS = new GoiDichVuBUS();
    private ObservableList<GoiDichVu> danhSachGoiDV = FXCollections.observableArrayList();
    private List<GoiDichVu> danhSachGoc; // Danh sách gốc (dùng cho tìm kiếm)

    // ============== INITIALIZE ==============

    @FXML
    public void initialize() {
        setupTableColumns();
        setupComboBoxes();
        setupTableSelection();
        loadDanhSach();
    }

    /**
     * Cấu hình các cột trong TableView
     */
    private void setupTableColumns() {
        colMaGoi.setCellValueFactory(new PropertyValueFactory<>("maGoi"));
        colTenGoi.setCellValueFactory(new PropertyValueFactory<>("tenGoi"));
        colLoaiGoi.setCellValueFactory(new PropertyValueFactory<>("loaiGoi"));
        colSoGio.setCellValueFactory(new PropertyValueFactory<>("soGio"));
        colSoNgayHL.setCellValueFactory(new PropertyValueFactory<>("soNgayHieuLuc"));
        colGiaGoc.setCellValueFactory(new PropertyValueFactory<>("giaGoc"));
        colGiaGoi.setCellValueFactory(new PropertyValueFactory<>("giaGoi"));
        colApDungKhu.setCellValueFactory(new PropertyValueFactory<>("apDungChoKhu"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));

        // Format cột giá tiền
        DecimalFormat df = new DecimalFormat("#,###");
        colGiaGoc.setCellFactory(col -> new TableCell<GoiDichVu, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : df.format(item) + " đ");
            }
        });
        colGiaGoi.setCellFactory(col -> new TableCell<GoiDichVu, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : df.format(item) + " đ");
            }
        });

        tableGoiDV.setItems(danhSachGoiDV);
    }

    /**
     * Khởi tạo giá trị cho các ComboBox
     */
    private void setupComboBoxes() {
        cboLoaiGoi.setItems(FXCollections.observableArrayList(
                "GIOCHOI", "NGAY", "TUAN", "THANG"));
        cboApDungKhu.setItems(FXCollections.observableArrayList(
                "TATCA", "THUONG", "VIP"));
        cboTrangThai.setItems(FXCollections.observableArrayList(
                "HOATDONG", "NGUNGBAN"));
    }

    /**
     * Bắt sự kiện chọn dòng trên bảng để hiển thị chi tiết
     */
    private void setupTableSelection() {
        tableGoiDV.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        hienThiChiTiet(newVal);
                    }
                });
    }

    // ============== LOAD DỮ LIỆU ==============

    /**
     * Load danh sách gói dịch vụ từ BUS
     */
    private void loadDanhSach() {
        try {
            danhSachGoc = goiDichVuBUS.getDanhSachGoiDV();
            danhSachGoiDV.setAll(danhSachGoc);
            setTrangThai("Đã tải " + danhSachGoc.size() + " gói dịch vụ");
        } catch (Exception e) {
            showError("Lỗi tải dữ liệu", e.getMessage());
        }
    }

    /**
     * Hiển thị chi tiết gói dịch vụ lên form
     */
    private void hienThiChiTiet(GoiDichVu g) {
        txtMaGoi.setText(g.getMaGoi());
        txtTenGoi.setText(g.getTenGoi());
        cboLoaiGoi.setValue(g.getLoaiGoi());
        txtSoGio.setText(String.valueOf(g.getSoGio()));
        txtSoNgayHL.setText(String.valueOf(g.getSoNgayHieuLuc()));
        txtGiaGoc.setText(String.valueOf(g.getGiaGoc()));
        txtGiaGoi.setText(String.valueOf(g.getGiaGoi()));
        cboApDungKhu.setValue(g.getApDungChoKhu());
        cboTrangThai.setValue(g.getTrangThai());
    }

    // ============== EVENT HANDLERS ==============

    /**
     * Xử lý nút Thêm gói
     */
    @FXML
    private void handleThem() {
        try {
            GoiDichVu goi = layThongTinTuForm();
            boolean result = goiDichVuBUS.themGoiDichVu(goi);
            if (result) {
                showInfo("Thành công", "Đã thêm gói dịch vụ: " + goi.getMaGoi());
                loadDanhSach();
                clearForm();
            } else {
                showError("Thất bại", "Không thể thêm gói dịch vụ");
            }
        } catch (Exception e) {
            showError("Lỗi thêm gói", e.getMessage());
        }
    }

    /**
     * Xử lý nút Sửa gói
     */
    @FXML
    private void handleSua() {
        GoiDichVu selected = tableGoiDV.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Chưa chọn gói", "Vui lòng chọn gói dịch vụ cần sửa trên bảng");
            return;
        }

        try {
            GoiDichVu goi = layThongTinTuForm();
            goi.setMaGoi(selected.getMaGoi()); // Giữ nguyên mã gói
            boolean result = goiDichVuBUS.suaGoiDichVu(goi);
            if (result) {
                showInfo("Thành công", "Đã cập nhật gói dịch vụ: " + goi.getMaGoi());
                loadDanhSach();
            } else {
                showError("Thất bại", "Không thể cập nhật gói dịch vụ");
            }
        } catch (Exception e) {
            showError("Lỗi sửa gói", e.getMessage());
        }
    }

    /**
     * Xử lý nút Xóa gói
     */
    @FXML
    private void handleXoa() {
        GoiDichVu selected = tableGoiDV.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Chưa chọn gói", "Vui lòng chọn gói dịch vụ cần xóa trên bảng");
            return;
        }

        // Hỏi xác nhận
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText("Xóa gói dịch vụ: " + selected.getTenGoi());
        confirm.setContentText("Bạn có chắc chắn muốn xóa gói này?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean result = goiDichVuBUS.xoaGoiDichVu(selected.getMaGoi());
                    if (result) {
                        showInfo("Thành công", "Đã xóa gói dịch vụ: " + selected.getMaGoi());
                        loadDanhSach();
                        clearForm();
                    } else {
                        showError("Thất bại", "Không thể xóa gói dịch vụ");
                    }
                } catch (Exception e) {
                    showError("Lỗi xóa gói", e.getMessage());
                }
            }
        });
    }

    /**
     * Xử lý nút Tìm kiếm
     */
    @FXML
    private void handleTimKiem() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            danhSachGoiDV.setAll(danhSachGoc);
            setTrangThai("Hiển thị tất cả " + danhSachGoc.size() + " gói");
            return;
        }

        List<GoiDichVu> ketQua = danhSachGoc.stream()
                .filter(g -> g.getTenGoi().toLowerCase().contains(keyword)
                        || g.getMaGoi().toLowerCase().contains(keyword)
                        || g.getLoaiGoi().toLowerCase().contains(keyword))
                .collect(Collectors.toList());

        danhSachGoiDV.setAll(ketQua);
        setTrangThai("Tìm thấy " + ketQua.size() + " gói cho từ khóa: \"" + keyword + "\"");
    }

    /**
     * Xử lý nút Làm mới
     */
    @FXML
    private void handleLamMoi() {
        clearForm();
        txtTimKiem.clear();
        loadDanhSach();
        tableGoiDV.getSelectionModel().clearSelection();
        setTrangThai("Đã làm mới dữ liệu");
    }

    // ============== HELPER METHODS ==============

    /**
     * Lấy thông tin gói dịch vụ từ form nhập liệu
     */
    private GoiDichVu layThongTinTuForm() throws Exception {
        String tenGoi = txtTenGoi.getText().trim();
        String loaiGoi = cboLoaiGoi.getValue();
        String soGioStr = txtSoGio.getText().trim();
        String soNgayStr = txtSoNgayHL.getText().trim();
        String giaGocStr = txtGiaGoc.getText().trim();
        String giaGoiStr = txtGiaGoi.getText().trim();
        String apDungKhu = cboApDungKhu.getValue();
        String trangThai = cboTrangThai.getValue();

        // Validate input
        if (tenGoi.isEmpty())
            throw new Exception("Tên gói không được để trống");
        if (loaiGoi == null)
            throw new Exception("Vui lòng chọn loại gói");
        if (apDungKhu == null)
            throw new Exception("Vui lòng chọn khu áp dụng");
        if (trangThai == null)
            throw new Exception("Vui lòng chọn trạng thái");

        double soGio, giaGoc, giaGoi;
        int soNgayHL;
        try {
            soGio = Double.parseDouble(soGioStr);
        } catch (NumberFormatException e) {
            throw new Exception("Số giờ phải là số hợp lệ");
        }
        try {
            soNgayHL = Integer.parseInt(soNgayStr);
        } catch (NumberFormatException e) {
            throw new Exception("Số ngày hiệu lực phải là số nguyên hợp lệ");
        }
        try {
            giaGoc = Double.parseDouble(giaGocStr);
        } catch (NumberFormatException e) {
            throw new Exception("Giá gốc phải là số hợp lệ");
        }
        try {
            giaGoi = Double.parseDouble(giaGoiStr);
        } catch (NumberFormatException e) {
            throw new Exception("Giá gói phải là số hợp lệ");
        }

        GoiDichVu goi = new GoiDichVu();
        goi.setTenGoi(tenGoi);
        goi.setLoaiGoi(loaiGoi);
        goi.setSoGio(soGio);
        goi.setSoNgayHieuLuc(soNgayHL);
        goi.setGiaGoc(giaGoc);
        goi.setGiaGoi(giaGoi);
        goi.setApDungChoKhu(apDungKhu);
        goi.setTrangThai(trangThai);

        return goi;
    }

    /**
     * Xóa trắng form nhập liệu
     */
    private void clearForm() {
        txtMaGoi.clear();
        txtTenGoi.clear();
        cboLoaiGoi.setValue(null);
        txtSoGio.clear();
        txtSoNgayHL.clear();
        txtGiaGoc.clear();
        txtGiaGoi.clear();
        cboApDungKhu.setValue(null);
        cboTrangThai.setValue(null);
    }

    /**
     * Cập nhật label trạng thái phía dưới
     */
    private void setTrangThai(String msg) {
        if (lblTrangThai != null) {
            lblTrangThai.setText(msg);
        }
    }

    // ============== DIALOG HELPERS ==============

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
        setTrangThai("Lỗi: " + content);
    }
}
