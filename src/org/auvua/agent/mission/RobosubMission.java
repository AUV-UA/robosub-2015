package org.auvua.agent.mission;

import jama.Matrix;

import org.auvua.agent.control.Timer;
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
  public double rollStart = 0;
  
  public RobosubMission(DangerZona robot) {
    this.robot = robot;
    
    RxVar<Matrix> noMove = R.var(new Matrix(new double[][] {
        {0, 0, 0}
    }).transpose());
    
    RxVar<Matrix> forward = R.var(new Matrix(new double[][] {
        {0, 70, 0}
    }).transpose());
    
    RxVar<Matrix> afterPortalMarker = R.var(new Matrix(new double[][] {
        {-10, 70, 0}
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
    
    MaintainDepth buoyDepthDescent = new MaintainDepth(robot, R.var(1.75), .05);
    MaintainDepth buoyDepth = new MaintainDepth(robot, R.var(1.75), .05);
    MaintainDepth portalDepthDescent = new MaintainDepth(robot, R.var(2.35), .05);
    MaintainDepth portalDepth = new MaintainDepth(robot, R.var(2.35), .05);
    MaintainDepth postDepthInit = new MaintainDepth(robot, R.var(1.75), .05);
    MaintainDepth postDepth = new MaintainDepth(robot, R.var(1.75), .05);
    MaintainDepth surface = new MaintainDepth(robot, R.var(0.1), .05);
    
    Translate afterMarker1Move = new Translate(robot, forward, 15, MotionMode.RELATIVE);
    Translate preBarrelRoll = new Translate(robot, forward, 12, MotionMode.RELATIVE);
    Translate barrelRoll = new Translate(robot, forwardSlow, 2 * Math.PI, MotionMode.RELATIVE);
    Translate afterBarrelRoll = new Translate(robot, afterManeuver, 1, MotionMode.RELATIVE);
    Translate afterMarker3Move = new Translate(robot, afterPortalMarker, 15, MotionMode.RELATIVE);
    Translate afterMarker4Move = new Translate(robot, forward, 15, MotionMode.RELATIVE);
    Translate afterMarker5Move = new Translate(robot, forward, 3, MotionMode.RELATIVE);
    Translate still = new Translate(robot, noMove, 300, MotionMode.RELATIVE);
    
    OrientRobot slowFlip = new OrientRobot(robot, roll, MotionMode.RELATIVE);
    
    SearchMoveAndAlign sma1 = new SearchMoveAndAlign(robot, forward, 120, MotionMode.RELATIVE);
    SearchMoveAndAlign sma2 = new SearchMoveAndAlign(robot, forward, 120, MotionMode.RELATIVE);
    SearchMoveAndAlign sma3 = new SearchMoveAndAlign(robot, forward, 120, MotionMode.RELATIVE);
    SearchMoveAndAlign sma4 = new SearchMoveAndAlign(robot, forward, 120, MotionMode.RELATIVE);
    SearchMoveAndAlign sma5 = new SearchMoveAndAlign(robot, forward, 120, MotionMode.RELATIVE);
    
    // After initial descent
    buoyDepthDescent.atDepth.triggers(() -> {
      buoyDepthDescent.stop();
      buoyDepth.start();
      sma1.start();
    });
    
    // Move forward from marker 1
    sma1.aligned.triggers(() -> {
      sma1.stop();
      afterMarker1Move.start();
    });
    
    // Search and align to second marker, also hit buoy
    afterMarker1Move.finished.triggers(() -> {
      afterMarker1Move.stop();
      sma2.start();
    });
    
    // Descend to portal depth
    sma2.aligned.triggers(() -> {
      sma2.stop();
      buoyDepth.stop();
      portalDepthDescent.start();
    });
    
    // Maintain portal depth and move forward slightly
    portalDepthDescent.atDepth.triggers(() -> {
      portalDepthDescent.stop();
      portalDepth.start();
      preBarrelRoll.start();
    });
    
    // Do a barrel roll! (Through the portal)
    preBarrelRoll.finished.triggers(() -> {
      preBarrelRoll.stop();
      rollStart = Timer.getInstance().get();
      barrelRoll.start();
      slowFlip.start();
    });
    
    // Search and align to marker 3 (after time portal)
    barrelRoll.finished.triggers(() -> {
      barrelRoll.stop();
      slowFlip.stop();
      portalDepth.stop();
      postDepthInit.start();
    });
    
    // Maintain depth for marker tracking and search
    postDepthInit.atDepth.triggers(() -> {
      postDepthInit.stop();
      postDepth.start();
      sma3.start();
    });
    
    // Move forward from marker 3 slightly
    sma3.aligned.triggers(() -> {
      sma3.stop();
      afterMarker3Move.start();
    });
    
    // Search and align to marker 4 (bin marker)
    afterMarker3Move.finished.triggers(() -> {
      afterMarker3Move.stop();
      sma4.start();
    });
    
    // Move forward from marker 4 slightly
    sma4.aligned.triggers(() -> {
      sma4.stop();
      afterMarker4Move.start();
    });

    // Search and align to marker 5 (octagon marker)
    afterMarker4Move.finished.triggers(() -> {
      afterMarker4Move.stop();
      sma5.start();
    });
    
    // Scoot back
    sma5.aligned.triggers(() -> {
      sma5.stop();
      afterMarker5Move.start();
    });
    
    // Surface in Octagon!
    afterMarker5Move.finished.triggers(() -> {
      afterMarker5Move.stop();
      still.start();
      postDepth.stop();
      surface.start();
    });
    
    startTask = buoyDepthDescent;
  }

  @Override
  public Task getStartTask() {
    return startTask;
  }

}
