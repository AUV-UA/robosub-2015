package org.auvua.agent.tasks;

import org.auvua.agent.control.PidController;
import org.auvua.model.component.DangerZonaInputs;
import org.auvua.model.component.DangerZonaOutputs;
import org.auvua.model.dangerZona.DangerZona;
import org.auvua.reactive.core.RxVar;

public class MaintainDepth extends AbstractTask {
  
  private RxVar<Double> desiredDepth;
  private DangerZona robot;
  private DangerZonaInputs inputs;
  private DangerZonaOutputs outputs;
  public TaskCondition atDepth;

  public MaintainDepth(DangerZona robot, RxVar<Double> desiredDepth, double error) {
    this.desiredDepth = desiredDepth;
    this.robot = robot;
    this.inputs = this.robot.hardware.getInputs();
    this.outputs = this.robot.hardware.getOutputs();
    this.atDepth = createCondition("atDepth", () -> {
      return Math.abs(desiredDepth.get() - outputs.depthSensor.get()) < error;
    });
  }
  
  @Override
  public void initialize() {
    PidController controller = new PidController(outputs.depthSensor, desiredDepth, -100, 0, -100);
    inputs.heaveFrontRight.setSupplier(controller);
    inputs.heaveFrontLeft.setSupplier(controller);
    inputs.heaveRearLeft.setSupplier(controller);
    inputs.heaveRearRight.setSupplier(controller);
  }

}
