package org.auvua.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.auvua.agent.control.Timer;
import org.auvua.reactive.core.RxVar;
import org.auvua.reactive.core.StandardDependency;

public class RChart extends StandardDependency {
  
  private final int width;
  private final int height;
  
  private Image graphImage;
  private Image valuesImage;
  private List<RxVar<? extends Number>> variables = new LinkedList<RxVar<? extends Number>>();
  private Map<RxVar<? extends Number>, Number> lastValues = new HashMap<RxVar<? extends Number>, Number>();
  private Map<RxVar<? extends Number>, String> labels = new HashMap<RxVar<? extends Number>, String>();
  
  private double lastTime;
  private final double pixelsPerSecond = 100;
  
  private final JPanel graphPanel;
  private final JPanel valuePanel;
  
  public RChart(int width, int height) {
    this.width = width;
    this.height = height;
    
    graphImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    valuesImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    
    graphPanel = new JPanel() {
      private static final long serialVersionUID = -3805700651446212348L;

      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(graphImage, 0, 0, null);
      }
    };
    
    valuePanel = new JPanel() {
      private static final long serialVersionUID = -3805700651446212348L;

      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(valuesImage, 0, 0, null);
      }
    };
    
    lastTime = Timer.getInstance().get();
  }

  @Override
  public void update() {
    double time = Timer.getInstance().get();
    Graphics gChart = graphImage.getGraphics();
    Graphics gValues = valuesImage.getGraphics();
    
    if (lastTime * pixelsPerSecond % width > time * pixelsPerSecond % width) {
      gChart.clearRect(0, 0, width, height);
      for (RxVar<? extends Number> var : variables) {
        lastValues.put(var, var.get());
      }
    } else {
      int i = 0;
      gValues.clearRect(0, 0, width, height);
      for (RxVar<? extends Number> var : variables) {
        double val = (double) var.get();
        
        if (lastValues.containsKey(var)) {
          double lastVal = (double) lastValues.get(var);
          
          int r = Integer.reverse(i << 1);
          float hue =  1.0f * r / Integer.MAX_VALUE;
          gChart.setColor(new Color(Color.HSBtoRGB(hue, 1, 1)));
          
          gChart.drawLine(
              (int) (lastTime * pixelsPerSecond % width),
              (int) lastVal + height / 2,
              (int) (time * pixelsPerSecond % width),
              (int) val + height / 2);
          
          gChart.clearRect(0, i * 20, 500, 20);
          gValues.drawString(labels.get(var) + ":", 10, 20 + i * 20);
          gValues.drawString(String.format("%.32f", val), 150, 20 + i * 20);
        }
        
        lastValues.put(var, val);
        i++;
      }
    }
    
    lastTime = time;
    
    graphPanel.repaint();
  }

  @Override
  public void awaitUpdate() {
    // TODO Auto-generated method stub
    
  }
  
  public void observe(RxVar<? extends Number> var, String label) {
    variables.add(var);
    labels.put(var, label);
    determineDependencies();
  }
  
  public JPanel getGraphPanel() {
    return graphPanel;
  }
  
  public JPanel getValuePanel() {
    return valuePanel;
  }
  
}
