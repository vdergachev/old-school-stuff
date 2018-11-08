package ru.glavkniga.gklients.mailing.token;

import java.util.function.Function;

/**
 * Created by vdergachev on 11.07.17.
 */
@FunctionalInterface
public interface TokenEvaluator<T, R> extends Function<T, R> {
    default R evaluate(T t) {
        return apply(t);
    }
}
