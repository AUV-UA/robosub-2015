package org.auvua.agent.mission;

import jama.Matrix;

import org.auvua.agent.control.Timer;
import org.auvua.agent.task.CompositeTask;
import org.auvua.agent.task.MaintainDepth;
import org.auvua.agent.task.MotionMode;
import org.auvua.agent.task.OrientRobot;
import org.auvua.agent.task.Task;
import org.auvua.agent.task.Translate;
import org.auvua.model.dangerZona.DangerZona;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;

public class SitStillMission implements Mission {
  
  public final DangerZona robot;
  private Task startTask;
  public double rollStart = 0;
  
  public SitStillMission(DangerZona robot) {
    this.robot = robot;
    
    RxVar<Matrix> translate = R.var(new Matrix(new double[][] {
        {0, 0, 0}
    }).transpose());
    
    RxVar<Matrix> translate2 = R.var(new Matrix(new double[][] {
        {-100, 0, 0}
    }).transpose());
    
    RxVar<Matrix> noMove = R.var(new Matrix(new double[][] {
        {0, 0, 0}
    }).transpose());
    
    RxVar<Double> dt = R.var(() -> .5 *(Timer.getInstance().get() - rollStart));
    
    RxVar<Matrix> down = R.var(() -> {
      return new Matrix(new double[][] {
        {Math.cos(dt.get()), 0, -Math.sin(dt.get())},
        {0, 1, 0},
        {Math.sin(dt.get()), 0, Math.cos(dt.get())}
        }).transpose();
    });
    
    RxVar<Matrix> spin = R.var(() -> {
      return new Matrix(new double[][] {
        {Math.cos(dt.get()), -Math.sin(dt.get()), 0},
        {Math.sin(dt.get()), Math.cos(dt.get()), 0},
        {0, 0, 1}
        }).transpose();
    });
    
    RxVar<Matrix> up = R.var(new Matrix(new double[][] {
        {1, 0, 0},
        {0, 1, 0},
        {0, 0, 1}
    }).transpose());
    
    RxVar<Matrix> left = R.var(new Matrix(new double[][] {
        {0, 1, 0},
        {-1, 0, 0},
        {0, 0, 1}
    }).transpose());
    
    
    
    MaintainDepth depth1 = new MaintainDepth(robot, R.var(0.5), .05);
    Translate still = new Translate(robot, noMove, 120, MotionMode.ABSOLUTE);
    OrientRobot orient1 = new OrientRobot(robot, up, MotionMode.ABSOLUTE);
    
    startTask = new CompositeTask(orient1, depth1, still);
  }

  @Override
  public Task getStartTask() {
    return startTask;
  }

}
