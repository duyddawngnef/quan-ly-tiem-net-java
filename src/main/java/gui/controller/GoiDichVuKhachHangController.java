package gui.controller;

import bus.GoiDichVuKhachHangBUS;
import bus.KhachHangBUS;
import bus.GoiDichVuBUS;
import entity.GoiDichVuKhachHang;
import entity.GoiDichVu;
import entity.KhachHang;
import gui.dialog.ThongBaoDialog;
import gui.dialog.XacNhanDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GoiDichVuKhachHangController implements Initializable {

    // ===================== TABLE CHÍNH =====================
    @FXML private TableView<GoiDichVuKhachHang>           tableView;
    @FXML private TableColumn<GoiDichVuKhachHang, String> colMaGoiKH, colMaKH, colMaGoi, colManv, colTrangThai;
    @FXML private TableColumn<GoiDichVuKhachHang, Double> colSoGioBanDau, colSoGioConLai, colGiaMua;
    @FXML private TableColumn<GoiDichVuKhachHang, String> colNgayMua, colNgayHetHan;

    // ===================== BẢNG KHÁCH HÀNG =====================
    @FXML private TableView<KhachHang>           tableKhachHang;
    @FXML private TableColumn<KhachHang, String> colKHMaKH, colKHTen, colKHTrangThai;
    @FXML private TableColumn<KhachHang, Double> colKHSoDu;

    // ===================== BẢNG GÓI CỦA 1 KH =====================
    @FXML private TableView<GoiDichVuKhachHang>           tableGoiCuaKH;
    @FXML private TableColumn<GoiDichVuKhachHang, String> colGKHMaGoiKH, colGKHMaGoi, colGKHManv, colGKHTrangThai;
    @FXML private TableColumn<GoiDichVuKhachHang, Double> colGKHSoGioBanDau, colGKHSoGioConLai, colGKHGiaMua;
    @FXML private TableColumn<GoiDichVuKhachHang, String> colGKHNgayMua, colGKHNgayHetHan;

    // ===================== TOOLBAR =====================
    @FXML private TextField        txtSearch;
    @FXML private ComboBox<String> cboFilterTrangThai;
    @FXML private ComboBox<String> cboFilterGoi;     // lọc theo mã gói, chỉ hiện ở chế độ ALL
    @FXML private ComboBox<String> cboViewMode;
    @FXML private Button           btnThem, btnLamMoi;

    // ===================== BACK BAR =====================
    @FXML private HBox   hboxBack;
    @FXML private Button btnBack;
    @FXML private Label  lblKHMode;

    // ===================== FORM MUA GÓI =====================
    @FXML private ScrollPane       paneDetail;
    @FXML private ComboBox<String> cboChonKH;
    @FXML private VBox             cardKH;
    @FXML private Label            lblAvatarKH, lblInfoTenKH, lblInfoSdtKH, lblInfoSoDuKH;
    @FXML private ListView<String> listGoiDichVu;
    @FXML private VBox             cardGoi;
    @FXML private Label            lblInfoMaGoi, lblInfoTenGoi, lblInfoSoGio, lblInfoGia;
    @FXML private Button           btnLuu, btnHuy;

    // ===================== LABELS =====================
    @FXML private Label lblSubtitle, lblTotal;

    // ===================== BUS =====================
    private final GoiDichVuKhachHangBUS bus    = new GoiDichVuKhachHangBUS();
    private final KhachHangBUS          khBUS  = new KhachHangBUS();
    private final GoiDichVuBUS          goiBUS = new GoiDichVuBUS();

    // ===================== STATE =====================
    private final ObservableList<GoiDichVuKhachHang> masterList   = FXCollections.observableArrayList();
    private FilteredList<GoiDichVuKhachHang>         filteredList;
    // danh sách KH cho bảng Theo KH (có thể lọc)
    private final ObservableList<KhachHang>          khMasterList = FXCollections.observableArrayList();
    private FilteredList<KhachHang>                  khFilteredList;

    private List<KhachHang>  dsKhachHang;
    private List<GoiDichVu>  dsGoiDichVu;
    private KhachHang        selectedKH = null;
    private KhachHang        viewingKH  = null;
    private String           viewMode   = "ALL"; // "ALL" | "BY_KH"

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ===================================================================
    //  KHỞI TẠO
    // ===================================================================
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupVietnameseText();
        setupTableColumns();
        setupKhachHangColumns();
        setupGoiCuaKHColumns();
        setupRowColors();
        setupRowColorsKH();
        setupComboBoxes();
        loadCacheData();
        hideForm();
        hide(hboxBack);

        cboViewMode.setValue("Tất cả gói");
        switchToAll();
        loadGoiFilterCombo(); // Điền combo gói ngay sau khi cache đã sẵn sàng
        loadData();

        // Click vào bảng KH
        if (tableKhachHang != null) {
            tableKhachHang.setOnMouseClicked(e -> {
                if (e.getClickCount() >= 1) {
                    KhachHang kh = tableKhachHang.getSelectionModel().getSelectedItem();
                    if (kh != null) handleClickKhachHang(kh);
                }
            });
        }
    }

    // ===================================================================
    //  SETUP TEXT
    // ===================================================================
    private void setupVietnameseText() {
        if (colMaGoiKH      != null) colMaGoiKH.setText("Mã gói KH");
        if (colMaKH         != null) colMaKH.setText("Mã KH");
        if (colMaGoi        != null) colMaGoi.setText("Mã gói");
        if (colManv         != null) colManv.setText("Nhân viên");
        if (colSoGioBanDau  != null) colSoGioBanDau.setText("Giờ ban đầu");
        if (colSoGioConLai  != null) colSoGioConLai.setText("Giờ còn lại");
        if (colNgayMua      != null) colNgayMua.setText("Ngày mua");
        if (colNgayHetHan   != null) colNgayHetHan.setText("Ngày hết hạn");
        if (colGiaMua       != null) colGiaMua.setText("Giá mua (VNĐ)");
        if (colTrangThai    != null) colTrangThai.setText("Trạng thái");

        if (colKHMaKH      != null) colKHMaKH.setText("Mã KH");
        if (colKHTen       != null) colKHTen.setText("Họ và tên");
        if (colKHSoDu      != null) colKHSoDu.setText("Số dư (VNĐ)");
        if (colKHTrangThai != null) colKHTrangThai.setText("Trạng thái");

        if (colGKHMaGoiKH     != null) colGKHMaGoiKH.setText("Mã gói KH");
        if (colGKHMaGoi       != null) colGKHMaGoi.setText("Mã gói");
        if (colGKHManv        != null) colGKHManv.setText("Nhân viên");
        if (colGKHSoGioBanDau != null) colGKHSoGioBanDau.setText("Giờ ban đầu");
        if (colGKHSoGioConLai != null) colGKHSoGioConLai.setText("Giờ còn lại");
        if (colGKHNgayMua     != null) colGKHNgayMua.setText("Ngày mua");
        if (colGKHNgayHetHan  != null) colGKHNgayHetHan.setText("Ngày hết hạn");
        if (colGKHGiaMua      != null) colGKHGiaMua.setText("Giá mua (VNĐ)");
        if (colGKHTrangThai   != null) colGKHTrangThai.setText("Trạng thái");

        if (btnThem   != null) btnThem.setText("+ Mua gói");
        if (btnLamMoi != null) btnLamMoi.setText("Làm mới");
        if (btnBack   != null) btnBack.setText("← Quay lại");
        if (btnLuu    != null) btnLuu.setText("Xác nhận mua");
        if (btnHuy    != null) btnHuy.setText("Hủy");
    }

    // ===================================================================
    //  COMBOBOXES
    // ===================================================================
    private void setupComboBoxes() {
        // Chế độ xem
        if (cboViewMode != null) {
            cboViewMode.getItems().setAll("Tất cả gói", "Theo khách hàng");
            cboViewMode.setValue("Tất cả gói");
        }

        // Lọc trạng thái
        if (cboFilterTrangThai != null) {
            cboFilterTrangThai.getItems().setAll("Tất cả", "CONHAN", "DAHETGIO", "HETHAN");
            cboFilterTrangThai.setValue("Tất cả");
            cboFilterTrangThai.setCellFactory(lv -> new ListCell<>() {
                @Override protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : trangThaiText(item));
                }
            });
            cboFilterTrangThai.setButtonCell(new ListCell<>() {
                @Override protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : trangThaiText(item));
                }
            });
            cboFilterTrangThai.setOnAction(e -> handleSearch());
        }

        // Lọc theo gói dịch vụ (chỉ dùng ở chế độ ALL)
        if (cboFilterGoi != null) {
            cboFilterGoi.setOnAction(e -> handleSearch());
        }
    }

    // ===================================================================
    //  LOAD CACHE
    // ===================================================================
    private void loadCacheData() {
        try {
            dsKhachHang = khBUS.getAllKhachHang();
            if (dsKhachHang == null) dsKhachHang = java.util.Collections.emptyList();
        } catch (Exception e) {
            dsKhachHang = java.util.Collections.emptyList();
        }
        try {
            dsGoiDichVu = goiBUS.getAll();
            if (dsGoiDichVu == null) dsGoiDichVu = new java.util.ArrayList<>();
            System.out.println("[DEBUG] Loaded " + dsGoiDichVu.size() + " goi dich vu");
        } catch (Exception e) {
            System.err.println("[DEBUG] loadCacheData goiBUS loi: " + e.getMessage());
            dsGoiDichVu = new java.util.ArrayList<>();
        }
    }

    // Điền danh sách gói vào cboFilterGoi sau khi có data
    private void loadGoiFilterCombo() {
        if (cboFilterGoi == null) return;
        String current = cboFilterGoi.getValue();
        cboFilterGoi.getItems().clear();
        cboFilterGoi.getItems().add("Tất cả gói");
        if (dsGoiDichVu != null) {
            for (GoiDichVu g : dsGoiDichVu) {
                cboFilterGoi.getItems().add(g.getMagoi() + " - " + g.getTengoi());
            }
        }
        System.out.println("[DEBUG] cboFilterGoi items: " + cboFilterGoi.getItems().size());
        // Giữ lựa chọn cũ nếu còn tồn tại
        if (current != null && cboFilterGoi.getItems().contains(current)) {
            cboFilterGoi.setValue(current);
        } else {
            cboFilterGoi.setValue("Tất cả gói");
        }
        // Đảm bảo visible trong chế độ ALL
        if ("ALL".equals(viewMode)) {
            cboFilterGoi.setVisible(true);
            cboFilterGoi.setManaged(true);
        }
    }

    // ===================================================================
    //  THAY ĐỔI CHẾ ĐỘ XEM
    // ===================================================================
    @FXML
    public void handleViewModeChange() {
        if (cboViewMode == null) return;
        String val = cboViewMode.getValue();
        if ("Theo khách hàng".equals(val)) {
            viewMode  = "BY_KH";
            viewingKH = null;
            switchToKHList();
        } else {
            viewMode = "ALL";
            switchToAll();
            loadData();
        }
        hideForm();
        txtSearch.clear();
    }

    private void switchToAll() {
        show(tableView);
        hide(tableKhachHang);
        hide(tableGoiCuaKH);
        hide(hboxBack);
        // Hiện cả 2 bộ lọc của chế độ ALL
        if (cboFilterTrangThai != null) { cboFilterTrangThai.setVisible(true); cboFilterTrangThai.setManaged(true); }
        if (cboFilterGoi       != null) { cboFilterGoi.setVisible(true);       cboFilterGoi.setManaged(true); }
        txtSearch.setPromptText("🔍 Tìm theo mã KH, mã gói KH...");
        // Điền combo gói nếu đã có data (trường hợp chuyển qua lại giữa 2 chế độ)
        if (dsGoiDichVu != null && !dsGoiDichVu.isEmpty()) loadGoiFilterCombo();
    }

    private void switchToKHList() {
        hide(tableView);
        show(tableKhachHang);
        hide(tableGoiCuaKH);
        hide(hboxBack);
        // Ẩn bộ lọc của chế độ ALL
        hide(cboFilterTrangThai);
        hide(cboFilterGoi);
        txtSearch.setPromptText("🔍 Tìm theo tên hoặc mã KH...");
        if (lblSubtitle != null) lblSubtitle.setText("Chọn khách hàng để xem gói dịch vụ");
        // Load danh sách KH với FilteredList
        khMasterList.setAll(dsKhachHang);
        khFilteredList = new FilteredList<>(khMasterList, p -> true);
        tableKhachHang.setItems(khFilteredList);
        updateTotal(dsKhachHang.size(), "khách hàng");
    }

    // ===================================================================
    //  CLICK VÀO 1 KH → HIỆN GÓI
    // ===================================================================
    private void handleClickKhachHang(KhachHang kh) {
        viewingKH = kh;
        String tenKH = ((kh.getHo() != null ? kh.getHo() : "") + " "
                + (kh.getTen() != null ? kh.getTen() : "")).trim();

        hide(tableKhachHang);
        show(tableGoiCuaKH);
        show(hboxBack);

        if (lblKHMode != null)
            lblKHMode.setText("Gói của: " + tenKH + " (" + kh.getMakh() + ")");

        try {
            List<GoiDichVuKhachHang> data = bus.getAll();
            if (data == null) data = java.util.Collections.emptyList();
            List<GoiDichVuKhachHang> filtered = data.stream()
                    .filter(g -> kh.getMakh().equals(g.getMakh()))
                    .collect(Collectors.toList());
            tableGoiCuaKH.setItems(FXCollections.observableArrayList(filtered));
            if (lblSubtitle != null) lblSubtitle.setText(tenKH + " — " + filtered.size() + " gói");
            updateTotal(filtered.size(), "gói");
        } catch (Exception e) {
            showError("Lỗi tải gói: " + e.getMessage());
        }
    }

    // ===================================================================
    //  NÚT BACK
    // ===================================================================
    @FXML
    public void handleBack() {
        viewingKH = null;
        txtSearch.clear();
        switchToKHList();
    }

    // ===================================================================
    //  LOAD DATA (chế độ ALL)
    // ===================================================================
    @FXML
    public void loadData() {
        try {
            List<GoiDichVuKhachHang> data = bus.getAll();
            if (data == null) data = java.util.Collections.emptyList();
            masterList.setAll(data);
            filteredList = new FilteredList<>(masterList, p -> true);
            tableView.setItems(filteredList);
            if (lblSubtitle != null) lblSubtitle.setText("Tổng cộng: " + data.size() + " gói dịch vụ");
            updateTotal(data.size(), "gói");
            loadGoiFilterCombo();
            handleSearch();
        } catch (Exception e) {
            showError("Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    // ===================================================================
    //  TÌM KIẾM — realtime, hoạt động theo từng chế độ
    // ===================================================================
    @FXML
    public void handleSearch() {
        String kw = txtSearch != null ? txtSearch.getText().toLowerCase().trim() : "";

        if ("ALL".equals(viewMode)) {
            // --- Chế độ Tất cả: lọc theo mã KH / mã gói KH, trạng thái, gói DV ---
            if (filteredList == null) return;
            String tt  = cboFilterTrangThai != null ? cboFilterTrangThai.getValue() : "Tất cả";
            String goiFilter = cboFilterGoi != null ? cboFilterGoi.getValue() : "Tất cả gói";

            filteredList.setPredicate(item -> {
                // tìm theo mã KH hoặc mã gói KH
                boolean matchKw = kw.isEmpty()
                        || item.getMakh().toLowerCase().contains(kw)
                        || item.getMagoikh().toLowerCase().contains(kw);
                // lọc trạng thái
                boolean matchTT = "Tất cả".equals(tt) || item.getTrangthai().equals(tt);
                // lọc gói dịch vụ
                boolean matchGoi = goiFilter == null || "Tất cả gói".equals(goiFilter)
                        || item.getMagoi().equals(goiFilter.split(" - ")[0]);
                return matchKw && matchTT && matchGoi;
            });
            updateTotal((int) filteredList.stream().count(), "gói");

        } else if ("BY_KH".equals(viewMode) && viewingKH == null) {
            // --- Chế độ Theo KH, đang ở danh sách KH: tìm theo mã hoặc tên ---
            if (khFilteredList == null) return;
            khFilteredList.setPredicate(kh -> {
                if (kw.isEmpty()) return true;
                String tenDay = ((kh.getHo() != null ? kh.getHo() : "") + " "
                        + (kh.getTen() != null ? kh.getTen() : "")).toLowerCase();
                return kh.getMakh().toLowerCase().contains(kw)
                        || tenDay.contains(kw);
            });
            updateTotal((int) khFilteredList.stream().count(), "khách hàng");
        }
        // Khi đang xem gói của 1 KH cụ thể thì không cần search
    }

    // ===================================================================
    //  MUA GÓI
    // ===================================================================
    @FXML
    public void handleThem() {
        loadCacheData();
        clearBuyForm();
        setupBuyFormListeners();
        showForm();
        tableView.getSelectionModel().clearSelection();
    }

    private void setupBuyFormListeners() {
        if (cboChonKH != null) {
            cboChonKH.getItems().clear();
            for (KhachHang kh : dsKhachHang) {
                cboChonKH.getItems().add(kh.getMakh() + " - " + kh.getHo() + " " + kh.getTen());
            }
            cboChonKH.setPromptText("Tìm hoặc chọn khách hàng...");
            cboChonKH.setOnAction(e -> handleChonKH());
        }

        if (listGoiDichVu != null) {
            listGoiDichVu.getItems().clear();
            List<GoiDichVu> goiHoatDong = dsGoiDichVu.stream()
                    .filter(g -> "HOATDONG".equals(g.getTrangthai()))
                    .collect(Collectors.toList());
            for (GoiDichVu g : goiHoatDong) {
                listGoiDichVu.getItems().add(g.getMagoi() + " - " + g.getTengoi());
            }
            listGoiDichVu.setPrefHeight(Math.min(5, goiHoatDong.size()) * 28 + 4);

            listGoiDichVu.getSelectionModel().selectedItemProperty()
                    .addListener((obs, o, newVal) -> {
                        if (newVal == null) { hide(cardGoi); return; }
                        String maGoi = newVal.split(" - ")[0];
                        GoiDichVu goi = dsGoiDichVu.stream()
                                .filter(g -> g.getMagoi().equals(maGoi)).findFirst().orElse(null);
                        if (goi != null) {
                            if (lblInfoMaGoi  != null) lblInfoMaGoi.setText(goi.getMagoi());
                            if (lblInfoTenGoi != null) lblInfoTenGoi.setText(goi.getTengoi());
                            if (lblInfoSoGio  != null) lblInfoSoGio.setText(String.format("%.0f giờ", goi.getSogio()));
                            if (lblInfoGia    != null) lblInfoGia.setText(String.format("%,.0f đ", goi.getGiagoi()));
                            show(cardGoi);
                        }
                    });
        }
    }

    @FXML
    public void handleChonKH() {
        if (cboChonKH == null) return;
        String val = cboChonKH.getValue();
        if (val == null || val.isBlank()) { hide(cardKH); selectedKH = null; return; }

        String maKH = val.split(" - ")[0].trim();
        selectedKH = dsKhachHang.stream()
                .filter(kh -> kh.getMakh().equals(maKH)).findFirst().orElse(null);

        if (selectedKH != null) {
            String ten  = ((selectedKH.getHo() != null ? selectedKH.getHo() : "") + " "
                    + (selectedKH.getTen() != null ? selectedKH.getTen() : "")).trim();
            String init = ten.isEmpty() ? "?" : String.valueOf(ten.charAt(0)).toUpperCase();
            if (lblAvatarKH   != null) lblAvatarKH.setText(init);
            if (lblInfoTenKH  != null) lblInfoTenKH.setText(ten);
            if (lblInfoSdtKH  != null) lblInfoSdtKH.setText(
                    selectedKH.getSodienthoai() != null ? selectedKH.getSodienthoai() : "Chưa có SĐT");
            if (lblInfoSoDuKH != null)
                lblInfoSoDuKH.setText(String.format("%,.0f đ", selectedKH.getSodu()));
            show(cardKH);
        } else {
            hide(cardKH);
        }
    }

    // ===================================================================
    //  XÁC NHẬN MUA
    // ===================================================================
    @FXML
    public void handleLuuDetail() {
        Stage owner = getOwnerStage();
        if (selectedKH == null) { showError("Vui lòng chọn khách hàng!"); return; }

        String goiItem = listGoiDichVu != null
                ? listGoiDichVu.getSelectionModel().getSelectedItem() : null;
        if (goiItem == null) { showError("Vui lòng chọn gói dịch vụ!"); return; }

        String maGoi = goiItem.split(" - ")[0];
        boolean ok = XacNhanDialog.show(owner, "Xác nhận mua gói",
                "Mua gói " + maGoi + " cho khách " + selectedKH.getMakh() + "?");
        if (!ok) return;

        try {
            bus.muaGoi(maGoi, selectedKH.getMakh());
            ThongBaoDialog.showSuccess(owner, "Mua gói dịch vụ thành công!");
            hideForm();
            loadCacheData();
            loadData();
            if ("BY_KH".equals(viewMode) && viewingKH != null
                    && viewingKH.getMakh().equals(selectedKH.getMakh())) {
                handleClickKhachHang(viewingKH);
            }
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void handleHuyDetail() { hideForm(); }

    // ===================================================================
    //  LÀM MỚI
    // ===================================================================
    @FXML
    public void handleLamMoi() {
        if (txtSearch != null) txtSearch.clear();
        if (cboFilterTrangThai != null) cboFilterTrangThai.setValue("Tất cả");
        if (cboFilterGoi != null) cboFilterGoi.setValue("Tất cả gói");
        hideForm();
        loadCacheData();
        if ("ALL".equals(viewMode)) {
            loadData();
        } else {
            viewingKH = null;
            switchToKHList();
        }
    }

    // ===================================================================
    //  SETUP COLUMNS
    // ===================================================================
    private void setupTableColumns() {
        colMaGoiKH.setCellValueFactory(new PropertyValueFactory<>("magoikh"));
        colMaKH.setCellValueFactory(new PropertyValueFactory<>("makh"));
        colMaGoi.setCellValueFactory(new PropertyValueFactory<>("magoi"));
        colManv.setCellValueFactory(new PropertyValueFactory<>("manv"));
        colSoGioBanDau.setCellValueFactory(new PropertyValueFactory<>("sogiobandau"));
        colSoGioConLai.setCellValueFactory(new PropertyValueFactory<>("sogioconlai"));
        colGiaMua.setCellValueFactory(new PropertyValueFactory<>("giamua"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangthai"));

        colNgayMua.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setText(null); return; }
                GoiDichVuKhachHang item = getTableView().getItems().get(getIndex());
                setText(item.getNgaymua() != null ? item.getNgaymua().format(FMT) : "");
                setStyle("-fx-alignment:CENTER;");
            }
        });
        colNgayHetHan.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setText(null); return; }
                GoiDichVuKhachHang item = getTableView().getItems().get(getIndex());
                setText(item.getNgayhethan() != null ? item.getNgayhethan().format(FMT) : "");
                setStyle("-fx-alignment:CENTER;");
            }
        });

        setCenterCell(colMaGoiKH,   v -> v);
        setCenterCell(colMaKH,      v -> v);
        setCenterCell(colMaGoi,     v -> v);
        setCenterCell(colManv,      v -> v);
        setCenterCell(colTrangThai, this::trangThaiText);
        setCenterCellDouble(colSoGioBanDau);
        setCenterCellDouble(colSoGioConLai);
        setCenterCellDouble(colGiaMua);
    }

    private void setupKhachHangColumns() {
        if (colKHMaKH == null) return;
        colKHMaKH.setCellValueFactory(new PropertyValueFactory<>("makh"));
        colKHSoDu.setCellValueFactory(new PropertyValueFactory<>("sodu"));
        colKHTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangthai"));

        colKHTen.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setText(null); return; }
                KhachHang kh = getTableView().getItems().get(getIndex());
                setText(((kh.getHo() != null ? kh.getHo() : "") + " "
                        + (kh.getTen() != null ? kh.getTen() : "")).trim());
                setStyle("-fx-alignment:CENTER_LEFT; -fx-padding:0 8;");
            }
        });
        colKHSoDu.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); return; }
                setText(String.format("%,.0f đ", v));
                setStyle("-fx-alignment:CENTER;");
            }
        });
        colKHMaKH.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : v);
                setStyle("-fx-alignment:CENTER;");
            }
        });
        colKHTrangThai.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); return; }
                setText("NGUNG".equals(v) ? "Đã khóa" : "Hoạt động");
                setStyle("-fx-alignment:CENTER;");
            }
        });
    }

    private void setupGoiCuaKHColumns() {
        if (colGKHMaGoiKH == null) return;
        colGKHMaGoiKH.setCellValueFactory(new PropertyValueFactory<>("magoikh"));
        colGKHMaGoi.setCellValueFactory(new PropertyValueFactory<>("magoi"));
        colGKHManv.setCellValueFactory(new PropertyValueFactory<>("manv"));
        colGKHSoGioBanDau.setCellValueFactory(new PropertyValueFactory<>("sogiobandau"));
        colGKHSoGioConLai.setCellValueFactory(new PropertyValueFactory<>("sogioconlai"));
        colGKHGiaMua.setCellValueFactory(new PropertyValueFactory<>("giamua"));
        colGKHTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangthai"));

        colGKHNgayMua.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setText(null); return; }
                GoiDichVuKhachHang item = getTableView().getItems().get(getIndex());
                setText(item.getNgaymua() != null ? item.getNgaymua().format(FMT) : "");
                setStyle("-fx-alignment:CENTER;");
            }
        });
        colGKHNgayHetHan.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setText(null); return; }
                GoiDichVuKhachHang item = getTableView().getItems().get(getIndex());
                setText(item.getNgayhethan() != null ? item.getNgayhethan().format(FMT) : "");
                setStyle("-fx-alignment:CENTER;");
            }
        });

        setCenterCellGKH(colGKHMaGoiKH,   v -> v);
        setCenterCellGKH(colGKHMaGoi,     v -> v);
        setCenterCellGKH(colGKHManv,      v -> v);
        setCenterCellGKH(colGKHTrangThai, this::trangThaiText);
        setCenterCellDoubleGKH(colGKHSoGioBanDau);
        setCenterCellDoubleGKH(colGKHSoGioConLai);
        setCenterCellDoubleGKH(colGKHGiaMua);
    }

    // ===================================================================
    //  ROW COLORS
    // ===================================================================
    private void setupRowColors() {
        tableView.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(GoiDichVuKhachHang item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setStyle(""); return; }
                applyGoiStyle(this, item.getTrangthai(), isSelected());
            }
        });
        tableView.getSelectionModel().selectedItemProperty()
                .addListener((obs, o, n) -> tableView.refresh());

        if (tableGoiCuaKH != null) {
            tableGoiCuaKH.setRowFactory(tv -> new TableRow<>() {
                @Override protected void updateItem(GoiDichVuKhachHang item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) { setStyle(""); return; }
                    applyGoiStyle(this, item.getTrangthai(), isSelected());
                }
            });
            tableGoiCuaKH.getSelectionModel().selectedItemProperty()
                    .addListener((obs, o, n) -> tableGoiCuaKH.refresh());
        }
    }

    private void applyGoiStyle(TableRow<?> row, String tt, boolean sel) {
        if (sel) {
            switch (tt) {
                case "CONHAN"   -> row.setStyle("-fx-background-color:#1b5e20; -fx-text-fill:white;");
                case "DAHETGIO" -> row.setStyle("-fx-background-color:#e65100; -fx-text-fill:white;");
                case "HETHAN"   -> row.setStyle("-fx-background-color:#b71c1c; -fx-text-fill:white;");
                default         -> row.setStyle("-fx-background-color:#37474f; -fx-text-fill:white;");
            }
        } else {
            switch (tt) {
                case "CONHAN"   -> row.setStyle("-fx-background-color:#dcedc8; -fx-text-fill:#1b5e20;");
                case "DAHETGIO" -> row.setStyle("-fx-background-color:#ffe0b2; -fx-text-fill:#e65100;");
                case "HETHAN"   -> row.setStyle("-fx-background-color:#ffcdd2; -fx-text-fill:#b71c1c;");
                default         -> row.setStyle("");
            }
        }
    }

    private void setupRowColorsKH() {
        if (tableKhachHang == null) return;
        tableKhachHang.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(KhachHang item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setStyle(""); return; }
                if (isSelected()) {
                    setStyle("-fx-background-color:#1565c0; -fx-text-fill:white;");
                } else if ("NGUNG".equals(item.getTrangthai())) {
                    setStyle("-fx-background-color:#ffcdd2; -fx-text-fill:#b71c1c;");
                } else {
                    setStyle("");
                }
            }
        });
        tableKhachHang.getSelectionModel().selectedItemProperty()
                .addListener((obs, o, n) -> tableKhachHang.refresh());
    }

    // ===================================================================
    //  HELPERS
    // ===================================================================
    private void clearBuyForm() {
        if (cboChonKH     != null) { cboChonKH.setValue(null); cboChonKH.getItems().clear(); }
        if (cardKH        != null) hide(cardKH);
        if (listGoiDichVu != null) listGoiDichVu.getSelectionModel().clearSelection();
        if (cardGoi       != null) hide(cardGoi);
        selectedKH = null;
    }

    private void showForm() {
        if (paneDetail != null) { paneDetail.setVisible(true);  paneDetail.setManaged(true); }
    }
    private void hideForm() {
        if (paneDetail != null) { paneDetail.setVisible(false); paneDetail.setManaged(false); }
    }
    private void show(javafx.scene.Node n) {
        if (n != null) { n.setVisible(true);  n.setManaged(true); }
    }
    private void hide(javafx.scene.Node n) {
        if (n != null) { n.setVisible(false); n.setManaged(false); }
    }
    // Overloads tường minh cho ComboBox để tránh ambiguity
    private <T> void show(ComboBox<T> c) { if (c != null) { c.setVisible(true);  c.setManaged(true); } }
    private <T> void hide(ComboBox<T> c) { if (c != null) { c.setVisible(false); c.setManaged(false); } }
    private void updateTotal(int count, String unit) {
        if (lblTotal != null) lblTotal.setText("Tổng: " + count + " " + unit);
    }

    // ===================================================================
    //  CELL FACTORIES
    // ===================================================================
    private void setCenterCell(TableColumn<GoiDichVuKhachHang, String> col,
                               java.util.function.Function<String, String> mapper) {
        col.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) setText(null);
                else { setText(mapper.apply(v)); setStyle("-fx-alignment:CENTER;"); }
            }
        });
    }
    private void setCenterCellDouble(TableColumn<GoiDichVuKhachHang, Double> col) {
        col.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) setText(null);
                else { setText(String.format("%,.1f", v)); setStyle("-fx-alignment:CENTER;"); }
            }
        });
    }
    private void setCenterCellGKH(TableColumn<GoiDichVuKhachHang, String> col,
                                  java.util.function.Function<String, String> mapper) {
        col.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) setText(null);
                else { setText(mapper.apply(v)); setStyle("-fx-alignment:CENTER;"); }
            }
        });
    }
    private void setCenterCellDoubleGKH(TableColumn<GoiDichVuKhachHang, Double> col) {
        col.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) setText(null);
                else { setText(String.format("%,.1f", v)); setStyle("-fx-alignment:CENTER;"); }
            }
        });
    }

    private String trangThaiText(String code) {
        if (code == null) return "";
        return switch (code) {
            case "CONHAN"   -> "Còn hạn";
            case "DAHETGIO" -> "Đã hết giờ";
            case "HETHAN"   -> "Hết hạn";
            case "Tất cả"   -> "Tất cả";
            default         -> code;
        };
    }

    private Stage getOwnerStage() {
        if (tableView != null && tableView.getScene() != null)
            return (Stage) tableView.getScene().getWindow();
        return null;
    }
    private void showError(String msg) {
        ThongBaoDialog.showError(getOwnerStage(), msg);
    }
}