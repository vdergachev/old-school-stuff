package io.playtech.spliturl.algorithms;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexAlgorithm implements UrlSplitAlgorithm {

    private static int DEFAULT_PORT = 80;

    // This regexp was stolen from stackoverflow and modified
    // Allows to extract each group of url
    final static Pattern PATTERN = Pattern.compile("^([^:/?#]+)?(://([^/?#]*)/?)?([^?#]*)(\\?([^#]*)?)?");

    @Override
    public Result split(String url) {

        final Result result = new Result();

        final Matcher matcher = PATTERN.matcher(url);
        if (!matcher.find()) {
            return result;
        }

        final String scheme = matcher.group(1);
        String host = matcher.group(3);
        int port = DEFAULT_PORT;
        if (host.contains(":")) {
            final String[] hostAndPort = host.split(":");
            host = hostAndPort[0];
            port = getPort(Integer.parseInt(hostAndPort[1]));
        }

        String path = "/" + getStringValue(matcher.group(4)); // dash is optional char of path but it should present ( look at Result.java)

        final String params = getStringValue(matcher.group(6));

        return result
                .withScheme(scheme)
                .withHost(host)
                .withPort(port)
                .withPath(path)
                .withParameters(params);
    }

    private static String getStringValue(final String val) {
        return val == null ? "" : val;
    }

    private static int getPort(final int port) {
        return port <= 0 ? DEFAULT_PORT : port;
    }
}
