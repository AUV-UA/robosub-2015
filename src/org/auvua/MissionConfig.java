package org.auvua;

import org.auvua.agent.mission.MissionFactory.MissionType;
import org.auvua.model.dangerZona.DangerZonaFactory.RobotType;

public class MissionConfig {
  private static final RobotType ROBOT_TYPE = RobotType.DANGER_ZONA_REAL;
  private static final MissionType MISSION_TYPE = MissionType.SIT_STILL;
  private static final boolean RECORD_DATA = true;
  
  public static RobotType getRobotType() {
    return ROBOT_TYPE;
  }
  
  public static MissionType getMissionType() {
    return MISSION_TYPE;
  }
  
  public static boolean recordData() {
    return RECORD_DATA;
  }
}
