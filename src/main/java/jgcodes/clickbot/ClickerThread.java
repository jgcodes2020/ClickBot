package jgcodes.clickbot;

import jgcodes.clickbot.util.ClickbotUtils;

import java.awt.AWTException;
import java.awt.Robot;

public class ClickerThread extends Thread {
  private boolean running = true;
  private final Robot bot;
  private final long interval;
  private final int buttons;

  public ClickerThread(long interval, int buttons) throws AWTException {
    this.interval = interval;
    this.buttons = buttons;
    this.bot = new Robot();
  }

  @Override
  public void run() {
    while (running) {
      try {
        Thread.sleep(interval);
      } catch (InterruptedException e) {
        break;
      }
      bot.mousePress(buttons);
      bot.mouseRelease(buttons);
    }
  }

  public static void endClicker(ClickerThread thread) {
    thread.running = false;
    thread.interrupt();
  }
}
