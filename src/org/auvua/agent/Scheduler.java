package org.auvua.agent;

import java.util.Timer;
import java.util.TimerTask;

public class Scheduler {
  Timer timer = new Timer();
  
  public void schedule(Runnable runnable, long interval) {
    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        runnable.run();
      }
    };
    
    timer.scheduleAtFixedRate(task, 0, interval);
  }
}
