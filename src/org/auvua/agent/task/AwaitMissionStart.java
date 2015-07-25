package org.auvua.agent.task;

import org.auvua.agent.control.Timer;
import org.auvua.model.dangerZona.DangerZona;

public class AwaitMissionStart extends AbstractTask {
  
  public final TaskCondition missionStart;
  
  public AwaitMissionStart(DangerZona robot) {
    this.missionStart = createCondition("missionStart", () -> {
      Timer.getInstance().get();
      return robot.hardware.getOutputs().missionSwitch.get();
    });
  }

  @Override
  public void initialize() {
    // Do nothing
  }

  @Override
  public void terminate() {
    // Do nothing
  }
  
  

}
