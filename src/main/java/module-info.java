module jgcodes.clickbot {
  // Used to input mouse clicks natively, since the AWT robot is less picky about threads
  requires java.desktop;
  // Used to disable logging from JNativeHook
  requires java.logging;
  // Used to save hotkeys
  requires java.prefs;
  // Used as the core GUI toolkit
  requires javafx.controls;
  // Used to track native keystrokes
  requires jnativehook;
  requires org.apache.commons.lang3;


  exports jgcodes.clickbot;
}