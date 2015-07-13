package org.auvua.agent.tasks;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.auvua.model.Component;
import org.auvua.reactive.core.RxCondition;

public interface Task extends Component {
  public static final Set<Task> runningTasks = new HashSet<Task>();
  
  public void start();
  public void stop();
  
  public Runnable begin();
  public Runnable end();
  
  public RxCondition getCondition(String name);
  public void setCondition(String name, TaskCondition condition);
  
  public Map<String, TaskCondition> getConditionDictionary();
  
  public static Runnable start(Task task) {
    return () -> task.start();
  }
  
  public static Runnable stop(Task task) {
    return () -> task.stop();
  }
  
  public static void stopAll() {
    runningTasks.forEach((task) -> {
      task.stop();
    });
    runningTasks.clear();
  };
}
