package delaunay_triangulation;

/**
 * Created by IntelliJ IDEA.
 * User: Aviad Segev
 * Date: 22/11/2009
 * Time: 20:29:56
 * BoundingBox represents a horizontal bounding rectangle defined by its lower left
 * and upper right point. This is usually used as a rough approximation of the
 * bounded geometry
 */
public class BoundingBox
{
	/**
	* The lower left point of the rectangle
	*/
	private Point_dt lowerLeft;

	/**
	* The upper right corner of the rectangle
	*/
	private Point_dt upperRight;

	/**
	 * Create a bounding box between lowerLeft and upperRight
	 * @param lowerLeft     lower left point of the box
	 * @param upperRight    upper left point of the box
	 */
	public BoundingBox(Point_dt lowerLeft, Point_dt upperRight)
	{
		// Swap the points so that lowerLeft will be below upperRight
		if(lowerLeft.isGreater(upperRight))
		{
			Point_dt temp =  upperRight;
			upperRight = lowerLeft;
			lowerLeft = temp;
		}

        this.lowerLeft = lowerLeft;
		this.upperRight = upperRight;
	}

	/**
	 * @return  Minimum x value
	 */
	public double minX()
	{
		return lowerLeft.x;
	}

	/**
	 * @return  Minimum y value
	 */
	public double minY()
	{
		return lowerLeft.y;
	}

	/**
	 * @return  Maximum x value
	 */
	public double maxX()
	{
		return upperRight.x;
	}

	/**
	 * @return  Maximum y value
	 */
	public double maxY()
	{
		return upperRight.y;
	}

	/**
	 * @return  Width of the bounding box
	 */
	public double getWidth()
	{
		return upperRight.x() - lowerLeft.x();
	}

	/**
	 * @return  Height of the bounding box
	 */
	public double getHeight()
	{
		return upperRight.y() - lowerLeft.y();
	}
}
