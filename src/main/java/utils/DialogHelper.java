package utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * Tiện ích mở dialog FXML theo chuẩn: UNDECORATED + APPLICATION_MODAL.
 */
public class DialogHelper {

    /**
     * Mở dialog và trả về controller để caller có thể set data.
     *
     * @param fxmlPath  e.g. "/fxml/dialogs/themDichVu.fxml"
     * @param owner     stage cha (lấy từ anyNode.getScene().getWindow())
     * @return controller instance hoặc null nếu lỗi
     */
    public static <T> T openDialog(String fxmlPath, Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogHelper.class.getResource(fxmlPath));
            Parent root = loader.load();
            T controller = loader.getController();

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            if (owner != null) stage.initOwner(owner);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            return controller;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Mở dialog có tiêu đề, style mặc định.
     */
    public static <T> T openDialogWithTitle(String fxmlPath, Window owner, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogHelper.class.getResource(fxmlPath));
            Parent root = loader.load();
            T controller = loader.getController();

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            if (owner != null) stage.initOwner(owner);
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            return controller;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
