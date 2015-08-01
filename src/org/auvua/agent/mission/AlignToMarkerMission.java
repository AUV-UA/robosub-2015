package org.auvua.agent.mission;

import jama.Matrix;

import org.auvua.agent.task.MaintainDepth;
import org.auvua.agent.task.MotionMode;
import org.auvua.agent.task.OrientRobot;
import org.auvua.agent.task.SearchMoveAndAlign;
import org.auvua.agent.task.Task;
import org.auvua.agent.task.Translate;
import org.auvua.model.dangerZona.DangerZona;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;

public class AlignToMarkerMission implements Mission {
  
  public final DangerZona robot;
  private Task startTask;
  
  public AlignToMarkerMission(DangerZona robot) {
    this.robot = robot;
    
    RxVar<Matrix> translate = R.var(new Matrix(new double[][] {
        {0, 40, 0}
    }).transpose());
    
    MaintainDepth depth1 = new MaintainDepth(robot, R.var(0.4), .05);
    MaintainDepth depth2 = new MaintainDepth(robot, R.var(0.4), .05);
    
    SearchMoveAndAlign sma1 = new SearchMoveAndAlign(robot, translate, 60, MotionMode.RELATIVE);
    
    depth1.atDepth.triggers(() -> {
      depth1.stop();
      depth2.start();
      sma1.start();
    });
    
    startTask = depth1;
  }

  @Override
  public Task getStartTask() {
    return startTask;
  }

}
