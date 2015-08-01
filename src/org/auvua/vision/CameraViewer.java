package org.auvua.vision;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.JFrame;

import org.opencv.core.Mat;

public class CameraViewer {

  private JFrame gui;

  public CameraViewer() {
    gui = new JFrame();

    gui.setVisible(true);
    gui.setPreferredSize(new Dimension(800,800));

    gui.setResizable(false);
    gui.setSize(800, 800);
  }

  public void setImage( Image i ) {
    gui.getContentPane().getGraphics().drawImage(i, 0, 0, null);
    gui.setSize(i.getWidth(null), i.getHeight(null));
  }
  
  public void setImageFromMat(Mat mat) {
    setImage(toBufferedImage(mat));
  }
  
  public static Image toBufferedImage(Mat m) {
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
