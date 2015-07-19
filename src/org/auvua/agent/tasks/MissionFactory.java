package org.auvua.agent.tasks;

import org.auvua.model.dangerZona.DangerZona;

public class MissionFactory {

  public static Task build(MissionType type, DangerZona robot) {
    switch(type) {
      case ROBOSUB_MISSION:
        return new RobosubMission().getStartTask();
      case REMOTE_CONTROL:
        return new RemoteControl(robot);
      default:
        return new DoNothing(robot);
    }
  }
  
  public enum MissionType {
    ROBOSUB_MISSION,
    MAINTAIN_DEPTH,
    REMOTE_CONTROL
  }
}
