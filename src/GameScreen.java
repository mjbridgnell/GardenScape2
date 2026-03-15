import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import Plants.Corn;
import Plants.Spinach;
import Plants.Sprinkler;
import Plants.GameState;
import Plants.Cucumber;
import Plants.Global;
import Plants.Grass;
import Plants.Plant;
import Plants.PopupManager;
import Plants.Radish;
import Plants.RemovePlant;
import Plants.Seeds;

import java.util.HashMap;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Pair;

// Game Screen Class
public class GameScreen {
    private Scene gameScene; // Game scene
    private Scene mainScene; // Used to go back to main menu
    Label widthLabel = new Label("TEST"); // Labels for dimensions dialog
    Label heightLabel = new Label("TEST");
    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds(); // Used to set the window size
    VBox layout1 = new VBox(20);
    VBox layout2 = new VBox(20);
    StackPane root = new StackPane(); // Root layout
    double zoomFactor = 1.0; // Zoom for grid
    int numRows; // Number of rows and column for garden grid
    int numCols;
    int squareSize = 100; // Size of grid cell
    GridPane gridPane = new GridPane();
    Image gardenTile = new Image("file:src\\Plants\\PlantSprites\\gardenTile.png"); // Image for base garden grid cell
    GameState loadState = null; // Load state to load save
    String musicFile = "src\\background2.wav"; // Background music
    Media sound = new Media(new File(musicFile).toURI().toString());
    MediaPlayer mediaPlayer = new MediaPlayer(sound);
    Label gameLabel = new Label("Your Garden");
    Image gameBackground = new Image("file:src\\field.jpeg"); // Backgroud image
    ImageView backgroundView = new ImageView(gameBackground);

    // Loads the previous save
    public void loadGameState(String filename) throws ClassNotFoundException {
        loadState = null;
        try {
            try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(filename))) {
                loadState = (GameState) input.readObject();
            }
        } catch (IOException ioe) {
            System.err.println("ERROR OPENING");
            return;
        }

        numCols = loadState.numCols;
        numRows = loadState.numRows;
        Global.plantMap2 = loadState.plantMap2;
        Global.setGrid(numCols, numRows);
        Global.cornHarvested = loadState.cornHarvested;
        Global.spinachHarvested = loadState.spinachHarvested;
        Global.radishHarvested = loadState.radishHarvested;
        Global.cucumberHarvested = loadState.cucumberHarvested;
        Seeds.cornSeeds = loadState.cornSeeds;
        Seeds.radishSeeds = loadState.radishSeeds;
        Seeds.cucumberSeeds = loadState.cucumberSeeds;
        Seeds.spinachSeeds = loadState.spinachSeeds;
        System.out.println(Seeds.cornSeeds);
        System.out.println(Seeds.radishSeeds);
        System.out.println(Seeds.cucumberSeeds);
        System.out.println(Seeds.spinachSeeds);

        gridPane.getChildren().clear();
        createGrid();
        Global.incrementPlants();
        Seeds.updateSeedsLabel();

        if (Global.plantMap2 != null) {
            for (HashMap.Entry<Pair<Integer, Integer>, Plant> entry : Global.plantMap2.entrySet()) {
                Pair<Integer, Integer> coordinates = entry.getKey(); // Get the coordinates
                Plant plantType = entry.getValue(); // Get the plant type
                if (plantType.getPlantName().equals("corn")) {
                    Corn corn = new Corn(squareSize, plantType.getTimestamp());
                    corn.loadCorn(coordinates.getKey(), coordinates.getValue(), gridPane, gameScene);
                } else if (plantType.getPlantName().equals("grass")) {
                    Grass grass = new Grass(squareSize, plantType.getTimestamp());
                    grass.loadGrass(coordinates.getKey(), coordinates.getValue(), gridPane, gameScene);
                } else if (plantType.getPlantName().equals("radish")) {
                    Radish radish = new Radish(squareSize, plantType.getTimestamp());
                    radish.loadRadish(coordinates.getKey(), coordinates.getValue(), gridPane, gameScene);
                } else if (plantType.getPlantName().equals("spinach")) {
                    Spinach spinach = new Spinach(squareSize, plantType.getTimestamp());
                    spinach.loadSpinach(coordinates.getKey(), coordinates.getValue(), gridPane, gameScene);
                } else if (plantType.getPlantName().equals("cucumber")) {
                    Cucumber cucumber = new Cucumber(squareSize, plantType.getTimestamp());
                    cucumber.loadCucumber(coordinates.getKey(), coordinates.getValue(), gridPane, gameScene);
                }
            }
            for (HashMap.Entry<Pair<Integer, Integer>, Plant> entry : Global.plantMap2.entrySet()) {
                Pair<Integer, Integer> coordinates = entry.getKey(); // Get the coordinates
                Plant plantType = entry.getValue(); // Get the plant type
                if (plantType.getPlantName().equals("sprinkler")) {
                    Sprinkler sprinkler = new Sprinkler(squareSize, plantType.getTimestamp());
                    sprinkler.loadSprinkler(coordinates.getKey(), coordinates.getValue(), gridPane, gameScene);
                }
            }
        }
    }

    // Creates the garden grid
    public void createGrid() {
        gridPane.setStyle("-fx-alignment: CENTER;");
        gridPane.setPadding(new javafx.geometry.Insets(0));
        gridPane.setHgap(-2);
        gridPane.setVgap(-2);

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                ImageView gardenView = new ImageView(gardenTile);
                gardenView.setFitWidth(squareSize);
                gardenView.setFitHeight(squareSize);

                Rectangle highlight = new Rectangle(squareSize, squareSize);
                highlight.setFill(Color.TRANSPARENT);
                highlight.setStroke(Color.BLACK);

                StackPane tileContainer = new StackPane();
                tileContainer.getChildren().addAll(gardenView, highlight);

                // Highlight square when mouse enters
                tileContainer.setOnMouseEntered(event -> {
                    highlight.setFill(Color.YELLOW);
                    highlight.toFront();
                });

                // Reset color when mouse exits
                tileContainer.setOnMouseExited(event -> {
                    highlight.setFill(Color.TRANSPARENT);
                });

                // Add square to the grid
                gridPane.add(tileContainer, col, row);
            }
        }
    }

    // Constructor
    public GameScreen(Scene mainScene) {
        this.mainScene = mainScene;
    }

    // Shows item menu and handles its functionality
    public void showItemMenu(Stage primaryStage) {
        Dialog<Void> itemMenuDialog = new Dialog<>();
        itemMenuDialog.setHeight(400);
        itemMenuDialog.setWidth(300);
        itemMenuDialog.setTitle("Item Menu");

        Label itemMenuLabel = new Label("Choose An Item");
        Button grassButton = new Button("Grass");
        Button cornButton = new Button("Corn - 4 seeds/ft^2");
        Button spinachButton = new Button("Spinach - 9 seeds/ft^2");
        Button cucumberButton = new Button("Cucumber - 1 seed/ft^2");
        Button radishButton = new Button("Radish - 16 seeds/ft^2");
        Button sprinklerButton = new Button("Sprinkler");
        Button removePlantButton = new Button("Remove Plant");
        VBox menuLayout = new VBox();

        menuLayout.getChildren().addAll(itemMenuLabel, grassButton, cucumberButton, cornButton,
                spinachButton, radishButton, sprinklerButton, removePlantButton);
        itemMenuDialog.getDialogPane().setContent(menuLayout);
        itemMenuDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);

        // Button functionality
        grassButton.setOnAction(event -> {
            itemMenuDialog.close();
            Grass grass = new Grass(squareSize);
            grass.placeGreenTile(gridPane, gameScene);
        });
        cornButton.setOnAction(event -> {
            itemMenuDialog.close();
            Corn corn = new Corn(squareSize);
            corn.placeTile(gridPane, gameScene);
        });
        spinachButton.setOnAction(event -> {
            itemMenuDialog.close();
            Spinach spinach = new Spinach(squareSize);
            spinach.placeTile(gridPane, gameScene);
        });
        cucumberButton.setOnAction(event -> {
            itemMenuDialog.close();
            Cucumber cucumber = new Cucumber(squareSize);
            cucumber.placeTile(gridPane, gameScene);
        });
        removePlantButton.setOnAction(evetn -> {
            itemMenuDialog.close();
            RemovePlant removePlant = new RemovePlant(squareSize);
            removePlant.removePlantFromTile(gridPane, gameScene);
        });
        radishButton.setOnAction(event -> {
            itemMenuDialog.close();
            Radish radish = new Radish(squareSize);
            radish.placeTile(gridPane, gameScene);
        });
        sprinklerButton.setOnAction(event -> {
            itemMenuDialog.close();
            Sprinkler sprinkler = new Sprinkler(squareSize);
            sprinkler.placeSprinklerTile(gridPane, gameScene);
        });
        itemMenuDialog.show();
    }

    // Settings menu
    public void showMenu(Stage primaryStage) {
        Dialog<Void> menuDialog = new Dialog<>();
        menuDialog.setHeight(400);
        menuDialog.setWidth(300);
        menuDialog.setTitle("Options");

        Button backButton = new Button("Main Menu");
        Button helpButton = new Button("Help");
        Button loadButton = new Button("Load Garden");
        Button saveButton = new Button("Save Garden");
        Button infoButton = new Button("Plant Information");
        Button exitButton = new Button("Exit Application");
        Button newGameButton = new Button("New Garden");

        VBox menuLayout = new VBox();
        menuLayout.getChildren().addAll(helpButton, saveButton, loadButton, newGameButton, backButton, infoButton,
                exitButton);
        menuDialog.getDialogPane().setContent(menuLayout);
        menuDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);

        // Button functionality
        backButton.setOnAction(e -> {
            Global.saveGameState();
            primaryStage.setScene(mainScene);
            menuDialog.close();
        });
        helpButton.setOnAction(e -> {
            showHelp(menuDialog, menuLayout);
        });
        saveButton.setOnAction(e -> {
            Global.saveGameState();
        });
        newGameButton.setOnAction(e -> {
            Global.saveGameState();
            showCustomDialog(widthLabel, heightLabel);
        });
        loadButton.setOnAction(e -> {
            try {
                loadGameState("test.dat");
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        });
        infoButton.setOnAction(e -> {
            Global.saveGameState();
            PlantInfo infoScene = new PlantInfo(mainScene);
            infoScene.start(primaryStage);
            menuDialog.close();
        });
        exitButton.setOnAction(e -> {
            Global.saveGameState();
            Platform.exit();
        });
        menuDialog.show();
    }

    // Shows help dialong
    private void showHelp(Dialog<Void> menuDialog, VBox menuLayout) {
        VBox helpLayout = new VBox();
        helpLayout.getChildren().add(new Label("Help Information:"));
        menuDialog.getDialogPane().setContent(helpLayout);

        Label helpInfo = new Label("Controls:\n" +
                "Escape - Show settings menu\n" +
                "E - Show Item menu\n" +
                "WASD - Move around\n" +
                "C - Cancel Placing Plant");

        Button backToMenuButton = new Button("Back");
        backToMenuButton.setOnAction(e -> {
            menuDialog.getDialogPane().setContent(menuLayout);
        });
        helpLayout.getChildren().addAll(helpInfo, backToMenuButton);
    }

    // Function to display dialog to create new garden
    public void showCustomDialog(Label widthLabel, Label heightLabel) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Garden Dimensions (ft)");

        ButtonType okButtonType = new ButtonType("OK", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
        TextField LengthInput = new TextField();
        LengthInput.setPromptText("Length");
        TextField WidthInput = new TextField();
        WidthInput.setPromptText("Width");

        GridPane grid = new GridPane();
        grid.add(new Label("Length:"), 0, 0);
        grid.add(LengthInput, 1, 0);
        grid.add(new Label("Width:"), 0, 1);
        grid.add(WidthInput, 1, 1);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return new Pair<>(LengthInput.getText(), WidthInput.getText());
            }
            return null;
        });

        // Set number of columns and rows and create grid
        dialog.showAndWait().ifPresent(result -> {
            try {
                numCols = Integer.parseInt(result.getKey());
                numRows = Integer.parseInt(result.getValue());
                widthLabel.setText("Columns: " + numCols);
                heightLabel.setText("Rows: " + numRows);
                Global.setGrid(numCols, numRows);

                gridPane.getChildren().clear();
                Global.plantMap2.clear();

                createGrid();
            } catch (NumberFormatException e) {
                System.err.println("Invalid input: " + e.getMessage());
            }
        });
    }

    // Function that runs when the scene starts
    public void start(Stage primaryStage) {
        Global.startClock();

        // Play music
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();

        // Game labels
        gameLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 5px; -fx-border-color: black;");
        Global.totalPlants.setStyle("-fx-background-color: lightgray; -fx-padding: 5px; -fx-border-color: black;");
        Global.dateLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 5px; -fx-border-color: black;");
        Seeds.seedLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 5px; -fx-border-color: black;");
        Seeds.pricesLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 5px; -fx-border-color: black;");

        // Settings for background image
        backgroundView.setFitWidth(screenBounds.getWidth()); // Match the width of the screen
        backgroundView.setPreserveRatio(true); // Preserve the aspect ratio
        backgroundView.setSmooth(true); // Enable smooth scaling

        // Settings for grid
        Pane gridHolder = new Pane(gridPane);
        GridMovable gridMovable = new GridMovable(gridPane, gridHolder, screenBounds);
        gridHolder.setFocusTraversable(true);

        // Set layout 1
        layout1.setStyle("-fx-alignment: TOP_CENTER;");
        layout1.setMouseTransparent(true);
        layout1.getChildren().addAll(gameLabel);

        // Set layout 2
        layout2.setStyle("-fx-alignment: TOP_RIGHT;");
        layout2.setMouseTransparent(true);
        layout2.getChildren().addAll(Global.dateLabel, Global.totalPlants, Seeds.pricesLabel, Seeds.seedLabel, PopupManager.harvestPopup, PopupManager.plantPopup);
        root.getChildren().addAll(backgroundView, gridHolder, layout1, layout2);

        // Create scene
        gameScene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
        primaryStage.setScene(gameScene);
        primaryStage.setMaximized(true);
        primaryStage.show();

        // Load game
        try {
            loadGameState("test.dat");
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }

        // Key press event handlers
        gameScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    showMenu(primaryStage);
                    break;
                case E:
                    showItemMenu(primaryStage);
                    break;
                default:
                    break;
            }
        });
        // Zoom event handler
        gameScene.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.getDeltaY() > 0) {
                zoomFactor *= 1.1;
            } else {
                zoomFactor /= 1.1;
            }
            zoomFactor = Math.max(0.5, Math.min(zoomFactor, 3.0));
            gridPane.setScaleX(zoomFactor);
            gridPane.setScaleY(zoomFactor);
            gridMovable.setZoomFactor(zoomFactor);
            event.consume();
        });
    }
}
