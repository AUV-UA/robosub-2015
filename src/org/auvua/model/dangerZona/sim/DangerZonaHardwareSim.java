package org.auvua.model.dangerZona.sim;

import org.auvua.agent.TwoVector;
import org.auvua.agent.control.HardLimit;
import org.auvua.agent.control.RateLimiter;
import org.auvua.agent.control.Timer;
import org.auvua.agent.signal.FirstOrderSystem;
import org.auvua.agent.signal.Integrator;
import org.auvua.model.component.DangerZonaInputs;
import org.auvua.model.component.DangerZonaOutputs;
import org.auvua.model.dangerZona.DangerZonaHardware;
import org.auvua.model.dangerZona.DangerZonaPhysicsModel;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;
import org.auvua.view.DangerZonaRenderer;

public class DangerZonaHardwareSim implements DangerZonaHardware {
  
  public RxVar<DangerZonaPhysicsModel> physicsModel;
  
  public RxVar<Double> depthSensor;
  public RxVar<Double> gyroRateX;
  public RxVar<Double> gyroRateY;
  public RxVar<Double> gyroRateZ;
  public TwoVector positionSensor;
  public TwoVector velocitySensor;
  
  public DangerZonaInputs inputs = new DangerZonaInputs();
  public DangerZonaOutputs outputs = new DangerZonaOutputs();
  
  public final RxVar<Double> frontRight = new RateLimiter(new HardLimit(inputs.frontRight, -5000, 5000), 10000);
  public final RxVar<Double> frontLeft = new RateLimiter(new HardLimit(inputs.frontLeft, -5000, 5000), 10000);
  public final RxVar<Double> rearLeft = new RateLimiter(new HardLimit(inputs.rearLeft, -5000, 5000), 10000);
  public final RxVar<Double> rearRight = new RateLimiter(new HardLimit(inputs.rearRight, -5000, 5000), 10000);
  public final RxVar<Double> heaveFrontRight = new RateLimiter(new HardLimit(inputs.heaveFrontRight, -5000, 5000), 10000);
  public final RxVar<Double> heaveFrontLeft = new RateLimiter(new HardLimit(inputs.heaveFrontLeft, -5000, 5000), 10000);
  public final RxVar<Double> heaveRearLeft = new RateLimiter(new HardLimit(inputs.heaveRearLeft, -5000, 5000), 10000);
  public final RxVar<Double> heaveRearRight = new RateLimiter(new HardLimit(inputs.heaveRearRight, -5000, 5000), 10000);
  
  public DangerZonaRenderer r;
  
  public DangerZonaHardwareSim(DangerZonaPhysicsModel physicsModel) {
    this.physicsModel = R.var(physicsModel);
    r = new DangerZonaRenderer(physicsModel);
    
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
    outputs.depthSensor = new FirstOrderSystem(R.var(() -> {
      return -physicsModel.get().kinematics.pos.z;
    }), 10);
    
    outputs.gyroRateX = new FirstOrderSystem(R.var(() -> {
      DangerZonaPhysicsModel robot = physicsModel.get();
      return robot.kinematics.orientation.localX.dot(robot.kinematics.angVel);
    }), 10);
    
    outputs.gyroRateY = new FirstOrderSystem(R.var(() -> {
      DangerZonaPhysicsModel robot = physicsModel.get();
      return robot.kinematics.orientation.localY.dot(robot.kinematics.angVel);
    }), 10);
    
    outputs.gyroRateZ = new FirstOrderSystem(R.var(() -> {
      DangerZonaPhysicsModel robot = physicsModel.get();
      return robot.kinematics.orientation.localZ.dot(robot.kinematics.angVel);
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
  }
  
  @Override
  public void update() {
    R.doSync(() -> {
      Timer.getInstance().trigger();
      physicsModel.mod((robot) -> {
        robot.t1.setThrust(frontRight.get());
        robot.t2.setThrust(frontLeft.get());
        robot.t3.setThrust(rearLeft.get());
        robot.t4.setThrust(rearRight.get());
        robot.t5.setThrust(heaveFrontRight.get());
        robot.t6.setThrust(heaveFrontLeft.get());
        robot.t7.setThrust(heaveRearLeft.get());
        robot.t8.setThrust(heaveRearRight.get());
        robot.update();
      });
      r.update();
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
}
