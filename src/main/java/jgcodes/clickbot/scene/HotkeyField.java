package jgcodes.clickbot.scene;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCombination.ModifierValue;
import javafx.scene.input.KeyEvent;

public class HotkeyField extends TextField {
  private static ModifierValue toModifierState(boolean in) {
    return in? ModifierValue.DOWN : ModifierValue.UP;
  }

  private KeyCode lastKeyCode = KeyCode.UNDEFINED;
  private boolean altDown;
  private boolean shiftDown;
  private boolean ctrlDown;
  private boolean metaDown;

  public HotkeyField() {
    super();
    setEditable(false);
    this.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
      if (!event.getCode().isModifierKey()) {
        lastKeyCode = event.getCode();
      }
      altDown = event.isAltDown();
      shiftDown = event.isShiftDown();
      ctrlDown = event.isShortcutDown();
      metaDown = event.isMetaDown();

      if (lastKeyCode != KeyCode.UNDEFINED) this.setText(getValue().getDisplayText());
    });
  }
  public KeyCombination getValue() {
    return (lastKeyCode == KeyCode.UNDEFINED)?
      null :
      new KeyCodeCombination(lastKeyCode,
        toModifierState(shiftDown), toModifierState(ctrlDown), toModifierState(altDown), toModifierState(metaDown), ModifierValue.UP);
  }
}
