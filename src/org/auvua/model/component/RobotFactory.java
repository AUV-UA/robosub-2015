package org.auvua.model.component;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import org.auvua.model.motion.Kinematics;

public class RobotFactory {
  
  public static PhysicsRobot build(RobotType type) {
    switch(type) {
      case DANGER_ZONA:
        return buildDangerZona();
      default:
        return null;
    }
  }
  
  public enum RobotType {
    DANGER_ZONA
  }
  
  public static PhysicsRobot buildDangerZona() {
    Kinematics kinematics = new Kinematics();
    MassProperties massProperties = new MassProperties(40.0, new Matrix3d(new double[] {
        10000.0, 0.0, 0.0,
        0.0, 10000.0, 0.0,
        0.0, 0.0, 10000.0
    }));
    Drag drag = new Drag(new Vector3d(1, .5, 1), new Vector3d(30000, 30000, 30000), kinematics);
    return new PhysicsRobot(kinematics, massProperties, drag);
  }
  
}
