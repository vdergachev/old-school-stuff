package ru.glavkniga.gklients.mailing;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

public class ParsedToken implements Serializable {

    private static final long serialVersionUID = 8374103754112136157L;

    private final String token;
    private final String defaultValue;

    private ParsedToken(String token, String defaultValue) {
        this.token = token;
        this.defaultValue = defaultValue;
    }

    public String getToken() {
        return token;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public static ParsedToken parse(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        value = value.trim();

        if (StringUtils.countMatches(value, "|") > 1) {
            return null;
        }

        final int sep = value.indexOf("|");

        if (sep == -1) {
            return new ParsedToken(value, null);
        }

        if (sep == 0) {
            return null;
        }

        final String token = value.substring(0, sep).trim();

        if (value.isEmpty()) {
            return null;
        }

        final String defValPart = value.substring(sep + 1).trim();

        if (defValPart.length() < 2) {
            return null;
        }

        if (StringUtils.countMatches(defValPart, "\"") != 2) {
            return null;
        }

        final String defVal = defValPart.substring(1, defValPart.length() - 1);
        return new ParsedToken(token, defVal);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParsedToken that = (ParsedToken) o;

        if (token != null ? !token.equals(that.token) : that.token != null) return false;
        return defaultValue != null ? defaultValue.equals(that.defaultValue) : that.defaultValue == null;
    }

    @Override
    public int hashCode() {
        int result = token != null ? token.hashCode() : 0;
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ParsedToken{" +
                "token='" + token + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                '}';
    }
}
