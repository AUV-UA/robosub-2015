package org.auvua.reactive.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import org.auvua.reactive.core.RxTaskBuilder.RxTaskTrigger;

public class R {
  private static Map<Thread, Set<ReactiveDependency>> threadToGetDependenciesMap
    = new ConcurrentHashMap<Thread, Set<ReactiveDependency>>();
  private static Map<Thread, Set<ReactiveDependency>> threadToSetDependenciesMap
    = new ConcurrentHashMap<Thread, Set<ReactiveDependency>>();
  private static Map<Thread, Set<ReactiveDependency>> threadToNewDependenciesMap
    = new ConcurrentHashMap<Thread, Set<ReactiveDependency>>();

  private static Set<Thread> threadsDetectingGets = new HashSet<Thread>();
  private static Set<Thread> threadsDetectingSets = new HashSet<Thread>();
  private static Set<Thread> threadsDetectingNewDeps = new HashSet<Thread>();
  
  private static ExecutorService executor;

  public static <E> RxVar<E> var(E value) {
    return new RxVar<E>(value);
  }

  public static <E> RxVar<E> var(Supplier<E> function) {
    return new RxVar<E>(function);
  }

  public static RxTask task(Runnable runnable) {
    return new RxTask(runnable);
  }

  public static RxTask task() {
    return new RxTask();
  }
  
  public static RxCondition cond(Supplier<Boolean> function) {
    return new RxCondition(function);
  }
  
  public static RxCondition cond(boolean b) {
    return new RxCondition(b);
  }
  
  public static <E> RxValve<E> valve(E value) {
    return new RxValve<E>(value);
  }

  public static <E> RxValve<E> valve(Supplier<E> function) {
    return new RxValve<E>(function);
  }
  
  public static void initialize(int threads) {
    executor = Executors.newFixedThreadPool(threads);
  }
  
  public static RxTaskTrigger when(RxCondition c) {
    return new RxTaskBuilder().when(c);
  }

  /**
   * Performs a runnable operation, treating all RxVar.set() operations as occurring simultaneously,
   * so as not to perform redundant reactive updates.
   * @param runnable the activity to run
   */
  public static void doSync(Runnable runnable) {
    startDetectingSets();
    runnable.run();
    stopDetectingSets();

    // First get all affected dependencies in the graph
    Set<ReactiveDependency> affectedDependencies = new LinkedHashSet<ReactiveDependency>();
    Set<ReactiveDependency> temp = new LinkedHashSet<ReactiveDependency>();
    temp.addAll(getSetDependenciesAndClear());
    while (!temp.isEmpty()) {
      Set<ReactiveDependency> nextTemp = new LinkedHashSet<ReactiveDependency>();
      for (ReactiveDependency dep : temp) {
        for (ReactiveDependency parent : dep.getParents()) {
          if (!affectedDependencies.contains(parent)) {
            nextTemp.add(parent);
          }
        }
      }
      affectedDependencies.addAll(nextTemp);
      temp = nextTemp;
    }
    
    // Then sort them by their topological ranking
    List<ReactiveDependency> dependencyQueue = new ArrayList<ReactiveDependency>(affectedDependencies);
    Collections.sort(dependencyQueue, (arg0, arg1) -> {
      return arg0.getDependencyRank() - arg1.getDependencyRank();
    });
    
    //System.out.println(dependencyQueue.size());
    if(executor != null) {
      for(ReactiveDependency dep : dependencyQueue) {
        dep.prepareUpdate();
      }
      for(ReactiveDependency dep : dependencyQueue) {
        executor.execute(dep.getUpdateRunner());
      }
      for(ReactiveDependency dep : dependencyQueue) {
        dep.awaitUpdate();
      }
    } else {
      for(ReactiveDependency dep : dependencyQueue) {
        dep.update();
      }
    }
  }

  public static void startDetectingGets() {
    Thread currThread = Thread.currentThread();
    threadsDetectingGets.add(currThread);
    threadToGetDependenciesMap.put(currThread, new HashSet<ReactiveDependency>());
  }

  public static void startDetectingSets() {
    Thread currThread = Thread.currentThread();
    threadsDetectingSets.add(currThread);
    threadToSetDependenciesMap.put(currThread, new HashSet<ReactiveDependency>());
  }
  
  public static void startDetectingNewDependencies() {
    Thread currThread = Thread.currentThread();
    threadsDetectingNewDeps.add(currThread);
    threadToNewDependenciesMap.put(currThread, new HashSet<ReactiveDependency>());
  }

  public static void stopDetectingGets() {
    threadsDetectingGets.remove(Thread.currentThread());
  }

  public static void stopDetectingSets() {
    threadsDetectingSets.remove(Thread.currentThread());
  }
  
  public static void stopDetectingNewDependencies() {
    threadsDetectingNewDeps.remove(Thread.currentThread());
  }

  public static void addThreadLocalGetDependency(ReactiveDependency dep) {
    threadToGetDependenciesMap.get(Thread.currentThread()).add(dep);
  }

  public static void addThreadLocalSetDependency(ReactiveDependency dep) {
    threadToSetDependenciesMap.get(Thread.currentThread()).add(dep);
  }
  
  public static void addNewDependency(ReactiveDependency dep) {
    threadToNewDependenciesMap.get(Thread.currentThread()).add(dep);
  }

  public static boolean isDetectingGets() {
    return threadsDetectingGets.contains(Thread.currentThread());
  }

  public static boolean isDetectingSets() {
    return threadsDetectingSets.contains(Thread.currentThread());
  }
  
  public static boolean isDetectingNewDependencies() {
    return threadsDetectingNewDeps.contains(Thread.currentThread());
  }

  public static Set<ReactiveDependency> getGetDependenciesAndClear() {
    Set<ReactiveDependency> threadLocalDependees = threadToGetDependenciesMap.get(Thread.currentThread());
    threadToGetDependenciesMap.put(Thread.currentThread(), new HashSet<ReactiveDependency>());
    if(threadLocalDependees == null) threadLocalDependees = new HashSet<ReactiveDependency>();
    return threadLocalDependees;
  }

  public static Set<ReactiveDependency> getSetDependenciesAndClear() {
    Set<ReactiveDependency> threadLocalDependees = threadToSetDependenciesMap.get(Thread.currentThread());
    threadToSetDependenciesMap.put(Thread.currentThread(), new HashSet<ReactiveDependency>());
    if(threadLocalDependees == null) threadLocalDependees = new HashSet<ReactiveDependency>();
    return threadLocalDependees;
  }
  
  public static Set<ReactiveDependency> getNewDependenciesAndClear() {
    Set<ReactiveDependency> threadLocalDependees = threadToNewDependenciesMap.get(Thread.currentThread());
    threadToNewDependenciesMap.put(Thread.currentThread(), new HashSet<ReactiveDependency>());
    if(threadLocalDependees == null) threadLocalDependees = new HashSet<ReactiveDependency>();
    return threadLocalDependees;
  }

}
