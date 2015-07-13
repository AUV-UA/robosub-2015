package org.auvua.vision.tests;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JSlider;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

/**
 * 
 * @author root
 * 
 * Filter testing class
 */
public class TestColorFiltering {
	
	public static void main( String[] args )
	{
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		
		Mat raw = Highgui.imread("marker.png", Highgui.IMREAD_COLOR);
		System.out.println(raw.cols());
		System.out.println(raw.rows());
		System.out.println(raw.channels());
		
	    JFrame gui = new JFrame();
	    JFrame controls = new JFrame();
	    controls.setLayout(new GridLayout(6,1));
	    JSlider[] sliders = new JSlider[6];
	    
	    int[] initValues = { 150, 255, 0, 255, 0, 255 };
	    
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
	    
	    gui.getContentPane().getGraphics().drawImage(toBufferedImage(raw), 0, 0, null);
	    

	    
	    while(true) {
		    Mat frame = new Mat(),
			        filtered = new Mat(),
		            edges = new Mat(),
		            rgb = new Mat();
	    	
	    	frame = raw.clone();
			
	    	Imgproc.cvtColor(frame, rgb, Imgproc.COLOR_BGR2RGB);
	    	
	    	Scalar lower = new Scalar( sliders[0].getValue(), sliders[2].getValue(), sliders[4].getValue() );
	    	Scalar upper = new Scalar( sliders[1].getValue(), sliders[3].getValue(), sliders[5].getValue() );
	    	
	    	for(int i = 0; i < 6; i++) {
	    		System.out.print(sliders[i].getValue() + "\t");
	    	}
	    	System.out.println();
	    	
	    	Core.inRange(rgb, lower, upper, filtered);
	    	
	    	Mat filterCopy = new Mat();
	    	filtered.assignTo(filterCopy);
	    	
	    	Imgproc.Canny(frame, edges, sliders[0].getValue(), sliders[1].getValue());
	    	Imgproc.cvtColor(edges, edges, Imgproc.COLOR_GRAY2BGR);

	    	gui.getContentPane().getGraphics().drawImage(toBufferedImage(filterCopy), 0, 0, null);
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