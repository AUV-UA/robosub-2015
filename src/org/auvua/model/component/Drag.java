package org.auvua.model.component;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import org.auvua.model.motion.Kinematics;

public class Drag {
  
  public Vector3d transDrag = new Vector3d();
  public Vector3d rotDrag = new Vector3d();
  public Vector3d transCoeff;
  public Vector3d rotCoeff;
  public Kinematics kinematics;
  
  public Drag (Vector3d transCoeff, Vector3d rotCoeff, Kinematics kinematics) {
    this.transCoeff = transCoeff;
    this.rotCoeff = rotCoeff;
    this.kinematics = kinematics;
  }
  
  public void update() {
    Matrix3d localToGlobal = kinematics.orientation.asMatrix3d();
    Matrix3d globalToLocal = new Matrix3d(localToGlobal);
    globalToLocal.transpose();
    
    Vector3d orientedVel = new Vector3d();
    globalToLocal.transform(kinematics.vel, orientedVel);
    
    Vector3d orientedAngVel = new Vector3d();
    globalToLocal.transform(kinematics.angVel, orientedAngVel);
    
    transDrag.x = -Math.abs(orientedVel.x) * orientedVel.x * transCoeff.x;
    transDrag.y = -Math.abs(orientedVel.y) * orientedVel.y * transCoeff.y;
    transDrag.z = -Math.abs(orientedVel.z) * orientedVel.z * transCoeff.z;
    
    rotDrag.x = -Math.abs(orientedAngVel.x) * orientedAngVel.x * rotCoeff.x;
    rotDrag.y = -Math.abs(orientedAngVel.y) * orientedAngVel.y * rotCoeff.y;
    rotDrag.z = -Math.abs(orientedAngVel.z) * orientedAngVel.z * rotCoeff.z;
  }
  
}
