package org.auvua.agent;

import java.awt.Dimension;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import org.auvua.agent.control.DataRecorder;
import org.auvua.agent.oi.OperatorInterface2;
import org.auvua.agent.tasks.Task;
import org.auvua.model.component.DangerZonaInputs;
import org.auvua.model.component.DangerZonaOutputs;
import org.auvua.model.dangerZona.DangerZona;
import org.auvua.model.dangerZona.DangerZonaFactory;
import org.auvua.model.dangerZona.DangerZonaFactory.RobotType;
import org.auvua.view.DangerZonaOrientationRenderer;
import org.auvua.view.RChart;

public class DangerZonaAgent {
  
  public static Task command;
  public static Map<Character,Integer> keyMap = new HashMap<Character,Integer>();
  public static DangerZona robot = DangerZonaFactory.build(RobotType.DANGER_ZONA_SIM);

  public static void main( String[] args ) throws SecurityException, IOException {
    
    buildFrames();
    
    DataRecorder recorder = new DataRecorder("data2.txt");
    recorder.record(robot.hardware.getOutputs().positionSensor.x, "xPos");
    recorder.record(robot.hardware.getOutputs().positionSensor.y, "yPos");
    recorder.record(robot.hardware.getOutputs().velocitySensor.x, "yVel");
    recorder.start();
    
    DangerZonaOrientationRenderer or = new DangerZonaOrientationRenderer(robot);

    new Thread(() -> {
      while(true) {
        robot.update();
        or.update();
        try {
          Thread.sleep(30);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  private static void buildFrames() {
    OperatorInterface2 oi = new OperatorInterface2();
    
    DangerZonaInputs inputs = robot.hardware.getInputs();
    DangerZonaOutputs outputs = robot.hardware.getOutputs();
    
    inputs.frontRight.setSupplier(() -> 5000 * (oi.forward.get() - oi.strafe.get() + oi.rotation.get()));
    inputs.frontLeft.setSupplier(() -> 5000 * (oi.forward.get() + oi.strafe.get() - oi.rotation.get()));
    inputs.rearLeft.setSupplier(() -> 5000 * (oi.forward.get() - oi.strafe.get() - oi.rotation.get()));
    inputs.rearRight.setSupplier(() -> 5000 * (oi.forward.get() + oi.strafe.get() + oi.rotation.get()));
    
    inputs.heaveFrontRight.setSupplier(() -> 5000 * (oi.elevation.get() + oi.pitch.get() - oi.roll.get()));
    inputs.heaveFrontLeft.setSupplier(() -> 5000 * (oi.elevation.get() + oi.pitch.get() + oi.roll.get()));
    inputs.heaveRearLeft.setSupplier(() -> 5000 * (oi.elevation.get() - oi.pitch.get() + oi.roll.get()));
    inputs.heaveRearRight.setSupplier(() -> 5000 * (oi.elevation.get() - oi.pitch.get() - oi.roll.get()));
    
    RChart chart = new RChart(800, 600);
    
    chart.observe(outputs.depthSensor, "Depth");
    chart.observe(oi.elevation, "Elevation Control");
    
    chart.observe(outputs.gyroRateX, "Gyro Rate X");
    chart.observe(outputs.gyroRateY, "Gyro Rate Y");
    chart.observe(outputs.gyroRateZ, "Gyro Rate Z");
    
    JFrame frame2 = new JFrame();
    frame2.setSize(new Dimension(800, 600));
    frame2.setVisible(true);
    frame2.add(chart.getPanel());
    frame2.addKeyListener(oi.getKeyListener());
  }

}
