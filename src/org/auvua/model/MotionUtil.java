package org.auvua.model;

import org.auvua.MissionConfig;

public class MotionUtil {
  public static double clamp(double num) {
    double max = MissionConfig.MAX_OUTPUT;
    num = num > max ? max : num;
    num = num < -max ? -max : num;
    return num;
  }
  
}
