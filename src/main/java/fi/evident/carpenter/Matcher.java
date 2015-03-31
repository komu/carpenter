package fi.evident.carpenter;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;

public abstract class Matcher<T> implements Function<T,Match<T>> {

    @NotNull
    public abstract Match<T> match(@NotNull T value);

    @Override
    public Match<T> apply(T t) {
        return match(t);
    }

    @NotNull
    public final Matcher<T> save(@NotNull Capture<T> capture) {
        return new Matcher<T>() {
            @NotNull
            @Override
            public Match<T> match(@NotNull T value) {
                Match<T> match = Matcher.this.match(value);
                if (match.isSuccess())
                    return capture.match(value, match::rebuild, match.constraints);
                else
                    return Match.failure();
            }
        };
    }

    @NotNull
    public final T rewriteAll(@NotNull T value, @NotNull Function<Match<T>,MatchRewrites> rewriteGenerator) {
        T lastValue = value;
        while (true) {
            T rewritten = rewrite(lastValue, rewriteGenerator).orElse(null);
            if (rewritten != null)
                lastValue = rewritten;
            else
                return lastValue;
        }
    }

    @NotNull
    public final Optional<T> rewrite(@NotNull T value, @NotNull Function<Match<T>,MatchRewrites> rewriteGenerator) {
        Match<T> match = match(value);
        if (match.isSuccess())
            return Optional.of(match.rebuild(rewriteGenerator.apply(match)));
        else
            return Optional.empty();
    }

    @NotNull
    public final Function<T,T> rewriter(@NotNull Function<Match<T>,MatchRewrites> rewriteGenerator) {
        return value -> rewrite(value, rewriteGenerator).orElse(value);
    }

    @NotNull
    public final Matcher<T> or(@NotNull Matcher<T> alternative) {
        return new Matcher<T>() {
            @NotNull
            @Override
            public Match<T> match(@NotNull T value) {
                Match<T> match = Matcher.this.match(value);
                return match.isSuccess() ? match : alternative.match(value);
            }
        };
    }
}
