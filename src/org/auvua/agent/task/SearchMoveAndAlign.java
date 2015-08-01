package org.auvua.agent.task;

import jama.Matrix;

import java.util.function.Supplier;

import org.auvua.agent.control.Timer;
import org.auvua.model.dangerZona.DangerZona;

public class SearchMoveAndAlign extends AbstractTask {
  
  public TaskCondition aligned;
  public TaskCondition timeout;
  private double startTime;
  
  private SearchForMarker search;
  private AlignToMarker align;
  private Translate move;
  
  public SearchMoveAndAlign(DangerZona robot, Supplier<Matrix> preSupplier, double time, MotionMode mode) {
    this.move = new Translate(robot, preSupplier, time, mode);
    this.align = new AlignToMarker(robot);
    this.search = new SearchForMarker(robot);
    
    this.aligned = createCondition("aligned", align.aligned);
    this.timeout = createCondition("finished", () -> {
      return Timer.getInstance().get() > startTime + time;
    });
    
    search.markerFound.triggers(() -> {
      search.stop();
      move.stop();
      align.start();
    });
  }

  @Override
  public void initialize() {
    startTime = Timer.getInstance().get();
    move.start();
    search.start();
  }

  @Override
  public void terminate() {
    search.stop();
    move.stop();
    align.stop();
  }

}
