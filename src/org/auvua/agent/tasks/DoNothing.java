package org.auvua.agent.tasks;

import org.auvua.agent.control.OpenLoopController;
import org.auvua.model.component.DangerZonaInputs;
import org.auvua.model.dangerZona.DangerZona;

public class DoNothing extends AbstractTask {
  
  private DangerZona robot;

  public DoNothing(DangerZona robot) {
    this.robot = robot;
  }
  @Override
  public void initialize() {
    DangerZonaInputs inputs = robot.hardware.getInputs();
    
    inputs.frontRight.setSupplier(new OpenLoopController(0.0));
    inputs.frontLeft.setSupplier(new OpenLoopController(0.0));
    inputs.rearLeft.setSupplier(new OpenLoopController(0.0));
    inputs.rearRight.setSupplier(new OpenLoopController(0.0));
    
    inputs.heaveFrontRight.setSupplier(new OpenLoopController(0.0));
    inputs.heaveFrontLeft.setSupplier(new OpenLoopController(0.0));
    inputs.heaveRearLeft.setSupplier(new OpenLoopController(0.0));
    inputs.heaveRearRight.setSupplier(new OpenLoopController(0.0));
  }

}
