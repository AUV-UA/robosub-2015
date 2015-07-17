package org.auvua.agent.tasks;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;

import jama.Matrix;

import org.auvua.agent.DzMotionController;
import org.auvua.agent.control.Timer;
import org.auvua.model.dangerZona.DangerZona;
import org.auvua.model.dangerZona.DzMotionTranslator;
import org.auvua.model.dangerZona.DzMotionTranslator.Rotation;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;
import org.auvua.view.DangerZonaRenderer;

public class OrientRobot extends AbstractTask {

  private RxVar<Matrix> orientation;
  private DangerZona robot;
  Matrix lastError;
  double lastTime;

  public OrientRobot(DangerZona robot, RxVar<Matrix> orientation) {
    this.robot = robot;
    this.orientation = orientation;
  }
  
  @Override
  public void initialize() {
    DzMotionController.setModel(robot);
    
    robot.calcKinematics.get().orientation.asMatrix3d();
    
    RxVar<Matrix> robotOrientation = R.var(() -> {
      Timer.getInstance().get();
      return robot.calcKinematics.get().orientation.asMatrix();
    });
    
    LineArray omegaLineArr = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
    omegaLineArr.setCapability(LineArray.ALLOW_COORDINATE_WRITE);
    omegaLineArr.setCoordinate(0, new double[] {0, 0, 0});
    omegaLineArr.setCoordinate(1, new double[] {2, 2, 2});
    omegaLineArr.setColors(0, new float[] {1.0f, 1.0f, 1.0f});
    omegaLineArr.setColors(1, new float[] {1.0f, 1.0f, 1.0f});
    
    LineArray omegaLineArrDesired = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
    omegaLineArrDesired.setCapability(LineArray.ALLOW_COORDINATE_WRITE);
    omegaLineArrDesired.setCoordinate(0, new double[] {0, 0, 0});
    omegaLineArrDesired.setCoordinate(1, new double[] {2, 2, 2});
    omegaLineArrDesired.setColors(0, new float[] {0, 1.0f, 0});
    omegaLineArrDesired.setColors(1, new float[] {0, 1.0f, 0});
    
    LineArray errorLineArrDesired = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
    errorLineArrDesired.setCapability(LineArray.ALLOW_COORDINATE_WRITE);
    errorLineArrDesired.setCoordinate(0, new double[] {0, 0, 0});
    errorLineArrDesired.setCoordinate(1, new double[] {2, 2, 2});
    errorLineArrDesired.setColors(0, new float[] {1.0f, 0, 0});
    errorLineArrDesired.setColors(1, new float[] {1.0f, 0, 0});
    
    BranchGroup orientationVectors = new BranchGroup();
    
    orientationVectors.addChild(new Shape3D(omegaLineArr));
    orientationVectors.addChild(new Shape3D(omegaLineArrDesired));
    orientationVectors.addChild(new Shape3D(errorLineArrDesired));
    
    DangerZonaRenderer.getInstance().group.addChild(orientationVectors);
    
    DzMotionController.angAccel.setSupplier(() -> {
      Rotation r = DzMotionTranslator.getRotation(robotOrientation.get(), orientation.get());
      
      Vector3d angVelVec = robot.calcKinematics.get().angVel;
      Matrix angVel = new Matrix(new double[][] {
          {angVelVec.x, angVelVec.y, angVelVec.z}
      }).transpose();
      
      double time = Timer.getInstance().get();
      Matrix angVelDesired = r.vector.times(r.angle * 5);
      Matrix error = angVelDesired.minus(angVel);
      
      Matrix out;
      if (lastError == null) {
        out = error.times(1);
      } else {
        Matrix derivative;
        if (time - lastTime != 0) {
          derivative = error.minus(lastError).times(1 / (time - lastTime));
        } else {
          derivative = new Matrix(3,1);
        }
        out = error.times(1);
      }
      
      omegaLineArr.setCoordinate(0, new double[] {0, 0, 0});
      double[] angVelArr = angVel.transpose().times(50).getArray()[0];
      omegaLineArr.setCoordinate(1, angVelArr);
      
      omegaLineArrDesired.setCoordinate(0, new double[] {0, 0, 0});
      double[] angVelDesiredArr = angVelDesired.transpose().times(50).getArray()[0];
      omegaLineArrDesired.setCoordinate(1, angVelDesiredArr);
      
      errorLineArrDesired.setCoordinate(0, angVelArr);
      double[] errorArr = error.transpose().times(50).getArray()[0];
      double[] sum = new double[] {
          errorArr[0] + angVelArr[0],
          errorArr[1] + angVelArr[1],
          errorArr[2] + angVelArr[2]
      };
      errorLineArrDesired.setCoordinate(1, sum);
      
      lastTime = time;
      lastError = error;
      return out;
    });
    
    DzMotionController.accel.setSupplier(() -> {
      Timer.getInstance().get();
      return new Matrix(new double[][] {
          { 5, 0, 0 }
      }).transpose();
    });
    
    DzMotionController.start();
  }

}
