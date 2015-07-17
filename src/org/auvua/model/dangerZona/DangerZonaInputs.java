package org.auvua.model.dangerZona;

import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxValve;

public class DangerZonaInputs {
  public final RxValve<Double> frontRight = R.valve(0.0);
  public final RxValve<Double> frontLeft = R.valve(0.0);
  public final RxValve<Double> rearLeft = R.valve(0.0);
  public final RxValve<Double> rearRight = R.valve(0.0);
  public final RxValve<Double> heaveFrontRight = R.valve(0.0);
  public final RxValve<Double> heaveFrontLeft = R.valve(0.0);
  public final RxValve<Double> heaveRearLeft = R.valve(0.0);
  public final RxValve<Double> heaveRearRight = R.valve(0.0);
  
  public void trigger() {
    frontRight.trigger();
    frontLeft.trigger();
    rearLeft.trigger();
    rearRight.trigger();
    heaveFrontRight.trigger();
    heaveFrontLeft.trigger();
    heaveRearLeft.trigger();
    heaveRearRight.trigger();
  }
}
