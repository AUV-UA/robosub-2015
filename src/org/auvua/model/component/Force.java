package org.auvua.model.component;

import javax.media.j3d.Transform3D;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import org.auvua.model.motion.Kinematics;

public class Force extends PhysicsObject {
  
  private Vector3d force;
  
  public Force(Vector3d location, Vector3d force) {
    super(new Kinematics(location));
    this.force = force;
  }

  public Vector3d getForce() {
    Vector3d f = new Vector3d(force);
    Matrix3d rot = new Matrix3d(this.parent.kinematics.orientation.asMatrix3d());
    Transform3D trans = new Transform3D();
    trans.setRotation(rot);
    trans.transform(f);
    return f;
  }
  
  public Vector3d getMoment() {
    return new Vector3d();
  }
}
