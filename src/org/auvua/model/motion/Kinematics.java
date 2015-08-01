package org.auvua.model.motion;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import org.auvua.agent.control.Timer;

public class Kinematics {
  public final Vector3d accel = new Vector3d();
  public final Vector3d vel = new Vector3d();
  public final Vector3d pos;
  
  public final Vector3d angAccel = new Vector3d();
  public final Vector3d angVel = new Vector3d();
  
  public final Orientation orientation;
  
  private double lastTime = Timer.getInstance().get();
  
  public Kinematics(Vector3d pos, Orientation orientation) {
    this.pos = pos;
    this.orientation = orientation;
  }
  
  public Kinematics(Vector3d pos) {
    this.pos = pos;
    this.orientation = new Orientation();
  }
  
  public Kinematics() {
    this(new Vector3d());
  }

  public void update() {
    double time = Timer.getInstance().get();
    double dt = time - lastTime;
    
    vel.scaleAdd(dt, accel, vel);
    Vector3d dPos = new Vector3d(vel);
    dPos.scale(dt);
    translate(dPos);
    
    angVel.scaleAdd(dt, angAccel, angVel);
    Vector3d dAngPos = new Vector3d(angVel);
    dAngPos.scale(dt);
    orientation.rotate(new AxisAngle4d(dAngPos, dAngPos.length()));
    
    lastTime = time;
  }
  
  public void translate(Vector3d vector) {
    pos.add(vector);
  }
}
