package algorithms.topographic_map;


import java.util.Iterator;
import java.util.LinkedList;

import delaunay_triangulation.Point_dt;

/**
 * The CounterLine class describes an counter line.
 * The CounterLine is defined by a list of points, height and an argument that describes 
 * if the counter line is closed (Polygon) or not (Path). 
 * @version 1.0 12 December 2009
 * @author Omri Gutman
 *
 */
public class CounterLine {
	private boolean _isClosed;
	private double _height;
	private LinkedList<Point_dt> _points;
	public CounterLine(LinkedList<Point_dt> points, double height, boolean isClosed){
		this._isClosed = isClosed;
		this._height = height;
		this._points = points;
	}
	
	
	/**
	 * @return the counter line height.
	 */
	public double getHeight(){
		return _height;
	}
	/**
	 * 
	 * @return An Iterator object that iterates over the counter line points.
	 * @see Point_dt
	 * @see Iterator
	 */
	public Iterator<Point_dt> getPointsListIterator(){
		return _points.listIterator();
	}
	
	/**
	 *
	 * @return true is the counter line is closed counter line(Polygon) or not (Path)
	 */
	public boolean isClosed(){
		return _isClosed;
	}
	
	
	/**
	 * @return the number of points in this CounterLine.
	 */
	public int getNumberOfPoints(){
		return _points.size();
	}
}
