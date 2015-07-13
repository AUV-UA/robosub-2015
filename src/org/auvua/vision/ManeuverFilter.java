package org.auvua.vision;

import java.util.HashMap;
import java.util.Map;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ManeuverFilter implements ImageFilter {

	private int rhoAvg = 320;
	private boolean drawIndicators = true;
	private Map<String,Object> filterOutput = new HashMap<String,Object>();

	double centerMovingAverage = 320;
	double widthMovingAverage = 0;
	double center = 320;
	double width = 0;

	public ManeuverFilter() {
		filterOutput.put("visible", false);
		filterOutput.put("xPosition", 320.0);
		filterOutput.put("size", 0.0);
	}

	@Override
	public Map<String, Object> filter(Mat image) {
		Mat frame = new Mat(), edges = new Mat(), lines = new Mat();

		frame = image.clone();

		Imgproc.blur(frame, frame, new Size(3,20)); // THIS IS WHERE THE MAGIC HAPPENS
		Imgproc.Canny(frame, edges, 10, 0);
		Imgproc.HoughLines(edges, lines, 1, Math.PI / 180, 100);
		Imgproc.cvtColor(edges, edges, Imgproc.COLOR_GRAY2BGR);

		int threshold = 30;

		// Calculate the x-center of the vertical object
		int count = 0;
		int rhoSum = 0;
		for(int i = 0; i < lines.cols(); i++) {
			double rho = lines.get(0, i)[0];
			double theta = lines.get(0, i)[1];
			double degrees = theta * 180 / Math.PI;

			if( degrees > 180 - threshold || degrees < threshold ) {
				count++;
				if( degrees == 0) {
					rhoSum += Math.abs(rho);
				}
				else {
					double x = Math.abs(rho) - Math.tan(theta) * 200;
					rhoSum += x;
				}

				if(drawIndicators) {
					double a = Math.cos(theta);
					double b = Math.sin(theta);
					double x0 = a*rho;
					double y0 = b*rho;
					Point pt1 = new Point(), pt2 = new Point();
					pt1.x = Math.round(x0 + 1000*(-b));
					pt1.y = Math.round(y0 + 1000*a);
					pt2.x = Math.round(x0 - 1000*(-b));
					pt2.y = Math.round(y0 - 1000*a);
					Core.line(edges, pt1, pt2, new Scalar(0,255,0), 3);
				}
			}

		}

		if(count != 0) {
			rhoAvg = rhoSum / count;

			// Calculate the standard deviation
			double variance = 0;
			for(int i = 0; i < lines.cols(); i++) {
				double rho = lines.get(0, i)[0];
				double theta = lines.get(0, i)[1];
				double degrees = theta * 180 / Math.PI;

				if( degrees > 180 - threshold || degrees < threshold ) {
					if( degrees == 0) {
						variance += (rho - rhoAvg) * (rho - rhoAvg);
					}
					else {
						double x = Math.abs(rho) - Math.tan(theta) * 200;
						variance += (x - rhoAvg) * (x - rhoAvg);
					}
				}
			}
			double stdDev = Math.sqrt(variance);

			// Remove outliers which are 'filterFactor' standard deviations away
			double filterFactor = 1;
			double leftest = 844;
			double rightest = 0;

			for(int i = 0; i < lines.cols(); i++) {
				double rho = lines.get(0, i)[0];
				double theta = lines.get(0, i)[1];
				double degrees = theta * 180 / Math.PI;

				if( degrees > 180 - threshold || degrees < threshold ) {
					double distance;
					double location;
					if( degrees == 0 ) {
						location = rho;
						distance = Math.abs(location - rhoAvg);
					} else {
						location = Math.abs(rho) - Math.tan(theta) * 200;
						distance = Math.abs(location - rhoAvg);
					}
					if( distance < filterFactor * stdDev ){
						if(location < leftest) leftest = location;
						if(location > rightest) rightest = location;
					}
				}
			}

			if(rightest - leftest < 0) {
				width = 0;
			} else {
				width = rightest - leftest;
				center = (rightest + leftest) / 2.0;
			}

			widthMovingAverage = .3 * widthMovingAverage + .7 * width;

			centerMovingAverage = .3 * centerMovingAverage + .7 * center; // Low pass filter

			filterOutput.put("xPosition", centerMovingAverage);
			filterOutput.put("visible", true);
			filterOutput.put("size", widthMovingAverage);
		} else {
			filterOutput.put("visible", false);
		}
		if(drawIndicators) {
			Core.circle(frame, new Point((int) centerMovingAverage, 200), (int) (widthMovingAverage / 2), new Scalar(255,255,255), 3);
			Core.addWeighted(frame, .7, edges, .3, 0, frame);
			frame.assignTo(image);
		}
		return filterOutput;
	}




	@Override
	public Map<String, Object> getFilterValues() {
		return filterOutput;
	}

}
