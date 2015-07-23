package org.auvua.model;

public class MotionUtil {
  public static double MAX_OUTPUT = .15;
  
  public static double clamp(double num) {
    num = num > MAX_OUTPUT ? MAX_OUTPUT : num;
    num = num < -MAX_OUTPUT ? -MAX_OUTPUT : num;
    return num;
  }
  
}
