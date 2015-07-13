package org.auvua.vision;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

public class Camera {
	Map<String,ImageFilter> filters = new HashMap<String,ImageFilter>();
	Map<String,Object> filterOutputs = new HashMap<String,Object>();
	
	Mat rawImage = new Mat();
	VideoCapture camera;
	
	public Camera() {
		camera = new VideoCapture(0);
	    if(!camera.isOpened()){
	        System.out.println("Camera Error");
	    }
	    else{
	        System.out.println("Camera Opened");
	    }
	}
	
	public Camera(int deviceNumber) {
		camera = new VideoCapture(deviceNumber);
	    if(!camera.isOpened()){
	        System.out.println("Camera Error");
	    }
	    else{
	        System.out.println("Camera Opened");
	    }
	}
	
	public void addFilter(String filterName, ImageFilter toAdd) {
		filters.put(filterName,toAdd);
		filterOutputs.put(filterName, toAdd.getFilterValues());
	}
	
	public void removeFilter(String filterName) {
		filters.remove(filterName);
	}
	
	public void capture() {
		camera.read(rawImage);
	}
	
	public Image getImage() {
		return toBufferedImage(rawImage);
	}
	
	public void applyFilters() {
		Set<String> filterNames = filters.keySet();
		for(String s : filterNames) {
			filterOutputs.put(s, filters.get(s).filter(rawImage));
		}
	}
	
	public Map<String,Object> getFilterOutputs() {
		return filterOutputs;
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
