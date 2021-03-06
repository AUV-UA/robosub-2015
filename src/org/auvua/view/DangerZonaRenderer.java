package org.auvua.view;

import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.auvua.model.dangerZona.DangerZonaPhysicsModel;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class DangerZonaRenderer {
  
  private TransformGroup objTrans;
  private Transform3D trans;
  public SimpleUniverse universe;
  private DangerZonaPhysicsModel robot;
  public Transform3D viewTrans;
  public Vector3d cameraPos = new Vector3d(1000.0, -1000.0, 1000.0);
  
  public DangerZonaRenderer(DangerZonaPhysicsModel robot) {
    this.robot = robot;
    
    universe = new SimpleUniverse();

    BranchGroup group = new BranchGroup();
    
    objTrans = new TransformGroup();
    objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    trans = new Transform3D();
    trans.setTranslation(new Vector3f(0.0f,0.0f,0.0f));
    objTrans.setTransform(trans);
    objTrans.addChild(new ColorCube(20.0));

    group.addChild(objTrans);

    viewTrans = new Transform3D();
    viewTrans.setTranslation(cameraPos);
    
    double s2 = 1.0 / Math.sqrt(2.0);
    double s3 = 1.0 / Math.sqrt(3.0);
    double s6 = 1.0 / Math.sqrt(6.0);
    double s23 = Math.sqrt(2.0 / 3.0);
    viewTrans.setRotation(new Matrix3d(new double[] {
        s2, -s6, s3,
        s2, s6, -s3,
        0.0, s23, s3
    }));
    
    universe.getViewingPlatform().getViewPlatformTransform().setTransform(viewTrans);
    universe.getViewer().getView().setBackClipDistance(10000.0f);

    universe.addBranchGraph(group);
    universe.addBranchGraph(createSceneGraph());
  }
  
  public void update() {
    
    Vector3d viewZ = new Vector3d();
    viewZ.sub(cameraPos, robot.kinematics.pos);
    viewZ.normalize();
    
    Vector3d viewX = new Vector3d();
    viewX.x = -viewZ.y;
    viewX.y = viewZ.x;
    viewX.normalize();
    
    Vector3d viewY = new Vector3d();
    viewY.cross(viewZ, viewX);
    
    Matrix3d cameraMat = new Matrix3d();
    cameraMat.setColumn(0, viewX);
    cameraMat.setColumn(1, viewY);
    cameraMat.setColumn(2, viewZ);

    viewTrans.setRotation(cameraMat);
    universe.getViewingPlatform().getViewPlatformTransform().setTransform(viewTrans);
    
    Matrix3d rotation = robot.kinematics.orientation.asMatrix();
    
    trans.setTranslation(robot.kinematics.pos);
    trans.setRotation(rotation);
    objTrans.setTransform(trans);
  }
  
  public BranchGroup createSceneGraph() {
    
    BranchGroup objRoot = new BranchGroup();
   
    BoundingSphere bounds = new BoundingSphere(new Point3d(1000.0, -1000.0, 1000.0), 3000.0);
    Background bg = new Background();
    bg.setApplicationBounds(bounds);
    BranchGroup backGeoBranch = new BranchGroup();
    Sphere sphereObj = new Sphere(1.1f, Sphere.GENERATE_NORMALS
        | Sphere.GENERATE_NORMALS_INWARD
        | Sphere.GENERATE_TEXTURE_COORDS, 45);
    
    Transform3D transform = new Transform3D();
    transform.setRotation(new AxisAngle4d(1.0, 0.0, 0.0, -Math.PI / 2));
    
    TransformGroup bgTrans = new TransformGroup();
    bgTrans.setTransform(transform);
    bgTrans.addChild(sphereObj);
    
    Appearance backgroundApp = sphereObj.getAppearance();
    backGeoBranch.addChild(bgTrans);
    bg.setGeometry(backGeoBranch);
 
    TextureLoader tex = new TextureLoader("background.jpg", new String(
        "RGB"), null);
    if (tex != null) {
      backgroundApp.setTexture(tex.getTexture());
    }
    objRoot.addChild(bg);
 
    return objRoot;
}
  
}
