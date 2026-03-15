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

// 50 days to grow
// Water several times a week, 1-2in
// Plant Mar-Sep
// One seed per square foot

public class Cucumber {
    private long growTime = Global.getGrowTime("cucumber");
    private double percentGrown = 0;
    private int squareSize;
    public ImageView tileImage;
    public Rectangle tile;
    public Rectangle highlight;
    public boolean harvestable = false;
    private long secondsElapsed;
    private long timestamp;
    private long currentTime;
    private int col;
    private int row;
    private Label cucumberInfoLabel = new Label("Cucumber: 0 seconds old"
            + "\nPercent Grown: 0%"
            + "\n50 days to grow"
            + "\nPlant Mar-Sep"
            + "\nWater several times a week, 1-2in"
            + "\n1 Seed per square foot");

    private void addHighlightEffect(StackPane tileContainer, Rectangle highlight, Scene gameScene, Cucumber cucumber) {
        cucumber.setInfoLabelVisible(false);

        cucumber.cucumberInfoLabel.setTranslateX(gameScene.getWidth() - gameScene.getWidth() / 1.5);
        cucumber.cucumberInfoLabel.setTranslateY(gameScene.getHeight() - gameScene.getHeight() * 1.2);

        Label harvestPopUp = new Label("Cucumber Harvested!");
        harvestPopUp.setStyle("-fx-background-color: lightgreen; -fx-padding: 5px; -fx-border-color: black;");
        harvestPopUp.setVisible(false);
        tileContainer.getChildren().add(harvestPopUp);
        harvestPopUp.setTranslateX(gameScene.getWidth() - gameScene.getWidth() / 1.5);
        harvestPopUp.setTranslateY(gameScene.getHeight() - gameScene.getHeight() * 1.3);

        ObservableList<Node> rootChildren = ((StackPane) gameScene.getRoot()).getChildren();
        if (!rootChildren.contains(cucumber.cucumberInfoLabel)) {
            rootChildren.add(cucumber.cucumberInfoLabel);
        }
        if (!rootChildren.contains(harvestPopUp)) {
            rootChildren.add(harvestPopUp);
        }

        tileContainer.setOnMouseEntered(event -> {
            highlight.setFill(Color.YELLOW);
            highlight.toFront();
            cucumber.setInfoLabelVisible(true);
            cucumber.cucumberInfoLabel.toFront();
        });

        tileContainer.setOnMouseExited(event -> {
            highlight.setFill(Color.TRANSPARENT);
            cucumber.setInfoLabelVisible(false);
        });

        // Handles Plants getting harvested
        tileContainer.setOnMouseClicked(event -> {
            if (cucumber.harvestable) {
                Global.cucumberHarvested++;
                Global.incrementPlants();
                cucumber.setImage("Plants/PlantSprites/immatureCucumber.png");
                cucumber.harvestable = false;
                cucumber.timestamp = System.currentTimeMillis();

                Global.updatePlanttoCell(cucumber.col, cucumber.row, timestamp);

                cucumber.updateLabel();
                harvestPopUp.setVisible(true);

                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), event1 -> {
                    harvestPopUp.setVisible(false);
                }));
                timeline.play();
            } else {

            }
        });
    }

    public void loadCucumber(int col, int row, GridPane gridPane, Scene gameScene) {
        this.col = col;
        this.row = row;
        StackPane tileContainer = new StackPane();
        tileContainer.getChildren().addAll(this.tileImage, this.highlight);
        addHighlightEffect(tileContainer, this.highlight, gameScene, this);
        gridPane.add(tileContainer, col, row);
    }

    public void placeTile(GridPane gridPane, Scene gameScene) {
        Global.setCurrentPlant("cucumber");

        // Check for correct month
        if (Global.month == 1 || Global.month == 2 || Global.month == 9 || Global.month == 11 || Global.month == 12) {
            Global.setCurrentPlant(null);
            Label seasonPopUp = new Label("Cannot plant cucumber in " + Global.getMonth() + "!"
                    + "\nPlant Mar-Sep.");
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
            if (Global.getCurrentPlant() != "cucumber")
                return;

            col = (int) (mouseEvent.getX() / squareSize);
            row = (int) (mouseEvent.getY() / squareSize);

            if (Global.getPlantAtCell(col, row) == "cucumber") {
                return;
            } else {
                Global.removePlantFromCell(col, row);
            }

            Cucumber cucumber = new Cucumber(squareSize);

            // Save cucumber
            Plant cucumberPlant = new Plant("cucumber", timestamp);
            Global.addPlanttoCell2(col, row, cucumberPlant);

            Seeds.incrementSeeds("cucumber");

            StackPane tileContainer = new StackPane();
            tileContainer.getChildren().addAll(cucumber.tileImage, cucumber.highlight);
            addHighlightEffect(tileContainer, cucumber.highlight, gameScene, cucumber);
            gridPane.add(tileContainer, col, row);
            Global.incrementPlants();
        });

        // Handles dragging plant
        gridPane.setOnMouseDragged(mouseEvent -> {
            if (Global.getCurrentPlant() != "cucumber")
                return;

            col = (int) (mouseEvent.getX() / squareSize);
            row = (int) (mouseEvent.getY() / squareSize);

            if (Global.getPlantAtCell(col, row) == "cucumber")
                return;
            else if (col >= Global.globalCols || row >= Global.globalRows)
                return;
            else {
                Global.removePlantFromCell(col, row);
            }

            Cucumber cucumber = new Cucumber(squareSize);

            // Save cucumber
            Plant cucumberPlant = new Plant("cucumber", timestamp);
            Global.addPlanttoCell2(col, row, cucumberPlant);

            Seeds.incrementSeeds("cucumber");

            StackPane tileContainer = new StackPane();
            tileContainer.getChildren().addAll(cucumber.tileImage, cucumber.highlight);
            addHighlightEffect(tileContainer, cucumber.highlight, gameScene, cucumber);
            gridPane.add(tileContainer, col, row);
            Global.incrementPlants();
        });
    }

    public Cucumber(int squareSize) {
        this.squareSize = squareSize;

        tileImage = new ImageView(new Image("Plants/PlantSprites/immatureCucumber.png"));
        cucumberInfoLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 5px; -fx-border-color: black;");

        highlight = new Rectangle(squareSize, squareSize);
        highlight.setFill(Color.TRANSPARENT);
        highlight.setStroke(Color.BLACK);

        timestamp = System.currentTimeMillis();
        startTimer();
    }

    public Cucumber(int squareSize, long timestamp) {
        this.squareSize = squareSize;
        this.timestamp = timestamp;

        tileImage = new ImageView(new Image("Plants/PlantSprites/immatureCucumber.png"));
        cucumberInfoLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 5px; -fx-border-color: black;");

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

        cucumberInfoLabel.setText("Cucumber Age " + days + ":" + hours + ":" + minutes + ":" + seconds
                + "\nPercent Grown: " + formattedPercentGrown + "%"
                + "\nCucumber Harvested: " + Global.cucumberHarvested
                + "\n50 days to grow"
                + "\nPlant Mar-Sep"
                + "\nWater several times a week, 1-2in"
                + "\n1 Seeds per square foot");

        if (secondsElapsed > 3) {
            setImage("Plants/PlantSprites/matureCucumber.png");
            harvestable = true;
        }
    }

    public void setInfoLabelVisible(boolean visible) {
        cucumberInfoLabel.setVisible(visible);
    }

    public void setImage(String imagePath) {
        tileImage.setImage(new Image(imagePath));
    }
}
