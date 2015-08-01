package org.auvua.agent.task;

import org.auvua.agent.control.Timer;
import org.auvua.model.dangerZona.DangerZona;

public class AwaitMissionStop extends AbstractTask {
  
  private DangerZona robot;
  public final TaskCondition missionStop;
  
  public AwaitMissionStop(DangerZona robot) {
    this.robot = robot;
    
    this.missionStop = createCondition("missionStart", () -> {
      Timer.getInstance().get();
      return !robot.hardware.getOutputs().missionSwitch.get();
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
