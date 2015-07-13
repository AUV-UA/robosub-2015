package org.auvua.agent.tasks;

import org.auvua.agent.control.PidController;
import org.auvua.model.RobotModel;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxCondition;
import org.auvua.reactive.core.RxVar;

public class MaintainDepth extends AbstractTask {
  
  private RxVar<Double> desiredDepth;
  private RobotModel robot;
  public TaskCondition atDepth;

  public MaintainDepth(RobotModel robot, RxVar<Double> desiredDepth, double error) {
    this.desiredDepth = desiredDepth;
    this.robot = robot;
    this.atDepth = createCondition("atDepth", () -> {
      return Math.abs(desiredDepth.get() - robot.motion.z.pos.get()) < error;
    });
  }
  
  @Override
  public void initialize() {
    PidController controller = new PidController(robot.motion.z.pos, desiredDepth, 1, 0, 2.5);
    controller.setSaturationLimits(-200, 200);
    robot.thrustInputZ.setSupplier(controller);
  }

}
