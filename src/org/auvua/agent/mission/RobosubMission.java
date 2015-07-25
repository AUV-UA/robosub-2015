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

public class RobosubMission implements Mission {
  
  public final DangerZona robot;
  private Task startTask;
  
  public RobosubMission(DangerZona robot) {
    this.robot = robot;
    
    RxVar<Matrix> translate = R.var(new Matrix(new double[][] {
        {0, 70, 0}
    }).transpose());
    
    RxVar<Matrix> flipMat = R.var(new Matrix(new double[][] {
            {-1, 0, 0},
            {0, 1, 0},
            {0, 0, -1}
        }).transpose());
    
    MaintainDepth depth1 = new MaintainDepth(robot, R.var(1.0), .05);
    MaintainDepth depth2 = new MaintainDepth(robot, R.var(1.0), .05);
    MaintainDepth portalDepth = new MaintainDepth(robot, R.var(2.5), .05);
    MaintainDepth portalDepth2 = new MaintainDepth(robot, R.var(2.5), .05);
    MaintainDepth buoyDepth = new MaintainDepth(robot, R.var(2.5), .05);
    MaintainDepth depth3 = new MaintainDepth(robot, R.var(0.3), .05);
    
    Translate move1 = new Translate(robot, translate, 5, MotionMode.RELATIVE);
    Translate move2 = new Translate(robot, translate, 120, MotionMode.RELATIVE);
    
    OrientRobot flip = new OrientRobot(robot, flipMat, MotionMode.ABSOLUTE);
    
    SearchMoveAndAlign sma1 = new SearchMoveAndAlign(robot, translate, 60, MotionMode.RELATIVE);
    SearchMoveAndAlign sma2 = new SearchMoveAndAlign(robot, translate, 60, MotionMode.RELATIVE);
    SearchMoveAndAlign sma3 = new SearchMoveAndAlign(robot, translate, 60, MotionMode.RELATIVE);
    
    depth1.atDepth.triggers(() -> {
      depth1.stop();
      depth2.start();
      sma1.start();
    });
    
    sma1.aligned.triggers(() -> {
      sma1.stop();
      move1.start();
    });
    
    move1.finished.triggers(() -> {
      move1.stop();
      sma2.start();
    });
    
    sma2.aligned.triggers(() -> {
      sma2.stop();
      portalDepth.start();
    });
    
    portalDepth.atDepth.triggers(() -> {
      portalDepth.stop();
      portalDepth2.start();
      move2.start();
      //flip.start();
    });
    
    move2.finished.triggers(() -> {
      move2.stop();
      sma3.start();
    });
    
    sma3.aligned.triggers(() -> {
      sma3.stop();
      depth2.stop();
      depth3.start();
    });
    
    startTask = depth1;
  }

  @Override
  public Task getStartTask() {
    return startTask;
  }

}
