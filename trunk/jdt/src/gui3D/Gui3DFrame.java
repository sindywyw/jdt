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

public class Gui3DFrame implements ActionListener {
	
	Frame _frame = null;
	Graphics3DEngine _g_engine = null;
	
	public Gui3DFrame(Frame frame, Graphics3DEngine engine) {
		_frame = frame;
		_g_engine = engine;
		
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
		MenuItem m1 = new MenuItem("Open");
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
		
		_frame.setMenuBar(mbar);
	}
	
	public void actionPerformed(ActionEvent evt) 
	{
		String arg = evt.getActionCommand();
		if (arg.equals("Open"))
		{
			openCoordinatesFile();
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
			
	}
	
	private void openCoordinatesFile()
	{
		FileDialog d = new FileDialog(_frame, "Open text file", FileDialog.LOAD);
		d.show();
		String dr = d.getDirectory();
		String fi = d.getFile();
		_g_engine.setNewDelaunayTriangulation(Gui3DMain.read3DPointsFromFile(dr+fi));
	}

}
