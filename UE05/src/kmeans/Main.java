package kmeans;

import inout.Out;

public class Main {
    /**
     * Number of clusters
     */
    private static final int K = 7;


    /**
     * Number of how many times the KMean Algorithm should be executed
     */
    private static final int EXECUTION_MULTIPLIER = 12;

    /**
     * Main method to start execution
     *
     * @param args not used
     */
    public static void main(String[] args) throws InterruptedException {
        execute(100);
        execute(1000);
        execute(10000);
        execute(100000);
        execute(1000000);
    }

    public static void execute(int n) {
        Out.println("Size: " + n);
        // execute
        long timeSum = 0;

        // execute 10 times for sequential time measurement
        for (int i = 0; i < EXECUTION_MULTIPLIER; i++) {
            KMeanSeq kMeansAlgoSeq = new KMeanSeq(KMeanSeq.createRandomData(n), K);
            long time = kMeansAlgoSeq.cluster(false);

            // ignore first try
            if (i > 1 || EXECUTION_MULTIPLIER == 1) {
                timeSum += time;
            }
        }
        Out.println("Completed " + (EXECUTION_MULTIPLIER - 2) + " sequential executions for size " + n + " in " + timeSum + " nanoseconds.");


        timeSum = 0;
        // execute 10 times for parallel time measurement
        for (int i = 0; i < EXECUTION_MULTIPLIER; i++) {
            KMeanParallel kMeansAlgoPar = new KMeanParallel(KMeanParallel.createRandomData(n), K);
            long time = kMeansAlgoPar.clusterParallel(false);

            // ignore first try
            if (i > 1 || EXECUTION_MULTIPLIER == 1) {
                timeSum += time;
            }
        }
        Out.println("Completed " + (EXECUTION_MULTIPLIER - 2) + " parallel executions for size " + n + " in " + timeSum + " nanoseconds.");
        Out.println();
    }
}
