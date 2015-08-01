package org.auvua.agent.signal;

import jama.Matrix;

import java.util.function.Supplier;

import org.auvua.agent.control.Timer;
import org.auvua.reactive.core.RxVar;

public class MatrixDifferentiator extends RxVar<Matrix> {
	
	private Matrix lastDepValue;
	private double lastIndepValue;
	private Matrix derivative;
	
	public MatrixDifferentiator(Supplier<Matrix> dependent) {
	  this(dependent, Timer.getInstance());
	}
	
	public MatrixDifferentiator(Supplier<Matrix> dependent, Supplier<Double> independent) {
	  this.lastDepValue = dependent.get();
	  this.lastIndepValue = independent.get();
	  this.derivative = new Matrix(dependent.get().getRowDimension(), dependent.get().getColumnDimension());

	  this.setSupplier(() -> {
	    Matrix currDepValue = dependent.get();
	    double currIndepValue = independent.get();
	    if (currIndepValue == lastIndepValue) return new Matrix(dependent.get().getRowDimension(), dependent.get().getColumnDimension());
	    Matrix derivative = currDepValue.minus(lastDepValue).times(1 / (currIndepValue - lastIndepValue));
	    this.derivative = derivative;
	    this.lastDepValue = currDepValue;
	    this.lastIndepValue = currIndepValue;
	    return this.derivative;
	  });
	}
}
