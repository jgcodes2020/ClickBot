package jgcodes.clickbot.util;

import java.util.TimerTask;

/**
 * A {@code TimerTask} that runs a {@code Runnable} when run.
 * Made to allow the use of lambda expressions for timers.
 */
public class RunnableTimerTask extends TimerTask {
  private final Runnable r;

  /**
   * Creates a new TimerTask for the given runnable.
   * @param r the Runnable to run
   */
  public RunnableTimerTask(Runnable r) {
    this.r = r;
  }

  @Override
  public void run() {
    r.run();
  }
}
