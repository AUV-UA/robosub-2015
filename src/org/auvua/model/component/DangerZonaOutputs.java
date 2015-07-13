package org.auvua.model.component;

import org.auvua.agent.TwoVector;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;

public class DangerZonaOutputs {
  public RxVar<Double> depthSensor;
  public RxVar<Double> gyroRateX;
  public RxVar<Double> gyroRateY;
  public RxVar<Double> gyroRateZ;
  public TwoVector positionSensor;
  public TwoVector velocitySensor;
}
