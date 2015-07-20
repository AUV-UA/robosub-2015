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
    
    //OrientRobot orient = new OrientRobot(robot, orientation);
    MaintainDepth depth1 = new MaintainDepth(robot, R.var(1.5), .05);
    MaintainDepth depth2 = new MaintainDepth(robot, R.var(1.5), .05);
    MaintainDepth depth3 = new MaintainDepth(robot, R.var(0.0), .05);
    
    SearchForMarker markerSearch1 = new SearchForMarker(robot);
    SearchForMarker markerSearch2 = new SearchForMarker(robot);
    SearchForMarker markerSearch3 = new SearchForMarker(robot);
    
    AlignToMarker markerAlign1 = new AlignToMarker(robot);
    AlignToMarker markerAlign2 = new AlignToMarker(robot);
    AlignToMarker markerAlign3 = new AlignToMarker(robot);
    
    Translate move1 = new Translate(robot, translate, 60, MotionMode.RELATIVE);
    Translate move2 = new Translate(robot, translate, 5, MotionMode.RELATIVE);
    Translate move3 = new Translate(robot, translate, 60, MotionMode.RELATIVE);
    Translate move4 = new Translate(robot, translate, 5, MotionMode.RELATIVE);
    Translate move5 = new Translate(robot, translate, 60, MotionMode.RELATIVE);
    Translate move6 = new Translate(robot, translate, 5, MotionMode.RELATIVE);
    
    depth1.atDepth.triggers(() -> {
      depth1.stop();
      depth2.start();
      move1.start();
      markerSearch1.start();
    });
    
    markerSearch1.markerFound.triggers(() -> {
      move1.stop();
      markerSearch1.stop();
      markerAlign1.start();
    });
    
    markerAlign1.aligned.triggers(() -> {
      markerAlign1.stop();
      move2.start();
    });
    
    move2.finished.triggers(() -> {
      move2.stop();
      move3.start();
      markerSearch2.start();
    });
    
    markerSearch2.markerFound.triggers(() -> {
      move3.stop();
      markerSearch2.stop();
      markerAlign2.start();
    });
    
    markerAlign2.aligned.triggers(() -> {
      markerAlign2.stop();
      move4.start();
    });
    
    move4.finished.triggers(() -> {
      move4.stop();
      move5.start();
      markerSearch3.start();
    });
    
    markerSearch3.markerFound.triggers(() -> {
      move5.stop();
      markerSearch3.stop();
      markerAlign3.start();
    });
    
    startTask = new CompositeTask(depth1);
  }

  @Override
  public Task getStartTask() {
    return startTask;
  }

}
