package org.auvua.vision;

import java.awt.Image;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

/*
 * Processes the image to find the center of the gate
 */
public class GateFilter implements ImageFilter {
	
	private Map<String,Object> filterOutput = new HashMap<String,Object>();
	private Mat filtered = new Mat(), rgb = new Mat(), edges = new Mat(), blurred = new Mat(), blurredfiltered = new Mat(), blurrededges = new Mat();
	private boolean drawIndicators = true;
	// Note that this is a complete guess about the color
	private Scalar gateColor = new Scalar(255, 30, 0);
	CameraViewer cv_canny;
	
	public GateFilter() {
		filterOutput.put("markerVisible", true);
		filterOutput.put("angle", 0.0);
		filterOutput.put("xPosition", 0.0);
		filterOutput.put("yPosition", 0.0);
		filterOutput.put("length", 0.0);
		filterOutput.put("width", 0.0);
		
		cv_canny = new CameraViewer();
	}

	@Override
	public Map<String, Object> filter(Mat image) {
		
		Imgproc.cvtColor(image, rgb, Imgproc.COLOR_BGR2RGB);
		Scalar lower = new Scalar( 120, 0, 0 );
    	Scalar upper = new Scalar( 255, 100, 100 );
    	

    	Imgproc.blur(rgb, blurred, new Size(5.0, 5.0));
    	Core.inRange(blurred, lower, upper, blurredfiltered);
    	Imgproc.Canny(blurredfiltered, blurrededges, 50, 150);
    	
    	Core.inRange(rgb, lower, upper, filtered);
    	Imgproc.Canny(filtered, edges, 50, 150);
    	
    	

    	Point avgCenter = new Point(-1,-1);
    
    	Moments moms = Imgproc.moments(blurrededges);
    	double avgX = moms.get_m10() / moms.get_m00();
    	double avgY = moms.get_m01() / moms.get_m00();
    	avgCenter.x = avgX;
    	avgCenter.y = avgY;

    	if(drawIndicators) {
	    	Core.circle(image, avgCenter, 5, new Scalar(255,0,0), 3);
	    	Imgproc.cvtColor(edges, edges, Imgproc.COLOR_GRAY2BGR);
	    	Imgproc.cvtColor(blurrededges, blurrededges, Imgproc.COLOR_GRAY2BGR);
	    	Imgproc.cvtColor(blurredfiltered, blurredfiltered, Imgproc.COLOR_GRAY2BGR);
	    	//Imgproc.blur(blurredfiltered, blurredfiltered, new Size(55.0, 55.0));
	    	Core.addWeighted(image, 1.0,blurredfiltered, 0.6, 0, image);
	    	cv_canny.setImage(Camera.toBufferedImage(blurredfiltered));
	    	Core.addWeighted(image, 0.7, edges, 0.9, 0, image);
    	}
    	
    	// Update filterOutput values
    	if(moms.get_m00() == 0)
    		filterOutput.put("markerVisible", false);
		filterOutput.put("angle", 0.0);
		filterOutput.put("xPosition", avgCenter.x);
		filterOutput.put("yPosition", avgCenter.y);
		filterOutput.put("length", 0.0);
		filterOutput.put("width", 0.0);
		
    	return filterOutput;
	}

	@Override
	public Map<String, Object> getFilterValues() {
		return filterOutput;
	}

}
