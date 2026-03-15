import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class MakeMovable {

    private BooleanProperty wPressed = new SimpleBooleanProperty();
    private BooleanProperty aPressed = new SimpleBooleanProperty();
    private BooleanProperty sPressed = new SimpleBooleanProperty();
    private BooleanProperty dPressed = new SimpleBooleanProperty();

    private BooleanBinding keyPressed = wPressed.or(aPressed).or(sPressed).or(dPressed);

    private int movementVariable = 8;
    private Rectangle shape1;
    private Pane scene; // Or AnchorPane/Scene

    // Constructor to initialize with the shape and scene
    public MakeMovable(Rectangle shape1, Pane scene) {
        this.shape1 = shape1;
        this.scene = scene;
        movementSetup(); // Call to initialize key bindings
        keyPressed.addListener((observableValue, aBoolean, t1) -> {
            if (!aBoolean) {
                timer.start();
            } else {
                timer.stop();
            }
        });
    }

    AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long timestamp) {
            if (wPressed.get()) {
                shape1.setLayoutY(shape1.getLayoutY() - movementVariable);
            }
            if (sPressed.get()) {
                shape1.setLayoutY(shape1.getLayoutY() + movementVariable);
            }
            if (aPressed.get()) {
                shape1.setLayoutX(shape1.getLayoutX() - movementVariable);
            }
            if (dPressed.get()) {
                shape1.setLayoutX(shape1.getLayoutX() + movementVariable);
            }
        }
    };

    public void movementSetup() {
        scene.setOnKeyPressed(e -> {
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

        scene.setOnKeyReleased(e -> {
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

