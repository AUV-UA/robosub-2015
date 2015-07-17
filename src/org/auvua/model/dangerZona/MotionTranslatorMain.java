package org.auvua.model.dangerZona;

import jama.Matrix;

public class MotionTranslatorMain {
  public static void main(String[] args) {
    DzMotionTranslator translator = new DzMotionTranslator();
    
    translator.accel = new Matrix(new double[][] {
        {1, 0, 0} 
    }).transpose();
    
    translator.angAccel = new Matrix(new double[][] {
        {0, 0, 0} 
    }).transpose();
    
    translator.orientation = new Matrix(new double[][] {
        {1, 0, 0},
        {0, 1, 0},
        {0, 0, 1}
    });
    
    System.out.println(mat2str(translator.solveGlobal()));
    
    System.out.println(DzMotionTranslator.getRotation(new Matrix(new double[][] {
        {0, 1, 0},
        {0, 0, 1},
        {1, 0, 0}
    }), new Matrix(new double[][] {
        {1, 0, 0},
        {0, 1, 0},
        {0, 0, 1}
    })).toString());
  }
  
  public static String mat2str(Matrix m) {
    String str = "";
    double[][] arr = m.getArray();
    for (int r = 0; r < arr.length; r++) {
      for (int c = 0; c < arr[r].length; c++) {
        str += String.format("%12.6f ", arr[r][c]);
      }
      str += "\n";
    }
    return str;
  }
}
