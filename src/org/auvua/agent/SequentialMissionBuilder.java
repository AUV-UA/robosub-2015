package org.auvua.agent;

import java.util.function.Supplier;

import org.auvua.agent.tasks.Task;
import org.auvua.reactive.core.R;

public class SequentialMissionBuilder {
  
  public static MissionTrigger when(Supplier<Boolean> var) {
    return new MissionTrigger(var);
  }
  
  public static class MissionTrigger {
    private Supplier<Boolean> predicate;
    
    public MissionTrigger(Supplier<Boolean> predicate) {
      this.predicate = predicate;
    }
    
    public MissionTrigger then(Runnable ... tasks) {
      R.task(() -> {
        if(predicate.get()) {
          for (Runnable task : tasks) {
            task.run();
          }
        }
      });
      return this;
    }
    
    public MissionTrigger start(Task ... tasks) {
      R.task(() -> {
        if(predicate.get()) {
          for (Task task : tasks) {
            task.start();
          }
        }
      });
      return this;
    }
    
    public MissionTrigger stop(Task ... tasks) {
      R.task(() -> {
        if(predicate.get()) {
          for (Task task : tasks) {
            task.stop();
          }
        }
      });
      return this;
    }
    
    public MissionTrigger restart(Task ... tasks) {
      R.task(() -> {
        if(predicate.get()) {
          for (Task task : tasks) {
            task.stop();
            task.start();
          }
        }
      });
      return this;
    }
  }
  
}
