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

// Sprinkler

public class Sprinkler {
    private int squareSize;
    public Label sprinklerInfoLabel;
    public Rectangle highlight;
    public ImageView tileImage;
    private long secondsElapsed;
    private int col;
    private int row;
    private long timestamp; // Timestamp when the sprinkler tile was placed

    private void checkPlantsInRange(GridPane gridPane) {
        for (int c = col - 1; c <= col + 1; c++) {
            for (int r = row - 1; r <= row + 1; r++) {
                if (c >= 0 && r >= 0 && c < Global.globalCols && r < Global.globalRows) {
                    String plant = Global.getPlantAtCell(c, r);
                    if (plant != null && !plant.equals("sprinkler")) {
                        System.out.println("Plant " + plant + " is within sprinkler range at (" + c + ", " + r + ")");
                    }
                }
            }
        }
    }

    private void addHighlightEffect(StackPane tileContainer, Rectangle highlight, Scene gameScene,
            Sprinkler sprinkler) {
        sprinkler.setInfoLabelVisible(false);

        sprinkler.sprinklerInfoLabel.setTranslateX(gameScene.getWidth() - gameScene.getWidth() / 1.5);
        sprinkler.sprinklerInfoLabel.setTranslateY(gameScene.getHeight() - gameScene.getHeight() * 1.2);

        ObservableList<Node> rootChildren = ((StackPane) gameScene.getRoot()).getChildren();
        if (!rootChildren.contains(sprinkler.sprinklerInfoLabel)) {
            rootChildren.add(sprinkler.sprinklerInfoLabel);
        }

        updatePopupPositions(gameScene, sprinkler);

        gameScene.widthProperty()
                .addListener((obs, oldVal, newVal) -> updatePopupPositions(gameScene, sprinkler));
        gameScene.heightProperty()
                .addListener((obs, oldVal, newVal) -> updatePopupPositions(gameScene, sprinkler));

        tileContainer.setOnMouseEntered(event -> {
            highlight.setFill(Color.YELLOW);
            highlight.toFront();
            sprinkler.setInfoLabelVisible(true);
            sprinkler.sprinklerInfoLabel.toFront();
        });

        tileContainer.setOnMouseExited(event -> {
            highlight.setFill(Color.TRANSPARENT);
            sprinkler.setInfoLabelVisible(false);
        });
    }

    private void updatePopupPositions(Scene gameScene, Sprinkler sprinkler) {
        sprinkler.sprinklerInfoLabel.setTranslateX(gameScene.getWidth() - gameScene.getWidth() / 1.5);
        sprinkler.sprinklerInfoLabel.setTranslateY(gameScene.getHeight() - gameScene.getHeight() * 1.2);
    }

    public void loadSprinkler(int col, int row, GridPane gridPane, Scene gameScene) {
        this.col = col;
        this.row = row;
        StackPane tileContainer = new StackPane();
        tileContainer.getChildren().addAll(this.tileImage, this.highlight);
        addHighlightEffect(tileContainer, this.highlight, gameScene, this);
        createRangeBoxes(gridPane, col, row);
        gridPane.add(tileContainer, col, row);
    }

    private void createRangeBoxes(GridPane gridPane, int col, int row) {
        int[][] offsets = {
                { -1, -1 }, { -1, 0 }, { -1, 1 },
                { 0, -1 }, { 0, 1 },
                { 1, -1 }, { 1, 0 }, { 1, 1 }
        };

        for (int[] offset : offsets) {
            int offsetCol = col + offset[0];
            int offsetRow = row + offset[1];

            // Check bounds
            if (offsetCol >= 0 && offsetRow >= 0 && offsetCol < Global.globalCols && offsetRow < Global.globalRows) {
                Rectangle rangeBox = new Rectangle(squareSize, squareSize);
                rangeBox.setFill(Color.BLUE.deriveColor(0, 1, 1, 0.5)); // Semi-transparent blue
                rangeBox.setStroke(Color.TRANSPARENT); // No border
                rangeBox.setVisible(true); // Make it visible

                rangeBox.setMouseTransparent(true);

                // Add the range box to the grid
                gridPane.add(rangeBox, offsetCol, offsetRow);
                // Bring the range box to front
                rangeBox.toFront();
                System.out.println("Creating range box at (" + offsetCol + ", " + offsetRow + ")");
            }
        }
    }

    // Handles placing sprinkler
    public void placeSprinklerTile(GridPane gridPane, Scene gameScene) {
        Global.setCurrentPlant("sprinkler");

        // Set up C key event listener to stop placing sprinkler
        gameScene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.C) {
                Global.setCurrentPlant(null);
                gameScene.setOnKeyPressed(null);
            }
        });

        gridPane.setOnMousePressed(mouseEvent -> {
            if (Global.getCurrentPlant() == null)
                return;
        
            col = (int) (mouseEvent.getX() / squareSize);
            row = (int) (mouseEvent.getY() / squareSize);
        
            if (Global.getPlantAtCell(col, row).equals("sprinkler")) {
                return;
            } else {
                Global.removePlantFromCell(col, row);
            }
        
            // Create the sprinkler
            Sprinkler sprinkler = new Sprinkler(squareSize);
        
            // Save the sprinkler
            Plant sprinklerPlant = new Plant("sprinkler", timestamp);
            Global.addPlanttoCell2(col, row, sprinklerPlant);
        
            // Create range boxes here, only once
            createRangeBoxes(gridPane, col, row);
            checkPlantsInRange(gridPane);
        
            StackPane tileContainer = new StackPane();
            tileContainer.getChildren().addAll(sprinkler.tileImage, sprinkler.highlight);
            addHighlightEffect(tileContainer, sprinkler.highlight, gameScene, sprinkler);
            gridPane.add(tileContainer, col, row);
        
            Global.incrementPlants();
        });
    }

    public Sprinkler(int squareSize) {
        this.squareSize = squareSize;

        sprinklerInfoLabel = new Label("Sprinkler: 0 seconds old");
        sprinklerInfoLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 5px; -fx-border-color: black;");

        tileImage = new ImageView(new Image("Plants/PlantSprites/immatureCucumber.png"));
        tileImage.setFitWidth(100);
        tileImage.setFitHeight(100);

        highlight = new Rectangle(squareSize, squareSize);
        highlight.setFill(Color.TRANSPARENT);
        highlight.setStroke(Color.BLACK);

        timestamp = System.currentTimeMillis();

        startTimer();
    }

    public Sprinkler(int squareSize, long timestamp) {
        this.squareSize = squareSize;
        this.timestamp = timestamp;

        sprinklerInfoLabel = new Label("Sprinkler: 0 seconds old");
        sprinklerInfoLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 5px; -fx-border-color: black;");

        tileImage = new ImageView(new Image("Plants/PlantSprites/immatureCucumber.png"));
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
        long currentTime = System.currentTimeMillis();
        secondsElapsed = (currentTime - timestamp) / 1000;

        long days = secondsElapsed / (24 * 3600);
        long hours = (secondsElapsed % (24 * 3600)) / 3600;
        long minutes = (secondsElapsed % 3600) / 60;
        long seconds = secondsElapsed % 60;

        sprinklerInfoLabel.setText("Sprinkler Age " + days + ":" + hours + ":" + minutes + ":" + seconds);
    }

    public void setInfoLabelVisible(boolean visible) {
        sprinklerInfoLabel.setVisible(visible);
    }

    public void setImage(String imagePath) {
        tileImage.setImage(new Image(imagePath));
    }
}
