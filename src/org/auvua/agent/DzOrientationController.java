package org.auvua.agent;

import java.util.function.Supplier;

import javax.vecmath.Vector3d;

import jama.Matrix;

import org.auvua.agent.control.MatrixPidController;
import org.auvua.model.dangerZona.DangerZona;
import org.auvua.model.motion.DzMotionTranslator;
import org.auvua.model.motion.DzMotionTranslator.Rotation;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;

public class DzOrientationController {
  
  private DangerZona robot;
  
  private Supplier<Matrix> torqueSupplier;
  public final RxVar<Matrix> orientation;
  public double lastTime;
  public Matrix lastAngPosError;
  public Matrix lastAngVelError;
  
  private boolean started = false;
  private boolean paused = false;
  
  public DzOrientationController(DangerZona robot) {
    this.robot = robot;
    this.orientation = R.var(Matrix.identity(3, 3));
    this.start();
  }
  
  public void start() {
    if (started) return;
    started = true;
    
    RxVar<Matrix> zero = R.var(new Matrix(3,1));
    
    RxVar<Matrix> robotOrientation = R.var(() -> {
      return robot.calcKinematics.get().orientation.asMatrix();
    });
    
    RxVar<Matrix> angularError = R.var(() -> {
      Rotation r = DzMotionTranslator.getRotation(robotOrientation.get(), orientation.get());
      return r.vector.times(-r.angle);
    });
    
    MatrixPidController angVelPid = new MatrixPidController(angularError, zero, 3, 0, 0);
    
    RxVar<Matrix> angVel = R.var(() -> {
      Vector3d angVelVec = robot.calcKinematics.get().angVel;
      return new Matrix(new double[][] {
          {angVelVec.x, angVelVec.y, angVelVec.z}
      }).transpose();
    });
    
    MatrixPidController angAccelPid = new MatrixPidController(angVel, angVelPid, 20, 0, 0);
    
    torqueSupplier = () -> {
      Matrix out = angAccelPid.get();
      if (paused) out = new Matrix(3,1);
      return out;
    };
    
    robot.motionController.torque.addSupplier(torqueSupplier);
  }
  
  public void stop() {
    started = false;
    
    robot.motionController.torque.removeSupplier(torqueSupplier);
  }
  
  public void pause() {
    paused = true;
  }
  
  public void unpause() {
    paused = false;
  }
  
}
