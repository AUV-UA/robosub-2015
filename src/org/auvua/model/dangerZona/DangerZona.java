package org.auvua.model.dangerZona;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import org.auvua.agent.DzMotionController;
import org.auvua.agent.DzOrientationController;
import org.auvua.agent.control.Timer;
import org.auvua.model.dangerZona.hardware.DzHardware;
import org.auvua.model.motion.DzMotionTranslator;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;
import org.auvua.view.Dashboard;

public class DangerZona {
  
  public final Dashboard dashboard;
  public final DzHardware hardware;
  public final RxVar<DzDerivedKinematics> calcKinematics;
  public final RxVar<DzMotionTranslator> motionTranslator;
  public final DzOrientationController orientationController;
  public final DzMotionController motionController;
  
  public DangerZona(DzHardware hw) {
    this.hardware = hw;
    this.calcKinematics = R.var(new DzDerivedKinematics());
    this.motionTranslator = R.var(new DzMotionTranslator());
    
    calcKinematics.setModifier((ck) -> {
      Matrix3d orientationMatrix = ck.orientation.asMatrix3d();
      
      Vector3d localAngVel = new Vector3d();
      localAngVel.x = hardware.getOutputs().gyroRateX.get();
      localAngVel.y = hardware.getOutputs().gyroRateY.get();
      localAngVel.z = hardware.getOutputs().gyroRateZ.get();
      
      Vector3d localAccel = new Vector3d();
      localAccel.x = hardware.getOutputs().accelX.get();
      localAccel.y = hardware.getOutputs().accelY.get();
      localAccel.z = hardware.getOutputs().accelZ.get();
      
      Vector3d withGravity = new Vector3d();
      
      orientationMatrix.transform(localAngVel, ck.angVel);
      orientationMatrix.transform(localAccel, withGravity);
      ck.accel.add(new Vector3d(0, 0, 9.806), withGravity);
      ck.update();
    });
    
    this.motionController = new DzMotionController(this);
    this.orientationController = new DzOrientationController(this);
    this.dashboard = new Dashboard(this);
  }
  
  public void update() {
    R.doSync(() -> {
      Timer.getInstance().trigger();
      hardware.update();
      dashboard.update();
    });
  }
}
