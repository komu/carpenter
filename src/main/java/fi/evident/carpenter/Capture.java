package fi.evident.carpenter;

import fi.evident.carpenter.utils.NameSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * A matcher that captures its value so that it can be changed before rebuilding.
 * Can also be used to implement equality constraints between different matches.
 */
public final class Capture<T> extends Matcher<T> {

    /** Name for debugging purposes */
    @NotNull
    private final String name;

    @NotNull
    private static final NameSequence defaultNameSequence = new NameSequence("capture");

    public Capture() {
        this(defaultNameSequence.next());
    }

    public Capture(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    @Override
    public Match<T> match(@NotNull T value) {
        return match(value, rewrites -> value, Constraints.empty());
    }

    @Override
    public String toString() {
        return name;
    }

    @Nullable
    Match<T> match(@NotNull T value, @NotNull Function<MatchRewrites, T> defaultValue, @NotNull Constraints constraints) {
        return Match.fromCapture(this, value, defaultValue, constraints);
    }
}
