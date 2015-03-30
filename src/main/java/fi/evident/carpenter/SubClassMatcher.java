package fi.evident.carpenter;

import org.jetbrains.annotations.NotNull;

/**
 * Base class for matchers that work with a specific sub class of S.
 */
public abstract class SubClassMatcher<T,S extends T> extends Matcher<T> {

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
}
