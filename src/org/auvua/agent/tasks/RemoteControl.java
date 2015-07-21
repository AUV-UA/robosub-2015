package org.auvua.agent.tasks;

import org.auvua.agent.oi.OperatorInterface;
import org.auvua.model.dangerZona.DangerZona;
import org.auvua.model.dangerZona.hardware.DangerZonaInputs;
import org.auvua.reactive.core.R;
import org.auvua.view.Dashboard;

public class RemoteControl extends AbstractTask {
  
  private DangerZona robot;
  private Dashboard dashboard;
  
  public TaskCondition atDepth;

  public RemoteControl(DangerZona robot) {
    this.robot = robot;
    this.dashboard = new Dashboard(robot);
  }
  
  @Override
  public void initialize() {
    DangerZonaInputs inputs = robot.hardware.getInputs();
    OperatorInterface oi = dashboard.oi;
    
    R.task(() -> {
      dashboard.update();
    });
    
    inputs.frontRight.setSupplier(() -> 30 * (oi.forward.get() - oi.strafe.get() + oi.rotation.get()));
    inputs.frontLeft.setSupplier(() -> 30 * (oi.forward.get() + oi.strafe.get() - oi.rotation.get()));
    inputs.rearLeft.setSupplier(() -> 30 * (oi.forward.get() - oi.strafe.get() - oi.rotation.get()));
    inputs.rearRight.setSupplier(() -> 30 * (oi.forward.get() + oi.strafe.get() + oi.rotation.get()));
    
    inputs.heaveFrontRight.setSupplier(() -> 30 * (oi.elevation.get() + oi.pitch.get() - oi.roll.get()));
    inputs.heaveFrontLeft.setSupplier(() -> 30 * (oi.elevation.get() + oi.pitch.get() + oi.roll.get()));
    inputs.heaveRearLeft.setSupplier(() -> 30 * (oi.elevation.get() - oi.pitch.get() + oi.roll.get()));
    inputs.heaveRearRight.setSupplier(() -> 30 * (oi.elevation.get() - oi.pitch.get() - oi.roll.get()));
    
    //dash
  }

  @Override
  public void terminate() {
    // TODO Auto-generated method stub
    
  }

}
