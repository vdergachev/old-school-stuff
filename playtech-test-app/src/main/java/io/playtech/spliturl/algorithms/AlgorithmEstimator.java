package io.playtech.spliturl.algorithms;

import java.time.Duration;
import java.time.Instant;

public class AlgorithmEstimator {

    final private String url;
    final private UrlSplitAlgorithm algorithm;

    private Result result;
    private long executionTime; // nanoseconds


    public AlgorithmEstimator(final String url, final UrlSplitAlgorithm algorithm) {
        this.url = url;
        this.algorithm = algorithm;
    }

    // This method invokes algorithm's split method given number of time and measures average time of execution
    public void estimate(final int times) {
        final Instant start = Instant.now();

        for (int i = 0; i < times; i++) {
            result = algorithm.split(url);
        }

        final Instant end = Instant.now();
        executionTime = Duration.between(start, end).toNanos() / times;
    }

    public Result getResult() {
        return result;
    }

    public long getExecutionTime() {
        return executionTime;
    }
}
