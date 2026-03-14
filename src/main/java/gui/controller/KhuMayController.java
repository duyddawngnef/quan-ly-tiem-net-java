package gui.controller;

import bus.KhuMayBUS;
import entity.KhuMay;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.ThongBaoDialogHelper;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class KhuMayController implements Initializable {

    @FXML private TableView<KhuMay> tableView;
    @FXML private TableColumn<KhuMay, String>  colMaKhu;        // ← SỬA khớp FXML
    @FXML private TableColumn<KhuMay, String>  colTenKhu;       // ← SỬA khớp FXML
    @FXML private TableColumn<KhuMay, String>  colSoMay;        // ← SỬA khớp FXML (hiển thị số máy)
    @FXML private TableColumn<KhuMay, String>  colMoTa;         // ← SỬA khớp FXML
    @FXML private TableColumn<KhuMay, String>  colTrangThai;

    @FXML private TextField txtSearch;
    @FXML private Label lblSubtitle;
    @FXML private Label lblTotal;
    @FXML private Label lblSoMay;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;

    private final KhuMayBUS khuMayBUS = new KhuMayBUS();
    private ObservableList<KhuMay> dataList = FXCollections.observableArrayList();
    private FilteredList<KhuMay> filteredList;
    private KhuMay selectedItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupTableSelection();
        loadData();
    }

    private void setupTableColumns() {
        if (colMaKhu    != null) colMaKhu.setCellValueFactory(new PropertyValueFactory<>("makhu"));
        if (colTenKhu   != null) colTenKhu.setCellValueFactory(new PropertyValueFactory<>("tenkhu"));
        if (colTrangThai != null) colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangthai"));

        // Cột Số máy: hiển thị số máy trong khu (gọi BUS đếm)
        if (colSoMay != null) {
            colSoMay.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                        setText(null);
                    } else {
                        KhuMay km = getTableRow().getItem();
                        try {
                            int soMay = khuMayBUS.demSoMayTrongKhu(km.getMakhu());
                            setText(soMay + " máy");
                        } catch (Exception e) {
                            setText("N/A");
                        }
                    }
                }
            });
        }

        // Cột Mô tả: hiển thị giá cơ sở + số máy tối đa
        if (colMoTa != null) {
            colMoTa.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                        setText(null);
                    } else {
                        KhuMay km = getTableRow().getItem();
                        setText(String.format("Giá: %,.0f ₫/giờ | Tối đa: %d máy",
                                km.getGiacoso(), km.getSomaytoida()));
                    }
                }
            });
        }
    }

    private void setupTableSelection() {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            selectedItem = n;
            boolean has = n != null;
            if (btnSua != null) btnSua.setDisable(!has);
            if (btnXoa != null) btnXoa.setDisable(!has);

            // Gọi onRowSelected khi chọn dòng
            onRowSelected(n);
        });
        if (btnSua != null) btnSua.setDisable(true);
        if (btnXoa != null) btnXoa.setDisable(true);
    }

    /**
     * Xử lý khi chọn 1 dòng trong bảng
     * Gọi KhuMayBUS.demSoMayTrongKhu(maKhu) để hiển thị số máy
     */
    private void onRowSelected(KhuMay khuMay) {
        if (lblSoMay == null) return;

        if (khuMay == null) {
            lblSoMay.setText("");
            return;
        }

        try {
            int soMay = khuMayBUS.demSoMayTrongKhu(khuMay.getMakhu());
            lblSoMay.setText("📊 Khu " + khuMay.getTenkhu() + ": " + soMay + " máy");
        } catch (Exception e) {
            lblSoMay.setText("⚠️ Không thể đếm số máy: " + e.getMessage());
        }
    }

    public void loadData() {
        try {
            List<KhuMay> list = khuMayBUS.getAllKhuMay();
            dataList.setAll(list);
            filteredList = new FilteredList<>(dataList, p -> true);
            tableView.setItems(filteredList);
            updateSubtitle();
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tableView.getScene(), "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    @FXML
    public void handleSearch() {
        String keyword = txtSearch != null ? txtSearch.getText().toLowerCase().trim() : "";
        if (filteredList == null) return;
        filteredList.setPredicate(item -> keyword.isEmpty()
                || (item.getMakhu()   != null && item.getMakhu().toLowerCase().contains(keyword))
                || (item.getTenkhu()  != null && item.getTenkhu().toLowerCase().contains(keyword)));
        updateSubtitle();
    }

    @FXML
    public void handleThem() { openDialog(null); }

    @FXML
    public void handleSua() {
        if (selectedItem == null) return;
        openDialog(selectedItem);
    }

    @FXML
    public void handleXoa() {
        if (selectedItem == null) return;
        Stage owner = (Stage) tableView.getScene().getWindow();
        if (!gui.dialog.XacNhanDialog.showDelete(owner, selectedItem.getTenkhu())) return;
        try {
            khuMayBUS.xoaKhuMay(selectedItem.getMakhu());
            ThongBaoDialogHelper.showSuccess(tableView.getScene(), "Đã xóa khu máy!");
            loadData();
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tableView.getScene(), "Lỗi xóa: " + e.getMessage());
        }
    }

    @FXML
    public void handleLamMoi() {
        if (txtSearch != null) txtSearch.clear();
        loadData();
    }

    private void openDialog(KhuMay entity) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/themKhuMay.fxml"));
            Parent root = loader.load();
            Object ctrl = loader.getController();
            try {
                ctrl.getClass().getMethod("setEntity", Object.class).invoke(ctrl, entity);
                ctrl.getClass().getMethod("setOnSaveCallback", Runnable.class).invoke(ctrl, (Runnable) this::loadData);
            } catch (NoSuchMethodException ignored) {}

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tableView.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tableView.getScene(), "Không thể mở dialog: " + e.getMessage());
        }
    }

    private void updateSubtitle() {
        int total = filteredList != null ? filteredList.size() : 0;
        if (lblSubtitle != null) lblSubtitle.setText("Tổng: " + total + " bản ghi");
        if (lblTotal    != null) lblTotal.setText("Tổng: " + total + " bản ghi");
    }
}