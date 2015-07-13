package org.auvua.model.component;

import org.auvua.agent.control.Timer;
import org.auvua.agent.oi.OperatorInterface2;
import org.auvua.model.component.RobotFactory.RobotType;
import org.auvua.view.PhysicsRobotRenderer;

public class PhysicsRobotMain {

  public static void main(String[] args) {
    PhysicsRobot obj = RobotFactory.build(RobotType.DANGER_ZONA);
    
    PhysicsRobotRenderer r = new PhysicsRobotRenderer(obj);
    OperatorInterface2 oi = new OperatorInterface2();
    r.universe.getCanvas().addKeyListener(oi.getKeyListener());
    
    double lastTime = Timer.getInstance().get();
    
    while(true) {
      Timer.getInstance().trigger();
      
      obj.t1.setThrust(3000 * (oi.forward.get() - oi.strafe.get() + oi.rotation.get()));
      obj.t2.setThrust(3000 * (oi.forward.get() + oi.strafe.get() - oi.rotation.get()));
      obj.t3.setThrust(3000 * (oi.forward.get() - oi.strafe.get() - oi.rotation.get()));
      obj.t4.setThrust(3000 * (oi.forward.get() + oi.strafe.get() + oi.rotation.get()));
      
      obj.t5.setThrust(3000 * (oi.elevation.get() + oi.pitch.get() - oi.roll.get()));
      obj.t6.setThrust(3000 * (oi.elevation.get() + oi.pitch.get() + oi.roll.get()));
      obj.t7.setThrust(3000 * (oi.elevation.get() - oi.pitch.get() + oi.roll.get()));
      obj.t8.setThrust(3000 * (oi.elevation.get() - oi.pitch.get() - oi.roll.get()));
      
      obj.update();
      r.update();
      
      double time = Timer.getInstance().get();
      System.out.println("FPS: " + (1 / (time - lastTime)));
      lastTime = time;
      
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

}
