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

// Grass class, kind of like default plant class

public class Grass {
    private int squareSize;
    public Label grassInfoLabel;
    public Rectangle highlight;
    public ImageView tileImage;
    private long secondsElapsed;
    private int col;
    private int row;
    private long timestamp; // Timestamp when the grass tile was placed

    private void addHighlightEffect(StackPane tileContainer, Rectangle highlight, Scene gameScene, Grass grass) {
        grass.setInfoLabelVisible(false);

        grass.grassInfoLabel.setTranslateX(gameScene.getWidth() - gameScene.getWidth() / 1.5);
        grass.grassInfoLabel.setTranslateY(gameScene.getHeight() - gameScene.getHeight() * 1.2);

        ObservableList<Node> rootChildren = ((StackPane) gameScene.getRoot()).getChildren();
        if (!rootChildren.contains(grass.grassInfoLabel)) {
            rootChildren.add(grass.grassInfoLabel);
        }

        updatePopupPositions(gameScene, grass);

        gameScene.widthProperty()
                .addListener((obs, oldVal, newVal) -> updatePopupPositions(gameScene, grass));
        gameScene.heightProperty()
                .addListener((obs, oldVal, newVal) -> updatePopupPositions(gameScene, grass));

        tileContainer.setOnMouseEntered(event -> {
            highlight.setFill(Color.YELLOW);
            highlight.toFront();
            grass.setInfoLabelVisible(true);
            grass.grassInfoLabel.toFront();
        });

        tileContainer.setOnMouseExited(event -> {
            highlight.setFill(Color.TRANSPARENT);
            grass.setInfoLabelVisible(false);
        });
    }

    private void updatePopupPositions(Scene gameScene, Grass grass) {
        grass.grassInfoLabel.setTranslateX(gameScene.getWidth() - gameScene.getWidth() / 1.5);
        grass.grassInfoLabel.setTranslateY(gameScene.getHeight() - gameScene.getHeight() * 1.2);
    }

    public void loadGrass(int col, int row, GridPane gridPane, Scene gameScene) {
        this.col = col;
        this.row = row;
        StackPane tileContainer = new StackPane();
        tileContainer.getChildren().addAll(this.tileImage, this.highlight);
        addHighlightEffect(tileContainer, this.highlight, gameScene, this);
        gridPane.add(tileContainer, col, row);
    }

    // Handles placing grass
    public void placeGreenTile(GridPane gridPane, Scene gameScene) {
        Global.setCurrentPlant("grass");

        // Set up C key event listener to stop placing grass
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

            if (Global.getPlantAtCell(col, row) == "grass") {
                return;
            } else {
                Global.removePlantFromCell(col, row);
            }

            Grass grass = new Grass(squareSize);

            // Save grass
            Plant grassPlant = new Plant("grass", timestamp);
            Global.addPlanttoCell2(col, row, grassPlant);

            StackPane tileContainer = new StackPane();
            tileContainer.getChildren().addAll(grass.tileImage, grass.highlight);
            addHighlightEffect(tileContainer, grass.highlight, gameScene, grass);
            gridPane.add(tileContainer, col, row);
            Global.incrementPlants();
        });
        // Place grass if mouse is pressed and dragged
        gridPane.setOnMouseDragged(mouseEvent -> {
            if (Global.getCurrentPlant() == null)
                return;

            col = (int) (mouseEvent.getX() / squareSize);
            row = (int) (mouseEvent.getY() / squareSize);

            if (Global.getPlantAtCell(col, row) == "grass")
                return;
            else if (col >= Global.globalCols || row >= Global.globalRows)
                return;
            else {
                Global.removePlantFromCell(col, row);
            }

            Grass grass = new Grass(squareSize);

            // Save grass
            Plant grassPlant = new Plant("grass", timestamp);
            Global.addPlanttoCell2(col, row, grassPlant);

            StackPane tileContainer = new StackPane();
            tileContainer.getChildren().addAll(grass.tileImage, grass.highlight);
            addHighlightEffect(tileContainer, grass.highlight, gameScene, grass);

            gridPane.add(tileContainer, col, row);
            Global.incrementPlants();
        });
    }

    public Grass(int squareSize) {
        this.squareSize = squareSize;

        grassInfoLabel = new Label("Grass: 0 seconds old");
        grassInfoLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 5px; -fx-border-color: black;");

        tileImage = new ImageView(new Image("Plants/PlantSprites/grassTile.png"));
        tileImage.setFitWidth(100);
        tileImage.setFitHeight(100);

        highlight = new Rectangle(squareSize, squareSize);
        highlight.setFill(Color.TRANSPARENT);
        highlight.setStroke(Color.BLACK);
        timestamp = System.currentTimeMillis();

        startTimer();
    }

    public Grass(int squareSize, long timestamp) {
        this.squareSize = squareSize;
        this.timestamp = timestamp;

        grassInfoLabel = new Label("Grass: 0 seconds old");
        grassInfoLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 5px; -fx-border-color: black;");

        tileImage = new ImageView(new Image("Plants/PlantSprites/grassTile.png"));
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

        grassInfoLabel.setText("Grass Age " + days + ":" + hours + ":" + minutes + ":" + seconds);
    }

    public void setInfoLabelVisible(boolean visible) {
        grassInfoLabel.setVisible(visible);
    }

    public void setImage(String imagePath) {
        tileImage.setImage(new Image(imagePath));
    }
}
