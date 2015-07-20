package org.auvua.agent.tasks;

import org.auvua.model.dangerZona.DangerZona;
import org.auvua.reactive.core.R;
import org.auvua.reactive.core.RxVar;
import org.auvua.vision.CameraViewer;
import org.auvua.vision.FloorMarkerFilter;
import org.auvua.vision.ImageSource;

public class SearchForMarker extends AbstractTask {

  private DangerZona robot;
  public TaskCondition markerFound;
  private RxVar<Boolean> found = R.var(false);
  
  public SearchForMarker(DangerZona robot) {
    this.robot = robot;
    
    this.markerFound = createCondition("markerFound", found);
  }
  
  @Override
  public void initialize() {
    RxVar<ImageSource> sourceVar = robot.hardware.getOutputs().downCamera;
    RxVar<FloorMarkerFilter> filterVar = R.var(new FloorMarkerFilter());
    
    filterVar.setModifier((filter) -> {
      ImageSource imSource = sourceVar.get();
      imSource.capture();
      filter.filter(imSource.getMat());
    });
    
    found.setSupplier(() -> filterVar.get().markerVisible);
    
    RxVar<CameraViewer> cvVar = R.var(new CameraViewer());
    cvVar.setModifier((viewer) -> {
      viewer.setImageFromMat(filterVar.get().imageOut);
    });
  }

  @Override
  public void terminate() {}

}