package org.auvua.agent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.auvua.MissionConfig;
import org.auvua.agent.control.DataRecorder;
import org.auvua.agent.control.Timer;
import org.auvua.agent.tasks.MissionFactory;
import org.auvua.agent.tasks.Task;
import org.auvua.model.dangerZona.DangerZona;
import org.auvua.model.dangerZona.DangerZonaFactory;

public class DangerZonaAgent {
  
  public static Task command;
  public static Map<Character,Integer> keyMap = new HashMap<Character,Integer>();
  public static DangerZona robot = DangerZonaFactory.build(MissionConfig.getRobotType());

  public static void main( String[] args ) throws SecurityException, IOException {
    Task task = MissionFactory.build(MissionConfig.getMissionType(), robot);
    task.start();
    
    Timer.getInstance().scale(1.0);
    
    DataRecorder recorder = new DataRecorder("data2.txt");
    recorder.start();
    
    Timer.getInstance().reset(); // Begin timing
    
    new Thread(() -> {
      while(true) {
        robot.update();
        try { Thread.sleep(10); } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

}
