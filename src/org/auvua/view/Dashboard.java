package org.auvua.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTextArea;

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
  private JTextArea sensorData = new JTextArea(5,30);

  public Dashboard(DangerZona robot) {
    this.robot = robot;
    this.tkl = new TeleopKeyListener();
    this.or = new OrientationRenderer(robot.calcKinematics.get().orientation);
    buildFrame();
  }

  public void update() {
    or.update();

    DangerZonaOutputs outputs = robot.hardware.getOutputs();

    sensorData.setText(String.format(
        "GyroX: %-12.4f\n" +
            "GyroY: %-12.4f\n" +
            "GyroZ: %-12.4f\n" +
            "AccelX: %-12.4f\n" +
            "AccelY: %-12.4f\n" +
            "AccelZ: %-12.4f\n",
            outputs.gyroRateX.get(),
            outputs.gyroRateY.get(),
            outputs.gyroRateZ.get(),
            outputs.accelX.get(),
            outputs.accelY.get(),
            outputs.accelZ.get()));
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
    frame.setSize(new Dimension(800, 600));
    frame.setVisible(true);
    frame.setLayout(new FlowLayout());
    sensorData.setEditable(false);

    frame.add(sensorData);
    frame.add(or.orientationCam.getCanvas3D());
    frame.addKeyListener(tkl.getKeyListener());

    for(Component c : getAllComponents(frame)) {
      c.addKeyListener(tkl.getKeyListener());
    }
    
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
    
    chart.observe(inputs.frontRight, "FR");
    chart.observe(inputs.frontLeft, "FL");
    chart.observe(inputs.rearLeft, "RL");
    chart.observe(inputs.rearRight, "RR");
    chart.observe(inputs.heaveFrontRight, "Heave FR");
    chart.observe(inputs.heaveFrontLeft, "Heave FL");
    chart.observe(inputs.heaveRearLeft, "Heave RL");
    chart.observe(inputs.heaveRearRight, "Heave RR");
    
    frame.add(chart.getValuePanel());
  }

}
