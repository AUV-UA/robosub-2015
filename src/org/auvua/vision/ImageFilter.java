package org.auvua.vision;

import org.opencv.core.Mat;

public interface ImageFilter {
	public void filter(Mat image);
}
