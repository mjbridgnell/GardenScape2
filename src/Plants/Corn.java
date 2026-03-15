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

// Plant Mar-Sep
// 90 days to grow
// Water 2 times a week, 1in
// 4 seeds per square foot

public class Corn {
    private long growTime = Global.getGrowTime("corn"); // seconds
    private double percentGrown = 0;
    private int squareSize;
    public ImageView tileImage;
    public Rectangle highlight;
    public boolean harvestable = false;
    private long secondsElapsed;
    private int col;
    private int row;
    private long timestamp; // Timestamp when the corn tile was placed
    private long currentTime;
    private PopupManager popupManager;
    private Label cornInfoLabel = new Label("Corn Age: 0"
            + "\nPercent Grown: 0%"
            + "\n90 days to grow"
            + "\nPlant Mar-Sep"
            + "\nWater 2 times a week, 1in"
            + "\n4 Seeds/Plants per square foot");

    private void addHighlightEffect(StackPane tileContainer, Rectangle highlight, Scene gameScene, Corn corn) {
        corn.setInfoLabelVisible(false);

        // Set info label position
        corn.cornInfoLabel.setTranslateX(gameScene.getWidth() - gameScene.getWidth() / 1.5);
        corn.cornInfoLabel.setTranslateY(gameScene.getHeight() - gameScene.getHeight() * 1.2);

        ObservableList<Node> rootChildren = ((StackPane) gameScene.getRoot()).getChildren();
        if (!rootChildren.contains(corn.cornInfoLabel)) {
            rootChildren.add(corn.cornInfoLabel);
        }

        tileContainer.setOnMouseEntered(event -> {
            highlight.setFill(Color.YELLOW);
            highlight.toFront();
            corn.setInfoLabelVisible(true);
            corn.cornInfoLabel.toFront();
        });

        tileContainer.setOnMouseExited(event -> {
            highlight.setFill(Color.TRANSPARENT);
            corn.setInfoLabelVisible(false);
        });

        // Handles Plants getting harvested
        tileContainer.setOnMouseClicked(event -> {
            if (corn.harvestable) {
                Global.cornHarvested = Global.cornHarvested + 4;
                Global.incrementPlants();
                corn.setImage("Plants/PlantSprites/immatureCorn.png");
                corn.harvestable = false;
                corn.timestamp = System.currentTimeMillis();

                Global.updatePlanttoCell(corn.col, corn.row, timestamp);

                Seeds.incrementSeeds("corn");

                PopupManager.displayHarvestPopup();

                corn.updateLabel();
            } else {

            }
        });
    }

    public void loadCorn(int col, int row, GridPane gridPane, Scene gameScene) {
        this.col = col;
        this.row = row;
        StackPane tileContainer = new StackPane();
        tileContainer.getChildren().addAll(this.tileImage, this.highlight);
        addHighlightEffect(tileContainer, this.highlight, gameScene, this);
        gridPane.add(tileContainer, col, row);
    }

    public void placeTile(GridPane gridPane, Scene gameScene) {
        Global.setCurrentPlant("corn");

        // C key event listener to stop placing corn
        gameScene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.C) {
                Global.setCurrentPlant(null);
                gameScene.setOnKeyPressed(null);
            }
        });

        gridPane.setOnMouseClicked(mouseEvent -> {
            if (Global.getCurrentPlant() != "corn")
                return;

            col = (int) (mouseEvent.getX() / squareSize);
            row = (int) (mouseEvent.getY() / squareSize);

            if (Global.getPlantAtCell(col, row) == "corn") {
                return; // Corn is already at that location
            } else {
                Global.removePlantFromCell(col, row);
            }

            Corn corn = new Corn(squareSize); // Create a new corn instance

            Plant cornPlant = new Plant("corn", timestamp);
            Global.addPlanttoCell2(col, row, cornPlant);

            Seeds.incrementSeeds("corn");

            StackPane tileContainer = new StackPane();
            tileContainer.getChildren().addAll(corn.tileImage, corn.highlight);
            addHighlightEffect(tileContainer, corn.highlight, gameScene, corn);
            gridPane.add(tileContainer, col, row);
            Global.incrementPlants();

            ObservableList<Node> rootChildren = ((StackPane) gameScene.getRoot()).getChildren();
            if (!rootChildren.contains(corn.cornInfoLabel)) {
                rootChildren.add(corn.cornInfoLabel);
            }

            PopupManager.displayPlantPopup();
        });

        // Handles dragging mouse to plant corn
        gridPane.setOnMouseDragged(mouseEvent -> {
            if (Global.getCurrentPlant() != "corn")
                return;

            col = (int) (mouseEvent.getX() / squareSize);
            row = (int) (mouseEvent.getY() / squareSize);

            if (Global.getPlantAtCell(col, row) == "corn")
                return;
            else if (col >= Global.globalCols || row >= Global.globalRows)
                return;
            else {
                Global.removePlantFromCell(col, row);
            }

            Corn corn = new Corn(squareSize);

            Seeds.incrementSeeds("corn");

            Plant cornPlant = new Plant("corn", timestamp);
            Global.addPlanttoCell2(col, row, cornPlant);

            StackPane tileContainer = new StackPane();
            tileContainer.getChildren().addAll(corn.tileImage, corn.highlight);

            addHighlightEffect(tileContainer, corn.highlight, gameScene, corn);
            gridPane.add(tileContainer, col, row);
            Global.incrementPlants();

            PopupManager.displayPlantPopup();
        });
    }

    public Corn(int squareSize) {
        this.squareSize = squareSize;
        this.popupManager = new PopupManager();

        cornInfoLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 5px; -fx-border-color: black;");

        tileImage = new ImageView(new Image("Plants/PlantSprites/immatureCorn.png"));
        tileImage.setFitWidth(100);
        tileImage.setFitHeight(100);

        highlight = new Rectangle(squareSize, squareSize);
        highlight.setFill(Color.TRANSPARENT);
        highlight.setStroke(Color.BLACK);

        timestamp = System.currentTimeMillis();
        startTimer(); // Start the timer
    }

    public Corn(int squareSize, long timestamp) {
        this.squareSize = squareSize;
        this.timestamp = timestamp;
        this.popupManager = new PopupManager();

        cornInfoLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 5px; -fx-border-color: black;");

        tileImage = new ImageView(new Image("Plants/PlantSprites/immatureCorn.png"));
        tileImage.setFitWidth(100);
        tileImage.setFitHeight(100);

        highlight = new Rectangle(squareSize, squareSize);
        highlight.setFill(Color.TRANSPARENT);
        highlight.setStroke(Color.BLACK);

        startTimer(); // Start the timer
    }

    private void startTimer() {
        // Create a timeline that updates the label every second
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateLabel()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateLabel() {
        currentTime = System.currentTimeMillis();
        // Retain previous secondsElapsed and only calculate the difference
        secondsElapsed = (currentTime - timestamp) / 1000;
        percentGrown = (double) secondsElapsed / growTime * 100;

        String formattedPercentGrown = String.format("%.6f", percentGrown);

        long days = secondsElapsed / (24 * 3600);
        long hours = (secondsElapsed % (24 * 3600)) / 3600;
        long minutes = (secondsElapsed % 3600) / 60;
        long seconds = secondsElapsed % 60;

        cornInfoLabel.setText("Corn Age " + days + ":" + hours + ":" + minutes + ":" + seconds
                + "\nPercent Grown: " + formattedPercentGrown + "%"
                + "\nCorn Harvested: " + Global.cornHarvested
                + "\n90 days to grow"
                + "\nPlant Mar-Sep"
                + "\nWater 2 times a week, 1in"
                + "\n4 Seeds/Plants per square foot");

        if (secondsElapsed > 5) {
            setImage("Plants/PlantSprites/matureCorn.png");
            harvestable = true;
        }
    }

    public void setInfoLabelVisible(boolean visible) {
        cornInfoLabel.setVisible(visible);
    }

    public void setImage(String imagePath) {
        tileImage.setImage(new Image(imagePath));
    }
}
