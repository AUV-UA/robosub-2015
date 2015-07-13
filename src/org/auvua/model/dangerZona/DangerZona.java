package org.auvua.model.dangerZona;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;


public class DangerZona {
  public final DangerZonaHardware hardware;
  public final DzCalculatedKinematics calcKinematics = new DzCalculatedKinematics();
  
  public DangerZona(DangerZonaHardware hardware) {
    this.hardware = hardware;
  }
  
  public void update() {
    hardware.update();
    Matrix3d orientationMatrix = calcKinematics.orientation.asMatrix();
    Vector3d localAngVel = new Vector3d();
    localAngVel.x = hardware.getOutputs().gyroRateX.get();
    localAngVel.y = hardware.getOutputs().gyroRateY.get();
    localAngVel.z = hardware.getOutputs().gyroRateZ.get();
    
    orientationMatrix.transform(localAngVel, calcKinematics.angVel);
    calcKinematics.update();
  }
}
