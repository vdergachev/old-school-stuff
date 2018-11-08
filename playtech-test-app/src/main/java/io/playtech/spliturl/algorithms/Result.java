package io.playtech.spliturl.algorithms;

import java.util.Objects;

public class Result {

    private String scheme;
    private String host;

    // So, i decided to assign a rule for next two fields
    // Default port is 80 if not specified by url
    // Path always starts with leading dash, default value is '/'
    private int port;
    private String path;

    private String parameters;

    public Result() {
    }

    public Result withScheme(final String scheme) {
        this.scheme = scheme;
        return this;
    }

    public Result withHost(final String host) {
        this.host = host;
        return this;
    }

    public Result withPort(final int port) {
        this.port = port;
        return this;
    }

    public Result withPath(final String path) {
        this.path = path;
        return this;
    }

    public Result withParameters(final String parameters) {
        this.parameters = parameters;
        return this;
    }

    // Looks awful
    @Override
    public String toString() {
        return scheme + "\n" + host + "\n" + port + "\n" + path + "\n" + parameters;
    }

    // I wanna be able to compare result of both algorithms
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return port == result.port &&
                Objects.equals(scheme, result.scheme) &&
                Objects.equals(host, result.host) &&
                Objects.equals(path, result.path) &&
                Objects.equals(parameters, result.parameters);
    }

}
