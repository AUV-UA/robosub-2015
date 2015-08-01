package org.auvua.agent;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.auvua.MissionConfig;
import org.auvua.agent.control.Timer;
import org.auvua.agent.mission.MissionFactory;
import org.auvua.agent.task.Task;
import org.auvua.agent.utilities.DataRecorder;
import org.auvua.model.dangerZona.DangerZona;
import org.auvua.model.dangerZona.DangerZonaFactory;
import org.auvua.model.dangerZona.hardware.DangerZonaInputs;
import org.auvua.model.dangerZona.hardware.DangerZonaOutputs;
import org.auvua.reactive.core.R;

public class DangerZonaAgent {
  
  public static Task command;
  public static Map<Character,Integer> keyMap = new HashMap<Character,Integer>();
  public static DangerZona robot = DangerZonaFactory.build(MissionConfig.ROBOT_TYPE);

  public static void main( String[] args ) throws SecurityException, IOException {
    Task task = MissionFactory.build(MissionConfig.MISSION_TYPE, robot);
    task.start();
    
    Timer.getInstance().scale(1.0);
    
    startRecorder();
    
    Timer.getInstance().reset(); // Begin timing
    
    new Thread(() -> {
      while(true) {
        robot.update();
        try { Thread.sleep(20); } catch (Exception e) {
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
    DangerZonaOutputs outputs = robot.hardware.getOutputs();
    DangerZonaInputs inputs = robot.hardware.getInputs();
    
    recorder.record(outputs.gyroRateX, "Gyro Rate X");
    recorder.record(outputs.gyroRateY, "Gyro Rate Y");
    recorder.record(outputs.gyroRateZ, "Gyro Rate Z");
    recorder.record(outputs.accelX, "Accel X");
    recorder.record(outputs.accelY, "Accel Y");
    recorder.record(outputs.accelZ, "Accel Z");
    recorder.record(outputs.depthSensor, "Depth");
    recorder.record(outputs.humidity, "Humidity");
    
    recorder.record(R.var(() -> robot.calcKinematics.get().orientation.getYaw() * 180 / Math.PI), "Yaw");
    recorder.record(R.var(() -> robot.calcKinematics.get().pos.x), "Pos X");
    recorder.record(R.var(() -> robot.calcKinematics.get().pos.y), "Pos Y");
    recorder.record(R.var(() -> robot.calcKinematics.get().pos.z), "Pos Z");
    recorder.record(R.var(() -> outputs.missionSwitch.get() ? 1.0 : 0.0), "MissionSwitch");
    
    recorder.record(inputs.frontRight, "FR");
    recorder.record(inputs.frontLeft, "FL");
    recorder.record(inputs.rearLeft, "RL");
    recorder.record(inputs.rearRight, "RR");
    recorder.record(inputs.heaveFrontRight, "Heave FR");
    recorder.record(inputs.heaveFrontLeft, "Heave FL");
    recorder.record(inputs.heaveRearLeft, "Heave RL");
    recorder.record(inputs.heaveRearRight, "Heave RR");
    
    recorder.start();
  }

}
