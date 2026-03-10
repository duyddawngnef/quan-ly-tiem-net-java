package gui.controller;

import bus.NhapHangBUS;
import entity.ChiTietPhieuNhap;
import entity.PhieuNhapHang;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import utils.ThongBaoDialogHelper;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class NhapHangController implements Initializable {

    @FXML private TableView<PhieuNhapHang> tablePhieu;
    @FXML private TableColumn<PhieuNhapHang, String> colMaPhieuNhap;
    @FXML private TableColumn<PhieuNhapHang, String> colMaNCC;
    @FXML private TableColumn<PhieuNhapHang, String> colMaNV;
    @FXML private TableColumn<PhieuNhapHang, String> colNgayNhap;
    @FXML private TableColumn<PhieuNhapHang, Double> colTongTien;
    @FXML private TableColumn<PhieuNhapHang, String> colTrangThai;

    @FXML private TableView<ChiTietPhieuNhap> tableChiTiet;
    @FXML private TableColumn<ChiTietPhieuNhap, String> colMaCTPN;
    @FXML private TableColumn<ChiTietPhieuNhap, String> colMaDV;
    @FXML private TableColumn<ChiTietPhieuNhap, Integer> colSoLuong;
    @FXML private TableColumn<ChiTietPhieuNhap, Double> colGiaNhap;
    @FXML private TableColumn<ChiTietPhieuNhap, Double> colThanhTien;

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cboTrangThai;
    @FXML private DatePicker dateFrom;
    @FXML private DatePicker dateTo;
    @FXML private Label lblTotal;

    // Detail panel
    @FXML private Label lblDetMaPhieu;
    @FXML private Label lblDetNCC;
    @FXML private Label lblDetNgay;
    @FXML private Label lblDetTongTien;
    @FXML private Label lblTrangThaiPhieu;
    @FXML private Button btnDuyet;
    @FXML private Button btnHuy;

    private final NhapHangBUS nhapHangBUS = new NhapHangBUS();
    private ObservableList<PhieuNhapHang> dataList = FXCollections.observableArrayList();
    private PhieuNhapHang selected;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (cboTrangThai != null) {
            cboTrangThai.getItems().setAll("Tất cả", "CHODUYET", "DANHAP", "DAHUY");
            cboTrangThai.setValue("Tất cả");
            cboTrangThai.setOnAction(e -> loadData());
        }
        if (dateFrom != null) dateFrom.setValue(LocalDate.now().withDayOfYear(1));
        if (dateTo   != null) dateTo.setValue(LocalDate.now());

        setupTableColumns();
        setupTableSelection();
        loadData();
    }

    private void setupTableColumns() {
        // Phiếu nhập
        if (colMaPhieuNhap != null) colMaPhieuNhap.setCellValueFactory(new PropertyValueFactory<>("maPhieuNhap"));
        if (colMaNCC != null) colMaNCC.setCellValueFactory(new PropertyValueFactory<>("maNCC"));
        if (colMaNV != null) colMaNV.setCellValueFactory(new PropertyValueFactory<>("maNV"));
        if (colNgayNhap != null) {
            colNgayNhap.setCellValueFactory(c -> {
                // ngayNhap là LocalDate — format không cần HH:mm
                LocalDate ngay = c.getValue().getNgayNhap();
                String val = ngay != null ? ngay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
                return new javafx.beans.property.SimpleStringProperty(val);
            });
        }
        if (colTongTien != null) {
            colTongTien.setCellValueFactory(new PropertyValueFactory<>("tongTien"));
            colTongTien.setCellFactory(col -> new TableCell<>() {
                @Override protected void updateItem(Double v, boolean empty) {
                    super.updateItem(v, empty);
                    setText(empty || v == null ? null : String.format("%,.0f ₫", v));
                }
            });
        }
        if (colTrangThai != null) colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));

        // Chi tiết phiếu
        if (colMaCTPN != null) colMaCTPN.setCellValueFactory(new PropertyValueFactory<>("maCTPN"));
        if (colMaDV != null) colMaDV.setCellValueFactory(new PropertyValueFactory<>("maDV"));
        if (colSoLuong != null) colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        if (colGiaNhap != null) {
            colGiaNhap.setCellValueFactory(new PropertyValueFactory<>("giaNhap"));
            colGiaNhap.setCellFactory(col -> new TableCell<>() {
                @Override protected void updateItem(Double v, boolean empty) {
                    super.updateItem(v, empty);
                    setText(empty || v == null ? null : String.format("%,.0f ₫", v));
                }
            });
        }
        if (colThanhTien != null) {
            colThanhTien.setCellValueFactory(new PropertyValueFactory<>("thanhTien"));
            colThanhTien.setCellFactory(col -> new TableCell<>() {
                @Override protected void updateItem(Double v, boolean empty) {
                    super.updateItem(v, empty);
                    setText(empty || v == null ? null : String.format("%,.0f ₫", v));
                }
            });
        }
    }

    private void setupTableSelection() {
        tablePhieu.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            selected = n;
            if (n != null) {
                showDetail(n);
                boolean isChoDuyet = "CHODUYET".equals(n.getTrangThai());
                if (btnDuyet != null) btnDuyet.setDisable(!isChoDuyet);
                if (btnHuy   != null) btnHuy.setDisable(!isChoDuyet);
                loadChiTiet(n);
            } else {
                if (btnDuyet != null) btnDuyet.setDisable(true);
                if (btnHuy   != null) btnHuy.setDisable(true);
            }
        });
        if (btnDuyet != null) btnDuyet.setDisable(true);
        if (btnHuy   != null) btnHuy.setDisable(true);
    }

    public void loadData() {
        try {
            List<PhieuNhapHang> list = nhapHangBUS.getAllPhieuNhap();
            // Filter by status
            String tt = cboTrangThai != null ? cboTrangThai.getValue() : "Tất cả";
            if (tt != null && !"Tất cả".equals(tt)) {
                list = list.stream()
                        .filter(p -> tt.equals(p.getTrangThai()))
                        .collect(java.util.stream.Collectors.toList());
            }

            // Filter by date range — dùng LocalDate trực tiếp vì ngayNhap là LocalDate
            LocalDate from = dateFrom != null ? dateFrom.getValue() : null;
            LocalDate to   = dateTo   != null ? dateTo.getValue()   : null;
            if (from != null && to != null) {
                list = list.stream()
                        .filter(p -> p.getNgayNhap() != null
                                && !p.getNgayNhap().isBefore(from)   // >= from
                                && !p.getNgayNhap().isAfter(to))     // <= to
                        .collect(java.util.stream.Collectors.toList());
            }

            dataList.setAll(list);
            tablePhieu.setItems(dataList);
            if (lblTotal != null) lblTotal.setText("Tổng: " + list.size() + " phiếu");
        } catch (Exception e) {
            if (lblTotal != null) lblTotal.setText("Lỗi: " + e.getMessage());
        }
    }

    @FXML public void handleSearch()  { loadData(); }
    @FXML public void handleFilter()  { loadData(); }
    @FXML public void handleRefresh() { loadData(); }

    @FXML
    public void handleTaoPhieu() {
        ThongBaoDialogHelper.showInfo(
                tablePhieu.getScene(), "Chức năng tạo phiếu nhập đang phát triển.");
    }

    @FXML
    public void handleDuyetPhieu() {
        if (selected == null) return;
        Stage owner = (Stage) tablePhieu.getScene().getWindow();
        boolean confirmed = gui.dialog.XacNhanDialog.show(owner,
                "Duyệt phiếu nhập", "Duyệt phiếu " + selected.getMaPhieuNhap() + "?");
        if (!confirmed) return;
        try {
            nhapHangBUS.duyetPhieu(selected.getMaPhieuNhap());
            ThongBaoDialogHelper.showSuccess(tablePhieu.getScene(), "Đã duyệt phiếu nhập!");
            loadData();
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tablePhieu.getScene(), "Lỗi duyệt: " + e.getMessage());
        }
    }

    @FXML
    public void handleHuyPhieu() {
        if (selected == null) return;
        Stage owner = (Stage) tablePhieu.getScene().getWindow();
        boolean confirmed = gui.dialog.XacNhanDialog.show(owner,
                "Hủy phiếu nhập", "Hủy phiếu " + selected.getMaPhieuNhap() + "?",
                "Hành động này không thể hoàn tác", gui.dialog.XacNhanDialog.Type.DELETE);
        if (!confirmed) return;
        try {
            nhapHangBUS.huyPhieu(selected.getMaPhieuNhap());
            ThongBaoDialogHelper.showSuccess(tablePhieu.getScene(), "Đã hủy phiếu nhập!");
            loadData();
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tablePhieu.getScene(), "Lỗi hủy: " + e.getMessage());
        }
    }

    private void showDetail(PhieuNhapHang phieu) {
        if (lblDetMaPhieu != null) lblDetMaPhieu.setText(phieu.getMaPhieuNhap());
        if (lblDetNCC     != null) lblDetNCC.setText(phieu.getMaNCC() != null ? phieu.getMaNCC() : "-");
        if (lblDetNgay    != null) {
            // ngayNhap là LocalDate — format dd/MM/yyyy
            String ngay = phieu.getNgayNhap() != null
                    ? phieu.getNgayNhap().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-";
            lblDetNgay.setText(ngay);
        }
        if (lblDetTongTien   != null) lblDetTongTien.setText(String.format("%,.0f ₫", phieu.getTongTien()));
        if (lblTrangThaiPhieu != null) updateTrangThaiStyle(phieu.getTrangThai());
    }

    private void updateTrangThaiStyle(String tt) {
        if (lblTrangThaiPhieu == null) return;
        lblTrangThaiPhieu.setText(tt);
        String style = switch (tt != null ? tt : "") {
            case "CHODUYET" -> "-fx-background-color:#FFF9C4; -fx-text-fill:#F57F17;";
            case "DANHAP"   -> "-fx-background-color:#C8E6C9; -fx-text-fill:#1B5E20;";
            case "DAHUY"    -> "-fx-background-color:#FFCDD2; -fx-text-fill:#B71C1C;";
            default          -> "";
        };
        lblTrangThaiPhieu.setStyle(style + "-fx-background-radius:12; -fx-padding:3 10;");
    }

    private void loadChiTiet(PhieuNhapHang phieu) {
        if (tableChiTiet == null) return;
        try {
            List<ChiTietPhieuNhap> ctList = nhapHangBUS.getChiTietByPhieu(phieu.getMaPhieuNhap());
            tableChiTiet.setItems(FXCollections.observableArrayList(ctList));
        } catch (Exception e) {
            tableChiTiet.setItems(FXCollections.observableArrayList());
        }
    }
}