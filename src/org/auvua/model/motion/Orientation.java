package org.auvua.model.motion;

import jama.Matrix;
import jama.SingularValueDecomposition;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

public class Orientation {
  public final Vector3d localX; // pitch
  public final Vector3d localY; // roll
  public final Vector3d localZ; // yaw
  public Matrix mat;
  
  public Orientation(Matrix mat) {
    this.mat = mat;
    localX = new Vector3d(mat.get(0, 0), mat.get(1, 0), mat.get(2, 0));
    localY = new Vector3d(mat.get(0, 1), mat.get(1, 1), mat.get(2, 1));
    localZ = new Vector3d(mat.get(0, 2), mat.get(1, 2), mat.get(2, 2));
  }
  
  public Orientation() {
    localX = new Vector3d(1,0,0);
    localY = new Vector3d(0,1,0);
    localZ = new Vector3d(0,0,1);
    mat = Matrix.identity(3, 3);
  }
  
  public void rotate(AxisAngle4d aa) {Matrix3d rotation3d = new Matrix3d();
    rotation3d.set(aa);
    
    Matrix rotation = mat3dToMat(rotation3d);
    
    Matrix temp = rotation.times(mat);
    SingularValueDecomposition svd = temp.svd();
    
    mat = svd.getU().times(svd.getV().transpose());
    
    localX.x = mat.get(0, 0);
    localX.y = mat.get(1, 0);
    localX.z = mat.get(2, 0);
    localY.x = mat.get(0, 1);
    localY.y = mat.get(1, 1);
    localY.z = mat.get(2, 1);
    localZ.x = mat.get(0, 2);
    localZ.y = mat.get(1, 2);
    localZ.z = mat.get(2, 2);
  }
  
  public Matrix3d asMatrix3d() {
    Matrix3d mat = new Matrix3d();
    mat.setColumn(0, localX);
    mat.setColumn(1, localY);
    mat.setColumn(2, localZ);
    return mat;
  }
  
  public Matrix asMatrix() {
    Matrix mat = new Matrix(new double[][] {
        {localX.x, localY.x, localZ.x},
        {localX.y, localY.y, localZ.y},
        {localX.z, localY.z, localZ.z}
    });
    return mat;
  }
  
  public double getYaw() {
    return Math.atan2(localY.x, localY.y);
  }
  
  public double getPitch() {
    return Math.asin(localY.z);
  }
  
  public double getRoll() {
    return Math.atan2(localY.z, localZ.z);
  }
  
  public static Matrix mat3dToMat (Matrix3d mat3d) {
    Matrix mat = new Matrix(new double[][] {
        {mat3d.m00, mat3d.m01, mat3d.m02},
        {mat3d.m10, mat3d.m11, mat3d.m12},
        {mat3d.m20, mat3d.m21, mat3d.m22}
    });
    return mat;
  }
  
  public void setMatrix(Matrix mat) {
    this.mat = mat;
    localX.x = mat.get(0, 0);
    localX.y = mat.get(1, 0);
    localX.z = mat.get(2, 0);
    localY.x = mat.get(0, 1);
    localY.y = mat.get(1, 1);
    localY.z = mat.get(2, 1);
    localZ.x = mat.get(0, 2);
    localZ.y = mat.get(1, 2);
    localZ.z = mat.get(2, 2);
  }
}
