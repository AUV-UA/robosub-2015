package org.auvua.model.dangerZona;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import org.auvua.model.component.Drag;
import org.auvua.model.component.MassProperties;
import org.auvua.model.dangerZona.sim.DangerZonaHardwareSim;
import org.auvua.model.motion.Kinematics;

public class DangerZonaFactory {
  
  public static DangerZona build(RobotType type) {
    switch(type) {
      case DANGER_ZONA_SIM:
        return buildDangerZona();
      default:
        return null;
    }
  }
  
  public enum RobotType {
    DANGER_ZONA_SIM,
    DANGER_ZONA_REAL
  }
  
  public static DangerZona buildDangerZona() {
    Kinematics kinematics = new Kinematics();
    MassProperties massProperties = new MassProperties(40.0, new Matrix3d(new double[] {
        10000.0, 0.0, 0.0,
        0.0, 10000.0, 0.0,
        0.0, 0.0, 10000.0
    }));
    Drag drag = new Drag(new Vector3d(1, .5, 1), new Vector3d(30000, 30000, 30000), kinematics);
    DangerZonaPhysicsModel physicsModel = new DangerZonaPhysicsModel(kinematics, massProperties, drag);
    DangerZonaHardware hardware = new DangerZonaHardwareSim(physicsModel);
    DangerZona model = new DangerZona(hardware);
    return model;
  }
  
}
