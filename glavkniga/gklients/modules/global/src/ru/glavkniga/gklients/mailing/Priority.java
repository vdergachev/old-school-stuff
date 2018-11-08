package ru.glavkniga.gklients.mailing;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Created by Vladimir on 25.06.2017.
 */
public enum Priority {
    HIGH(1),
    ABOVE_NORMAL(2),
    NORMAL(3),
    BELOW_NORMAL(4),
    LOW(5);

    private final int value;

    Priority(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getAsString() {
        return Integer.toString(value);
    }

    public static Priority parse(final String value) {

        if (isNullOrEmpty(value)) {
            throw new IllegalArgumentException("Wrong \"" + value + "\" email message priority");
        }

        for (final Priority priority : Priority.values()) {
            if (priority.getAsString().equals(value)) {
                return priority;
            }
        }

        return null;
    }
}
