package org.auvua.model.dangerZona;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import org.auvua.agent.control.Timer;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;


public class DangerZona {
  public final DzHardware hardware;
  public final RxVar<DzCalculatedKinematics> calcKinematics = R.var(new DzCalculatedKinematics());
  public final RxVar<DzMotionTranslator> motionTranslator = R.var(new DzMotionTranslator());
  
  public DangerZona(DzHardware hw) {
    this.hardware = hw;
    
    calcKinematics.setModifier((ck) -> {
      Matrix3d orientationMatrix = ck.orientation.asMatrix3d();
      Vector3d localAngVel = new Vector3d();
      localAngVel.x = hardware.getOutputs().gyroRateX.get();
      localAngVel.y = hardware.getOutputs().gyroRateY.get();
      localAngVel.z = hardware.getOutputs().gyroRateZ.get();
      
      orientationMatrix.transform(localAngVel, ck.angVel);
      ck.update();
    });
  }
  
  public void update() {
    R.doSync(() -> {
      Timer.getInstance().trigger();
      hardware.update();
    });
    
  }
}
