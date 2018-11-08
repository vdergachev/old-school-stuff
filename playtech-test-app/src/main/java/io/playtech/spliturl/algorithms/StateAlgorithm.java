package io.playtech.spliturl.algorithms;

import java.net.URL;

public class StateAlgorithm implements UrlSplitAlgorithm {

    private static int DEFAULT_PORT = 80;

    private interface SplitState {
        UrlSplitState nextState(Result result, URL url);
    }

    // Enums allow me to organise all steps in a wired-pipeline
    // But it can be implemented like a map, key is step name, and value is an implementation
    // Each step make extraction of url part and changes the state
    private enum UrlSplitState implements SplitState {
        BEGIN_STATE {
            @Override
            public UrlSplitState nextState(final Result result, final URL url) {
                return SCHEME_STATE;
            }
        },
        SCHEME_STATE {
            @Override
            public UrlSplitState nextState(final Result result, final URL url) {
                result.withScheme(url.getProtocol());
                return HOST_STATE;
            }
        },
        HOST_STATE {
            @Override
            public UrlSplitState nextState(final Result result, final URL url) {
                result.withHost(url.getHost());
                return PORT_STATE;
            }
        },
        PORT_STATE {
            @Override
            public UrlSplitState nextState(final Result result, final URL url) {
                result.withPort(getPort(url.getPort()));
                return PATH_STATE;
            }
        },
        PATH_STATE {
            @Override
            public UrlSplitState nextState(final Result result, final URL url) {
                result.withPath(getPath(url.getPath()));
                return PARAMETERS_STATE;
            }
        },
        PARAMETERS_STATE {
            @Override
            public UrlSplitState nextState(final Result result, final URL url) {
                result.withParameters(getStringValue(url.getQuery()));
                return END_STATE;
            }
        },
        END_STATE {
            @Override
            public UrlSplitState nextState(final Result result, final URL url) {
                return this;
            }
        }
    }

    private static String getStringValue(final String val) {
        return val == null ? "" : val;
    }

    private static String getPath(final String val) {
        final String value = getStringValue(val);
        return value.startsWith("/") ? value : "/" + value;
    }

    private static int getPort(final int port) {
        return port <= 0 ? DEFAULT_PORT : port;
    }


    @Override
    public Result split(String urlStr) {

        final Result result = new Result();
        final URL url = getURL(urlStr);

        for (UrlSplitState splitState = UrlSplitState.BEGIN_STATE; !splitState.equals(UrlSplitState.END_STATE); ) {
            splitState = splitState.nextState(result, url);
        }

        return result;
    }

    private static URL getURL(final String url) {
        try {
            return new URL(url);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
