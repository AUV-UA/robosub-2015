package org.auvua.vision;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

public class RedBuoyFilter implements ImageFilter {
	
	private Map<String,Object> filterOutput = new HashMap<String,Object>();
	private boolean drawIndicators = true;
	
	public RedBuoyFilter() {
		filterOutput.put("buoyVisible", false);
		filterOutput.put("xPosition", 320.0);
		filterOutput.put("yPosition", 240.0);
		filterOutput.put("size", 0.0);
	}

	@Override
	public Map<String, Object> filter(Mat image) {
		
		Mat frame = new Mat(),
		        filtered = new Mat(),
	            rgb = new Mat();
    	
    	image.assignTo(frame);
		
    	Imgproc.cvtColor(frame, rgb, Imgproc.COLOR_BGR2RGB);
    	
    	Scalar lower = new Scalar( 150, 0, 0 );
    	Scalar upper = new Scalar( 255, 230, 230 );
    	
//		Scalar lower = new Scalar( 170, 0, 0 );
//    	Scalar upper = new Scalar( 255, 100, 100 );
    	
    	Core.inRange(rgb, lower, upper, filtered);
    	
    	Point avgCenter = new Point(-1,-1);
    	
    	Moments moms = Imgproc.moments(filtered);
    	double size = moms.get_m00() / 100;
        if(size != 0) {
	    	double avgX = moms.get_m10() / moms.get_m00();
	    	double avgY = moms.get_m01() / moms.get_m00();
	    	avgCenter.x = avgX;
	    	avgCenter.y = avgY;
	    	
	    	filterOutput.put("buoyVisible", true);
			filterOutput.put("xPosition", avgCenter.x + 0.0);
			filterOutput.put("yPosition", avgCenter.y + 0.0);
			filterOutput.put("size", size);
			
	    	if(drawIndicators) {
		    	Core.circle(image, avgCenter, 5, new Scalar(255,0,0), 3);
	    	}
        } else {
        	filterOutput.put("buoyVisible", false);
    		filterOutput.put("xPosition", 320.0);
    		filterOutput.put("yPosition", 240.0);
    		filterOutput.put("size", 0.0);
        }
        
    	try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    	return filterOutput;
	}

	@Override
	public Map<String, Object> getFilterValues() {
		return filterOutput;
	}

}
