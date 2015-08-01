package org.auvua.vision.tests;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.JTextField;

import org.auvua.vision.CameraViewer;
import org.auvua.vision.FloorMarkerFilter;
import org.auvua.vision.ImageFilter;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

public class TestFilterWithImage {

  public static void main( String[] args ) {
    System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
    
    ImageFilter filter = new FloorMarkerFilter(false);

    CameraViewer viewer = new CameraViewer();

    Mat image = new Mat();

    JFrame controls = new JFrame();
    controls.setLayout(new GridLayout(7,1));
    JSlider[] sliders = new JSlider[6];

    int[] initValues = { 10,  41,  0,  255,  112,  255 };

    for(int i = 0; i < sliders.length; i++) {
      sliders[i] = new JSlider(0,255,initValues[i]);
      sliders[i].setMaximum(255);
      sliders[i].setMinimum(0);
      sliders[i].setMajorTickSpacing(32);
      sliders[i].setPaintLabels(true);
      sliders[i].setPaintTicks(true);
      controls.add(sliders[i]);
    }
    
    JTextField field = new JTextField("/home/auvua/repos/robosub-2015/images/2015-07-26-09-58-47/img0.png");
    controls.add(field);

    controls.setVisible(true);
    controls.setSize(600,500);

    

    while(true) {
      image = Highgui.imread(field.getText());
      if (image.width() != 0 && image.height() != 0) {
        ((FloorMarkerFilter) filter).lower = new Scalar(sliders[0].getValue(), sliders[2].getValue(), sliders[4].getValue());
        ((FloorMarkerFilter) filter).upper = new Scalar(sliders[1].getValue(), sliders[3].getValue(), sliders[5].getValue());
        filter.filter(image);
        viewer.setImageFromMat(filter.getImageOut());
      }
      
      System.out.println("Lower: " + sliders[0].getValue() + " " + sliders[2].getValue() + " " + sliders[4].getValue());
      System.out.println("Upper: " + sliders[1].getValue() + " " + sliders[3].getValue() + " " + sliders[5].getValue());

      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

}
