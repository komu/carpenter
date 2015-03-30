package fi.evident.carpenter;

import fi.evident.carpenter.utils.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static fi.evident.carpenter.MatchConstraints.mergeConstraints;

public final class Match<E> {

    @NotNull
    private static final Match<Object> FAILURE = new Match<>(r -> { throw new UnsupportedOperationException(); }, MatchConstraints.invalid());

    @NotNull
    private final Function<MatchRewrites, E> rebuilder;

    @NotNull
    final MatchConstraints constraints;

    private Match(@NotNull Function<MatchRewrites, E> rebuilder, @NotNull MatchConstraints constraints) {
        this.rebuilder = rebuilder;
        this.constraints = constraints;
    }

    @NotNull
    public <V> V getValue(@NotNull Capture<V> capture) {
        return constraints.getValue(capture);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public static <T> Match<T> failure() {
        return (Match<T>) FAILURE;
    }

    public boolean isSuccess() {
        return !isFailure();
    }

    @SuppressWarnings("ObjectEquality")
    public boolean isFailure() {
        return this == FAILURE;
    }

    @NotNull
    private static <T> Match<T> from(@NotNull Function<MatchRewrites, T> rebuilder, @NotNull MatchConstraints constraints) {
        if (!constraints.isValid()) return failure();
        return new Match<>(rebuilder, constraints);
    }

    @NotNull
    public static <T> Match<T> fromCapture(@NotNull Capture<T> capture, @NotNull T value, @NotNull Function<MatchRewrites, T> defaultValue, @NotNull MatchConstraints constraints) {
        MatchConstraints newConstraints = constraints.merge(MatchConstraints.fromCapture(capture, value));
        return from(rewrites -> rewrites.getReplacedValue(capture).orElseGet(() -> defaultValue.apply(rewrites)), newConstraints);
    }

    @NotNull
    public static <T, V> Match<T> from(@NotNull Function<V, T> builder, @NotNull Match<V> match) {
        if (match.isFailure()) return failure();

        return from(rewrites -> builder.apply(match.rebuild(rewrites)), match.constraints);
    }

    @NotNull
    public static <T, V1, V2> Match<T> from(@NotNull BiFunction<V1, V2, T> builder, @NotNull Match<V1> m1, @NotNull Match<V2> m2) {
        if (m1.isFailure() || m2.isFailure()) return failure();

        return from(rewrites -> builder.apply(m1.rebuild(rewrites), m2.rebuild(rewrites)), m1.constraints.merge(m2.constraints));
    }

    @NotNull
    public static <T, V1, V2, V3> Match<T> from(@NotNull TernaryFunction<V1, V2, V3, T> builder, @NotNull Match<V1> m1, @NotNull Match<V2> m2, @NotNull Match<V3> m3) {
        if (m1.isFailure() || m2.isFailure() || m3.isFailure()) return failure();

        return from(rewrites -> builder.apply(m1.rebuild(rewrites), m2.rebuild(rewrites), m3.rebuild(rewrites)), m1.constraints.merge(m2.constraints).merge(m3.constraints));
    }

    @NotNull
    public static <T, V1, V2, V3, V4> Match<T> from(@NotNull QuadFunction<V1, V2, V3, V4, T> builder, @NotNull Match<V1> m1, @NotNull Match<V2> m2, @NotNull Match<V3> m3, @NotNull Match<V4> m4) {
        if (m1.isFailure() || m2.isFailure() || m3.isFailure() || m4.isFailure()) return failure();

        return from(rewrites -> builder.apply(m1.rebuild(rewrites), m2.rebuild(rewrites), m3.rebuild(rewrites), m4.rebuild(rewrites)), m1.constraints.merge(m2.constraints).merge(m3.constraints).merge(m4.constraints));
    }

    @NotNull
    public static <T, V> Match<T> fromList(@NotNull Function<List<V>, T> builder, @NotNull List<Match<V>> matches) {
        MatchConstraints constraints = mergeConstraints(matches);
        return Match.from(rewrites -> builder.apply(CollectionUtils.map(matches, m -> m.rebuild(rewrites))), constraints);
    }

    @NotNull
    public static <T, V, V2> Match<T> fromList(@NotNull BiFunction<List<V>, V2, T> builder, @NotNull List<Match<V>> matches, @NotNull Match<V2> m2) {
        if (m2.isFailure()) return failure();
        MatchConstraints constraints = mergeConstraints(matches).merge(m2.constraints);
        return Match.from(rewrites -> builder.apply(CollectionUtils.map(matches, m -> m.rebuild(rewrites)), m2.rebuild(rewrites)), constraints);
    }

    @NotNull
    public static <T> Match<List<T>> fromList(@NotNull List<Match<T>> matches) {
        return Match.from(rewrites -> CollectionUtils.map(matches, (Match<T> m) -> m.rebuild(rewrites)), mergeConstraints(matches));
    }

    @NotNull
    public E rebuild(@NotNull MatchRewrites rewriter) {
        return rebuilder.apply(rewriter);
    }

    @NotNull
    public <T> Match<T> map(@NotNull Function<? super E, ? extends T> mapper) {
        if (isFailure()) return failure();
        return Match.from(rewrites -> mapper.apply(rebuild(rewrites)), constraints);
    }

    @NotNull
    public static <T> Match<T> constant(T value) {
        return from(rewrites -> value, MatchConstraints.empty());
    }

    public void ifSuccess(@NotNull Consumer<Match<E>> consumer) {
        if (isSuccess())
            consumer.accept(this);
    }

    @FunctionalInterface
    public interface TernaryFunction<V1, V2, V3, T> {
        T apply(V1 v1, V2 v2, V3 v3);
    }

    @FunctionalInterface
    public interface QuadFunction<V1, V2, V3, V4, T> {
        T apply(V1 v1, V2 v2, V3 v3, V4 v4);
    }
}
