package org.auvua.vision.tests;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.JFrame;
import javax.swing.JSlider;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

/**
 * 
 * @author root
 * 
 * Filter testing class
 */
public class TestManeuverVision {

	public static void main( String[] args )
	{
	  System.out.println(System.getProperty("java.library.path"));
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );

		Mat raw = new Mat();

//		VideoCapture video = new VideoCapture("maneuverApproach");
//		VideoCapture video = new VideoCapture("maneuverCircumnavigate");
		VideoCapture video = new VideoCapture(0);

		if(!video.isOpened()){
			System.out.println("Video Error");
		} else{
			System.out.println("Video Opened");
		}

		JFrame gui = new JFrame();
		JFrame controls = new JFrame();
		controls.setLayout(new GridLayout(8,1));
		JSlider[] sliders = new JSlider[8];

		int[] initValues = { 40, 255, 160, 200, 200, 235, 10, 0 };

		for(int i = 0; i < 8; i++) {
			sliders[i] = new JSlider(0,255,initValues[i]);
			sliders[i].setMaximum(255);
			sliders[i].setMinimum(0);
			controls.add(sliders[i]);
		}

		controls.setVisible(true);
		controls.setSize(200,500);

		gui.setVisible(true);
		gui.setPreferredSize(new Dimension(800,800));

		gui.setResizable(false);
		gui.setSize(800, 800);

		int rhoAvg = 422;
		double centerMovingAverage = 422;
		double widthMovingAverage = 1;
		double center = 422;
		double width = 0;

		while(true) {

			Mat frame = new Mat(), edges = new Mat(), lines = new Mat();

			video.read(frame);

			Imgproc.blur(frame, frame, new Size(3,20)); // THIS IS WHERE THE MAGIC HAPPENS
			Imgproc.Canny(frame, edges, sliders[6].getValue(), sliders[7].getValue());
			Imgproc.HoughLines(edges, lines, 1, Math.PI / 180, 100);
			Imgproc.cvtColor(edges, edges, Imgproc.COLOR_GRAY2BGR);

			int threshold = 30;
			
			// Calculate the x-center of the vertical object
			int count = 0;
			int rhoSum = 0;
			for(int i = 0; i < lines.cols(); i++) {
				double rho = lines.get(0, i)[0];
				double theta = lines.get(0, i)[1];
				double a = Math.cos(theta);
				double b = Math.sin(theta);
				double x0 = a*rho;
				double y0 = b*rho;
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

					Point pt1 = new Point(), pt2 = new Point();
					pt1.x = Math.round(x0 + 1000*(-b));
					pt1.y = Math.round(y0 + 1000*a);
					pt2.x = Math.round(x0 - 1000*(-b));
					pt2.y = Math.round(y0 - 1000*a);
					Core.line(edges, pt1, pt2, new Scalar(0,255,0), 3);
				}

			}

			if(count > 0) rhoAvg = (int) (rhoSum / count);

			// Calculate the standard deviation
			double variance = 0;
			double sum = 0;
			for(int i = 0; i < lines.cols(); i++) {
				double rho = lines.get(0, i)[0];
				double theta = lines.get(0, i)[1];
				double degrees = theta * 180 / Math.PI;

				if( degrees > 180 - threshold || degrees < threshold ) {
					if( degrees == 0) {
						variance += (rho - rhoAvg) * (rho - rhoAvg);
						sum += Math.abs(rho - rhoAvg);
					}
					else {
						double x = Math.abs(rho) - Math.tan(theta) * 200;
						variance += (x - rhoAvg) * (x - rhoAvg);
						sum += Math.abs(x - rhoAvg);
					}
				}
			}
			double stdDev = Math.sqrt(variance);

			// Remove outliers which are 'filterFactor' standard deviations away
			double filterFactor = 1;
			double furthest = 0;
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
			
			System.out.println(width);

			widthMovingAverage = .3 * widthMovingAverage + .7 * width;
			
			centerMovingAverage = .3 * centerMovingAverage + .7 * center; // Low pass filter

			Core.circle(frame, new Point((int) centerMovingAverage, 200), (int) (widthMovingAverage / 2), new Scalar(255,255,255), 3);

			Core.addWeighted(frame, .7, edges, .3, 0, frame);

			gui.getContentPane().getGraphics().drawImage(toBufferedImage(frame), 0, 0, null);
			gui.setSize(frame.width(), frame.height());

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}


	}

	public static Image toBufferedImage(Mat m){
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if ( m.channels() > 1 ) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels()*m.cols()*m.rows();
		byte [] b = new byte[bufferSize];
		m.get(0,0,b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);  
		return image;

	}
}