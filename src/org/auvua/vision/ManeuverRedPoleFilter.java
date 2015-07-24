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
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ManeuverRedPoleFilter implements ImageFilter {

	private Map<String,Object> filterOutput = new HashMap<String,Object>();

	double center = 320;
	double width = 0;

	boolean drawIndicators = true;

	public ManeuverRedPoleFilter() {
		filterOutput.put("poleVisible", false);
		filterOutput.put("center", 320.0);
		filterOutput.put("width", 0.0);
	}

	@Override
	public void filter(Mat image) {
		Mat rgb = new Mat(),
				filtered = new Mat(),
				edges = new Mat();


		Imgproc.cvtColor(image, rgb, Imgproc.COLOR_BGR2RGB);
		Scalar lower = new Scalar( 0, 0, 0 );
		Scalar upper = new Scalar( 255, 120, 150 );

		Core.inRange(rgb, lower, upper, filtered);
		Imgproc.Canny(filtered, edges, 50, 150);

		List<MatOfPoint> contours = new LinkedList<MatOfPoint>();
		Imgproc.findContours(filtered, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		MatOfPoint largestContour = null;

		double largestArea = 0;
		for (MatOfPoint contour : contours) {
			double area = Imgproc.contourArea(contour);
			if(area > largestArea) {
				largestArea = area;
				largestContour = contour;
			}
		}

		if (largestContour != null) {
			RotatedRect box = Imgproc.minAreaRect(new MatOfPoint2f(largestContour.toArray()));

    		if(box.size.height > box.size.width) {
    			width = box.size.width;
    		} else {
    			width = box.size.height;
    		}

			if(drawIndicators) {
				Imgproc.cvtColor(edges, edges, Imgproc.COLOR_GRAY2BGR);
				Core.addWeighted(image, 0.7, edges, 0.3, 0, image);
				Core.circle(image, box.center, 5, new Scalar(255,0,0), 3);
			}

			filterOutput.put("poleVisible", true);
			filterOutput.put("center", box.center.x);
			filterOutput.put("width", width);
		} else {
			filterOutput.put("poleVisible", false);
		}

		return;
	}

}
