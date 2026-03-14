package test.gui;

import entity.NhanVien;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.SessionManager;

public class KhuMayTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Giả lập đăng nhập QUANLY để có đủ quyền test
            NhanVien quanLy = new NhanVien();
            quanLy.setManv("NV001");
            quanLy.setTen("Nguyễn Văn A");
            quanLy.setChucvu("QUANLY");
            SessionManager.setCurrentUser(quanLy);

            // 2. Tải file giao diện
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/khuMay.fxml"));
            Parent root = loader.load();

            // 3. Gắn giao diện vào Scene
            Scene scene = new Scene(root);

            // 4. Cấu hình và hiển thị Stage
            primaryStage.setTitle("Hệ thống Test - Quản lý Khu Máy");
            primaryStage.setWidth(900);
            primaryStage.setHeight(600);
            primaryStage.setScene(scene);
            primaryStage.show();

            System.out.println("╔══════════════════════════════════════════════════╗");
            System.out.println("║     TEST KhuMayController - GIAO DIỆN           ║");
            System.out.println("╠══════════════════════════════════════════════════╣");
            System.out.println("║  Đăng nhập: QUANLY (NV001)                      ║");
            System.out.println("║  Giao diện đã mở → Test thủ công theo checklist ║");
            System.out.println("╚══════════════════════════════════════════════════╝");

        } catch (Exception e) {
            System.err.println("Không thể khởi động bản demo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}