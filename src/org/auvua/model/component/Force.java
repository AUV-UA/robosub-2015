package org.auvua.model.component;

import javax.vecmath.Vector3d;

public class Force extends PhysicsObject2 {
  
  private Vector3d force;
  
  public Force(Vector3d location, Vector3d force) {
    super(new Kinematics(location));
    this.force = force;
  }

  public Vector3d getForce() {
    return force;
  }
  
  public Vector3d getMoment() {
    return new Vector3d();
  }
}
