package org.auvua.vision;

import org.opencv.core.Mat;

public interface ImageSource {
  public void capture();
  public Mat getMat();
}
