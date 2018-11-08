package io.playtech.spliturl;

import io.playtech.spliturl.algorithms.AlgorithmEstimator;
import io.playtech.spliturl.algorithms.RegexAlgorithm;
import io.playtech.spliturl.algorithms.Result;
import io.playtech.spliturl.algorithms.StateAlgorithm;

import java.net.MalformedURLException;
import java.net.URL;

public class SplitUrl {

    private static int ESTIMATION_COUNT = 10_000;

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Usage: SplitUrl url");
            return;
        }

        final String url = args[0];

        if (!isValidUrl(url)) {
            System.out.println("Invalid url");
            return;
        }

        final AlgorithmEstimator regex = new AlgorithmEstimator(url, new RegexAlgorithm());
        final AlgorithmEstimator state = new AlgorithmEstimator(url, new StateAlgorithm());

        regex.estimate(ESTIMATION_COUNT);
        state.estimate(ESTIMATION_COUNT);

        final Result reResult = regex.getResult();

        // So, the results of both algorithms MUST be the same and you should never see this message
        if(!reResult.equals(state.getResult())) {
            System.out.println("Programmer made a mistake, sorry '(");
            return;
        }

        // print the results
        System.out.println(reResult);
        System.out.println("Regex: " + regex.getExecutionTime() + " nanoseconds");
        System.out.println("State: " + state.getExecutionTime() + " nanoseconds");

    }

    private static boolean isValidUrl(final String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }
}
