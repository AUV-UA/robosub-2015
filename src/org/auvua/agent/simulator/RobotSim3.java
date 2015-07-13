package org.auvua.agent.simulator;

import java.awt.Dimension;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JFrame;

import org.auvua.agent.control.DataRecorder;
import org.auvua.agent.control.Timer;
import org.auvua.agent.oi.OperatorInterface2;
import org.auvua.agent.tasks.Task;
import org.auvua.model.RobotModel3;
import org.auvua.view.PhysicsRobotRenderer;
import org.auvua.view.RChart;

public class RobotSim3 {
  
  public static Task command;
  public static Map<Character,Integer> keyMap = new HashMap<Character,Integer>();
  public static RobotModel3 robot = RobotModel3.getInstance();
  public static PhysicsRobotRenderer r = new PhysicsRobotRenderer(robot.physicsRobot.get());

  public static void main( String[] args ) throws SecurityException, IOException {
    
    buildFrames();
    
    DataRecorder recorder = new DataRecorder("data2.txt");
    recorder.record(robot.positionSensor.x, "xPos");
    recorder.record(robot.positionSensor.y, "yPos");
    recorder.record(robot.velocitySensor.x, "yVel");
    recorder.start();

    new Thread(() -> {
      while(true) {
        robot.trigger();
        
        try {
          Thread.sleep(30);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();
    
    new Thread(() -> {
      while(true) {
        r.update();
        
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();
    
    new Thread(() -> {
      @SuppressWarnings("resource")
      Scanner input = new Scanner(System.in);
      while(true) {
        Timer.getInstance().scale(input.nextDouble());
      }
    }).start();
  }

  private static void buildFrames() {
    OperatorInterface2 oi = new OperatorInterface2();
    
    robot.frontRight.setSupplier(() -> 5000 * (oi.forward.get() - oi.strafe.get() + oi.rotation.get()));
    robot.frontLeft.setSupplier(() -> 5000 * (oi.forward.get() + oi.strafe.get() - oi.rotation.get()));
    robot.rearLeft.setSupplier(() -> 5000 * (oi.forward.get() - oi.strafe.get() - oi.rotation.get()));
    robot.rearRight.setSupplier(() -> 5000 * (oi.forward.get() + oi.strafe.get() + oi.rotation.get()));
    
    robot.heaveFrontRight.setSupplier(() -> 5000 * (oi.elevation.get() + oi.pitch.get() - oi.roll.get()));
    robot.heaveFrontLeft.setSupplier(() -> 5000 * (oi.elevation.get() + oi.pitch.get() + oi.roll.get()));
    robot.heaveRearLeft.setSupplier(() -> 5000 * (oi.elevation.get() - oi.pitch.get() + oi.roll.get()));
    robot.heaveRearRight.setSupplier(() -> 5000 * (oi.elevation.get() - oi.pitch.get() - oi.roll.get()));
    
    r.universe.getCanvas().addKeyListener(oi.getKeyListener());
    
    RChart chart = new RChart(800, 600);
    
    chart.observe(robot.depthSensor, "Depth");
    chart.observe(oi.elevation, "Elevation Control");
    
    chart.observe(robot.gyroRateX, "Gyro Rate X");
    chart.observe(robot.gyroRateY, "Gyro Rate Y");
    chart.observe(robot.gyroRateZ, "Gyro Rate Z");
    
    JFrame frame2 = new JFrame();
    frame2.setSize(new Dimension(800, 600));
    frame2.setVisible(true);
    frame2.add(chart.getPanel());
  }

}
