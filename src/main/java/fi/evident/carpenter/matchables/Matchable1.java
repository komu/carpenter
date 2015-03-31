package fi.evident.carpenter.matchables;

import fi.evident.carpenter.Match;
import fi.evident.carpenter.Matcher;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Matchable1<T,V1> {

    @NotNull
    Match<T> match(@NotNull T value, @NotNull Matcher<V1> matcher);
}
