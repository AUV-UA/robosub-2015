package org.auvua.agent.mission;

import jama.Matrix;

import org.auvua.agent.tasks.CompositeTask;
import org.auvua.agent.tasks.MaintainDepth;
import org.auvua.agent.tasks.MotionMode;
import org.auvua.agent.tasks.Task;
import org.auvua.agent.tasks.Translate;
import org.auvua.model.dangerZona.DangerZona;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;

public class SitStillMission implements Mission {
  
  public final DangerZona robot;
  private Task startTask;
  
  public SitStillMission(DangerZona robot) {
    this.robot = robot;
    
    RxVar<Matrix> translate = R.var(new Matrix(new double[][] {
        {0, 0, 0}
    }).transpose());
    
    MaintainDepth depth1 = new MaintainDepth(robot, R.var(0.5), .05);
    
    Translate still = new Translate(robot, translate, 120, MotionMode.ABSOLUTE);
    
    startTask = new CompositeTask(still, depth1);
  }

  @Override
  public Task getStartTask() {
    return startTask;
  }

}
