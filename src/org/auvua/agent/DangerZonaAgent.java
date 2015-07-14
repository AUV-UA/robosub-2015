package org.auvua.agent;

import java.awt.Dimension;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.vecmath.Vector3d;

import org.auvua.agent.control.DataRecorder;
import org.auvua.agent.control.Timer;
import org.auvua.agent.tasks.MissionFactory;
import org.auvua.agent.tasks.Task;
import org.auvua.agent.tasks.MissionFactory.MissionType;
import org.auvua.model.component.DangerZonaInputs;
import org.auvua.model.component.DangerZonaOutputs;
import org.auvua.model.dangerZona.DangerZona;
import org.auvua.model.dangerZona.DangerZonaFactory;
import org.auvua.model.dangerZona.DangerZonaFactory.RobotType;
import org.auvua.model.dangerZona.sim.DangerZonaHardwareSim;
import org.auvua.reactive.core.R;
import org.auvua.view.RChart;

public class DangerZonaAgent {
  
  public static Task command;
  public static Map<Character,Integer> keyMap = new HashMap<Character,Integer>();
  public static DangerZona robot;

  public static void main( String[] args ) throws SecurityException, IOException {
    
    robot = DangerZonaFactory.build(RobotType.DANGER_ZONA_SIM);
    
    buildFrames();
    
    Task task = MissionFactory.build(MissionType.REMOTE_CONTROL, robot);
    task.start();
    
    DataRecorder recorder = new DataRecorder("data2.txt");
    recorder.record(robot.hardware.getOutputs().positionSensor.x, "xPos");
    recorder.record(robot.hardware.getOutputs().positionSensor.y, "yPos");
    recorder.record(robot.hardware.getOutputs().velocitySensor.x, "yVel");
    recorder.start();
    
    Timer.getInstance().reset(); // Begin timing

    new Thread(() -> {
      while(true) {
        robot.update();
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  private static void buildFrames() {
    DangerZonaOutputs outputs = robot.hardware.getOutputs();
    DangerZonaInputs inputs = robot.hardware.getInputs();
    
    RChart chart = new RChart(800, 600);
    
    chart.observe(outputs.depthSensor, "Depth");
    
    chart.observe(outputs.gyroRateX, "Gyro Rate X");
    chart.observe(outputs.gyroRateY, "Gyro Rate Y");
    chart.observe(outputs.gyroRateZ, "Gyro Rate Z");
    
    chart.observe(inputs.heaveFrontRight, "Heave FR");
    chart.observe(inputs.heaveFrontLeft, "Heave FL");
    chart.observe(inputs.heaveRearLeft, "Heave RL");
    chart.observe(inputs.heaveRearRight, "Heave RR");
    
    chart.observe(R.var(() -> {
      Vector3d calcRoll = robot.calcKinematics.get().orientation.localY;
      Vector3d actualRoll = ((DangerZonaHardwareSim) robot.hardware).physicsModel.get().kinematics.orientation.localY;
      return calcRoll.angle(actualRoll);
    }), "Roll error");
    
    JFrame frame2 = new JFrame();
    frame2.setSize(new Dimension(800, 600));
    frame2.setVisible(true);
    frame2.add(chart.getPanel());
  }

}
