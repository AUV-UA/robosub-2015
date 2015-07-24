package org.auvua.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

import org.auvua.vision.Camera;
import org.auvua.vision.ImageSource;

public class CameraPanel extends JPanel {
  private static final long serialVersionUID = -2868409220823588279L;
  private ImageSource source;
  
  public CameraPanel (ImageSource source) {
    this.source = source;
    setPreferredSize(new Dimension(640, 480));
  }
  
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Image img = Camera.toBufferedImage(source.getMat());
    if (img != null)
      g.drawImage(img, 0, 0, 640, 480, null);
  }
}
