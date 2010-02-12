package gui3D;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.util.Iterator;
import com.sun.j3d.utils.behaviors.keyboard.*;
import com.sun.j3d.utils.behaviors.mouse.*;

import javax.media.j3d.*;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.universe.*;
import javax.vecmath.*;

import com.sun.j3d.utils.applet.*;
import com.sun.org.apache.bcel.internal.generic.DALOAD;

import delaunay_triangulation.*;
import delaunay_triangulation.BoundingBox;

/*
 * Main class used to run the GUI3d
 */
public class Gui3DMain extends Applet{
	
	public Gui3DMain() {
		Delaunay_Triangulation triangulation = new Delaunay_Triangulation();
		
		Graphics3DEngine g_engine = new Graphics3DEngine(triangulation);
		Gui3DFrame frame = new Gui3DFrame(new MainFrame(g_engine, 512, 512), g_engine, triangulation);
		g_engine.set_gui_frame(frame);
    }
	
	public static void main(String[] args) 
	{
		Gui3DMain gui = new Gui3DMain();	
    }
}
