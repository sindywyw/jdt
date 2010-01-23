package gui3D;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.security.AllPermission;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.universe.SimpleUniverse;

import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;

public class Graphics3DEngine extends Applet{
	
	static final double bounding_sphere_size = 100000000.0;
	static final double viewer_max_distance = 100000000.0;
	
	static final double default_bounding_box_min_x = -1000.0;
	static final double default_bounding_box_max_x = 1000.0;
	static final double default_bounding_box_min_y = -1000.0;
	static final double default_bounding_box_max_y = 1000.0;
	static final double default_bounding_box_min_z = 0.0;
	static final double default_bounding_box_max_z = 100.0;
	
	static final int VIEW_TYPE_SURFACE = PolygonAttributes.POLYGON_FILL;
	static final int VIEW_TYPE_GRID = PolygonAttributes.POLYGON_LINE;
	static final int VIEW_TYPE_POINTS = PolygonAttributes.POLYGON_POINT;
	
	Triangle3DObject _triangles_drawer = null;
	
	SimpleUniverse _simpleU = null;
	BranchGroup _objRoot = null;
	
	Shape3D createLand(delaunay_triangulation.BoundingBox bound_box)
    { 	
    	int center_x = (int)bound_box.getMinPoint().x() + (int)((bound_box.getMaxPoint().x() - bound_box.getMinPoint().x()) / 2);
    	int center_y = (int)bound_box.getMinPoint().y() + (int)((bound_box.getMaxPoint().y() - bound_box.getMinPoint().y()) / 2);
    	int min_z = (int)bound_box.getMinPoint().z();
    	
        
        Color3f col_green = new Color3f(0.1f, 0.8f, 0.1f);
        
        int grid_size =  4 * Math.max(	(int)(bound_box.getMaxPoint().x() - bound_box.getMinPoint().x()),
        					(int)(bound_box.getMaxPoint().y() - bound_box.getMinPoint().y()) );
        int grid_spacing = (int) (0.05 * grid_size);
        
        LineArray landGeom = new LineArray((int)(((2*grid_size)/grid_spacing)+1)*4, 
				GeometryArray.COORDINATES |GeometryArray.COLOR_3);
        	
        float l = (float)-grid_size;
        
        for(int i = 0; i < ((2*grid_size)/grid_spacing)+1; i+=1){
            landGeom.setCoordinate( 4*i+0, new Point3f((float)-grid_size + (float)center_x, l + (float)center_y,min_z ));
            landGeom.setColor(4*i+0, col_green);
            landGeom.setCoordinate( 4*i+1, new Point3f((float)grid_size+ (float)center_x, l + (float)center_y,min_z ));
            landGeom.setColor(4*i+1, col_green);
            
            landGeom.setCoordinate( 4*i+2, new Point3f(l+ (float)center_x, (float)-grid_size + (float)center_y, min_z ));
            landGeom.setColor(4*i+2, col_green);
            landGeom.setCoordinate( 4*i+3, new Point3f(l+ (float)center_x, (float)grid_size + (float)center_y,  min_z));
            landGeom.setColor(4*i+3, col_green);
            l += (float)grid_spacing;
        }

        return new Shape3D(landGeom);
    }
    
	/*
	public BranchGroup createSceneGraph(SimpleUniverse su, Delaunay_Triangulation delaunay) 
	{
		// Create the root of the branch graph
	        TransformGroup vpTrans = null;

	        BranchGroup objRoot = new BranchGroup();

	        //TransformGroup TG = null;

	        delaunay_triangulation.BoundingBox dt_bounding_box = delaunay.getBoundingBox();
	        
	        objRoot.addChild(createLand(dt_bounding_box));
	        
	        vpTrans = su.getViewingPlatform().getViewPlatformTransform();
	        setMouseControls(objRoot, vpTrans);
	        setKeyboardControls(objRoot, vpTrans);
	        setCameraPosition(dt_bounding_box, vpTrans);
	        
	        _triangles_drawer = new Triangle3DObject(delaunay.trianglesIterator(), delaunay.trianglesSize()); 
	        objRoot.addChild(_triangles_drawer);

		// Let Java 3D perform optimizations on this scene graph.
	        objRoot.compile();

	        PolygonAttributes polyAttrib = new PolygonAttributes();
		    polyAttrib.setPolygonMode(PolygonAttributes.POLYGON_LINE);
		    
	        _triangles_drawer._appearance.setPolygonAttributes(polyAttrib);
	        return objRoot;
	    } // end of CreateSceneGraph method of KeyNavigatorApp */
	
	
	private void createSceneGraph(Delaunay_Triangulation delaunay) 
	{
	        delaunay_triangulation.BoundingBox dt_bounding_box = null;
	        if (delaunay != null)
	        {
	        	dt_bounding_box = delaunay.getBoundingBox();
	        }
	        else
	        {
	        	dt_bounding_box = new delaunay_triangulation.BoundingBox(new Point_dt(default_bounding_box_min_x, default_bounding_box_min_y, default_bounding_box_min_z), new Point_dt(default_bounding_box_max_x, default_bounding_box_max_y, default_bounding_box_max_z));
	        }
	        
	        if (_objRoot!=null)
	        {
	        	_objRoot.detach();
	        }
	        
	        _objRoot = new BranchGroup();
	        _objRoot.setCapability(BranchGroup.ALLOW_DETACH);
	        _objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
	        _objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
	        _objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
	        
	        _objRoot.addChild(createLand(dt_bounding_box));
	        
	        TransformGroup vpTrans = null;
	        vpTrans = _simpleU.getViewingPlatform().getViewPlatformTransform();
	        setMouseControls(_objRoot, vpTrans);
	        setKeyboardControls(_objRoot, vpTrans);
	        setCameraPosition(dt_bounding_box, vpTrans);
	        
	        if (delaunay != null)
	        {
	        	_triangles_drawer = new Triangle3DObject(delaunay.trianglesIterator(), delaunay.trianglesSize(), delaunay.getBoundingBox()); 
	        	_objRoot.addChild(_triangles_drawer);
	        }
		// Let Java 3D perform optimizations on this scene graph.
	        _objRoot.compile();

	        _simpleU.addBranchGraph(_objRoot);
	    }
	
	
	public void setNewDelaunayTriangulation(Delaunay_Triangulation dt)
	{
		createSceneGraph(dt);
	}
	
	/*
	public BranchGroup createSceneGraph() 
	{
		// Create the root of the branch graph
	        TransformGroup vpTrans = null;

	        _objRoot = new BranchGroup();
	        _objRoot.setCapability(BranchGroup.ALLOW_DETACH);
	        _objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
	        _objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
	        _objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

	        //TransformGroup TG = null;

	        delaunay_triangulation.BoundingBox dt_bounding_box = new delaunay_triangulation.BoundingBox(new Point_dt(default_bounding_box_min_x, default_bounding_box_min_y, default_bounding_box_min_z), new Point_dt(default_bounding_box_max_x, default_bounding_box_max_y, default_bounding_box_max_z));
	        
	        _objRoot.addChild(createLand(dt_bounding_box));
	        
	        vpTrans = _simpleU.getViewingPlatform().getViewPlatformTransform();
	        setMouseControls(_objRoot, vpTrans);
	        setKeyboardControls(_objRoot, vpTrans);
	        setCameraPosition(dt_bounding_box, vpTrans);

	        return _objRoot;
	 }*/
	
	private void setCameraPosition(delaunay_triangulation.BoundingBox bound_box, TransformGroup input_TG)
	{
		int height_supp = 100;
		int center_x = (int)bound_box.getMinPoint().x() + (int)((bound_box.getMaxPoint().x() - bound_box.getMinPoint().x()) / 2);
    	int center_y = (int)bound_box.getMinPoint().y() + (int)((bound_box.getMaxPoint().y() - bound_box.getMinPoint().y()) / 2);
    	int max_z = (int)bound_box.getMaxPoint().z();
    	System.out.println("max z:"+max_z);
    	
		Vector3f translate = new Vector3f();
        Transform3D T3D = new Transform3D();
        translate.set( (float)center_x, (float)center_y, (float)2*max_z+height_supp);
        System.out.println("camera position: "+center_x+" "+center_y+" "+(2*max_z+height_supp));
        T3D.setTranslation(translate);
        input_TG.setTransform(T3D);
	}
	
	private void setMouseControls(BranchGroup objRoot, TransformGroup input_TG)
	{
		BoundingSphere mouseBounds = new BoundingSphere(new Point3d(), bounding_sphere_size);
		MouseRotate myMouseRotate = new MouseRotate(MouseBehavior.INVERT_INPUT);
        myMouseRotate.setTransformGroup(input_TG);
        myMouseRotate.setSchedulingBounds(mouseBounds);
        objRoot.addChild(myMouseRotate);

        MouseTranslate myMouseTranslate = new MouseTranslate(MouseBehavior.INVERT_INPUT);
        myMouseTranslate.setTransformGroup(input_TG);
        myMouseTranslate.setSchedulingBounds(mouseBounds);
        myMouseTranslate.setFactor(100.0);
        objRoot.addChild(myMouseTranslate);

        MouseZoom myMouseZoom = new MouseZoom(MouseBehavior.INVERT_INPUT);
        myMouseZoom.setTransformGroup(input_TG);
        myMouseZoom.setSchedulingBounds(mouseBounds);
        myMouseZoom.setFactor(100.0);
        objRoot.addChild(myMouseZoom);
	}
	
	private void setKeyboardControls(BranchGroup objRoot, TransformGroup input_TG)
	{
        KeyNavigatorBehavior keyNavBeh = new KeyNavigatorBehavior(input_TG);
        keyNavBeh.setSchedulingBounds(new BoundingSphere(new Point3d(),bounding_sphere_size));
        objRoot.addChild(keyNavBeh);
	}
	
	public void changeViewType(int type)
	{
		PolygonAttributes polyAttrib = new PolygonAttributes();
	    polyAttrib.setPolygonMode(type);
	    _triangles_drawer._appearance.setPolygonAttributes(polyAttrib);
	}
	
	public Graphics3DEngine()
	{
		setLayout(new BorderLayout());
		GraphicsConfiguration config =
	           SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas3D = new Canvas3D(config);
        add("Center", canvas3D);
        
        _simpleU = new SimpleUniverse(canvas3D);
        _simpleU.getViewer().getView().setBackClipDistance(viewer_max_distance);
        _simpleU.getViewer().getView().setSceneAntialiasingEnable(true);
        
        createSceneGraph(null);
        //_simpleU.addBranchGraph(createSceneGraph());
	}

}
