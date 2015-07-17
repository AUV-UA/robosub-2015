package org.auvua.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import org.auvua.agent.oi.OperatorInterface;
import org.auvua.model.dangerZona.DangerZona;
import org.auvua.model.dangerZona.DangerZonaOutputs;

public class Dashboard {
  
  private DangerZona robot;
  public final OperatorInterface oi;
  public final OrientationRenderer or;
  
  private JFrame frame;
  private JTextArea sensorData = new JTextArea(5,30);

  public Dashboard(DangerZona robot) {
    this.robot = robot;
    
    oi = new OperatorInterface();
    
    frame = new JFrame();
    frame.setSize(new Dimension(400, 400));
    frame.setVisible(true);
    frame.setLayout(new FlowLayout());
    
    or = new OrientationRenderer(robot.calcKinematics.get().orientation);
    
    sensorData.setEditable(false);
    
    frame.add(sensorData);
    frame.add(or.universe.getCanvas());
    frame.addKeyListener(oi.getKeyListener());
    
    for(Component c : getAllComponents(frame)) {
      c.addKeyListener(oi.getKeyListener());
    }
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

}
