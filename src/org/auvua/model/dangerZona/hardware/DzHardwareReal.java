package org.auvua.model.dangerZona.hardware;

import org.auvua.agent.control.Timer;
import org.auvua.model.MotionUtil;
import org.auvua.model.dangerZona.hardware.proto.InputMessageProto.InputMessage;
import org.auvua.model.dangerZona.hardware.proto.OutputMessageProto.OutputMessage;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;
import org.auvua.vision.Camera;

public class DzHardwareReal implements DzHardware {
  
  public static int FRONT_RIGHT_MOTOR = 0;
  public static int FRONT_LEFT_MOTOR = 1;
  public static int REAR_LEFT_MOTOR = 2;
  public static int REAR_RIGHT_MOTOR = 3;
  public static int HEAVE_FRONT_RIGHT_MOTOR = 4;
  public static int HEAVE_FRONT_LEFT_MOTOR = 5;
  public static int HEAVE_REAR_LEFT_MOTOR = 6;
  public static int HEAVE_REAR_RIGHT_MOTOR = 7;
  
  public DangerZonaInputs inputs = new DangerZonaInputs();
  public DangerZonaOutputs outputs = new DangerZonaOutputs();
  
  private DzHardwareSocket socket = new DzHardwareSocket();
  
  public RxVar<InputMessage> inputMessage = R.var(() -> {
    return InputMessage.newBuilder()
        // 10 PWM Outputs
        .addMotor      (MotionUtil.clamp(inputs.heaveRearLeft.get() / 26.7))
        .addMotor      (MotionUtil.clamp(-inputs.rearRight.get() / 35.6))
        .addMotor      (MotionUtil.clamp(-inputs.heaveRearRight.get() / 26.7))
        .addMotor      (MotionUtil.clamp(-inputs.rearLeft.get() / 35.6))
        .addMotor      (MotionUtil.clamp(-inputs.heaveFrontRight.get() / 26.7))
        .addMotor      (MotionUtil.clamp(inputs.frontLeft.get() / 35.6))
        .addMotor      (MotionUtil.clamp(inputs.heaveFrontLeft.get() / 26.7))
        .addMotor      (MotionUtil.clamp(inputs.frontRight.get() / 35.6))
        .addMotor      (0.0)
        .addMotor      (0.0)
        // 10 Actuators
        .addActuator   (false)
        .addActuator   (false)
        .addActuator   (false)
        .addActuator   (false)
        .addActuator   (false)
        .addActuator   (false)
        .addActuator   (false)
        .addActuator   (false)
        .addActuator   (false)
        .addActuator   (false)
        // 8 Switches
        .addSwitch     (false)
        .addSwitch     (false)
        .addSwitch     (false)
        .addSwitch     (false)
        .addSwitch     (false)
        .addSwitch     (false)
        .addSwitch     (false)
        .addSwitch     (false)
        // 2 GoPro switches
        .setFrontGoPro (true)
        .setDownGoPro  (true)
        .setLightRed   (0.0)
        .setLightGreen (0.0)
        .setLightBlue  (0.0)
        .build();
  });
  
  public RxVar<OutputMessage> outputMessage = R.var(() -> {
    byte[] encodedInput = inputMessage.get().toByteArray();
    byte[] encodedOutput = socket.sendData(encodedInput);
    OutputMessage outputMessage = null;
    try {
      outputMessage = OutputMessage.parseFrom(encodedOutput);
    } catch (Exception e) {
      // Handle this
    }
    return outputMessage;
  });
  
  public DzHardwareReal() {
    createSensors();
  }

  private void createSensors() {
    outputs.depthSensor = R.var(() ->  outputMessage.get().getDepth() );
    outputs.humidity = R.var(() -> outputMessage.get().getHumidity());
    outputs.missionSwitch = R.var(() -> outputMessage.get().getMission());
    
    outputs.gyroRateX = R.var(() -> outputMessage.get().getGyroX());
    outputs.gyroRateY = R.var(() -> outputMessage.get().getGyroY());
    outputs.gyroRateZ = R.var(() -> outputMessage.get().getGyroZ());
    
    outputs.accelX = R.var(() -> outputMessage.get().getAccelX());
    outputs.accelY = R.var(() -> outputMessage.get().getAccelY());
    outputs.accelZ = R.var(() -> outputMessage.get().getAccelZ());
    
    outputs.frontCamera = R.var(new Camera(0));
    outputs.downCamera = R.var(new Camera(1));
    
    outputs.frontCamera.setModifier((camera) -> {
      Timer.getInstance().get();
      camera.capture();
    });
    
    outputs.downCamera.setModifier((camera) -> {
      Timer.getInstance().get();
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
