package test.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.SessionManager;

public class LoginTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Đảm bảo xóa sạch phiên đăng nhập cũ (nếu có) trước khi test
            SessionManager.clearSession();

            // 1. Tải file giao diện từ thư mục resources
            // Chú ý: Cập nhật lại đường dẫn này nếu file fxml của bạn nằm ở thư mục khác
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            // 2. Gắn giao diện vào một "Cảnh" (Scene)
            Scene scene = new Scene(root);

            // 3. Cấu hình và hiển thị "Sân khấu" (Stage)
            primaryStage.setTitle("Hệ thống Test - Đăng nhập");
            primaryStage.setScene(scene);

            // Có thể cố định kích thước màn hình đăng nhập nếu không muốn user phóng to
            primaryStage.setResizable(false);

            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Không thể khởi động màn hình Đăng Nhập: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Kích hoạt môi trường JavaFX
        launch(args);
    }
}