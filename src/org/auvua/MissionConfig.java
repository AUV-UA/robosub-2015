package org.auvua;

import org.auvua.agent.mission.MissionFactory.MissionType;
import org.auvua.model.dangerZona.DangerZonaFactory.RobotType;

public class MissionConfig {
  private static final RobotType ROBOT_TYPE = RobotType.DANGER_ZONA_SIM;
  private static final MissionType MISSION_TYPE = MissionType.GATE_MISSION;
  
  public static RobotType getRobotType() {
    return ROBOT_TYPE;
  }
  
  public static MissionType getMissionType() {
    return MISSION_TYPE;
  }
}
