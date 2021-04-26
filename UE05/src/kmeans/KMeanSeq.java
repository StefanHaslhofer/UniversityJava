package kmeans;

import java.awt.Color;
import java.util.Random;

import inout.In;
import inout.Out;
import inout.Window;

/** 
 * Sequential k-mean clustering algorithm. 
 */
@SuppressWarnings("unused")
public class KMeanSeq {
	
	/** Number of data points */
	private static final int N = 1000; 
	
	/** Number of clusters */ 
	private static final int K = 7; 
	
	/** Max values of x and y coordinates of data points */
	private static final int SIZE = 600; 
	
	/** Random number generator */
	private static final Random RAND = new Random(); 
	
	private static final int N_EXPERIMENTS = 20;
	
	/**
	 * Creates random data points. 
	 * x and y coordinates are randomly chosen between 0 and {@link SIZE}.
	 * 
	 * @param n number of points  
	 * @return array of n data points
	 */
	private static DataPoint[] createRandomData(int n) {
		DataPoint[] points = new DataPoint[n];
		for (int i = 0; i < n; i++) {
			points[i] = new DataPoint(RAND.nextInt(SIZE), RAND.nextInt(SIZE)); 
		}
		return points; 
	}
	
	/** 
	 * Main method to start execution 
	 * @param args not used 
	 */
	public static void main(String[] args) {
		
		KMeanSeq kMeansAlgo = new KMeanSeq(createRandomData(N), K); 
		kMeansAlgo.cluster(); 
				
	}
	
	// ---  
	
	/** Number of clusters */
	private final int k; 
	
	/** The data points to cluster */ 
	private final DataPoint[] data; 
	
	/** Points representing the cluster centroids */ 
	private Point[] centroids; 
		
	/** 
	 * Constructor initializing the data points to cluster 
	 * and the number of clusters. 
	 * 
	 * @param data the data points 
	 * @k the number of clusters 
	 */
	public KMeanSeq(DataPoint[] data, int k) {
		super();
		this.k = k; 
		this.data = data;
		centroids = new Point[k]; 
	}

	/** 
	 * Does the clustering of the data points. 
	 * As a result, the data points get assigned ids for the clusters. 
	 */
	public void cluster() {
		
		doInitialClustering(); 
		
		computeCentroids(); 
		
		output(); 
		
		boolean stable = false; 
		while (! stable) {
			
			stable = doNewClustering(); 
			computeCentroids();
			
			output();
		}
		
		Out.print("Completed");
	}

	/**
	 * Does a random initial clustering of the data points into k clusters. 
	 * As a result, the data points get assigned initial ids for clusters. 
	 */
	private void doInitialClustering() {
		for (int i = 0; i < data.length; i++) {
			data[i].cluster = RAND.nextInt(k); 
		}
	}

	/** 
	 * Does a new clustering of the data points. 
	 * 
	 * @return true, if there was no change in the clustering, false otherwise. 
	 */
	private boolean doNewClustering() {
		boolean stable = true; 
		// TODO: parallelize using thread pool ---------------------
		for (int i = 0; i < data.length; i++) {
			int closestCluster = getClosestCluster(data[i]);
			if (stable && data[i].cluster != closestCluster) {
				stable = false; 
			}
			data[i].cluster = closestCluster; 
		}
		// ---------------------------------------------------------
		return stable; 
	}

	/** 
	 * Computes the cluster centroids for the current clustering. 
	 * Result is are new points in the array {@link centroids}.  
	 */
	private void computeCentroids() {
		
		// sums[0][j] sum of x-coordiantes of points in cluster j
		// sums[1][j] sum of y-coordiantes of points in cluster j
		// sums[2][j] number of points in cluster j
		int[][] sums =  new int[3][k]; 

		// TODO: parallelize with fork-join recursive tasks ------------
		for (int i = 0; i < data.length; i++) {
			sums[0][data[i].cluster] += data[i].x; 
			sums[1][data[i].cluster] += data[i].y;
			sums[2][data[i].cluster]++; 
		}
		// -------------------------------------------------------------
 
		for (int j = 0; j < centroids.length; j++) {
			if (sums[2][j] != 0) {
				centroids[j] = new Point(sums[0][j] / sums[2][j], 
						sums[1][j] / sums[2][j]);
			} 
		}
	}
	
	/** 
	 * Computes the cluster with the centroid closest to the given data point. 
	 * 
	 * @param p the data point
	 * @return the index of the closest cluster 
	 */
	private int getClosestCluster(DataPoint p) {
		int minCluster = -1; 
		double minDist = Double.MAX_VALUE; 
		for (int c = 0; c < centroids.length; c++) {
			double dist = computeDist(p, centroids[c]); 
			if (dist < minDist) {
				minCluster = c; 
				minDist = dist; 
			}
		}
		return minCluster;
	}

	/** 
	 * Computes the square of the distance of two points.
	 *  
	 * @param p1 the first point
	 * @param p2 the second point
	 * @return the square of the distance 
	 */
	private double computeDist(Point p1, Point p2) {
		int dx = p1.x - p2.x; 
		int dy = p1.y - p2.y; 
		return dx * dx + dy * dy;
	}
	
	// ---- output -----------------------------------------------------

	/** Constant for delaying the output in ms */ 
	private static final int DELAY = 1000; // ms

	/** Colors used for showing the clustering of data points in {@link inout.Window} output. */
	private static final Color[] COLORS = 
		{ Color.RED, Color.BLUE, Color.CYAN, Color.GREEN, 
		  Color.GRAY, Color.MAGENTA, Color.ORANGE, Color.BLACK, 
		  Color.DARK_GRAY, Color.YELLOW, Color.PINK }; 
	
	/**
	 * Puts out the current clustering by 
	 * <ul>
	 *   <li> drawing data points and cluster centroids on {@link inout.Window} </li> 
	 *   <li> printing the cluster centroids </li> 
	 *   <li>  delaying the computation </li> 
	 * </ul>
	 */
	private void output() {
		draw(); 
		printClusters(); 
		delay();
	}

	/** 
	 * Draws the data points and cluster centroids on {@link inout.Window}.  
	 */
	private void draw() {
		Window.clear();
		for (int i = 0; i < data.length; i++) {
			Window.fillCircle(data[i].x, data[i].y, 2, 
					COLORS[data[i].cluster % COLORS.length]);
		}
		for (int c = 0; c < centroids.length; c++) {
			Window.drawCircle(centroids[c].x, centroids[c].y, 4, COLORS[c % COLORS.length]);
		}
	}

	/** 
	 * Prints out the cluster centroids. 
	 */
	private void printClusters() {
		for (Point cc : centroids) {
			Out.print(cc.toString() + " ");
		}
		Out.println(); 
	}
	
	/** 
	 * Delays the computation for allowing inspection of intermediate results. 
	 * Delay is defined by constant {@link Delayed}. 
	 * Alternatively, a user input may be used to stop computation. 
	 */
	private void delay() {
		// Uncomment for controlling steps by users 
//		Out.print("next step: ");
//		In.readLine(); // request any input from user to continue
		try {
			Thread.sleep(DELAY);
		} catch (InterruptedException e) {}
	}
	
}
