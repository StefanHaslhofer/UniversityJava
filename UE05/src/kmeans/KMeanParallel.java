package kmeans;

import java.awt.Color;
import java.util.Random;
import java.util.concurrent.*;

import inout.Out;
import inout.Window;

/**
 * Sequential k-mean clustering algorithm.
 */
@SuppressWarnings("unused")
public class KMeanParallel {



    /**
     * Max values of x and y coordinates of data points
     */
    private static final int SIZE = 600;

    /**
     * Random number generator
     */
    private static final Random RAND = new Random();

    private static final int N_EXPERIMENTS = 20;
    private static final int THRESHHOLD = 1000;

    private static final int PARALLELISM = 16;

    private boolean outputEnabled = true;

    public final ExecutorService executor = Executors.newFixedThreadPool(PARALLELISM);
    private CountDownLatch endLatch;

    // ---

    /**
     * Number of clusters
     */
    private final int k;

    /**
     * The data points to cluster
     */
    private final DataPoint[] data;

    /**
     * Points representing the cluster centroids
     */
    private Point[] centroids;

    /**
     * Constructor initializing the data points to cluster
     * and the number of clusters.
     *
     * @param data the data points
     * @k the number of clusters
     */
    public KMeanParallel(DataPoint[] data, int k) {
        super();
        this.k = k;
        this.data = data;
        centroids = new Point[k];
    }

    private class ClusterTask implements Callable<Boolean> {

        private final int from;
        private final int to;

        private ClusterTask(int from, int to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public Boolean call() throws Exception {
            boolean stable = true;
            for (int i = this.from; i < this.to; i++) {
                int closestCluster = getClosestCluster(data[i]);
                if (stable && data[i].cluster != closestCluster) {
                    stable = false;
                }
                data[i].cluster = closestCluster;
            }

            computeCentroids();

            output();
            endLatch.countDown();
            return stable;
        }
    }

    /**
     * Creates random data points.
     * x and y coordinates are randomly chosen between 0 and {@link SIZE}.
     *
     * @param n number of points
     * @return array of n data points
     */
    public static DataPoint[] createRandomData(int n) {
        DataPoint[] points = new DataPoint[n];
        for (int i = 0; i < n; i++) {
            points[i] = new DataPoint(RAND.nextInt(SIZE), RAND.nextInt(SIZE));
        }
        return points;
    }

    /**
     * Main method to start execution
     *
     * @param outputEnabled       specifies if window and console output should be shown
     *
     * @return execution time
     */
    public long clusterParallel(boolean outputEnabled) {
        this.outputEnabled = outputEnabled;

        doInitialClustering();

        // measure start-time
        long startTime = System.nanoTime();

        computeCentroids();

        output();

        boolean stable = false;
        while (!stable) {

            stable = doNewClustering();
            computeCentroids();

            output();
        }

        return System.nanoTime() - startTime;
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
     * Calls clustering tasks
     *
     * @return true, if all tasks were executed successfully, false otherwise
     */
    private boolean doNewClustering() {
        endLatch = new CountDownLatch(PARALLELISM);
        Future<Boolean>[] clusterTaskRes = new Future[PARALLELISM];

        int size = Math.round(data.length / PARALLELISM);
        for (int i = 0; i < PARALLELISM; i++) {
            clusterTaskRes[i] = executor.submit(new ClusterTask(i * size, (i + 1) * size));
        }
        try {
            endLatch.await();
            for (Future<Boolean> r : clusterTaskRes) {
                if (!r.get()) {
                    return false;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
        executor.shutdown();
        return true;
    }

    private class RecursiveSumTask extends RecursiveTask<int[][]> {

        private final int from;
        private final int to;

        private RecursiveSumTask(int from, int to) {
            this.from = from;
            this.to = to;
        }

        @Override
        protected int[][] compute() {
            int[][] sums = new int[3][k];
            if (to - from < THRESHHOLD) {
                return computeCentroids(from, to, sums);
            } else {
                int half = (from + to) / 2;
                RecursiveSumTask task1 = new RecursiveSumTask(from, half + 1);
                RecursiveSumTask task2 = new RecursiveSumTask(half + 1, to);
                task1.fork();
                task2.fork();

                int[][] sum1 = task1.join();
                int[][] sum2 = task2.join();

                for (int i = 0; i < sums.length; i++) {
                    for (int j = 0; j < sums[i].length; j++) {
                        sums[i][j] = sum1[i][j] + sum2[i][j];
                    }
                }

                return sums;
            }
        }

        private int[][] computeCentroids(int from, int to, int[][]sums) {
            for (int i = from; i < to; i++) {
                sums[0][data[i].cluster] += data[i].x;
                sums[1][data[i].cluster] += data[i].y;
                sums[2][data[i].cluster]++;
            }

            return sums;


        }
    }

    /**
     * Computes the cluster centroids for the current clustering.
     * Result is are new points in the array {@link centroids}.
     */
    private void computeCentroids() {
        RecursiveSumTask task = new RecursiveSumTask(0, data.length);
        int[][] sums = ForkJoinPool.commonPool().invoke(task);

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

    /**
     * Constant for delaying the output in ms
     */
    private static final int DELAY = 1000; // ms

    /**
     * Colors used for showing the clustering of data points in {@link inout.Window} output.
     */
    private static final Color[] COLORS =
            {Color.RED, Color.BLUE, Color.CYAN, Color.GREEN,
                    Color.GRAY, Color.MAGENTA, Color.ORANGE, Color.BLACK,
                    Color.DARK_GRAY, Color.YELLOW, Color.PINK};

    /**
     * Puts out the current clustering by
     * <ul>
     *   <li> drawing data points and cluster centroids on {@link inout.Window} </li>
     *   <li> printing the cluster centroids </li>
     *   <li>  delaying the computation </li>
     * </ul>
     */
    private void output() {
        if (this.outputEnabled) {
            draw();
            printClusters();
            delay();
        }
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
        } catch (InterruptedException e) {
        }
    }

}
