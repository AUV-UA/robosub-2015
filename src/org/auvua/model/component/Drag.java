package org.auvua.model.component;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

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
    Matrix3d globalToLocal = new Matrix3d();
    globalToLocal.setRow(0, kinematics.localX);
    globalToLocal.setRow(1, kinematics.localY);
    globalToLocal.setRow(2, kinematics.localZ);
    
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
    
    Matrix3d transform = new Matrix3d();
    transform.setColumn(0, kinematics.localX);
    transform.setColumn(1, kinematics.localY);
    transform.setColumn(2, kinematics.localZ);
    transform.transform(localTransDrag, transDrag);
    transform.transform(localRotDrag, rotDrag);
  }
  
}
