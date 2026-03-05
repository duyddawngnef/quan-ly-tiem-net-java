package gui.controller;

import bus.ThongKeBUS;
import entity.ThongkeDoanhThu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ThongKeController implements Initializable {

    // Summary cards
    @FXML private Label lblTongThu;
    @FXML private Label lblTongChi;
    @FXML private Label lblLoiNhuan;
    @FXML private Label lblSoPhien;

    // Tab 1 - Doanh thu
    @FXML private ComboBox<String> cboLoaiTK;
    @FXML private DatePicker dateFrom;
    @FXML private DatePicker dateTo;
    @FXML private TableView<ThongkeDoanhThu> tableThongKe;
    @FXML private TableColumn<ThongkeDoanhThu, String> colThoiGian;
    @FXML private TableColumn<ThongkeDoanhThu, Double> colDoanhThu;
    @FXML private TableColumn<ThongkeDoanhThu, Double> colNhapHang;
    @FXML private TableColumn<ThongkeDoanhThu, Double> colLoiNhuanCol;
    @FXML private StackPane chartContainer;
    @FXML private Label lblChartPlaceholder;

    // Tab 2 - Thu Chi
    @FXML private ComboBox<String> cboThoiGian;
    @FXML private DatePicker dateTCFrom;
    @FXML private DatePicker dateTCTo;
    @FXML private TableView<ThongkeDoanhThu> tableThuChi;
    @FXML private TableColumn<ThongkeDoanhThu, String> colTCThoiGian;
    @FXML private TableColumn<ThongkeDoanhThu, Double> colTCThu;
    @FXML private TableColumn<ThongkeDoanhThu, Double> colTCChi;
    @FXML private TableColumn<ThongkeDoanhThu, Double> colTCLoiNhuan;

    // Tab 3 - Top KH
    @FXML private ComboBox<Integer> cboNamTop;
    @FXML private ComboBox<String> cboTopN;
    @FXML private TableView<Object[]> tableTop;

    private final ThongKeBUS thongKeBUS = new ThongKeBUS();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (dateFrom   != null) dateFrom.setValue(LocalDate.now().withDayOfMonth(1));
        if (dateTo     != null) dateTo.setValue(LocalDate.now());
        if (dateTCFrom != null) dateTCFrom.setValue(LocalDate.now().withDayOfMonth(1));
        if (dateTCTo   != null) dateTCTo.setValue(LocalDate.now());

        if (cboLoaiTK != null) {
            cboLoaiTK.getItems().setAll("Từ ngày đến ngày", "Theo tháng");
            cboLoaiTK.setValue("Từ ngày đến ngày");
        }
        if (cboThoiGian != null) {
            cboThoiGian.getItems().setAll("Hôm nay", "Tuần này", "Tháng này", "Quý này", "Năm nay", "Tùy chỉnh");
            cboThoiGian.setValue("Tháng này");
        }
        if (cboTopN != null) {
            cboTopN.getItems().setAll("Top 5", "Top 10", "Top 20");
            cboTopN.setValue("Top 10");
        }

        setupTableColumns();
        loadYearCombo();
        loadSummaryCards();
    }

    private void setupTableColumns() {
        // Tab 1
        if (colThoiGian    != null) colThoiGian.setCellValueFactory(new PropertyValueFactory<>("thoiGian"));
        if (colDoanhThu    != null) {
            colDoanhThu.setCellValueFactory(new PropertyValueFactory<>("tongDoanhThu"));
            colDoanhThu.setCellFactory(col -> moneyCell());
        }
        if (colNhapHang    != null) {
            colNhapHang.setCellValueFactory(new PropertyValueFactory<>("tongNhapHang"));
            colNhapHang.setCellFactory(col -> moneyCell());
        }
        if (colLoiNhuanCol != null) {
            colLoiNhuanCol.setCellValueFactory(new PropertyValueFactory<>("loiNhuan"));
            colLoiNhuanCol.setCellFactory(col -> colorMoneyCell());
        }

        // Tab 2 (same structure)
        if (colTCThoiGian != null) colTCThoiGian.setCellValueFactory(new PropertyValueFactory<>("thoiGian"));
        if (colTCThu      != null) {
            colTCThu.setCellValueFactory(new PropertyValueFactory<>("tongDoanhThu"));
            colTCThu.setCellFactory(col -> moneyCell());
        }
        if (colTCChi      != null) {
            colTCChi.setCellValueFactory(new PropertyValueFactory<>("tongNhapHang"));
            colTCChi.setCellFactory(col -> moneyCell());
        }
        if (colTCLoiNhuan != null) {
            colTCLoiNhuan.setCellValueFactory(new PropertyValueFactory<>("loiNhuan"));
            colTCLoiNhuan.setCellFactory(col -> colorMoneyCell());
        }
    }

    private <S> TableCell<S, Double> moneyCell() {
        return new TableCell<>() {
            @Override protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : String.format("%,.0f ₫", v));
            }
        };
    }

    private <S> TableCell<S, Double> colorMoneyCell() {
        return new TableCell<>() {
            @Override protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); setStyle(""); return; }
                setText(String.format("%,.0f ₫", v));
                setStyle(v >= 0 ? "-fx-text-fill:#388E3C;" : "-fx-text-fill:#C62828;");
            }
        };
    }

    private void loadYearCombo() {
        if (cboNamTop == null) return;
        int year = LocalDate.now().getYear();
        cboNamTop.getItems().setAll(year, year - 1, year - 2, year - 3);
        cboNamTop.setValue(year);
    }

    private void loadSummaryCards() {
        try {
            LocalDate from = LocalDate.now().withDayOfMonth(1);
            LocalDate to   = LocalDate.now();
            Map<String, BigDecimal> summary = thongKeBUS.getSummary(from, to);
            BigDecimal thu      = summary.getOrDefault("thu",      BigDecimal.ZERO);
            BigDecimal chi      = summary.getOrDefault("chi",      BigDecimal.ZERO);
            BigDecimal loiNhuan = summary.getOrDefault("loiNhuan", BigDecimal.ZERO);
            BigDecimal soPhien  = summary.getOrDefault("soPhien",  BigDecimal.ZERO);

            if (lblTongThu  != null) lblTongThu.setText(String.format("%,.0f ₫", thu));
            if (lblTongChi  != null) lblTongChi.setText(String.format("%,.0f ₫", chi));
            if (lblLoiNhuan != null) {
                lblLoiNhuan.setText(String.format("%,.0f ₫", loiNhuan));
                lblLoiNhuan.setStyle(loiNhuan.compareTo(BigDecimal.ZERO) >= 0
                    ? "-fx-text-fill:#388E3C; -fx-font-weight:bold; -fx-font-size:18px;"
                    : "-fx-text-fill:#C62828; -fx-font-weight:bold; -fx-font-size:18px;");
            }
            if (lblSoPhien  != null) lblSoPhien.setText(String.valueOf(soPhien.intValue()));
        } catch (Exception e) {
            if (lblTongThu != null) lblTongThu.setText("N/A");
        }
    }

    @FXML
    public void handleThongKe() {
        try {
            String loai = cboLoaiTK != null ? cboLoaiTK.getValue() : null;
            LocalDate from = dateFrom != null ? dateFrom.getValue() : LocalDate.now().withDayOfMonth(1);
            LocalDate to   = dateTo   != null ? dateTo.getValue()   : LocalDate.now();
            if (from == null || to == null) return;

            List<ThongkeDoanhThu> data = thongKeBUS.thongKe(loai, from, to);
            if (tableThongKe != null)
                tableThongKe.setItems(FXCollections.observableArrayList(data));
            if (lblChartPlaceholder != null) lblChartPlaceholder.setVisible(data.isEmpty());
        } catch (Exception e) {
            if (lblChartPlaceholder != null)
                lblChartPlaceholder.setText("Lỗi: " + e.getMessage());
        }
    }

    @FXML
    public void handleThongKeThuChi() {
        try {
            String period = cboThoiGian != null ? cboThoiGian.getValue() : null;
            LocalDate from = dateTCFrom != null ? dateTCFrom.getValue() : LocalDate.now().withDayOfMonth(1);
            LocalDate to   = dateTCTo   != null ? dateTCTo.getValue()   : LocalDate.now();

            List<ThongkeDoanhThu> data = thongKeBUS.thongKeThuChi(period, from, to);
            if (tableThuChi != null)
                tableThuChi.setItems(FXCollections.observableArrayList(data));
        } catch (Exception ignored) {}
    }

    @FXML
    public void handleThongKeTop() {
        try {
            Integer nam = cboNamTop != null ? cboNamTop.getValue() : LocalDate.now().getYear();
            String topNStr = cboTopN != null ? cboTopN.getValue() : "Top 10";
            int n = topNStr != null ? Integer.parseInt(topNStr.replace("Top ", "")) : 10;
            if (nam == null) return;

            List<Object[]> data = thongKeBUS.topKhachHang(nam, n);
            if (tableTop != null)
                tableTop.setItems(FXCollections.observableArrayList(data));
        } catch (Exception ignored) {}
    }

    @FXML
    public void handleXuatExcel() {
        // TODO: implement export to Excel
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText("Chức năng xuất Excel đang phát triển.");
        alert.showAndWait();
    }
}
