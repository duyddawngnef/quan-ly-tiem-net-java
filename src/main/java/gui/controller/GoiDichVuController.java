package gui.controller;

import bus.GoiDichVuBUS;
import entity.GoiDichVu;
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
import javafx.stage.Stage;
import javafx.stage.FileChooser;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GoiDichVuController implements Initializable {

    // === TABLE ===
    @FXML private TableView<GoiDichVu>             tableView;
    @FXML private TableColumn<GoiDichVu, String>   colMaGoi, colTenGoi, colLoaiGoi, colTrangThai;
    @FXML private TableColumn<GoiDichVu, Double>   colSoGio, colGiaGoc, colGiaGoi;
    @FXML private TableColumn<GoiDichVu, Integer>  colSoNgay;

    // === TOOLBAR ===
    @FXML private TextField        txtSearch;
    @FXML private ComboBox<String> cboFilterLoai;
    @FXML private Button           btnThem, btnXoa, btnKhoiPhuc, btnLamMoi, btnXuatExcel;

    // === FORM ===
    @FXML private ScrollPane       paneDetail;
    @FXML private TextField        detailMaGoi, detailTenGoi, detailSoGio, detailSoNgay,
            detailGiaGoc, detailGiaGoi;
    @FXML private ComboBox<String> detailLoaiGoi, detailTrangThai;
    @FXML private Button           btnLuu, btnHuy;

    // === LABELS ===
    @FXML private Label lblSubtitle, lblTotal;
    @FXML private Label lblFormTitle, lblMaGoi, lblTenGoi, lblLoaiGoi,
            lblSoGio, lblSoNgay, lblGiaGoc, lblGiaGoi, lblTrangThai;

    private final GoiDichVuBUS bus = new GoiDichVuBUS();
    private final ObservableList<GoiDichVu> masterList = FXCollections.observableArrayList();
    private FilteredList<GoiDichVu> filteredList;

    private enum FormMode { NONE, THEM, SUA }
    private FormMode formMode = FormMode.NONE;
    private GoiDichVu selectedGoi = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupVietnameseText();
        setupTableColumns();
        setupRowColors();
        setupComboBoxes();
        hideForm();
        loadData();

        tableView.setOnMouseClicked(e -> {
            GoiDichVu sel = tableView.getSelectionModel().getSelectedItem();
            if (sel == null) resetForm();
            else handleRowClick();
        });
    }

    private void setupVietnameseText() {
        if (colMaGoi    != null) colMaGoi.setText("Mã gói");
        if (colTenGoi   != null) colTenGoi.setText("Tên gói");
        if (colLoaiGoi  != null) colLoaiGoi.setText("Loại gói");
        if (colSoGio    != null) colSoGio.setText("Số giờ");
        if (colSoNgay   != null) colSoNgay.setText("Hiệu lực");
        if (colGiaGoc   != null) colGiaGoc.setText("Giá gốc");
        if (colGiaGoi   != null) colGiaGoi.setText("Giá bán");
        if (colTrangThai!= null) colTrangThai.setText("Trạng thái");

        if (txtSearch    != null) txtSearch.setPromptText("🔍 Tìm theo tên, mã...");
        if (btnThem      != null) btnThem.setText("+ Thêm");
        if (btnXoa       != null) btnXoa.setText("Xóa");
        if (btnKhoiPhuc  != null) btnKhoiPhuc.setText("Khôi phục");
        if (btnLamMoi    != null) btnLamMoi.setText("Làm mới");
        if (btnXuatExcel != null) {
            btnXuatExcel.setText("📥 Xuất Excel");
            String baseStyle = "-fx-background-color:#16A34A; -fx-text-fill:white; "
                    + "-fx-font-weight:bold; -fx-padding:8 16; "
                    + "-fx-background-radius:6; -fx-cursor:hand;";
            btnXuatExcel.setStyle(baseStyle);
            btnXuatExcel.setOnMouseEntered(e -> btnXuatExcel.setStyle(
                    "-fx-background-color:#15803D; -fx-text-fill:white; "
                            + "-fx-font-weight:bold; -fx-padding:8 16; "
                            + "-fx-background-radius:6; -fx-cursor:hand;"));
            btnXuatExcel.setOnMouseExited(e  -> btnXuatExcel.setStyle(baseStyle));
            btnXuatExcel.setOnMousePressed(e -> btnXuatExcel.setStyle(
                    "-fx-background-color:#166534; -fx-text-fill:white; "
                            + "-fx-font-weight:bold; -fx-padding:8 16; "
                            + "-fx-background-radius:6; -fx-cursor:hand;"));
            btnXuatExcel.setOnMouseReleased(e -> btnXuatExcel.setStyle(baseStyle));
        }

        if (lblFormTitle != null) lblFormTitle.setText("Thông tin gói dịch vụ");
        if (lblMaGoi     != null) lblMaGoi.setText("Mã gói");
        if (lblTenGoi    != null) lblTenGoi.setText("Tên gói");
        if (lblLoaiGoi   != null) lblLoaiGoi.setText("Loại gói");
        if (lblSoGio     != null) lblSoGio.setText("Số giờ");
        if (lblSoNgay    != null) lblSoNgay.setText("Hiệu lực (ngày)");
        if (lblGiaGoc    != null) lblGiaGoc.setText("Giá gốc (VNĐ)");
        if (lblGiaGoi    != null) lblGiaGoi.setText("Giá bán (VNĐ)");
        if (lblTrangThai != null) lblTrangThai.setText("Trạng thái");

        if (btnLuu != null) btnLuu.setText("Lưu");
        if (btnHuy != null) btnHuy.setText("Hủy");

        if (detailMaGoi  != null) detailMaGoi.setPromptText("Tự động");
        if (detailTenGoi != null) detailTenGoi.setPromptText("Nhập tên gói...");
        if (detailSoGio  != null) detailSoGio.setPromptText("Ví dụ: 10");
        if (detailSoNgay != null) detailSoNgay.setPromptText("Ví dụ: 30");
        if (detailGiaGoc != null) detailGiaGoc.setPromptText("Ví dụ: 100000");
        if (detailGiaGoi != null) detailGiaGoi.setPromptText("Ví dụ: 90000");
    }

    private void setupTableColumns() {
        colMaGoi.setCellValueFactory(new PropertyValueFactory<>("magoi"));
        colTenGoi.setCellValueFactory(new PropertyValueFactory<>("tengoi"));
        colLoaiGoi.setCellValueFactory(new PropertyValueFactory<>("loaigoi"));
        colSoGio.setCellValueFactory(new PropertyValueFactory<>("sogio"));
        colSoNgay.setCellValueFactory(new PropertyValueFactory<>("songayhieuluc"));
        colGiaGoc.setCellValueFactory(new PropertyValueFactory<>("giagoc"));
        colGiaGoi.setCellValueFactory(new PropertyValueFactory<>("giagoi"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangthai"));

        setCenterCell(colMaGoi,     v -> v);
        setCenterCell(colTenGoi,    v -> v);
        setCenterCell(colLoaiGoi,   v -> loaiGoiText(v));
        setCenterCellDouble(colSoGio);
        setCenterCellInt(colSoNgay);
        setCenterCellDouble(colGiaGoc);
        setCenterCellDouble(colGiaGoi);
        setCenterCell(colTrangThai, v -> trangThaiText(v));
    }

    private void setupRowColors() {
        tableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(GoiDichVu item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setStyle(""); return; }
                if (isSelected()) {
                    if ("HOATDONG".equals(item.getTrangthai()))
                        setStyle("-fx-background-color:#1b5e20; -fx-text-fill:white;");
                    else
                        setStyle("-fx-background-color:#b71c1c; -fx-text-fill:white;");
                } else {
                    if ("HOATDONG".equals(item.getTrangthai()))
                        setStyle("-fx-background-color:#dcedc8; -fx-text-fill:#1b5e20;");
                    else
                        setStyle("-fx-background-color:#ffcdd2; -fx-text-fill:#b71c1c;");
                }
            }
        });
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, o, n) -> tableView.refresh()
        );
    }

    private void setupComboBoxes() {
        if (cboFilterLoai != null) {
            cboFilterLoai.getItems().setAll("Tất cả", "THEOGIO", "THEONGAY", "THEOTUAN", "THEOTHANG");
            cboFilterLoai.setValue("Tất cả");
            cboFilterLoai.setCellFactory(lv -> new ListCell<>() {
                @Override protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : loaiGoiText(item));
                }
            });
            cboFilterLoai.setButtonCell(new ListCell<>() {
                @Override protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : loaiGoiText(item));
                }
            });
            cboFilterLoai.setOnAction(e -> handleFilterLoai());
        }

        if (detailLoaiGoi != null) {
            detailLoaiGoi.getItems().setAll("THEOGIO", "THEONGAY", "THEOTUAN", "THEOTHANG");
            detailLoaiGoi.setCellFactory(lv -> new ListCell<>() {
                @Override protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : loaiGoiText(item));
                }
            });
            detailLoaiGoi.setButtonCell(new ListCell<>() {
                @Override protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : loaiGoiText(item));
                }
            });
        }

        if (detailTrangThai != null) {
            detailTrangThai.getItems().setAll("HOATDONG", "NGUNG");
            detailTrangThai.setCellFactory(lv -> new ListCell<>() {
                @Override protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : trangThaiText(item));
                }
            });
            detailTrangThai.setButtonCell(new ListCell<>() {
                @Override protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : trangThaiText(item));
                }
            });
        }
    }

    private String loaiGoiText(String code) {
        return switch (code) {
            case "Tất cả"   -> "Tất cả";
            case "THEOGIO"  -> "Theo giờ";
            case "THEONGAY" -> "Theo ngày";
            case "THEOTUAN" -> "Theo tuần";
            case "THEOTHANG"-> "Theo tháng";
            default         -> code;
        };
    }

    private String trangThaiText(String code) {
        return switch (code) {
            case "HOATDONG" -> "Hoạt động";
            case "NGUNG"    -> "Ngừng";
            default         -> code;
        };
    }

    @FXML
    public void loadData() {
        try {
            List<GoiDichVu> data = bus.getAll();
            masterList.setAll(data);
            filteredList = new FilteredList<>(masterList, p -> true);
            tableView.setItems(filteredList);
            if (lblSubtitle != null) lblSubtitle.setText("Tổng cộng: " + data.size() + " gói dịch vụ");
            if (lblTotal    != null) lblTotal.setText("Tổng: " + data.size() + " bản ghi");
            handleSearch();
        } catch (Exception e) {
            showError("Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    @FXML
    public void handleSearch() {
        if (filteredList == null) return;
        String kw   = txtSearch     != null ? txtSearch.getText().toLowerCase().trim() : "";
        String loai = cboFilterLoai != null ? cboFilterLoai.getValue() : "Tất cả";
        filteredList.setPredicate(g -> {
            boolean matchKw = kw.isEmpty()
                    || g.getTengoi().toLowerCase().contains(kw)
                    || g.getMagoi().toLowerCase().contains(kw);
            boolean matchLoai = "Tất cả".equals(loai) || g.getLoaigoi().equals(loai);
            return matchKw && matchLoai;
        });
    }

    @FXML
    public void handleFilterLoai() {
        handleSearch();
    }

    @FXML
    public void handleRowClick() {
        GoiDichVu sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null || formMode == FormMode.THEM) return;
        selectedGoi = sel;
        formMode    = FormMode.SUA;
        fillForm(sel);
        showForm();
        btnLuu.setVisible(true);
        btnHuy.setVisible(true);
        btnXoa.setDisable(false);
        btnKhoiPhuc.setDisable(false);
    }

    @FXML
    public void handleThem() {
        formMode    = FormMode.THEM;
        selectedGoi = null;
        clearForm();
        showForm();
        btnLuu.setVisible(true);
        btnHuy.setVisible(true);
        btnXoa.setDisable(true);
        btnKhoiPhuc.setDisable(true);
        tableView.getSelectionModel().clearSelection();
    }

    @FXML
    public void handleLuuDetail() {
        Stage owner = (Stage) tableView.getScene().getWindow();
        try {
            GoiDichVu dv = buildFromForm();
            if (formMode == FormMode.THEM) {
                bus.themGoiDichVu(dv);
                ThongBaoDialog.showSuccess(owner, "Thêm gói dịch vụ thành công!");
            } else {
                bus.suaGoiDichVu(dv);
                ThongBaoDialog.showSuccess(owner, "Cập nhật thành công!");
            }
            resetForm();
            loadData();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void handleHuyDetail() {
        resetForm();
    }

    @FXML
    public void handleXoa() {
        if (selectedGoi == null) return;
        Stage owner = (Stage) tableView.getScene().getWindow();
        boolean ok = XacNhanDialog.showDelete(owner, selectedGoi.getTengoi());
        if (!ok) return;
        try {
            bus.xoaGoiDichVu(selectedGoi.getMagoi());
            ThongBaoDialog.showSuccess(owner, "Đã ngừng hoạt động gói dịch vụ.");
            resetForm();
            loadData();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void handleKhoiPhuc() {
        if (selectedGoi == null) return;
        Stage owner = (Stage) tableView.getScene().getWindow();
        boolean ok = XacNhanDialog.show(owner, "Khôi phục",
                "Khôi phục gói: " + selectedGoi.getTengoi() + "?");
        if (!ok) return;
        try {
            bus.khoiPhucGDV(selectedGoi.getMagoi());
            ThongBaoDialog.showSuccess(owner, "Khôi phục thành công!");
            resetForm();
            loadData();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void handleLamMoi() {
        if (txtSearch    != null) txtSearch.clear();
        if (cboFilterLoai!= null) cboFilterLoai.setValue("Tất cả");
        resetForm();
        loadData();
    }

    // =====================================================================
    // XUẤT EXCEL
    // Xuất đúng các cột đang hiển thị trên bảng:
    //   Mã gói | Tên gói | Loại gói | Số giờ | Hiệu lực (ngày) | Giá gốc | Giá bán | Trạng thái
    // Dữ liệu lấy từ filteredList (tôn trọng bộ lọc hiện tại).
    // =====================================================================
    @FXML
    public void handleXuatExcel() {
        if (filteredList == null || filteredList.isEmpty()) {
            showError("Không có dữ liệu để xuất!");
            return;
        }

        Stage owner = (Stage) tableView.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu file Excel");
        fileChooser.setInitialFileName("DanhSachGoiDichVu.xlsx");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files (*.xlsx)", "*.xlsx"));
        File file = fileChooser.showSaveDialog(owner);
        if (file == null) return;

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh sach goi dich vu");

            // --- Style: Header ---
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // --- Style: Data ---
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // --- Style: Số (căn phải) ---
            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.cloneStyleFrom(dataStyle);
            numberStyle.setAlignment(HorizontalAlignment.RIGHT);

            // --- Hàng tiêu đề (khớp đúng thứ tự cột trên bảng) ---
            String[] headers = {
                    "Mã gói", "Tên gói", "Loại gói",
                    "Số giờ", "Hiệu lực (ngày)",
                    "Giá gốc (VNĐ)", "Giá bán (VNĐ)", "Trạng thái"
            };
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // --- Hàng dữ liệu ---
            int rowIdx = 1;
            for (GoiDichVu g : filteredList) {
                Row row = sheet.createRow(rowIdx++);

                Cell c0 = row.createCell(0); c0.setCellValue(g.getMagoi());           c0.setCellStyle(dataStyle);
                Cell c1 = row.createCell(1); c1.setCellValue(g.getTengoi());          c1.setCellStyle(dataStyle);
                // Hiển thị loại gói bằng tiếng Việt như trên bảng
                Cell c2 = row.createCell(2); c2.setCellValue(loaiGoiText(g.getLoaigoi())); c2.setCellStyle(dataStyle);
                Cell c3 = row.createCell(3); c3.setCellValue(g.getSogio());           c3.setCellStyle(numberStyle);
                Cell c4 = row.createCell(4); c4.setCellValue(g.getSongayhieuluc());   c4.setCellStyle(numberStyle);
                Cell c5 = row.createCell(5); c5.setCellValue(g.getGiagoc());          c5.setCellStyle(numberStyle);
                Cell c6 = row.createCell(6); c6.setCellValue(g.getGiagoi());          c6.setCellStyle(numberStyle);
                // Hiển thị trạng thái bằng tiếng Việt như trên bảng
                Cell c7 = row.createCell(7); c7.setCellValue(trangThaiText(g.getTrangthai())); c7.setCellStyle(dataStyle);
            }

            // --- Tự động căn độ rộng cột ---
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // --- Ghi file ---
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }

            ThongBaoDialog.showSuccess(owner, "Xuất Excel thành công!\n" + file.getAbsolutePath());

        } catch (Exception e) {
            showError("Xuất Excel thất bại: " + e.getMessage());
        }
    }

    // === HELPERS ===

    private void fillForm(GoiDichVu g) {
        if (detailMaGoi    != null) detailMaGoi.setText(g.getMagoi());
        if (detailTenGoi   != null) detailTenGoi.setText(g.getTengoi());
        if (detailLoaiGoi  != null) detailLoaiGoi.setValue(g.getLoaigoi());
        if (detailSoGio    != null) detailSoGio.setText(String.valueOf(g.getSogio()));
        if (detailSoNgay   != null) detailSoNgay.setText(String.valueOf(g.getSongayhieuluc()));
        if (detailGiaGoc   != null) detailGiaGoc.setText(String.valueOf(g.getGiagoc()));
        if (detailGiaGoi   != null) detailGiaGoi.setText(String.valueOf(g.getGiagoi()));
        if (detailTrangThai!= null) detailTrangThai.setValue(g.getTrangthai());
    }

    private void clearForm() {
        if (detailMaGoi    != null) detailMaGoi.clear();
        if (detailTenGoi   != null) detailTenGoi.clear();
        if (detailLoaiGoi  != null) detailLoaiGoi.setValue(null);
        if (detailSoGio    != null) detailSoGio.clear();
        if (detailSoNgay   != null) detailSoNgay.clear();
        if (detailGiaGoc   != null) detailGiaGoc.clear();
        if (detailGiaGoi   != null) detailGiaGoi.clear();
        if (detailTrangThai!= null) detailTrangThai.setValue(null);
    }

    private GoiDichVu buildFromForm() throws Exception {
        String ten  = detailTenGoi   != null ? detailTenGoi.getText().trim()   : "";
        String loai = detailLoaiGoi  != null ? detailLoaiGoi.getValue()        : null;
        String tt   = detailTrangThai!= null ? detailTrangThai.getValue()      : null;
        double soGio, giaGoc, giaGoi;
        int    soNgay;
        try { soGio  = Double.parseDouble(detailSoGio  != null ? detailSoGio.getText().trim()  : "0"); }
        catch (NumberFormatException e) { throw new Exception("Số giờ phải là số hợp lệ!"); }
        try { soNgay = Integer.parseInt(detailSoNgay   != null ? detailSoNgay.getText().trim() : "0"); }
        catch (NumberFormatException e) { throw new Exception("Số ngày hiệu lực phải là số nguyên!"); }
        try { giaGoc = Double.parseDouble(detailGiaGoc != null ? detailGiaGoc.getText().trim() : "0"); }
        catch (NumberFormatException e) { throw new Exception("Giá gốc phải là số hợp lệ!"); }
        try { giaGoi = Double.parseDouble(detailGiaGoi != null ? detailGiaGoi.getText().trim() : "0"); }
        catch (NumberFormatException e) { throw new Exception("Giá bán phải là số hợp lệ!"); }

        String maDV = (formMode == FormMode.SUA && selectedGoi != null) ? selectedGoi.getMagoi() : "";
        GoiDichVu g = new GoiDichVu();
        g.setMagoi(maDV);
        g.setTengoi(ten);
        g.setLoaigoi(loai);
        g.setSogio(soGio);
        g.setSongayhieuluc(soNgay);
        g.setGiagoc(giaGoc);
        g.setGiagoi(giaGoi);
        g.setTrangthai(tt != null ? tt : "HOATDONG");
        return g;
    }

    private void setCenterCell(TableColumn<GoiDichVu, String> col,
                               java.util.function.Function<String, String> mapper) {
        col.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) setText(null);
                else { setText(mapper.apply(v)); setStyle("-fx-alignment:CENTER;"); }
            }
        });
    }

    private void setCenterCellDouble(TableColumn<GoiDichVu, Double> col) {
        col.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) setText(null);
                else { setText(String.format("%,.0f", v)); setStyle("-fx-alignment:CENTER;"); }
            }
        });
    }

    private void setCenterCellInt(TableColumn<GoiDichVu, Integer> col) {
        col.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Integer v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) setText(null);
                else { setText(String.valueOf(v)); setStyle("-fx-alignment:CENTER;"); }
            }
        });
    }

    private void showForm() {
        if (paneDetail != null) { paneDetail.setVisible(true);  paneDetail.setManaged(true); }
    }

    private void hideForm() {
        if (paneDetail != null) { paneDetail.setVisible(false); paneDetail.setManaged(false); }
    }

    private void resetForm() {
        formMode    = FormMode.NONE;
        selectedGoi = null;
        clearForm();
        hideForm();
        if (btnLuu      != null) btnLuu.setVisible(false);
        if (btnHuy      != null) btnHuy.setVisible(false);
        if (btnXoa      != null) btnXoa.setDisable(true);
        if (btnKhoiPhuc != null) btnKhoiPhuc.setDisable(true);
        tableView.getSelectionModel().clearSelection();
    }

    private void showError(String msg) {
        Stage owner = tableView != null && tableView.getScene() != null
                ? (Stage) tableView.getScene().getWindow() : null;
        ThongBaoDialog.showError(owner, msg);
    }
}