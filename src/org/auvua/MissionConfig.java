package org.auvua;

import org.auvua.agent.mission.MissionFactory.MissionType;
import org.auvua.model.dangerZona.DangerZonaFactory.RobotType;

public class MissionConfig {
  public static final RobotType ROBOT_TYPE = RobotType.DANGER_ZONA_REAL;
  public static final MissionType MISSION_TYPE = MissionType.ROBOSUB_MISSION;
  public static final double MAX_OUTPUT = .7;
  
  public static RobotType getRobotType() {
    return ROBOT_TYPE;
  }
  
  public static MissionType getMissionType() {
    return MISSION_TYPE;
  }
}
