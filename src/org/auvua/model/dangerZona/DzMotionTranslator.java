package org.auvua.model.dangerZona;

import org.auvua.util.MatrixUtil;

import jama.EigenvalueDecomposition;
import jama.Matrix;

/**
 * 
 * @author sean
 *
 * Translates desired motion (acceleration and angular acceleration) into thruster values
 */
public class DzMotionTranslator {
  
  private static final double SQRT_3 = Math.sqrt(3.0);
  
  public final Matrix system;
  public Matrix force = new Matrix(3, 1);
  public Matrix torque = new Matrix(3, 1);
  
  // A 3x3 matrix representing the orientation of the robot
  // Each row represents the direction of the robot's current principal
  // X, Y, and Z axes respectively.
  public Matrix orientation;
  
  public final Matrix forceLocations = new Matrix(new double[][] {
      { 0.216,  0.267, 0.000},
      {-0.216,  0.267, 0.000},
      {-0.216, -0.267, 0.000},
      { 0.216, -0.267, 0.000},
      { 0.146,  0.216, 0.100},
      {-0.146,  0.216, 0.100},
      {-0.146, -0.216, 0.100},
      { 0.146, -0.216, 0.100}
  });
  
  public final Matrix forceDirections = new Matrix(new double[][] {
      {-0.500, SQRT_3 / 2, 0.000},
      { 0.500, SQRT_3 / 2, 0.000},
      {-0.500, SQRT_3 / 2, 0.000},
      { 0.500, SQRT_3 / 2, 0.000},
      { 0.000,      0.000, 1.000},
      { 0.000,      0.000, 1.000},
      { 0.000,      0.000, 1.000},
      { 0.000,      0.000, 1.000},
  });
  
  public final Matrix torqueDirections = new Matrix(8,3);
  
  // Additional constraints since system is under-determined
  public final Matrix constraints = new Matrix(new double[][] {
      {1, 1, -1, -1, 0,  0, 0,  0},
      {0, 0,  0,  0, 1, -1, 1, -1}
  });
  
  public DzMotionTranslator() {
    this.system = new Matrix(8,8);
    
    Matrix forceLocTrans = forceLocations.transpose();
    Matrix forceDirTrans = forceDirections.transpose();
    
    for (int i = 0; i < forceDirections.getRowDimension(); i++) {
      double[] u = forceLocTrans.getColumnVector(i);
      double[] v = forceDirTrans.getColumnVector(i);
      torqueDirections.set(i, 0, u[1]*v[2] - u[2]*v[1]);
      torqueDirections.set(i, 1, u[2]*v[0] - u[0]*v[2]);
      torqueDirections.set(i, 2, u[0]*v[1] - u[1]*v[0]);
    }
    
    system.setMatrix(0, 2, 0, 7, forceDirections.transpose());
    system.setMatrix(3, 5, 0, 7, torqueDirections.transpose());
    system.setMatrix(6, 7, 0, 7, constraints);
  }
  
  // Solve for thrust outputs assuming absolute-oriented accelerations
  public Matrix solveGlobal() {
    Matrix accelLocal = orientation.transpose().times(force);
    Matrix angAccelLocal = orientation.transpose().times(torque);
    
    Matrix motion = new Matrix(8,1);
    motion.setMatrix(0, 2, 0, 0, accelLocal);
    motion.setMatrix(3, 5, 0, 0, angAccelLocal);
    
    Matrix thrusts = system.solve(motion);
    
    return thrusts;
  }
  
  //Solve for thrust outputs using robot-oriented accelerations
  public Matrix solveLocal() {
    Matrix motion = new Matrix(8,1);
    motion.setMatrix(0, 2, 0, 0, force);
    motion.setMatrix(3, 5, 0, 0, torque);
    
    Matrix thrusts = system.solve(motion);
    
    return thrusts;
  }
  
  public static Matrix getRotationMatrix(Matrix initialOrientation, Matrix finalOrientation) {
    Matrix rot = finalOrientation.times(initialOrientation.transpose());
    return rot;
  }
  
  public static Rotation getRotation(Matrix initialOrientation, Matrix finalOrientation) {
    Matrix rotationMatrix = getRotationMatrix(initialOrientation, finalOrientation);
    EigenvalueDecomposition eigenDecomp = rotationMatrix.eig();
    double[] imEigParts = eigenDecomp.getImagEigenvalues();
    double[] imRealParts = eigenDecomp.getRealEigenvalues();
    for (int i = 0; i < imEigParts.length; i++) {
      if (imEigParts[i] == 0 && Math.abs(imRealParts[i] - 1) < 0.0001 ) {
        double trace = rotationMatrix.trace();
        double angle = Math.acos(Math.min(1, Math.max(-1, (trace - 1) / 2)));
        Matrix vector = eigenDecomp.getV().getMatrix(0, 2, i, i);
        double x = vector.get(0, 0);
        double y = vector.get(1, 0);
        if (x == 0 && y == 0) {
          x = 1;
          y = 0;
        }
        double mag = Math.sqrt(x * x + y * y);
        Matrix p1 = new Matrix(new double[][] {
            { -y / mag, x / mag, 0 }
        }).transpose();
        Matrix p2 = rotationMatrix.times(p1);
        Matrix cross = MatrixUtil.cross(p1, p2);
        if (cross.transpose().times(vector).get(0, 0) < 0) {
          vector = vector.times(-1);
        }
        return new Rotation(vector, angle);
      }
    }
    return null;
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
  
  public static class Rotation {
    public Matrix vector;
    public double angle;
    public Rotation(Matrix vector, double angle) {
      this.vector = vector;
      this.angle = angle;
    }
    
    public String toString() {
      double x = vector.get(0, 0);
      double y = vector.get(1, 0);
      double z = vector.get(2, 0);
      return String.format("X: %-8.4f Y: %-8.4f Z: %-8.4f A: %-8.4f", x, y, z, angle);
    }
  }
  
}
