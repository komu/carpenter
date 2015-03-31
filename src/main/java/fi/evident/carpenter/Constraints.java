package fi.evident.carpenter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;

/**
 * Constraints are key/value mappings that can be merged if the values agree on both sides.
 */
public abstract class Constraints {

    @NotNull
    private static final Constraints EMPTY_CONSTRAINTS = new DefaultConstraints(emptyMap());

    @NotNull
    private static final Constraints INVALID_CONSTRAINTS = new InvalidConstraints();

    private Constraints() { }

    /**
     * Returns empty, valid constraints.
     */
    @NotNull
    public static Constraints empty() {
        return EMPTY_CONSTRAINTS;
    }

    /**
     * Returns invalid constraints. Merging invalid constraints with any other constraints
     * will always produce invalid constraints.
     */
    @NotNull
    public static Constraints invalid() {
        return INVALID_CONSTRAINTS;
    }

    /**
     * Creates constraints where given key is bound to given value. The returned
     * constraints are always valid since it can't be inconsistent with itself.
     */
    @NotNull
    public static <T> Constraints forValue(@NotNull Capture<T> key, @Nullable T value) {
        return new DefaultConstraints(key, value);
    }

    /**
     * Return true iff the constraints are empty (and therefore also valid).
     */
    public abstract boolean isEmpty();

    /**
     * Returns value for given key.
     *
     * @throws IllegalArgumentException if there is no value for given key
     */
    public abstract <T> T getValue(@NotNull Capture<T> key);

    /**
     * Merge these constraints with given constraints.
     *
     * Merging constraints succeeds if both sides are valid and all variables defined on either
     * side are either missing on the other side or have same value on the other side.
     *
     * If merging succeeds, new constraints with union of the values is returned. Otherwise
     * invalid constraints are returned.
     */
    @NotNull
    public final Constraints merge(@NotNull Constraints rhs) {
        if (isInvalid() || rhs.isInvalid()) return invalid();
        if (isEmpty()) return rhs;
        if (rhs.isEmpty()) return this;

        // Since we already tested for invalid, we must have DefaultConstraints instances here
        return merge((DefaultConstraints) this, (DefaultConstraints) rhs);
    }

    @NotNull
    private static Constraints merge(@NotNull DefaultConstraints c1, @NotNull DefaultConstraints c2) {
        Map<Capture<?>,Object> result = new HashMap<>();

        result.putAll(c1.values);

        for (Map.Entry<Capture<?>, Object> entry : c2.values.entrySet()) {
            Capture<?> key = entry.getKey();
            Object rhs = entry.getValue();

            Object lhs = result.putIfAbsent(key, rhs);

            if ((lhs != null || c1.values.containsKey(entry.getKey())) && !Objects.equals(lhs, rhs))
                return invalid();
        }

        return new DefaultConstraints(result);
    }

    /**
     * Returns true iff the constraints are valid.
     */
    public abstract boolean isValid();

    /**
     * Returns true iff the constraints are not valid.
     */
    public final boolean isInvalid() {
        return !isValid();
    }

    /**
     * Merges all given constraints.
     */
    @NotNull
    public static Constraints mergeAll(@NotNull Collection<? extends Constraints> cs) {
        return cs.stream().collect(mergeAll());
    }

    /**
     * Returns a {@link Collector} that merges all constraints.
     */
    @NotNull
    public static Collector<Constraints, ?, Constraints> mergeAll() {
        return Collectors.reducing(empty(), Constraints::merge);
    }

    private static final class DefaultConstraints extends Constraints {

        @NotNull
        private final Map<Capture<?>,Object> values;

        public DefaultConstraints(@NotNull Map<Capture<?>, Object> values) {
            this.values = values;
        }

        public DefaultConstraints(Capture<?> key, Object value) {
            this.values = singletonMap(key, value);
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public boolean isEmpty() {
            return values.isEmpty();
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getValue(@NotNull Capture<T> key) {
            Object value = values.get(key);

            if (value == null && !values.containsKey(key))
                throw new IllegalArgumentException("no value for key " + key);

            return (T) value;
        }

        @Override
        public String toString() {
            return "Constraints[values=" + values+ ']';
        }
    }

    private static final class InvalidConstraints extends Constraints {

        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public <T> T getValue(@NotNull Capture<T> key) {
            throw new IllegalStateException("can't get value for " + key + " from invalid constraints");
        }

        @Override
        public String toString() {
            return "InvalidConstraints";
        }
    }
}
