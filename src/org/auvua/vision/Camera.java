package org.auvua.vision;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

public class Camera implements ImageSource {
  boolean opened;
  Mat rawImage = new Mat();
  VideoCapture capture;

  public Camera(int deviceNumber) {
    capture = new VideoCapture(deviceNumber);
    if(!capture.isOpened()){
      opened = false;
      System.out.println("Camera Error");
    } else {
      opened = true;
      System.out.println("Camera Opened");
    }
  }

  public void capture() {
    if (opened) {
      capture.read(rawImage);
    }
  }

  public Image getImage() {
    return toBufferedImage(rawImage);
  }

  public static Image toBufferedImage(Mat m){
    int type = BufferedImage.TYPE_BYTE_GRAY;
    if ( m.channels() > 1 ) {
      type = BufferedImage.TYPE_3BYTE_BGR;
    }
    int bufferSize = m.channels()*m.cols()*m.rows();
    byte [] b = new byte[bufferSize];
    m.get(0,0,b); // get all the pixels
    if (m.cols() == 0 && m.rows() == 0) {
      return null;
    }
    BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
    final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    System.arraycopy(b, 0, targetPixels, 0, b.length);  
    return image;
  }

  @Override
  public Mat getMat() {
    return rawImage;
  }
}
