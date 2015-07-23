package org.auvua.agent.tasks;

import jama.Matrix;

import org.auvua.model.dangerZona.DangerZona;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;

public class GateMission implements Mission {
  
  public final DangerZona robot;
  private Task startTask;
  
  public GateMission(DangerZona robot) {
    this.robot = robot;
    
    RxVar<Matrix> translate = R.var(new Matrix(new double[][] {
        {0, 10, 0}
    }).transpose());
    
    MaintainDepth depth1 = new MaintainDepth(robot, R.var(1.0), .05);
    MaintainDepth depth2 = new MaintainDepth(robot, R.var(1.0), .05);
    MaintainDepth depth3 = new MaintainDepth(robot, R.var(0.0), .05);
    
    Translate move1 = new Translate(robot, translate, 5, MotionMode.RELATIVE);
    Translate move2 = new Translate(robot, translate, 5, MotionMode.RELATIVE);
    
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
      sma1.start();
    });
    
    sma2.aligned.triggers(() -> {
      sma2.stop();
      move2.start();
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
