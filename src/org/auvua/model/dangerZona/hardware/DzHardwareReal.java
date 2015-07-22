package org.auvua.model.dangerZona.hardware;

import org.auvua.agent.control.Timer;
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
        .setMotor      (FRONT_RIGHT_MOTOR,       inputs.frontRight.get())
        .setMotor      (FRONT_LEFT_MOTOR,        inputs.frontLeft.get())
        .setMotor      (REAR_LEFT_MOTOR,         inputs.rearLeft.get())
        .setMotor      (REAR_RIGHT_MOTOR,        inputs.rearRight.get())
        .setMotor      (HEAVE_FRONT_RIGHT_MOTOR, inputs.heaveFrontRight.get())
        .setMotor      (HEAVE_FRONT_LEFT_MOTOR,  inputs.heaveFrontLeft.get())
        .setMotor      (HEAVE_REAR_LEFT_MOTOR,   inputs.heaveRearLeft.get())
        .setMotor      (HEAVE_REAR_RIGHT_MOTOR,  inputs.heaveRearRight.get())
        .setMotor      (8, 0.0)
        .setMotor      (9, 0.0)
        .setActuator   (0, false)
        .setActuator   (1, false)
        .setActuator   (2, false)
        .setActuator   (3, false)
        .setActuator   (4, false)
        .setActuator   (5, false)
        .setActuator   (6, false)
        .setActuator   (7, false)
        .setActuator   (8, false)
        .setActuator   (9, false)
        .setSwitch     (0, false)
        .setSwitch     (1, false)
        .setSwitch     (2, false)
        .setSwitch     (3, false)
        .setSwitch     (4, false)
        .setSwitch     (5, false)
        .setSwitch     (6, false)
        .setSwitch     (7, false)
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
