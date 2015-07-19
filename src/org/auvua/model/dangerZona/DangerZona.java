package org.auvua.model.dangerZona;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import org.auvua.agent.DzMotionController;
import org.auvua.agent.control.Timer;
import org.auvua.model.dangerZona.DangerZonaFactory.RobotType;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;


public class DangerZona {
  
  private static DangerZona instance;
  
  public final DzHardware hardware;
  public final RxVar<DzCalculatedKinematics> calcKinematics;
  public final RxVar<DzMotionTranslator> motionTranslator;
  public final DzMotionController motionController;
  
  public DangerZona(DzHardware hw) {
    this.hardware = hw;
    this.calcKinematics = R.var(new DzCalculatedKinematics());
    this.motionTranslator = R.var(new DzMotionTranslator());
    
    calcKinematics.setModifier((ck) -> {
      Matrix3d orientationMatrix = ck.orientation.asMatrix3d();
      Vector3d localAngVel = new Vector3d();
      localAngVel.x = hardware.getOutputs().gyroRateX.get();
      localAngVel.y = hardware.getOutputs().gyroRateY.get();
      localAngVel.z = hardware.getOutputs().gyroRateZ.get();
      
      orientationMatrix.transform(localAngVel, ck.angVel);
      ck.update();
    });
    
    this.motionController = new DzMotionController(this);
    this.motionController.start();
  }
  
  public void update() {
    R.doSync(() -> {
      Timer.getInstance().trigger();
      hardware.update();
    });
    
  }

  public static DangerZona getInstance() {
    if (instance == null) {
      instance = DangerZonaFactory.build(RobotType.DANGER_ZONA_SIM);
    }
    return instance;
  }
}
