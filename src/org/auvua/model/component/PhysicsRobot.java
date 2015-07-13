package org.auvua.model.component;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

public class PhysicsRobot extends PhysicsObject2 {
  
  public Thruster t1 = new Thruster(new Vector3d(8.5, 10.5, 0));
  public Thruster t2 = new Thruster(new Vector3d(-8.5, 10.5, 0));
  public Thruster t3 = new Thruster(new Vector3d(-8.5, -10.5, 0));
  public Thruster t4 = new Thruster(new Vector3d(8.5, -10.5, 0));
  
  public Thruster t5 = new Thruster(new Vector3d(5.75, 8.5, 4));
  public Thruster t6 = new Thruster(new Vector3d(-5.75, 8.5, 4));
  public Thruster t7 = new Thruster(new Vector3d(-5.75, -8.5, 4));
  public Thruster t8 = new Thruster(new Vector3d(5.75, -8.5, 4));

  public PhysicsRobot(Kinematics kinematics, MassProperties massProperties, Drag drag) {
    super(kinematics, massProperties, drag);
    this.buildChildren();
  }

  private void buildChildren() {
    t1.rotate(new AxisAngle4d(0, 0, 1, Math.PI / 6));
    t2.rotate(new AxisAngle4d(0, 0, 1, -Math.PI / 6));
    t3.rotate(new AxisAngle4d(0, 0, 1, Math.PI / 6));
    t4.rotate(new AxisAngle4d(0, 0, 1, -Math.PI / 6));
    
    t5.rotate(new AxisAngle4d(1, 0, 0, Math.PI / 2));
    t6.rotate(new AxisAngle4d(1, 0, 0, Math.PI / 2));
    t7.rotate(new AxisAngle4d(1, 0, 0, Math.PI / 2));
    t8.rotate(new AxisAngle4d(1, 0, 0, Math.PI / 2));
    
    Force buoyancy = new Force(new Vector3d(0.0, 0.0, 4.0), new Vector3d(0.0, 0.0, 10000.0));
    Force gravity = new Force(new Vector3d(0.0, 0.0, 0.0), new Vector3d(0.0, 0.0, -10000.0));
    
    addChildren(t1, t2, t3, t4, t5, t6, t7, t8, buoyancy, gravity);
  }
  
  

}
