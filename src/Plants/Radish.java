package Plants;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

// 30 days to grow
// Water several times a week, 1-1.5in
// Plant Jan-Mar, Oct-Nov
// 9 seeds per square foot

public class Radish {
    private long growTime = Global.getGrowTime("radish"); // seconds
    private double percentGrown = 0;
    private int squareSize;
    public ImageView tileImage;
    public Rectangle highlight;
    public boolean harvestable = false;
    private long secondsElapsed;
    private int col;
    private int row;
    private long timestamp;
    private long currentTime;
    private Label radishInfoLabel = new Label("Radish Age: 0"
            + "\nPercent Grown: 0%"
            + "\n25 days to grow"
            + "\nPlant Feb or Sep"
            + "\nWater 2 times a week, 1in"
            + "\n16 Seeds/Plants per square foot");

    private void addHighlightEffect(StackPane tileContainer, Rectangle highlight, Scene gameScene, Radish radish) {
        radish.setInfoLabelVisible(false);

        Label harvestPopup = new Label("16 Radish Plants Harvested!");
        harvestPopup.setStyle("-fx-background-color: lightgreen; -fx-padding: 5px; -fx-border-color: black;");
        harvestPopup.setVisible(false); // Initially hidden

        tileContainer.getChildren().add(harvestPopup);
        ObservableList<Node> rootChildren = ((StackPane) gameScene.getRoot()).getChildren();
        if (!rootChildren.contains(radish.radishInfoLabel)) {
            rootChildren.add(radish.radishInfoLabel);
        }
        if (!rootChildren.contains(harvestPopup)) {
            rootChildren.add(harvestPopup);
        }

        // Set initial positions
        updatePopupPositions(gameScene, radish, harvestPopup);

        // Add a listener to adjust the popups when the window is resized
        gameScene.widthProperty()
                .addListener((obs, oldVal, newVal) -> updatePopupPositions(gameScene, radish, harvestPopup));
        gameScene.heightProperty()
                .addListener((obs, oldVal, newVal) -> updatePopupPositions(gameScene, radish, harvestPopup));

        tileContainer.setOnMouseEntered(event -> {
            highlight.setFill(Color.YELLOW); // Set highlight color to yellow
            highlight.toFront(); // Bring highlight to front
            radish.setInfoLabelVisible(true); // Show info box when hovering
            radish.radishInfoLabel.toFront(); // Bring the popup to the front
        });

        tileContainer.setOnMouseExited(event -> {
            highlight.setFill(Color.TRANSPARENT); // Set back to transparent
            radish.setInfoLabelVisible(false); // Hide the info box
        });

        tileContainer.setOnMouseClicked(event -> {
            if (radish.harvestable) {
                Global.radishHarvested = Global.radishHarvested + 16;
                Global.incrementPlants();
                radish.setImage("Plants/PlantSprites/immatureRadish.png");
                radish.harvestable = false;
                radish.timestamp = System.currentTimeMillis();

                Global.updatePlanttoCell(radish.col, radish.row, timestamp);

                Seeds.incrementSeeds("radish");

                radish.updateLabel();
                harvestPopup.setVisible(true);

                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), event1 -> {
                    harvestPopup.setVisible(false);
                }));
                timeline.play();
            }
        });
    }

    private void updatePopupPositions(Scene gameScene, Radish radish, Label harvestPopup) {
        radish.radishInfoLabel.setTranslateX(gameScene.getWidth() - gameScene.getWidth() / 1.5);
        radish.radishInfoLabel.setTranslateY(gameScene.getHeight() - gameScene.getHeight() * 1.2);

        harvestPopup.setTranslateX(gameScene.getWidth() - gameScene.getWidth() / 1.5);
        harvestPopup.setTranslateY(gameScene.getHeight() - gameScene.getHeight() * 1.3);
    }

    public void loadRadish(int col, int row, GridPane gridPane, Scene gameScene) {
        this.col = col;
        this.row = row;
        StackPane tileContainer = new StackPane();
        tileContainer.getChildren().addAll(this.tileImage, this.highlight);
        addHighlightEffect(tileContainer, this.highlight, gameScene, this);
        gridPane.add(tileContainer, col, row);
    }

    public void placeTile(GridPane gridPane, Scene gameScene) {
        Global.setCurrentPlant("radish");

        // Check if month is correct
        if (Global.month != 2 && Global.month != 10) {
            Global.setCurrentPlant(null);
            Label seasonPopUp = new Label("Cannot plant radish in " + Global.getMonth() + "!"
                    + "\nPlant Feb or Sep.");
            seasonPopUp.setStyle("-fx-background-color: lightgreen; -fx-padding: 5px; -fx-border-color: black;");
            seasonPopUp.setVisible(true);

            seasonPopUp.setTranslateX(gameScene.getWidth() - gameScene.getWidth() / 1.5);
            seasonPopUp.setTranslateY(gameScene.getHeight() - gameScene.getHeight() * 1.4);

            ObservableList<Node> rootChildren = ((StackPane) gameScene.getRoot()).getChildren();
            if (!rootChildren.contains(seasonPopUp)) {
                rootChildren.add(seasonPopUp);
            }

            Timeline hidePopUpTimeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
                seasonPopUp.setVisible(false);
            }));
            hidePopUpTimeline.play();
            return;
        }

        // Set up C key event listener to stop placing corn
        gameScene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.C) {
                Global.setCurrentPlant(null);
                gameScene.setOnKeyPressed(null);
            }
        });

        // Handles click plant
        gridPane.setOnMouseClicked(mouseEvent -> {
            if (Global.getCurrentPlant() != "radish")
                return;

            col = (int) (mouseEvent.getX() / squareSize);
            row = (int) (mouseEvent.getY() / squareSize);

            if (Global.getPlantAtCell(col, row) == "radish") {
                return;
            }
            else {
                Global.removePlantFromCell(col, row);
            }

            Radish radish = new Radish(squareSize);

            // Save radish
            Plant radishPlant = new Plant("radish", timestamp);
            Global.addPlanttoCell2(col, row, radishPlant);

            Seeds.incrementSeeds("radish");

            StackPane tileContainer = new StackPane();
            tileContainer.getChildren().addAll(radish.tileImage, radish.highlight);
            addHighlightEffect(tileContainer, radish.highlight, gameScene, radish);
            gridPane.add(tileContainer, col, row);
            Global.incrementPlants();
        });

        // Handles dragging plant
        gridPane.setOnMouseDragged(mouseEvent -> {
            if (Global.getCurrentPlant() != "radish")
                return;

            col = (int) (mouseEvent.getX() / squareSize);
            row = (int) (mouseEvent.getY() / squareSize);

            if (Global.getPlantAtCell(col, row) == "radish")
                return;
            else if (col >= Global.globalCols || row >= Global.globalRows)
                return;
            else {
                Global.removePlantFromCell(col, row);
            }

            Radish radish = new Radish(squareSize);

            // Save radish
            Plant radishPlant = new Plant("radish", timestamp);
            Global.addPlanttoCell2(col, row, radishPlant);

            Seeds.incrementSeeds("radish");

            StackPane tileContainer = new StackPane();
            tileContainer.getChildren().addAll(radish.tileImage, radish.highlight);

            addHighlightEffect(tileContainer, radish.highlight, gameScene, radish);
            gridPane.add(tileContainer, col, row);
            Global.incrementPlants();
        });
    }

    public Radish(int squareSize) {
        this.squareSize = squareSize;

        radishInfoLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 5px; -fx-border-color: black;");

        tileImage = new ImageView(new Image("Plants/PlantSprites/immatureRadish.png"));
        tileImage.setFitWidth(100);
        tileImage.setFitHeight(100);

        highlight = new Rectangle(squareSize, squareSize);
        highlight.setFill(Color.TRANSPARENT);
        highlight.setStroke(Color.BLACK);

        timestamp = System.currentTimeMillis();
        startTimer();
    }

    public Radish(int squareSize, long timestamp) {
        this.squareSize = squareSize;
        this.timestamp = timestamp;

        radishInfoLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 5px; -fx-border-color: black;");

        tileImage = new ImageView(new Image("Plants/PlantSprites/immatureRadish.png"));
        tileImage.setFitWidth(100);
        tileImage.setFitHeight(100);

        highlight = new Rectangle(squareSize, squareSize);
        highlight.setFill(Color.TRANSPARENT);
        highlight.setStroke(Color.BLACK);

        startTimer();
    }

    private void startTimer() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateLabel()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateLabel() {
        currentTime = System.currentTimeMillis();
        secondsElapsed = (currentTime - timestamp) / 1000; // Convert milliseconds to seconds
        percentGrown = (double) secondsElapsed / growTime * 100;

        String formattedPercentGrown = String.format("%.6f", percentGrown);

        long days = secondsElapsed / (24 * 3600);
        long hours = (secondsElapsed % (24 * 3600)) / 3600;
        long minutes = (secondsElapsed % 3600) / 60;
        long seconds = secondsElapsed % 60;

        radishInfoLabel.setText("Radish Age " + days + ":" + hours + ":" + minutes + ":" + seconds
                + "\nPercent Grown: " + formattedPercentGrown + "%"
                + "\nRadish Harvested: " + Global.radishHarvested
                + "\n25 days to grow"
                + "\nPlant Feb or Oct"
                + "\nWater 2 times a week, 1in"
                + "\n16 Seeds/Plants per square foot");

        if (secondsElapsed > 3) {
            setImage("Plants/PlantSprites/matureRadish.png");
            harvestable = true;
        }
    }

    public void setInfoLabelVisible(boolean visible) {
        radishInfoLabel.setVisible(visible);
    }

    public void setImage(String imagePath) {
        tileImage.setImage(new Image(imagePath));
    }
}
