package org.auvua.agent.tasks;

import javax.vecmath.Vector3d;

import jama.Matrix;

import org.auvua.agent.control.Timer;
import org.auvua.model.dangerZona.DangerZona;
import org.auvua.model.dangerZona.DzMotionTranslator;
import org.auvua.model.dangerZona.DzMotionTranslator.Rotation;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;

public class OrientRobot extends AbstractTask {

  private RxVar<Matrix> orientation;
  private DangerZona robot;

  public OrientRobot(DangerZona robot, RxVar<Matrix> orientation) {
    this.robot = robot;
    this.orientation = orientation;
  }
  
  @Override
  public void initialize() {
    robot.calcKinematics.get().orientation.asMatrix3d();
    
    RxVar<Matrix> robotOrientation = R.var(() -> {
      Timer.getInstance().get();
      return robot.calcKinematics.get().orientation.asMatrix();
    });
    
    robot.motionController.torque.addSupplier(() -> {
      Rotation r = DzMotionTranslator.getRotation(robotOrientation.get(), orientation.get());
      
      Vector3d angVelVec = robot.calcKinematics.get().angVel;
      Matrix angVel = new Matrix(new double[][] {
          {angVelVec.x, angVelVec.y, angVelVec.z}
      }).transpose();
      
      Matrix angVelDesired = r.vector.times(r.angle * 5);
      Matrix error = angVelDesired.minus(angVel);
      
      Matrix out = error.times(1);
      
      return out;
    });
    
    robot.motionController.force.addSupplier(() -> {
      Timer.getInstance().get();
      return new Matrix(new double[][] {
          { 0, 0, 0 }
      }).transpose();
    });
  }

  @Override
  public void terminate() {
    
  }

}
