package jgcodes.clickbot.dialog;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import jgcodes.clickbot.scene.HotkeyField;
import jgcodes.clickbot.util.ClickbotUtils;

/**
 * A dialog which allows the user to set a hotkey. <br><br>
 *
 * Code here was copied and modified from {@link javafx.scene.control.TextInputDialog} as the two are very similar.
 * Thanks to the OpenJFX devs for making this.
 */
public class HotkeyDialog extends Dialog<KeyCombination> {
  // Static Fields
  private final GridPane grid;
  private final Label label;
  private final HotkeyField field;
  private final String defaultValue;

  public HotkeyDialog(String defaultValue) {
    final DialogPane dialogPane = this.getDialogPane();

    this.field = new HotkeyField();
    field.setMaxWidth(Double.MAX_VALUE);
    GridPane.setHgrow(field, Priority.ALWAYS);
    GridPane.setFillWidth(field, true);

    this.label = ClickbotUtils.createDialogContentLabel(dialogPane.getContentText());
    label.setPrefHeight(Region.USE_COMPUTED_SIZE);
    label.textProperty().bind(dialogPane.contentTextProperty());

    this.defaultValue = defaultValue;

    dialogPane.contentTextProperty().addListener(o -> updateGrid());

    setTitle("Hotkey");
    dialogPane.setHeaderText("Enter a hotkey");
    dialogPane.getStyleClass().add("text-input-dialog");
    dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

    this.grid = new GridPane();
    this.grid.setHgap(10);
    this.grid.setMaxWidth(Double.MAX_VALUE);
    this.grid.setAlignment(Pos.CENTER_LEFT);

    updateGrid();

    this.setResultConverter(buttonType -> {
      final ButtonData data = (buttonType == null)? null : buttonType.getButtonData();
      return (data == ButtonData.OK_DONE)? field.getValue() : null;
    });
  }
  public HotkeyDialog() {
    this("");
  }


  private void updateGrid() {
    grid.getChildren().clear();

    grid.add(label, 0, 0);
    grid.add(field, 1, 0);
    getDialogPane().setContent(grid);

    Platform.runLater(field::requestFocus);
  }
}
