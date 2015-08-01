package org.auvua.vision;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class FloorMarkerFilter implements ImageFilter {

  public Mat imageOut;
  private Mat filtered = new Mat(), hsv = new Mat(), edges = new Mat();
  private boolean drawIndicators = true;
  private int imgNumber = 0;
  
  public boolean markerVisible = false;
  public double angle = 0.0;
  public double xPosition = 0.0;
  public double yPosition = 0.0;
  public double length = 0.0;
  public double width = 0.0;
  public double imageCenterX = 0.0;
  public double imageCenterY = 0.0;
  
  public String date = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
  public boolean saveImages = false;
  
//  public Scalar lower = new Scalar( 0, 15, 58 );
//  public Scalar upper = new Scalar( 53, 93, 255 );
  
  public Scalar lower = new Scalar( 10, 0, 112 );
  public Scalar upper = new Scalar( 41, 255, 255 );

  public FloorMarkerFilter() {
    this(true);
  }

  public FloorMarkerFilter(boolean b) {
    saveImages = b;
    if (saveImages) {
      new File("/home/auvua/repos/robosub-2015/images/" + date).mkdirs();
    }
  }

  @Override
  public void filter(Mat image) {
    if (saveImages && imgNumber % 20 == 0) {
      System.out.println("Writing image!");
      System.out.println(Highgui.imwrite("/home/auvua/repos/robosub-2015/images/" + date + "/img" + (imgNumber / 20) + ".png",
          image));
    }
    imgNumber++;
    
    image = image.t();
    Core.flip(image, image, 1);
    
    this.imageCenterX = image.width() / 2;
    this.imageCenterY = image.height() / 2;

    Imgproc.cvtColor(image, hsv, Imgproc.COLOR_BGR2HSV);
    // Daytime, 1 PM Cloudless
//    Scalar lower = new Scalar( 0, 15, 58 );
//    Scalar upper = new Scalar( 53, 93, 255 );
    
    // Nighttime, hotel pool with light
//    Scalar lower = new Scalar( 0, 0, 0 );
//    Scalar upper = new Scalar( 17, 255, 255 );
    
//    Scalar lower = new Scalar( 109, 78, 69 );
//    Scalar upper = new Scalar( 146, 143, 108 );
    
//    Scalar lower = new Scalar( 91, 78, 69 );
//    Scalar upper = new Scalar( 177, 255, 108 );

    Core.inRange(hsv, lower, upper, filtered);

    List<MatOfPoint> contours = new LinkedList<MatOfPoint>();
    Imgproc.findContours(filtered, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

    for (MatOfPoint contour : contours) {
      double area = Imgproc.contourArea(contour);
      if(area < 3000) continue;

      RotatedRect box = Imgproc.minAreaRect(new MatOfPoint2f(contour.toArray()));

      double theta;
      double length;
      double width;

      if(box.size.height > box.size.width) {
        theta = box.angle / 180 * Math.PI;
        length = box.size.height;
        width = box.size.width;
      } else {
        theta = (box.angle + 90) / 180 * Math.PI;
        length = box.size.width;
        width = box.size.height;
      }

      double rectangularity = (area / (length * width));
      double aspectRatio = length / width;

      if(aspectRatio >= 3 && aspectRatio <= 12 && rectangularity > .6) {
        this.markerVisible = true;
        this.angle = -theta;
        this.xPosition = box.center.x;
        this.yPosition = box.center.y;
        this.length = length;
        this.width = width;

        if(drawIndicators) {
          imageOut = image.clone();
          
          Imgproc.Canny(filtered, edges, 50, 150);

          Imgproc.cvtColor(edges, edges, Imgproc.COLOR_GRAY2BGR);

          Core.addWeighted(imageOut, 0.7, edges, 0.3, 0, imageOut);

          Core.circle(imageOut, box.center, 5, new Scalar(255,0,0), 3);

          Point start = new Point(), end = new Point();
          double a = Math.cos(theta), b = Math.sin(theta);

          start.x = Math.round(box.center.x + 1000*(-b));
          start.y = Math.round(box.center.y + 1000*(a));
          end.x = Math.round(box.center.x - 1000*(-b));
          end.y = Math.round(box.center.y - 1000*(a));

          Core.line(imageOut, start, end, new Scalar(0,255,0),2);
        }

        return;
      }
    }
    
    if(drawIndicators) {
      imageOut = image;
    }
    
    this.markerVisible = false;
    this.angle = 0.0;
  }

  @Override
  public Mat getImageOut() {
    return imageOut;
  }

}
