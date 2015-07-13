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
    Matrix3d localToGlobal = kinematics.orientation.asMatrix();
    Matrix3d globalToLocal = new Matrix3d(localToGlobal);
    globalToLocal.transpose();
    
    Vector3d orientedVel = new Vector3d();
    globalToLocal.transform(kinematics.vel, orientedVel);
    
    Vector3d orientedAngVel = new Vector3d();
    globalToLocal.transform(kinematics.angVel, orientedAngVel);
    
    Vector3d localTransDrag = new Vector3d();
    Vector3d localRotDrag = new Vector3d();
    
    localTransDrag.x = -Math.abs(orientedVel.x) * orientedVel.x * transCoeff.x;
    localTransDrag.y = -Math.abs(orientedVel.y) * orientedVel.y * transCoeff.y;
    localTransDrag.z = -Math.abs(orientedVel.z) * orientedVel.z * transCoeff.z;
    
    localRotDrag.x = -Math.abs(orientedAngVel.x) * orientedAngVel.x * rotCoeff.x;
    localRotDrag.y = -Math.abs(orientedAngVel.y) * orientedAngVel.y * rotCoeff.y;
    localRotDrag.z = -Math.abs(orientedAngVel.z) * orientedAngVel.z * rotCoeff.z;
    
    localToGlobal.transform(localTransDrag, transDrag);
    localToGlobal.transform(localRotDrag, rotDrag);
  }
  
}
