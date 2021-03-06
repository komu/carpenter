package fi.evident.carpenter.matchables;

import fi.evident.carpenter.Match;
import fi.evident.carpenter.Matcher;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Matchable3<T,V1,V2,V3> {

    @NotNull
    Match<T> match(@NotNull T value, @NotNull Matcher<V1> matcher1, @NotNull Matcher<V2> matcher2, @NotNull Matcher<V3> matcher3);
}
