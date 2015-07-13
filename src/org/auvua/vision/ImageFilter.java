package org.auvua.vision;

import java.util.Map;

import org.opencv.core.Mat;

public interface ImageFilter {
	public Map<String,Object> filter(Mat image);
	public Map<String,Object> getFilterValues();
}
