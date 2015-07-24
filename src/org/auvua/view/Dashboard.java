package org.auvua.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.auvua.agent.oi.TeleopKeyListener;
import org.auvua.model.dangerZona.DangerZona;
import org.auvua.model.dangerZona.hardware.DangerZonaInputs;
import org.auvua.model.dangerZona.hardware.DangerZonaOutputs;
import org.auvua.reactive.core.R;

public class Dashboard {

  private DangerZona robot;
  public final TeleopKeyListener tkl;
  public final OrientationRenderer or;

  private JFrame frame;
  private JPanel cameraPanel = new JPanel(new GridLayout(2,1));
  private JPanel rightPanel = new JPanel(new GridLayout(2,1));

  public Dashboard(DangerZona robot) {
    this.robot = robot;
    this.tkl = new TeleopKeyListener();
    this.or = new OrientationRenderer(robot.calcKinematics.get().orientation);
    buildFrame();
  }

  public void update() {
    or.update();
    frame.repaint();
  }

  public static List<Component> getAllComponents(final Container c) {
    Component[] comps = c.getComponents();
    List<Component> compList = new ArrayList<Component>();
    for (Component comp : comps) {
      compList.add(comp);
      if (comp instanceof Container)
        compList.addAll(getAllComponents((Container) comp));
    }
    return compList;
  }
  
  private void buildFrame() {
    frame = new JFrame();
    frame.setLayout(new GridLayout(1,2));
    frame.setSize(new Dimension(1280, 720));
    frame.setVisible(true);
    
    frame.add(cameraPanel);
    frame.add(rightPanel);

    //rightPanel.add(or.orientationCam.getCanvas3D());
    
    DangerZonaOutputs outputs = robot.hardware.getOutputs();
    DangerZonaInputs inputs = robot.hardware.getInputs();
    
    RChart chart = new RChart(800, 600);
    
    chart.observe(outputs.gyroRateX, "Gyro Rate X");
    chart.observe(outputs.gyroRateY, "Gyro Rate Y");
    chart.observe(outputs.gyroRateZ, "Gyro Rate Z");
    chart.observe(outputs.accelX, "Accel X");
    chart.observe(outputs.accelY, "Accel Y");
    chart.observe(outputs.accelZ, "Accel Z");
    chart.observe(outputs.depthSensor, "Depth");
    chart.observe(outputs.humidity, "Humidity");
    chart.observe(R.var(() -> robot.calcKinematics.get().orientation.getYaw() * 180 / Math.PI), "Yaw");
    chart.observe(R.var(() -> robot.calcKinematics.get().pos.x), "Pos X");
    chart.observe(R.var(() -> robot.calcKinematics.get().pos.y), "Pos Y");
    chart.observe(R.var(() -> robot.calcKinematics.get().pos.z), "Pos Z");
    chart.observe(R.var(() -> outputs.missionSwitch.get() ? 1.0 : 0.0), "MissionSwitch");
    
    chart.observe(inputs.frontRight, "FR");
    chart.observe(inputs.frontLeft, "FL");
    chart.observe(inputs.rearLeft, "RL");
    chart.observe(inputs.rearRight, "RR");
    chart.observe(inputs.heaveFrontRight, "Heave FR");
    chart.observe(inputs.heaveFrontLeft, "Heave FL");
    chart.observe(inputs.heaveRearLeft, "Heave RL");
    chart.observe(inputs.heaveRearRight, "Heave RR");
    
    rightPanel.add(chart.getValuePanel());
    
    CameraPanel cp1 = new CameraPanel(robot.hardware.getOutputs().frontCamera.get());
    CameraPanel cp2 = new CameraPanel(robot.hardware.getOutputs().downCamera.get());
    
    cameraPanel.add(cp1);
    cameraPanel.add(cp2);
    
    frame.pack();
    
    frame.addKeyListener(tkl.getKeyListener());

    for(Component c : getAllComponents(frame)) {
      c.addKeyListener(tkl.getKeyListener());
    }
  }

}
