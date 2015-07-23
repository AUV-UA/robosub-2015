package org.auvua.model.dangerZona.hardware;

public interface DzHardware {
  public DangerZonaInputs getInputs();
  public DangerZonaOutputs getOutputs();
  public void update();
}
