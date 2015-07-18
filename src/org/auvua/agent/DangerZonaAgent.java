package org.auvua.agent;

import jama.Matrix;

import java.awt.Dimension;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.vecmath.Vector3d;

import org.auvua.agent.control.DataRecorder;
import org.auvua.agent.control.Timer;
import org.auvua.agent.tasks.MissionFactory;
import org.auvua.agent.tasks.OrientRobot;
import org.auvua.agent.tasks.Task;
import org.auvua.agent.tasks.MissionFactory.MissionType;
import org.auvua.model.dangerZona.DangerZona;
import org.auvua.model.dangerZona.DangerZonaFactory;
import org.auvua.model.dangerZona.DangerZonaInputs;
import org.auvua.model.dangerZona.DangerZonaOutputs;
import org.auvua.model.dangerZona.DzHardwareSim;
import org.auvua.model.dangerZona.DangerZonaFactory.RobotType;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;
import org.auvua.view.RChart;

public class DangerZonaAgent {
  
  public static Task command;
  public static Map<Character,Integer> keyMap = new HashMap<Character,Integer>();
  public static DangerZona robot;

  public static void main( String[] args ) throws SecurityException, IOException {
    
    robot = DangerZonaFactory.build(RobotType.DANGER_ZONA_SIM);
    
    buildFrames();
    
    double s2 = 1 / Math.sqrt(2.0);
    
    //Task task = MissionFactory.build(MissionType.REMOTE_CONTROL, robot);
    RxVar<Matrix> orientation = R.var(new Matrix(new double[][] {
        {1, 0, 0},
        {0, s2, s2},
        {0, -s2, s2}
    }));
    Task task = new OrientRobot(robot, orientation);
    task.start();
    
    Timer.getInstance().scale(1.0);
    
    DataRecorder recorder = new DataRecorder("data2.txt");
    recorder.record(robot.hardware.getOutputs().positionSensor.x, "xPos");
    recorder.record(robot.hardware.getOutputs().positionSensor.y, "yPos");
    recorder.record(robot.hardware.getOutputs().velocitySensor.x, "yVel");
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

  private static void buildFrames() {
    DangerZonaOutputs outputs = robot.hardware.getOutputs();
    DangerZonaInputs inputs = robot.hardware.getInputs();
    
    RChart chart = new RChart(800, 600);
    
    chart.observe(outputs.depthSensor, "Depth");
    
    chart.observe(outputs.gyroRateX, "Gyro Rate X");
    chart.observe(outputs.gyroRateY, "Gyro Rate Y");
    chart.observe(outputs.gyroRateZ, "Gyro Rate Z");
    
    chart.observe(inputs.frontRight, "FR");
    chart.observe(inputs.frontLeft, "FL");
    chart.observe(inputs.rearLeft, "RL");
    chart.observe(inputs.rearRight, "RR");
    chart.observe(inputs.heaveFrontRight, "Heave FR");
    chart.observe(inputs.heaveFrontLeft, "Heave FL");
    chart.observe(inputs.heaveRearLeft, "Heave RL");
    chart.observe(inputs.heaveRearRight, "Heave RR");
    
    JFrame frame2 = new JFrame();
    frame2.setSize(new Dimension(800, 600));
    frame2.setVisible(true);
    frame2.add(chart.getPanel());
  }

}
