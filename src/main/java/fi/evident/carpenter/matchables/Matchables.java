package fi.evident.carpenter.matchables;

import fi.evident.carpenter.Match;
import fi.evident.carpenter.functions.Function3;
import fi.evident.carpenter.functions.Function4;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Constructs {@code MatchableX} instances from accessor and constructor references.
 */
public final class Matchables {

    private Matchables() {
    }

    @NotNull
    public static <S, T extends S, V>
    Matchable1<S, V> matchable(@NotNull Class<T> type,
                               @NotNull Function<V, S> ctor,
                               @NotNull Function<? super T, ? extends V> getter) {
        return (value, matcher) ->
                withType(type, value, (T v) -> Match.from(ctor, matcher.apply(getter.apply(v))));
    }

    @NotNull
    public static <S, T extends S, V1, V2>
    Matchable2<S, V1, V2> matchable(@NotNull Class<T> type,
                                    @NotNull BiFunction<V1, V2, S> ctor,
                                    @NotNull Function<? super T, ? extends V1> getter1,
                                    @NotNull Function<? super T, ? extends V2> getter2) {
        return (value, matcher1, matcher2) ->
                withType(type, value, (T v) -> Match.from(ctor, matcher1.apply(getter1.apply(v)), matcher2.apply(getter2.apply(v))));
    }

    @NotNull
    public static <S, T extends S, V1, V2, V3>
    Matchable3<S, V1, V2, V3> matchable(@NotNull Class<T> type,
                                        @NotNull Function3<V1, V2, V3, S> ctor,
                                        @NotNull Function<? super T, ? extends V1> getter1,
                                        @NotNull Function<? super T, ? extends V2> getter2,
                                        @NotNull Function<? super T, ? extends V3> getter3) {
        return (value, matcher1, matcher2, matcher3) ->
                withType(type, value, (T v) -> Match.from(ctor, matcher1.apply(getter1.apply(v)), matcher2.apply(getter2.apply(v)), matcher3.apply(getter3.apply(v))));
    }

    @NotNull
    public static <S, T extends S, V1, V2, V3, V4>
    Matchable4<S, V1, V2, V3, V4> matchable(@NotNull Class<T> type,
                                            @NotNull Function4<V1, V2, V3, V4, S> ctor,
                                            @NotNull Function<? super T, ? extends V1> getter1,
                                            @NotNull Function<? super T, ? extends V2> getter2,
                                            @NotNull Function<? super T, ? extends V3> getter3,
                                            @NotNull Function<? super T, ? extends V4> getter4) {
        return (value, matcher1, matcher2, matcher3, matcher4) ->
                withType(type, value, (T v) -> Match.from(ctor, matcher1.apply(getter1.apply(v)), matcher2.apply(getter2.apply(v)), matcher3.apply(getter3.apply(v)), matcher4.apply(getter4.apply(v))));
    }

    @NotNull
    private static <S, T extends S> Match<S> withType(@NotNull Class<T> type, @NotNull Object value, @NotNull Function<? super T, Match<S>> callback) {
        if (type.isInstance(value))
            return callback.apply(type.cast(value));
        else
            return Match.failure();
    }
}
