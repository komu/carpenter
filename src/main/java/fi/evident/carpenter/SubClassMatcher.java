package fi.evident.carpenter;

import fi.evident.carpenter.functions.Function3;
import fi.evident.carpenter.functions.Function4;
import fi.evident.carpenter.matchables.Matchable1;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Function;

import static fi.evident.carpenter.matchables.Matchables.matchable;

/**
 * Base class for matchers that work with a specific sub class of S.
 */
public abstract class SubClassMatcher<T, S extends T> extends Matcher<T> {

    @NotNull
    private final Class<? extends S> inputType;

    protected SubClassMatcher(@NotNull Class<? extends S> inputType) {
        this.inputType = inputType;
    }

    @NotNull
    @Override
    public final Match<T> match(@NotNull T value) {
        if (inputType.isInstance(value))
            return matchSafely(inputType.cast(value));
        else
            return Match.failure();
    }

    @NotNull
    protected abstract Match<T> matchSafely(@NotNull S value);


    @NotNull
    public static <S, T extends S, V> Matcher<S> of(@NotNull Class<T> type,
                                                    @NotNull Function<V, S> ctor,
                                                    @NotNull Function<T,V> getter,
                                                    @NotNull Matcher<V> matcher) {
        return of(matcher, matchable(type, ctor, getter));
    }

    @NotNull
    public static <S, V> Matcher<S> of(@NotNull Matcher<V> matcher, @NotNull Matchable1<S, V> matchable) {
        return new Matcher<S>() {
            @NotNull
            @Override
            public Match<S> match(@NotNull S value) {
                return matchable.match(value, matcher);
            }
        };
    }

    @NotNull
    public static <S, T extends S, V1, V2> Matcher<S> of(@NotNull Class<T> type,
                                                         @NotNull BiFunction<V1, V2, S> ctor,
                                                         @NotNull Function<T, Match<V1>> matcher1,
                                                         @NotNull Function<T, Match<V2>> matcher2) {
        return new SubClassMatcher<S, T>(type) {

            @NotNull
            @Override
            protected Match<S> matchSafely(@NotNull T value) {
                return Match.from(ctor, matcher1.apply(value), matcher2.apply(value));
            }
        };
    }

    @NotNull
    public static <S, T extends S, V1, V2, V3> Matcher<S> of(@NotNull Class<T> type,
                                                             @NotNull Function3<V1, V2, V3, S> ctor,
                                                             @NotNull Function<T, Match<V1>> matcher1,
                                                             @NotNull Function<T, Match<V2>> matcher2,
                                                             @NotNull Function<T, Match<V3>> matcher3) {
        return new SubClassMatcher<S, T>(type) {

            @NotNull
            @Override
            protected Match<S> matchSafely(@NotNull T value) {
                return Match.from(ctor, matcher1.apply(value), matcher2.apply(value), matcher3.apply(value));
            }
        };
    }

    @NotNull
    public static <S, T extends S, V1, V2, V3, V4> Matcher<S> of(@NotNull Class<T> type,
                                                                 @NotNull Function4<V1, V2, V3, V4, S> ctor,
                                                                 @NotNull Function<T, Match<V1>> matcher1,
                                                                 @NotNull Function<T, Match<V2>> matcher2,
                                                                 @NotNull Function<T, Match<V3>> matcher3,
                                                                 @NotNull Function<T, Match<V4>> matcher4) {
        return new SubClassMatcher<S, T>(type) {

            @NotNull
            @Override
            protected Match<S> matchSafely(@NotNull T value) {
                return Match.from(ctor, matcher1.apply(value), matcher2.apply(value), matcher3.apply(value), matcher4.apply(value));
            }
        };
    }
}
