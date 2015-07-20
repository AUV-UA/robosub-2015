package org.auvua.model.dangerZona;

import java.util.Random;

import javax.vecmath.Vector3d;

import org.auvua.agent.TwoVector;
import org.auvua.agent.control.HardLimit;
import org.auvua.agent.control.RateLimiter;
import org.auvua.agent.control.Timer;
import org.auvua.agent.signal.Delayer;
import org.auvua.agent.signal.FirstOrderSystem;
import org.auvua.agent.signal.Integrator;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;
import org.auvua.view.DangerZonaRenderer;
import org.auvua.vision.CameraSim;

public class DzHardwareSim implements DzHardware {
  
  public RxVar<DangerZonaPhysicsModel> physicsModel = R.var(DangerZonaPhysicsModel.getInstance());
  
  public RxVar<Double> depthSensor;
  public RxVar<Double> gyroRateX;
  public RxVar<Double> gyroRateY;
  public RxVar<Double> gyroRateZ;
  public TwoVector positionSensor;
  public TwoVector velocitySensor;
  
  public DangerZonaInputs inputs = new DangerZonaInputs();
  public DangerZonaOutputs outputs = new DangerZonaOutputs();
  
  public final RxVar<Double> frontRight = new Delayer(new RateLimiter(new HardLimit(inputs.frontRight, -35.6, 35.6), 35.6 * 2), .02);
  public final RxVar<Double> frontLeft = new Delayer(new RateLimiter(new HardLimit(inputs.frontLeft, -35.6, 35.6), 35.6 * 2), .02);
  public final RxVar<Double> rearLeft = new Delayer(new RateLimiter(new HardLimit(inputs.rearLeft, -35.6, 35.6), 35.6 * 2), .02);
  public final RxVar<Double> rearRight = new Delayer(new RateLimiter(new HardLimit(inputs.rearRight, -35.6, 35.6), 35.6 * 2), .02);
  public final RxVar<Double> heaveFrontRight = new Delayer(new RateLimiter(new HardLimit(inputs.heaveFrontRight, -26.7, 26.7), 26.7 * 2), .02);
  public final RxVar<Double> heaveFrontLeft = new Delayer(new RateLimiter(new HardLimit(inputs.heaveFrontLeft, -26.7, 26.7), 26.7 * 2), .02);
  public final RxVar<Double> heaveRearLeft = new Delayer(new RateLimiter(new HardLimit(inputs.heaveRearLeft, -26.7, 26.7), 26.7 * 2), .02);
  public final RxVar<Double> heaveRearRight = new Delayer(new RateLimiter(new HardLimit(inputs.heaveRearRight, -26.7, 26.7), 26.7 * 2), .02);
  
  public DangerZonaRenderer r;
  
  public DzHardwareSim() {
    r = DangerZonaRenderer.getInstance();
    
    this.physicsModel.setModifier((model) -> {
      model.t1.setThrust(frontRight.get());
      model.t2.setThrust(frontLeft.get());
      model.t3.setThrust(rearLeft.get());
      model.t4.setThrust(rearRight.get());
      model.t5.setThrust(heaveFrontRight.get());
      model.t6.setThrust(heaveFrontLeft.get());
      model.t7.setThrust(heaveRearLeft.get());
      model.t8.setThrust(heaveRearRight.get());
      model.update();
      r.update();
    });
    
    createSensors();
  }

  private void createSensors() {
    outputs.depthSensor = new Delayer(new FirstOrderSystem(R.var(() -> {
      return -physicsModel.get().kinematics.pos.z + new Random().nextGaussian() * .02;
    }), 10), .02);
    
    outputs.gyroRateX = R.var(() -> {
      DangerZonaPhysicsModel robot = physicsModel.get();
      return robot.kinematics.orientation.localX.dot(robot.kinematics.angVel);
    });
    
    outputs.gyroRateY = R.var(() -> {
      DangerZonaPhysicsModel robot = physicsModel.get();
      return robot.kinematics.orientation.localY.dot(robot.kinematics.angVel);
    });
    
    outputs.gyroRateZ = R.var(() -> {
      DangerZonaPhysicsModel robot = physicsModel.get();
      return robot.kinematics.orientation.localZ.dot(robot.kinematics.angVel);
    });
    
    outputs.accelX = new FirstOrderSystem(R.var(() -> {
      DangerZonaPhysicsModel robot = physicsModel.get();
      Vector3d accel = new Vector3d(robot.kinematics.accel);
      accel.add(new Vector3d(0.0, 0.0, -9.81));
      return robot.kinematics.orientation.localX.dot(accel);
    }), 10);
    
    outputs.accelY = new FirstOrderSystem(R.var(() -> {
      DangerZonaPhysicsModel robot = physicsModel.get();
      Vector3d accel = new Vector3d(robot.kinematics.accel);
      accel.add(new Vector3d(0.0, 0.0, -9.81));
      return robot.kinematics.orientation.localY.dot(accel);
    }), 10);
    
    outputs.accelZ = new FirstOrderSystem(R.var(() -> {
      DangerZonaPhysicsModel robot = physicsModel.get();
      Vector3d accel = new Vector3d(robot.kinematics.accel);
      accel.add(new Vector3d(0.0, 0.0, -9.81));
      return robot.kinematics.orientation.localZ.dot(accel);
    }), 10);
    
    outputs.velocitySensor = new TwoVector(
        new FirstOrderSystem(R.var(() -> {
          return physicsModel.get().kinematics.vel.x;
        }), 10),
        new FirstOrderSystem(R.var(() -> {
          return physicsModel.get().kinematics.vel.y;
        }), 10));
    
    outputs.positionSensor = new TwoVector(
        new Integrator(R.var(() -> {
          return physicsModel.get().kinematics.pos.x;
        }), Timer.getInstance()),
        new Integrator(R.var(() -> {
          return physicsModel.get().kinematics.pos.y;
        }), Timer.getInstance()));
    
    outputs.frontCamera = R.var(new CameraSim(r.frontCameraCanvas));
    outputs.downCamera = R.var(new CameraSim(r.downCameraCanvas));
    
    r.frontCameraCanvas.beginCapturing(true);
    r.downCameraCanvas.beginCapturing(true);
    
    outputs.frontCamera.setModifier((camera) -> {
      this.physicsModel.get();
      camera.capture();
    });
    
    outputs.downCamera.setModifier((camera) -> {
      this.physicsModel.get();
      camera.capture();
    });

  }

  @Override
  public DangerZonaInputs getInputs() {
    return inputs;
  }

  @Override
  public DangerZonaOutputs getOutputs() {
    return outputs;
  }
  
  @Override
  public void update() {
    inputs.trigger();
  }
}
