package algorithms.topographic_map;

import java.util.ArrayList;
import java.util.Iterator;

import delaunay_triangulation.Triangle_dt;

/**
 * Mock Implementation of {@link ITopographicMap}
 * @version 1.0 12 December 2009
 * @author Omri Gutman
 *
 */
public class TopographicMapMock implements ITopographicMap{

	/** 
	 * Mock implementation
	 * @return an empty list of counter lines.
	 * @see ITopographicMap#createTopographicMap(java.util.Iterator, int)
	 */
	@Override
	public ArrayList<CounterLine> createTopographicMap(
			Iterator<Triangle_dt> triangles, int height) throws Exception {
		return new ArrayList<CounterLine>();
	}

}
