package org.auvua.util;

import jama.Matrix;

public class MatrixUtil {
  
  public static Matrix cross(Matrix m1, Matrix m2) {
    boolean m1Valid = m1.getColumnDimension() == 1 && m1.getRowDimension() == 3;
    boolean m2Valid = m2.getColumnDimension() == 1 && m2.getRowDimension() == 3;
    if (!m1Valid || !m2Valid) throw new IllegalArgumentException("Matrices must be column vectors of size 3");
    Matrix out = new Matrix(3,1);
    double[] u = m1.getColumnVector(0);
    double[] v = m2.getColumnVector(0);
    out.set(0, 0, u[1]*v[2] - u[2]*v[1]);
    out.set(1, 0, u[2]*v[0] - u[0]*v[2]);
    out.set(2, 0, u[0]*v[1] - u[1]*v[0]);
    return out;
  }
  
  public static String mat2str(Matrix m) {
    String str = "";
    double[][] arr = m.getArray();
    for (int r = 0; r < arr.length; r++) {
      for (int c = 0; c < arr[r].length; c++) {
        str += String.format("%12.8f ", arr[r][c]);
      }
      str += "\n";
    }
    return str;
  }
  
}
