package org.auvua.agent.task;


import jama.Matrix;

import org.auvua.model.dangerZona.DangerZona;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;

public class OrientRobot extends AbstractTask {

  private RxVar<Matrix> orientation;
  private DangerZona robot;
  private MotionMode mode;

  public OrientRobot(DangerZona robot, RxVar<Matrix> orientation, MotionMode mode) {
    this.robot = robot;
    this.orientation = orientation;
    this.mode = mode;
  }
  
  @Override
  public void initialize() {
    Matrix orRobot = robot.calcKinematics.get().orientation.asMatrix();
    RxVar<Matrix> or = R.var(() -> {
      Matrix orDesired = orientation.get();
      if (mode == MotionMode.ABSOLUTE) {
        return orDesired;
      } else {
        return orRobot.times(orDesired);
      }
    });
    robot.orientationController.orientation.setSupplier(or);
  }

  @Override
  public void terminate() {
    Matrix orRobot = robot.calcKinematics.get().orientation.asMatrix();
    RxVar<Matrix> or = R.var(() -> orRobot);
    robot.orientationController.orientation.setSupplier(or);
  }

}
