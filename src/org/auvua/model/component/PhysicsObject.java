package org.auvua.model.component;

import java.util.LinkedList;
import java.util.List;

import javax.media.j3d.Transform3D;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import org.auvua.agent.control.Timer;

public class PhysicsObject {
  
  public Vector3d accel = new Vector3d();
  public Vector3d vel = new Vector3d();
  public Vector3d pos;
  
  public Vector3d angAccel = new Vector3d();
  public Vector3d angVel = new Vector3d();
  
  public Vector3d localX = new Vector3d(1,0,0);
  public Vector3d localY = new Vector3d(0,1,0);
  public Vector3d localZ = new Vector3d(0,0,1);
  
  protected Vector3d locationFromParent;
  
  protected PhysicsObject parent;
  protected List<PhysicsObject> children = new LinkedList<PhysicsObject>();
  
  protected MassProperties massProperties;
  
  private double lastTime = Timer.getInstance().get();
  
  public PhysicsObject(Vector3d location, MassProperties massProperties) {

    this.pos = location;
    
    this.massProperties = massProperties;
  }
  
  public void addChild(PhysicsObject child) {
    children.add(child);
    child.parent = this;
    Vector3d toChild = new Vector3d();
    
    Matrix3d transform = new Matrix3d();
    transform.setColumn(0, localX);
    transform.setColumn(1, localY);
    transform.setColumn(2, localZ);
    transform.transform(child.pos, toChild);
    
    child.locationFromParent = toChild;
  }
  
  public void addChildren(PhysicsObject ... children) {
    for(PhysicsObject child : children) {
      addChild(child);
    }
  }
  
  public void translate(Vector3d vector) {
    pos.add(vector);
    children.forEach((child) -> {
      child.translate(vector);
    });
  }
  
  public void rotate(AxisAngle4d aa) {
    Transform3D trans = new Transform3D();
    trans.setRotation(aa);
    
    trans.transform(localX);
    trans.transform(localY);
    trans.transform(localZ);
    if (locationFromParent != null) {
      trans.transform(locationFromParent);
    }
    
    if (parent != null) {
      pos.add(parent.pos, locationFromParent);
    }
    
    children.forEach((child) -> {
      child.rotate(aa);
    });
  }
  
  public Vector3d getForce() {
    Vector3d force = new Vector3d();
    children.forEach((child) -> {
      force.add(child.getForce());
    });
    return force;
  }
  
  public Vector3d getMoment() {
    Vector3d moment = new Vector3d();
    children.forEach((child) -> {
      Vector3d cross = new Vector3d();
      cross.cross(child.locationFromParent, child.getForce());
      moment.add(child.getMoment());
      moment.add(cross);
    });
    return moment;
  }
  
  public Vector3d getAngularAcceleration() {
    Vector3d moment = getMoment();
    Matrix3d inertiaTensor = massProperties.inertiaTensor;
    Matrix3d inverse = new Matrix3d();
    inverse.invert(inertiaTensor);
    Vector3d angularAccel = new Vector3d();
    inverse.transform(moment, angularAccel);
    return angularAccel;
  }
  
  public Vector3d getAcceleration() {
    Vector3d accel = new Vector3d();
    accel.scale(1 / massProperties.mass, getForce());
    return accel;
  }
  
  public void update() {
    double time = Timer.getInstance().get();
    double dt = time - lastTime;
    
    accel = getAcceleration();
    vel.scaleAdd(dt, accel, vel);
    Vector3d dPos = new Vector3d(vel);
    dPos.scale(dt);
    translate(dPos);
    
    angAccel = getAngularAcceleration();
    angVel.scaleAdd(dt, angAccel, angVel);
    Vector3d dAngPos = new Vector3d(angVel);
    dAngPos.scale(dt);
    rotate(new AxisAngle4d(dAngPos, dAngPos.length()));
  }

}
