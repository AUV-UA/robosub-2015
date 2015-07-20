package org.auvua.view;

import java.awt.Component;

import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Group;
import javax.media.j3d.Locale;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.auvua.model.dangerZona.DangerZonaPhysicsModel;
import org.auvua.vision.CapturingCanvas3D;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class DangerZonaRenderer {
  
  public SimpleUniverse universe;
  private DangerZonaPhysicsModel robot;
  
  private TransformGroup robotTG = new TransformGroup();
  private Transform3D robotTrans = new Transform3D();
  
  CapturingCameraView frontCamera = new CapturingCameraView(400, 400, false);
  private Transform3D frontCameraTrans = new Transform3D();
  
  CapturingCameraView downCamera = new CapturingCameraView(400, 400, false);
  private Transform3D downCameraTrans = new Transform3D();
  
  public Transform3D viewTrans = new Transform3D();

  public Vector3d cameraPos = new Vector3d(5.0, -5.0, 5.0);
  public BranchGroup rootGroup;
  
  public CapturingCanvas3D frontCameraCanvas;
  public CapturingCanvas3D downCameraCanvas;
  
  public Component frontCameraComponent;
  public Component downCameraComponent;
  
  private static DangerZonaRenderer instance;
  
  public static DangerZonaRenderer getInstance() {
    if (instance == null) {
      instance = new DangerZonaRenderer(DangerZonaPhysicsModel.getInstance());
    }
    return instance;
  }
  
  public DangerZonaRenderer(DangerZonaPhysicsModel robot) {
    
    this.robot = robot;
    
    universe = new SimpleUniverse();
    Locale locale = new Locale(universe);

    rootGroup = new BranchGroup();
    rootGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
    
    robotTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    robotTG.setTransform(robotTrans);
    robotTG.addChild(new ColorCube(0.35));

    rootGroup.addChild(robotTG);
    
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
    
    JFrame fcFrame = new JFrame();
    fcFrame.add(frontCamera.getCanvas3D());
    fcFrame.setVisible(true);
    fcFrame.setSize(400, 400);
    frontCameraComponent = fcFrame.getRootPane();
    
    JFrame dcFrame = new JFrame();
    dcFrame.add(downCamera.getCanvas3D());
    dcFrame.setVisible(true);
    dcFrame.setSize(400, 400);
    downCameraComponent = dcFrame.getRootPane();
    
    frontCameraCanvas = frontCamera.getCanvas3D();
    downCameraCanvas = downCamera.getCanvas3D();

    locale.addBranchGraph( frontCamera.getRootBG() );
    locale.addBranchGraph( downCamera.getRootBG() );
    
    universe.getViewingPlatform().getViewPlatformTransform().setTransform(viewTrans);
    universe.getViewer().getView().setBackClipDistance(1000.0f);
    
    addDirectionalLight(new Vector3f(0.0f, 0.0f, -1.0f), new Color3f(.5f, .5f, .5f));

    universe.addBranchGraph(createSceneGraph());
    universe.addBranchGraph(rootGroup);
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
    
    Matrix3d rotation = robot.kinematics.orientation.asMatrix3d();
    
    robotTrans.setTranslation(robot.kinematics.pos);
    robotTrans.setRotation(rotation);
    robotTG.setTransform(robotTrans);
    
    frontCameraTrans.setRotation(robot.frontCamera.getAbsoluteOrientation().asMatrix3d());
    frontCameraTrans.setTranslation(robot.frontCamera.getAbsolutePosition());
    
    downCameraTrans.setRotation(robot.downCamera.getAbsoluteOrientation().asMatrix3d());
    downCameraTrans.setTranslation(robot.downCamera.getAbsolutePosition());
    
    frontCamera.getViewPlatformTransformGroup().setTransform( frontCameraTrans );
    downCamera.getViewPlatformTransformGroup().setTransform( downCameraTrans );
  }
  
  public BranchGroup createSceneGraph() {
    
    BranchGroup objRoot = new BranchGroup();
   
    BoundingSphere bounds = new BoundingSphere(new Point3d(1000.0, -1000.0, 1000.0), 3000.0);
    Background bg = new Background();
    bg.setApplicationBounds(bounds);
    BranchGroup backGeoBranch = new BranchGroup();
    
    Appearance floorAppearance = new Appearance();
    Material floorMat = new Material();
    floorMat.setDiffuseColor(new Color3f(.31f, .49f, .49f));
    floorMat.setSpecularColor(new Color3f(.31f, .49f, .49f));
    floorMat.setShininess(1.0f);
    floorAppearance.setMaterial(floorMat);
    Box floor = new Box(100f, 100f, .02f, Box.GENERATE_NORMALS, floorAppearance);
    TransformGroup floorTrans = new TransformGroup();
    Transform3D floorTransform = new Transform3D();
    floorTransform.setTranslation(new Vector3d(0, 0, -5.5));
    floorTrans.setTransform(floorTransform);
    floorTrans.addChild(floor);
    
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
    objRoot.addChild(createFloorMarker(new Vector3d(0, 10.0, -5.0), Math.PI / 3));
    objRoot.addChild(createFloorMarker(new Vector3d(-7.32, 15.0, -5.0), 2 * Math.PI / 3));
    objRoot.addChild(createFloorMarker(new Vector3d(-14.64, 10.0, -5.0), 0));
    objRoot.addChild(floorTrans);
 
    return objRoot;
}
  
  private Node createFloorMarker(Vector3d vector3d, double d) {
    Appearance markerAppearance = new Appearance();
    Material markerMat = new Material();
    markerMat.setDiffuseColor(new Color3f(.8f, .69f, .55f));
    markerMat.setSpecularColor(new Color3f(.8f, .69f, .55f));
    markerMat.setShininess(1.0f);
    markerAppearance.setMaterial(markerMat);
    Box marker = new Box(.152f, 1.219f, .02f, Box.GENERATE_NORMALS, markerAppearance);
    TransformGroup markerTrans = new TransformGroup();
    Transform3D markerTransform = new Transform3D();
    markerTransform.setTranslation(vector3d);
    markerTransform.setRotation(new AxisAngle4d(0, 0, 1.0, d));
    markerTrans.setTransform(markerTransform);
    markerTrans.addChild(marker);
    return markerTrans;
  }

  public void addDirectionalLight(Vector3f direction, Color3f color) {
    // Creates a bounding sphere for the lights
    BoundingSphere bounds = new BoundingSphere();
    bounds.setRadius(1000d);

    // Then create a directional light with the given
    // direction and color
    DirectionalLight lightD = new DirectionalLight(color, direction);
    lightD.setInfluencingBounds(bounds);

    // Then add it to the root BranchGroup
    rootGroup.addChild(lightD);
  }
  
}
