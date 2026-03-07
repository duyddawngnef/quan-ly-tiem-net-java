package gui.dialog;

import bus.DichVuBUS;
import bus.SuDungDichVuBUS;
import entity.DichVu;
import entity.SuDungDichVu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ChonDichVuDialog implements Initializable {

    @FXML
    private Label lblPhienInfo;
    @FXML
    private TextField txtSearch;
    @FXML
    private TableView<DichVu> tableDichVu;
    @FXML
    private TableColumn<DichVu, String> colMaDV;
    @FXML
    private TableColumn<DichVu, String> colTenDV;
    @FXML
    private TableColumn<DichVu, Double> colGia;
    @FXML
    private TableColumn<DichVu, Integer> colTonKho;

    @FXML
    private Label lblSelectedDV;
    @FXML
    private TextField txtSoLuong;

    @FXML
    private TableView<CartItem> tableCart;
    @FXML
    private TableColumn<CartItem, String> colCartDV;
    @FXML
    private TableColumn<CartItem, Integer> colCartSL;
    @FXML
    private TableColumn<CartItem, String> colCartDonGia;
    @FXML
    private TableColumn<CartItem, String> colCartThanhTien;

    @FXML
    private Label lblTongTienCart;
    @FXML
    private Label lblError;
    @FXML
    private Button btnXacNhan;

    private final DichVuBUS dichVuBUS = new DichVuBUS();
    private final SuDungDichVuBUS suDungDVBUS = new SuDungDichVuBUS();

    private ObservableList<DichVu> dichVuList = FXCollections.observableArrayList();
    private ObservableList<CartItem> cartList = FXCollections.observableArrayList();
    private DichVu selectedDV;
    private String maPhien;
    private Runnable onOrderCallback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        tableCart.setItems(cartList);
        if (txtSoLuong != null)
            txtSoLuong.setText("1");
        loadDichVu();
        setupTableSelection();
    }

    public void setPhien(String maPhien, String phienInfo) {
        this.maPhien = maPhien;
        if (lblPhienInfo != null)
            lblPhienInfo.setText("Phiên: " + phienInfo);
    }

    public void setOnOrderCallback(Runnable cb) {
        this.onOrderCallback = cb;
    }

    private void setupTableColumns() {
        if (colMaDV != null)
            colMaDV.setCellValueFactory(new PropertyValueFactory<>("maDV"));
        if (colTenDV != null)
            colTenDV.setCellValueFactory(new PropertyValueFactory<>("tenDV"));
        if (colGia != null) {
            colGia.setCellValueFactory(new PropertyValueFactory<>("donGia"));
            colGia.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Double v, boolean empty) {
                    super.updateItem(v, empty);
                    setText(empty || v == null ? null : String.format("%,.0f ₫", v));
                }
            });
        }
        if (colTonKho != null)
            colTonKho.setCellValueFactory(new PropertyValueFactory<>("soLuongTon"));

        // Cart columns
        if (colCartDV != null)
            colCartDV.setCellValueFactory(new PropertyValueFactory<>("tenDV"));
        if (colCartSL != null)
            colCartSL.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        if (colCartDonGia != null)
            colCartDonGia.setCellValueFactory(new PropertyValueFactory<>("donGiaFormatted"));
        if (colCartThanhTien != null)
            colCartThanhTien.setCellValueFactory(new PropertyValueFactory<>("thanhTienFormatted"));
    }

    private void loadDichVu() {
        try {
            List<DichVu> list = dichVuBUS.getDanhSachDV();
            dichVuList.setAll(list);
            tableDichVu.setItems(dichVuList);
        } catch (Exception ignored) {
        }
    }

    private void setupTableSelection() {
        tableDichVu.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            selectedDV = n;
            if (lblSelectedDV != null)
                lblSelectedDV.setText(
                        n != null ? n.getTenDV() + " (" + String.format("%,.0f ₫", n.getDonGia()) + ")" : "Chưa chọn");
        });
    }

    @FXML
    public void handleSearch() {
        String kw = txtSearch != null ? txtSearch.getText().toLowerCase() : "";
        tableDichVu.setItems(dichVuList.filtered(d -> kw.isEmpty() || d.getTenDV().toLowerCase().contains(kw)));
    }

    @FXML
    public void handleIncrease() {
        if (txtSoLuong == null)
            return;
        try {
            txtSoLuong.setText(String.valueOf(Integer.parseInt(txtSoLuong.getText()) + 1));
        } catch (NumberFormatException e) {
            txtSoLuong.setText("1");
        }
    }

    @FXML
    public void handleDecrease() {
        if (txtSoLuong == null)
            return;
        try {
            int qty = Integer.parseInt(txtSoLuong.getText()) - 1;
            txtSoLuong.setText(String.valueOf(Math.max(1, qty)));
        } catch (NumberFormatException e) {
            txtSoLuong.setText("1");
        }
    }

    @FXML
    public void handleAddToCart() {
        if (selectedDV == null) {
            setError("Vui lòng chọn dịch vụ");
            return;
        }
        int qty;
        try {
            qty = Integer.parseInt(txtSoLuong != null ? txtSoLuong.getText() : "1");
        } catch (NumberFormatException e) {
            qty = 1;
        }
        if (qty <= 0) {
            setError("Số lượng phải lớn hơn 0");
            return;
        }

        final int finalQty = qty;
        final DichVu dv = selectedDV;

        cartList.stream()
                .filter(item -> item.getMaDV().equals(dv.getMaDV()))
                .findFirst()
                .ifPresentOrElse(
                        item -> item.setSoLuong(item.getSoLuong() + finalQty),
                        () -> cartList.add(new CartItem(dv, finalQty)));
        tableCart.refresh();
        updateCartTotal();
        clearError();
    }

    @FXML
    public void handleRemoveFromCart() {
        CartItem selected = tableCart.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cartList.remove(selected);
            updateCartTotal();
        }
    }

    @FXML
    public void handleXacNhan() {
        if (cartList.isEmpty()) {
            setError("Giỏ hàng trống");
            return;
        }
        if (maPhien == null || maPhien.isEmpty()) {
            setError("Chưa có thông tin phiên");
            return;
        }

        try {
            for (CartItem item : cartList) {
                suDungDVBUS.orderDichVu(maPhien, item.getMaDV(), item.getSoLuong());
            }
            if (onOrderCallback != null)
                onOrderCallback.run();
            closeDialog();
        } catch (Exception e) {
            setError("Lỗi đặt dịch vụ: " + e.getMessage());
        }
    }

    @FXML
    public void handleCancel() {
        closeDialog();
    }

    private void updateCartTotal() {
        double total = cartList.stream().mapToDouble(CartItem::getThanhTien).sum();
        if (lblTongTienCart != null)
            lblTongTienCart.setText(String.format("%,.0f ₫", total));
    }

    private void setError(String msg) {
        if (lblError != null)
            lblError.setText(msg);
    }

    private void clearError() {
        if (lblError != null)
            lblError.setText("");
    }

    private void closeDialog() {
        ((Stage) btnXacNhan.getScene().getWindow()).close();
    }

    // ===== INNER CLASS CART ITEM =====

    public static class CartItem {
        private final DichVu dichVu;
        private int soLuong;

        public CartItem(DichVu dv, int qty) {
            this.dichVu = dv;
            this.soLuong = qty;
        }

        public String getMaDV() {
            return dichVu.getMaDV();
        }

        public String getTenDV() {
            return dichVu.getTenDV();
        }

        public int getSoLuong() {
            return soLuong;
        }

        public void setSoLuong(int q) {
            this.soLuong = q;
        }

        public double getDonGia() {
            return dichVu.getDonGia();
        }

        public double getThanhTien() {
            return dichVu.getDonGia() * soLuong;
        }

        public String getDonGiaFormatted() {
            return String.format("%,.0f ₫", getDonGia());
        }

        public String getThanhTienFormatted() {
            return String.format("%,.0f ₫", getThanhTien());
        }
    }
}
