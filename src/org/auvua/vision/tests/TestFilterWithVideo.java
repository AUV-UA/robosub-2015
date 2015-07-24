package org.auvua.vision.tests;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JSlider;


import org.auvua.vision.CameraViewer;
import org.auvua.vision.FloorMarkerFilter;
import org.auvua.vision.ImageFilter;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

public class TestFilterWithVideo {

  public static void main( String[] args ) {
    System.loadLibrary( Core.NATIVE_LIBRARY_NAME );

    CameraViewer viewer = new CameraViewer();

    //		VideoCapture video = new VideoCapture("~/Videos/vlc-record-2015-07-24-11h21m49s-v4l2____dev_video1-.avi");
    VideoCapture video = new VideoCapture(0);

    if(!video.isOpened()){
      System.out.println("Video Error");
    } else{
      System.out.println("Video Opened");
    }

    Mat image = new Mat();


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

    ImageFilter filter = new FloorMarkerFilter();

    while(true) {
      video.read(image);
      filter.filter(image);
      viewer.setImageFromMat(filter.getImageOut());

      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

}
