import javafx.animation.AnimationTimer;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class GridMovable {

    private BooleanProperty wPressed = new SimpleBooleanProperty();
    private BooleanProperty aPressed = new SimpleBooleanProperty();
    private BooleanProperty sPressed = new SimpleBooleanProperty();
    private BooleanProperty dPressed = new SimpleBooleanProperty();

    private BooleanBinding keyPressed = wPressed.or(aPressed).or(sPressed).or(dPressed);

    private int movementVariable = 8;
    private GridPane gridPane;
    private Pane pane;
    private Rectangle2D screenBounds;

    private double zoomFactor = 1.0;

    // Constructor to initialize with the shape and scene
    public GridMovable(GridPane gridPane, Pane pane, Rectangle2D screenBounds) {
        this.gridPane = gridPane;
        this.pane = pane;
        this.screenBounds = screenBounds;
        movementSetup(); // Call to initialize key bindings
        keyPressed.addListener((observableValue, aBoolean, t1) -> {
            if (!aBoolean) {
                timer.start();
            } else {
                timer.stop();
            }
        });
    }

    public void setZoomFactor(double zoomFactor)
    {
        this.zoomFactor = zoomFactor;
    }

    AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long timestamp) {
            double newLayoutX = gridPane.getLayoutX();
            double newLayoutY = gridPane.getLayoutY();

            // Get the pane's width and height (the parent container)
            double paneWidth = screenBounds.getWidth();
            double paneHeight = screenBounds.getHeight();

            // Get the gridPane's width and height
            double gridWidth = gridPane.getWidth();
            double gridHeight = gridPane.getHeight();

            // Calculate new X and Y positions
            if (wPressed.get()) {
                newLayoutY -= movementVariable;
            }
            if (sPressed.get()) {
                newLayoutY += movementVariable;
            }
            if (aPressed.get()) {
                newLayoutX -= movementVariable;
            }
            if (dPressed.get()) {
                newLayoutX += movementVariable;
            }

            // Ensure part of the gridPane is always on the screen
            // X-axis constraints
            if (gridWidth > paneWidth) { // If grid is wider than the pane
                if (newLayoutX < paneWidth - gridWidth) { // Prevent moving too far left
                    newLayoutX = paneWidth - gridWidth;
                } else if (newLayoutX > 0) { // Prevent moving too far right
                    newLayoutX = 0;
                }
            } else { // If grid is smaller or equal to pane width, keep it within pane
                if (newLayoutX < 0) { // Prevent moving off to the left
                    newLayoutX = 0;
                } else if (newLayoutX + gridWidth > paneWidth) { // Prevent moving off to the right
                    newLayoutX = paneWidth - gridWidth;
                }
            }

            // Y-axis constraints
            if (gridHeight > paneHeight) {
                if (newLayoutY < paneHeight - gridHeight) {
                    newLayoutY = paneHeight - gridHeight;
                } else if (newLayoutY > 0) {
                    newLayoutY = 0;
                }
            } else {
                if (newLayoutY < 0) {
                    newLayoutY = 0;
                } else if (newLayoutY + gridHeight > paneHeight) {
                    newLayoutY = paneHeight - gridHeight;
                }
            }

            // Set the new position of the gridPane
            gridPane.setLayoutX(newLayoutX);
            gridPane.setLayoutY(newLayoutY);
        }
    };

    public void movementSetup() {
        pane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.W) {
                wPressed.set(true);
            }
            if (e.getCode() == KeyCode.A) {
                aPressed.set(true);
            }
            if (e.getCode() == KeyCode.S) {
                sPressed.set(true);
            }
            if (e.getCode() == KeyCode.D) {
                dPressed.set(true);
            }
        });

        pane.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.W) {
                wPressed.set(false);
            }
            if (e.getCode() == KeyCode.A) {
                aPressed.set(false);
            }
            if (e.getCode() == KeyCode.S) {
                sPressed.set(false);
            }
            if (e.getCode() == KeyCode.D) {
                dPressed.set(false);
            }
        });
    }
}
