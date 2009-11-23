package delaunay_triangulation;

/**
 * Created by IntelliJ IDEA.
 * User: Aviad Segev
 * Date: 22/11/2009
 * Time: 20:10:04
 *
 * Grid Index is a simple spatial index for fast point/triangle location.
 * The idea is to divide a predefined geographic extent into equal sized
 * cell matrix (tiles). Every cell will be associated with a triangle which lies inside.
 * Therfore, one can easily locate a triangle in close proximity of the required
 * point by searching from the point's cell triangle. If the triangulation is
 * more or less uniform and bound in space, this index is very effective,
 * roughly recuing the searched triangles by square(xCellCount * yCellCount),
 * as only the triangles inside the cell are searched.
 *
 * The index takes xCellCount * yCellCount capacity. While more cells allow
 * faster searches, even a small grid is helpfull.
 *
 * This implementation holds the cells in a memory matrix, but such a grid can
 * be easily mapped to a DB table or file where it is usually used for it's fullest.
 *
 * Note that the index is geographically bound - only the region given in the
 * c'tor is indexed. Triangles outside the indexed region are not indexed in order
 * to avoid massive memory copying. Since triangulation is mostly always
 * used for static raster data, this is never an issue in real life. after all, one can
 * simply rebuild the index in short time.
 *
 * TODO:  Any change to the triangles after the indexing will invalidate the index,
 * as we do not keep track of updated indexed triangles. After dynamic addtion and
 * Removal of points will be enabled, all changed triangles' cells must be resampled
 * for valid triangles (The changed triangles can be the new cell triangles)
 */
public class GridIndex
{
	/**
	 * Horizontal geographic size of a cell index
	 */
	private final double x_size;

	/**
	 * Vertical geographic size of a cell inedx
	 */
	private final double y_size;

	/**
	 * The indexed geographic size
	 */
	private BoundingBox indexRegion;

	/**
	 * A division of indexRegion to a cell matrix, where each cell holds a triangle
	 * which lies in it
	 */
	private Triangle_dt[][] grid;

	/**
	 * Constructs a grid index holding the triangles of a delaunay triangulation.
	 * This version uses the bounding box of the triangulation as the region to index.
	 *
	 *  @param   delaunay        delaunay triangulation to index
	 *  @param   xCellCount     number of grid cells in a row
	 *  @param   yCellCount     number of grid cells in a column
	 */
	public GridIndex( Delaunay_Triangulation delaunay, int xCellCount, int yCellCount)
	{
		this(delaunay, xCellCount, yCellCount,
				new BoundingBox(delaunay.minBoundingBox(), delaunay.maxBoundingBox()));
	}

	/**
	 * Constructs a grid index holding the triangles of a delaunay triangulation.
	 * The grid will be made of (xCellCount * yCellCount) cells.
	 * The smaller the cells the less triangles that fall in them, whuch means better
	 * indexing, but also more cells in the index, which mean more storage.
	 * The smaller the indexed region is, the smaller the cells can be and still
	 * maintain the same capacity, but adding geometries outside the initial region
	 * will invalidate the index !
	 *
	 *  @param   delaunay     delaunay triangulation to index
	 *  @param   xCellCount   number of grid cells in a row
	 *  @param   yCellCount   number of grid cells in a column
	 *  @param   region         geographic region to index
	 */
	public GridIndex( Delaunay_Triangulation delaunay, int xCellCount,
	                  int yCellCount, BoundingBox region)
	{
		indexRegion = region;
		x_size = region.getWidth() / yCellCount;
		y_size = region.getHeight() / xCellCount;

		// The grid will hold a trinagle for each cell, so a point (x,y) will lie
		// in the cell representing the grid partition of region to a
		//  xCellCount on yCellCount grid
		grid = new Triangle_dt[xCellCount][yCellCount];

		// Go over each grid cell and locate a triangle in it to be the cell's
		// starting search triangle. Since we only pass between adjacent cells
		// we can search from the last triangle found and not from the start.

		Triangle_dt colStartTriangle = delaunay.find(MiddleOfCell(0,0));

		// Add triangles for each column cells
		for(int i = 0; i < xCellCount; i++)
		{
			// Find a triangle at the begining of the current column
			colStartTriangle = delaunay.find(MiddleOfCell(i, 0), colStartTriangle);
			grid[i][0] = colStartTriangle;
			Triangle_dt prevRowTriangle = colStartTriangle;

			// Add triangles for the next row cells
			for(int j = 1; j < yCellCount; j++)
			{
				grid[i][j] = delaunay.find(MiddleOfCell(i,j), prevRowTriangle);
				prevRowTriangle = grid[i][j];
			}
		}
	}

	/**
	 * Finds a triangle near the given point
	 * @param point     a query point
	 * @return              a triangle at the same cell of the point
	 */
	public Triangle_dt findCellTriangleOf(Point_dt point) {
		int x_index = (int) ((point.x() - indexRegion.minX()) / x_size);
		int y_index = (int) ((point.y() - indexRegion.minY()) / y_size);
		return grid[x_index][y_index];
	}

	/**
	 * Create a point at the center of a cell
	 * @param x_index   horizontal cell index
	 * @param y_index   vertical cell index
	 * @return                Point at the center of the cell at (x_index, y_index)
	 */
	private Point_dt MiddleOfCell(int x_index, int y_index)
	{
		double middleXCell = indexRegion.minX() + x_index * x_size + x_size/2;
		double middleYCell = indexRegion.minY() + y_index * y_size + y_size/2;
		return new Point_dt(middleXCell, middleYCell);
	}
}
