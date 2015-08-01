package org.auvua.agent.mission;

import org.auvua.agent.task.AwaitMissionStart;
import org.auvua.agent.task.AwaitMissionStop;
import org.auvua.agent.task.CompositeTask;
import org.auvua.agent.task.DoNothing;
import org.auvua.agent.task.RemoteControl;
import org.auvua.agent.task.ResetRobot;
import org.auvua.agent.task.Task;
import org.auvua.model.dangerZona.DangerZona;

public class MissionFactory {

  public static Task build(MissionType type, DangerZona robot) {
    AwaitMissionStart awaitStart = new AwaitMissionStart(robot);
    AwaitMissionStop awaitStop = new AwaitMissionStop(robot);
    ResetRobot resetRobot = new ResetRobot(robot);
    DoNothing idle = new DoNothing(robot);
    Task startTask;
    
    switch(type) {
      case ROBOSUB_MISSION:
        startTask = new RobosubMission(robot).getStartTask();
        break;
      case REMOTE_CONTROL:
        startTask = new RemoteControl(robot);
        break;
      case GATE_MISSION:
        startTask = new GateMission(robot).getStartTask();
        break;
      case SIT_STILL:
        startTask = new SitStillMission(robot).getStartTask();
        break;
      case ALIGN_TO_MARKER:
        startTask = new AlignToMarkerMission(robot).getStartTask();
        break;
      case BARREL_ROLL:
        startTask = new BarrelRollMission(robot).getStartTask();
        break;
      default:
        startTask = new DoNothing(robot);
        break;
    }
    
    awaitStart.missionStart.triggers(() -> {
      System.out.println("Mission started!");
      awaitStart.stop();
      idle.stop();
      resetRobot.start();
      awaitStop.start();
      startTask.start();
    });
    
    awaitStop.missionStop.triggers(() -> {
      System.out.println("Mission stopped...");
      Task.stopAll();
      awaitStart.start();
      idle.start();
    });
    
    return new CompositeTask(idle, awaitStart);
  }
  
  public enum MissionType {
    ROBOSUB_MISSION,
    GATE_MISSION,
    MAINTAIN_DEPTH,
    REMOTE_CONTROL,
    ALIGN_TO_MARKER,
    SIT_STILL,
    BARREL_ROLL
  }
}
