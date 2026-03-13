package gui.controller;

import bus.ThongKeBUS;
import entity.ThongKe;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

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
    // Tab 1 — Doanh thu
    @FXML private ComboBox<String> cboLoaiTK;
    @FXML private DatePicker dateFrom;
    @FXML private DatePicker dateTo;
    @FXML private TableView<ThongKe>           tableThongKe;
    @FXML private TableColumn<ThongKe, String> colThoiGian;
    @FXML private TableColumn<ThongKe, Double> colDoanhThu;
    @FXML private TableColumn<ThongKe, Double> colNhapHang;
    @FXML private TableColumn<ThongKe, Double> colLoiNhuanCol;
    @FXML private Label lblChartPlaceholder;
    @FXML private javafx.scene.layout.StackPane chartContainer;
    // Tab 2 — Thu Chi
    @FXML private ComboBox<String> cboThoiGian;
    @FXML private DatePicker dateTCFrom;
    @FXML private DatePicker dateTCTo;
    @FXML private TableView<ThongKe>           tableThuChi;
    @FXML private TableColumn<ThongKe, String> colTCThoiGian;
    @FXML private TableColumn<ThongKe, Double> colTCThu;
    @FXML private TableColumn<ThongKe, Double> colTCChi;
    @FXML private TableColumn<ThongKe, Double> colTCLoiNhuan;
    // Tab 3 — Top KH
    @FXML private ComboBox<Integer> cboNamTop;
    @FXML private ComboBox<String>  cboTopN;
    @FXML private TableView<Object[]>           tableTop;
    @FXML private TableColumn<Object[], Number> colTopSTT;
    @FXML private TableColumn<Object[], String> colTopMaKH;
    @FXML private TableColumn<Object[], String> colTopTen;
    @FXML private TableColumn<Object[], Number> colTopSoPhien;
    @FXML private TableColumn<Object[], String> colTopTongTien;

    private final ThongKeBUS thongKeBUS = new ThongKeBUS();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (dateFrom   != null) dateFrom.setValue(LocalDate.now().withDayOfMonth(1));
        if (dateTo     != null) dateTo.setValue(LocalDate.now());
        if (dateTCFrom != null) dateTCFrom.setValue(LocalDate.now().withDayOfMonth(1));
        if (dateTCTo   != null) dateTCTo.setValue(LocalDate.now());
        if (cboLoaiTK   != null) { cboLoaiTK.getItems().setAll("Từ ngày đến ngày", "Theo tháng"); cboLoaiTK.setValue("Từ ngày đến ngày"); }
        if (cboThoiGian != null) { cboThoiGian.getItems().setAll("Hôm nay","Tuần này","Tháng này","Quý này","Năm nay","Tùy chỉnh"); cboThoiGian.setValue("Tháng này"); }
        if (cboTopN     != null) { cboTopN.getItems().setAll("Top 5","Top 10","Top 20"); cboTopN.setValue("Top 10"); }
        loadYearCombo();
        setupColumns();
        loadSummaryCards();
        handleThongKe();
        handleThongKeThuChi();
    }

    private void setupColumns() {
        // Tab 1
        if (colThoiGian    != null) colThoiGian.setCellValueFactory(new PropertyValueFactory<>("thoiGian"));
        if (colDoanhThu    != null) { colDoanhThu.setCellValueFactory(new PropertyValueFactory<>("tongDoanhThu")); colDoanhThu.setCellFactory(c -> moneyCell()); }
        if (colNhapHang    != null) { colNhapHang.setCellValueFactory(new PropertyValueFactory<>("tongNhapHang")); colNhapHang.setCellFactory(c -> moneyCell()); }
        if (colLoiNhuanCol != null) { colLoiNhuanCol.setCellValueFactory(new PropertyValueFactory<>("loiNhuan")); colLoiNhuanCol.setCellFactory(c -> colorMoneyCell()); }
        // Tab 2
        if (colTCThoiGian != null) colTCThoiGian.setCellValueFactory(new PropertyValueFactory<>("thoiGian"));
        if (colTCThu      != null) { colTCThu.setCellValueFactory(new PropertyValueFactory<>("tongDoanhThu")); colTCThu.setCellFactory(c -> moneyCell()); }
        if (colTCChi      != null) { colTCChi.setCellValueFactory(new PropertyValueFactory<>("tongNhapHang")); colTCChi.setCellFactory(c -> moneyCell()); }
        if (colTCLoiNhuan != null) { colTCLoiNhuan.setCellValueFactory(new PropertyValueFactory<>("loiNhuan")); colTCLoiNhuan.setCellFactory(c -> colorMoneyCell()); }
        // Tab 3
        if (colTopSTT  != null)
            colTopSTT.setCellValueFactory(c ->
                    new SimpleIntegerProperty(tableTop.getItems().indexOf(c.getValue()) + 1));
        if (colTopMaKH != null)
            colTopMaKH.setCellValueFactory(c ->
                    new SimpleStringProperty((String) c.getValue()[0]));
        if (colTopTen  != null)
            colTopTen.setCellValueFactory(c ->
                    new SimpleStringProperty((String) c.getValue()[1]));
        if (colTopSoPhien != null)
            colTopSoPhien.setCellValueFactory(c ->
                    new SimpleIntegerProperty(((Number) c.getValue()[2]).intValue()));
        if (colTopTongTien != null)
            colTopTongTien.setCellValueFactory(c ->
                    new SimpleStringProperty(String.format("%,.0f ₫", ((Number) c.getValue()[3]).doubleValue())));
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
        int y = LocalDate.now().getYear();
        cboNamTop.getItems().setAll(y, y-1, y-2, y-3);
        cboNamTop.setValue(y);
    }

    private void loadSummaryCards() {
        try {
            ThongKe s = thongKeBUS.getSummary(
                    LocalDate.now().withDayOfMonth(1), LocalDate.now());
            if (lblTongThu  != null) lblTongThu.setText(String.format("%,.0f ₫", s.getThu()));
            if (lblTongChi  != null) lblTongChi.setText(String.format("%,.0f ₫", s.getChi()));
            if (lblSoPhien  != null) lblSoPhien.setText(String.valueOf(s.getSoPhien()));
            if (lblLoiNhuan != null) {
                double ln = s.getLoiNhuanSummary();
                lblLoiNhuan.setText(String.format("%,.0f ₫", ln));
                lblLoiNhuan.setStyle(ln >= 0
                        ? "-fx-text-fill:#388E3C; -fx-font-weight:bold; -fx-font-size:18px;"
                        : "-fx-text-fill:#C62828; -fx-font-weight:bold; -fx-font-size:18px;");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (lblTongThu != null) lblTongThu.setText("Lỗi kết nối");
        }
    }

    @FXML public void handleThongKe() {
        try {
            String    loai = cboLoaiTK != null ? cboLoaiTK.getValue() : "Từ ngày đến ngày";
            LocalDate from = dateFrom  != null && dateFrom.getValue() != null ? dateFrom.getValue() : LocalDate.now().withDayOfMonth(1);
            LocalDate to   = dateTo    != null && dateTo.getValue()   != null ? dateTo.getValue()   : LocalDate.now();

            List<ThongKe> data = thongKeBUS.thongKe(loai, from, to);
            if (tableThongKe        != null) tableThongKe.setItems(FXCollections.observableArrayList(data));
            if (lblChartPlaceholder != null) lblChartPlaceholder.setVisible(data.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            if (lblChartPlaceholder != null) lblChartPlaceholder.setText("Lỗi: " + e.getMessage());
        }
    }

    @FXML public void handleLoaiTKChanged() { handleThongKe(); }

    @FXML public void handleThongKeThuChi() {
        try {
            String    period = cboThoiGian != null ? cboThoiGian.getValue() : null;
            LocalDate from   = dateTCFrom  != null && dateTCFrom.getValue() != null ? dateTCFrom.getValue() : LocalDate.now().withDayOfMonth(1);
            LocalDate to     = dateTCTo    != null && dateTCTo.getValue()   != null ? dateTCTo.getValue()   : LocalDate.now();

            List<ThongKe> data = thongKeBUS.thongKeThuChi(period, from, to);
            if (tableThuChi != null) tableThuChi.setItems(FXCollections.observableArrayList(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML public void handleThongKeTop() {
        try {
            Integer nam    = cboNamTop != null ? cboNamTop.getValue() : LocalDate.now().getYear();
            String  topStr = cboTopN   != null ? cboTopN.getValue()   : "Top 10";
            int n = topStr != null ? Integer.parseInt(topStr.replace("Top ", "")) : 10;
            if (nam == null) return;

            List<Object[]> data = thongKeBUS.topKhachHang(nam, n);
            if (tableTop != null) tableTop.setItems(FXCollections.observableArrayList(data));
        } catch (Exception e) {
            e.printStackTrace();
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