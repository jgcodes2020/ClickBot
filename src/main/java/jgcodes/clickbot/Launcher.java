package jgcodes.clickbot;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import javafx.util.Duration;
import jgcodes.clickbot.util.FillGridPane;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyAdapter;
import org.jnativehook.keyboard.NativeKeyEvent;

import java.security.Key;
import java.util.List;

public class Launcher extends Application {
  public static ObservableList<String> mouseButtons = FXCollections.observableArrayList(
    "Left", "Middle", "Right"
  );

  private static boolean wasValueChanged = false;

  private static final ToggleButton startStopButton = new ToggleButton("Start clicker");

  public static void main(String[] args) {
    try {
      GlobalScreen.registerNativeHook();
    } catch (NativeHookException e) {
      e.printStackTrace();
    }
    GlobalScreen.addNativeKeyListener(new NativeKeyAdapter() {
      @Override
      public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_F6)
          Platform.runLater(() -> {
            startStopButton.setSelected(!startStopButton.isSelected());
          });
      }
    });
    launch(args);
  }

  @Override
  public void start(Stage stage) {
    GridPane root = new FillGridPane();

    ComboBox<String> mouseButtonCBox = new ComboBox<>(mouseButtons);
    mouseButtonCBox.getSelectionModel().clearAndSelect(0);

    Label cBoxLabel = new Label("Mouse button");
    cBoxLabel.setLabelFor(mouseButtonCBox);
    cBoxLabel.setWrapText(true);
    cBoxLabel.setOnMouseClicked(event -> root.requestFocus());

    Spinner<Integer> intervalSpinner = new Spinner<>(
      new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 20000, 500, 5));
    intervalSpinner.setEditable(true);
    intervalSpinner.valueProperty().addListener((obs, prev, curr) -> {
      wasValueChanged = true;
    });

    Label spinnerLabel = new Label("Interval (ms)");
    spinnerLabel.setLabelFor(intervalSpinner);
    spinnerLabel.setWrapText(true);
    spinnerLabel.setOnMouseClicked(event -> root.requestFocus());

    startStopButton.selectedProperty().addListener(new ChangeListener<>() {
      private final Timeline runningTimeline = new Timeline(200);
      private final Robot robot = new Robot();

      {
        runningTimeline.setCycleCount(Animation.INDEFINITE);
        runningTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1)));
        runningTimeline.stop();
      }

      @Override
      public void changed(ObservableValue<? extends Boolean> obs, Boolean prev, Boolean curr) {
        mouseButtonCBox.setDisable(curr);
        intervalSpinner.setDisable(curr);

        if (curr) {
          List<KeyFrame> keyFrames = runningTimeline.getKeyFrames();
          MouseButton btn = switch (mouseButtonCBox.getSelectionModel().getSelectedItem()) {
            case "Left" -> MouseButton.PRIMARY;
            case "Right" -> MouseButton.SECONDARY;
            case "Middle" -> MouseButton.MIDDLE;
            default -> throw new RuntimeException("*looks at combobox\nwait, that's illegal");
          };
          keyFrames.clear();
          keyFrames.add(new KeyFrame(
            Duration.millis(intervalSpinner.getValue()),
            event -> robot.mouseClick(btn)
          ));
          runningTimeline.play();
        } else {
          runningTimeline.stop();
        }
      }
    });

    root.setHgap(5.0);
    root.setVgap(2.5);
    root.setPadding(new Insets(5.0));

    root.add(cBoxLabel, 0, 0);
    root.add(mouseButtonCBox, 1, 0);
    root.add(spinnerLabel, 0, 1);
    root.add(intervalSpinner, 1, 1);
    root.add(startStopButton, 0, 2, 2, 1);

    stage.setOnCloseRequest(event -> {
      Platform.exit();
      System.exit(0);
    });

    stage.setScene(new Scene(root));
    stage.setTitle("ClickBot");
    stage.show();
  }
}
