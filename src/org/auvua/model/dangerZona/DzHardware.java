package org.auvua.model.dangerZona;

import org.auvua.vision.ImageSource;


public interface DzHardware {
  public DangerZonaInputs getInputs();
  public DangerZonaOutputs getOutputs();
  public void update();
}
