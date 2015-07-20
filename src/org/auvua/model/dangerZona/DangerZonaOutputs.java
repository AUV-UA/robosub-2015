package org.auvua.model.dangerZona;

import org.auvua.agent.TwoVector;
import org.auvua.reactive.core.RxVar;
import org.auvua.vision.ImageSource;

public class DangerZonaOutputs {
  public RxVar<Double> depthSensor;
  
  public RxVar<Double> gyroRateX;
  public RxVar<Double> gyroRateY;
  public RxVar<Double> gyroRateZ;
  
  public RxVar<Double> accelX;
  public RxVar<Double> accelY;
  public RxVar<Double> accelZ;
  
  public TwoVector positionSensor;
  public TwoVector velocitySensor;
  
  public RxVar<ImageSource> frontCamera;
  public RxVar<ImageSource> downCamera;
}
