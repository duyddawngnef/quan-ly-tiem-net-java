package gui.controller;

import bus.ThongKeBUS;
import entity.ThongKeDoanhThu;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ThongKeController implements Initializable {

    // Summary Cards
    @FXML private Label lblTongThu;
    @FXML private Label lblTongChi;
    @FXML private Label lblLoiNhuan;
    @FXML private Label lblSoPhien;

    // Tab 1 - Doanh thu
    @FXML private ComboBox<String> cboLoaiTK;
    @FXML private DatePicker dateFrom;
    @FXML private DatePicker dateTo;
    @FXML private TableView<ThongKeDoanhThu> tableThongKe;
    @FXML private TableColumn<ThongKeDoanhThu, String> colThoiGian;
    @FXML private TableColumn<ThongKeDoanhThu, Double> colDoanhThu;
    @FXML private TableColumn<ThongKeDoanhThu, Double> colNhapHang;
    @FXML private TableColumn<ThongKeDoanhThu, Double> colLoiNhuanCol;
    @FXML private StackPane chartContainer;
    @FXML private Label lblChartPlaceholder;

    // Tab 2 - Thu Chi
    @FXML private ComboBox<String> cboThoiGian;
    @FXML private DatePicker dateTCFrom;
    @FXML private DatePicker dateTCTo;
    @FXML private TableView<ThongKeDoanhThu> tableThuChi;
    @FXML private TableColumn<ThongKeDoanhThu, String> colTCThoiGian;
    @FXML private TableColumn<ThongKeDoanhThu, Double> colTCThu;
    @FXML private TableColumn<ThongKeDoanhThu, Double> colTCChi;
    @FXML private TableColumn<ThongKeDoanhThu, Double> colTCLoiNhuan;

    // Tab 3 - Top KH
    @FXML private ComboBox<Integer> cboNamTop;
    @FXML private ComboBox<String> cboTopN;
    @FXML private TableView<Object[]> tableTop;

    private final ThongKeBUS thongKeBUS = new ThongKeBUS();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (dateFrom != null)
            dateFrom.setValue(LocalDate.now().withDayOfMonth(1));
        if (dateTo != null)
            dateTo.setValue(LocalDate.now());
        if (dateTCFrom != null)
            dateTCFrom.setValue(LocalDate.now().withDayOfMonth(1));
        if (dateTCTo != null)
            dateTCTo.setValue(LocalDate.now());

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
        if (colThoiGian != null)
            colThoiGian.setCellValueFactory(new PropertyValueFactory<>("thoiGian"));
        if (colDoanhThu != null) {
            colDoanhThu.setCellValueFactory(new PropertyValueFactory<>("tongDoanhThu"));
            colDoanhThu.setCellFactory(col -> moneyCell());
        }
        if (colNhapHang != null) {
            colNhapHang.setCellValueFactory(new PropertyValueFactory<>("tongNhapHang"));
            colNhapHang.setCellFactory(col -> moneyCell());
        }
        if (colLoiNhuanCol != null) {
            colLoiNhuanCol.setCellValueFactory(new PropertyValueFactory<>("loiNhuan"));
            colLoiNhuanCol.setCellFactory(col -> colorMoneyCell());
        }

        // Tab 2
        if (colTCThoiGian != null)
            colTCThoiGian.setCellValueFactory(new PropertyValueFactory<>("thoiGian"));
        if (colTCThu != null) {
            colTCThu.setCellValueFactory(new PropertyValueFactory<>("tongDoanhThu"));
            colTCThu.setCellFactory(col -> moneyCell());
        }
        if (colTCChi != null) {
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
            @Override
            protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : String.format("%,.0f ₫", v));
            }
        };
    }

    private <S> TableCell<S, Double> colorMoneyCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                setText(String.format("%,.0f ₫", v));
                setStyle(v >= 0
                        ? "-fx-text-fill:#388E3C;"
                        : "-fx-text-fill:#C62828;");
            }
        };
    }

    private void loadYearCombo() {
        if (cboNamTop == null)
            return;
        int year = LocalDate.now().getYear();
        cboNamTop.getItems().setAll(year, year - 1, year - 2, year - 3);
        cboNamTop.setValue(year);
    }

    private void loadSummaryCards() {
        try {
            LocalDate from = LocalDate.now().withDayOfMonth(1);
            LocalDate to = LocalDate.now();

            ThongKeDoanhThu s = thongKeBUS.getSummary(from, to);

            if (lblTongThu != null)
                lblTongThu.setText(String.format("%,.0f ₫", s.getThu()));
            if (lblTongChi != null)
                lblTongChi.setText(String.format("%,.0f ₫", s.getChi()));
            if (lblLoiNhuan != null) {
                lblLoiNhuan.setText(String.format("%,.0f ₫", s.getLoiNhuanSummary()));
                lblLoiNhuan.setStyle(s.getLoiNhuanSummary() >= 0
                        ? "-fx-text-fill:#388E3C; -fx-font-weight:bold; -fx-font-size:18px;"
                        : "-fx-text-fill:#C62828; -fx-font-weight:bold; -fx-font-size:18px;");
            }
            if (lblSoPhien != null)
                lblSoPhien.setText(String.valueOf(s.getSoPhien()));

        } catch (Exception e) {
            if (lblTongThu != null)
                lblTongThu.setText("N/A");
        }
    }

    @FXML public void handleThongKe() {
        try {
            String loai = cboLoaiTK != null ? cboLoaiTK.getValue() : null;
            LocalDate from = dateFrom != null ? dateFrom.getValue() : LocalDate.now().withDayOfMonth(1);
            LocalDate to = dateTo != null ? dateTo.getValue() : LocalDate.now();
            if (from == null || to == null)
                return;

            List<ThongKeDoanhThu> data = thongKeBUS.thongKe(loai, from, to);
            if (tableThongKe != null)
                tableThongKe.setItems(FXCollections.observableArrayList(data));
            if (lblChartPlaceholder != null)
                lblChartPlaceholder.setVisible(data.isEmpty());

        } catch (Exception e) {
            if (lblChartPlaceholder != null)
                lblChartPlaceholder.setText("Lỗi: " + e.getMessage());
        }
    }

    @FXML public void handleLoaiTKChanged() {
        handleThongKe();
    }

    @FXML public void handleThongKeThuChi() {
        try {
            String period = cboThoiGian != null ? cboThoiGian.getValue() : null;
            LocalDate from = dateTCFrom != null ? dateTCFrom.getValue() : LocalDate.now().withDayOfMonth(1);
            LocalDate to = dateTCTo != null ? dateTCTo.getValue() : LocalDate.now();

            List<ThongKeDoanhThu> data = thongKeBUS.thongKeThuChi(period, from, to);
            if (tableThuChi != null)
                tableThuChi.setItems(FXCollections.observableArrayList(data));

        } catch (Exception ignored) {
        }
    }

    @FXML public void handleThongKeTop() {
        try {
            Integer nam = cboNamTop != null ? cboNamTop.getValue() : LocalDate.now().getYear();
            String topNStr = cboTopN != null ? cboTopN.getValue() : "Top 10";
            int n = topNStr != null ? Integer.parseInt(topNStr.replace("Top ", "")) : 10;
            if (nam == null)
                return;

            List<Object[]> data = thongKeBUS.topKhachHang(nam, n);
            if (tableTop != null)
                tableTop.setItems(FXCollections.observableArrayList(data));

        } catch (Exception ignored) {
        }
    }

    @FXML public void handleXuatExcel() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText("Chức năng xuất Excel đang phát triển.");
        alert.showAndWait();
    }
}