package org.auvua.model.dangerZona.hardware;

import org.auvua.reactive.core.RxVar;
import org.auvua.vision.ImageSource;

public class DangerZonaOutputs {
  public RxVar<Double> depthSensor;
  public RxVar<Double> humidity;
  public RxVar<Boolean> missionSwitch;
  
  public RxVar<Double> gyroRateX;
  public RxVar<Double> gyroRateY;
  public RxVar<Double> gyroRateZ;
  
  public RxVar<Double> accelX;
  public RxVar<Double> accelY;
  public RxVar<Double> accelZ;
  
  public RxVar<ImageSource> frontCamera;
  public RxVar<ImageSource> downCamera;
}
