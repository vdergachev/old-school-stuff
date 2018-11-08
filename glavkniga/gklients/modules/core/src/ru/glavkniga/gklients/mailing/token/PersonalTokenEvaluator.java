package ru.glavkniga.gklients.mailing.token;

import java.util.function.Function;

/**
 * Created by vdergachev on 11.07.17.
 */
@FunctionalInterface
public interface PersonalTokenEvaluator<T, R> extends Function<T, R> {
    default Function<T, R> getEvaluator(T t) {
        if (t == null) {
            return null;
        }
        return this;
    }
}
