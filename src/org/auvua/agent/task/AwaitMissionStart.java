package org.auvua.agent.task;

import org.auvua.agent.control.Timer;
import org.auvua.model.dangerZona.DangerZona;
import org.auvua.model.dangerZona.hardware.LEDStrips.LED_STRIP;

public class AwaitMissionStart extends AbstractTask {

  public final TaskCondition missionStart;
  public final DangerZona robot;

  public AwaitMissionStart(DangerZona robot) {
    this.robot = robot;
    this.missionStart = createCondition("missionStart", () -> {
      Timer.getInstance().get();
      return robot.hardware.getOutputs().missionSwitch.get();
    });
  }

  @Override
  public void initialize() {
    robot.hardware.getInputs().indicators.enqueueAnimation((indicators) -> {
      while(!robot.hardware.getOutputs().missionSwitch.get()) {
        indicators.setStripColor(LED_STRIP.BACKLEFT, 0x7f7f7f);
        indicators.setStripColor(LED_STRIP.BACKRIGHT, 0x7f7f7f);
        try { Thread.sleep(1000); } catch (Exception e) {}
        indicators.setStripColor(LED_STRIP.BACKLEFT, 0x000000);
        indicators.setStripColor(LED_STRIP.BACKRIGHT, 0x000000);
        try { Thread.sleep(1000); } catch (Exception e) {}
      }
    });
  }

  @Override
  public void terminate() {
    // Do nothing
  }



}
