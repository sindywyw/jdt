package gui;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Vector;

import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;
import delaunay_triangulation.Triangle_dt;

/**
 * GUI class to test the delaunay_triangulation Triangulation package:
 */

class MyFrame extends Frame implements ActionListener {

	public static void main(String[] args) {
		MyFrame win = new MyFrame();
		win.start();
	}

	private static final long serialVersionUID = 1L;
	// *** private data ***
	public static final int POINT = 1, FIND = 2, VIEW1 = 3, VIEW2 = 4,
			VIEW3 = 5, VIEW4 = 6, SECTION1 = 7, SECTION2 = 8, GUARD = 9,
			CLIENT = 10;
	private int _stage, _view_flag = VIEW1, _mc = 0;
	private Triangle_dt _t1, _t2; // tmp triangle for find testing for selection
	private Delaunay_Triangulation _ajd = null;
	protected Vector<Point_dt> _clients, _guards;
	protected Point_dt _dx_f, _dy_f, _dx_map, _dy_map, _p1, _p2;// ,_guard=null,
																// _client=null;
	protected boolean _visible = false;
	private double _topo_dz = 100.0, GH = 30, CH = 5;
	// private Vector<Triangle_dt> _tr = null;//new Vector<Triangle_dt>();
	private Visibility _los;// , _section2;

	// *** text area ***
	public MyFrame() {
		this.setTitle("Delaunay GUI tester");
		this.setSize(500, 500);
		_stage = 0;
		_ajd = new Delaunay_Triangulation();

		_dx_f = new Point_dt(10, this.getWidth() - 10);
		_dy_f = new Point_dt(55, this.getHeight() - 10);
		_dx_map = new Point_dt(_dx_f);
		_dy_map = new Point_dt(_dy_f);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	public MyFrame(Delaunay_Triangulation aj) {
		this.setTitle("ajDelaunay GUI tester");
		this.setSize(500, 500);
		_stage = 0;
		_ajd = aj;
		_dx_f = new Point_dt(10, this.getWidth() - 10);
		_dy_f = new Point_dt(55, this.getHeight() - 10);
		_dx_map = new Point_dt(aj.maxBoundingBox().x(), aj.minBoundingBox().x());
		_dy_map = new Point_dt(aj.maxBoundingBox().y(), aj.minBoundingBox().y());
		_clients = null;
		_guards = null;
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	public void paint(Graphics g) {
		// _ajd.initTriangle();
		// ajTriangle[] tt = _ajd._triangles;
		if (_ajd == null || _ajd.size() == 0)
			return;
		_dx_f = new Point_dt(10, this.getWidth() - 10);
		_dy_f = new Point_dt(55, this.getHeight() - 10);

		Triangle_dt curr = null;
		Iterator<Triangle_dt> it = _ajd.trianglesIterator();
		while (it.hasNext()) {
			curr = it.next();
			if (!curr.isHalfplane())
				drawTriangle(g, curr, null);
		}
		it = _ajd.trianglesIterator();
		while (it.hasNext()) {
			curr = it.next();
			if (curr.isHalfplane())
				drawTriangle(g, curr, null);
		}
		if (_t2 != null)
			drawTriangle(g, _t2, Color.red);
		if (_t1 != null && _stage == FIND)
			drawTriangle(g, _t1, Color.green);
		if (this._view_flag == VIEW3)
			drawTopo(g);

		// debug
		if (_mc < _ajd.getModeCounter()) {
			_mc = _ajd.getModeCounter();
			int i = 0;
			for (Iterator<Triangle_dt> it2 = _ajd.getLastUpdatedTriangles(); it2
					.hasNext();) {
				i++;
				drawTriangle(g, it2.next(), Color.CYAN);
			}
			System.out.println("   MC: " + _mc
					+ "  number of triangles updated: " + i);

		}

		if (_los != null && (_stage == SECTION1 | _stage == SECTION2)) {
			if (_los != null && _los._tr != null) {
				it = _los._tr.iterator();
				while (it.hasNext()) {
					curr = it.next();
					if (!curr.isHalfplane())
						drawTriangle(g, curr, Color.RED);
				}
			}
			Iterator<Point_dt> pit = _los._section.iterator();
			int i = 0;
			while (pit.hasNext()) {
				Point_dt curr_p = pit.next();
				if (curr_p != null) {
					drawPoint(g, curr_p, Color.BLUE);
					System.out.println(i + ") " + curr_p + "  dist _p1: "
							+ _p1.distance(curr_p));
					i++;
				}
			}
			drawLine(g, _p1, _p2);
		}
		/*
		 * if(_stage == GUARD | _stage == CLIENT) { if(_p1!=null)
		 * drawPoint(g,_p1,6,Color.ORANGE); if(_p2!=null) { if(_visible)
		 * drawPoint(g,_p2,6,Color.BLUE); else drawPoint(g,_p2,6, Color.RED); }
		 * }
		 */
		if (_los == null)
			_los = new Visibility(_ajd);
		if (_stage == GUARD | _stage == CLIENT) {
			int[] ccc = new int[0];
			if (_clients != null)
				ccc = new int[_clients.size()];
			for (int gr = 0; _guards != null && gr < _guards.size(); gr++) {
				Point_dt gg = _guards.elementAt(gr);
				drawPoint(g, gg, 8, Color.ORANGE);

				for (int c = 0; _clients != null && c < _clients.size(); c++) {
					Point_dt cc = _clients.elementAt(c);
					drawPoint(g, cc, 6, Color.white);
					// Color cl = Color.RED;
					if (_los.los(gg, cc)) {
						this.drawLine(g, gg, cc);
						ccc[c]++;
					}
				}
			}
			int c1 = 0, c2 = 0, c3 = 0;
			for (int i = 0; i < ccc.length; i++) {
				if (ccc[i] > 0) {
					c1++;
					c2 += ccc[i];
				}
			}
			if (c1 > 0)
				System.out.println("clients:" + ccc.length + "  visible c:"
						+ c1 + "   ave:" + c2 / c1);
		}

	}

	void drawTopo(Graphics g) {
		Triangle_dt curr = null;
		Iterator<Triangle_dt> it = _ajd.trianglesIterator();
		g.setColor(Color.red);
		while (it.hasNext()) {
			curr = it.next();
			if (!curr.isHalfplane())
				drawTriangleTopoLines(g, curr, this._topo_dz, null);
		}
	}

	void drawTriangleTopoLines(Graphics g, Triangle_dt t, double dz, Color cl) {
		if (t.p1().z() < 0 | t.p2().z() < 0 | t.p3().z() < 0)
			return;
		Point_dt[] p12 = computePoints(t.p1(), t.p2(), dz);
		Point_dt[] p23 = computePoints(t.p2(), t.p3(), dz);
		Point_dt[] p31 = computePoints(t.p3(), t.p1(), dz);

		int i12 = 0, i23 = 0, i31 = 0;
		boolean cont = true;
		while (cont) {
			cont = false;
			if (i12 < p12.length && i23 < p23.length
					&& p12[i12].z() == p23[i23].z()) {
				g.setColor(Color.YELLOW);
				if (p12[i12].z() % 200 > 100)
					g.setColor(Color.red);
				drawLine(g, p12[i12], p23[i23]);
				i12++;
				i23++;
				cont = true;
			}
			if (i23 < p23.length && i31 < p31.length
					&& p23[i23].z() == p31[i31].z()) {
				g.setColor(Color.YELLOW);
				if (p23[i23].z() % 200 > 100)
					g.setColor(Color.red);
				drawLine(g, p23[i23], p31[i31]);
				i23++;
				i31++;
				cont = true;
			}
			if (i12 < p12.length && i31 < p31.length
					&& p12[i12].z() == p31[i31].z()) {
				g.setColor(Color.YELLOW);
				if (p12[i12].z() % 200 > 100)
					g.setColor(Color.red);
				drawLine(g, p12[i12], p31[i31]);
				i12++;
				i31++;
				cont = true;
			}
		}
	}

	Point_dt[] computePoints(Point_dt p1, Point_dt p2, double dz) {
		Point_dt[] ans = new Point_dt[0];
		double z1 = Math.min(p1.z(), p2.z()), z2 = Math.max(p1.z(), p2.z());
		if (z1 == z2)
			return ans;
		double zz1 = ((int) (z1 / dz)) * dz;
		if (zz1 < z1)
			zz1 += dz;
		double zz2 = ((int) (z2 / dz)) * dz;
		int len = (int) ((zz2 - zz1) / dz) + 1, i = 0;
		ans = new Point_dt[len];
		double DZ = p2.z() - p1.z(), DX = p2.x() - p1.x(), DY = p2.y() - p1.y();
		for (double z = zz1; z <= zz2; z += dz) {
			double scale = (z - p1.z()) / DZ;
			double x = p1.x() + DX * scale;
			double y = p1.y() + DY * scale;
			ans[i] = new Point_dt(x, y, z);
			i++;
		}
		return ans;
	}

	public void drawTriangle(Graphics g, Triangle_dt t, Color cl) {
		if (_view_flag == VIEW1 | t.isHalfplane()) {
			if (cl != null)
				g.setColor(cl);
			if (t.isHalfplane()) {
				if (cl == null)
					g.setColor(Color.blue);
				drawLine(g, t.p1(), t.p2());
			} else {
				if (cl == null)
					g.setColor(Color.black);
				drawLine(g, t.p1(), t.p2());
				drawLine(g, t.p2(), t.p3());
				drawLine(g, t.p3(), t.p1());
			}
		} else {
			// //////////////////////////////////////////////////////////////////
			double maxZ = _ajd.maxBoundingBox().z();
			double minZ = _ajd.minBoundingBox().z();
			double z = (t.p1().z() + t.p2().z() + t.p3().z()) / 3.0;
			double dz = maxZ - minZ;
			int co = 30 + (int) (220 * ((z - minZ) / dz));
			if (cl == null)
				cl = new Color(co, co, co);
			g.setColor(cl);
			int[] xx = new int[3], yy = new int[3];
			// double f = 0;
			// double dx_map = _dx_map.y()- _dx_map.x();
			// double dy_map = _dy_map.y()- _dy_map.x();

			// f = (t.p1().x() -_dx_map.x())/dx_map;
			Point_dt p1 = world2screen(t.p1());
			xx[0] = (int) p1.x();
			yy[0] = (int) p1.y();
			Point_dt p2 = world2screen(t.p2());
			xx[1] = (int) p2.x();
			yy[1] = (int) p2.y();
			Point_dt p3 = world2screen(t.p3());
			xx[2] = (int) p3.x();
			yy[2] = (int) p3.y();

			g.fillPolygon(xx, yy, 3);

			// ////////////////////////////////////
		}
	}

	public void drawLine(Graphics g, Point_dt p1, Point_dt p2) {
		// g.drawLine((int)p1.x(), (int)p1.y(), (int)p2.x(), (int)p2.y());
		Point_dt t1 = this.world2screen(p1);
		Point_dt t2 = this.world2screen(p2);
		g.drawLine((int) t1.x(), (int) t1.y(), (int) t2.x(), (int) t2.y());
	}

	public void drawPoint(Graphics g, Point_dt p1, Color cl) {
		drawPoint(g, p1, 4, cl);
	}

	public void drawPoint(Graphics g, Point_dt p1, int r, Color cl) {
		// g.drawLine((int)p1.x(), (int)p1.y(), (int)p2.x(), (int)p2.y());
		Point_dt t1 = this.world2screen(p1);
		g.setColor(cl);
		g.fillOval((int) t1.x() - r / 2, (int) t1.y() - r / 2, r, r);
	}

	public void start() {
		this.show();
		Dialog();
	}

	public void Dialog() {
		MenuBar mbar = new MenuBar();

		Menu m = new Menu("File");
		MenuItem m1;
		m1 = new MenuItem("Open");
		m1.addActionListener(this);
		m.add(m1);
		m1 = new MenuItem("Save tsin");
		m1.addActionListener(this);
		m.add(m1);
		m1 = new MenuItem("Save smf");
		m1.addActionListener(this);
		m.add(m1);

		MenuItem m6 = new MenuItem("Clear");
		m6.addActionListener(this);
		m.add(m6);

		MenuItem m2 = new MenuItem("Exit");
		m2.addActionListener(this);
		m.add(m2);
		mbar.add(m);

		m = new Menu("Input");
		MenuItem m3 = new MenuItem("Point");
		m3.addActionListener(this);
		m.add(m3);
		m3 = new MenuItem("100-rand-ps");
		m3.addActionListener(this);
		m.add(m3);
		m3 = new MenuItem("Guard-30m");
		m3.addActionListener(this);
		m.add(m3);
		m3 = new MenuItem("Client-5m");
		m3.addActionListener(this);
		m.add(m3);

		mbar.add(m);

		m = new Menu("View");
		m3 = new MenuItem("Lines");
		m3.addActionListener(this);
		m.add(m3);
		m3 = new MenuItem("Triangles");
		m3.addActionListener(this);
		m.add(m3);
		m3 = new MenuItem("Topo");
		m3.addActionListener(this);
		m.add(m3);
		MenuItem m4 = new MenuItem("Find");
		m4.addActionListener(this);
		m.add(m4);
		m4 = new MenuItem("Section");
		m4.addActionListener(this);
		m.add(m4);
		m4 = new MenuItem("Info");
		m4.addActionListener(this);
		m.add(m4);
		m4 = new MenuItem("CH");
		m4.addActionListener(this);
		m.add(m4);
		mbar.add(m);

		setMenuBar(mbar);
		this.addMouseListener(new mouseManeger());
	}

	public void actionPerformed(ActionEvent evt) {
		String arg = evt.getActionCommand();
		if (arg.equals("Open"))
			openTextFile();
		else if (arg.equals("Save tsin"))
			saveTextFile();
		else if (arg.equals("Save smf"))
			saveTextFile2();
		else if (arg.equals("Lines")) {
			this._view_flag = VIEW1;
			repaint();
		} else if (arg.equals("Triangles")) {
			this._view_flag = VIEW2;
			repaint();
		} else if (arg.equals("Topo")) {
			this._view_flag = VIEW3;
			repaint();
		} else if (arg.equals("Clear")) {
			_ajd = new Delaunay_Triangulation();
			_dx_map = new Point_dt(_dx_f);
			_dy_map = new Point_dt(_dy_f);
			_clients = null;
			_guards = null;
			_mc = 0;
			repaint();
		} else if (arg.equals("Exit")) {
			System.exit(209);
		}

		else if (arg.equals("Point")) {
			_stage = POINT;
		} else if (arg.equals("CH")) {
			_ajd.CH_vertices_Iterator();
		} else if (arg.equals("100-rand-ps")) {
			double x0 = 10, y0 = 60, dx = this.getWidth() - x0 - 10, dy = this
					.getHeight()
					- y0 - 10;
			for (int i = 0; i < 100; i++) {
				double x = Math.random() * dx + x0;
				double y = Math.random() * dy + y0;
				Point_dt q = new Point_dt(x, y);
				Point_dt p = screen2world(q);
				_ajd.insertPoint(p);
			}
			repaint();
		} else if (arg.equals("Find")) {
			_stage = FIND;
		} else if (arg.equals("Section")) {
			_stage = SECTION1;
		} else if (arg.equals("Client-5m")) {
			// System.out.println("CL!");
			_stage = this.CLIENT;

		} else if (arg.equals("Guard-30m")) {// System.out.println("GR!");
			_stage = this.GUARD;
		} else if (arg.equals("Info")) {
			String ans = "" + _ajd.getClass().getCanonicalName()
					+ "  # vertices:" + _ajd.size() + "  # triangles:"
					+ _ajd.trianglesSize();
			ans += "   min BB:" + _ajd.minBoundingBox() + "   max BB:"
					+ _ajd.maxBoundingBox();
			System.out.println(ans);
			System.out.println();
		}

	}

	// *** private methodes - random points obs ****

	// ********** Private methodes (open,save...) ********

	private void openTextFile() {
		_stage = 0;
		FileDialog d = new FileDialog(this, "Open text file", FileDialog.LOAD);
		d.show();
		String dr = d.getDirectory();
		String fi = d.getFile();
		_clients = null;
		_guards = null;
		if (fi != null) { // the user actualy choose a file.
			try {
				_ajd = new Delaunay_Triangulation(dr + fi);
				_dx_map = new Point_dt(_ajd.minBoundingBox().x(), _ajd
						.maxBoundingBox().x());
				_dy_map = new Point_dt(_ajd.minBoundingBox().y(), _ajd
						.maxBoundingBox().y());
				repaint();
			} catch (Exception e) { // in case something went wrong.
				System.out.println("** Error while reading text file **");
				System.out.println(e);
			}

		}
	}

	private void saveTextFile() {
		_stage = 0;
		FileDialog d = new FileDialog(this, "Saving TSIN text file",
				FileDialog.SAVE);
		d.show();
		String dr = d.getDirectory();
		String fi = d.getFile();
		if (fi != null) {
			try {
				// _ajd.write_tsin2(dr+fi);
				// _ajd.write_CH(dr+"CH_"+fi);
				_ajd.write_tsin(dr + fi);
			} catch (Exception e) {
				System.out.println("ERR cant save to text file: " + dr + fi);
				e.printStackTrace();
			}
		}
	}

	public void saveTextFile2() {
		_stage = 0;
		FileDialog d = new FileDialog(this, "Saving SMF text file",
				FileDialog.SAVE);
		d.show();
		String dr = d.getDirectory();
		String fi = d.getFile();
		if (fi != null) {
			try {
				_ajd.write_smf(dr + fi);
			} catch (Exception e) {
				System.out.println("ERR cant save to text file: " + dr + fi);
				e.printStackTrace();
			}
		}
	}

	// ***** inner classes (mouse maneger) *****
	// class mouseManeger1 extends MouseMotionAdapter {
	// public void mouseMoved(MouseEvent e) {
	// m_x = e.getX(); m_y = e.getY();
	// }
	// }

	class mouseManeger extends MouseAdapter { // inner class!!
		public void mousePressed(MouseEvent e) {
			int xx = e.getX();
			int yy = e.getY();
			// System.out.println("_stage: "+_stage+"  selected: "+len);
			switch (_stage) {
			case (0): {
				System.out.println("[" + xx + "," + yy + "]");
				break;
			}
			case (POINT): {
				Point_dt q = new Point_dt(xx, yy);
				Point_dt p = screen2world(q);
				_ajd.insertPoint(p);
				repaint();
				break;
			}
			case (FIND): {
				Point_dt q = new Point_dt(xx, yy);
				Point_dt p = screen2world(q);
				_t1 = _ajd.find(p);
				repaint();
				break;
			}
			case (SECTION1): {
				Point_dt q = new Point_dt(xx, yy);
				_p1 = screen2world(q);
				// _p1 = new Point_dt(99792.03,1073355.0,30.0);

				// _t1 = _ajd.find(_p1);
				_stage = SECTION2;
				break;
			}
			case (SECTION2): {
				Point_dt q = new Point_dt(xx, yy);
				_p2 = screen2world(q);
				// _p2 = new Point_dt(149587.055,1040477.0,5.0);

				// _t2 = _ajd.find(_p2);
				_los = new Visibility(_ajd);
				_los.computeSection(_p1, _p2);
				repaint();
				_stage = SECTION1;
				break;
			}
			case (GUARD): {
				Point_dt q = new Point_dt(xx, yy);
				_p1 = screen2world(q);
				if (_guards == null)
					_guards = new Vector<Point_dt>();
				_guards.add(new Point_dt(_p1.x(), _p1.y(), GH));
				/*
				 * if(_p2!=null) { _los = new Visibility(_ajd);
				 * _los.computeSection(_p1,_p2); _visible =
				 * _los.isVisible(30,5); }
				 */
				repaint();
				break;
			}
			case (CLIENT): {
				Point_dt q = new Point_dt(xx, yy);
				_p2 = screen2world(q);
				if (_clients == null)
					_clients = new Vector<Point_dt>();
				_clients.add(new Point_dt(_p2.x(), _p2.y(), CH));
				/*
				 * if(_p1!=null) { _los = new Visibility(_ajd);
				 * _los.computeSection(_p1,_p2); _visible =
				 * _los.isVisible(30,5); }
				 */
				repaint();
				break;
			}

				// //////////////
			}
		}
	}

	Point_dt screen2world(Point_dt p) {
		double x = transform(_dx_f, p.x(), _dx_map);
		double y = transformY(_dy_f, p.y(), _dy_map);
		return new Point_dt(x, y);
	}

	Point_dt world2screen(Point_dt p) {
		double x = transform(_dx_map, p.x(), _dx_f);
		double y = transformY(_dy_map, p.y(), _dy_f);
		return new Point_dt(x, y);
	}

	/**
	 * transforms the point p from the Rectangle th into this Rectangle, Note:
	 * r.contains(p) must be true! assume p.x
	 * < p
	 * .y
	 * 
	 * */

	static double transform(Point_dt range, double x, Point_dt new_range) {
		double dx1 = range.y() - range.x();
		double dx2 = new_range.y() - new_range.x();

		double scale = (x - range.x()) / dx1;
		double ans = new_range.x() + dx2 * scale;
		return ans;
	}

	/**
	 * transform the point p from the Rectangle th into this Rectangle ,Note:
	 * flips the Y cordination for frame!, Note: r.contains(p) must be true!
	 * assume p.x
	 * < p
	 * .y
	 * 
	 * */

	static double transformY(Point_dt range, double x, Point_dt new_range) {
		double dy1 = range.y() - range.x();
		double dy2 = new_range.y() - new_range.x();

		double scale = (x - range.x()) / dy1;
		double ans = new_range.y() - dy2 * scale;
		return ans;
	}
}
