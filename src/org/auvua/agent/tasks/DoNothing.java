package org.auvua.agent.tasks;

import org.auvua.model.dangerZona.DangerZona;

public class DoNothing extends AbstractTask {
  
  private DangerZona robot;

  public DoNothing(DangerZona robot) {
    this.robot = robot;
  }
  @Override
  public void initialize() {
    robot.motionController.stop();
    robot.orientationController.stop();
  }
  @Override
  public void terminate() {
    robot.motionController.start();
    robot.orientationController.start();
  }

}
