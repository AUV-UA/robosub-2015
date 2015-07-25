package org.auvua.agent;

import jama.Matrix;

import org.auvua.agent.control.Timer;
import org.auvua.agent.task.MotionMode;
import org.auvua.model.dangerZona.DangerZona;
import org.auvua.model.dangerZona.hardware.DangerZonaInputs;
import org.auvua.model.motion.DzMotionTranslator;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxAccumulator;
import org.auvua.reactive.core.RxVar;

public class DzMotionController {
  
  public DangerZona robot;
  public MotionMode mode = MotionMode.ABSOLUTE;
  
  public final RxAccumulator<Matrix> force =
      new RxAccumulator<Matrix>(new Matrix(3,1), (v1, v2) -> v1.plus(v2));
  public final RxAccumulator<Matrix> torque =
      new RxAccumulator<Matrix>(new Matrix(3,1), (v1, v2) -> v1.plus(v2));
  
  private DangerZonaInputs inputs;
  private final DzMotionTranslator translator;
  
  private boolean started = false;
  private boolean paused = false;
  
  public DzMotionController(DangerZona robot) {
    this.robot = robot;
    this.inputs = robot.hardware.getInputs();
    this.translator = new DzMotionTranslator();
    
    force.addSupplier(() -> {
      Timer.getInstance().get();
      return new Matrix(3,1);
    });
    torque.addSupplier(() -> {
      Timer.getInstance().get();
      return new Matrix(3,1);
    });
    
    this.start();
  }
  
  public void setOrientationMode(MotionMode mode) {
    this.mode = mode;
  }
  
  public void start() {
    if (started) return;
    started = true;
    
    RxVar<Matrix> thrustValues = R.var(() -> {
      translator.force = force.get();
      translator.torque = torque.get();
      translator.orientation = robot.calcKinematics.get().orientation.asMatrix();
      
      if (paused) {
        return new Matrix(8,1);
      }
      if (mode == MotionMode.ABSOLUTE) {
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
  
  public void stop() {
    started = false;
    
    inputs.frontRight.setSupplier(() -> 0.0);
    inputs.frontLeft.setSupplier(() -> 0.0);
    inputs.rearLeft.setSupplier(() -> 0.0);
    inputs.rearRight.setSupplier(() -> 0.0);
    
    inputs.heaveFrontRight.setSupplier(() -> 0.0);
    inputs.heaveFrontLeft.setSupplier(() -> 0.0);
    inputs.heaveRearLeft.setSupplier(() -> 0.0);
    inputs.heaveRearRight.setSupplier(() -> 0.0);
  }
  
  public void pause() {
    paused = true;
  }
  
  public void unpause() {
    paused = false;
  }
  
}
