import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

// Main App Class
public class App extends Application {

    Stage mainStage;
    Scene mainScene;
    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;
        Button playButton = new Button("Enter Garden");
        Button helpButton = new Button("Help");
        Button exitButton = new Button("Exit Application");
        Button infoButton = new Button("Plant Information");
        Label editionLabel = new Label("Sacremento Valley Edition");
        Image gardenScapeTitle = new Image("file:src\\gardenscapetitle.png"); // Title image
        ImageView gardenScapeView = new ImageView(gardenScapeTitle); // View for title image

        editionLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 5px; -fx-border-color: black;");

        Image gameBackground = new Image("file:src\\field.jpeg"); // Background image
        ImageView backgroundView = new ImageView(gameBackground);
        backgroundView.setFitWidth(screenBounds.getWidth());
        backgroundView.setPreserveRatio(true);
        backgroundView.setSmooth(true);

        // Main screen buttons
        playButton.setOnAction(event -> {
            GameScreen gameScreen = new GameScreen(mainScene);
            gameScreen.start(mainStage);
        });

        helpButton.setOnAction(event -> {
            SettingsScreen settingsScreen = new SettingsScreen(mainScene);
            settingsScreen.start(mainStage);
        });

        exitButton.setOnAction(event -> {
            Platform.exit();
        });

        infoButton.setOnAction(event -> {
            PlantInfo infoScene = new PlantInfo(mainScene);
            infoScene.start(mainStage);
        });

        StackPane root = new StackPane();
        VBox layout1 = new VBox(10);
        layout1.getChildren().addAll(gardenScapeView, editionLabel, playButton, helpButton, infoButton, exitButton);
        layout1.setStyle("-fx-alignment: CENTER;");
        root.getChildren().addAll(backgroundView, layout1);

        mainScene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());

        mainStage.setScene(mainScene);
        mainStage.setMaximized(true);
        mainStage.setTitle("GardenScape");
        mainStage.show();
    }
}
