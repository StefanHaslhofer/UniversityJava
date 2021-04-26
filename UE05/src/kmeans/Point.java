package kmeans;

/**
 * Point with x and y coordinate 
 */
public class Point {
	
	/** x coordinate */
	public final int x;
	
	/** y coordinate */
	public final int y;

	/**
	 * Constructor initializing x and y. 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public Point(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns a string representation. 
	 * @return the string representation 
	 */
	@Override
	public String toString() {
		return String.format("%d/%d", x, y);
	}

}
