
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import utils.SessionManager;
import entity.NhanVien;


import java.io.InputStream;
import java.util.Objects;

/**
 * Main.java - Điểm khởi chạy ứng dụng Quản Lý Tiệm Internet
 *
 * Đặt tại: src/main/java/com/quanlytiemnet/Main.java
 *
 * Chạy bằng Maven : mvn javafx:run
 * Chạy bằng IDE   : set Main Class = com.quanlytiemnet.Main
 */
public class Main extends Application {

    private static final double LOGIN_WIDTH  = 1000;
    private static final double LOGIN_HEIGHT = 800;
    private static final String APP_TITLE    = "Quản Lý Tiệm Internet";

    @Override
    public void start(Stage primaryStage) {
        try {
            Font.loadFont(getClass().getResourceAsStream("/fonts/MaterialIcons-Regular.ttf"), 14);
            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(
                            getClass().getResource("/fxml/login.fxml"),
                            "Không tìm thấy /fxml/hoadon.fxml"
                    )
            );

            Scene scene = new Scene(root, LOGIN_WIDTH, LOGIN_HEIGHT);

            primaryStage.setScene(scene);
            primaryStage.setTitle(APP_TITLE);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();


            // Đăng xuất session khi đóng cửa sổ
            primaryStage.setOnCloseRequest(e -> SessionManager.clearSession());

            primaryStage.show();

        } catch (Exception e) {
            System.err.println("[Main] Lỗi khởi động: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        SessionManager.clearSession();
    }

    /**
     * Gọi launch() từ method riêng để tránh lỗi
     * "JavaFX runtime components are missing" khi chạy từ IDE.
     */
    public static void main(String[] args) {
        launch(args);
    }
}