package org.auvua.reactive.core;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

public class RxAccumulator<E> extends StandardDependency implements Supplier<E> {
  
  private E identity;
  private BinaryOperator<E> accumulator;
  private Set<Supplier<E>> suppliers = new HashSet<Supplier<E>>();
  private Var<E> var = new Var<E>();
  
  public RxAccumulator(E identity, BinaryOperator<E> accumulator) {
    this.identity = identity;
    this.accumulator = accumulator;
    if(R.isDetectingNewDependencies()) {
      R.addNewDependency(this);
    } 
    var.set(null);
  }
  
  public void addSupplier(Supplier<E> supplier) {
    this.clear();
    suppliers.add(supplier);
    determineDependencies();
  }
  
  public void removeSupplier(Supplier<E> supplier) {
    this.clear();
    suppliers.remove(supplier);
    determineDependencies();
  }
  
  public void removeAllSuppliers() {
    this.clear();
    suppliers.clear();
    determineDependencies();
  }

  @Override
  public void update() {
    synchronized(this) {
      E accum = identity;
      for (Supplier<E> supplier : suppliers) {
        accum = accumulator.apply(accum, supplier.get());
      }
      var.set(accum);
      finishUpdate();
    }
  }

  @Override
  public void awaitUpdate() {
    synchronized(this) {
      while(isUpdating()) {
        try {
          this.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  @Override
  public E get() {
    if(R.isDetectingGets()) {
      R.addThreadLocalGetDependency(this);
    }
    return var.get();
  }

}
