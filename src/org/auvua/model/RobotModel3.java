package org.auvua.model;
import java.util.HashMap;
import java.util.Map;

import org.auvua.agent.TwoVector;
import org.auvua.agent.control.Controllable;
import org.auvua.agent.control.HardLimit;
import org.auvua.agent.control.RateLimiter;
import org.auvua.agent.control.Timer;
import org.auvua.agent.signal.FirstOrderSystem;
import org.auvua.agent.signal.Integrator;
import org.auvua.model.component.PhysicsRobot;
import org.auvua.model.component.RobotFactory;
import org.auvua.model.component.RobotFactory.RobotType;
import org.auvua.model.motion.Kinematics;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;
import org.auvua.reactive.core.Triggerable;

public class RobotModel3 implements Controllable, Triggerable {
  
  private static RobotModel3 model;
  private Map<String, RxVar<?>> inputs = new HashMap<String, RxVar<?>>();
  private Map<String, RxVar<?>> outputs = new HashMap<String, RxVar<?>>();
  
  public final RxVar<PhysicsRobot> physicsRobot = R.var(RobotFactory.build(RobotType.DANGER_ZONA));
  
  public final RxVar<Double> frontRightInput = R.var(0.0);
  public final RxVar<Double> frontLeftInput = R.var(0.0);
  public final RxVar<Double> rearLeftInput = R.var(0.0);
  public final RxVar<Double> rearRightInput = R.var(0.0);
  public final RxVar<Double> heaveFrontRightInput = R.var(0.0);
  public final RxVar<Double> heaveFrontLeftInput = R.var(0.0);
  public final RxVar<Double> heaveRearLeftInput = R.var(0.0);
  public final RxVar<Double> heaveRearRightInput = R.var(0.0);

  public final RxVar<Double> frontRight = new RateLimiter(new HardLimit(frontRightInput, -5000, 5000), 10000);
  public final RxVar<Double> frontLeft = new RateLimiter(new HardLimit(frontLeftInput, -5000, 5000), 10000);
  public final RxVar<Double> rearLeft = new RateLimiter(new HardLimit(rearLeftInput, -5000, 5000), 10000);
  public final RxVar<Double> rearRight = new RateLimiter(new HardLimit(rearRightInput, -5000, 5000), 10000);
  public final RxVar<Double> heaveFrontRight = new RateLimiter(new HardLimit(heaveFrontRightInput, -5000, 5000), 10000);
  public final RxVar<Double> heaveFrontLeft = new RateLimiter(new HardLimit(heaveFrontLeftInput, -5000, 5000), 10000);
  public final RxVar<Double> heaveRearLeft = new RateLimiter(new HardLimit(heaveRearLeftInput, -5000, 5000), 10000);
  public final RxVar<Double> heaveRearRight = new RateLimiter(new HardLimit(heaveRearRightInput, -5000, 5000), 10000);
  
  public TwoVector velocitySensor = new TwoVector(
      new FirstOrderSystem(R.var(() -> {
        return physicsRobot.get().kinematics.vel.x;
      }), 5),
      new FirstOrderSystem(R.var(() -> {
        return physicsRobot.get().kinematics.vel.y;
      }), 5));
  
  public TwoVector positionSensor = new TwoVector(
      new Integrator(R.var(() -> {
        return physicsRobot.get().kinematics.pos.x;
      }), Timer.getInstance()),
      new Integrator(R.var(() -> {
        return physicsRobot.get().kinematics.pos.y;
      }), Timer.getInstance()));
  
  public RxVar<Double> depthSensor = new FirstOrderSystem(R.var(() -> {
    return -physicsRobot.get().kinematics.pos.z;
  }), 5);
  
  public RxVar<Double> gyroRateX = new FirstOrderSystem(R.var(() -> {
    PhysicsRobot robot = physicsRobot.get();
    return robot.kinematics.orientation.localX.dot(robot.kinematics.angVel);
  }), 5);
  
  public RxVar<Double> gyroRateY = new FirstOrderSystem(R.var(() -> {
    PhysicsRobot robot = physicsRobot.get();
    return robot.kinematics.orientation.localY.dot(robot.kinematics.angVel);
  }), 5);
  
  public RxVar<Double> gyroRateZ = new FirstOrderSystem(R.var(() -> {
    PhysicsRobot robot = physicsRobot.get();
    return robot.kinematics.orientation.localZ.dot(robot.kinematics.angVel);
  }), 5);
  
  public RobotModel3() {
    outputs.put("xPos", positionSensor.x);
    outputs.put("yPos", positionSensor.y);
    outputs.put("depth", depthSensor);
  }
  
  public static RobotModel3 getInstance() {
    if (model == null) model = new RobotModel3();
    return model;
  }

  @Override
  @SuppressWarnings("rawtypes")
  public RxVar getInput(String name) {
    return inputs.get(name);
  }

  @Override
  @SuppressWarnings("rawtypes")
  public RxVar getOutput(String name) {
    return outputs.get(name);
  }

  @Override
  public void trigger() {
    R.doSync(() -> {
      Timer.getInstance().trigger();
      
      physicsRobot.mod((robot) -> {
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
    });
  }
  
}