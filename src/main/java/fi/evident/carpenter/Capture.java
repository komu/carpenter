package fi.evident.carpenter;

import fi.evident.carpenter.utils.NameSequence;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Capture stores the matched value for later access and possible replacement.
 * Equality constraints between matches can be implemented by using same capture.
 */
public final class Capture<T> extends Matcher<T> {

    @NotNull
    private final String debugName;

    @NotNull
    private static final NameSequence defaultNameSequence = new NameSequence("capture");

    /**
     * Construcs new Capture.
     */
    public Capture() {
        this(defaultNameSequence.next());
    }

    /**
     * Constructs new Capture with given name for debugging.
     */
    public Capture(@NotNull String debugName) {
        this.debugName = debugName;
    }

    /**
     * Matches this capture against given value.
     */
    @NotNull
    @Override
    public Match<T> apply(@NotNull T value) {
        return fromCapture(this, rewrites -> value, constraints(value));
    }

    @NotNull
    public Matcher<T> save(@NotNull Matcher<T> matcher) {
        return Matcher.of(value -> matcher.apply(value).flatMap(m -> fromCapture(this, m::rebuild, m.getConstraints().merge(constraints(value)))));
    }

    @NotNull
    private Constraints constraints(@NotNull T value) {
        return Constraints.forValue(this, value);
    }

    @NotNull
    private static <T> Match<T> fromCapture(@NotNull Capture<T> capture, @NotNull Function<MatchRewrites, T> defaultValue, @NotNull Constraints constraints) {
        return Match.from(rewrites -> rewrites.getReplacedValue(capture).orElseGet(() -> defaultValue.apply(rewrites)), constraints);
    }

    @Override
    public String toString() {
        return debugName;
    }
}
