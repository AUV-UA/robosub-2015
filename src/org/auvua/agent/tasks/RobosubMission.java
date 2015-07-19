package org.auvua.agent.tasks;

import jama.Matrix;

import org.auvua.model.dangerZona.DangerZona;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;

public class RobosubMission implements Mission {
  
  public DangerZona robot = DangerZona.getInstance();
  private Task startTask;
  
  public RobosubMission() {
    RxVar<Matrix> orientation = R.var(new Matrix(new double[][] {
        {1, 0, 0},
        {0, 1, 0},
        {0, 0, 1}
    }));
    RxVar<Matrix> translate = R.var(new Matrix(new double[][] {
        {0, 10, 0}
    }).transpose());
    
    OrientRobot orient = new OrientRobot(robot, orientation);
    MaintainDepth depth1 = new MaintainDepth(robot, R.var(2.5), .05);
    MaintainDepth depth2 = new MaintainDepth(robot, R.var(0.0), .05);
    Translate move1 = new Translate(robot, translate, 5);
    
    depth1.atDepth.triggers(() -> {
      move1.start();
    });
    
    move1.finished.triggers(() -> {
      move1.stop();
      depth1.stop();
      depth2.start();
    });
    
    startTask = new CompositeTask(orient, depth1);
  }

  @Override
  public Task getStartTask() {
    return startTask;
  }

}
