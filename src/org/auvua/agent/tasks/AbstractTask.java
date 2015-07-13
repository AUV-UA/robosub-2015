package org.auvua.agent.tasks;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.auvua.model.BaseComponent;
import org.auvua.reactive.core.ReactiveDependency;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxCondition;

public abstract class AbstractTask extends BaseComponent implements Task {

  private Map<String, TaskCondition> conditionDictionary
  = new HashMap<String, TaskCondition>();
  private Collection<ReactiveDependency> newReactiveDependencies;
  private boolean started = false;

  public abstract void initialize();

  public void start() {
    if (started) return;
    runningTasks.add(this);
    started = true;
    for(String condName : conditionDictionary.keySet()) {
      conditionDictionary.get(condName).start();
    }
    R.startDetectingNewDependencies();
    initialize();
    R.stopDetectingNewDependencies();
    newReactiveDependencies = R.getNewDependenciesAndClear();
  }

  public void stop() {
    if (!started) return;
    started = false;
    for(ReactiveDependency dep : newReactiveDependencies) {
      dep.clear();
    }
    for(String condName : conditionDictionary.keySet()) {
      conditionDictionary.get(condName).stop();
    }
  }

  public TaskCondition createCondition(String name, Supplier<Boolean> supplier) {
    TaskCondition cond = new TaskCondition(supplier);
    setCondition(name, cond);
    return cond;
  }

  // Syntactic sugar for composing conditions and triggers
  public RxCondition when(String name) {
    return getCondition(name);
  }

  public RxCondition getCondition(String name) {
    RxCondition condition = conditionDictionary.get(name);
    if(condition == null) {
      throw new IllegalStateException("Condition \"" + name + "\" is undeclared for this task.");
    }
    return condition;
  }

  public void setCondition(String name, TaskCondition condition) {
    conditionDictionary.put(name, condition);
  }

  public Runnable begin() {
    return () -> this.start();
  }

  public Runnable end() {
    return () -> this.stop();
  }

  public Map<String, TaskCondition> getConditionDictionary() {
    return conditionDictionary;
  }

}