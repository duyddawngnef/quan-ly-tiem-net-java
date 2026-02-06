package gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.May;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SoDoMayController {

    @FXML
    private GridPane gridMay;

    @FXML
    private ComboBox<String> comboKhuMay;

    private List<May> danhSachMay = new ArrayList<>();

    @FXML
    public void initialize() {
        fakeData();
        loadSoDoMay();
        autoRefresh();
    }

    private void fakeData() {
        for (int i = 1; i <= 20; i++) {
            if (i % 3 == 0)
                danhSachMay.add(new May(i, "May " + i, "TRONG"));
            else if (i % 3 == 1)
                danhSachMay.add(new May(i, "May " + i, "DANGDUNG"));
            else
                danhSachMay.add(new May(i, "May " + i, "BAOTRI"));
        }
    }

    private void loadSoDoMay() {
        gridMay.getChildren().clear();

        int cols = 5;

        for (int i = 0; i < danhSachMay.size(); i++) {
            May may = danhSachMay.get(i);

            Button btn = new Button(may.getTenMay());
            btn.setPrefSize(100, 60);

            setColor(btn, may.getTrangThai());
            setupTooltip(btn, may);

            btn.setOnAction(e -> handleClickMay(may));

            gridMay.add(btn, i % cols, i / cols);
        }
    }

    private void setColor(Button btn, String trangThai) {
        switch (trangThai) {
            case "TRONG":
                btn.setStyle("-fx-background-color: #4CAF50;");
                break;
            case "DANGDUNG":
                btn.setStyle("-fx-background-color: #F44336;");
                break;
            case "BAOTRI":
                btn.setStyle("-fx-background-color: #9E9E9E;");
                break;
        }
    }

    private void setupTooltip(Button btn, May may) {
        Tooltip tooltip = new Tooltip(
                may.getTenMay() + "\nTrang thai: " + may.getTrangThai()
        );
        Tooltip.install(btn, tooltip);
    }

    private void handleClickMay(May may) {
        switch (may.getTrangThai()) {

            case "TRONG":
                showDialog("Mo phien cho " + may.getTenMay());
                may.setTrangThai("DANGDUNG");
                break;

            case "DANGDUNG":
                showDialog("Xem / dong phien " + may.getTenMay());
                may.setTrangThai("TRONG");
                break;

            case "BAOTRI":
                showDialog("May dang bao tri");
                break;
        }

        loadSoDoMay();
    }

    private void showDialog(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void autoRefresh() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                javafx.application.Platform.runLater(() -> loadSoDoMay());
            }
        }, 30000, 30000);
    }
}
