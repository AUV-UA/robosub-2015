package org.auvua.agent.task;

public class CompositeTask extends AbstractTask {
  
  Task[] tasks;
  
  public CompositeTask(Task ... tasks) {
    this.tasks = tasks;
  }

  @Override
  public void initialize() {
    for (Task task : tasks) {
      task.start();
    }
  }

  @Override
  public void terminate() {
    for (Task task : tasks) {
      task.stop();
    }
  }

}
