package org.auvua.agent.tasks;

import org.auvua.agent.TwoVector;
import org.auvua.agent.control.PidController;
import org.auvua.agent.control.StoppingDistance;
import org.auvua.model.dangerZona.DangerZona;
import org.auvua.model.dangerZona.DangerZonaInputs;
import org.auvua.model.dangerZona.DangerZonaOutputs;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxCondition;
import org.auvua.reactive.core.RxVar;

public class GoToArea extends AbstractTask {

  public TwoVector target;
  public TwoVector output;
  private TwoVector position;
  private DangerZona robot;
  private double radius;
  public TaskCondition inArea;
  public TaskCondition timeout;
  
  DangerZonaInputs inputs;
  DangerZonaOutputs outputs;

  public GoToArea(DangerZona robot, TwoVector target, double radius) {
    this.target = target;
    this.robot = robot;
    this.radius = radius;
    
    inputs = robot.hardware.getInputs();
    outputs = robot.hardware.getOutputs();

    this.position = new TwoVector(outputs.positionSensor.x, outputs.positionSensor.x);
    this.inArea = createCondition("atDepth", new OccupyingArea(position, target, radius));
    this.timeout = createCondition("timeout", new Timeout(600.0));
  }

  @Override
  public void initialize() {
    RxVar<Double> xSetPoint = this.target.x;
    RxVar<Double> xPos = outputs.positionSensor.x;
    RxVar<Double> xStopPos = new StoppingDistance(outputs.velocitySensor.x, outputs.accelX, 1, 1);
    PidController xController = new PidController(R.var(() -> xPos.get() + xStopPos.get()), xSetPoint, 1, 0, 0);

    RxVar<Double> ySetPoint = this.target.y;
    RxVar<Double> yPos = outputs.positionSensor.y;
    RxVar<Double> yStopPos = new StoppingDistance(outputs.velocitySensor.y, outputs.accelY, 1, 1);
    PidController yController = new PidController(R.var(() -> yPos.get() + yStopPos.get()), ySetPoint, 1, 0, 0);
    
    inputs.frontRight.setSupplier(supplier);
  }

}
