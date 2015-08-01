package org.auvua.agent.signal;

import java.util.function.Supplier;

public class MovingAverageExponential implements Supplier<Double>  {
  
  private Supplier<Double> var;
  private double average = 0;
  private double inputScale = 0.01;
  
  public MovingAverageExponential(Supplier<Double> var, double inputScale) {
    this.var = var;
    this.inputScale = inputScale;
  }

  @Override
  public Double get() {
    double value = var.get();
    average = inputScale * value + (1 - inputScale) * average;
    return average;
  }

}
