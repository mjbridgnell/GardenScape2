import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class SettingsScreen {
    private Scene mainScene;
    private Scene settingsScene;
    Button backButton = new Button("Back to Main Menu");
    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
    Label helpInfo = new Label("Controls:\n" +
        "Escape - Show settings menu\n" +
        "E - Show item menu\n" +
        "WASD - Move around\n" +
        "C - Cancel placing plant");
    public SettingsScreen(Scene mainScene) {
        this.mainScene = mainScene;
    }

    public void start(Stage primaryStage) {
        backButton.setOnAction(event -> {
            primaryStage.setScene(mainScene);
        });
        VBox root = new VBox();
        root.getChildren().addAll(helpInfo, backButton);
        settingsScene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());

        primaryStage.setScene(settingsScene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
}