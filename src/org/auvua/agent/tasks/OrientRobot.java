package org.auvua.agent.tasks;

import java.util.function.Supplier;

import javax.vecmath.Vector3d;

import jama.Matrix;

import org.auvua.agent.control.Timer;
import org.auvua.model.dangerZona.DangerZona;
import org.auvua.model.motion.DzMotionTranslator;
import org.auvua.model.motion.DzMotionTranslator.Rotation;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;

public class OrientRobot extends AbstractTask {

  private RxVar<Matrix> orientation;
  private DangerZona robot;
  private Supplier<Matrix> forceSupplier;
  private Supplier<Matrix> torqueSupplier;

  public OrientRobot(DangerZona robot, RxVar<Matrix> orientation) {
    this.robot = robot;
    this.orientation = orientation;
  }
  
  @Override
  public void initialize() {
    RxVar<Matrix> robotOrientation = R.var(() -> {
      return robot.calcKinematics.get().orientation.asMatrix();
    });
    
    torqueSupplier = () -> {
      Rotation r = DzMotionTranslator.getRotation(robotOrientation.get(), orientation.get());
      
      Vector3d angVelVec = robot.calcKinematics.get().angVel;
      Matrix angVel = new Matrix(new double[][] {
          {angVelVec.x, angVelVec.y, angVelVec.z}
      }).transpose();
      
      Matrix angVelDesired = r.vector.times(r.angle * 5);
      Matrix error = angVelDesired.minus(angVel);
      
      Matrix out = error.times(1);
      
      return out;
    };
    
    forceSupplier = () -> {
      Timer.getInstance().get();
      return new Matrix(new double[][] {
          { 0, 0, 0 }
      }).transpose();
    };
    
    robot.motionController.torque.addSupplier(torqueSupplier);
    robot.motionController.force.addSupplier(forceSupplier);
  }

  @Override
  public void terminate() {
    robot.motionController.torque.removeSupplier(torqueSupplier);
    robot.motionController.force.removeSupplier(forceSupplier);
  }

}
