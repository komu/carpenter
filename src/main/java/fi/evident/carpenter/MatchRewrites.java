package fi.evident.carpenter;

import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;
import java.util.Optional;

public final class MatchRewrites {

    @NotNull
    private final IdentityHashMap<Capture<?>, Object> replaced = new IdentityHashMap<>();

    public <V> void replaceValue(@NotNull Capture<V> capture, @NotNull V value) {
        replaced.put(capture, value);
    }

    @NotNull
    public <V> Optional<V> getReplacedValue(@NotNull Capture<V> capture) {
        @SuppressWarnings("unchecked")
        V value = (V) replaced.get(capture);
        return Optional.ofNullable(value);
    }
}
