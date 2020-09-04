package jgcodes.clickbot;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jgcodes.clickbot.dialog.HotkeyDialog;
import jgcodes.clickbot.scene.FillGridPane;
import jgcodes.clickbot.util.ClickbotUtils.PreferenceConstants;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.SwingKeyAdapter;

import java.awt.AWTException;
import java.awt.event.InputEvent;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class Launcher extends Application {
  public static final ObservableList<String> mouseButtons = FXCollections.observableArrayList(
    "Left", "Middle", "Right"
  );

  private static boolean wasValueChanged = false;

  private static final ToggleButton startStopButton =
    new ToggleButton("Start clicker (F6)");
  private static final ObjectProperty<KeyCombination> toggleHotkey =
    new SimpleObjectProperty<>(new KeyCodeCombination(KeyCode.F6));

  public static void main(String[] args) {
    loadPreferences();
    launchJNativeHook();

    Application.launch(Launcher.class, args);
  }

  private static void loadPreferences() {
    Preferences prefs = Preferences.userRoot().node(PreferenceConstants.PREFERENCES_PATH);

    String shortcut = prefs.get("last-hotkey", null);
    System.out.println(shortcut);
    if (shortcut == null) {
      prefs.put("last-hotkey", "F6");
      shortcut = "F6";
    }
    toggleHotkey.set(KeyCombination.keyCombination(shortcut));
  }

  private static void launchJNativeHook() {
    Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
    logger.setLevel(Level.WARNING);
    logger.setUseParentHandlers(false);

    try {
      GlobalScreen.registerNativeHook();
    } catch (NativeHookException e) {
      e.printStackTrace();
      System.exit(-100);
    }
    GlobalScreen.addNativeKeyListener(new SwingKeyAdapter() {
      boolean ctrl = false, meta = false, shift = false, alt = false;

      @Override
      public void keyPressed(java.awt.event.KeyEvent keyEvent) {
        KeyCode jfxKeyCode = Arrays
          .stream(KeyCode.values())
          .filter(code -> code.getCode() == keyEvent.getKeyCode())
          .findFirst()
          .orElse(KeyCode.UNDEFINED);

        if (keyEvent.getKeyCode() == java.awt.event.KeyEvent.VK_CONTROL) ctrl = true;
        if (keyEvent.getKeyCode() == java.awt.event.KeyEvent.VK_ALT) alt = true;
        if (keyEvent.getKeyCode() == java.awt.event.KeyEvent.VK_SHIFT) shift = true;
        if (keyEvent.getKeyCode() == java.awt.event.KeyEvent.VK_META) meta = true;

        KeyEvent jfxKeyEvent = new KeyEvent(
          keyEvent.getSource(), null, KeyEvent.KEY_PRESSED, KeyEvent.CHAR_UNDEFINED, "",
          jfxKeyCode, shift, ctrl, alt, meta
        );

        if (toggleHotkey.get().match(jfxKeyEvent)) {
          Platform.runLater(startStopButton::fire);
        }
      }

      @Override
      public void keyReleased(java.awt.event.KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == java.awt.event.KeyEvent.VK_CONTROL) ctrl = false;
        if (keyEvent.getKeyCode() == java.awt.event.KeyEvent.VK_ALT) alt = false;
        if (keyEvent.getKeyCode() == java.awt.event.KeyEvent.VK_SHIFT) shift = false;
        if (keyEvent.getKeyCode() == java.awt.event.KeyEvent.VK_META) meta = false;
      }
    });
  }

  @Override
  public void start(Stage stage) {
    bindPreferences();

    // GUI INIT CODE
    VBox root = new VBox();

    MenuBar menuBar = new MenuBar();
    initializeMenuBar(menuBar);
    VBox.setVgrow(menuBar, Priority.NEVER);

    GridPane subRoot = new FillGridPane();
    initializeSubRoot(subRoot);
    VBox.setVgrow(subRoot, Priority.ALWAYS);

    root.getChildren().addAll(menuBar, subRoot);

    completeSetup(stage, root);
  }

  private void bindPreferences() {
    startStopButton.textProperty()
      .bind(
        Bindings.createStringBinding(() ->
          "Start clicker (" + toggleHotkey.get().getDisplayText() + ")", toggleHotkey)
      );
    toggleHotkey.addListener((obs, prev, curr) ->
      Preferences.userRoot()
      .node(PreferenceConstants.PREFERENCES_PATH)
      .put(PreferenceConstants.HOTKEY_PREFERENCE, curr.getName()));
  }
  private void initializeSubRoot(GridPane subRoot) {
    ComboBox<String> mouseButtonCBox = new ComboBox<>(mouseButtons);
    mouseButtonCBox.getSelectionModel().clearAndSelect(0);

    Label cBoxLabel = new Label("Mouse button");
    cBoxLabel.setLabelFor(mouseButtonCBox);
    cBoxLabel.setWrapText(true);
    cBoxLabel.setOnMouseClicked(event -> subRoot.requestFocus());

    Spinner<Integer> intervalSpinner = new Spinner<>(
      new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 20000, 500, 5));
    intervalSpinner.setEditable(true);
    intervalSpinner.valueProperty().addListener((obs, prev, curr) -> wasValueChanged = true);

    Label spinnerLabel = new Label("Interval (ms)");
    spinnerLabel.setLabelFor(intervalSpinner);
    spinnerLabel.setWrapText(true);
    spinnerLabel.setOnMouseClicked(event -> subRoot.requestFocus());



    startStopButton.selectedProperty().addListener(new ChangeListener<>() {
      private ClickerThread clicker;

      @Override
      public void changed(ObservableValue<? extends Boolean> obs, Boolean prev, Boolean curr) {
        mouseButtonCBox.setDisable(curr);
        intervalSpinner.setDisable(curr);

        final int button = switch (mouseButtonCBox.getSelectionModel().getSelectedItem()) {
          case "Left" -> InputEvent.BUTTON1_DOWN_MASK;
          case "Middle" -> InputEvent.BUTTON2_DOWN_MASK;
          case "Right" -> InputEvent.BUTTON3_DOWN_MASK;
          default -> throw new RuntimeException("Wha-huh? There's an option we forgot!");
        };

        if (curr) {
          try {
            clicker = new ClickerThread(intervalSpinner.getValue(), button);
          } catch (AWTException e) {
            e.printStackTrace();
          }
          clicker.start();
        }
        else {
          ClickerThread.endClicker(clicker);
          try {
            clicker.join();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    });

    subRoot.setHgap(5.0);
    subRoot.setVgap(2.5);
    subRoot.setPadding(new Insets(5.0));

    subRoot.add(cBoxLabel, 0, 0);
    subRoot.add(mouseButtonCBox, 1, 0);
    subRoot.add(spinnerLabel, 0, 1);
    subRoot.add(intervalSpinner, 1, 1);
    subRoot.add(startStopButton, 0, 2, 2, 1);
  }
  private void initializeMenuBar(MenuBar menuBar) {
    Menu optionsMenu = new Menu("Options");
    {
      MenuItem changeHotkeyItem = new MenuItem("Change hotkey");
      changeHotkeyItem.setOnAction(event -> {
        try {
          var dialog = new HotkeyDialog();
          dialog.setHeaderText("Please select a new hotkey.");
          dialog.setContentText("New hotkey");
          toggleHotkey.set(dialog.showAndWait().orElseThrow());
        }
        catch (NoSuchElementException ignored) {}
      });

      optionsMenu.getItems().addAll(
        changeHotkeyItem
      );
    }
    menuBar.getMenus().add(
      optionsMenu
    );
  }
  private void completeSetup(Stage stage, Parent root) {
    stage.setOnCloseRequest(event -> {
      Platform.exit();
      System.exit(0);
    });

    stage.setMaxWidth(390);
    stage.setMaxHeight(193);
    stage.setMinWidth(240);
    stage.setMinHeight(163);


    stage.setScene(new Scene(root));
    stage.setTitle("ClickBot");
    stage.show();
  }
}
