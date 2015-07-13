package org.auvua.agent.tasks;

import java.util.function.Supplier;

import org.auvua.reactive.core.RxCondition;

public class TaskCondition extends RxCondition {
  
  private Supplier<Boolean> taskSupplier;
  
  public TaskCondition(Supplier<Boolean> supplier) {
    super(false);
    taskSupplier = supplier;
  }
  
  public void start() {
    setSupplier(taskSupplier);
  }
  
  public void stop() {
    setSupplier(() -> false);
  }
}
