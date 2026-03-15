import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class PlantInfo {

    private Scene mainScene;
    private Scene infoScene;
    Button backButton = new Button("Back to Main Menu");
    Button playButton = new Button("Back to Garden");
    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
    Label plantInfoLabel = new Label();
    ImageView spriteImageView = new ImageView();
    List<PlantDes> plants = new ArrayList<>();
    Image gameBackground = new Image("file:src\\field.jpeg");
    ImageView backgroundView = new ImageView(gameBackground);
    HBox spriteMenu = new HBox(10); // Holds sprites
    VBox menuBox = new VBox(20); // Holds spriteMenu
    VBox infoBox = new VBox(10); // Holds plant info
    StackPane root = new StackPane(); // Root layout

    // Add plants here
    public PlantInfo(Scene mainScene) {
        this.mainScene = mainScene;

        plants.add(new PlantDes("Corn",
                "Plant March to September"
                        + "\n90 days to grow"
                        + "\nWater 2 times a week, 1in"
                        + "\n4 seeds per square foot",
                "Plants/PlantSprites/matureCorn.png"));
        plants.add(new PlantDes("Grass",
                "Decorative"
                        + "\nMow every 7-14 days"
                        + "\nWater several times a week, 1-2in",
                "Plants/PlantSprites/grassTile.png"));
        plants.add(new PlantDes("Spinach",
                "Plant January to March or October to November"
                        + "\n30 days to grow"
                        + "\nWater several times a week, 1-1.5in"
                        + "\n9 seeds per square foot",
                "Plants/PlantSprites/matureSpinach.png"));
        plants.add(new PlantDes("Cucumber",
                "Plant March to September"
                        + "\n50 days to grow"
                        + "\nWater several times a week, 1-2in"
                        + "\n1 seed per square foot",
                "Plants/PlantSprites/matureCucumber.png"));
        plants.add(new PlantDes("Radish",
                "Plant February or October"
                        + "\n25 days to grow"
                        + "\nWater 2 times a week, 1in"
                        + "\n16 seeds per square foot",
                "Plants/PlantSprites/matureRadish.png"));
    }

    private void updatePlantDisplay(PlantDes plant) {
        plantInfoLabel.setText(plant.getName() + "\n" + plant.getDescription());
        plantInfoLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 5px; -fx-border-color: black;");
        Image spriteImage = new Image(plant.getSpritePath());
        spriteImageView.setImage(spriteImage);

        spriteImageView.setFitWidth(200);
        spriteImageView.setPreserveRatio(true);
    }

    public void start(Stage primaryStage) {
        backButton.setOnAction(event -> {
            primaryStage.setScene(mainScene);
        });

        playButton.setOnAction(event -> {
            GameScreen gameScreen = new GameScreen(mainScene);
            gameScreen.start(primaryStage);
        });

        // Settings for background image
        backgroundView.setFitWidth(screenBounds.getWidth());
        backgroundView.setPreserveRatio(true);
        backgroundView.setSmooth(true);

        spriteMenu.setAlignment(Pos.CENTER);

        // Loop over the plant data and create clickable ImageViews for each plant
        for (PlantDes plant : plants) {
            ImageView plantSprite = new ImageView(new Image(plant.getSpritePath()));
            plantSprite.setFitWidth(100);
            plantSprite.setPreserveRatio(true);

            plantSprite.setOnMouseClicked(event -> {
                updatePlantDisplay(plant);
            });

            spriteMenu.getChildren().add(plantSprite);
        }

        menuBox.setAlignment(Pos.TOP_CENTER);
        menuBox.getChildren().add(spriteMenu);

        infoBox.setAlignment(Pos.CENTER);
        infoBox.getChildren().addAll(menuBox, spriteImageView, plantInfoLabel, playButton, backButton);

        root.getChildren().addAll(backgroundView, infoBox);

        infoScene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
        primaryStage.setScene(infoScene);
        primaryStage.setMaximized(true);
        primaryStage.show();

        updatePlantDisplay(plants.get(0)); // Display the first plant initially
    }
}

// Class to represent a plant with name, description, and image path
class PlantDes {
    private String name;
    private String description;
    private String spritePath;

    public PlantDes(String name, String description, String spritePath) {
        this.name = name;
        this.description = description;
        this.spritePath = spritePath;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSpritePath() {
        return spritePath;
    }
}
