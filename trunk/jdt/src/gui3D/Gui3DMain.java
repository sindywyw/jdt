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

public class Gui3DMain extends Applet{
	
	Graphics3DEngine _g_engine = null;
	Gui3DFrame _frame = null;
	
	static public Delaunay_Triangulation read3DPointsFromFile(String file_name)
	{
		Delaunay_Triangulation delunay_triangluation = null;
		
		try 
		{
			delunay_triangluation = new Delaunay_Triangulation(file_name);
		} 
		catch (Exception e) 
		{
			System.out.println("caught exception in read3DPointsFromFile!!");
			e.printStackTrace();
		}
		return delunay_triangluation;		
	}
	
	public Gui3DMain() {
		Delaunay_Triangulation delaunay = null;
		_g_engine = new Graphics3DEngine();
		_frame = new Gui3DFrame(new MainFrame(_g_engine, 512, 512), _g_engine);
    }
	
	public static void main(String[] args) 
	{
		Gui3DMain gui = new Gui3DMain();	
    }
}
