package org.auvua.agent.tasks;


import jama.Matrix;

import org.auvua.model.dangerZona.DangerZona;
import org.auvua.reactive.core.RxVar;

public class OrientRobot extends AbstractTask {

  private RxVar<Matrix> orientation;
  private DangerZona robot;

  public OrientRobot(DangerZona robot, RxVar<Matrix> orientation) {
    this.robot = robot;
    this.orientation = orientation;
  }
  
  @Override
  public void initialize() {
    robot.orientationController.orientation.setSupplier(orientation);
  }

  @Override
  public void terminate() {
  }

}
