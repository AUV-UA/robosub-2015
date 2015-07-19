package org.auvua.agent.tasks;

import java.util.function.Supplier;

import jama.Matrix;

import org.auvua.agent.control.PidController;
import org.auvua.agent.signal.Differentiator;
import org.auvua.agent.signal.MovingAverageExponential;
import org.auvua.model.dangerZona.DangerZona;
import org.auvua.model.dangerZona.DangerZonaOutputs;
import org.auvua.reactive.core.RxVar;

public class MaintainDepth extends AbstractTask {
  
  private DangerZona robot;
  private RxVar<Double> desiredDepth;
  private Supplier<Matrix> vectorSupplier;
  private DangerZonaOutputs outputs;
  public TaskCondition atDepth;

  public MaintainDepth(DangerZona robot, RxVar<Double> desiredDepth, double error) {
    this.robot = robot;
    this.desiredDepth = desiredDepth;
    this.outputs = robot.hardware.getOutputs();
    this.atDepth = createCondition("atDepth", () -> {
      return Math.abs(desiredDepth.get() - outputs.depthSensor.get()) < error;
    });
  }
  
  @Override
  public void initialize() {
    Supplier<Double> desiredVelocity = () -> -(desiredDepth.get() - outputs.depthSensor.get()) * .5;
    Supplier<Double> velocity = new Differentiator(new MovingAverageExponential(() -> -outputs.depthSensor.get(), 0.1));
    
    PidController controller = new PidController(velocity, desiredVelocity, 100, 1, 10);
    
    vectorSupplier = () -> {
      return new Matrix(new double[][] {
          { 0, 0, 1 }
      }).transpose().times(controller.get());
    };
    
    robot.motionController.force.addSupplier(vectorSupplier);
  }

  @Override
  public void terminate() {
    robot.motionController.force.removeSupplier(vectorSupplier);
  }

}
