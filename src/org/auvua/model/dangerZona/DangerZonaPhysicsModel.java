package org.auvua.model.dangerZona;

import jama.Matrix;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import org.auvua.model.component.Drag;
import org.auvua.model.component.Force;
import org.auvua.model.component.MassProperties;
import org.auvua.model.component.PhysicsObject;
import org.auvua.model.component.Thruster;
import org.auvua.model.motion.Kinematics;
import org.auvua.model.motion.Orientation;

public class DangerZonaPhysicsModel extends PhysicsObject {
  
  public Thruster t1 = new Thruster(new Vector3d( .216,  .267, 0));
  public Thruster t2 = new Thruster(new Vector3d(-.216,  .267, 0));
  public Thruster t3 = new Thruster(new Vector3d(-.216, -.267, 0));
  public Thruster t4 = new Thruster(new Vector3d( .216, -.267, 0));
  
  public Thruster t5 = new Thruster(new Vector3d( .146,  .216, 0.1));
  public Thruster t6 = new Thruster(new Vector3d(-.146,  .216, 0.1));
  public Thruster t7 = new Thruster(new Vector3d(-.146, -.216, 0.1));
  public Thruster t8 = new Thruster(new Vector3d( .146, -.216, 0.1));
  
  public PhysicsObject frontCamera = new PhysicsObject(
      new Kinematics(
          new Vector3d(0, .267, 0),
          new Orientation(new Matrix(new double[][] {
              {1, 0, 0},
              {0, 0, -1},
              {0, 1, 0},
          })
      )
  ));
  public PhysicsObject downCamera = new PhysicsObject(
      new Kinematics(
          new Vector3d(0, .267, -.051),
          new Orientation(new Matrix(new double[][] {
              {1, 0, 0},
              {0, 1, 0},
              {0, 0, 1},
          })
      )
  ));
  
  public static DangerZonaPhysicsModel instance;
  
  public static DangerZonaPhysicsModel getInstance() {
    if (instance == null) {
      instance = new DangerZonaPhysicsModel();
    }
    return instance;
  }

  public DangerZonaPhysicsModel() {
    Kinematics kinematics = new Kinematics(new Vector3d(0.0, 0.0, 0.0));
    
    MassProperties massProperties = new MassProperties(30.0, new Matrix3d(new double[] {
        .4874, 0.0, 0.0,
        0.0, .3877, 0.0,
        0.0, 0.0, .6998
    }));
    
    double rhoWater = 1000.0; // kg / m^3
    
    double cdX = 1.28; // Flat plate
    double cdY = 0.82; // Cylinder, axial
    double cdZ = 1.10; // Cylinder, radial
    
    double areaX = 1.0 / 10.76; // m^2
    double areaY = 0.1963 / 10.76; // m^2
    double areaZ = 1.0 / 10.76; // m^2
    
    double dragX = .5 * rhoWater * cdX * areaX * 10;
    double dragY = .5 * rhoWater * cdY * areaY * 10;
    double dragZ = .5 * rhoWater * cdZ * areaZ * 10;
    
    double rotDragX = 20;
    double rotDragY = 10;
    double rotDragZ = 20;
    
    Drag drag = new Drag(new Vector3d(dragX, dragY, dragZ), new Vector3d(rotDragX, rotDragY, rotDragZ), kinematics);
    
    this.kinematics = kinematics;
    this.massProperties = massProperties;
    this.drag = drag;

    this.buildChildren();
  }

  private void buildChildren() {
    t1.rotate(new AxisAngle4d(0, 0, 1, Math.PI / 6));
    t2.rotate(new AxisAngle4d(0, 0, 1, -Math.PI / 6));
    t3.rotate(new AxisAngle4d(0, 0, 1, Math.PI / 6));
    t4.rotate(new AxisAngle4d(0, 0, 1, -Math.PI / 6));
    
    t5.rotate(new AxisAngle4d(1, 0, 0, Math.PI / 2));
    t6.rotate(new AxisAngle4d(1, 0, 0, Math.PI / 2));
    t7.rotate(new AxisAngle4d(1, 0, 0, Math.PI / 2));
    t8.rotate(new AxisAngle4d(1, 0, 0, Math.PI / 2));
    
    Force buoyancy = new Force(new Vector3d(0.0, 0.0, 0.0), new Vector3d(0.0, 0.0, 30.1 * 9.81));
    Force gravity = new Force(new Vector3d(0.0, 0.0, 0.0), new Vector3d(0.0, 0.0, -30.0 * 9.81));
    
    addChildren(t1, t2, t3, t4, t5, t6, t7, t8, buoyancy, gravity, frontCamera, downCamera);
  }

}
