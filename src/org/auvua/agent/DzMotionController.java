package org.auvua.agent;

import jama.Matrix;

import org.auvua.model.dangerZona.DangerZona;
import org.auvua.model.dangerZona.DangerZonaInputs;
import org.auvua.model.dangerZona.DzMotionTranslator;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;

public class DzMotionController {
  public static DangerZona robot;
  public static DzOrientationMode mode = DzOrientationMode.ABSOLUTE;
  
  public static final RxVar<Matrix> angAccel = R.var(new Matrix(3,3));
  public static final RxVar<Matrix> accel = R.var(new Matrix(new double[][] {
      { 1, 0, 0 }
  }).transpose());
  
  private static DangerZonaInputs inputs;
  
  private static final DzMotionTranslator translator = new DzMotionTranslator();
  
  public static void setModel(DangerZona robot) {
    DzMotionController.robot = robot;
    DzMotionController.inputs = robot.hardware.getInputs();
   }
  
  public static void setOrientationMode(DzOrientationMode mode) {
    DzMotionController.mode = mode;
  }
  
  public static void start() {
    RxVar<Matrix> thrustValues = R.var(new Matrix(8,1));
    
    thrustValues.setSupplier(() -> {
      translator.accel = accel.get();
      translator.angAccel = angAccel.get();
      translator.orientation = robot.calcKinematics.get().orientation.asMatrix();
      if (mode == DzOrientationMode.ABSOLUTE) {
        return translator.solveGlobal();
      } else {
        return translator.solveLocal();
      }
    });
    
    inputs.frontRight.setSupplier(() -> thrustValues.get().get(0, 0));
    inputs.frontLeft.setSupplier(() -> thrustValues.get().get(1, 0));
    inputs.rearLeft.setSupplier(() -> thrustValues.get().get(2, 0));
    inputs.rearRight.setSupplier(() -> thrustValues.get().get(3, 0));
    
    inputs.heaveFrontRight.setSupplier(() -> thrustValues.get().get(4, 0));
    inputs.heaveFrontLeft.setSupplier(() -> thrustValues.get().get(5, 0));
    inputs.heaveRearLeft.setSupplier(() -> thrustValues.get().get(6, 0));
    inputs.heaveRearRight.setSupplier(() -> thrustValues.get().get(7, 0));
  }
  
  public static void stop() {
    inputs.frontRight.setSupplier(() -> 0.0);
    inputs.frontLeft.setSupplier(() -> 0.0);
    inputs.rearLeft.setSupplier(() -> 0.0);
    inputs.rearRight.setSupplier(() -> 0.0);
    
    inputs.heaveFrontRight.setSupplier(() -> 0.0);
    inputs.heaveFrontLeft.setSupplier(() -> 0.0);
    inputs.heaveRearLeft.setSupplier(() -> 0.0);
    inputs.heaveRearRight.setSupplier(() -> 0.0);
  }
  
  public enum DzOrientationMode {
    ABSOLUTE,
    RELATIVE
  }
  
}
