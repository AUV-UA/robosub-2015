package org.auvua.agent;

import java.util.function.Supplier;

import javax.vecmath.Vector3d;

import jama.Matrix;

import org.auvua.model.dangerZona.DangerZona;
import org.auvua.model.dangerZona.DzMotionTranslator;
import org.auvua.model.dangerZona.DzMotionTranslator.Rotation;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;

public class DzOrientationController {
  
  private DangerZona robot;
  private DzOrientationMode mode = DzOrientationMode.ABSOLUTE;
  
  private Supplier<Matrix> torqueSupplier;
  public final RxVar<Matrix> orientation;
  
  private boolean started = false;
  
  public DzOrientationController(DangerZona robot) {
    this.robot = robot;
    this.orientation = R.var(Matrix.identity(3, 3));
    this.start();
  }
  
  public void setOrientationMode(DzOrientationMode mode) {
    this.mode = mode;
  }
  
  public void start() {
    if (started) return;
    started = true;
    
    RxVar<Matrix> robotOrientation = R.var(() -> {
      return robot.calcKinematics.get().orientation.asMatrix();
    });
    
    torqueSupplier = () -> {
      Rotation r = DzMotionTranslator.getRotation(robotOrientation.get(), orientation.get());
      
      Vector3d angVelVec = robot.calcKinematics.get().angVel;
      Matrix angVel = new Matrix(new double[][] {
          {angVelVec.x, angVelVec.y, angVelVec.z}
      }).transpose();
      
      Matrix angVelDesired = r.vector.times(r.angle * 1);
      Matrix error = angVelDesired.minus(angVel);
      
      Matrix out = error.times(1);
      
      return out;
    };
    
    robot.motionController.torque.addSupplier(torqueSupplier);
  }
  
  public void stop() {
    started = false;
    
    robot.motionController.torque.removeSupplier(torqueSupplier);
  }
  
  public enum DzOrientationMode {
    ABSOLUTE,
    RELATIVE
  }
  
}
