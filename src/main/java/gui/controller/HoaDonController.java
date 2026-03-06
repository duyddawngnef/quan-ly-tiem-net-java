package gui.controller;

import bus.HoaDonBUS;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import utils.ThongBaoDialogHelper;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

public class HoaDonController implements Initializable {

    @FXML private TableView<HoaDon> tableView;
    @FXML private TableColumn<HoaDon, String> colMaHD;
    @FXML private TableColumn<HoaDon, String> colMaPhien;
    @FXML private TableColumn<HoaDon, String> colKH;
    @FXML private TableColumn<HoaDon, String> colNV;
    @FXML private TableColumn<HoaDon, String> colNgayLap;
    @FXML private TableColumn<HoaDon, String> colTongTien;
    @FXML private TableColumn<HoaDon, String> colThanhToan;
    @FXML private TableColumn<HoaDon, String> colPTTT;
    @FXML private TableColumn<HoaDon, String> colTrangThai;

    @FXML private DatePicker dateFrom;
    @FXML private DatePicker dateTo;
    @FXML private TextField txtSearch;
    @FXML private Label lblTotal;
    @FXML private Label lblTongTienAll;
    @FXML private Button btnXuatPDF;

    // Detail panel
    @FXML private VBox vboxDetail;
    @FXML private Label lblNoSelection;
    @FXML private Label lblDetMaHD;
    @FXML private Label lblDetKH;
    @FXML private Label lblDetMay;
    @FXML private Label lblDetThoiGian;
    @FXML private Label lblDetTienMay;
    @FXML private Label lblDetTongTien;
    @FXML private TableView<ChiTietHoaDon> tableChiTiet;
    @FXML private TableColumn<ChiTietHoaDon, String> colCtLoai;
    @FXML private TableColumn<ChiTietHoaDon, String> colCtMoTa;
    @FXML private TableColumn<ChiTietHoaDon, Double> colCtSoLuong;
    @FXML private TableColumn<ChiTietHoaDon, String> colCtDonGia;
    @FXML private TableColumn<ChiTietHoaDon, String> colCtThanhTien;

    private final HoaDonBUS hoaDonBUS = new HoaDonBUS();
    private ObservableList<HoaDon> dataList = FXCollections.observableArrayList();
    private HoaDon selectedHD;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (dateFrom != null) dateFrom.setValue(LocalDate.now().withDayOfMonth(1));
        if (dateTo   != null) dateTo.setValue(LocalDate.now());
        setupTableColumns();
        setupTableSelection();
        hideDetail();
        loadData();
    }

    private void setupTableColumns() {
        if (colMaHD      != null) colMaHD.setCellValueFactory(new PropertyValueFactory<>("maHD"));
        if (colMaPhien   != null) colMaPhien.setCellValueFactory(new PropertyValueFactory<>("maPhien"));
        if (colKH        != null) colKH.setCellValueFactory(new PropertyValueFactory<>("maKH"));
        if (colNV        != null) colNV.setCellValueFactory(new PropertyValueFactory<>("maNV"));
        if (colNgayLap   != null) colNgayLap.setCellValueFactory(new PropertyValueFactory<>("ngayLapFormatted"));
        if (colTongTien  != null) colTongTien.setCellValueFactory(new PropertyValueFactory<>("tongTienFormatted"));
        if (colThanhToan != null) colThanhToan.setCellValueFactory(new PropertyValueFactory<>("thanhToanFormatted"));
        if (colPTTT      != null) colPTTT.setCellValueFactory(new PropertyValueFactory<>("phuongThucTT"));
        if (colTrangThai != null) colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));

        // Chi tiết
        if (colCtLoai     != null) colCtLoai.setCellValueFactory(new PropertyValueFactory<>("tenLoaiChiTiet"));
        if (colCtMoTa     != null) colCtMoTa.setCellValueFactory(new PropertyValueFactory<>("moTa"));
        if (colCtSoLuong  != null) colCtSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        if (colCtDonGia   != null) colCtDonGia.setCellValueFactory(new PropertyValueFactory<>("donGiaFormatted"));
        if (colCtThanhTien!= null) colCtThanhTien.setCellValueFactory(new PropertyValueFactory<>("thanhTienFormatted"));
    }

    private void setupTableSelection() {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            selectedHD = n;
            if (btnXuatPDF != null) btnXuatPDF.setDisable(n == null);
            if (n != null) showDetail(n);
            else hideDetail();
        });
    }

    public void loadData() {
        try {
            LocalDate from = dateFrom != null ? dateFrom.getValue() : LocalDate.now().withDayOfMonth(1);
            LocalDate to   = dateTo   != null ? dateTo.getValue()   : LocalDate.now();
            if (from == null || to == null) return;

            LocalDateTime dtFrom = from.atStartOfDay();
            LocalDateTime dtTo   = to.atTime(LocalTime.MAX);
            List<HoaDon> list = hoaDonBUS.getHoaDonsByDateRange(dtFrom, dtTo);

            // Filter by search text
            String kw = txtSearch != null ? txtSearch.getText().toLowerCase().trim() : "";
            if (!kw.isEmpty()) {
                list = list.stream()
                    .filter(h -> h.getMaHD().toLowerCase().contains(kw)
                              || (h.getMaKH() != null && h.getMaKH().toLowerCase().contains(kw)))
                    .collect(java.util.stream.Collectors.toList());
            }

            dataList.setAll(list);
            tableView.setItems(dataList);
            updateFooter(list);
        } catch (Exception e) {
            if (lblTotal != null) lblTotal.setText("Lỗi: " + e.getMessage());
        }
    }

    private void updateFooter(List<HoaDon> list) {
        if (lblTotal != null) lblTotal.setText("Tổng: " + list.size() + " hóa đơn");
        double sum = list.stream().mapToDouble(HoaDon::getThanhToan).sum();
        if (lblTongTienAll != null)
            lblTongTienAll.setText(String.format("Tổng tiền: %,.0f ₫", sum));
    }

    @FXML public void handleSearch()     { loadData(); }
    @FXML public void handleSearchText() { loadData(); }
    @FXML public void handleRefresh()    { loadData(); }

    @FXML
    public void handleXuatPDF() {
        if (selectedHD == null) return;
        try {
            String path = hoaDonBUS.xuatHoaDonPDF(selectedHD.getMaHD());
            ThongBaoDialogHelper.showSuccess(tableView.getScene(), "Đã xuất PDF: " + path);
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tableView.getScene(), "Lỗi xuất PDF: " + e.getMessage());
        }
    }

    @FXML
    public void handleXuatPDFChiTiet() { handleXuatPDF(); }

    @FXML
    public void handleXuatExcel() {
        ThongBaoDialogHelper.showInfo(tableView.getScene(), "Chức năng xuất Excel đang phát triển.");
    }

    private void showDetail(HoaDon hd) {
        if (lblNoSelection != null) { lblNoSelection.setVisible(false); lblNoSelection.setManaged(false); }
        if (vboxDetail     != null) { vboxDetail.setVisible(true);     vboxDetail.setManaged(true); }

        if (lblDetMaHD   != null) lblDetMaHD.setText(hd.getMaHD());
        if (lblDetKH     != null) lblDetKH.setText(hd.getMaKH() != null ? hd.getMaKH() : "-");
        if (lblDetMay    != null) lblDetMay.setText(hd.getMaPhien() != null ? hd.getMaPhien() : "-");
        if (lblDetThoiGian!= null) lblDetThoiGian.setText(hd.getNgayLapFormatted());
        if (lblDetTienMay!= null) lblDetTienMay.setText(hd.getTienGioChoiFormatted());
        if (lblDetTongTien!= null) lblDetTongTien.setText(hd.getThanhToanFormatted());

        loadChiTietHoaDon(hd.getMaHD());
    }

    private void hideDetail() {
        if (vboxDetail     != null) { vboxDetail.setVisible(false);    vboxDetail.setManaged(false); }
        if (lblNoSelection != null) { lblNoSelection.setVisible(true); lblNoSelection.setManaged(true); }
    }

    private void loadChiTietHoaDon(String maHD) {
        if (tableChiTiet == null) return;
        try {
            List<ChiTietHoaDon> ctList = hoaDonBUS.xemChiTietHoaDon(maHD);
            tableChiTiet.setItems(FXCollections.observableArrayList(ctList));
        } catch (Exception ignored) {}
    }
}
