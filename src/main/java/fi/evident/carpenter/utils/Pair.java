package fi.evident.carpenter.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class Pair<F,S> {

    public final F first;
    public final S second;

    private Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    @NotNull
    public static <F,S> Pair<F,S> of(@NotNull F first, @NotNull S second) {
        return new Pair<>(first, second);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair<?,?> pair = (Pair<?,?>) o;

        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "Pair[" + first + ',' + second + ']';
    }
}
