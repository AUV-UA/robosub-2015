package org.auvua.agent.signal;

import jama.Matrix;

import java.util.function.Supplier;

import org.auvua.agent.control.Timer;
import org.auvua.reactive.core.RxVar;

public class MatrixIntegrator extends RxVar<Matrix> {

  private Matrix integral;
  private double lastVariableValue;
  
  public MatrixIntegrator(Supplier<Matrix> integrand) {
    this(integrand, Timer.getInstance());
  }

  public MatrixIntegrator(Supplier<Matrix> integrand, Supplier<Double> variable) {
    this(integrand, variable, new Matrix(integrand.get().getRowDimension(), integrand.get().getColumnDimension()));
  }

  public MatrixIntegrator(Supplier<Matrix> integrand, Supplier<Double> variable, Matrix init) {
    this.lastVariableValue = variable.get();
    this.setNoSync(init);
    
    this.setSupplier(() -> {
      double currVariableValue = variable.get();
      integral = this.peek();
      integral.plusEquals(integrand.get().times(currVariableValue - lastVariableValue));
      lastVariableValue = currVariableValue;
      return integral;
    });
  }

}
