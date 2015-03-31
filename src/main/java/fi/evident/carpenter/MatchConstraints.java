package fi.evident.carpenter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class MatchConstraints {

    @NotNull
    private static final MatchConstraints EMPTY = new MatchConstraints();

    @NotNull
    private static final MatchConstraints INVALID = new MatchConstraints();

    @NotNull
    private final IdentityHashMap<Capture<?>, Object> map = new IdentityHashMap<>();

    @NotNull
    public static MatchConstraints empty() {
        return EMPTY;
    }

    @NotNull
    public static MatchConstraints invalid() {
        return INVALID;
    }

    @NotNull
    public static <V> MatchConstraints mergeConstraints(@NotNull List<Match<V>> matches) {
        return matches.stream().map(a -> a.constraints).reduce(MatchConstraints::merge).orElse(empty());
    }

    @SuppressWarnings("ObjectEquality")
    @NotNull
    public MatchConstraints merge(@NotNull MatchConstraints constraints) {
        if (this == EMPTY) return constraints;
        if (constraints == EMPTY) return this;
        if (this == INVALID || constraints == INVALID) return INVALID;

        MatchConstraints result = new MatchConstraints();
        result.map.putAll(map);

        for (Map.Entry<Capture<?>, Object> entry : constraints.map.entrySet()) {
            Object existing = result.map.putIfAbsent(entry.getKey(), entry.getValue());
            if ((existing != null || map.containsKey(entry.getKey())) && !Objects.equals(existing, entry.getValue()))
                return INVALID;
        }

        return result;
    }

    @NotNull
    public static <T> MatchConstraints fromCapture(@NotNull Capture<T> capture, @Nullable T value) {
        MatchConstraints constraints = new MatchConstraints();
        constraints.map.put(capture, value);
        return constraints;
    }

    @SuppressWarnings("ObjectEquality")
    public boolean isValid() {
        return this != INVALID;
    }

    public <V> V getValue(@NotNull Capture<V> capture) {
        if (!isValid()) throw new IllegalStateException("can't get value from invalid constraints");

        @SuppressWarnings("unchecked")
        V value = (V) map.get(capture);
        if (value != null)
            return value;
        else
            throw new RuntimeException("no value for capture " + capture);
    }

    @Override
    public String toString() {
        if (!isValid()) return "invalid";

        return "[Constraints=" + map + ']';
    }
}
