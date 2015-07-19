package org.auvua.agent.tasks;

import jama.Matrix;

import java.util.function.Supplier;

import org.auvua.agent.control.Timer;
import org.auvua.model.dangerZona.DangerZona;

public class Translate extends AbstractTask {

  private DangerZona robot;
  private Supplier<Matrix> vectorSupplier;
  public TaskCondition finished;
  private double startTime;

  public Translate(DangerZona robot, Supplier<Matrix> vectorSupplier, double time) {
    this.robot = robot;
    this.vectorSupplier = vectorSupplier;
    this.finished = createCondition("finished", () -> {
      return Timer.getInstance().get() > startTime + time;
    });
  }
  
  @Override
  public void initialize() {
    startTime = Timer.getInstance().get();
    robot.motionController.force.addSupplier(vectorSupplier);
  }

  @Override
  public void terminate() {
    robot.motionController.force.removeSupplier(vectorSupplier);
  }

}
