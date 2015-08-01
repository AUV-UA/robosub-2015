package org.auvua.view;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Locale;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.auvua.model.motion.Orientation;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class OrientationRenderer {
  
  private TransformGroup objTrans;
  private Transform3D trans;
  public SimpleUniverse universe;
  private Orientation orientation;
  public Vector3d cameraPos = new Vector3d(100.0, -100.0, 100.0);
  
  public CameraView orientationCam = new CameraView(200, 200, true);
  private Transform3D orientationCamTrans = new Transform3D();
  
  public OrientationRenderer(Orientation orientation) {
    this.orientation = orientation;
    
    universe = new SimpleUniverse();
    Locale locale = new Locale(universe);

    BranchGroup group = new BranchGroup();
    
    objTrans = new TransformGroup();
    objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    trans = new Transform3D();
    trans.setTranslation(new Vector3f(0.0f,0.0f,0.0f));
    objTrans.setTransform(trans);
    objTrans.addChild(new ColorCube(20.0));

    group.addChild(objTrans);
    
    double s2 = 1.0 / Math.sqrt(2.0);
    double s3 = 1.0 / Math.sqrt(3.0);
    double s6 = 1.0 / Math.sqrt(6.0);
    double s23 = Math.sqrt(2.0 / 3.0);
    orientationCamTrans.setRotation(new Matrix3d(new double[] {
        s2, -s6, s3,
        s2, s6, -s3,
        0.0, s23, s3
    }));
    
    orientationCam.getView().setProjectionPolicy( View.PERSPECTIVE_PROJECTION );
    
    locale.addBranchGraph( orientationCam.getRootBG() );

    universe.addBranchGraph(group);
  }
  
  public void update() {
    Matrix3d rotation = orientation.asMatrix3d();
    orientationCamTrans.setRotation(rotation);
    orientationCam.getViewPlatformTransformGroup().setTransform( orientationCamTrans );
  }
  
}
