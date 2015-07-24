package org.auvua.agent.tasks;

import jama.Matrix;

import java.util.function.Supplier;

import org.auvua.agent.control.Timer;
import org.auvua.model.dangerZona.DangerZona;

public class Translate extends AbstractTask {

  private MotionMode mode;
  private DangerZona robot;
  private Supplier<Matrix> preSupplier;
  private Supplier<Matrix> postSupplier;
  public TaskCondition finished;
  private double startTime;

  public Translate(DangerZona robot, Supplier<Matrix> preSupplier, double time) {
    this.mode = MotionMode.ABSOLUTE;
    this.robot = robot;
    this.preSupplier = preSupplier;
    this.finished = createCondition("finished", () -> {
      return Timer.getInstance().get() > startTime + time;
    });
  }
  
  public Translate(DangerZona robot, Supplier<Matrix> preSupplier, double time, MotionMode mode) {
    this.mode = mode;
    this.robot = robot;
    this.preSupplier = preSupplier;
    this.finished = createCondition("finished", () -> {
      return Timer.getInstance().get() > startTime + time;
    });
  }
  
  @Override
  public void initialize() {
    startTime = Timer.getInstance().get();

    Matrix or = robot.calcKinematics.get().orientation.asMatrix();
    
    postSupplier = () -> {
      Matrix pre = preSupplier.get();
      if (this.mode == MotionMode.RELATIVE) {
        return or.times(pre);
      }
      return pre;
    };
    
    robot.motionController.force.addSupplier(postSupplier);
  }

  @Override
  public void terminate() {
    robot.motionController.force.removeSupplier(postSupplier);
  }

}
