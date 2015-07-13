package org.auvua.model.component;

import java.util.function.Supplier;

import javax.vecmath.Vector3d;

import org.auvua.agent.control.Timer;
import org.auvua.reactive.core.RxVar;

public class VectorIntegrator extends RxVar<Vector3d> {

  private double lastVariableValue;
  
  public VectorIntegrator(Supplier<Vector3d> integrand) {
    this(integrand, Timer.getInstance(), new Vector3d());
  }

  public VectorIntegrator(Supplier<Vector3d> integrand, Supplier<Double> variable) {
    this(integrand, variable, new Vector3d());
  }

  public VectorIntegrator(Supplier<Vector3d> integrand, Supplier<Double> variable, Vector3d init) {
    this.lastVariableValue = variable.get();
    this.setNoSync(init);
    
    this.setSupplier(() -> {
      double currVariableValue = variable.get();
      Vector3d toAdd = new Vector3d();
      toAdd.scale(currVariableValue - lastVariableValue, integrand.get());
      Vector3d integral = this.peek();
      integral.add(toAdd);
      lastVariableValue = currVariableValue;
      return integral;
    });
  }

}
