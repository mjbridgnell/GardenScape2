package Plants;

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

// RemovePlant class to handle removing plants from tiles

public class RemovePlant {
    private int squareSize;
    public Rectangle highlight;
    public ImageView tileImage;

    private void addHighlightEffect(StackPane tileContainer, Rectangle highlight, Scene gameScene, RemovePlant removePlant) {

        ObservableList<Node> rootChildren = ((StackPane) gameScene.getRoot()).getChildren();

        // Handles hover highlight
        tileContainer.setOnMouseEntered(event -> {
            highlight.setFill(Color.YELLOW);
            highlight.toFront(); 
        });


        tileContainer.setOnMouseExited(event -> {
            highlight.setFill(Color.TRANSPARENT);
        });
    }

    public void loadTile(int col, int row, GridPane gridPane, Scene gameScene) {
        StackPane tileContainer = new StackPane();
        tileContainer.getChildren().addAll(this.tileImage, this.highlight);
        addHighlightEffect(tileContainer, this.highlight, gameScene, this);
        gridPane.add(tileContainer, col, row);
    }

    // Handles removing plants
    public void removePlantFromTile(GridPane gridPane, Scene gameScene) {
        Global.setCurrentPlant("remove"); // Activate removal mode

        // Set up C key event listener to stop removing
        gameScene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.C) {
                Global.setCurrentPlant(null); // Stop removing when C is pressed
                gameScene.setOnKeyPressed(null); // Remove key listener
            }
        });

        // Add mouse click event handler to the gridPane
        gridPane.setOnMousePressed(mouseEvent -> {
            if (Global.getCurrentPlant() != "remove")
                return;

            // Calculate the column and row where the user clicked
            int col = (int) (mouseEvent.getX() / squareSize);
            int row = (int) (mouseEvent.getY() / squareSize);

            // Check if there's a plant at the clicked cell
            if (Global.getPlantAtCell(col, row) == null) {
                return; // No plant to remove
            }

            // Remove the plant
            Global.removePlantFromCell(col, row); // Implement this method in your Global class
            RemovePlant removePlant = new RemovePlant(squareSize);
            // Clear the grid cell
            StackPane tileContainer = new StackPane(); // Create a StackPane to hold the green tile and highlight
            tileContainer.getChildren().addAll(removePlant.tileImage, removePlant.highlight);
            addHighlightEffect(tileContainer, removePlant.highlight, gameScene, removePlant); // Add hover highlight effect to the
            gridPane.add(tileContainer, col, row); 
            Global.incrementPlants(); // Implement this to update the plant count
        });
    }

    public RemovePlant(int squareSize) {
        this.squareSize = squareSize;

        tileImage = new ImageView(new Image("Plants/PlantSprites/gardenTile.png")); // Use an appropriate image
        tileImage.setFitWidth(100);
        tileImage.setFitHeight(100);

        highlight = new Rectangle(squareSize, squareSize);
        highlight.setFill(Color.TRANSPARENT);
        highlight.setStroke(Color.BLACK);
    }

    public void setImage(String imagePath) {
        tileImage.setImage(new Image(imagePath));
    }
}
