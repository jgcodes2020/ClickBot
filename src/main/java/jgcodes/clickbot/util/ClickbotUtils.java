package jgcodes.clickbot.util;

import javafx.scene.control.Label;

public class ClickbotUtils {
  public class PreferenceConstants {
    public static final String PREFERENCES_PATH = "/jgcodes/clickbot/prefs";
    public static final String HOTKEY_PREFERENCE = "last-hotkey";
  }

  public static Label createDialogContentLabel(String text) {
    Label label = new Label(text);
    label.setMaxWidth(Double.MAX_VALUE);
    label.setMaxHeight(Double.MAX_VALUE);
    label.getStyleClass().add("content");
    label.setWrapText(true);
    label.setPrefWidth(360);
    return label;
  }
}
