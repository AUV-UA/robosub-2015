package org.auvua.model.dangerZona;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

import org.auvua.agent.control.Timer;
import org.auvua.model.motion.Orientation;

public class DzCalculatedKinematics {
  public final Vector3d vel = new Vector3d();
  public final Vector3d pos = new Vector3d();
  
  public final Vector3d angVel = new Vector3d();
  
  public final Orientation orientation = new Orientation();
  
  private double lastTime = Timer.getInstance().get();
  
  public DzCalculatedKinematics() {}

  public void update() {
    double time = Timer.getInstance().get();
    double dt = time - lastTime;
    
    Vector3d dPos = new Vector3d(vel);
    dPos.scale(dt);
    translate(dPos);
    
    Vector3d dAngPos = new Vector3d(angVel);
    dAngPos.scale(dt);
    rotate(new AxisAngle4d(dAngPos, dAngPos.length()));
    
    lastTime = time;
  }
  
  public void translate(Vector3d vector) {
    pos.add(vector);
  }
  
  public void rotate(AxisAngle4d aa) {
    orientation.rotate(aa);
  }
}
