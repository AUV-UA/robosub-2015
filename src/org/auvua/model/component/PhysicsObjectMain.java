package org.auvua.model.component;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

public class PhysicsObjectMain {

  public static void main(String[] args) {
    Vector3d location = new Vector3d(0,0,0);
    Matrix3d inertiaTensor = new Matrix3d(new double[] {
      1.0, 0.0, 0.0,
      0.0, 1.0, 0.0,
      0.0, 0.0, 1.0
    });
    MassProperties massProps = new MassProperties(10.0, inertiaTensor);
    PhysicsObject obj = new PhysicsObject(location, massProps);
    
    System.out.println(obj.getAngularAcceleration());
  }

}
