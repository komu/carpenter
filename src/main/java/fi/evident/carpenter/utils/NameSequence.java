package fi.evident.carpenter.utils;

import org.jetbrains.annotations.NotNull;

public final class NameSequence {

    @NotNull
    private final String prefix;

    private int counter = 1;

    public NameSequence(@NotNull String prefix) {
        this.prefix = prefix;
    }

    @NotNull
    public String next() {
        return prefix + counter++;
    }
}
