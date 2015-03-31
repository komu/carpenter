package fi.evident.carpenter;

import fi.evident.carpenter.functions.Function3;
import fi.evident.carpenter.functions.Function4;
import fi.evident.carpenter.utils.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents a match (or match failure) of given type. Successful matches
 * can be used to ask values of {@link Capture}s using {@link #getValue(Capture)}
 * or the matched object can be rewritten using {@link #rebuild(MatchRewrites)}.
 */
public abstract class Match<T> {

    @NotNull
    private static final Match<Object> FAILURE = new FailureMatch<>();

    /**
     * Rebuilds the object tree rooted at the matched object. Given
     * {@link MatchRewrites} can be used to selectively override captured
     * objects in rewritten graph.
     */
    @NotNull
    public abstract T rebuild(@NotNull MatchRewrites rewrites);

    /**
     * Returns value of given {@link Capture} in the object graph.
     */
    @NotNull
    public abstract <V> V getValue(@NotNull Capture<V> capture);

    /**
     * Returns the merged constraints of all given matches.
     */
    @NotNull
    public static Constraints mergedConstraints(@NotNull Collection<? extends Match<?>> matches) {
        return matches.stream().map(Match::getConstraints).collect(Constraints.mergeAll());
    }

    @NotNull
    protected abstract Constraints getConstraints();

    /**
     * Returns a match representing failure.
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> Match<T> failure() {
        return (Match<T>) FAILURE;
    }

    /**
     * Is this match successfull?
     */
    public final boolean isSuccess() {
        return !isFailure();
    }

    /**
     * Is this match a failure?
     */
    public abstract boolean isFailure();

    /**
     * Creates a new Match using given constraints and given function for rebuilding the match.
     */
    @NotNull
    public static <T> Match<T> from(@NotNull Function<MatchRewrites, T> rebuilder, @NotNull Constraints constraints) {
        return constraints.isValid() ? new SuccessMatch<>(rebuilder, constraints) : failure();
    }

    /**
     * If this match is successful, calls {@code mapper} with it and return result. Otherwise returns invalid match.
     */
    @NotNull
    public abstract Match<T> flatMap(@NotNull Function<Match<T>, Match<T>> mapper);

    /**
     * Returns a successful match that produces given value.
     */
    @NotNull
    public static <T> Match<T> constant(T value) {
        return from(rewrites -> value, Constraints.empty());
    }

    /**
     * If the match is successfull, rewrites it and returns the value.
     */
    @NotNull
    public abstract Optional<T> rewrite(@NotNull BiConsumer<Match<T>, MatchRewrites> rewriteGenerator);

    @NotNull
    public static <T, V> Match<T> from(@NotNull Function<V, T> builder, @NotNull Match<V> match) {
        if (match.isFailure()) return failure();

        return from(rewrites -> builder.apply(match.rebuild(rewrites)), match.getConstraints());
    }

    @NotNull
    public static <T, V1, V2> Match<T> from(@NotNull BiFunction<V1, V2, T> builder, @NotNull Match<V1> m1, @NotNull Match<V2> m2) {
        if (m1.isFailure() || m2.isFailure()) return failure();

        Constraints constraints = m1.getConstraints().merge(m2.getConstraints());
        return from(rewrites -> builder.apply(m1.rebuild(rewrites), m2.rebuild(rewrites)), constraints);
    }

    @NotNull
    public static <T, V1, V2, V3> Match<T> from(@NotNull Function3<V1, V2, V3, T> builder, @NotNull Match<V1> m1, @NotNull Match<V2> m2, @NotNull Match<V3> m3) {
        if (m1.isFailure() || m2.isFailure() || m3.isFailure()) return failure();

        Constraints constraints = m1.getConstraints().merge(m2.getConstraints()).merge(m3.getConstraints());
        return from(rewrites -> builder.apply(m1.rebuild(rewrites), m2.rebuild(rewrites), m3.rebuild(rewrites)), constraints);
    }

    @NotNull
    public static <T, V1, V2, V3, V4> Match<T> from(@NotNull Function4<V1, V2, V3, V4, T> builder, @NotNull Match<V1> m1, @NotNull Match<V2> m2, @NotNull Match<V3> m3, @NotNull Match<V4> m4) {
        if (m1.isFailure() || m2.isFailure() || m3.isFailure() || m4.isFailure()) return failure();

        Constraints constraints = m1.getConstraints().merge(m2.getConstraints()).merge(m3.getConstraints()).merge(m4.getConstraints());
        return from(rewrites -> builder.apply(m1.rebuild(rewrites), m2.rebuild(rewrites), m3.rebuild(rewrites), m4.rebuild(rewrites)), constraints);
    }

    @NotNull
    public static <T, V> Match<T> fromList(@NotNull Function<List<V>, T> builder, @NotNull List<Match<V>> matches) {
        Constraints constraints = mergedConstraints(matches);
        return from(rewrites -> builder.apply(CollectionUtils.map(matches, m -> m.rebuild(rewrites))), constraints);
    }

    @NotNull
    public static <T, V, V2> Match<T> fromList(@NotNull BiFunction<List<V>, V2, T> builder, @NotNull List<Match<V>> matches, @NotNull Match<V2> m2) {
        if (m2.isFailure()) return failure();

        Constraints constraints = mergedConstraints(matches).merge(m2.getConstraints());
        return from(rewrites -> builder.apply(CollectionUtils.map(matches, m -> m.rebuild(rewrites)), m2.rebuild(rewrites)), constraints);
    }

    @NotNull
    public static <T> Match<List<T>> fromList(@NotNull List<Match<T>> matches) {
        return from(rewrites -> CollectionUtils.map(matches, m -> m.rebuild(rewrites)), mergedConstraints(matches));
    }

    /**
     * Represents a successful match.
     */
    private static final class SuccessMatch<T> extends Match<T> {

        @NotNull
        private final Function<MatchRewrites, T> rebuilder;

        @NotNull
        private final Constraints constraints;

        private SuccessMatch(@NotNull Function<MatchRewrites, T> rebuilder, @NotNull Constraints constraints) {
            assert constraints.isValid();

            this.rebuilder = rebuilder;
            this.constraints = constraints;
        }

        @NotNull
        @Override
        public T rebuild(@NotNull MatchRewrites rewrites) {
            return rebuilder.apply(rewrites);
        }

        @Override
        @NotNull
        public <V> V getValue(@NotNull Capture<V> capture) {
            return constraints.getValue(capture);
        }

        @NotNull
        @Override
        protected Constraints getConstraints() {
            return constraints;
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @NotNull
        @Override
        public Match<T> flatMap(@NotNull Function<Match<T>, Match<T>> mapper) {
            return mapper.apply(this);
        }

        @NotNull
        @Override
        public Optional<T> rewrite(@NotNull BiConsumer<Match<T>, MatchRewrites> rewriteGenerator) {
            MatchRewrites rewrites = new MatchRewrites();
            rewriteGenerator.accept(this, rewrites);
            return Optional.of(rebuild(rewrites));
        }
    }

    /**
     * Represents a failed match.
     */
    private static final class FailureMatch<T> extends Match<T> {

        @NotNull
        @Override
        public T rebuild(@NotNull MatchRewrites rewrites) {
            throw new UnsupportedOperationException("can't rebuild failed match");
        }

        @Override
        @NotNull
        public <V> V getValue(@NotNull Capture<V> capture) {
            throw new UnsupportedOperationException("can't get values from failed match");
        }

        @NotNull
        @Override
        protected Constraints getConstraints() {
            return Constraints.invalid();
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @NotNull
        @Override
        public Match<T> flatMap(@NotNull Function<Match<T>, Match<T>> mapper) {
            return this;
        }

        @NotNull
        @Override
        public Optional<T> rewrite(@NotNull BiConsumer<Match<T>, MatchRewrites> rewriteGenerator) {
            return Optional.empty();
        }
    }
}
