package gui3D;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.FileDialog;
import java.util.Iterator;

import javax.swing.JOptionPane;

import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;

public class Gui3DFrame implements ActionListener {
	
	private Frame _frame = null;
	private Graphics3DEngine _g_engine = null;
	private Delaunay_Triangulation _current_dt = null;
	private boolean _in_add_point_tool = false;
	private boolean _in_delete_point_tool = false;
	
	public Gui3DFrame(Frame frame, Graphics3DEngine engine, Delaunay_Triangulation dt) {
		_frame = frame;
		_g_engine = engine;
		
		_current_dt = dt;
		
		_frame.setTitle("Delaunay 3D GUI tester");
		_frame.setSize(512, 512);
		
		CreateDialog();
		
		_frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
	
	private void CreateDialog()
	{
		MenuBar mbar = new MenuBar();
		
		Menu menu_inst = new Menu("File");
		MenuItem m1 = new MenuItem("New");
		m1.addActionListener(this);
		menu_inst.add(m1);
		m1 = new MenuItem("Open");
		m1.addActionListener(this);
		menu_inst.add(m1);
		m1 = new MenuItem("Save smf");
		m1.addActionListener(this);
		menu_inst.add(m1);
		m1 = new MenuItem("Save tsin");
		m1.addActionListener(this);
		menu_inst.add(m1);
		mbar.add(menu_inst);
		
		menu_inst = new Menu("View");
		m1 = new MenuItem("Surface");
		m1.addActionListener(this);
		menu_inst.add(m1);
		m1 = new MenuItem("Grid");
		m1.addActionListener(this);
		menu_inst.add(m1);
		m1 = new MenuItem("Points");
		m1.addActionListener(this);
		menu_inst.add(m1);
		mbar.add(menu_inst);
		
		menu_inst = new Menu("Tools");
		m1 = new MenuItem("Add Point");
		m1.addActionListener(this);
		menu_inst.add(m1);
		m1 = new MenuItem("Delete Point");
		m1.addActionListener(this);
		menu_inst.add(m1);
		m1 = new MenuItem("Scale Z");
		m1.addActionListener(this);
		menu_inst.add(m1);
		mbar.add(menu_inst);
		
		menu_inst = new Menu("Help");
		m1 = new MenuItem("Help");
		m1.addActionListener(this);
		menu_inst.add(m1);
		mbar.add(menu_inst);
		
		_frame.setMenuBar(mbar);
	}
	
	public void point_picked_callback(Point_dt point_picked)
	{
		if (_in_delete_point_tool == true)
		{
			_in_add_point_tool = false;
			_in_delete_point_tool = false;
			
			_current_dt.deletePoint(new Point_dt((int)point_picked.x(), (int)point_picked.y(), (int)point_picked.z()));
			_g_engine.setNewDelaunayTriangulation(_current_dt, false, false);
		}		
	}
	
	public void mouse_position_callback(Point_dt pdt)
	{
		if (_in_add_point_tool == true)
		{
			_in_delete_point_tool = false;
			_in_add_point_tool = false;
			
			String response = JOptionPane.showInputDialog(null,
					  "Enter the point's Z value: (lowest Z value is "+(int)pdt.z()+")",
					  "Point height",
					  JOptionPane.QUESTION_MESSAGE);
			
			if (response == null)
			{
				return;
			}
			
			Point_dt point_to_add = new Point_dt((int)pdt.x(), (int)pdt.y(), (int)(Double.parseDouble(response))); 
			
			_current_dt.insertPoint(point_to_add);
			_g_engine.setNewDelaunayTriangulation(_current_dt, false, false);
		}
	}
	
	public void actionPerformed(ActionEvent evt) 
	{
		String arg = evt.getActionCommand();
		if (arg.equals("New"))
		{
			clearTriangulation();
		}
		else if (arg.equals("Open"))
		{
			openCoordinatesFile();
		}
		else if (arg.equals("Save tsin"))
		{
			saveCoordinatesFile_tsin();
		}
		else if (arg.equals("Save smf"))
		{
			saveCoordinatesFile_smf();
		}
		else if (arg.equals("Surface"))
		{
			_g_engine.changeViewType(Graphics3DEngine.VIEW_TYPE_SURFACE);
		}
		else if (arg.equals("Grid"))
		{
			_g_engine.changeViewType(Graphics3DEngine.VIEW_TYPE_GRID);
		}
		else if (arg.equals("Points"))
		{
			_g_engine.changeViewType(Graphics3DEngine.VIEW_TYPE_POINTS);
		}
		else if (arg.equals("Delete Point"))
		{
			_in_add_point_tool = false;
			_in_delete_point_tool = true;
			_g_engine.make_points_visible_and_pickable();
		}
		else if (arg.equals("Add Point"))
		{
			_in_delete_point_tool = false;
			_in_add_point_tool = true;
		}
		else if (arg.equals("Scale Z"))
		{
			String response = JOptionPane.showInputDialog(null,
					  "Enter Z scale:",
					  "Height scaling",
					  JOptionPane.QUESTION_MESSAGE);
			
			if (response == null)
			{
				return;
			}
			float scale = 1.0f;
			try
			{
				scale = Float.parseFloat(response);
				scaleZ(scale);
			}
			catch(Exception e) {}
		}
		else if (arg.equals("Help"))
		{
			showInfo();
		}
	}
	
	private void showInfo()
	{
		String help_info = "Use the mouse and keyboard buttons to navigate around\n"+
							"Mouse controls: left click to rotate, right click to translate, middle mouse button to zoom in and out\n"+
							"Keyboard controls: use the arrow keys to navigate, page-up and down to look up or down\n"+
							"*********************************************************************************************************\n"+
							"Use the add/delete point tools to dynamically add points to the triangulation\n"+
							"*********************************************************************************************************\n"+
							"Asaf & Tzach @ IDC JAN 2010";
		JOptionPane.showMessageDialog(null, help_info, "Help", JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void scaleZ(float scale) throws Exception
	{
		Iterator<Point_dt> point_iter = _current_dt.verticesIterator();
		Point_dt scaled_points_arr[] = new Point_dt[_current_dt.size()];
		for (int i=0; i<_current_dt.size(); ++i)
		{
			if (point_iter.hasNext() == false)
			{
				throw new Exception();
			}
			Point_dt curr_p = point_iter.next();
			scaled_points_arr[i] = new Point_dt(curr_p.x(), curr_p.y(), scale*curr_p.z());
		}
		_current_dt = new Delaunay_Triangulation(scaled_points_arr);
		_g_engine.setNewDelaunayTriangulation(_current_dt, false, false);
	}
	
	public void saveCoordinatesFile_tsin()
	{
		FileDialog d = new FileDialog(_frame, "Saving TSIN text file",
										FileDialog.SAVE);
		d.show();
		String dr = d.getDirectory();
		String fi = d.getFile();
		if (fi != null) 
		{
			try 
			{
				_current_dt.write_tsin(dr + fi);
			} 
			catch (Exception e) 
			{
				System.out.println("ERR cant save to text file: " + dr + fi);
				e.printStackTrace();
			}
		}
	}
	
	public void saveCoordinatesFile_smf()
	{
		FileDialog d = new FileDialog(_frame, "Saving SMF text file",
				FileDialog.SAVE);
		d.show();
		String dr = d.getDirectory();
		String fi = d.getFile();
		if (fi != null) 
		{
		try 
		{
		_current_dt.write_smf(dr + fi);
		} 
		catch (Exception e) 
		{
		System.out.println("ERR cant save to text file: " + dr + fi);
		e.printStackTrace();
		}
}
	}
	
	public Delaunay_Triangulation getTriangulation()
	{
		return _current_dt;
	}
	
	private void clearTriangulation()
	{
		_current_dt = new Delaunay_Triangulation();
		_g_engine.setNewDelaunayTriangulation(_current_dt, true, true);
	}
	
	private void openCoordinatesFile()
	{
		FileDialog d = new FileDialog(_frame, "Open text file", FileDialog.LOAD);
		d.show();
		String dr = d.getDirectory();
		String fi = d.getFile();
		if ((dr!=null)&&(fi!=null))
		{
			String file_name = dr+fi;
			try {
				_current_dt = new Delaunay_Triangulation(file_name);
				_g_engine.setNewDelaunayTriangulation(_current_dt, true, true);
			}
			catch (Exception e)
			{
				System.err.println("failed to open input coordinated file!!");
				e.printStackTrace();
			}
			
		}	
	}

}
