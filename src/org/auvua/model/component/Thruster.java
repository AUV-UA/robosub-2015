package org.auvua.model.component;

import javax.vecmath.Vector3d;

import org.auvua.model.motion.Kinematics;

public class Thruster extends PhysicsObject {
  
  private double thrust = 0;
  private Vector3d forceDirection;
  
  public Thruster(Vector3d location) {
    super(new Kinematics(location));
    forceDirection = kinematics.orientation.localY;
  }

  public Vector3d getForce() {
    Vector3d force = new Vector3d(0,0,0);
    force.scale(thrust, forceDirection);
    children.forEach((child) -> {
      force.add(child.getForce());
    });
    return force;
  }
  
  public Vector3d getMoment() {
    Vector3d moment = new Vector3d(0,0,0);
    children.forEach((child) -> {
      Vector3d cross = new Vector3d(0,0,0);
      cross.cross(child.locationFromParent, child.getForce());
      moment.add(child.getMoment());
      moment.add(cross);
    });
    return moment;
  }
  
  public void setThrust(double thrust) {
    this.thrust = thrust;
  }
}
