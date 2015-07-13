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
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class RedDownBuoyFilter implements ImageFilter {

	private Map<String,Object> filterOutput = new HashMap<String,Object>();
	private boolean drawIndicators = true;

	Point circleCenter = new Point(320,240);
	double circleRadius = 0.0;

	public RedDownBuoyFilter() {
		filterOutput.put("buoyVisible", false);
		filterOutput.put("xPosition", 0.0);
		filterOutput.put("yPosition", 0.0);
		filterOutput.put("radius", 0.0);
	}

	@Override
	public Map<String, Object> filter(Mat image) {

		Mat frame = new Mat(),
				filtered = new Mat(),
				rgb = new Mat(),
				circles = new Mat();

		frame = image.clone();

		Imgproc.cvtColor(frame, rgb, Imgproc.COLOR_BGR2RGB);
		
		// Adjust these values depending on lighting conditions
		Scalar lower = new Scalar( 100, 0, 0 );
		Scalar upper = new Scalar( 255, 210, 210 );

		Core.inRange(rgb, lower, upper, filtered);

		Mat filterCopy;
		filterCopy = filtered.clone();

		Imgproc.HoughCircles(filtered, circles, Imgproc.CV_HOUGH_GRADIENT, 10, 100, 200, 100, 30, 100);

		List<MatOfPoint> contours = new LinkedList<MatOfPoint>();
		Imgproc.findContours(filtered, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		double maxCircularity = 0.0;

		for (MatOfPoint contour : contours) {
			Point center = new Point();
			float[] radius = { 0.0f };

			double area = Imgproc.contourArea(contour);

			
			if(area < 1500 || area > 10000) continue;

			Imgproc.minAreaRect(new MatOfPoint2f(contour.toArray()));
			Imgproc.minEnclosingCircle(new MatOfPoint2f(contour.toArray()), center, radius);

			double circularity = area / (radius[0] * radius[0] * Math.PI);

			if(circularity > maxCircularity) {
				maxCircularity = circularity;
				circleCenter = center;
				circleRadius = radius[0];
			}
		}
		
		Imgproc.cvtColor(filterCopy, filterCopy, Imgproc.COLOR_BayerBG2BGR);

		if(maxCircularity > .5) {
			filterOutput.put("buoyVisible", true);
			filterOutput.put("xPosition", circleCenter.x);
			filterOutput.put("yPosition", circleCenter.y);
			filterOutput.put("radius", circleRadius);
			if(drawIndicators) {
				Core.circle(filterCopy, circleCenter, (int) circleRadius, new Scalar(255,0,255), 3);
			}
			
			System.out.println(maxCircularity * circleRadius * circleRadius * Math.PI);
		} else {
			filterOutput.put("buoyVisible", false);
		}
		
		if(drawIndicators) {
	    	Core.addWeighted(image, 0.7, filterCopy, 0.3, 0, image);
		}

		return filterOutput;
	}

	@Override
	public Map<String, Object> getFilterValues() {
		return filterOutput;
	}

}
