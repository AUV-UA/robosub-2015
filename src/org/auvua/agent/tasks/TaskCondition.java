package org.auvua.agent.tasks;

import java.util.function.Supplier;

import org.auvua.reactive.core.RxCondition;
import org.auvua.reactive.core.RxTaskBuilder;

public class TaskCondition extends RxCondition {
  
  private Supplier<Boolean> taskSupplier;
  
  public TaskCondition(Supplier<Boolean> supplier) {
    super(false);
    this.taskSupplier = supplier;
  }
  
  public void start() {
    setSupplier(taskSupplier);
  }
  
  public void stop() {
    setSupplier(() -> false);
  }
  
  public void triggers(Runnable ... tasks) {
    new RxTaskBuilder().when(this).then(tasks);
  }
}
