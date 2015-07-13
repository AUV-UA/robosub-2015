package org.auvua.model.component;

import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;

public class DangerZonaInputs {
  public final RxVar<Double> frontRight = R.var(0.0);
  public final RxVar<Double> frontLeft = R.var(0.0);
  public final RxVar<Double> rearLeft = R.var(0.0);
  public final RxVar<Double> rearRight = R.var(0.0);
  public final RxVar<Double> heaveFrontRight = R.var(0.0);
  public final RxVar<Double> heaveFrontLeft = R.var(0.0);
  public final RxVar<Double> heaveRearLeft = R.var(0.0);
  public final RxVar<Double> heaveRearRight = R.var(0.0);
}
