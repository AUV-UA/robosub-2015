package org.auvua.vision.tests;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JSlider;

import org.auvua.vision.Camera;
import org.auvua.vision.FloorMarkerFilter;
import org.auvua.vision.ImageFilter;
import org.auvua.vision.ManeuverRedPoleFilter;
import org.auvua.vision.RedDownBuoyFilter;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

public class TestFilterWithVideo {
	
	public static void main( String[] args ) {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		
		VideoCapture video = new VideoCapture("~/Documents/GOPR0025.MP4");

	    if(!video.isOpened()){
	        System.out.println("Video Error");
	    } else{
	        System.out.println("Video Opened");
	    }
	    
	    Mat image = new Mat();
		
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
	    
	    ImageFilter filter = new FloorMarkerFilter();
	    
	    while(true) {
		    video.read(image);
		    System.out.println(filter.filter(image));
		    
	    	gui.getContentPane().getGraphics().drawImage(Camera.toBufferedImage(image), 0, 0, null);
	    	gui.setSize(image.width(), image.height());
	    	
	    	try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
	
}
