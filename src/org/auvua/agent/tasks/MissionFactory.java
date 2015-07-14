package org.auvua.agent.tasks;

import org.auvua.model.dangerZona.DangerZona;
import org.auvua.reactive.core.R;

public class MissionFactory {

  public static Task build(MissionType type, DangerZona robot) {
    switch(type) {
      case SQUARE_WALK:
        return new DrivingMission().getStartTask();
      case REMOTE_CONTROL:
        return new RemoteControl(robot);
      case MAINTAIN_DEPTH:
        return new MaintainDepth(robot, R.var(100.0), 5.0);
      default:
        return new DoNothing(robot);
    }
  }
  
  public enum MissionType {
    RANDOM_WALK,
    SQUARE_WALK,
    MAINTAIN_DEPTH,
    REMOTE_CONTROL
  }
}
