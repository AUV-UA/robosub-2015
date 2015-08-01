package org.auvua.agent.task;

import jama.Matrix;

import java.util.function.Supplier;

import org.auvua.agent.control.PidController;
import org.auvua.agent.signal.Differentiator;
import org.auvua.agent.signal.Duration;
import org.auvua.agent.signal.MovingAverageExponential;
import org.auvua.model.dangerZona.DangerZona;
import org.auvua.model.dangerZona.hardware.LEDStrips;
import org.auvua.model.dangerZona.hardware.LEDStrips.LED_STRIP;
import org.auvua.model.motion.Orientation;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;
import org.auvua.vision.CameraViewer;
import org.auvua.vision.FloorMarkerFilter;
import org.auvua.vision.ImageSource;

public class AlignToMarker extends AbstractTask {

  private DangerZona robot;
  private Supplier<Matrix> forceSupplier;
  private Supplier<Matrix> torqueSupplier;
  private RxVar<Boolean> alignedCond = R.var(false);
  public TaskCondition aligned;
  private double lastXVel = 0;
  private double lastYVel = 0;

  public AlignToMarker(DangerZona robot) {
    this.robot = robot;
    this.aligned = createCondition("aligned", alignedCond);
  }
  
  @Override
  public void initialize() {
    System.out.println("Aligning to marker!");
    
    RxVar<ImageSource> sourceVar = robot.hardware.getOutputs().downCamera;
    RxVar<FloorMarkerFilter> filterVar = R.var(new FloorMarkerFilter());
    
    filterVar.setModifier((filter) -> {
      ImageSource imSource = sourceVar.get();
      imSource.capture();
      filter.filter(imSource.getMat());
    });
    /*
    RxVar<CameraViewer> cvVar = R.var(new CameraViewer());
    cvVar.setModifier((viewer) -> {
      viewer.setImageFromMat(filterVar.get().imageOut);
    });
    */
    RxVar<Double> xPos = R.var(() -> {
      return filterVar.get().imageCenterX - filterVar.get().xPosition;
    });
    RxVar<Double> xVel = new Differentiator(xPos);
    MovingAverageExponential xVel2 = new MovingAverageExponential(() -> {
      double xVelocity = xVel.get();
      if (filterVar.get().markerVisible) {
        lastXVel = xVelocity;
      }
      return lastXVel;
    }, .2);
    
    RxVar<Double> yPos = R.var(() -> {
      return filterVar.get().yPosition - filterVar.get().imageCenterY;
    });
    RxVar<Double> yVel = new Differentiator(yPos);
    MovingAverageExponential yVel2 = new MovingAverageExponential(() -> {
      double yVelocity = yVel.get();
      if (filterVar.get().markerVisible) {
        lastYVel = yVelocity;
      }
      return lastYVel;
    }, .2);
    
    alignedCond.setSupplier(new Duration(() -> {
      FloorMarkerFilter filter = filterVar.get();
      boolean inCircle = Math.hypot(xPos.get(), yPos.get()) < 70;
      boolean aligned = Math.abs(filter.angle) < Math.PI / 36;
      return filter.markerVisible && inCircle && aligned;
    }, .5));
    
    RxVar<Double> xVelSetPoint = R.var(() -> {
      return (filterVar.get().xPosition - filterVar.get().imageCenterX) * 1;
    });
    
    RxVar<Double> yVelSetPoint = R.var(() -> {
      return (filterVar.get().imageCenterY - filterVar.get().yPosition) * 1;
    });
    
    RxVar<Double> zRotSetPoint = R.var(() -> {
      return filterVar.get().angle * .03;
    });
    
    PidController xController = new PidController(xVel2, xVelSetPoint, .1, 0, .03);
    PidController yController = new PidController(yVel2, yVelSetPoint, .08, 0, .03);
    PidController rotController = new PidController(robot.hardware.getOutputs().gyroRateZ, zRotSetPoint, .5, 0, 0);
    
    forceSupplier = () -> {
      return robot.calcKinematics.get().orientation.asMatrix().times(new Matrix(new double[][] {
          { xController.get(), yController.get(), 0 }
      }).transpose());
    };
    
    torqueSupplier = () -> {
      return robot.calcKinematics.get().orientation.asMatrix().times(new Matrix(new double[][] {
          { 0, 0, rotController.get() }
      }).transpose());
    };
    
    robot.orientationController.orientation.setSupplier(() -> {
      Orientation or = robot.calcKinematics.get().orientation;
      double x = or.localY.x;
      double y = or.localY.y;
      double mag = Math.hypot(x, y);
      x /= mag;
      y /= mag;
      
      Matrix orMatrix = new Matrix(new double[][] {
          { y, x, 0},
          {-x, y, 0},
          { 0, 0, 1}
      });
      
      double sin = Math.sin(filterVar.get().angle);
      double cos = Math.cos(filterVar.get().angle);
      
      Matrix rotMatrix = new Matrix(new double[][] {
          {cos, -sin, 0},
          {sin,  cos, 0},
          {  0,    0, 1}
      });
      
      return rotMatrix.times(orMatrix);
    });
    
    robot.motionController.force.addSupplier(forceSupplier);
    
    robot.hardware.getInputs().indicators.enqueueAnimation((indicators) -> {
      while(!alignedCond.get()) {
        boolean visible = filterVar.get().markerVisible;
        if (visible) {
          indicators.setStripColor(LED_STRIP.BACKLEFT, 0x7f7f00);
          indicators.setStripColor(LED_STRIP.BACKRIGHT, 0x7f7f00);
        } else {
          indicators.setStripColor(LED_STRIP.BACKLEFT, 0x000000);
          indicators.setStripColor(LED_STRIP.BACKRIGHT, 0x000000);
        }
      }
    });
    
    aligned.triggers(() -> {
      robot.hardware.getInputs().indicators.enqueueAnimation((indicators) -> {
        indicators.setStripColor(LED_STRIP.BACKLEFT, 0x000000);
        indicators.setStripColor(LED_STRIP.BACKRIGHT, 0x000000);
        try { Thread.sleep(250); } catch (Exception e) {}
        for (int i = 0; i < 3; i++) {
          indicators.setStripColor(LED_STRIP.BACKLEFT, 0x007f00);
          indicators.setStripColor(LED_STRIP.BACKRIGHT, 0x007f00);
          try { Thread.sleep(250); } catch (Exception e) {}
          indicators.setStripColor(LED_STRIP.BACKLEFT, 0x000000);
          indicators.setStripColor(LED_STRIP.BACKRIGHT, 0x000000);
          try { Thread.sleep(250); } catch (Exception e) {}
        }
      });
    });
  }

  @Override
  public void terminate() {
    robot.motionController.force.removeSupplier(forceSupplier);
    Matrix or = robot.calcKinematics.get().orientation.asMatrix();
    robot.orientationController.orientation.setSupplier(() -> {
      return or;
    });
  }

}

