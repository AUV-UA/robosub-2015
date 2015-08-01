package org.auvua.vision.tests;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JSlider;

import org.auvua.vision.CameraViewer;
import org.auvua.vision.FloorMarkerFilter;
import org.auvua.vision.ImageFilter;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.VideoCapture;

public class TestFilterWithVideo {

  public static void main( String[] args ) throws InterruptedException {
    System.loadLibrary( Core.NATIVE_LIBRARY_NAME );

    CameraViewer viewer = new CameraViewer();

    VideoCapture video = new VideoCapture("/home/auvua/Videos/2015-07-24-down.mp4");
//    VideoCapture video = new VideoCapture("/home/auvua/Videos/FloorMarker1.mp4");
    
//    VideoCapture video = new VideoCapture(0);

    if(!video.isOpened()){
      System.out.println("Video Error");
    } else{
      System.out.println("Video Opened");
    }

    Mat image = new Mat();


    JFrame controls = new JFrame();
    controls.setLayout(new GridLayout(6,1));
    JSlider[] sliders = new JSlider[6];

    int[] initValues = { 14,  38,  0,  255,  200,  255 };

    for(int i = 0; i < sliders.length; i++) {
      sliders[i] = new JSlider(0,255,initValues[i]);
      sliders[i].setMaximum(255);
      sliders[i].setMinimum(0);
      controls.add(sliders[i]);
    }

    controls.setVisible(true);
    controls.setSize(200,500);

    ImageFilter filter = new FloorMarkerFilter(false);

    while(true) {
      video.read(image);
      ((FloorMarkerFilter) filter).lower = new Scalar(sliders[0].getValue(), sliders[2].getValue(), sliders[4].getValue());
      ((FloorMarkerFilter) filter).upper = new Scalar(sliders[1].getValue(), sliders[3].getValue(), sliders[5].getValue());
      filter.filter(image);
      viewer.setImageFromMat(filter.getImageOut());
      System.out.println("Lower: " + sliders[0].getValue() + " " + sliders[2].getValue() + " " + sliders[4].getValue());
      System.out.println("Upper: " + sliders[1].getValue() + " " + sliders[3].getValue() + " " + sliders[5].getValue());
      Thread.sleep(10);
    }
  }

}
