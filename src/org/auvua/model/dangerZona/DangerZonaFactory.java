package org.auvua.model.dangerZona;

import org.auvua.model.dangerZona.hardware.DzHardware;
import org.auvua.model.dangerZona.hardware.DzHardwareReal;
import org.auvua.model.dangerZona.hardware.DzHardwareSim;
import org.opencv.core.Core;


public class DangerZonaFactory {
  
  public static DangerZona build(RobotType type) {
    switch(type) {
      case DANGER_ZONA_SIM:
        return buildDangerZonaSim();
      case DANGER_ZONA_REAL:
        return buildDangerZonaReal();
      default:
        return null;
    }
  }
  
  public enum RobotType {
    DANGER_ZONA_SIM,
    DANGER_ZONA_REAL
  }
  
  public static DangerZona buildDangerZonaSim() {
    System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
    DzHardware hardware = new DzHardwareSim();
    DangerZona model = new DangerZona(hardware);
    return model;
  }
  
  public static DangerZona buildDangerZonaReal() {
    System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
    DzHardware hardware = new DzHardwareReal();
    DangerZona model = new DangerZona(hardware);
    return model;
  }
  
}
