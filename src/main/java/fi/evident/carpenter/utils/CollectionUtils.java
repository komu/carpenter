package fi.evident.carpenter.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * Various utility methods for simplifying collection operations.
 */
public final class CollectionUtils {

    private CollectionUtils() {
    }

    /**
     * Returns a list which concatenates {@code xs} and {@code ys} in their iteration order.
     */
    @NotNull
    public static <T> List<T> concat(@NotNull Collection<T> xs, @NotNull Collection<T> ys) {
        List<T> concatenation = new ArrayList<>(xs.size() + ys.size());

        concatenation.addAll(xs);
        concatenation.addAll(ys);

        return concatenation;
    }

    /**
     * Applies {@code mapper} to elements of {@code xs} and returns the results as a list.
     */
    @NotNull
    public static <A,B> List<B> map(@NotNull Collection<? extends A> xs, @NotNull Function<? super A, ? extends B> mapper) {
        return xs.stream().map(mapper).collect(toList());
    }

    /**
     * Returns copy of {@code xs} which is identical to the original list, except that value
     * at {@code index} has been replaced with {@code x}.
     */
    @NotNull
    public static <T> List<T> copyWithReplacedValue(@NotNull List<T> xs, int index, T x) {
        List<T> copy = new ArrayList<>(xs);
        copy.set(index, x);
        return copy;
    }

    /**
     * Returns copy of {@code xs} which is identical to the original list, except that values
     * {@code index..(index+ys.size()) have been replaced by values of {@code ys}.
     */
    @NotNull
    public static <T> List<T> copyWithReplacedSubList(@NotNull List<T> xs, int index, @NotNull List<T> ys) {
        List<T> copy = new ArrayList<>(xs);

        int i = index;
        for (T v : ys)
            copy.set(i++, v);

        return copy;
    }
}
