package org.auvua.agent.task;

import org.auvua.model.dangerZona.DangerZona;

public class DoNothing extends AbstractTask {
  
  private DangerZona robot;

  public DoNothing(DangerZona robot) {
    this.robot = robot;
  }
  @Override
  public void initialize() {
    robot.motionController.pause();
    robot.orientationController.pause();
  }
  @Override
  public void terminate() {
    robot.motionController.unpause();
    robot.orientationController.unpause();
  }

}
