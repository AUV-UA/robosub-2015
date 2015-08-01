package org.auvua.agent.control;
import jama.Matrix;

import java.util.function.Supplier;

import org.auvua.agent.signal.MatrixDifferentiator;
import org.auvua.agent.signal.MatrixIntegrator;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;

public class MatrixPidController extends RxVar<Matrix> {
  
  private double feedForward = 0;
  public final RxVar<Matrix> error;
  public final RxVar<Matrix> proportional;
  public final RxVar<Matrix> integral;
  public final RxVar<Matrix> derivative;
  private final int rows;
  private final int columns;
  
  public MatrixPidController(Supplier<Matrix> processVar, Supplier<Matrix> targetVar, double kp, double ki, double kd) {
    Timer time = Timer.getInstance();
    rows = processVar.get().getRowDimension();
    columns = processVar.get().getColumnDimension();
    this.setNoSync(new Matrix(rows, columns));
    
    error = R.var(() -> targetVar.get().minus(processVar.get()));
    proportional = error;
    integral = new MatrixIntegrator(error, time);
    derivative = new MatrixDifferentiator(error, time);
    
    this.setSupplier(() -> {
      Matrix p = proportional.get().times(kp);
      Matrix i = integral.get().times(ki);
      Matrix d = derivative.get().times(kd);
      Matrix f = targetVar.get().times(feedForward);
      return p.plus(i).plus(d).plus(f);
    });
  }
}
