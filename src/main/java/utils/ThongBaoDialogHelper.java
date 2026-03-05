package utils;

import gui.dialog.ThongBaoDialog;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Wrapper tiện ích giúp các controller gọi ThongBaoDialog
 * mà không cần tự lấy Stage từ Scene.
 */
public class ThongBaoDialogHelper {

    public static void showSuccess(Scene scene, String message) {
        if (scene == null) return;
        ThongBaoDialog.showSuccess((Stage) scene.getWindow(), message);
    }

    public static void showError(Scene scene, String message) {
        if (scene == null) return;
        ThongBaoDialog.showError((Stage) scene.getWindow(), message);
    }

    public static void showWarning(Scene scene, String message) {
        if (scene == null) return;
        ThongBaoDialog.showWarning((Stage) scene.getWindow(), message);
    }

    public static void showInfo(Scene scene, String message) {
        if (scene == null) return;
        ThongBaoDialog.showInfo((Stage) scene.getWindow(), message);
    }
}
