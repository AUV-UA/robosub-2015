package org.auvua.model.component;

import javax.vecmath.Matrix3d;

public class MassProperties {
  
  public double mass;
  public Matrix3d inertiaTensor;
  
  public MassProperties(double mass, Matrix3d inertiaTensor) {
    this.mass = mass;
    this.inertiaTensor = inertiaTensor;
  }
  
  
  
}
