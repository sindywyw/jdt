package gui3D;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.security.AllPermission;
import java.util.Enumeration;

import javax.media.j3d.Alpha;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Group;
import javax.media.j3d.LineArray;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.PickPoint;
import javax.media.j3d.PickRay;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.SceneGraphPath;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.behaviors.picking.Intersect;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.universe.SimpleUniverse;

import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;

/*
 * This is the main Graphics engine for the Delaunay Triangulation 3DGUI
 */
public class Graphics3DEngine extends Applet implements MousePosListener{
	
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
	
	private TrianglesArr3DObject _triangles_drawer = null;
	private Points3DSphereObject _points_drawer = null;
	
	private SimpleUniverse _simpleU = null;
	private BranchGroup _objRoot = null;
	
	private Canvas3D _canvas3D = null;
	
	private Gui3DFrame _gui_frame = null;
	
	private delaunay_triangulation.BoundingBox _bound_box = null;
	
	private int _view_mode = VIEW_TYPE_SURFACE;
	
	/*
	 * The function creates a green square land, its size is determined according to the input
	 * Triangulation 
	 */
	Shape3D createLand(delaunay_triangulation.BoundingBox bound_box, boolean should_reconfigure_land)
    { 	
		// check if the old land should be used
		if (should_reconfigure_land == false)
		{
			bound_box = _bound_box;
		}
		else
		{
			_bound_box = bound_box;
		}
		// calc square land limits, use the bounding box from the triangulation
    	int center_x = (int)bound_box.getMinPoint().x() + (int)((bound_box.getMaxPoint().x() - bound_box.getMinPoint().x()) / 2);
    	int center_y = (int)bound_box.getMinPoint().y() + (int)((bound_box.getMaxPoint().y() - bound_box.getMinPoint().y()) / 2);
    	int min_z = (int)bound_box.getMinPoint().z();
    	
        
        Color3f col_green = new Color3f(0.1f, 0.8f, 0.1f);
        
        int grid_size =  4 * Math.max(	(int)(bound_box.getMaxPoint().x() - bound_box.getMinPoint().x()),
        					(int)(bound_box.getMaxPoint().y() - bound_box.getMinPoint().y()) );
        int grid_spacing = (int) (0.05 * grid_size);
        
        // The square surface is created as an array of green lines
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

        Shape3D ret_shape = new Shape3D(landGeom);
        ret_shape.setPickable(false);
        return ret_shape;
    }
	
	/*
	 * Main function for creating the scene graph
	 */
	private void createSceneGraph(Delaunay_Triangulation delaunay, boolean should_change_camera, boolean should_reconfigure_land) 
	{
	        delaunay_triangulation.BoundingBox dt_bounding_box = null;
	        
	        //use a default bounding box in case the triangulation is empty
	        if (delaunay.size() >= 3)
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
	        	_objRoot = null;
	        	_points_drawer = null;
	        	_triangles_drawer = null;
	        }
	        
	        _objRoot = new BranchGroup();
	        _objRoot.setCapability(BranchGroup.ALLOW_DETACH);
	        _objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
	        _objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
	        _objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
	        _objRoot.setCapability(BranchGroup.ENABLE_PICK_REPORTING);
	        

	        _objRoot.addChild(createLand(dt_bounding_box, should_change_camera));

	        TransformGroup vpTrans = null;
	        vpTrans = _simpleU.getViewingPlatform().getViewPlatformTransform();
	        setMouseControls(_objRoot, vpTrans);
	        setKeyboardControls(_objRoot, vpTrans);
	        
	        if (should_change_camera == true)
	        	setCameraPosition(dt_bounding_box, vpTrans);
	        
	        // draw the triangles and the hidden point spheres (used for selection)
	        if (delaunay.size() >= 3)
	        {
	        	_triangles_drawer = new TrianglesArr3DObject(delaunay.trianglesIterator(), delaunay.trianglesSize(), delaunay.getBoundingBox());
	        	_triangles_drawer.setPickable(false);
	        	_objRoot.addChild(_triangles_drawer);
	        	changeViewType(_view_mode);
	        	
	        	_points_drawer = new Points3DSphereObject(this, delaunay.verticesIterator(), delaunay.size(), delaunay.getBoundingBox());
	        	_objRoot.addChild(_points_drawer.getSpheresBG());
	        	setMousePickBehavior(_objRoot, _points_drawer);
	        }
	        else
	        {
	        	_triangles_drawer = null;
	        	_points_drawer = null;
	        	setMousePickBehavior(_objRoot, null);
	        }
	        
		// Let Java 3D perform optimizations on this scene graph.
	        _objRoot.compile();

	        _simpleU.addBranchGraph(_objRoot);
	    }
	
	
	/*
	 * This is a callback function used to monitor the position of the mouse,
	 * The function calculates the intersection between a ray emerging from the image plate
	 * to the surface of the triangulation
	 */
	public void notify_mouse_position(Point3d p3d, Vector3d v3d, PickRay pick_ray)
	{	
		int min_z = (int)_bound_box.getMinPoint().z();
		Intersect intersector = new Intersect();
    	Point3d arr[] = new Point3d[4];
    	arr[0] = new Point3d(-10000,0,min_z);
    	arr[1] = new Point3d(1000,0,min_z);
    	arr[2] = new Point3d(0,1000,min_z);
    	arr[3] = new Point3d(0,-1000,min_z);
    	
    	double dist[] = new double[4];
    	intersector.rayAndQuad(pick_ray, arr, 0, dist);
    	Point_dt ground_p3d = new Point_dt((v3d.x) * dist[0] + p3d.x, (v3d.y) * dist[0] + p3d.y, (v3d.z) * dist[0] + p3d.z);	
    	
		_gui_frame.mouse_position_callback(ground_p3d);
	}
	
	/*
	 * A callback function used to select scene graph objects
	 */
	public void point_picked_callback(Point3d point_picked)
	{
		_gui_frame.point_picked_callback(new Point_dt(point_picked.x, point_picked.y ,point_picked.z));
	}
	
	
	public void setNewDelaunayTriangulation(Delaunay_Triangulation dt, boolean should_change_camera, boolean should_reconfigure_land)
	{
		createSceneGraph(dt, should_change_camera, should_reconfigure_land);
	}

	/*
	 * Set Virtual world camera position
	 */
	private void setCameraPosition(delaunay_triangulation.BoundingBox bound_box, TransformGroup input_TG)
	{
		int height_supp = 100;
		int center_x = (int)bound_box.getMinPoint().x() + (int)((bound_box.getMaxPoint().x() - bound_box.getMinPoint().x()) / 2);
    	int center_y = (int)bound_box.getMinPoint().y() + (int)((bound_box.getMaxPoint().y() - bound_box.getMinPoint().y()) / 2);
    	int max_z = (int)bound_box.getMaxPoint().z();
    	
		Vector3f translate = new Vector3f();
        Transform3D T3D = new Transform3D();
        translate.set( (float)center_x, (float)center_y, (float)4*max_z+height_supp);
        System.out.println("DEBUG INFO camera position: "+center_x+" "+center_y+" "+(4*max_z+height_supp));
        T3D.setTranslation(translate);
        input_TG.setTransform(T3D);
	}
	
	/*
	 * add mouse pick behavior - used to select points for deletion
	 */
	private void setMousePickBehavior(BranchGroup objRoot, GraphNodePicker picker_callback)
	{
		MousePick3DObjectBehavior pick_behvior = new MousePick3DObjectBehavior(_canvas3D, objRoot, picker_callback, this);
        pick_behvior.setSchedulingBounds(new BoundingSphere(new Point3d(),bounding_sphere_size));
        objRoot.addChild(pick_behvior);
	}
	
	/*
	 * add mouse controls
	 */
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
	
	/*
	 * add keyboard controls
	 */
	private void setKeyboardControls(BranchGroup objRoot, TransformGroup input_TG)
	{
        KeyNavigatorBehavior keyNavBeh = new KeyNavigatorBehavior(input_TG);
        keyNavBeh.setSchedulingBounds(new BoundingSphere(new Point3d(),bounding_sphere_size));
        objRoot.addChild(keyNavBeh);
	}
	
	public void changeViewType(int type)
	{
		_view_mode = type;
		PolygonAttributes polyAttrib = new PolygonAttributes();
	    polyAttrib.setPolygonMode(type);
	    if (_triangles_drawer != null)
	    {
	    	_triangles_drawer._appearance.setPolygonAttributes(polyAttrib);
	    }
	}
	
	public void set_gui_frame(Gui3DFrame gui_frame)
	{
		_gui_frame = gui_frame;
	}
	
	public void make_points_visible_and_pickable()
	{
		if (_points_drawer != null)
			_points_drawer.make_points_visible_and_pickable();
	}
	
	public Graphics3DEngine(Delaunay_Triangulation dt)
	{
		setLayout(new BorderLayout());
		GraphicsConfiguration config =
	           SimpleUniverse.getPreferredConfiguration();
		_canvas3D = new Canvas3D(config);
        add("Center", _canvas3D);
        
        _simpleU = new SimpleUniverse(_canvas3D);
        _simpleU.getViewer().getView().setBackClipDistance(viewer_max_distance);
        _simpleU.getViewer().getView().setSceneAntialiasingEnable(true);
		
        createSceneGraph(dt, true, true);
	}

}
