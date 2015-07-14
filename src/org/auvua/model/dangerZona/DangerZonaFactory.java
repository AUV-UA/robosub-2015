package org.auvua.model.dangerZona;

import org.auvua.model.dangerZona.sim.DangerZonaHardwareSim;

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
    DangerZonaPhysicsModel physicsModel = new DangerZonaPhysicsModel();
    DangerZonaHardware hardware = new DangerZonaHardwareSim(physicsModel);
    DangerZona model = new DangerZona(hardware);
    return model;
  }
  
}
