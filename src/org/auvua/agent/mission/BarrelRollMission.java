package org.auvua.agent.mission;

import jama.Matrix;

import org.auvua.agent.control.Timer;
import org.auvua.agent.task.MaintainDepth;
import org.auvua.agent.task.MotionMode;
import org.auvua.agent.task.OrientRobot;
import org.auvua.agent.task.Task;
import org.auvua.agent.task.Translate;
import org.auvua.model.dangerZona.DangerZona;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;

public class BarrelRollMission implements Mission {
  
  public final DangerZona robot;
  private Task startTask;
  public double rollStart = 0;
  
  public BarrelRollMission(DangerZona robot) {
    this.robot = robot;
    
    RxVar<Matrix> noMove = R.var(new Matrix(new double[][] {
        {0, 0, 0}
    }).transpose());
    
    RxVar<Matrix> forward = R.var(new Matrix(new double[][] {
        {0, 70, 0}
    }).transpose());
    
    RxVar<Matrix> forwardSlow = R.var(new Matrix(new double[][] {
        {-10, 40, 0}
    }).transpose());
    
    RxVar<Matrix> afterManeuver = R.var(new Matrix(new double[][] {
        {-20, 0, 0}
    }).transpose());
    
    RxVar<Double> dt = R.var(() -> 1.0 * (Timer.getInstance().get() - rollStart));
    
    RxVar<Matrix> roll = R.var(() -> {
      return new Matrix(new double[][] {
        {Math.cos(dt.get()), 0, -Math.sin(dt.get())},
        {0, 1, 0},
        {Math.sin(dt.get()), 0, Math.cos(dt.get())}
        }).transpose();
    });
    
    MaintainDepth descend = new MaintainDepth(robot, R.var(0.3), .05);
    MaintainDepth maintain = new MaintainDepth(robot, R.var(0.3), .05);
    
    Translate preBarrelRoll = new Translate(robot, forward, 1, MotionMode.RELATIVE);
    Translate barrelRoll = new Translate(robot, forwardSlow, 2 * Math.PI, MotionMode.RELATIVE);
    Translate afterBarrelRoll = new Translate(robot, afterManeuver, 1, MotionMode.RELATIVE);
    Translate still = new Translate(robot, noMove, 60, MotionMode.RELATIVE);
    
    OrientRobot slowFlip = new OrientRobot(robot, roll, MotionMode.RELATIVE);
    
    descend.atDepth.triggers(() -> {
      descend.stop();
      maintain.start();
      preBarrelRoll.start();
    });
    
    preBarrelRoll.finished.triggers(() -> {
      preBarrelRoll.stop();
      rollStart = Timer.getInstance().get();
      barrelRoll.start();
      slowFlip.start();
    });
    
    barrelRoll.finished.triggers(() -> {
      barrelRoll.stop();
      slowFlip.stop();
      afterBarrelRoll.start();
    });
    
    afterBarrelRoll.finished.triggers(() -> {
      afterBarrelRoll.stop();
      still.start();
    });
    
    startTask = descend;
  }

  @Override
  public Task getStartTask() {
    return startTask;
  }

}
