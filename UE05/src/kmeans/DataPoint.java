package kmeans;

/**
 * Point which is assigned to a cluster 
 */
public class DataPoint extends Point {
	
	/**
	 * The cluster for this point. 
	 * This cluster id is mutable. 
	 */
	public int cluster; 

	/**
	 * Constructor initializing x and y. 
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public DataPoint(int x, int y) {
		super(x, y);
	}

	/**
	 * Returns a string representation with x, y and cluster id.
	 * @return the string representation 
	 */
	@Override
	public String toString() {
		return String.format("%d/%d(%d)", x, y, cluster);
	}
	
	

}
