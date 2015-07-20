package org.auvua.view;

import java.awt.*;

import javax.media.j3d.*;

class CameraView {

  protected static final PhysicalBody physBody = new PhysicalBody();
  protected static final PhysicalEnvironment physEnv =
      new PhysicalEnvironment();

  protected BranchGroup rootBG = null;
  protected TransformGroup vpTG = null;
  protected ViewPlatform viewPlatform = null;
  protected View view = null;
  protected Canvas3D canvas = null;
  
  public CameraView (int width, int height) {
    this(width, height, false);
  }

  public CameraView (int width, int height, boolean offscreen) {

    GraphicsConfigTemplate3D gconfigTempl =
        new GraphicsConfigTemplate3D();
    GraphicsConfiguration gconfig = 
        GraphicsEnvironment.getLocalGraphicsEnvironment().
        getDefaultScreenDevice().
        getBestConfiguration( gconfigTempl );

    canvas = new Canvas3D( gconfig, offscreen );
    canvas.setSize(new Dimension(width, height));
    
    if (offscreen) {
      Screen3D sOff = canvas.getScreen3D();
      sOff.setSize(new Dimension(400,400));
      sOff.setPhysicalScreenWidth(400);
      sOff.setPhysicalScreenHeight(400);
    }

    

    viewPlatform = new ViewPlatform();
    
    view = new View();
    
    view.setPhysicalBody( physBody );
    view.setPhysicalEnvironment( physEnv );
    view.attachViewPlatform( viewPlatform );
    view.addCanvas3D( canvas );
    view.setProjectionPolicy( View.PERSPECTIVE_PROJECTION );

    vpTG = new TransformGroup();
    vpTG.setCapability( TransformGroup.ALLOW_TRANSFORM_READ );
    vpTG.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
    vpTG.addChild( viewPlatform );

    rootBG = new BranchGroup();
    rootBG.setCapability( BranchGroup.ALLOW_DETACH );
    rootBG.addChild(vpTG);

  }

  public TransformGroup getViewPlatformTransformGroup() {
    return this.vpTG;
  }

  public BranchGroup getRootBG() {
    return this.rootBG;
  }

  public View getView() {
    return this.view;
  }

  public Canvas3D getCanvas3D() {
    return this.canvas;
  }

}