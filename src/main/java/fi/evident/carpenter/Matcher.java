package fi.evident.carpenter;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class Matcher<T> implements Function<T,Match<T>> {

    @Override
    @NotNull
    public abstract Match<T> apply(@NotNull T t);

    @NotNull
    public static <T> Matcher<T> of(@NotNull Function<T,Match<T>> function) {
        return new Matcher<T>() {
            @NotNull
            @Override
            public Match<T> apply(@NotNull T t) {
                return function.apply(t);
            }
        };
    }

    /**
     * Rewrites matches until matcher does not provide new matches. If no matches
     * are produced at all, returns the original value.
     *
     * @see #rewrite(Object, BiConsumer)
     */
    @NotNull
    public final T rewriteAll(@NotNull T value, @NotNull BiConsumer<Match<T>,MatchRewrites> rewriteGenerator) {
        T lastValue = value;
        while (true) {
            T rewritten = rewrite(lastValue, rewriteGenerator).orElse(null);
            if (rewritten != null)
                lastValue = rewritten;
            else
                return lastValue;
        }
    }

    /**
     * Tries to match this matcher against given value and rewrites it if match is successful.
     * If match fails, returns {@link Optional#empty()}.
     */
    @NotNull
    public final Optional<T> rewrite(@NotNull T value, @NotNull BiConsumer<Match<T>,MatchRewrites> rewriteGenerator) {
        return apply(value).rewrite(rewriteGenerator);
    }

    /**
     * Returns a matcher that calls {@code alternative} if the match fails.
     */
    @NotNull
    public final Matcher<T> or(@NotNull Matcher<T> alternative) {
        // TODO: merging constraints could fail later, perhaps we'll need to return both matches?
        return new Matcher<T>() {
            @NotNull
            @Override
            public Match<T> apply(@NotNull T value) {
                Match<T> match = Matcher.this.apply(value);
                return match.isSuccess() ? match : alternative.apply(value);
            }
        };
    }
}
