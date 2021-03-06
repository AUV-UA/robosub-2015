package org.auvua.model.component;

import java.util.LinkedList;
import java.util.List;

import javax.media.j3d.Transform3D;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import org.auvua.agent.control.Timer;
import org.auvua.model.motion.Kinematics;

public abstract class PhysicsObject2 {
  
  public final Kinematics kinematics;
  public final Drag drag;
  public final MassProperties massProperties;
  
  protected Vector3d locationFromParent;
  
  protected PhysicsObject2 parent;
  protected List<PhysicsObject2> children = new LinkedList<PhysicsObject2>();
  
  private double lastTime = Timer.getInstance().get();
  
  public PhysicsObject2(Kinematics kinematics) {
    this.kinematics = kinematics;
    this.massProperties = new MassProperties(0.0, new Matrix3d());
    this.drag = new Drag(new Vector3d(), new Vector3d(), kinematics);
  }
  
  public PhysicsObject2(Kinematics kinematics, MassProperties massProperties, Drag drag) {
    this.kinematics = kinematics;
    this.massProperties = massProperties;
    this.drag = drag;
  }

  public void addChild(PhysicsObject2 child) {
    children.add(child);
    child.parent = this;
    Vector3d toChild = new Vector3d();
    
    Matrix3d transform = kinematics.orientation.asMatrix();
    transform.transform(child.kinematics.pos, toChild);
    
    child.locationFromParent = toChild;
  }
  
  public void addChildren(PhysicsObject2 ... children) {
    for(PhysicsObject2 child : children) {
      addChild(child);
    }
  }
  
  public void translate(Vector3d vector) {
    kinematics.pos.add(vector);
    children.forEach((child) -> {
      child.translate(vector);
    });
  }
  
  public void rotate(AxisAngle4d aa) {
    kinematics.orientation.rotate(aa);
    
    Transform3D trans = new Transform3D();
    trans.setRotation(aa);
    if (locationFromParent != null) {
      trans.transform(locationFromParent);
    }
    
    if (parent != null) {
      kinematics.pos.add(parent.kinematics.pos, locationFromParent);
    }
    
    children.forEach((child) -> {
      child.rotate(aa);
    });
  }
  
  public Vector3d getForce() {
    Vector3d force = new Vector3d();
    force.add(drag.transDrag);
    
    children.forEach((child) -> {
      force.add(child.getForce());
    });
    return force;
  }
  
  public Vector3d getMoment() {
    Vector3d moment = new Vector3d();
    moment.add(drag.rotDrag);
    
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
    drag.update();
    
    double time = Timer.getInstance().get();
    double dt = time - lastTime;
    
    kinematics.accel.set(getAcceleration());
    kinematics.vel.scaleAdd(dt, kinematics.accel, kinematics.vel);
    Vector3d dPos = new Vector3d(kinematics.vel);
    dPos.scale(dt);
    translate(dPos);
    
    kinematics.angAccel.set(getAngularAcceleration());
    kinematics.angVel.scaleAdd(dt, kinematics.angAccel, kinematics.angVel);
    Vector3d dAngPos = new Vector3d(kinematics.angVel);
    dAngPos.scale(dt);
    rotate(new AxisAngle4d(dAngPos, dAngPos.length()));
    
    lastTime = time;
  }

}
