package org.auvua.agent.task;

import java.util.function.Supplier;

import jama.Matrix;

import org.auvua.agent.control.PidController;
import org.auvua.agent.signal.Differentiator;
import org.auvua.agent.signal.MovingAverageExponential;
import org.auvua.model.dangerZona.DangerZona;
import org.auvua.model.dangerZona.hardware.DangerZonaOutputs;
import org.auvua.model.dangerZona.hardware.LEDStrips.LED_STRIP;
import org.auvua.reactive.core.RxVar;

public class MaintainDepth extends AbstractTask {
  
  private DangerZona robot;
  private RxVar<Double> desiredDepth;
  private Supplier<Matrix> vectorSupplier;
  private DangerZonaOutputs outputs;
  public TaskCondition atDepth;

  public MaintainDepth(DangerZona robot, RxVar<Double> desiredDepth, double error) {
    this.robot = robot;
    this.desiredDepth = desiredDepth;
    this.outputs = robot.hardware.getOutputs();
    this.atDepth = createCondition("atDepth", () -> {
      return Math.abs(desiredDepth.get() - outputs.depthSensor.get()) < error;
    });
  }
  
  @Override
  public void initialize() {
    Supplier<Double> desiredVelocity = () -> -(desiredDepth.get() - outputs.depthSensor.get()) * 2;
    Supplier<Double> velocity = new Differentiator(new MovingAverageExponential(() -> -outputs.depthSensor.get(), 0.1));
    
    PidController controller = new PidController(velocity, desiredVelocity, 70, 0, 0);
    
    vectorSupplier = () -> {
      return new Matrix(new double[][] {
          { 0, 0, 1 }
      }).transpose().times(controller.get());
    };
    
    robot.motionController.force.addSupplier(vectorSupplier);
    
    robot.hardware.getInputs().indicators.enqueueAnimation((indicators) -> {
      while(!atDepth.get()) {
        indicators.setStripColor(LED_STRIP.BACKLEFT, 0x00007f);
        indicators.setStripColor(LED_STRIP.BACKRIGHT, 0x00007f);
        try { Thread.sleep(1000); } catch (Exception e) {}
        indicators.setStripColor(LED_STRIP.BACKLEFT, 0x000000);
        indicators.setStripColor(LED_STRIP.BACKRIGHT, 0x000000);
        try { Thread.sleep(1000); } catch (Exception e) {}
      }
    });
  }

  @Override
  public void terminate() {
    robot.motionController.force.removeSupplier(vectorSupplier);
  }

}
