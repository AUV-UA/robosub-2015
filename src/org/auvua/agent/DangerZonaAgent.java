package org.auvua.agent;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.auvua.MissionConfig;
import org.auvua.agent.control.DataRecorder;
import org.auvua.agent.control.Timer;
import org.auvua.agent.mission.MissionFactory;
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
    
    /*
    R.task(() -> {
      DzDerivedKinematics kin = robot.calcKinematics.get();
      Matrix mat = kin.orientation.asMatrix();
      System.out.println(DzMotionTranslator.mat2str(mat));
      System.out.println("Yaw: " + kin.orientation.getYaw() * 180 / Math.PI);
    });
    */
    startRecorder();
    
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

  private static void startRecorder() throws SecurityException, IOException {
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
    dateFormat.format(date);
    
    DataRecorder recorder = new DataRecorder(String.format("logs/%s.txt", dateFormat.format(date)));
    recorder.record(robot.hardware.getOutputs().depthSensor, "depth");
    recorder.record(robot.hardware.getOutputs().gyroRateX, "gyro x");
    recorder.record(robot.hardware.getOutputs().gyroRateY, "gyro y");
    recorder.record(robot.hardware.getOutputs().gyroRateZ, "gyro z");
    recorder.start();
  }

}
