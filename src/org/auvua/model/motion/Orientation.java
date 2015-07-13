package org.auvua.model.motion;

import javax.media.j3d.Transform3D;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

public class Orientation {
  public final Vector3d localX; // pitch
  public final Vector3d localY; // roll
  public final Vector3d localZ; // yaw
  
  public Orientation() {
    localX = new Vector3d(1,0,0);
    localY = new Vector3d(0,1,0);
    localZ = new Vector3d(0,0,1);
  }
  
  public void rotate(AxisAngle4d aa) {
    Transform3D trans = new Transform3D();
    trans.setRotation(aa);
    
    trans.transform(localX);
    trans.transform(localY);
    trans.transform(localZ);
  }
  
  public Matrix3d asMatrix() {
    Matrix3d mat = new Matrix3d();
    mat.setColumn(0, localX);
    mat.setColumn(1, localY);
    mat.setColumn(2, localZ);
    return mat;
  }
}
