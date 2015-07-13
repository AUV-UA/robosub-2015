package org.auvua.model.dangerZona;

import org.auvua.model.component.DangerZonaInputs;
import org.auvua.model.component.DangerZonaOutputs;

public interface DangerZonaHardware {
  public void update();
  public DangerZonaInputs getInputs();
  public DangerZonaOutputs getOutputs();
}
