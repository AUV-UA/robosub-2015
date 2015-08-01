package org.auvua.vision;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class CameraSim implements ImageSource {
  Mat rawImage = new Mat();
  private CapturingCanvas3D canvas;

  public CameraSim(CapturingCanvas3D canvas) {
    this.canvas = canvas;
  }
  
  public void capture() {
    BufferedImage bImage = canvas.getLastImage();
    rawImage = img2Mat(bImage);
    
//    Mat noise = rawImage.clone();
//    Core.randn(noise, 0, 10);
//    Core.add(rawImage, noise, rawImage);
  }
  
  public Image getImage() {
    return toBufferedImage(rawImage);
  }

  @Override
  public Mat getMat() {
    return rawImage;
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

  /**
   * 
   * @param in
   * @return A BGR Matrix
   */
  public static Mat img2Mat(BufferedImage in) {
    Mat out;
    byte[] data;
    int r, g, b;
    int width = in.getWidth();
    int height = in.getHeight();

    if (in.getType() == BufferedImage.TYPE_INT_RGB || in.getType() == BufferedImage.TYPE_INT_ARGB) {
      out = new Mat(height, width, CvType.CV_8UC3);
      data = new byte[height * width * (int)out.elemSize()];
      int[] dataBuff = in.getRGB(0, 0, width, height, null, 0, width);
      for (int i = 0; i < dataBuff.length; i++) {
        data[i*3 + 2] = (byte) ((dataBuff[i] >> 16) & 0xFF);
        data[i*3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
        data[i*3 + 0] = (byte) ((dataBuff[i] >> 0) & 0xFF);
      }
    } else {
      out = new Mat(height, width, CvType.CV_8UC1);
      data = new byte[width * height * (int)out.elemSize()];
      int[] dataBuff = in.getRGB(0, 0, width, height, null, 0, width);
      for(int i = 0; i < dataBuff.length; i++) {
        r = (byte) ((dataBuff[i] >> 16) & 0xFF);
        g = (byte) ((dataBuff[i] >> 8) & 0xFF);
        b = (byte) ((dataBuff[i] >> 0) & 0xFF);
        data[i] = (byte)((0.21 * r) + (0.71 * g) + (0.07 * b)); //luminosity
      }
    }
    out.put(0, 0, data);
    return out;
  } 
}
