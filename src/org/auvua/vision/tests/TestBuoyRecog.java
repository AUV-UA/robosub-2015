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
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

/**
 * 
 * @author root
 * 
 * Filter testing class
 */
public class TestBuoyRecog {
	
	public static void main( String[] args )
	{
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		
		VideoCapture video = new VideoCapture("buoysVideo");

	    if(!video.isOpened()){
	        System.out.println("Video Error");
	    } else{
	        System.out.println("Video Opened");
	    }
	    
	    Mat raw = new Mat();
		
	    JFrame gui = new JFrame();
	    JFrame controls = new JFrame();
	    controls.setLayout(new GridLayout(6,1));
	    JSlider[] sliders = new JSlider[6];
	    
	    int[] initValues = { 100, 255, 0, 230, 0, 230 };
	    
	    for(int i = 0; i < sliders.length; i++) {
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
	    
	    
	    
	    while(true) {
		    Mat frame = new Mat(),
			        filtered = new Mat(),
		            edges = new Mat(),
		            rgb = new Mat();
	    	
		    video.read(raw);
		    
	    	frame = raw.clone();
			
	    	Imgproc.cvtColor(frame, rgb, Imgproc.COLOR_BGR2RGB);
	    	
	    	Scalar lower = new Scalar( sliders[0].getValue(), sliders[2].getValue(), sliders[4].getValue() );
	    	Scalar upper = new Scalar( sliders[1].getValue(), sliders[3].getValue(), sliders[5].getValue() );
	    	
	    	Core.inRange(rgb, lower, upper, filtered);
	    	
	    	Mat filterCopy = new Mat();
	    	filterCopy = filtered.clone();
	    	
	    	Point avgCenter = new Point(-1,-1);
	    	
	    	Moments moms = Imgproc.moments(filtered);
	    	double size = moms.get_m00() / 100;
	        if(size != 0) {
		    	double avgX = moms.get_m10() / moms.get_m00();
		    	double avgY = moms.get_m01() / moms.get_m00();
		    	avgCenter.x = avgX;
		    	avgCenter.y = avgY;
				
		    	double radius = Math.sqrt(size / Math.PI);
		    	Imgproc.cvtColor(filtered, filtered, Imgproc.COLOR_GRAY2BGR);
			    Core.circle(filtered, avgCenter, (int) radius, new Scalar(0,0,255), 1);
	        }
	        System.out.println(size);
	    	
	    	Imgproc.Canny(frame, edges, sliders[0].getValue(), sliders[1].getValue());
	    	Imgproc.cvtColor(edges, edges, Imgproc.COLOR_GRAY2BGR);

	    	gui.getContentPane().getGraphics().drawImage(toBufferedImage(filtered), 0, 0, null);
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