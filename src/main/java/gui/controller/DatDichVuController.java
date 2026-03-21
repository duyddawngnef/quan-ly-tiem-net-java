package gui.controller;

import bus.DichVuBUS;
import dao.DichVuDAO;
import dao.KhachHangDAO;
import dao.PhienSuDungDAO;
import dao.SuDungDichVuDAO;
import entity.DichVu;
import entity.PhienSuDung;
import entity.SuDungDichVu;
import utils.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DatDichVuController implements Initializable {

    @FXML
    private Label lblPhienInfo;
    @FXML
    private Label lblCanhBao;
    @FXML
    private ComboBox<String> cboLoai;
    @FXML
    private ScrollPane paneForm;

    // --- Bảng catalogue ---
    @FXML
    private TableView<DichVu> tableDichVu;
    @FXML
    private TableColumn<DichVu, String> colMaDV;
    @FXML
    private TableColumn<DichVu, String> colTenDV;
    @FXML
    private TableColumn<DichVu, String> colLoai;
    @FXML
    private TableColumn<DichVu, String> colDonGia;
    @FXML
    private TableColumn<DichVu, String> colSoLuong;

    // --- Form đặt ---
    @FXML
    private Label lblChonDV;
    @FXML
    private Label lblGia;
    @FXML
    private Spinner<Integer> spinnerSoLuong;
    @FXML
    private Label lblTongTien;
    @FXML
    private Label lblFormError;
    @FXML
    private Button btnDat;

    // --- Bảng dịch vụ đã đặt ---
    @FXML
    private VBox paneDaDat;
    @FXML
    private Label lblTongDaDat;
    @FXML
    private TableView<SuDungDichVu> tableDaDat;
    @FXML
    private TableColumn<SuDungDichVu, String> colDDTen;
    @FXML
    private TableColumn<SuDungDichVu, String> colDDSoLuong;
    @FXML
    private TableColumn<SuDungDichVu, String> colDDDonGia;
    @FXML
    private TableColumn<SuDungDichVu, String> colDDThanhTien;
    @FXML
    private TableColumn<SuDungDichVu, String> colDDThoiGian;

    private final DichVuBUS dichVuBUS = new DichVuBUS();
    private final DichVuDAO dichVuDAO = new DichVuDAO();
    private final PhienSuDungDAO phienDAO = new PhienSuDungDAO();
    private final SuDungDichVuDAO suDungDAO = new SuDungDichVuDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    private List<DichVu> allDV = new ArrayList<>();
    private PhienSuDung phienHienTai = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupColumns();
        setupColumnsDaDat();
        setupSpinner();
        loadPhienAndDichVu();
    }

    // ── Cột bảng catalogue ──────────────────────────────────────────
    private void setupColumns() {
        colMaDV.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMadv()));
        colTenDV.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTendv()));
        colLoai.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLoaidv()));
        colDonGia.setCellValueFactory(d
                -> new SimpleStringProperty(fmt.format(d.getValue().getDongia()) + " ₫"));
        colSoLuong.setCellValueFactory(d
                -> new SimpleStringProperty(String.valueOf(d.getValue().getSoluongton())));

        tableDichVu.getSelectionModel().selectedItemProperty().addListener((obs, old, nw) -> {
            if (nw != null) {
                lblChonDV.setText(nw.getTendv());
                lblGia.setText(fmt.format(nw.getDongia()) + " ₫");
                updateTongTien(nw);
                lblFormError.setText("");
            }
        });
    }

    // ── Cột bảng dịch vụ đã đặt ────────────────────────────────────
    private void setupColumnsDaDat() {
        colDDTen.setCellValueFactory(d -> {
            DichVu dv = dichVuDAO.getByID(d.getValue().getMadv());
            return new SimpleStringProperty(dv != null ? dv.getTendv() : d.getValue().getMadv());
        });
        colDDSoLuong.setCellValueFactory(d
                -> new SimpleStringProperty(String.valueOf(d.getValue().getSoluong())));
        colDDDonGia.setCellValueFactory(d
                -> new SimpleStringProperty(fmt.format(d.getValue().getDongia()) + " ₫"));
        colDDThanhTien.setCellValueFactory(d
                -> new SimpleStringProperty(fmt.format(d.getValue().getThanhtien()) + " ₫"));
        colDDThoiGian.setCellValueFactory(d -> {
            LocalDateTime t = d.getValue().getThoigian();
            return new SimpleStringProperty(t != null ? t.format(dtf) : "");
        });
    }

    private void setupSpinner() {
        SpinnerValueFactory<Integer> factory
                = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1);
        spinnerSoLuong.setValueFactory(factory);
        spinnerSoLuong.valueProperty().addListener((obs, old, nw) -> {
            DichVu selected = tableDichVu.getSelectionModel().getSelectedItem();
            if (selected != null) {
                updateTongTien(selected);
            }
        });
    }

    private void updateTongTien(DichVu dv) {
        int sl = spinnerSoLuong.getValue();
        lblTongTien.setText(fmt.format(dv.getDongia() * sl) + " ₫");
    }

    private void loadPhienAndDichVu() {
        String maKH = SessionManager.getCurrentMaKH();
        if (maKH == null) {
            return;
        }

        try {
            phienHienTai = phienDAO.getPhienDangChoiByKhachHang(maKH);
        } catch (Exception e) {
            phienHienTai = null;
        }

        if (phienHienTai != null) {
            lblPhienInfo.setText("Phiên: " + phienHienTai.getMaPhien() + " | Máy: " + phienHienTai.getMaMay());
            lblCanhBao.setVisible(false);
            lblCanhBao.setManaged(false);
            btnDat.setDisable(false);
            paneForm.setDisable(false);
        } else {
            lblPhienInfo.setText("Bạn chưa có phiên sử dụng.");
            lblCanhBao.setText(" Bạn chưa sử dụng máy. Vui lòng yêu cầu nhân viên mở phiên trước khi đặt dịch vụ.");
            lblCanhBao.setVisible(true);
            lblCanhBao.setManaged(true);
            btnDat.setDisable(true);
            paneForm.setDisable(true);
        }

        try {
            allDV = dichVuBUS.getDichVuConHang();
            tableDichVu.setItems(FXCollections.observableArrayList(allDV));

            List<String> loais = allDV.stream()
                    .map(DichVu::getLoaidv).distinct().collect(Collectors.toList());
            loais.add(0, "Tất cả");
            cboLoai.setItems(FXCollections.observableArrayList(loais));
            cboLoai.getSelectionModel().selectFirst();
        } catch (Exception e) {
            System.err.println("Lỗi load dịch vụ: " + e.getMessage());
        }

        loadDaDat();
    }

    /**
     * Tải lại danh sách dịch vụ KH đã đặt trong phiên hiện tại
     */
    private void loadDaDat() {
        if (phienHienTai == null) {
            paneDaDat.setVisible(false);
            paneDaDat.setManaged(false);
            return;
        }
        try {
            List<SuDungDichVu> ds = suDungDAO.geyByPhien(phienHienTai.getMaPhien());
            if (ds == null || ds.isEmpty()) {
                paneDaDat.setVisible(false);
                paneDaDat.setManaged(false);
                return;
            }
            tableDaDat.setItems(FXCollections.observableArrayList(ds));
            double tong = ds.stream().mapToDouble(SuDungDichVu::getThanhtien).sum();
            lblTongDaDat.setText("Tổng: " + fmt.format(tong) + " ₫");
            paneDaDat.setVisible(true);
            paneDaDat.setManaged(true);
        } catch (Exception e) {
            System.err.println("Lỗi load dịch vụ đã đặt: " + e.getMessage());
            paneDaDat.setVisible(false);
            paneDaDat.setManaged(false);
        }
    }

    @FXML
    private void handleFilter() {
        String loai = cboLoai.getValue();
        if (loai == null || "Tất cả".equals(loai)) {
            tableDichVu.setItems(FXCollections.observableArrayList(allDV));
        } else {
            ObservableList<DichVu> filtered = FXCollections.observableArrayList(
                    allDV.stream().filter(dv -> loai.equals(dv.getLoaidv())).collect(Collectors.toList()));
            tableDichVu.setItems(filtered);
        }
    }

    @FXML
    private void handleDatDichVu() {
        DichVu selected = tableDichVu.getSelectionModel().getSelectedItem();
        if (selected == null) {
            lblFormError.setText("Vui lòng chọn một dịch vụ.");
            return;
        }
        if (phienHienTai == null) {
            lblFormError.setText("Bạn chưa có phiên đang chạy.");
            return;
        }

        int sl = spinnerSoLuong.getValue();
        if (sl < 1) {
            lblFormError.setText("Số lượng phải ít nhất là 1.");
            return;
        }

        // Kiểm tra số dư trước khi đặt dịch vụ
        try {
            // Lấy số dư mới nhất từ DB
            entity.KhachHang kh = khachHangDAO.getById(phienHienTai.getMaKH());
            double soDu = kh != null ? kh.getSodu() : 0;

            // Tiền máy đến hiện tại
            double tienMay = 0;
            if (phienHienTai.getGioBatDau() != null && phienHienTai.getGiaMoiGio() > 0) {
                long phut = java.time.temporal.ChronoUnit.MINUTES.between(
                        phienHienTai.getGioBatDau(), LocalDateTime.now());
                tienMay = (phut / 60.0) * phienHienTai.getGiaMoiGio();
            }
            // Tiền dịch vụ đã order
            double tienDVDaOrder = 0;
            List<SuDungDichVu> daDat = suDungDAO.geyByPhien(phienHienTai.getMaPhien());
            if (daDat != null) {
                for (SuDungDichVu sv : daDat) {
                    tienDVDaOrder += sv.getThanhtien();
                }
            }
            // Tiền order mới
            double tienMoi = selected.getDongia() * sl;
            double tongCan = tienMay + tienDVDaOrder + tienMoi;

            if (soDu - tongCan < 0) {
                lblFormError.setStyle("-fx-text-fill: red;");
                lblFormError.setText(String.format(
                        "⚠ Số dư không đủ! Cần: %,.0f ₫  |  Hiện có: %,.0f ₫  |  Thiếu: %,.0f ₫",
                        tongCan, soDu, tongCan - soDu));
                return;
            }
        } catch (Exception e) {
            // Nếu không lấy được số dư thì bỏ qua kiểm tra (an toàn hơn là chặn hoàn toàn)
            System.err.println("[DatDV] Không lấy được số dư: " + e.getMessage());
        }

        try {
            SuDungDichVu su = new SuDungDichVu();
            su.setMaphien(phienHienTai.getMaPhien());
            su.setMadv(selected.getMadv());
            su.setSoluong(sl);
            su.setDongia(selected.getDongia());
            su.setThanhtien(selected.getDongia() * sl);
            su.setThoigian(LocalDateTime.now());

            boolean ok = suDungDAO.insert(su);
            if (ok) {
                lblFormError.setStyle("-fx-text-fill: #2e7d32;");
                lblFormError.setText("✅ Đặt thành công! Nhân viên sẽ phục vụ bạn sớm.");
                tableDichVu.getSelectionModel().clearSelection();
                spinnerSoLuong.getValueFactory().setValue(1);
                lblChonDV.setText("Chưa chọn");
                lblGia.setText("0 ₫");
                lblTongTien.setText("0 ₫");
                loadDaDat(); // refresh danh sách đã đặt
            } else {
                lblFormError.setStyle("-fx-text-fill: red;");
                lblFormError.setText("Đặt dịch vụ thất bại. Vui lòng thử lại.");
            }
        } catch (Exception e) {
            lblFormError.setStyle("-fx-text-fill: red;");
            lblFormError.setText("Lỗi: " + e.getMessage());
        }
    }

    @FXML
    private void handleHuy() {
        tableDichVu.getSelectionModel().clearSelection();
        spinnerSoLuong.getValueFactory().setValue(1);
        lblChonDV.setText("Chưa chọn");
        lblGia.setText("0 ₫");
        lblTongTien.setText("0 ₫");
        lblFormError.setText("");
    }

    @FXML
    private void handleLamMoi() {
        loadPhienAndDichVu();
    }
}
