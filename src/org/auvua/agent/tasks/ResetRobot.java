package org.auvua.agent.tasks;

import jama.Matrix;

import org.auvua.model.dangerZona.DangerZona;

public class ResetRobot extends AbstractTask {
  
  private DangerZona robot;
  
  public ResetRobot(DangerZona robot) {
    this.robot = robot;
  }

  @Override
  public void initialize() {
    robot.calcKinematics.get().orientation.setMatrix(Matrix.identity(3, 3));
  }

  @Override
  public void terminate() {
    // TODO Auto-generated method stub
    
  }

}
