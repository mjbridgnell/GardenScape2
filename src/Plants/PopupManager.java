package Plants;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class PopupManager {
    public static Label plantPopup = new Label("Plant Planted!");
    public static Label harvestPopup = new Label("Plant Harvested!");

    public PopupManager() {
        setupPopupStyles();
    }

    private void setupPopupStyles() {
        String style = "-fx-background-color: lightgreen; -fx-padding: 5px; -fx-border-color: black;";
        harvestPopup.setStyle(style);
        harvestPopup.setVisible(false); // Initially hidden
        
        plantPopup.setStyle(style);
        plantPopup.setVisible(false); // Initially hidden
    }

    public static void displayPlantPopup() {
        plantPopup.setVisible(true);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), event1 -> {
            plantPopup.setVisible(false);
        }));
        timeline.play();
    }

    public static void displayHarvestPopup() {
        harvestPopup.setVisible(true);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), event1 -> {
            harvestPopup.setVisible(false);
        }));
        timeline.play();
    }
}
